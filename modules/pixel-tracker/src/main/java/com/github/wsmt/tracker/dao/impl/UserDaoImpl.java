package com.github.wsmt.tracker.dao.impl;

import com.github.wsmt.tracker.dao.UserDao;
import com.github.wsmt.tracker.model.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final Configuration hBaseConfiguration;
    private final TableName profileTable;

    private UserDaoImpl(Configuration hBaseConfiguration, TableName profileTable) {
        this.hBaseConfiguration = hBaseConfiguration;
        this.profileTable = profileTable;
    }

    public static UserDaoImpl newUserDaoImpl(Configuration configuration, String tableName) {
        return new UserDaoImpl(configuration, TableName.valueOf(tableName));
    }

    @Override
    public User save(User user) {

        ConnectionFactory.createAsyncConnection(hBaseConfiguration)
                .thenAccept(connection -> connection.getAdmin()
                        .tableExists(profileTable)
                        .thenCompose(tableExists -> {
                            if (!tableExists) {
                                return connection.getAdmin()
                                        .createTable(TableDescriptorBuilder.newBuilder(profileTable)
                                                .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(
                                                        Bytes.toBytes("HISTORY")
                                                )
                                                        .setMaxVersions(100)
                                                        .build())
                                                .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(
                                                        Bytes.toBytes("HEADER")
                                                )
                                                        .setMaxVersions(100)
                                                        .build())
                                                .build());
                            }

                            return CompletableFuture.completedFuture(null);
                        })
                        .thenCompose(response -> {
                            Put put = new Put(Bytes.toBytes(DigestUtils.md5Hex(user.getId())));
//                            put.addColumn(
//                                    Bytes.toBytes("HISTORY"),
//                                    Bytes.toBytes("url"),
//                                    Bytes.toBytes(response.uri()
//                                            .toString())
//                            );
                            user.getBehavior().forEach((key, value) -> put.addColumn(
                                    Bytes.toBytes("HEADER"),
                                    Bytes.toBytes(key.toLowerCase()),
                                    Bytes.toBytes(value)
                            ));
                            return connection.getTable(profileTable)
                                    .put(put);
                        })
                        .whenComplete((response, exception) -> {
                            if (exception != null) {
                                log.warn("Tracker does not track visit.", exception);
                            }
                            try {
                                connection.close();
                            } catch (IOException e) {
                                log.warn("Tracker does not close connection.", e);
                            }
                        }));
        return user;
    }

    public void resetTable() {

        ConnectionFactory.createAsyncConnection(hBaseConfiguration)
                .thenAccept(connection -> connection.getAdmin()
                        .disableTable(profileTable)
                        .thenCompose(result -> connection.getAdmin()
                                .deleteTable(profileTable)
                        )
                        .thenCompose(result -> connection.getAdmin()
                                .createTable(TableDescriptorBuilder.newBuilder(profileTable)
                                        .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(
                                                Bytes.toBytes("HISTORY")
                                        )
                                                .setMaxVersions(100)
                                                .build())
                                        .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(
                                                Bytes.toBytes("HEADER")
                                        )
                                                .setMaxVersions(100)
                                                .build())
                                        .build()))
                        .whenComplete((response, exception) -> {
                            if (exception != null) {
                                log.warn("Tracker does not track visit.", exception);
                            }
                            try {
                                connection.close();
                            } catch (IOException e) {
                                log.warn("Tracker does not close connection.", e);
                            }
                        }));
    }
}
