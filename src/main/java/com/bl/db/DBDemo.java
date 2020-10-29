package com.bl.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DBDemo {
    public static void main(String[] args) {
        String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?userSSL=false";
        String userName="root";
        String password="root";
        Connection connection;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        listDrivers();
        try{
            System.out.println("Connecting to . . ."+jdbcURL);
            connection=DriverManager.getConnection(jdbcURL,userName,password);
            System.out.println("Connected "+ connection);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    public static  void listDrivers(){
        Enumeration<Driver> driverList= DriverManager.getDrivers();
        while (driverList.hasMoreElements()){
            Driver driverClass= (Driver)driverList.nextElement();
            System.out.println("  "+driverClass.getClass().getName());
        }
    }
}
