package cz.tul;

import cz.tul.controller.MainController;
import cz.tul.model.db.Projeti;
import cz.tul.utils.CsvCreator;
import cz.tul.utils.CsvParser;
import cz.tul.utils.Parser;
import cz.tul.utils.ProjetiCsvCreater;
import javafx.application.Application;
import javafx.stage.Stage;
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
import org.springframework.context.annotation.Profile;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


@EntityScan("cz.tul.model")
@SpringBootApplication
public class App extends Application {
    private static boolean hbase = false;

    private ApplicationContext springContext;

    @Bean
    public CsvCreator<Projeti> getCreater() {
        return new ProjetiCsvCreater();
    }

    @Profile("hbase")
    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        return new HbaseTemplate(conf);
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        if (hbase) {
            hbase();
            return;
        }

        SpringApplication springApplication = new SpringApplication(App.class);
        springContext = springApplication.run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Objects.requireNonNull(MainController.createNewStage(springContext)).show();
    }


    private static List<Projeti> parseDataFromFile() {
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
        return projeti;
    }

    private static void hbase() {

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

    public static URL getLayoutResource(String fileName) {
        return App.class.getClassLoader().getResource("layout/" + fileName);
    }
}
