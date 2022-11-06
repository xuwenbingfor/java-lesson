package com.jz;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    public static void main(String args[]) throws SQLException {
        //注册驱动程序
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        //获得连接
        String url = "jdbc:mysql://10.0.4.12:3307/test?serverTimezone=UTC&useSSL=false";
        Connection con = DriverManager.getConnection(url, "root", "123456");
        System.out.println("Connection established......");
        //检索元数据对象
        DatabaseMetaData metaData = con.getMetaData();
        //检索数据库中基础的URL-
        String dbUrl = metaData.getURL();
        System.out.println("URL for the underlying DBMS: "+dbUrl);
    }
}