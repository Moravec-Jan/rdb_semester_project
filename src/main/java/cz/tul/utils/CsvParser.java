package cz.tul.utils;

import com.opencsv.CSVReader;
import cz.tul.model.db.Auto;
import cz.tul.model.db.Brana;
import cz.tul.model.db.Projeti;
import cz.tul.model.db.Ridic;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CsvParser implements Parser<Projeti> {
    boolean complete = false;
    CSVReader csvReader;

    @Override
    public boolean isComplete() {
        return complete;
    }

    public List<Projeti> parse(FileReader reader, long numberOfLines) {
        List<Projeti> objects = new ArrayList<>();
        try {
            if (csvReader ==null)
            csvReader = new CSVReader(reader);
            String[] values;

            for (int i = 0; i < numberOfLines; i++) {
                values = csvReader.readNext();
                if (values == null) {
                    complete = true;
                    reader.close();
                    break;
                }
                Projeti projeti = parseObjectFromLine(values);
                objects.add(projeti);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return objects;
    }

    private Projeti parseObjectFromLine(String[] values) {
        Auto auto = new Auto(values[0], Integer.valueOf(values[1]), values[2], values[3]);
        Ridic ridic = new Ridic(values[4], values[5]);
        Brana brana = new Brana(values[11], Float.valueOf(values[12]), Float.valueOf(values[13]), Float.valueOf(values[15]), values[14]);
        return new Projeti(convertUnixTimeToTimestamp(values[6]), Integer.valueOf(values[7]), Integer.valueOf(values[8]), Float.valueOf(values[9]), ridic, brana, auto);
    }

    private Timestamp convertUnixTimeToTimestamp(String value) {
        return new Timestamp(Long.valueOf(value) * 1000L);
    }
}
