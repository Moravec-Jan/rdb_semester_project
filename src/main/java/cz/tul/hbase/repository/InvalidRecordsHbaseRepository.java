package cz.tul.hbase.repository;

import cz.tul.hbase.model.InvalidHbaseRecords;
import cz.tul.model.generic.InvalidRecords;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Profile("hbase")
@Repository
public class InvalidRecordsHbaseRepository {
    public static final String INVALID_RECORDS_QUALIFIER = "invalid_records";

    @Autowired
    Connection connection;

    public void increment(long value) throws IOException {
        Table table = getTable();
        Increment inc = new Increment(getId());
        inc.addColumn(InvalidRecords.COLUMN_FAMILY.getBytes(), INVALID_RECORDS_QUALIFIER.getBytes(), value);
        table.increment(inc);
    }

    public InvalidRecords get() throws IOException {
        Table table = getTable();
        Get get = new Get(getId());
        get.addColumn(InvalidRecords.COLUMN_FAMILY.getBytes(), INVALID_RECORDS_QUALIFIER.getBytes());
        Result result = table.get(get);
        byte[] value = result.getValue(InvalidRecords.COLUMN_FAMILY.getBytes(), INVALID_RECORDS_QUALIFIER.getBytes());
        long i = Bytes.toLong(value);
        return new InvalidHbaseRecords((int) i);
    }

    public void reset() throws IOException {
        Table table = getTable();
        Put p = new Put(getId());
        p.addColumn(InvalidRecords.COLUMN_FAMILY.getBytes(), InvalidRecordsHbaseRepository.INVALID_RECORDS_QUALIFIER.getBytes(), Bytes.toBytes(0L));
        table.put(p);
    }

    private Table getTable() throws IOException {
        return connection.getTable(TableName.valueOf(InvalidRecords.TABLE_NAME));
    }


    byte[] getId() {
        return Bytes.toBytes(1);
    }
}
