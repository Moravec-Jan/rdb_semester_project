package cz.tul;

import cz.tul.controller.MainController;
import cz.tul.model.generic.Projeti;
import cz.tul.model.mysql.ProjetiMysql;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@EnableTransactionManagement
@EntityScan("cz.tul.model")
@SpringBootApplication
public class App extends Application {
    private static App instance;

    private static boolean hbase = false;

    private ApplicationContext springContext;

    private static final String defaultProfile = "mysql";

    @Bean
    public CsvCreator<ProjetiMysql> getCreater() {
        return new ProjetiCsvCreater();
    }

    @Profile("hbase")
    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        return new HbaseTemplate(conf);
    }

    public static App getInstance() {
        return instance;
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        instance = this;

        if (hbase) {
            hbase();
            return;
        }
        if (System.getProperty("spring.profiles.active") == null)
            System.setProperty("spring.profiles.active", defaultProfile);
        SpringApplication springApplication = new SpringApplication(App.class);
        springContext = springApplication.run();

    }

    public void restart() {
        ((ConfigurableApplicationContext) springContext).close();
        SpringApplication springApplication = new SpringApplication(App.class);
        springContext = springApplication.run();
        Objects.requireNonNull(MainController.createNewStage(springContext)).show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Objects.requireNonNull(MainController.createNewStage(springContext)).show();
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
