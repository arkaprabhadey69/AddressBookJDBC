package com.bl.db;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class EmployeePayrollServiceTest {
    @Test
    public void readData() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData();
        Assert.assertEquals(4, employeePayrollDataList.size());

    }

    @Test
    public void givenNewAddressForEmployee_WhenUpdated_ShouldMatch() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData();
        employeePayrollService.updateEmployeeAddress("Charlie", "Kalighat");
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Charlie");
        Assert.assertTrue(result);


    }

    @Test
    public void readDataWithinDate() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollDataWithinDate();
        Assert.assertEquals(3, employeePayrollDataList.size());
    }

    @Test
    public void getEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        int countFemale = 0;
        countFemale = employeePayrollService.getCountOfEmployees("F");
        int countMale = 0;
        countMale = employeePayrollService.getCountOfEmployees("F");
        Assert.assertEquals(2, countFemale);
        Assert.assertEquals(2, countMale);


    }

    @Test
    public void getEmployeeAvgSalary() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        int avg = 0;
        avg=employeePayrollService.getAvgSalaryOfEmployees("F");
        int avg1 = 0;
        avg1=employeePayrollService.getAvgSalaryOfEmployees("M");
        Assert.assertEquals(102, avg);
        Assert.assertEquals(102, avg1);

    }
    @Test
    public void getEmployeeMaxMinSum() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        int minFemale= 0;
        minFemale = employeePayrollService.getMaxMinSalaryOfEmployees("F",EmployeePayrollDBService.getMin);
        Assert.assertEquals(30000, minFemale);
        int maxFemale= 0;
        maxFemale = employeePayrollService.getMaxMinSalaryOfEmployees("F",EmployeePayrollDBService.getMax);
        Assert.assertEquals(50000, maxFemale);
        int maxMale= 0;
        maxMale = employeePayrollService.getMaxMinSalaryOfEmployees("M",EmployeePayrollDBService.getMax);
        Assert.assertEquals(50000, maxMale);
        int minMale= 0;
        minMale = employeePayrollService.getMaxMinSalaryOfEmployees("M",EmployeePayrollDBService.getMin);
        Assert.assertEquals(40000, minMale);
        int sumMale= 0;
        sumMale = employeePayrollService.getMaxMinSalaryOfEmployees("M",EmployeePayrollDBService.getSum);
        Assert.assertEquals(90000, sumMale);
        int sumFemale= 0;
        sumFemale = employeePayrollService.getMaxMinSalaryOfEmployees("F",EmployeePayrollDBService.getSum);
        Assert.assertEquals(80000, sumFemale);

    }
    @Test
    public void givenNewEmployeeWhenAddedShouldSyncWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
       employeePayrollService.readEmployeePayrollData();
       employeePayrollService.addEmployeeToPayroll("101","Mark","908765","Swinhoe","M", LocalDate.now());
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Mark");
        Assert.assertTrue(result);

    }

}