package com.bl.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private static final String getCount = "SELECT COUNT(*) as Count FROM employee_payroll WHERE gender =  ? ";
    private static final String getAvg = " select avg(dept) as avg from employee_payroll where gender=? group by gender;";
    public static final String getMax = " Select max(basic_pay) as salary from employee_payroll e, payroll p where e.id = p.empID and e.gender =? group by e.gender";
    public static final String getMin = " Select min(basic_pay) as salary from employee_payroll e, payroll p where e.id = p.empID and e.gender =? group by e.gender";
    public static final String getSum = " Select sum(basic_pay) as salary from employee_payroll e, payroll p where e.id = p.empID and e.gender =? group by e.gender";

    private EmployeePayrollDBService() {

    }

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    public List<EmployeePayrollData> readData() throws SQLException {
        String sql = "SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection();) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = this.getEmployeePayroll(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return employeePayrollDataList;

    }

    public int getEmployeeCount(String gender) {
        int count = 0;
        try (Connection connection = this.getConnection();) {
            employeePayrollDataStatement = connection.prepareStatement(getCount);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("Count");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return count;

    }

    public int getEmployeeAvg(String gender) {
        int avg = 0;
        try (Connection connection = this.getConnection();) {
            employeePayrollDataStatement = connection.prepareStatement(getAvg);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                avg = resultSet.getInt("avg");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return avg;

    }

    public int getEmployeeMaxMin(String gender, String maxMin) {
        int result = 0;
        try (Connection connection = this.getConnection();) {
            employeePayrollDataStatement = connection.prepareStatement(maxMin);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getInt("salary");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;

    }

    public List<EmployeePayrollData> readDataWithinDate() throws SQLException {
        String sql = "select * from employee_payroll where start between cast('2019-01-01' as date) and DATE(NOW());";
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection();) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollDataList = this.getEmployeePayroll(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return employeePayrollDataList;

    }

    private synchronized Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://127.0.0.1:3306/payroll_service?userSSL=false";
        String userName = "root";
        String password = "root";
        Connection connection;
        System.out.println("Connecting to . . ." + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connected " + connection);
        return connection;
    }

    public int updateEmployeeData(String name, String address) {
        return this.updateEmployeeDataUsingStatement(name, address);
    }

    private int updateEmployeeDataUsingStatement(String name, String address) {
        String sql = String.format("update employee_payroll set address='%s' where name='%s';", address, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayroll(String name) throws SQLException {
        List<EmployeePayrollData> employeePayrollDataList = null;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try {
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollDataList = this.getEmployeePayroll(resultSet);
            getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;

    }

    private List<EmployeePayrollData> getEmployeePayroll(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dept = resultSet.getString("dept");
                String name = resultSet.getString("name");
                String phone_number = resultSet.getString("phone_number");
                String address = resultSet.getString("address");
                String gender = resultSet.getString("gender");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id, dept, name, phone_number, address, gender, startDate));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollDataList;

    }

    private void prepareStatementForEmployeeData() throws SQLException {
        try {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM employee_payroll where name= ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public EmployeePayrollData addEmployeeToPayroll(String dept, String name, String number, String address, String gender, LocalDate date) throws SQLException {
        int id = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (Statement statement = connection.createStatement();) {
            String sql = String.format("Insert into employee_payroll(dept,name,phone_number,address,gender,start) values ('%s','%s','%s','%s','%s','%s')", dept, name, number, address, gender, Date.valueOf(date));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement();){
            double salary=50000;
            double deductions=salary*0.2;
            double taxablePay=salary-deductions;
            double tax=taxablePay*0.1;
            double netPay=salary-tax;
            String sql=String.format("Insert into payroll(empID,basic_pay,deductions,taxable_pay,tax,net_pay) values (%s,%s,%s,%s,%s,%s)",id,salary,deductions,taxablePay,tax,netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(id, dept, name, number, address, gender, date);

            }
            }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
      return employeePayrollData;

    }

    public void removeEmployeeData(String name) {
        String sql = String.format("update employee_payroll set isActive='F' where name='%s';",name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
             statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
