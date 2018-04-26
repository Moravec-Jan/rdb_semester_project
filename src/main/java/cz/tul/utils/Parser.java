package cz.tul.utils;

import java.io.FileReader;
import java.util.List;

public interface Parser<T> {
    List<T> parse(FileReader reader, long numberOfLines);

    boolean isComplete();
}
