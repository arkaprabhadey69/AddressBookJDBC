package com.bl.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService(){

    }

    public static EmployeePayrollDBService getInstance(){
        if(employeePayrollDBService==null)
            employeePayrollDBService= new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    public List<EmployeePayrollData> readData() throws SQLException {
        String sql= "SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollDataList= new ArrayList<>();
        try(Connection connection = this.getConnection();) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dept = resultSet.getString("dept");
                String name = resultSet.getString("name");
                String phone_number = resultSet.getString("phone_number");
                String address = resultSet.getString("address");
                String gender = resultSet.getString("gender");
                LocalDate startdate = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id, dept, name, phone_number, address, gender, startdate));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return employeePayrollDataList;

    }

    private Connection getConnection() throws SQLException {
        String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?userSSL=false";
        String userName="root";
        String password="root";
        Connection connection;
        System.out.println("Connecting to . . ."+jdbcURL);
        connection= DriverManager.getConnection(jdbcURL,userName,password);
        System.out.println("Connected "+ connection);
        return connection;
    }

    public int updateEmployeeData(String name, String address) {
        return this.updateEmployeeDataUsingStatement(name,address);
    }

    private int updateEmployeeDataUsingStatement(String name, String address) {
        String sql=String.format("update employee_payroll set address='%s' where name='%s';",address,name);
        try(Connection connection=this.getConnection()){
            Statement statement= connection.createStatement();
            return statement.executeUpdate(sql);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayroll(String name) throws SQLException {
        List<EmployeePayrollData> employeePayrollDataList=null;
        if(this.employeePayrollDataStatement==null)
            this.prepareStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet= employeePayrollDataStatement.executeQuery();
            employeePayrollDataList=this.getEmployeePayroll(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;
        
    }

    private List<EmployeePayrollData> getEmployeePayroll(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollDataList= new ArrayList<>();
        try(Connection connection = this.getConnection()){
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dept = resultSet.getString("dept");
                String name = resultSet.getString("name");
                String phone_number = resultSet.getString("phone_number");
                String address = resultSet.getString("address");
                String gender = resultSet.getString("gender");
                LocalDate startdate = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id, dept, name, phone_number, address, gender, startdate));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;

    }

    private void prepareStatementForEmployeeData() throws SQLException {
            Connection connection=this.getConnection();
            String sql="SELECT * FROM employee_payroll where name= ?";
            employeePayrollDataStatement=connection.prepareStatement(sql);


    }
}
