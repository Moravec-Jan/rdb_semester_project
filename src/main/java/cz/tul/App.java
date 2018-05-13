package cz.tul;

import cz.tul.controller.MainController;
import cz.tul.mysql.model.ProjetiMysql;
import cz.tul.utils.CsvCreator;
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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@EnableTransactionManagement
@SpringBootApplication
public class App extends Application {
    private static App instance;


    private ApplicationContext springContext;

    private static final String defaultProfile = "hbase";

    @Bean
    public CsvCreator<ProjetiMysql> getCreater() {
        return new ProjetiCsvCreater();
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

    public static URL getLayoutResource(String fileName) {
        return App.class.getClassLoader().getResource("layout/" + fileName);
    }
}
