package cz.tul.task;

import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.Gate;
import cz.tul.service.DatabaseService;
import cz.tul.utils.CsvParser;
import javafx.concurrent.Task;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ImportFileTask extends Task<Boolean> {


    private DatabaseService service;
    private File csv;
    private long batch = 100;
    private long recordCount;
    private boolean validateCoordinates = false;


    public ImportFileTask(DatabaseService service, File csv) {
        this.service = service;
        this.csv = csv;
    }

    public ImportFileTask() {
    }

    public void setBatch(long batch) {
        this.batch = batch;
    }

    public void setValidateCoordinates(boolean validateCoordinates) {
        this.validateCoordinates = validateCoordinates;
    }

    @Override
    protected Boolean call() throws IOException {

        LineNumberReader lineCount = new LineNumberReader(new FileReader(csv));
        lineCount.skip(Long.MAX_VALUE);
        recordCount = lineCount.getLineNumber();
        lineCount.close();


        long actualCount = 0L;
        List<Passage> loaded;
        CsvParser parser = new CsvParser();
        FileReader reader = new FileReader(csv);
        while (!parser.isComplete()) {

            updateProgress(actualCount, recordCount);
            loaded = parser.parse(reader, batch);
            try {
                saveRecords(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }

            actualCount += batch;
        }

        return true;
    }

    private void saveRecords(List<Passage> records) {
        AtomicInteger invalidRecordCount = new AtomicInteger();
        if (records != null) {
            records.forEach(record -> {
                if (validateCoordinates) {
                    if (!Gate.validate(record.getBrana().getLongtitude(), record.getBrana().getLatitude())) {
                        invalidRecordCount.getAndIncrement();
                        return;
                    }
                }
                try {
                    boolean success = service.saveWholeRecord(record);
                    if (!success){
                        invalidRecordCount.getAndIncrement();
                    }
                } catch (DataIntegrityViolationException e) {
                    invalidRecordCount.getAndIncrement();
                } catch (Exception e) {
                    e.printStackTrace();
                    invalidRecordCount.getAndIncrement();
                }
            });
        }

        if (invalidRecordCount.get() > 0) {
            service.addInvalidRecords(invalidRecordCount.get());
        }
    }
}
