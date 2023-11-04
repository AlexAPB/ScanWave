package com.fatec.rfidscanwave.view;

import com.fatec.rfidscanwave.ScanWave;
import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.model.ClockDayModel;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.ui.date.MonthYearSelector;
import com.fatec.rfidscanwave.ui.graphics.GraphicContent;
import com.fatec.rfidscanwave.ui.graphics.HalfCircleGraph;
import com.fatec.rfidscanwave.ui.input.*;
import com.fatec.rfidscanwave.ui.table.ClockRow;
import com.fatec.rfidscanwave.util.FXUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fatec.rfidscanwave.util.FXUtil.getDivider;
import static com.fatec.rfidscanwave.util.FXUtil.getSpacer;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class BoardView {
    private EmployeeModel employee;
    private final ScanWaveView parent;
    private final ScanWaveDB db;
    private final ScrollPane mainScreen;
    private final VBox screen;
    private VBox topBoard;
    private static Image defaultImage;

    //Header
    private Button returnButton;
    private ImageView image;
    private Circle userImage;
    private LabelTitle nameLabel;
    private LabelTitle jobLabel;
    private LabelTitle idLabel;
    private LabelTitle departmentLabel;
    private Label workShiftLabel;
    private Label workingLabel;

    //Board
    private LabelTitle totalWorked;
    private LabelTitle overtimeLabel;
    private LabelTitle needWork;

    //Table
    private TableView<ClockRow> clockTable;

    private MonthYearSelector datePicker;
    private HalfCircleGraph lateGraph;
    private HalfCircleGraph presenceGraphic;
    private List<ClockDayModel> clockDayList;
    private ObservableList<ClockRow> clockRowList;
    private SortedList<ClockRow> sortedClockList;
    private FilteredList<ClockRow> filteredList;

    private Editor editor;

    public BoardView(ScanWaveView parent, ScanWaveDB db){
        this.parent = parent;
        this.db = db;

        mainScreen = new ScrollPane();
        mainScreen.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScreen.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScreen.setFitToWidth(true);
        //mainScreen.setFitToHeight(true);
        AnchorPane.setLeftAnchor(mainScreen, 0D);
        AnchorPane.setTopAnchor(mainScreen, 0D);
        AnchorPane.setRightAnchor(mainScreen, 0D);

        screen = new VBox();
        screen.setFillWidth(true);
        screen.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        clockTable = new TableView<>();

        screen.getChildren().add(create());

        this.mainScreen.setVisible(false);
        this.mainScreen.setDisable(true);
        this.mainScreen.setContent(screen);

        parent.getWaveScreen().getChildren().add(mainScreen);
    }

    public VBox create(){
        VBox container = new VBox();
        container.setPadding(new Insets(0, 10, 0, 10));
        container.setFillWidth(true);
        VBox.setVgrow(container, Priority.ALWAYS);

        createTop(container);
        container.getChildren().add(getSpacer(1, 10));
        createMid(container);
        container.getChildren().add(getSpacer(1, 10));
        createBottom(container);

        return container;
    }

    private void createTop(VBox container){
        topBoard = new VBox();
        topBoard.setId("board-user-container");
        topBoard.setPadding(new Insets(10, 10, 0, 10));

        HBox userBoard = new HBox();

        returnButton = new Button();

        if(defaultImage == null){
             defaultImage = new Image(ScanWave.class.getResource("/images/user.png").toString());
        }

        userImage = new Circle(45);
        userImage.setFill(new ImagePattern(defaultImage));
        userImage.setSmooth(true);

        nameLabel = new LabelTitle("Name");
        nameLabel.getStyleClass().add("semibold-title");

        departmentLabel = new LabelTitle("Department");
        departmentLabel.getStyleClass().add("normal-title");
        departmentLabel.getStyleClass().add("opacity-90");

        jobLabel = new LabelTitle("Job");
        jobLabel.getStyleClass().add("italic-title");
        jobLabel.getStyleClass().add("opacity-80");

        idLabel = new LabelTitle("Id: -");
        idLabel.getStyleClass().add("mini-semibold-title");
        idLabel.getStyleClass().add("opacity-50");

        workShiftLabel = new Label("Turno Atual: -");
        workShiftLabel.getStyleClass().add("header");
        workShiftLabel.setStyle("-fx-font-size: 2em; -fx-opacity: 0.8;");
        workShiftLabel.setPadding(new Insets(0, 0, 10, 0));

        workingLabel = new Label("está trabalhando!");
        workingLabel.getStyleClass().add("header");
        workingLabel.setStyle("-fx-font-size: 1.3em; -fx-opacity: 0.6;");

        VBox userInfoVBox = new VBox();
        userInfoVBox.setAlignment(Pos.CENTER_LEFT);
        userInfoVBox.setPadding(new Insets(0, 0, 0, 10));
        userInfoVBox.getChildren().add(nameLabel);
        userInfoVBox.getChildren().add(departmentLabel);
        userInfoVBox.getChildren().add(jobLabel);
        userInfoVBox.getChildren().add(idLabel);

        HBox userInfoHBox = new HBox();
        HBox.setHgrow(userInfoHBox, Priority.ALWAYS);
        userInfoHBox.getChildren().add(userImage);
        userInfoHBox.getChildren().add(userInfoVBox);

        VBox workInfoVBox = new VBox();
        workInfoVBox.setAlignment(Pos.CENTER_RIGHT);
        workInfoVBox.getChildren().add(workShiftLabel);
        workInfoVBox.getChildren().add(workingLabel);

        HBox workInfoHBox = new HBox();
        workInfoHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(workInfoHBox, Priority.ALWAYS);
        workInfoHBox.getChildren().add(workInfoVBox);

        userBoard.getChildren().add(userInfoHBox);
        userBoard.getChildren().add(workInfoHBox);
        topBoard.getChildren().add(userBoard);

        topBoard.getChildren().add(getSpacer(1, 7));
        topBoard.getChildren().add(getDivider(false, Color.BLACK, 0.5f, Insets.EMPTY));
        topBoard.getChildren().add(getSpacer(1, 7));
    }

    private void createMid(VBox container){
        HBox mainBox = new HBox();

        HBox backContainer = new HBox();
        backContainer.setAlignment(Pos.BOTTOM_CENTER);
        backContainer.setFillHeight(false);
        backContainer.setPadding(new Insets(0, 0, 10, 0));

        BackButton back = new BackButton();
        back.pressedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                parent.loadBackEmployees();
            }
        });

        backContainer.getChildren().add(back);

        HBox infoBox = new HBox();
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setAlignment(Pos.CENTER);

        infoBox.getChildren().add(expandingHPane());
        infoBox.getChildren().add(presenceGraphic());
        addLine(true, infoBox, 50, 0.25f, new Insets(0, 0, 0, 0));
        infoBox.getChildren().add(lateGraphic());
        addLine(true, infoBox, 50, 0.25f, new Insets(0, 0, 0, 0));
        infoBox.getChildren().add(annualLeave());
        addLine(true, infoBox, 50, 0.25f, new Insets(0, 0, 0, 0));
        infoBox.getChildren().add(overTime());
        infoBox.getChildren().add(expandingHPane());

        mainBox.getChildren().add(backContainer);
        mainBox.getChildren().add(infoBox);

        topBoard.getChildren().add(mainBox);

        container.getChildren().add(topBoard);
    }

    private void createBottom(VBox container){
        VBox tableBox = new VBox();
        tableBox.setPadding(new Insets(10));
        tableBox.setId("board-user-container");

        tableBox.getChildren().add(filters());
        tableBox.getChildren().add(FXUtil.getSpacer(1, 10));
        tableBox.getChildren().add(clockTable());

        container.getChildren().add(tableBox);
    }

    private HBox filters(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        datePicker = new MonthYearSelector();
        datePicker.setId("ios-date");

        editor = new Editor(this, db, clockTable, new LabelTitle());

        hBox.getChildren().add(datePicker);
        hBox.getChildren().add(expandingHPane());
        hBox.getChildren().add(editor.getWarnings());
        hBox.getChildren().add(expandingHPane());
        hBox.getChildren().add(editor);

        return hBox;
    }

    private TableView<ClockRow> clockTable(){
        clockTable.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        TableColumn<ClockRow, EditableCheckBox> statusColumn = new TableColumn("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
        statusColumn.setId("center-column");
        statusColumn.setResizable(true);

        TableColumn<ClockRow, String> dayColumn = new TableColumn("Data");
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));

        TableColumn<ClockRow, EditableText> clock1Column = new TableColumn("Ent1 → Sai1");
        clock1Column.setCellValueFactory(new PropertyValueFactory<>("clock1"));

        TableColumn<ClockRow, EditableText> clock2Column = new TableColumn("Ent2 → Sai2");
        clock2Column.setCellValueFactory(new PropertyValueFactory<>("clock2"));

        TableColumn<ClockRow, String> lateColumn = new TableColumn("Atraso");
        lateColumn.setCellValueFactory(new PropertyValueFactory<>("late"));

        TableColumn<ClockRow, String> overtimeColumn = new TableColumn("Horas extras");
        overtimeColumn.setCellValueFactory(new PropertyValueFactory<>("overtime"));

        clockTable.setRowFactory((item) -> {
            TableRow<ClockRow> row = new TableRow<>() {
                @Override
                public void updateItem(ClockRow clock, boolean empty) {
                    super.updateItem(clock, empty);
                }
            };

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    editor.setSelected(row.getItem());
                }
            });
            return row;
        });

        clockTable.getColumns().add(statusColumn);
        clockTable.getColumns().add(dayColumn);
        clockTable.getColumns().add(clock1Column);
        clockTable.getColumns().add(clock2Column);
        clockTable.getColumns().add(lateColumn);
        clockTable.getColumns().add(overtimeColumn);

        return clockTable;
    }

    private Pane expandingHPane(){
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        return pane;
    }

    private void addLine(boolean vertical, Pane pane, int padding, float opacity, Insets insets){
        int width, height;

        if(vertical){
            width = padding;
            height = 1;
        } else {
            height = padding;
            width = 1;
        }

        pane.getChildren().add(getSpacer(width, height));
        pane.getChildren().add(getDivider(true, Color.BLACK, opacity, insets));
        pane.getChildren().add(getSpacer(width, height));

    }

    public VBox lateGraphic(){
        VBox lateBox = new VBox();
        lateBox.setAlignment(Pos.TOP_CENTER);

        Text lateText = new LabelTitle("Pontualidade");
        lateText.setStyle("-fx-font-size: 1.2em; -fx-opacity: 0.6;");

        lateGraph = new HalfCircleGraph();
        lateGraph.setPadding(new Insets(10, 0, 10, 0));

        lateBox.getChildren().add(lateText);
        lateBox.getChildren().add(lateGraph);

        return lateBox;
    }

    public void initLateGraphic(){
        int lates = 0;
        int onTime = 0;

        for(ClockDayModel c : clockDayList){
            if(c.getClockIn().getTime() == null)
                continue;

            LocalDateTime date = LocalDateTime.of(c.getClockIn().getDate(), c.getShift().getClockInTime());
            Duration duration = Duration.between(date, c.getClockIn().getDateTime());

            if(c.getOffDuty() != 0)
                continue;

            if(duration.toSeconds() < 300) {
                onTime++;
            } else {
                lates++;
            }
        }

        lateGraph.unload();

        if(!(onTime == 0 && lates == 0)) {
            lateGraph.load(
                    Color.WHITE,
                    50,
                    new GraphicContent(lates, "Atrasos - " + lates, Color.rgb(166, 171, 220)),
                    new GraphicContent(onTime, "Na hora - " + onTime, Color.rgb(109, 110, 190))
            );
        }
    }

    private void initPresenceGraphic(){
        int missedWork = 0;
        int dayWorked = 0;

        for(ClockDayModel c : clockDayList){
            if(c.getOffDuty() == 0)
                dayWorked++;
            else if(c.getOffDuty() == 1)
                missedWork++;
        }

        presenceGraphic.unload();

        if(!(dayWorked == 0 && missedWork == 0)) {
            presenceGraphic.load(
                    Color.WHITE,
                    50,
                    new GraphicContent(dayWorked, "Presenças - " + dayWorked, Color.DARKGREEN),
                    new GraphicContent(missedWork, "Faltas - " + missedWork, Color.DARKRED)
            );
        }
    }

    private VBox presenceGraphic(){
        VBox presenceBox = new VBox();
        presenceBox.setAlignment(Pos.TOP_CENTER);

        Text presenceText = new LabelTitle("Presença");
        presenceText.setStyle("-fx-font-size: 1.2em; -fx-opacity: 0.6;");

        presenceGraphic = new HalfCircleGraph();
        presenceGraphic.setPadding(new Insets(10, 0, 10, 0));

        presenceBox.getChildren().add(presenceText);
        presenceBox.getChildren().add(presenceGraphic);

        return presenceBox;
    }

    private VBox annualLeave(){
        VBox annualLeaveBox = new VBox();
        annualLeaveBox.setAlignment(Pos.TOP_CENTER);

        LabelTitle worked = new LabelTitle("Total trabalhado");
        worked.setStyle("-fx-font-size: 1.2em; -fx-opacity: 0.6;");

        totalWorked = new LabelTitle("342h");
        totalWorked.setStyle("-fx-font-size: 2.3em; -fx-opacity: 0.6;");

        needWork = new LabelTitle("/350h");
        needWork.setStyle("-fx-font-size: 2.3em;");

        VBox annualLeaveTextBox = new VBox();
        VBox.setVgrow(annualLeaveTextBox, Priority.ALWAYS);
        annualLeaveTextBox.setAlignment(Pos.CENTER);

        HBox timeWorkedBox = new HBox();
        timeWorkedBox.getChildren().add(totalWorked);
        timeWorkedBox.getChildren().add(needWork);

        annualLeaveTextBox.getChildren().add(timeWorkedBox);

        annualLeaveBox.getChildren().add(worked);
        annualLeaveBox.getChildren().add(annualLeaveTextBox);

        return annualLeaveBox;
    }

    private VBox overTime(){
        VBox overTimeBox = new VBox();
        overTimeBox.setAlignment(Pos.TOP_CENTER);

        Text label = new LabelTitle("Horas extras");
        label.setStyle("-fx-font-size: 1.2em; -fx-opacity: 0.6;");

        VBox overTimeTextBox = new VBox();
        VBox.setVgrow(overTimeTextBox, Priority.ALWAYS);
        overTimeTextBox.setAlignment(Pos.CENTER);

        overtimeLabel = new LabelTitle("12h");
        overtimeLabel.setStyle("-fx-font-size: 2.3em;");
        overTimeTextBox.getChildren().add(overtimeLabel);

        overTimeBox.getChildren().add(label);
        overTimeBox.getChildren().add(overTimeTextBox);

        return overTimeBox;
    }

    public void loadTimeWorkedAndOvertime(){
        int totalTime = 0;
        int workedTime = 0;
        int overtime = 0;

        for(ClockDayModel c : clockDayList){
            if(c.getClockIn() == null)
                continue;

            if(c.getOffDuty() == 0 || c.getOffDuty() == 1) {
                totalTime += Math.round(Duration.between(c.getShift().getClockInTime(), c.getShift().getClockOutTime())
                        .minusSeconds(c.getShift().getBreakDuration().toSecondOfDay()).toMinutes() / 60f);
            }

            if(c.getOffDuty() == 0 && c.getClockOut() != null) {
                long time = Duration.between(
                        LocalDateTime.of(c.getClockIn().getDate(), c.getClockIn().getTime()),
                        LocalDateTime.of(c.getClockOut().getDate(), c.getClockOut().getTime())
                ).minusSeconds(c.getShift().getBreakDuration().toSecondOfDay()).toMinutes();

                workedTime += Math.round(time / 60f);
            }

            if(c.getOffDuty() == 0){
                LocalDateTime dateIn = LocalDateTime.of(c.getClockIn().getDate(), c.getShift().getClockInTime());
                Duration durationIn = Duration.between(c.getClockIn().getDateTime(), dateIn);

                LocalDateTime dateOut = LocalDateTime.of(c.getClockOut().getDate(), c.getShift().getClockOutTime());
                Duration durationOut = Duration.between(dateOut, c.getClockOut().getDateTime());

                if(((durationIn.toMinutes() + durationOut.toMinutes()) / 60f) > 0)
                    overtime += Math.round((durationIn.toMinutes() + durationOut.toMinutes()) / 60f);
            }
        }

        totalWorked.setText(String.valueOf(workedTime));
        needWork.setText("/" + totalTime + "h");
        overtimeLabel.setText(overtime + "h");
    }

    public static int iaa = 0;
    private void loadTable(){
        final List<TableColumn<ClockRow, ?>> columns = clockTable.getColumns();

        AtomicBoolean changed = new AtomicBoolean(true);
        filteredList = new FilteredList<>(clockRowList);

        filteredList.predicateProperty().bind(
                Bindings.createObjectBinding(() -> {
            if(!changed.get())
                return null;

            changed.set(false);

            LocalDate date = datePicker.getCurrent();

            final String finalDate = date.format(DateTimeFormatter.ofPattern("/MM/YYYY"));

            return o -> {
                for(TableColumn<ClockRow, ?> col : columns){
                    ObservableValue<?> observable = col.getCellObservableValue(o);

                    if(!col.getText().equals("Data"))
                        continue;

                    if(observable != null){
                        Object value = observable.getValue();

                        if(value != null && value.toString().contains(finalDate)) {
                            return true;
                        }
                    }
                }
                return false;
            };

        },
                        datePicker.getNext().pressedProperty(),
                        datePicker.getPrevious().pressedProperty())
        );

        if(sortedClockList == null) {
            datePicker.getPrevious().pressedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1) {
                        if (datePicker.previous()) {
                            changed.set(true);
                            clockDayList.clear();
                            employee.setClocks(db.getClockListById(employee, true));
                            clockDayList.addAll(FXCollections.observableArrayList(ClockDayModel.getMonthlyClock(employee, datePicker.getCurrent())));

                            List<ClockRow> clockRows = new ArrayList<>();

                            for (ClockDayModel c : clockDayList) {
                                clockRows.add(new ClockRow(c));
                            }

                            clockRowList.clear();
                            clockRowList.addAll(FXCollections.observableList(clockRows));

                            FilteredList<ClockRow> newFiltered = new FilteredList<>(clockRowList);
                            newFiltered.predicateProperty().set(filteredList.getPredicate());

                            sortedClockList = new SortedList<>(newFiltered);

                            loadTimeWorkedAndOvertime();
                            initLateGraphic();
                            initPresenceGraphic();
                        }

                    }
                }
            });

            datePicker.getNext().pressedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1) {
                        if (datePicker.next()) {
                            changed.set(true);
                            clockDayList.clear();
                            employee.setClocks(db.getClockListById(employee, true));
                            clockDayList.addAll(FXCollections.observableArrayList(ClockDayModel.getMonthlyClock(employee, datePicker.getCurrent())));

                            List<ClockRow> clockRows = new ArrayList<>();

                            for (ClockDayModel c : clockDayList) {
                                clockRows.add(new ClockRow(c));
                            }

                            clockRowList.clear();
                            clockRowList.addAll(FXCollections.observableList(clockRows));

                            FilteredList<ClockRow> newFiltered = new FilteredList<>(clockRowList);
                            newFiltered.predicateProperty().set(filteredList.getPredicate());

                            sortedClockList = new SortedList<>(newFiltered);

                            loadTimeWorkedAndOvertime();
                            initLateGraphic();
                            initPresenceGraphic();
                        }

                    }
                }
            });
        }

        sortedClockList = new SortedList<>(filteredList);
        sortedClockList.comparatorProperty().bind(clockTable.comparatorProperty());
    }

    public void updateView(EmployeeModel employee){
        this.employee = employee;

        editor.setEmployee(employee);
        clockDayList = FXCollections.observableArrayList(ClockDayModel.getMonthlyClock(employee, datePicker.getCurrent()));

        List<ClockRow> clockRows = new ArrayList<>();

        for(ClockDayModel c : clockDayList){
            clockRows.add(new ClockRow(c));
        }

        clockRowList = FXCollections.observableList(clockRows);

        Image img = db.getImageById(employee.getId(), false);
        userImage.setFill(new ImagePattern(img));

        nameLabel.setText(employee.getName() + ", " + employee.getAge());
        departmentLabel.setText(employee.getDepartment().getDepartmentName());
        jobLabel.setText(employee.getJob().getJobName());
        idLabel.setText("ID: " + employee.getId());

        workShiftLabel.setText(employee.getShift().getClockInTime() + " - " + employee.getShift().getClockOutTime());

        if(employee.isWorking())
            workingLabel.setText("Em serviço");
        else
            workingLabel.setText("Fora de serviço");

        datePicker.setMin(employee.getHireDate());

        loadTimeWorkedAndOvertime();

        initLateGraphic();

        initPresenceGraphic();

        loadTable();

        clockTable.setItems(sortedClockList);
    }

    public ScanWaveView getParent() {
        return parent;
    }

    public ScanWaveDB getDb() {
        return db;
    }

    public ScrollPane getScreen() {
        return mainScreen;
    }
}
