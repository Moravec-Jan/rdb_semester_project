package cz.tul.task;

import cz.tul.model.db.Projeti;
import cz.tul.service.DatabaseService;
import cz.tul.utils.CsvParser;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class ImportFileTask extends Task<Boolean> {


    private DatabaseService service;
    private File csv;
    private long batch = 100;
    private long recordCount;


    public ImportFileTask(DatabaseService service, File csv) {
        this.service = service;
        this.csv = csv;
    }

    public ImportFileTask() {
    }

    public void setBatch(long batch) {
        this.batch = batch;
    }


    @Override
    protected Boolean call() throws IOException {

        LineNumberReader lineCount = new LineNumberReader(new FileReader(csv));
        lineCount.skip(Long.MAX_VALUE);
        recordCount = lineCount.getLineNumber();
        lineCount.close();


        long actualCount = 0L;
        List<Projeti> loaded;
        CsvParser parser = new CsvParser();
        FileReader reader = new FileReader(csv);
        while (!parser.isComplete()) {

            updateProgress(actualCount, recordCount);
            loaded = parser.parse(reader, batch);
            service.saveWholeRecords(loaded);
            actualCount += batch;
        }

        return true;
    }
}
