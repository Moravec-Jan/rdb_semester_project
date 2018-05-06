package cz.tul.task;

import cz.tul.model.generic.Projeti;
import cz.tul.model.mysql.Brana;
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
    private boolean validateCoordinated = true;


    public ImportFileTask(DatabaseService service, File csv) {
        this.service = service;
        this.csv = csv;
    }

    public ImportFileTask() {
    }

    public void setBatch(long batch) {
        this.batch = batch;
    }

    public void setValidateCoordinated(boolean validateCoordinated) {
        this.validateCoordinated = validateCoordinated;
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
            try {
                saveRecords(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }

            actualCount += batch;
        }

        return true;
    }

    private void saveRecords(List<Projeti> records) {
        AtomicInteger invalidRecordCount = new AtomicInteger();
        if (records != null) {
            records.forEach(record -> {
                if (validateCoordinated) {
                    if (!Brana.validate(record.getBrana().getLongtitude(), record.getBrana().getLatitude())) {
                        invalidRecordCount.getAndIncrement();
                        return;
                    }
                }
                try {
                    service.saveWholeRecord(record);
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
