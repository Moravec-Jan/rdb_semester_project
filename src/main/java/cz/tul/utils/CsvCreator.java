package cz.tul.utils;

import java.util.List;

public interface CsvCreator<T> {
    List<String> create(List<T> data);
}
