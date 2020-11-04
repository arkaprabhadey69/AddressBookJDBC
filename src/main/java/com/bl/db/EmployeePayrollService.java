package com.bl.db;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

    public enum IOService {DB_IO, FILE_IO, REST_IO}

    public List<EmployeePayrollData> employeePayrollDataList;
    private EmployeePayrollDBService employeePayrollDBService;


    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
        this.employeePayrollDataList = new ArrayList<>();
    }

    public List<EmployeePayrollData> readEmployeePayrollData() throws SQLException {
        this.employeePayrollDataList = employeePayrollDBService.readData();
        return this.employeePayrollDataList;

    }

    public List<EmployeePayrollData> readEmployeePayrollDataWithinDate() throws SQLException {
        this.employeePayrollDataList = employeePayrollDBService.readDataWithinDate();
        return this.employeePayrollDataList;

    }

    public int getCountOfEmployees(String gender) throws SQLException {
        int count = 0;
        count = employeePayrollDBService.getEmployeeCount(gender);
        return count;

    }

    public int getAvgSalaryOfEmployees(String gender) throws SQLException {
        int avg = 0;
        avg = employeePayrollDBService.getEmployeeAvg(gender);
        return avg;

    }

    public int getMaxMinSalaryOfEmployees(String gender, String maxMin) throws SQLException {
        int avg = 0;
        avg = employeePayrollDBService.getEmployeeMaxMin(gender, maxMin);
        return avg;

    }


    public void updateEmployeeAddress(String name, String address) {
        int result = employeePayrollDBService.updateEmployeeData(name, address);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null) employeePayrollData.address = address;

    }

    public void addEmployeeToPayroll(String dept, String name, String number, String address, String gender, LocalDate date) throws SQLException {
        employeePayrollDataList.add(employeePayrollDBService.addEmployeeToPayroll(dept, name, number, address, gender, date));
    }

    public void addEmployeesWithoutThreads(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            try {
                this.addEmployeeToPayroll(employeePayrollData.dept, employeePayrollData.name, employeePayrollData.phone_number, employeePayrollData.address, employeePayrollData.gender, employeePayrollData.start);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void addEmployeeWithThreads(List<EmployeePayrollData> employeePayrollList) throws SQLException {
        Map<Integer, Boolean> employeeStatus = new HashMap<>();
        employeePayrollList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                try {
                    employeeStatus.put(employeePayrollData.hashCode(), false);
                    System.out.println("Adding.." + Thread.currentThread().getName());
                    this.addEmployeeToPayroll(employeePayrollData.dept, employeePayrollData.name, employeePayrollData.phone_number, employeePayrollData.address, employeePayrollData.gender, employeePayrollData.start);
                    employeeStatus.put(employeePayrollData.hashCode(), true);
                    System.out.println("Added!" + Thread.currentThread().getName());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });

    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        for (EmployeePayrollData employeePayrollData : employeePayrollDataList) {
            Runnable task = () -> {
                try {
                    employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                    System.out.println("Employee Being Added: " + Thread.currentThread().getName());
                    addEmployeeToPayroll(employeePayrollData.dept, employeePayrollData.name, employeePayrollData.phone_number, employeePayrollData.address, employeePayrollData.gender, employeePayrollData.start);
                    employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
                    System.out.println("Employee Added: " + Thread.currentThread().getName());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        }
        while (employeeAdditionStatus.containsValue(false)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(this.employeePayrollDataList);
    }

    public int countEntries() {
        return this.employeePayrollDataList.size();
    }


    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollDataList.stream()
                .filter(employeePayrollData -> employeePayrollData.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public void removeEmployee(String name) {
        employeePayrollDBService.removeEmployeeData(name);
    }

    public boolean checkEmployeePayrollSyncWithDB(String name) throws SQLException {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayroll(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));


    }
}
