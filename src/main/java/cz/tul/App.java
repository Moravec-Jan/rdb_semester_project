package cz.tul;

import cz.tul.model.Projeti;
import cz.tul.repository.AutoRepository;
import cz.tul.repository.BranaRepository;
import cz.tul.repository.ProjetiRepository;
import cz.tul.repository.RidicRepository;
import cz.tul.utils.CsvParser;
import cz.tul.utils.Parser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@EntityScan("cz.tul.model")
@SpringBootApplication
public class App {
    private static boolean hbase = false;

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        return new HbaseTemplate(conf);
    }

    public static void main(String[] args) {
        if (hbase) {
            hbase();
            return;
        }
        List<Projeti> projeti = null;
        try {
            URL url = App.class.getClassLoader().getResource("sample_data.csv");
            Path path = null;
            path = Paths.get(url.toURI());
            Parser<Projeti> parser = new CsvParser();
            projeti = parser.parse(new FileReader(path.toString()), 100);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }


        SpringApplication app = new SpringApplication(App.class);
        ApplicationContext ctx = app.run(args);
        AutoRepository autoRepository = ctx.getBean(AutoRepository.class);
        RidicRepository ridicRepository = ctx.getBean(RidicRepository.class);
        BranaRepository branaRepository = ctx.getBean(BranaRepository.class);
        ProjetiRepository projetiRepository = ctx.getBean(ProjetiRepository.class);
        if (projeti != null) {
            projeti.forEach(projeti1 -> {
                ridicRepository.save(projeti1.getCrp_ridic());
                autoRepository.save(projeti1.getSpz_auto());
                branaRepository.save(projeti1.getId_brana());
                projetiRepository.save(projeti1);
            });
        }

        Iterable<Projeti> all = projetiRepository.findAll();
        System.out.println("");
//        List<String> strings = carDao.getAll();
//        System.out.println(carDao.getAll());

    }

    public static void hbase() {

        Configuration conf = org.apache.hadoop.hbase.HBaseConfiguration.create();
        org.apache.hadoop.hbase.client.Connection conn = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Admin admin = null;
        try {
            admin = conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TableName tableName = TableName.valueOf("test");
        try {
            if (!admin.tableExists(tableName)) {
                admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor("cf")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Table table = null;
        try {
            table = conn.getTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Put p = new Put(Bytes.toBytes("AAPL10232015"));
        p.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("close"), Bytes.toBytes(119));
        try {
            table.put(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Result r = null;
        try {
            r = table.get(new Get(Bytes.toBytes("AAPL10232015")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }
}
