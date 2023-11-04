package com.fatec.rfidscanwave.db;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.model.*;
import javafx.scene.image.Image;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                            "FROM employees" + "\n" +
                            "INNER JOIN shifts ON employees.shift_id = shifts.id"
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
                employee.setShift(
                        new ShiftModel(
                                resultSet.getInt("id"),
                                LocalTime.parse(resultSet.getTime("clock_in").toString()),
                                LocalTime.parse(resultSet.getTime("clock_out").toString()),
                                LocalTime.parse(resultSet.getTime("break_duration").toString())
                        )
                );
                employee.setBirthDate(resultSet.getTimestamp("birth_date").toLocalDateTime().toLocalDate());
                employee.setHireDate(resultSet.getTimestamp("hire_date").toLocalDateTime().toLocalDate());
                employee.setImage(getImageById(employee.getId(), true), EmployeeModel.ImageType.MINI);
                employee.setClocks(getClockListById(employee, false));

                for(ClockDayModel c : employee.getClocks()){
                    c.setShift(employee.getShift());
                }

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
    public List<EmployeeModel> getSimpleEmployeesByManagerDepartment(List<JobModel> jobList) {
        ResultSet resultSet = null;
        Statement statement = null;
        List<EmployeeModel> employeeList = new ArrayList<>();

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT *" + "\n" +
                            "FROM employees" + "\n" +
                            "INNER JOIN shifts ON employees.shift_id = shifts.id"
            );

            if (!resultSet.next())
                return employeeList;

            do {
                int jId = resultSet.getInt("job_id");
                if (jId != 0) {
                    for (JobModel job : jobList) {
                        if (job.getId() == jId) {
                            jId = job.getDepartment().getId();
                            break;
                        }
                    }
                }

                if(jId == ManagerModel.getInstance().getDepartment()){
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
                    employee.setShift(
                            new ShiftModel(
                                    resultSet.getInt("id"),
                                    LocalTime.parse(resultSet.getTime("clock_in").toString()),
                                    LocalTime.parse(resultSet.getTime("clock_out").toString()),
                                    LocalTime.parse(resultSet.getTime("break_duration").toString())
                            )
                    );
                    employee.setBirthDate(resultSet.getTimestamp("birth_date").toLocalDateTime().toLocalDate());
                    employee.setHireDate(resultSet.getTimestamp("hire_date").toLocalDateTime().toLocalDate());
                    employee.setImage(getImageById(employee.getId(), true), EmployeeModel.ImageType.MINI);
                    employee.setClocks(getClockListById(employee, false));

                    for(ClockDayModel c : employee.getClocks()){
                        c.setShift(employee.getShift());
                    }

                    employeeList.add(employee);
                }
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
    public List<ClockDayModel> getClockListById(EmployeeModel employee, boolean haveShift){
        ResultSet resultSet = null;
        Statement statement = null;
        List<ClockDayModel> clockList = new ArrayList<>();

        try {
            statement = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(
                    "SELECT * FROM clock WHERE employee_id=" + employee.getId() + " ORDER BY clock_date DESC, clock_state DESC;"
            );

            if (!resultSet.next()) {
                ClockDayModel clock = new ClockDayModel(employee.getShift(), 2, LocalDate.now());
                clockList.add(clock);
                return clockList;
            }

            ClockDayModel clockDay = new ClockDayModel();

            do {
                int state = resultSet.getInt("clock_state");

                if(clockDay.canSetClock(state)){
                    if(resultSet.getTime("clock_time") != null)
                        clockDay.setClock(resultSet.getTimestamp("clock_date"), resultSet.getTimestamp("clock_time"), state);
                } else {
                    resultSet.previous();
                    clockList.add(clockDay);
                    clockDay = new ClockDayModel();
                }
            } while(resultSet.next());

            clockList.add(clockDay);

            if(haveShift){
                for(ClockDayModel c : clockList){
                    c.setShift(employee.getShift());
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
    public void clock(EmployeeModel employee, ClockDayModel clockDay){
        PreparedStatement insertClock = null;

        try {
            String sqlDate = clockDay.getClockIn().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            insertClock = db.prepareStatement(
                    "INSERT INTO clock(employee_id, employee_shift, clock_date, clock_time, clock_state)" + "\n" +
                            "VALUES ( ?, ?, ?, ?, ?), " +
                            "( ?, ?, ?, ?, ?), " +
                            "( ?, ?, ?, ?, ?), " +
                            "( ?, ?, ?, ?, ?); "
            );
            insertClock.setInt(1, employee.getId());
            insertClock.setInt(6, employee.getId());
            insertClock.setInt(11, employee.getId());
            insertClock.setInt(16, employee.getId());

            insertClock.setInt(2, employee.getShift().getId());
            insertClock.setInt(7, employee.getShift().getId());
            insertClock.setInt(12, employee.getShift().getId());
            insertClock.setInt(17, employee.getShift().getId());

            insertClock.setString(3, sqlDate);
            insertClock.setString(8, sqlDate);
            insertClock.setString(13, sqlDate);
            insertClock.setString(18, sqlDate);

            insertClock.setString(4, null);
            insertClock.setString(9, null);
            insertClock.setString(14, null);
            insertClock.setString(19, null);

            insertClock.setInt(5, 1);
            insertClock.setInt(10, 2);
            insertClock.setInt(15, 3);
            insertClock.setInt(20, 4);

            insertClock.execute();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateClock(int employeeId, EmployeeModel employee, ClockDayModel clockDay) {
        PreparedStatement updateClock = null;

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            if(clockDay.getOffDuty() == 0){
                clock(employee, clockDay);
            }

            String date = "'" + clockDay.getClockIn().getDate().format(dateFormatter) + "'";

            String inQuery = clockDay.getClockIn() == null ? "NULL" : "'" + clockDay.getClockIn().getDateTime().format(timeFormatter) + "'";
            String lunchOutQuery = clockDay.getLunchOut() == null ? "NULL" : "'" + clockDay.getLunchOut().getDateTime().format(timeFormatter) + "'";
            String lunchReturnQuery = clockDay.getLunchReturn() == null ? "NULL" : "'" + clockDay.getLunchReturn().getDateTime().format(timeFormatter) + "'";
            String outQuery = clockDay.getClockOut() == null ? "NULL" : "'" + clockDay.getClockOut().getDateTime().format(timeFormatter) + "'";

            String inUpdate = "UPDATE clock SET clock_time = " + inQuery + " WHERE employee_id = " + employeeId +
                    " AND clock_date = " + date + " AND clock_state = 1;";
            String lunchOut = "UPDATE clock SET clock_time = " + lunchOutQuery + " WHERE employee_id = " + employeeId +
                    " AND clock_date = " + date + " AND clock_state = 2;";
            String lunchReturn = "UPDATE clock SET clock_time = " + lunchReturnQuery + " WHERE employee_id = " + employeeId +
                    " AND clock_date = " + date + " AND clock_state = 3;";
            String out = "UPDATE clock SET clock_time = " + outQuery + " WHERE employee_id = " + employeeId +
                    " AND clock_date = " + date + " AND clock_state = 4;";

            updateClock = db.prepareStatement(inUpdate);
            updateClock.execute();

            updateClock = db.prepareStatement(lunchOut);
            updateClock.execute();

            updateClock = db.prepareStatement(lunchReturn);
            updateClock.execute();

            updateClock = db.prepareStatement(out);
            updateClock.execute();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int employeeId, ClockDayModel oldClockDay){
        PreparedStatement updateClock = null;

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String date = "'" + oldClockDay.getClockIn().getDate().format(dateFormatter) + "'";

            updateClock = db.prepareStatement(
                    "DELETE FROM clock WHERE employee_id = " + employeeId + " AND \n" +
                            "clock_date = " + date
                    );
            updateClock.execute();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void generateLog(int employeeId, ClockDayModel oldClockDay, int managerId, ClockDayModel clockDay) {
        PreparedStatement logClock = null;

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            String date = clockDay.getClockIn().getDate().format(dateFormatter);

            String inUpdate = "INSERT INTO logs (manager_id, employee_id, clock_date, from_clock_time, " +
                    "to_clock_time, clock_state)" +
                    "VALUES " +
                    "(" + managerId + ", " + employeeId + ", '" + date + "', ?, ?, 1);";

            String lunchOutUpdate = "INSERT INTO logs (manager_id, employee_id, clock_date, from_clock_time, " +
                    "to_clock_time, clock_state)" +
                    "VALUES " +
                    "(" + managerId + ", " + employeeId + ", '" + date + "', ?, ?, 2);";

            String lunchReturnUpdate = "INSERT INTO logs (manager_id, employee_id, clock_date, from_clock_time, " +
                    "to_clock_time, clock_state)" +
                    "VALUES " +
                    "(" + managerId + ", " + employeeId + ", '" + date + "', ?, ?, 3);";

            String outUpdate = "INSERT INTO logs (manager_id, employee_id, clock_date, from_clock_time, " +
                    "to_clock_time, clock_state)" +
                    "VALUES " +
                    "(" + managerId + ", " + employeeId + ", '" + date + "', ?, ?, 4);";

            logClock = db.prepareStatement(inUpdate);
            logClock.setString(1, (oldClockDay.getClockIn() == null || oldClockDay.getClockIn().getTime() == null)  ? null : oldClockDay.getClockIn().getDateTime().format(timeFormatter));
            logClock.setString(2, (clockDay.getClockIn() == null || clockDay.getClockIn().getTime() == null) ? null : clockDay.getClockIn().getDateTime().format(timeFormatter));
            logClock.execute();

            logClock = db.prepareStatement(lunchOutUpdate);
            logClock.setString(1, (oldClockDay.getLunchOut() == null || oldClockDay.getLunchOut().getTime() == null)  ? null : oldClockDay.getLunchOut().getDateTime().format(timeFormatter));
            logClock.setString(2, (clockDay.getLunchOut() == null || clockDay.getLunchOut().getTime() == null)  ? null : clockDay.getLunchOut().getDateTime().format(timeFormatter));
            logClock.execute();

            logClock = db.prepareStatement(lunchReturnUpdate);
            logClock.setString(1, (oldClockDay.getLunchReturn() == null || oldClockDay.getLunchReturn().getTime() == null)  ? null : oldClockDay.getLunchReturn().getDateTime().format(timeFormatter));
            logClock.setString(2, (clockDay.getLunchReturn() == null || clockDay.getLunchReturn().getTime() == null)  ? null : clockDay.getLunchReturn().getDateTime().format(timeFormatter));
            logClock.execute();

            logClock = db.prepareStatement(outUpdate);
            logClock.setString(1, (oldClockDay.getClockOut() == null || oldClockDay.getClockOut().getTime() == null)  ? null : oldClockDay.getClockOut().getDateTime().format(timeFormatter));
            logClock.setString(2, (clockDay.getClockOut() == null || clockDay.getClockOut().getTime() == null)  ? null : clockDay.getClockOut().getDateTime().format(timeFormatter));
            logClock.execute();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean login(String user, String password) {
        ResultSet resultSet = null;
        Statement statement = null;
        List<ClockDayModel> clockList = new ArrayList<>();

        try {
            statement = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(
                    "SELECT m.manager_id, m.login, m.password, j.department_id " +
                            "FROM manager AS m " +
                            "INNER JOIN employees AS e ON e.id = m.manager_id " +
                            "INNER JOIN jobs AS j ON j.id = e.job_id " +
                            "WHERE login = '" + user + "' AND password = '" + password + "';"
            );

            if (!resultSet.next()) {
                return false;
            }

            ManagerModel.getInstance().setId(resultSet.getInt("manager_id"));
            ManagerModel.getInstance().setUser(resultSet.getString("login"));
            ManagerModel.getInstance().setPassword(resultSet.getString("password"));
            ManagerModel.getInstance().setDepartment(resultSet.getInt("department_id"));

            resultSet.close();
            statement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
