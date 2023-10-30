package com.fatec.rfidscanwave.model;

import java.util.ArrayList;
import java.util.List;

public class ManagerModel {
    private static ManagerModel manager;
    private int id;
    private String user;
    private String password;
    private int department;

    private ManagerModel(){ }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public int getDepartment() {
        return department;
    }

    public static ManagerModel getInstance() {
        if(manager == null)
            manager = new ManagerModel();

        return manager;
    }

}
