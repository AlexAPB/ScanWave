package com.fatec.rfidscanwave.model;

public class JobModel {
    private int id;
    private DepartmentModel department;
    private String jobName;

    public void setId(int id) {
        this.id = id;
    }

    public void setDepartment(DepartmentModel department) {
        this.department = department;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getId() {
        return id;
    }

    public DepartmentModel getDepartment() {
        return department;
    }

    public String getJobName() {
        return jobName;
    }

    @Override
    public String toString() {
        return getJobName();
    }
}
