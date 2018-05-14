package cz.tul.mongo;

import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Gate;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.mongo.model.InvalidMongoRecords;
import cz.tul.mongo.model.PassageMongo;
import cz.tul.mysql.model.Driver;
import cz.tul.model.ui.DriverEntity;
import cz.tul.mongo.repository.InvalidRecordsMongoRepository;
import cz.tul.mongo.repository.PassageMongoRepository;
import cz.tul.service.DatabaseService;
import org.bson.Document;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Profile("mongo")
@Service
public class MongoDatabaseService extends DatabaseService {
    @Autowired
    PassageMongoRepository projetiMongoRepository;

    @Autowired
    InvalidRecordsMongoRepository invalidRecordsMongoRepository;

    @Autowired
    private MongoTemplate mongo;

    @Override
    public void deleteAll() {
        invalidRecordsMongoRepository.save(new InvalidMongoRecords(1, 0));
        projetiMongoRepository.deleteAll();
    }

    @Override
    public boolean saveWholeRecord(Passage projeti) {
        if (!validateBrana(projeti.getBrana())) {
            return false;
        }
        if (!validateAuto(projeti.getAuto())) {
            return false;
        }
        if (!validateRidic(projeti.getRidic())) {
            return false;
        }



        projetiMongoRepository.save(new PassageMongo(projeti));
        return true;
    }

    private boolean validateRidic(Driver ridic) {
        Passage found = projetiMongoRepository.findFirstByRidic_Crp(ridic.getCrp());
        if (found == null)
            return true;
        Driver foundRidic = found.getRidic();
        return foundRidic.getJmeno().equals(ridic.getJmeno());
    }

    private boolean validateAuto(Car auto) {
        Passage found = projetiMongoRepository.findFirstByAuto_Spz(auto.getSpz());
        if (found == null)
            return true;
        Car foundAuto = found.getAuto();
        return foundAuto.getTyp().equals(auto.getTyp()) && foundAuto.getVyrobce().equals(auto.getVyrobce()) && foundAuto.getBarva() == auto.getBarva();
    }

    private boolean validateBrana(Gate brana) {
        Passage found = projetiMongoRepository.findFirstByBrana_Id(brana.getId());
        if (found == null)
            return true;
        Gate foundBrana = found.getBrana();
        return foundBrana.getCena() == brana.getCena()  && foundBrana.getTyp().equals(brana.getTyp());
    }

    @Override
    public void addInvalidRecords(int count) {
        Optional<InvalidMongoRecords> records = invalidRecordsMongoRepository.findById(1);

        if (!records.isPresent()) {
            invalidRecordsMongoRepository.save(new InvalidMongoRecords(1, count));

        } else {
            InvalidMongoRecords invalidRecords = records.get();
            invalidRecords.setPocet(invalidRecords.getPocet() + count);
            try {
                invalidRecordsMongoRepository.save(invalidRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public InvalidRecords getInvalidRecordsReport() {
        return invalidRecordsMongoRepository.findAll().get(0);
    }

    @Override
    public List<GatePassageProjection> getByGate(String key) {
        return projetiMongoRepository.findByBrana_Id(key, PageRequest.of(0, 1000));
    }

    @Override
    public List<DriverEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km) {
        String fromString = convertTime(from);
        if (fromString == null) return null;
        String toString = convertTime(to);
        if (toString == null) return null;

        String command = SECOND_SELECT.replace("${fromDate}", fromString);
        command = command.replace("${toDate}", toString);
        command = command.replace("${najeto}", String.valueOf(km));
        Document commandResult = mongo.executeCommand(command);
        return parseResult(commandResult);
    }

    private List<DriverEntity> parseResult(Document commandResult) {
        String s = commandResult.toJson();
        List<DriverEntity> drivers = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONObject(s).getJSONObject("cursor").getJSONArray("firstBatch");
            JSONObject driver;
            int i = 0;
            while (!jsonArray.isNull(i)) {
                driver = jsonArray.getJSONObject(i);
                drivers.add(new DriverEntity(driver.getInt("najeto_km"), driver.getString("_id"), driver.getString("jmeno")));
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    private String convertTime(Timestamp time) {
        SimpleDateFormat first = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date parsedFrom = null;

        try {
            parsedFrom = first.parse(time.toString());

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        SimpleDateFormat second = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z", Locale.ENGLISH);
        return second.format(parsedFrom);
    }


    /// 2018-04-24T01:00:00 ---
    ///
    ///
    private static final String SECOND_SELECT = "{\n" +
            "  \"aggregate\": \"Projeti\",\n" +
            "  \"pipeline\": [\n" +
            "    {\n" +
            "      $match: {\n" +
            "        '_id.cas': {\n" +
            "          $gte: new Date(\"${fromDate}\"),\n" +
            "          $lte: new Date(\"${toDate}\")\n" +
            "        },\n" +
            "        'brana.typ': {\n" +
            "          $ne: \"Satellite\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      $lookup: {\n" +
            "        from: \"Projeti\",\n" +
            "        let: {\n" +
            "          ridic_crp: \"$_id.ridic\",\n" +
            "          ridic_jmeno: \"$ridic.jmeno\"\n" +
            "        },\n" +
            "        pipeline: [\n" +
            "          {\n" +
            "            $match: {\n" +
            "              $expr: {\n" +
            "                $and: [\n" +
            "                  {\n" +
            "                    $eq: [\n" +
            "                      \"$_id.ridic\",\n" +
            "                      \"$$ridic_crp\"\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  {\n" +
            "                    $eq: [\n" +
            "                      \"$ridic.jmeno\",\n" +
            "                      \"$$ridic_jmeno\"\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  {\n" +
            "                    $gte: [\n" +
            "                      \"$_id.cas\",\n" +
            "                      new Date(\"${fromDate}\")\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  {\n" +
            "                    $lte: [\n" +
            "                      \"$_id.cas\",\n" +
            "                      new Date(\"${toDate}\")\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  {\n" +
            "                    $eq: [\n" +
            "                      \"$brana.typ\",\n" +
            "                      \"Satellite\"\n" +
            "                    ]\n" +
            "                  }\n" +
            "                ]\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            $project: {\n" +
            "              _id: 1\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        as: \"sattelite_passage\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      $match: {\n" +
            "        \"sattelite_passage\": {\n" +
            "          $eq: [\n" +
            "            \n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      $group: {\n" +
            "        _id: \"$_id.ridic\",\n" +
            "        jmeno: {\n" +
            "          $first: \"$ridic.jmeno\"\n" +
            "        },\n" +
            "        start: {\n" +
            "          $min: \"$najeto\"\n" +
            "        },\n" +
            "        end: {\n" +
            "          $max: \"$najeto\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      $addFields: {\n" +
            "        najeto_km: {\n" +
            "          $subtract: [\n" +
            "            \"$end\",\n" +
            "            \"$start\"\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      $match: {\n" +
            "        \"najeto_km\": {\n" +
            "          $gte: ${najeto}\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      $project: {\n" +
            "        _id: 1,\n" +
            "        \"jmeno\": 1,\n" +
            "        \"najeto_km\": 1\n" +
            "      }\n" +
            "    }\n" +
            "  ],  " +
            "   cursor:{" +
            "       batchSize: 1000" +
            "           } \n" +
            "}";
}