package com.bl.db;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class EmployeePayrollData {
    public int id;
    public String dept;
    public String name;
    public String phone_number;
    public String address;
    public String gender;
    public LocalDate start;

    public EmployeePayrollData(int id, String dept, String name, String phone_number, String address, String gender, LocalDate start) {
        this.id = id;
        this.dept = dept;
        this.name = name;
        this.phone_number = phone_number;
        this.address = address;
        this.gender = gender;
        this.start = start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id &&
                dept.equals(that.dept) &&
                name.equals(that.name) &&
                phone_number.equals(that.phone_number) &&
                address.equals(that.address) &&
                gender.equals(that.gender) &&
                start.equals(that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dept, name, phone_number, address, gender, start);
    }
}
