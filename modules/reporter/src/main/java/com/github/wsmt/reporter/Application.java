package com.github.wsmt.reporter;

import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Collections;

public class Application {

    private static ApplicationContext applicationConfig;
    private static String hBaseScanColumnConf = "hbase.mapreduce.scan.columns";


    private static void urlReport() {
        applicationConfig.getHBaseConf().set(hBaseScanColumnConf, "HEADER:referer");
        report("url");
    }

    private static void browserReport() {
        applicationConfig.getHBaseConf().set(hBaseScanColumnConf, "HEADER:user-agent");
        report("browser");
    }

    private static void report(String column) {
        final JavaRDD<Row> result = applicationConfig.getJsc()
                .newAPIHadoopRDD(
                        applicationConfig.getHBaseConf(),
                        TableInputFormat.class,
                        ImmutableBytesWritable.class,
                        Result.class
                )
                .values()
                .map(Result::listCells)
                .flatMap(Iterable::iterator)
                .map(CellUtil::cloneValue)
                .map(Bytes::toString)
                .map(RowFactory::create);

        final StructType schema = DataTypes.createStructType(
                Collections.singletonList(
                        DataTypes.createStructField(column, DataTypes.StringType, false)
                )
        );

        applicationConfig.getSqlContext()
                .createDataFrame(result, schema)
                .registerTempTable(column);


        applicationConfig.getSqlContext()
                .sql("SELECT " + column + ", COUNT(" + column + ") AS count FROM " + column +
                        " GROUP BY " + column + " ORDER BY count DESC")
                //.show();
                .write()
                .format("jdbc")
                .options(applicationConfig.getPostgresConf())
                .option("dbtable", "statistics." + column + "s")
                .mode(SaveMode.Append)
                .save();
    }


    public static void main(String[] args) {

        applicationConfig = ApplicationContext.read(args);
        browserReport();
        urlReport();
//        final ApplicationContext applicationConfig = ApplicationContext.read(args);
//
//        final JavaRDD<Row> result = applicationConfig.getJsc()
//                .newAPIHadoopRDD(
//                        applicationConfig.getHBaseConf(),
//                        TableInputFormat.class,
//                        ImmutableBytesWritable.class,
//                        Result.class
//                )
//                .values()
//                .map(Result::listCells)
//                .flatMap(Iterable::iterator)
//                .map(CellUtil::cloneValue)
//                .map(Bytes::toString)
//                .map(RowFactory::create);
//
//        final StructType schema = DataTypes.createStructType(
//                Collections.singletonList(
//                        DataTypes.createStructField("url", DataTypes.StringType, false)
//                )
//        );
//
//        applicationConfig.getSqlContext()
//                .createDataFrame(result, schema)
//                .registerTempTable("url");
//
//        applicationConfig.getSqlContext()
//                .sql("SELECT url, COUNT(url) AS count FROM url GROUP BY url ORDER BY count DESC")
//                .write()
//                .format("jdbc")
//                .options(applicationConfig.getPostgresConf())
//                .mode(SaveMode.Append)
//                .save();
    }

    private static String getBrowser(String userAgent) {

        String user = userAgent.toLowerCase();

        String browser;
        if (user.equalsIgnoreCase(chrome)) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome"))
                    .split(" ")[0])
                    .replace("/", "-");

        } else if (user.equalsIgnoreCase(epiphany)) {
            browser = (userAgent.substring(userAgent.indexOf("Epiphany"))
                    .split(" ")[0])
                    .replace("/", "-");

        } else {
            browser = "UnKnown";
        }
        return browser;
    }

    private static String chrome = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
    private static String epiphany = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Safari/605.1.15 elementaryOS/0.4 (Loki) Epiphany/3.18.11";
}
