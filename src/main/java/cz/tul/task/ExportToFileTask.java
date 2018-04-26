package cz.tul.task;

import cz.tul.utils.CsvCreator;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ExportToFileTask<Data, Creator extends CsvCreator<Data>> extends Task<Boolean> {
    private List<Data> data;
    private Creator creater;
    private File csv;
    private int batch = 100;
    private long recordCount;


    public ExportToFileTask(File csv, List<Data> data, Creator creater) {
        this.data = data;
        this.csv = csv;
        this.creater = creater;
    }

    @Override
    protected Boolean call() {
        try {
            Files.deleteIfExists(csv.toPath());
        } catch (IOException ex) {
            ex.printStackTrace();
            //continue
        }
        try {
            Files.createFile(Paths.get(csv.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean end = false;
        try {
            recordCount = data.size();
            int actualCount = 0;
            while (true) {
                updateProgress(actualCount, recordCount);
                List<Data> dataToSave = null;
                if (data.size() > actualCount + batch)
                    dataToSave = data.subList(actualCount, actualCount + batch);
                else {
                    int size = data.size() - 1;
                    int count = size - actualCount;
                    if (count == 0) break;
                    dataToSave = data.subList(actualCount, actualCount + count);
                    end = true;
                }
                saveLinesToFile(dataToSave);

                if (end)
                    break;
                actualCount += batch;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        updateProgress(recordCount, recordCount);
        return true;
    }

    private void saveLinesToFile(List<Data> data) throws IOException {
        List<String> lines = creater.create(data);
        Files.write(csv.toPath(), lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
    }

}
