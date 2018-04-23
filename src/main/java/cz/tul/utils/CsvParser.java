package cz.tul.utils;

import com.opencsv.CSVReader;
import cz.tul.model.Auto;
import cz.tul.model.Brana;
import cz.tul.model.Projeti;
import cz.tul.model.Ridic;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CsvParser implements Parser<Projeti> {

    public List<Projeti> parse(FileReader reader, int numberOfLines) {
        List<Projeti> objects = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(reader);
            String[] values;

            for (int i = 0; i < numberOfLines; i++) {
                values = csvReader.readNext();
                if (values == null)
                    break;
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
        Projeti projeti = new Projeti(new Timestamp(Long.valueOf(values[6])*1000L), Integer.valueOf(values[7]), Integer.valueOf(values[8]), Float.valueOf(values[9]), ridic, brana, auto);
        return projeti;
    }
}
