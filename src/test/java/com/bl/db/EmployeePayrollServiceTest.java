package com.bl.db;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class EmployeePayrollServiceTest {
    @Test
    public void readData() throws SQLException {
        EmployeePayrollService employeePayrollService= new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList=employeePayrollService.readEmployeePayrollData();
        Assert.assertEquals(4,employeePayrollDataList.size());
       // System.out.println(employeePayrollDataList.size());

    }
    @Test
    public void givenNewAddressForEmployee_WhenUpdated_ShouldMatch() throws SQLException {
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList= employeePayrollService.readEmployeePayrollData();
        employeePayrollService.updateEmployeeAddress("Charlie","Kalighat");
       boolean result=employeePayrollService.checkEmployeePayrollSyncWithDB("Charlie");
       Assert.assertTrue(result);


    }
    @Test
    public void readDataWithinDate() throws SQLException {
        EmployeePayrollService employeePayrollService= new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList=employeePayrollService.readEmployeePayrollDataWithinDate();
        Assert.assertEquals(3,employeePayrollDataList.size());
    }

}