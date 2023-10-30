package com.fatec.rfidscanwave.view;

import com.fatec.rfidscanwave.db.ScanWaveDB;
import com.fatec.rfidscanwave.model.DepartmentModel;
import com.fatec.rfidscanwave.model.EmployeeModel;
import com.fatec.rfidscanwave.model.JobModel;
import com.fatec.rfidscanwave.model.ManagerModel;
import com.fatec.rfidscanwave.ui.input.TextField2;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import jfxtras.styles.jmetro.*;

import java.time.LocalDate;
import java.util.List;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class EmployeesView {
    private final ScanWaveDB db;
    private ScanWaveView parent;
    private VBox screen;
    private VBox employeesPane;
    private HBox overTableBox;
    private Label quantity;
    private TableView<EmployeeModel> table;
    private final List<DepartmentModel> departments;
    private final List<JobModel> jobs;
    private ObservableList<EmployeeModel> simpleEmployees;
    private ObservableList<String> filterModels = FXCollections.observableArrayList("-", "ID", "Nome", "Cargo", "Departamento", "Turno");

    public EmployeesView(ScanWaveView parent, ScanWaveDB db){
        this.parent = parent;
        this.db = db;
        this.departments = db.getDepartments();
        this.jobs = db.getJobs(departments);
        this.simpleEmployees = FXCollections.observableArrayList(db.getSimpleEmployees(jobs));

        createScreen();
        createPanes(screen);
        create();

        screen.setVisible(false);
        screen.setDisable(true);
    }

    public void createScreen(){
        screen = new VBox();
        AnchorPane.setBottomAnchor(screen, 0D);
        AnchorPane.setLeftAnchor(screen, 0D);
        AnchorPane.setTopAnchor(screen, 0D);
        AnchorPane.setRightAnchor(screen, 0D);

        HBox optionGroup = new HBox();
        optionGroup.setMinHeight(50);
        optionGroup.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        Label logoutButton = new Label("Sair");
        logoutButton.setStyle("-fx-font-size: 1.8em; -fx-opacity: 0.75; -fx-padding: 0px 15px;");
        optionGroup.setAlignment(Pos.CENTER_RIGHT);
        logoutButton.pressedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(!t1)
                    return;

                parent.loadLogin();
            }
        });
        optionGroup.getChildren().add(logoutButton);

        JMetro jmetro = new JMetro(optionGroup, Style.DARK);

        screen.getChildren().add(optionGroup);

        parent.getWaveScreen().getChildren().add(screen);
    }

    public void createPanes(VBox nodeParent){
        employeesPane = new VBox();
        employeesPane.setFillWidth(true);
        VBox.setVgrow(employeesPane, Priority.ALWAYS);

        employeesPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        employeesPane.getStyleClass().add("window-background");

        nodeParent.getChildren().add(employeesPane);
    }

    public void create() {
        //Label text = new Label("Funcionários");
        //text.getStyleClass().add("header");
        //text.setStyle("-fx-font-size: 40px");

        overTableBox = new HBox();
        overTableBox.setAlignment(Pos.BOTTOM_LEFT);
        //overTableBox.getChildren().add(text);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(anchorPane, Priority.ALWAYS);

        Pane spacing = new Pane();
        spacing.setPrefWidth(1);
        spacing.setPrefHeight(10);


        quantity = new Label();
        quantity.setPadding(new Insets(10, 0, 0, 0));
        quantity.setOpacity(0.6);

        HBox quantityBox = new HBox();
        quantityBox.setAlignment(Pos.CENTER_RIGHT);
        quantityBox.getChildren().add(quantity);

        loadTable(anchorPane);
        loadFilters();

        employeesPane.getChildren().add(overTableBox);
        employeesPane.getChildren().add(spacing);
        employeesPane.getChildren().add(anchorPane);
        employeesPane.getChildren().add(quantityBox);
    }

    private void loadTable(AnchorPane container){
        table = new TableView<>();
        AnchorPane.setBottomAnchor(table, 0D);
        AnchorPane.setLeftAnchor(table, 0D);
        AnchorPane.setTopAnchor(table, 0D);
        AnchorPane.setRightAnchor(table, 0D);

        TableColumn<EmployeeModel, Circle> imageColumn = new TableColumn();
        imageColumn.setPrefWidth(80);
        imageColumn.setResizable(false);
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("thumbnail"));
        imageColumn.setSortable(false);

        TableColumn<EmployeeModel, Circle> isWorkingColumn = new TableColumn();
        isWorkingColumn.setPrefWidth(80);
        isWorkingColumn.setResizable(false);
        isWorkingColumn.setCellValueFactory(new PropertyValueFactory<>("working"));
        isWorkingColumn.setSortable(false);
        isWorkingColumn.setId("center-right-column");

        TableColumn<EmployeeModel, Integer> idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EmployeeModel, String> nameColumn = new TableColumn("Nome");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<EmployeeModel, String> departmentColumn = new TableColumn("Departamento");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<EmployeeModel, String> jobColumn = new TableColumn("Cargo");
        jobColumn.setCellValueFactory(new PropertyValueFactory<>("job"));

        TableColumn<EmployeeModel, Integer> workShiftColumn = new TableColumn("Turno");
        workShiftColumn.setPrefWidth(100);
        workShiftColumn.setCellValueFactory(new PropertyValueFactory<>("workshift"));
        workShiftColumn.setId("center-column");

        TableColumn<EmployeeModel, String> workDurationColumn = new TableColumn("Jornada");
        workDurationColumn.setPrefWidth(100);
        workDurationColumn.setCellValueFactory(new PropertyValueFactory<>("workdayDuration"));
        workDurationColumn.setId("center-column");
        table.setRowFactory((item) -> {
            TableRow<EmployeeModel> row = new TableRow<>() {
                @Override
                public void updateItem(EmployeeModel employee, boolean empty) {
                    super.updateItem(employee, empty);

                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    EmployeeModel rowData = row.getItem();
                    parent.loadEmployee(rowData);
                }
            });
            return row;
        });
        table.getColumns().add(isWorkingColumn);
        table.getColumns().add(imageColumn);
        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(jobColumn);
        table.getColumns().add(departmentColumn);
        table.getColumns().add(workDurationColumn);
        table.getColumns().add(workShiftColumn);

        container.getChildren().add(table);
    }

    private void loadFilters(){
        FilteredList<EmployeeModel> allData = new FilteredList<>(simpleEmployees);

        FilteredList<EmployeeModel> filteredDate = loadDateFilter(allData);
        loadTextFilter(filteredDate);
    }

    private FilteredList<EmployeeModel> loadDateFilter(FilteredList<EmployeeModel> data){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        datePicker.getEditor().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                datePicker.show();
            }
        });
        datePicker.setId("ios-date");

        Pane spacing = new Pane();
        spacing.setPrefWidth(10);
        spacing.setPrefHeight(1);

        MDL2IconFont clearIcon = new MDL2IconFont("\uE711");
        clearIcon.getStyleClass().add("close-button");

        Button clear = new Button();
        clear.setGraphic(clearIcon);

        FilteredList<EmployeeModel> filteredList = new FilteredList<>(data);
        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            if(clear.isPressed()){
                datePicker.setValue(null);
            }

            LocalDate date = datePicker.getValue();

            if(date == null){
                return null;
            }

            final LocalDate filterDate = date;

            return o -> {
                return o.wasThere(filterDate);
            };

        }, datePicker.valueProperty(), clear.pressedProperty()));

        hBox.getChildren().add(datePicker);
        hBox.getChildren().add(spacing);
        hBox.getChildren().add(clear);


        overTableBox.getChildren().add(hBox);

        return filteredList;
    }

    public VBox getScreen() {
        return screen;
    }

    private void loadTextFilter(FilteredList<EmployeeModel> data){
        Pane expand = new Pane();
        expand.setPrefHeight(1);
        HBox.setHgrow(expand, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        ComboBox<String> choiceFilter = new ComboBox<>(filterModels);
        choiceFilter.getSelectionModel().select(0);
        choiceFilter.setId("ios-box");

        TextField2 filterField = new TextField2();
        final List<TableColumn<EmployeeModel, ?>> columns = table.getColumns();

        FilteredList<EmployeeModel> filteredList = new FilteredList<>(data);
        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            String text = filterField.getText();
            String filter = choiceFilter.getValue();

            if(text == null || text.isEmpty() || filter == null || filter.equals("-"))
                return null;

            final String filterText = text.toLowerCase();

            return o -> {
                for(TableColumn<EmployeeModel, ?> col : columns){
                    ObservableValue<?> observable = col.getCellObservableValue(o);
                    if(!col.getText().equals(filter))
                        continue;

                    if(observable != null){
                        Object value = observable.getValue();

                        if(value != null && value.toString().toLowerCase().startsWith(filterText)) {
                            return true;
                        }
                    }
                }
                return false;
            };

        }, filterField.textProperty(), choiceFilter.valueProperty()));

        SortedList<EmployeeModel> sortedList = new SortedList<>(filteredList);

        sortedList.comparatorProperty().bind(table.comparatorProperty());
        sortedList.addListener(new ListChangeListener<EmployeeModel>() {
            @Override
            public void onChanged(Change<? extends EmployeeModel> change) {
                quantity.setText(sortedList.size() + " de " + data.getSource().size() + " funcionários");
            }
        });
        table.setItems(sortedList);
        quantity.setText(sortedList.size() + " de " + data.getSource().size() + " funcionários");

        filterField.setId("ios-field");
        filterField.setPromptText("Buscar...");

        Pane spacing = new Pane();
        spacing.setPrefHeight(1);
        spacing.setPrefWidth(10);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_RIGHT);

        hBox.getChildren().add(choiceFilter);
        hBox.getChildren().add(spacing);
        hBox.getChildren().add(filterField);

        Label filterLabel = new Label("Filtrar");
        filterLabel.setStyle("-fx-font-size: 16px;");

        filterLabel.setAlignment(Pos.CENTER);

        vBox.getChildren().add(filterLabel);
        vBox.getChildren().add(hBox);

        overTableBox.getChildren().add(expand);
        overTableBox.getChildren().add(vBox);
    }

    public void load(){
        simpleEmployees.clear();

        if(ManagerModel.getInstance().getDepartment() == 8){
            simpleEmployees.addAll(FXCollections.observableArrayList(db.getSimpleEmployees(jobs)));
        } else {
            simpleEmployees.addAll(FXCollections.observableArrayList(db.getSimpleEmployeesByManagerDepartment(jobs)));
        }
    }
}
