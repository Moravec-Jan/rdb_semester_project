package cz.tul.utils;

import cz.tul.model.ui.BranaTableEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BranaCvsCreator implements CsvCreator<BranaTableEntity> {

    @Override
    public List<String> create(List<BranaTableEntity> data) {
        List<String> lines = new ArrayList<>();
        data.forEach(object -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(convertTimeStampToUnixTime(Timestamp.valueOf(object.getCas())));
            stringBuilder.append(',');
            stringBuilder.append(object.getCrp());
            stringBuilder.append(',');
            stringBuilder.append(object.getJmeno());
            stringBuilder.append(',');
            stringBuilder.append(object.getSpz());
            stringBuilder.append(',');
            stringBuilder.append(object.getVyrobce());
            stringBuilder.append(',');
            stringBuilder.append(object.getTyp());
            lines.add(stringBuilder.toString());
        });

        return lines;
    }

    private long convertTimeStampToUnixTime(Timestamp value) {
        return value.getTime() / 1000L;
    }
}
