package com.fatec.rfidscanwave.db;

import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.model.DepartmentModel;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.model.JobModel;
import javafx.scene.image.Image;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IScanWaveDB {
    void clock(EmployeeModel employee, ClockDayModel clockDay);
    void updateClock(int employeeId, EmployeeModel employee, ClockDayModel clockDay);
    void generateLog(int employeeId, ClockDayModel oldClockDay, int managerId, ClockDayModel clockDay);
    boolean login(String user, String password);
    List<DepartmentModel> getDepartments();
    List<JobModel> getJobs(List<DepartmentModel> departmentList);
    List<EmployeeModel> getSimpleEmployees(List<JobModel> jobList);
    List<EmployeeModel> getSimpleEmployeesByManagerDepartment(List<JobModel> jobList);
    List<EmployeeModel> getEmployees(List<JobModel> jobList);
    List<ClockDayModel> getClockListById(EmployeeModel employee);
    ClockDayModel getClockDayByEmployeeId(int employeeId);
    ClockDayModel getLastClockDayByEmployeeId(int employeeId);
    Image getImageById(int id, boolean isThumbnail);
    void delete(int employeeId, ClockDayModel oldClockDay);
}
