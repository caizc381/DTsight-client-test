package com.dtstack.util.db;

import com.dtstack.base.ConfDefine;
import com.dtstack.util.ConfParser;
import org.apache.hive.service.server.HiveServer2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HiveUtils {
    private Connection connection = null;
    private String driver;
    private String url;
    private String username;
    private String password;
    private String queryTimeout;


    public Connection getHiveConnection() {
        ConfParser cparser = new ConfParser(ConfDefine.ENV_CONFIG);
        driver = cparser.getValue(ConfDefine.HIVE, ConfDefine.HIVE_DRIVER);
        url = cparser.getValue(ConfDefine.HIVE, ConfDefine.HIVE_URL);
        username = cparser.getValue(ConfDefine.HIVE, ConfDefine.HIVE_USERNAME);
        password = cparser.getValue(ConfDefine.HIVE, ConfDefine.HIVE_PASSWORD);
        queryTimeout = cparser.getValue(ConfDefine.HIVE, ConfDefine.HIVE_QUERY_TIMEOUT);


        if (null == connection) {
            synchronized (HiveServer2.class) {
                if (null == connection) {
                    try {
                        Class.forName(driver);
                        connection = DriverManager.getConnection(url, username, password);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }
}
