package com.bl.db;

//import io.restassured.RestAssured;
//import io.restassured.response.Response;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EmployeePayrollServiceTest {
    @Test
    public void readData() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData();
        Assert.assertEquals(15, employeePayrollDataList.size());

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
        avg = employeePayrollService.getAvgSalaryOfEmployees("F");
        int avg1 = 0;
        avg1 = employeePayrollService.getAvgSalaryOfEmployees("M");
        Assert.assertEquals(102, avg);
        Assert.assertEquals(102, avg1);

    }

    @Test
    public void getEmployeeMaxMinSum() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        int minFemale = 0;
        minFemale = employeePayrollService.getMaxMinSalaryOfEmployees("F", EmployeePayrollDBService.getMin);
        Assert.assertEquals(30000, minFemale);
        int maxFemale = 0;
        maxFemale = employeePayrollService.getMaxMinSalaryOfEmployees("F", EmployeePayrollDBService.getMax);
        Assert.assertEquals(50000, maxFemale);
        int maxMale = 0;
        maxMale = employeePayrollService.getMaxMinSalaryOfEmployees("M", EmployeePayrollDBService.getMax);
        Assert.assertEquals(50000, maxMale);
        int minMale = 0;
        minMale = employeePayrollService.getMaxMinSalaryOfEmployees("M", EmployeePayrollDBService.getMin);
        Assert.assertEquals(40000, minMale);
        int sumMale = 0;
        sumMale = employeePayrollService.getMaxMinSalaryOfEmployees("M", EmployeePayrollDBService.getSum);
        Assert.assertEquals(90000, sumMale);
        int sumFemale = 0;
        sumFemale = employeePayrollService.getMaxMinSalaryOfEmployees("F", EmployeePayrollDBService.getSum);
        Assert.assertEquals(80000, sumFemale);

    }

    @Test
    public void givenNewEmployeeWhenAddedShouldSyncWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData();
        employeePayrollService.addEmployeeToPayroll("101", "Mark", "908765", "Swinhoe", "M", LocalDate.now());
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Mark");
        Assert.assertTrue(result);

    }

    @Test
    public void givenEmployee_WhenRemoved_ShouldMatch() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData();
        employeePayrollService.removeEmployee("Charlie");
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Charlie");
        Assert.assertTrue(result);


    }

    @Test
    public void given3Employees_WhenAdded_ShouldMatch() throws SQLException, InterruptedException {
        EmployeePayrollData[] arrayOfEmployees = {new EmployeePayrollData(0, "101", "Dan", "908765", "Swinhoe", "M", LocalDate.now()),
                new EmployeePayrollData(0, "101", "Mark", "908765", "Swinhoe", "M", LocalDate.now()),
                new EmployeePayrollData(0, "103", "Modugu", "9087657", "Swinhoe", "M", LocalDate.now())};
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        Instant start = Instant.now();
        employeePayrollService.addEmployeesWithoutThreads(Arrays.asList(arrayOfEmployees));
        Instant end = Instant.now();
        System.out.println("DurationWithoutThreads: " + Duration.between(start, end));
        employeePayrollService.readEmployeePayrollData();
        Instant start1 = Instant.now();
        employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmployees));
        Thread.sleep(160);
        Instant end1 = Instant.now();
        System.out.println("DurationWithThreads: " + Duration.between(start1, end1));
        Assert.assertEquals(16, employeePayrollService.countEntries());


    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4000;
    }

    public EmployeePayrollData[] getEmployees() {
        Response response = RestAssured.get("/employees/list");
        System.out.println(response.asString());
        EmployeePayrollData[] employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
        return employeePayrollData;

    }

    public Response addEmployee(EmployeePayrollData employeePayrollData) {
        String empJSon = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJSon);
        return request.post("/employees/create");

    }

    @Test
    public void givenEmployeeDataInJSONServer_WhenRetrieved_ShouldMatchCount() {
        EmployeePayrollData[] employeePayrollData = getEmployees();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(employeePayrollData));
        int count = employeePayrollService.countEntries();
        Assert.assertEquals(4, count);


    }

    @Test
    public void employeesWhenAddedShouldReturnTrue() {
        EmployeePayrollData employeePayrollData1 = new EmployeePayrollData(0, "101", "Nayan", "908765", "Swinhoe", "F", LocalDate.now());
        Response response = addEmployee(employeePayrollData1);
        int status = response.getStatusCode();
        Assert.assertEquals(201, status);
        EmployeePayrollData[] employeePayrollData = getEmployees();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(employeePayrollData));
        int count = employeePayrollService.countEntries();
        Assert.assertEquals(5, count);
    }

    @Test
    public void multipleEmployeesWhenAddedShouldReturnTrue() {
        EmployeePayrollData[] arrayOfEmployees = {new EmployeePayrollData(0, "101", "Dan", "908765", "Swinhoe", "M", LocalDate.now()),
                new EmployeePayrollData(0, "101", "Wood", "908765", "Swinhoe", "M", LocalDate.now()),
                new EmployeePayrollData(0, "103", "Prince", "9087657", "Swinhoe", "M", LocalDate.now())};
        for (EmployeePayrollData employeePayrollData : arrayOfEmployees) {
            Response response = addEmployee(employeePayrollData);
            int status = response.getStatusCode();
            Assert.assertEquals(201, status);
        }
        EmployeePayrollData[] employeePayrollData = getEmployees();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(employeePayrollData));
        int count = employeePayrollService.countEntries();
        Assert.assertEquals(8, count);
    }

    @Test
    public void givenNameShouldUpdateDept() {
        EmployeePayrollData[] employeePayrollData = getEmployees();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(employeePayrollData));
        employeePayrollService.updateEmployeeAddressUsingREST("Pankaj", "Hazra");
        EmployeePayrollData employeePayrollData1 = employeePayrollService.getEmployeePayrollData("Pankaj");
        System.out.println(employeePayrollData1.address);
        String empJSon = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJSon);
        Response response = request.put("/employees/update/" + employeePayrollData1.id);
        int status = response.getStatusCode();
        Assert.assertEquals(200, status);


    }
    @Test
    public void givenNameShouldDeleteDept() {
        EmployeePayrollData[] employeePayrollData = getEmployees();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(employeePayrollData));
        EmployeePayrollData employeePayrollData1 = employeePayrollService.getEmployeePayrollData("Wood");
        String empJSon = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJSon);
        Response response = request.delete("/employees/delete/" + employeePayrollData1.id);
        int status = response.getStatusCode();
        Assert.assertEquals(200, status);
        EmployeePayrollData[] employeePayrollData2 = getEmployees();
        Assert.assertEquals(7,employeePayrollData2.length);


    }
}


