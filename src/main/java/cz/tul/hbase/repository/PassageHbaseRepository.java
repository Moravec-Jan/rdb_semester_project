package cz.tul.hbase.repository;

import cz.tul.hbase.model.GatePassageHbaseProjection;
import cz.tul.hbase.model.DriverHbase;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Gate;
import cz.tul.mysql.model.Driver;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Profile("hbase")
@Repository
public class PassageHbaseRepository {
    private static final String PROJETI_CAS_QUALIFIER = "projeti.cas";
    private static final String PROJETI_BENZIN_QUALIFIER = "projeti.benzin";
    private static final String PROJETI_NAJETO_QUALIFIER = "projeti.najeto";
    private static final String PROJETI_NAPETI_QUALIFIER = "projeti.napeti";
    private static final String PROJETI_LONGTITUDE_QUALIFIER = "projeti.longtitude";
    private static final String PROJETI_LATITUDE_QUALIFIER = "projeti.latitude";
    private static final String RIDIC_JMENO_QUALIFIER = "ridic.jmeno";
    private static final String RIDIC_CRP_QUALIFIER = "ridic.crp";
    private static final String AUTO_TYP_QUALIFIER = "auto.typ";
    private static final String AUTO_BARVA_QUALIFIER = "auto.barva";
    private static final String AUTO_VYROBCE_QUALIFIER = "auto.vyrobce";
    private static final String AUTO_SPZ_QUALIFIER = "auto.spz";
    private static final String BRANA_ID_QUALIFIER = "brana.id";
    private static final String BRANA_CENA_QUALIFIER = "brana.cena";
    private static final String BRANA_TYP_QUALIFIER = "brana.typ";

    @Autowired
    Admin admin;

    @Autowired
    Connection connection;

    @Autowired
    AggregationClient aggregationClient;

    public Driver getRidicByCrp(String crp) throws IOException {
        Table table = getTable();
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), RIDIC_CRP_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), RIDIC_JMENO_QUALIFIER.getBytes());
        scan.setFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                RIDIC_CRP_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, crp.getBytes()));
        ResultScanner results = table.getScanner(scan);
        Result next = results.next();
        if (next == null)
            return null;
        byte[] crpBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), RIDIC_CRP_QUALIFIER.getBytes());
        byte[] jmenoBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), RIDIC_JMENO_QUALIFIER.getBytes());
        return new Driver(Bytes.toString(crpBytes), Bytes.toString(jmenoBytes));
    }

    public List<DriverHbase> getDriverByDate(Timestamp from, Timestamp to) throws IOException {
        Table table = getTable();
        Scan scan = new Scan();
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), RIDIC_CRP_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), RIDIC_JMENO_QUALIFIER.getBytes());
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                PROJETI_CAS_QUALIFIER.getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(from.getTime())));
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                PROJETI_CAS_QUALIFIER.getBytes(), CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(to.getTime())));
        scan.setFilter(filterList);
        ResultScanner results = table.getScanner(scan);
        Result next = results.next();
        if (next == null)
            return null;
        List<DriverHbase> list = new ArrayList<>();
        for(Result result: results) {
            list.add(parseDriverHbase(result));
        }
        return list;
    }

    private DriverHbase parseDriverHbase(Result next) {
        byte[] row = next.getRow();
        String rowId = Bytes.toString(row);
        byte[] crpBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), RIDIC_CRP_QUALIFIER.getBytes());
        byte[] jmenoBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), RIDIC_JMENO_QUALIFIER.getBytes());
        return new DriverHbase(Bytes.toString(crpBytes), rowId, Bytes.toString(jmenoBytes));
    }

    public boolean hasPassedSatelliteGate(Timestamp from, Timestamp to, String crp) throws IOException {
        Table table = getTable();
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                PROJETI_CAS_QUALIFIER.getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(from.getTime())));
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                PROJETI_CAS_QUALIFIER.getBytes(), CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(to.getTime())));
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                BRANA_TYP_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, Bytes.toBytes("Satellite")));
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                RIDIC_CRP_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(crp)));
        scan.setFilter(filterList);
        ResultScanner results = table.getScanner(scan);
        Result next = results.next();
        return next != null;
    }

    public Car getAutoBySpz(String spz) throws IOException {
        Table table = getTable();
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), AUTO_SPZ_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), AUTO_TYP_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), AUTO_VYROBCE_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), AUTO_BARVA_QUALIFIER.getBytes());
        scan.setFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                AUTO_SPZ_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, spz.getBytes()));
        ResultScanner results = table.getScanner(scan);
        Result next = results.next();
        if (next == null)
            return null;
        byte[] spzBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_SPZ_QUALIFIER.getBytes());
        byte[] typBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_TYP_QUALIFIER.getBytes());
        byte[] vyrobceBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_VYROBCE_QUALIFIER.getBytes());
        byte[] barvaBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_BARVA_QUALIFIER.getBytes());
        return new Car(Bytes.toString(spzBytes), Bytes.toInt(barvaBytes), Bytes.toString(vyrobceBytes), Bytes.toString(typBytes));
    }

    public Gate getBranaById(String id) throws IOException {
        Table table = getTable();
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), BRANA_CENA_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), BRANA_TYP_QUALIFIER.getBytes());
        scan.addColumn(Passage.COLUMN_FAMILY.getBytes(), BRANA_ID_QUALIFIER.getBytes());
        scan.setFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                BRANA_ID_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, id.getBytes()));
        ResultScanner results = table.getScanner(scan);
        Result next = results.next();
        if (next == null)
            return null;
        byte[] idBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), BRANA_ID_QUALIFIER.getBytes());
        String idString = Bytes.toString(idBytes);
        byte[] cenaBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), BRANA_CENA_QUALIFIER.getBytes());
        byte[] typBytes = next.getValue(Passage.COLUMN_FAMILY.getBytes(), BRANA_TYP_QUALIFIER.getBytes());
        return new Gate(id, Bytes.toFloat(cenaBytes), Bytes.toString(typBytes));
    }

    private Table getTable() throws IOException {
        return connection.getTable(TableName.valueOf(Passage.TABLE_NAME));
    }

    public List<GatePassageProjection> getByBranaId(String id) throws IOException {
        Table table = getTable();
        Scan scan = new Scan();
        scan.setFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                BRANA_ID_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, id.getBytes()));
        ResultScanner results = table.getScanner(scan);
        List<GatePassageProjection> list = new ArrayList<>();
        for (Result result : results) {
            list.add(mapResultToGatePassageProjection(result));
        }
        return list;
    }

    private GatePassageHbaseProjection mapResultToGatePassageProjection(Result result) {
        byte[] casBytes = result.getValue(Passage.COLUMN_FAMILY.getBytes(), PROJETI_CAS_QUALIFIER.getBytes());
        byte[] spzBytes = result.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_SPZ_QUALIFIER.getBytes());
        byte[] vyrobceBytes = result.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_VYROBCE_QUALIFIER.getBytes());
        byte[] typBytes = result.getValue(Passage.COLUMN_FAMILY.getBytes(), AUTO_TYP_QUALIFIER.getBytes());
        byte[] crpBytes = result.getValue(Passage.COLUMN_FAMILY.getBytes(), RIDIC_CRP_QUALIFIER.getBytes());
        byte[] jmenoBytes = result.getValue(Passage.COLUMN_FAMILY.getBytes(), RIDIC_JMENO_QUALIFIER.getBytes());

        Timestamp cas = new Timestamp(Bytes.toLong(casBytes));
        String spz = Bytes.toString(spzBytes);
        String vyrobce = Bytes.toString(vyrobceBytes);
        String typ = Bytes.toString(typBytes);
        String crp = Bytes.toString(crpBytes);
        String jmeno = Bytes.toString(jmenoBytes);
        return new GatePassageHbaseProjection(cas, spz, vyrobce, typ, crp, jmeno);
    }

    //@Transactional
    public void delete(String tableNameString) throws IOException {
        TableName tableName = TableName.valueOf(tableNameString);
        Table table = connection.getTable(tableName);
        Scan s = new Scan();
        List<Delete> listOfBatchDelete = new ArrayList<Delete>();
        //add your filters to the scanner
        s.addFamily(Passage.COLUMN_FAMILY.getBytes());
        ResultScanner scanner = table.getScanner(s);
        for (Result rr : scanner) {
            Delete d = new Delete(rr.getRow());
            listOfBatchDelete.add(d);
        }
        try {
            table.delete(listOfBatchDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Transactional
    public void save(Passage projeti) throws IOException {
        String id = createId(projeti);
        Table table = getTable();
        Put p = new Put(id.getBytes());
        byte[] columnFamily = Passage.COLUMN_FAMILY.getBytes();
        p.addColumn(columnFamily, RIDIC_JMENO_QUALIFIER.getBytes(), projeti.getRidic().getJmeno().getBytes());
        p.addColumn(columnFamily, RIDIC_CRP_QUALIFIER.getBytes(), projeti.getRidic().getCrp().getBytes());
        p.addColumn(columnFamily, AUTO_TYP_QUALIFIER.getBytes(), projeti.getAuto().getTyp().getBytes());
        p.addColumn(columnFamily, AUTO_VYROBCE_QUALIFIER.getBytes(), projeti.getAuto().getVyrobce().getBytes());
        p.addColumn(columnFamily, AUTO_BARVA_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getAuto().getBarva()));
        p.addColumn(columnFamily, AUTO_SPZ_QUALIFIER.getBytes(), projeti.getAuto().getSpz().getBytes());
        p.addColumn(columnFamily, BRANA_TYP_QUALIFIER.getBytes(), projeti.getBrana().getTyp().getBytes());
        p.addColumn(columnFamily, BRANA_CENA_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getBrana().getCena()));
        p.addColumn(columnFamily, BRANA_ID_QUALIFIER.getBytes(), projeti.getBrana().getId().getBytes());
        p.addColumn(columnFamily, PROJETI_CAS_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getCas().getTime()));
        p.addColumn(columnFamily, PROJETI_BENZIN_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getBenzin()));
        p.addColumn(columnFamily, PROJETI_NAJETO_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getNajeto()));
        p.addColumn(columnFamily, PROJETI_NAPETI_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getNapeti()));
        p.addColumn(columnFamily, PROJETI_LONGTITUDE_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getBrana().getLongtitude()));
        p.addColumn(columnFamily, PROJETI_LATITUDE_QUALIFIER.getBytes(), Bytes.toBytes(projeti.getBrana().getLatitude()));
        table.put(p);
    }

    private String createId(Passage projeti) {
        return projeti.getCas().getTime() + projeti.getRidic().getCrp();
    }

    public int findDistance(Timestamp from, Timestamp to, String crp) throws Throwable {
        Table table = getTable();
        Scan scan = new Scan();
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                PROJETI_CAS_QUALIFIER.getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(from.getTime())));
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                PROJETI_CAS_QUALIFIER.getBytes(), CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(to.getTime())));
        filterList.addFilter(new SingleColumnValueFilter(Passage.COLUMN_FAMILY.getBytes(),
                RIDIC_CRP_QUALIFIER.getBytes(), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(crp)));
        scan.setFilter(filterList);
        ResultScanner results = table.getScanner(scan);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Result result : results) {
            byte[] value = result.getValue(Passage.COLUMN_FAMILY.getBytes(), PROJETI_NAJETO_QUALIFIER.getBytes());
            int najeto = Bytes.toInt(value);
            if (najeto < min)
                min = najeto;
            if (najeto > max) {
                max = najeto;
            }
        }
        if (min == Integer.MAX_VALUE)
            return Integer.MIN_VALUE;

        return max - min;
    }
}