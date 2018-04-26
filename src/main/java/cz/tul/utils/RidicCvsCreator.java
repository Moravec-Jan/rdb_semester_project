package cz.tul.utils;

import cz.tul.model.ui.RidicEntity;

import java.util.ArrayList;
import java.util.List;

public class RidicCvsCreator implements CsvCreator<RidicEntity> {
    @Override
    public List<String> create(List<RidicEntity> data) {
        List<String> lines = new ArrayList<>();
        data.forEach(object -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(object.getCrp());
            stringBuilder.append(',');
            stringBuilder.append(object.getJmeno());
            stringBuilder.append(',');
            stringBuilder.append(object.getKm());
            lines.add(stringBuilder.toString());
        });

        return lines;
    }
}
