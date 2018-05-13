package cz.tul.hbase;

import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Projeti;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.IOException;


@Profile("hbase")
@org.springframework.context.annotation.Configuration
public class HbaseConfig {

    @Bean
    public Configuration hBaseConfiguration() {
        return HBaseConfiguration.create();
    }


    @Bean
    Connection connection(Configuration config) throws IOException {
        return ConnectionFactory.createConnection(config);
    }

    @Bean
    AggregationClient aggregationClient(Configuration config) {
        return new AggregationClient(config);
    }

    @Bean
    public Admin hbaseAdmin(Connection conn) throws IOException {
        return conn.getAdmin();
    }

    static void createScheme(Admin admin) throws IOException {
        TableName tableName = TableName.valueOf(Projeti.TABLE_NAME);
        if (!admin.tableExists(tableName)) {
            admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor(Projeti.COLUMN_FAMILY)));
        }

        TableName tableName2 = TableName.valueOf(InvalidRecords.TABLE_NAME);
        if (!admin.tableExists(tableName2)) {
            admin.createTable(new HTableDescriptor(tableName2).addFamily(new HColumnDescriptor(InvalidRecords.COLUMN_FAMILY)));
        }
    }

}
