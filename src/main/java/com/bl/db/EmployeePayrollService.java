package com.bl.db;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollService {

   // public void addEmployeeToPayroll(String dept , String name, String number, String address, String gender, LocalDate date) {
   // }

    public enum IOService {DB_IO, FILE_IO, REST_IO}
    public List<EmployeePayrollData> employeePayrollDataList;
    private  EmployeePayrollDBService employeePayrollDBService;



    
    public EmployeePayrollService(){
        employeePayrollDBService= EmployeePayrollDBService.getInstance();
        this.employeePayrollDataList=new ArrayList<>();
    }

    public List<EmployeePayrollData> readEmployeePayrollData() throws SQLException {
        this.employeePayrollDataList= employeePayrollDBService.readData();
        return this.employeePayrollDataList;

    }
    public List<EmployeePayrollData> readEmployeePayrollDataWithinDate() throws SQLException {
        this.employeePayrollDataList= employeePayrollDBService.readDataWithinDate();
        return this.employeePayrollDataList;

    }
    public int getCountOfEmployees(String gender) throws SQLException {
        int count=0;
        count= employeePayrollDBService.getEmployeeCount(gender);
        return count;

    }
    public int getAvgSalaryOfEmployees(String gender) throws SQLException {
        int avg=0;
        avg= employeePayrollDBService.getEmployeeAvg(gender);
        return avg;

    }
    public int getMaxMinSalaryOfEmployees(String gender,String maxMin) throws SQLException {
        int avg=0;
        avg= employeePayrollDBService.getEmployeeMaxMin(gender,maxMin);
        return avg;

    }


    public void updateEmployeeAddress(String name, String address) {
        int result=employeePayrollDBService.updateEmployeeData(name,address);
        if(result==0) return;
        EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
        if(employeePayrollData !=null) employeePayrollData.address= address;

    }
    public void addEmployeeToPayroll(String dept , String name, String number, String address, String gender, LocalDate date) {
        employeePayrollDataList.add(employeePayrollDBService.addEmployeeToPayroll( dept , name,  number, address, gender,  date));
    }


    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollDataList.stream()
                .filter(employeePayrollData -> employeePayrollData.name.equals(name))
                .findFirst()
                .orElse(null);
    }
    public boolean checkEmployeePayrollSyncWithDB(String name) throws SQLException {
    List<EmployeePayrollData> employeePayrollDataList= employeePayrollDBService.getEmployeePayroll(name);
    return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));


    }
}
