package cz.tul.utils;

import com.opencsv.CSVReader;
import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Gate;
import cz.tul.mysql.model.PassageMysql;
import cz.tul.mysql.model.Driver;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CsvParser implements Parser<Passage> {
    boolean complete = false;
    CSVReader csvReader;

    @Override
    public boolean isComplete() {
        return complete;
    }

    public List<Passage> parse(FileReader reader, long numberOfLines) {
        List<Passage> objects = new ArrayList<>();
        try {
            if (csvReader == null)
                csvReader = new CSVReader(reader);
            String[] values;

            for (int i = 0; i < numberOfLines; i++) {
                values = csvReader.readNext();
                if (values == null) {
                    complete = true;
                    reader.close();
                    break;
                }
                Passage mysqlProjeti = parseObjectFromLine(values);
                objects.add(mysqlProjeti);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return objects;
    }

    private Passage parseObjectFromLine(String[] values) {
        Car auto = new Car(values[0], Integer.valueOf(values[1]), values[2], values[3]);
        Driver ridic = new Driver(values[4], values[5]);
        Gate brana = new Gate(values[11], Float.valueOf(values[12]), Float.valueOf(values[13]), Float.valueOf(values[15]), values[14]);
        return new PassageMysql(convertUnixTimeToTimestamp(values[6]), Integer.valueOf(values[7]), Integer.valueOf(values[8]), Float.valueOf(values[9]), ridic, brana, auto);
    }

    private Timestamp convertUnixTimeToTimestamp(String value) {
        return new Timestamp(Long.valueOf(value) * 1000L);
    }
}
