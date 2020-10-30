package com.bl.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollService {

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

    public void updateEmployeeAddress(String name, String address) {
        int result=employeePayrollDBService.updateEmployeeData(name,address);
        if(result==0) return;
        EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
        if(employeePayrollData !=null) employeePayrollData.address= address;

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
