package com.fatec.rfidscanwave.model;

public class DepartmentModel {
    private int id;
    private String departmentName;

    public DepartmentModel(){

    }

    public DepartmentModel(int id, String departmentName){
        this.id = 0;
        this.departmentName = departmentName;
    }

    public int getId() {
        return id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return getDepartmentName();
    }
}
