package com.fatec.rfidscanwave.db;

import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.model.DepartmentModel;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.model.JobModel;
import javafx.scene.image.Image;

import java.util.List;

public interface IScanWaveDB {
    List<DepartmentModel> getDepartments();
    List<JobModel> getJobs(List<DepartmentModel> departmentList);
    List<EmployeeModel> getSimpleEmployees(List<JobModel> jobList);
    List<EmployeeModel> getEmployees(List<JobModel> jobList);
    List<ClockDayModel> getClockListById(int employeeId);
    ClockDayModel getClockDayByEmployeeId(int employeeId);
    ClockDayModel getLastClockDayByEmployeeId(int employeeId);
    Image getImageById(int id, boolean isThumbnail);
}
