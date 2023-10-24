package com.fatec.rfidscanwave.db;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.model.DepartmentModel;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.model.JobModel;
import javafx.scene.image.Image;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ScanWaveDB implements IScanWaveDB {
    private Connection db;

    public ScanWaveDB(){
        db = DB.getConnection();
    }

    @Override
    public List<EmployeeModel> getSimpleEmployees(List<JobModel> jobList) {
        ResultSet resultSet = null;
        Statement statement = null;
        List<EmployeeModel> employeeList = new ArrayList<>();

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT *" + "\n" +
                            "FROM employees"
            );

            if (!resultSet.next())
                return employeeList;

            do {
                EmployeeModel employee = new EmployeeModel();

                int jobId = resultSet.getInt("job_id");
                if (jobId != 0) {
                    for (JobModel job : jobList) {
                        if (job.getId() == jobId) {
                            employee.setJob(job);
                            break;
                        }
                    }
                }

                employee.setId(resultSet.getInt("id"));
                employee.setName(resultSet.getString("name"));
                employee.setWorkdayDuration(resultSet.getInt("workday_duration"));
                employee.setWorkShift(resultSet.getString("work_shift").charAt(0));
                employee.setImage(getImageById(employee.getId(), true), EmployeeModel.ImageType.MINI);
                employee.setClocks(getClockListById(employee.getId()));

                employeeList.add(employee);
            } while (resultSet.next());

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employeeList;
    }

    @Override
    public List<ClockDayModel> getClockListById(int employeeId){
        ResultSet resultSet = null;
        Statement statement = null;
        List<ClockDayModel> clockList = new ArrayList<>();

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM clock WHERE id=" + employeeId + " ORDER BY clock_time DESC;"
            );

            if (!resultSet.next()) {
                clockList.add(new ClockDayModel());
                return clockList;
            }

            ClockDayModel clockDay = new ClockDayModel();

            if(resultSet.getInt("clock_state") == ClockDayModel.ClockState.CLOCK_IN.getState()){
                clockDay.setClockIn(new ClockDayModel.Clock(resultSet.getTimestamp("clock_time"), ClockDayModel.ClockState.CLOCK_IN));
            } else {
                clockDay.setClockIn(new ClockDayModel.Clock(resultSet.getTimestamp("clock_time"), ClockDayModel.ClockState.CLOCK_IN));
                resultSet.next();
                clockDay.setClockOut(new ClockDayModel.Clock(resultSet.getTimestamp("clock_time"), ClockDayModel.ClockState.CLOCK_OUT));
            }
            clockList.add(clockDay);

            clockDay = null;

            while (resultSet.next()) {
                if(clockDay == null){
                    clockDay = new ClockDayModel();
                }

                if(resultSet.getInt("clock_state") == ClockDayModel.ClockState.CLOCK_IN.getState()){
                    clockDay.setClockIn(new ClockDayModel.Clock(resultSet.getTimestamp("clock_time"), ClockDayModel.ClockState.CLOCK_IN));
                } else {
                    clockDay.setClockOut(new ClockDayModel.Clock(resultSet.getTimestamp("clock_time"), ClockDayModel.ClockState.CLOCK_OUT));
                }

                if(clockDay.getClockIn() != null && clockDay.getClockOut() != null){
                    clockList.add(clockDay);
                    clockDay = null;
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clockList;
    }

    @Override
    public List<DepartmentModel> getDepartments() {
        ResultSet resultSet = null;
        Statement statement = null;
        List<DepartmentModel> departmentList = new ArrayList<>();

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM department;"
            );

            if(!resultSet.next())
                return departmentList;

            do {
                DepartmentModel department = new DepartmentModel();
                department.setId(resultSet.getInt("id"));
                department.setDepartmentName(resultSet.getString("department_name"));
                departmentList.add(department);
            } while(resultSet.next());

            departmentList.add(new DepartmentModel(0, "-"));

            resultSet.close();
            statement.close();
        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }

        return departmentList;
    }

    @Override
    public List<JobModel> getJobs(List<DepartmentModel> departmentList) {
        ResultSet resultSet = null;
        Statement statement = null;
        List<JobModel> jobList = new ArrayList<>();

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM jobs;"
            );

            if (!resultSet.next())
                return jobList;

            do {
                JobModel job = new JobModel();
                job.setId(resultSet.getInt("id"));

                int departmentId = resultSet.getInt("department_id");
                for (DepartmentModel department : departmentList) {
                    if (department.getId() == departmentId) {
                        job.setDepartment(department);
                        break;
                    }
                }

                job.setJobName(resultSet.getString("job_name"));
                jobList.add(job);
            } while (resultSet.next());

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobList;
    }

    @Override
    public List<EmployeeModel> getEmployees(List<JobModel> jobList) {
        return null;
    }

    @Override
    public ClockDayModel getClockDayByEmployeeId(int employeeId) {
        return null;
    }

    @Override
    public ClockDayModel getLastClockDayByEmployeeId(int employeeId) {

        return null;
    }

    @Override
    public Image getImageById(int id, boolean isThumbnail) {
        Image image = null;
        String str = isThumbnail ? "-thumbnail" : "";
        try {
            image = new Image(ScanWave.class.getResource("/db/employees/" + id + str + ".jpg").toString());
        } catch (Exception e){
            image = new Image(ScanWave.class.getResource("/images/user.png").toString());
        }

        return image;
    }
}
