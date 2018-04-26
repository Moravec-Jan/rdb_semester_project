package cz.tul.controller;

import cz.tul.App;
import cz.tul.model.db.Projeti;
import cz.tul.model.ui.BranaTableEntity;
import cz.tul.model.ui.RidicEntity;
import cz.tul.model.db.projections.GatePassageProjection;
import cz.tul.service.DatabaseService;
import cz.tul.task.ExportToFileTask;
import cz.tul.task.ImportFileTask;
import cz.tul.utils.BranaCvsCreator;
import cz.tul.utils.CsvCreator;
import cz.tul.utils.RidicCvsCreator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.util.List;

@Controller
public class MainController {

    @FXML
    private AnchorPane pane;
    @FXML
    private AnchorPane innerPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextField gate;
    @FXML
    private ChoiceBox<String> select;
    @FXML
    private TableView table;
    @FXML
    private HBox selectPanel1;
    @FXML
    private HBox selectPanel2;
    @FXML
    private DatePicker from_date;
    @FXML
    private DatePicker to_date;
    @FXML
    private TextField from_time;
    @FXML
    private TextField to_time;
    @FXML
    private TextField kilometers;
    @FXML
    private VBox exportStatusPanel;
    @FXML
    private ProgressIndicator exportProgressBar;
    @Autowired
    DatabaseService service;

    @Autowired
    CsvCreator creator;

    private Stage stage;

    public static Stage createNewStage(ApplicationContext context) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.getLayoutResource("main.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);

        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Stage mainStage = new Stage();
        MainController controller = fxmlLoader.getController();
        controller.stage = mainStage;
        mainStage.setOnCloseRequest(event -> Platform.exit());
        mainStage.setTitle("Car tracking");
        mainStage.setScene(new Scene(root, 1280, 720));
        return mainStage;
    }

    public void exit() {
        Platform.exit();
    }

    public void initialize() {
        exportStatusPanel.setVisible(false);
        select.getItems().add(0, "Průjezdy bránou");
        select.getItems().add(1, "Kdo neprojel satelitní bránou");
        select.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    selectPanel1.setVisible(true);
                    selectPanel2.setVisible(false);
                    break;
                case 1:
                    selectPanel1.setVisible(false);
                    selectPanel2.setVisible(true);
                    break;
            }
        });
        select.getSelectionModel().select(0);
        table.getColumns().clear();
        table.getItems().clear();
        from_date.setValue(LocalDate.parse("2018-03-26"));
        to_date.setValue(LocalDate.parse("2018-04-23"));
        from_time.setText("06:30:00.0");
        to_time.setText("23:59:59.0");
    }

    //D:\Moje dokumenty\skola\RDB\projekt\src\main\resources
    public void importData() {
        service.deleteAll();
        Iterable<Projeti> all = service.getAll();


        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Cvs file", "*.csv"));
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            ImportFileTask task = new ImportFileTask(service, new File("D:\\Moje dokumenty\\skola\\RDB\\projekt\\src\\main\\resources\\sample_data.csv"));
            setProgressBarVisibility(progressBar, true);
            innerPane.setVisible(false);
            progressBar.progressProperty().bind(task.progressProperty());
            task.addEventHandler(WorkerStateEvent.ANY, event -> {
                if (event.getEventType().equals(WorkerStateEvent.WORKER_STATE_SUCCEEDED)) {
                    onDataLoaded();
                }
            });
            new Thread(task).start();
        }
    }

    private void setProgressBarVisibility(Parent progressBar, boolean visisbility) {
        progressBar.getParent().setVisible(visisbility);
    }

    private void onDataLoaded() {
        FXMLLoader fxmlLoader = new FXMLLoader(App.getLayoutResource("body.fxml"));
        AnchorPane body;
        setProgressBarVisibility(progressBar, false);
        innerPane.setVisible(true);
        try {
            body = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        body.prefWidthProperty().bind(pane.widthProperty());
        body.prefHeightProperty().bind(pane.heightProperty());
    }

    public void onFindByGate() {
        String gateText = gate.getText();
        if (gateText == null || gateText.isEmpty()) {
            return;
        }

        setGateTableData(service.getByGate(gateText));
    }

    private void setGateTableData(List<GatePassageProjection> records) {

        table.getItems().clear();
        ObservableList<BranaTableEntity> projeti = FXCollections.observableArrayList();
        projeti.addAll(BranaTableEntity.createFromProjeti(records));

        if (table.getColumns().size() != 6) {
            table.getColumns().clear();
            TableColumn<BranaTableEntity, String> crpColumn = new TableColumn<>("CRP");
            crpColumn.setMinWidth(100);
            crpColumn.setCellValueFactory(new PropertyValueFactory<>("crp"));

            TableColumn<BranaTableEntity, String> jmenoColumn = new TableColumn<>("Jméno");
            jmenoColumn.setMinWidth(100);
            jmenoColumn.setCellValueFactory(new PropertyValueFactory<>("jmeno"));

            TableColumn<BranaTableEntity, String> casColumn = new TableColumn<>("Čas");
            casColumn.setMinWidth(130);
            casColumn.setCellValueFactory(new PropertyValueFactory<>("cas"));

            TableColumn<BranaTableEntity, String> spzColumn = new TableColumn<>("Spz");
            spzColumn.setMinWidth(100);
            spzColumn.setCellValueFactory(new PropertyValueFactory<>("spz"));

            TableColumn<BranaTableEntity, String> vyrobceColumn = new TableColumn<>("Výrobce");
            vyrobceColumn.setMinWidth(100);
            vyrobceColumn.setCellValueFactory(new PropertyValueFactory<>("vyrobce"));

            TableColumn<BranaTableEntity, String> typColumn = new TableColumn<>("Typ");
            typColumn.setMinWidth(100);
            typColumn.setCellValueFactory(new PropertyValueFactory<>("typ"));
            table.getColumns().addAll(crpColumn, jmenoColumn, casColumn, spzColumn, vyrobceColumn, typColumn);
        }
        table.setItems(projeti);


    }

    public void onSecondSelectClick() {
        Timestamp to;
        Timestamp from;
        int km;
        try {
            LocalDate fromDate = from_date.getValue();
            LocalTime fromTime = LocalTime.parse(from_time.getText());
            LocalDate toDate = to_date.getValue();
            LocalTime toTime = LocalTime.parse(to_time.getText());
            LocalDateTime fromDateTime = LocalDateTime.of(fromDate, fromTime);
            ZonedDateTime fromZdt = fromDateTime.atZone(ZoneId.systemDefault());
            from = new Timestamp(fromZdt.toInstant().toEpochMilli());
            LocalDateTime toDateTime = LocalDateTime.of(toDate, toTime);
            ZonedDateTime toZdt = toDateTime.atZone(ZoneId.systemDefault());
            to = new Timestamp(toZdt.toInstant().toEpochMilli());

            km = Integer.valueOf(kilometers.getText());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        setDriverTableData(service.getDriversByKmWhoDidNotPassSatelliteGate(from, to, km));

    }

    private void setDriverTableData(List<RidicEntity> data) {
        table.getItems().clear();
        ObservableList<RidicEntity> drivers = FXCollections.observableArrayList();
        drivers.addAll(data);

        if (table.getColumns().size() != 3) {
            table.getColumns().clear();
            TableColumn<RidicEntity, String> crpColumn = new TableColumn<>("CRP");
            crpColumn.setMinWidth(100);
            crpColumn.setCellValueFactory(new PropertyValueFactory<>("crp"));

            TableColumn<RidicEntity, String> jmenoColumn = new TableColumn<>("Jméno");
            jmenoColumn.setMinWidth(100);
            jmenoColumn.setCellValueFactory(new PropertyValueFactory<>("jmeno"));

            TableColumn<RidicEntity, Integer> kmColumn = new TableColumn<>("Najeto");
            kmColumn.setMinWidth(130);
            kmColumn.setCellValueFactory(new PropertyValueFactory<>("km"));

            table.getColumns().addAll(crpColumn, jmenoColumn, kmColumn);
        }
        table.setItems(drivers);
    }

    public void exportData() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Cvs file", "*.csv"));
        chooser.setInitialFileName("rdb_data.csv");
        File file = chooser.showSaveDialog(stage);

        ObservableList items = table.getItems();
        if (file != null) {
            CsvCreator creator = null;
            if (table.getColumns().size() == 3) {
                creator = new RidicCvsCreator();
            } else {
                creator = new BranaCvsCreator();
            }
            ExportToFileTask task = new ExportToFileTask(file, items, creator);
            exportProgressBar.progressProperty().bind(task.progressProperty());
            exportStatusPanel.setVisible(true);
            task.addEventHandler(WorkerStateEvent.ANY, event -> {
                if (event.getEventType().equals(WorkerStateEvent.WORKER_STATE_SUCCEEDED)) {
                    //exportStatusPanel.setVisible(false);
                }
                if (event.getEventType().equals(WorkerStateEvent.WORKER_STATE_FAILED)) {
                    exportStatusPanel.setVisible(false);
                }
                if (event.getEventType().equals(WorkerStateEvent.WORKER_STATE_CANCELLED)) {
                    exportStatusPanel.setVisible(false);
                }
            });
            new Thread(task).start();
        }
    }
}
