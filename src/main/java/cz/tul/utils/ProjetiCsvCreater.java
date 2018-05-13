package cz.tul.utils;

import cz.tul.mysql.model.ProjetiMysql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProjetiCsvCreater implements CsvCreator<ProjetiMysql> {

    @Override
    public List<String> create(List<ProjetiMysql> data) {
        List<String> lines = new ArrayList<>();
        data.forEach(object -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(object.getAuto().getSpz());
            stringBuilder.append(',');
            stringBuilder.append(object.getAuto().getBarva());
            stringBuilder.append(',');
            stringBuilder.append(object.getAuto().getVyrobce());
            stringBuilder.append(',');
            stringBuilder.append(object.getAuto().getTyp());
            stringBuilder.append(',');
            stringBuilder.append(object.getRidic().getCrp());
            stringBuilder.append(',');
            stringBuilder.append(object.getRidic().getJmeno());
            stringBuilder.append(',');
            stringBuilder.append(convertTimeStampToUnixTime(object.getCas()));
            stringBuilder.append(',');
            stringBuilder.append(object.getNajeto());
            stringBuilder.append(',');
            stringBuilder.append(object.getBenzin());
            stringBuilder.append(',');
            stringBuilder.append(object.getNapeti());
            stringBuilder.append(',');
            stringBuilder.append(convertTimeStampToUnixTime(object.getCas()));
            stringBuilder.append(',');
            stringBuilder.append(object.getBrana().getId());
            stringBuilder.append(',');
            stringBuilder.append(object.getBrana().getLongtitude());
            stringBuilder.append(',');
            stringBuilder.append(object.getBrana().getLatitude());
            stringBuilder.append(',');
            stringBuilder.append(object.getBrana().getTyp());
            stringBuilder.append(',');
            stringBuilder.append(object.getBrana().getCena());
            lines.add(stringBuilder.toString());
        });

        return lines;
    }

    private long convertTimeStampToUnixTime(Timestamp value) {
        return value.getTime() / 1000L;
    }
}
