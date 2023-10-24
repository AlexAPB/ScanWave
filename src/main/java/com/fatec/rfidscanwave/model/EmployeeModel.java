package com.fatec.rfidscanwave.model;

import com.fatec.rfidscanwave.util.ImageUtil;
import com.fatec.rfidscanwave.view.EmployeesView;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.time.LocalDate;
import java.util.List;

public class EmployeeModel {
    private int id;
    private String name;
    private LocalDate birthDate;
    private char gender;
    private String phoneNumber;
    private String address;
    private float salary;
    private String cpf;
    private LocalDate hireDate;
    private int workdayDuration;
    private DepartmentModel department;
    private JobModel job;
    private char workShift;
    private String rfid;
    private ImageView image;
    private List<ClockDayModel> clocks;
    private Circle working;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(Image img, ImageType type) {
        this.image = new ImageView(img);

        this.image.setPreserveRatio(true);
        this.image.setSmooth(true);
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void setJob(JobModel job) {
        this.job = job;

        if(job != null)
            department = job.getDepartment();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setWorkdayDuration(int workdayDuration) {
        this.workdayDuration = workdayDuration;
    }

    public void setClocks(List<ClockDayModel> clocks) {
        this.clocks = clocks;
    }

    public void setWorkShift(char workShift) {
        this.workShift = workShift;
    }

    public Circle getWorking() {
        if(working == null)
            working = new Circle(4);

        if (getClocks().get(getClocks().size() - 1).isWorking())
            working.setFill(Color.GREEN);
        else
            working.setFill(Color.RED);

        return working;
    }

    public Circle getThumbnail(){
        Circle circle = new Circle(25);
        circle.setFill(new ImagePattern(image.getImage()));
        return circle;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ImageView getImage() {
        return image;
    }

    public char getGender() {
        return gender;
    }

    public float getSalary() {
        return salary;
    }

    public String getWorkdayDuration() {
        return workdayDuration + "h";
    }

    public JobModel getJob() {
        return job;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public DepartmentModel getDepartment() {
        return department;
    }

    public boolean wasThere(LocalDate date){
        if(getClocks() == null)
            return false;

        for(ClockDayModel clock : getClocks()){
            if(clock.getClockIn() == null)
                continue;

            if(clock.getClockIn().getClock().toLocalDate().equals(date)) {
                working.setFill(Color.GREEN);
                return true;
            }
        }

        return false;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRfid() {
        return rfid;
    }

    public char getWorkShift() {
        return workShift;
    }

    public List<ClockDayModel> getClocks() {
        return clocks;
    }

    public static enum ImageType {
        MINI,
        NORMAL
    }
}
