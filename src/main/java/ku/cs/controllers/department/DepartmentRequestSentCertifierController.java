package ku.cs.controllers.department;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ku.cs.models.department.DepartmentCertifier;
import ku.cs.models.department.DepartmentCertifierList;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.faculty.FacultyCertifierList;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.Datasource;
import ku.cs.services.DepartmentCertifierListFileDatasource;
import ku.cs.services.FXRouter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

public class DepartmentRequestSentCertifierController {
    @FXML private Request request = (Request) FXRouter.getData();
    @FXML private Label fileNameLabel;
    @FXML private TableView<DepartmentCertifier> departmentCertifierTableView;

    private DataRepository dataRepository;
    private DepartmentCertifierList departmentCertifierList;
    private RequestList requestList;
    private FilteredList<DepartmentCertifier> filteredCertifiers;
    private SortedList<DepartmentCertifier> sortedCertifiers;

    @FXML
    public void initialize() {
        fileNameLabel.setText("ยังไม่ได้อัพโหลดไฟล์");


        dataRepository = DataRepository.getDataRepository();
        requestList = dataRepository.getRequestList();
        departmentCertifierList = dataRepository.getDepartmentCertifierList();

        TableColumn<DepartmentCertifier, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<DepartmentCertifier, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<DepartmentCertifier, String> positionCol = new TableColumn<>("ตำแหน่ง");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        departmentCertifierTableView.getColumns().clear();
        departmentCertifierTableView.getColumns().addAll(firstNameCol, lastNameCol, positionCol);
        showDepartmentCertifiers();

        //ขยาย Table View ให้เหมาะกับข้อความ
        departmentCertifierTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = positionCol.getWidth() +
                    firstNameCol.getWidth() +
                    lastNameCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                departmentCertifierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                departmentCertifierTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });

    }
    private void showDepartmentCertifiers() {
        departmentCertifierTableView.getItems().clear();
        filteredCertifiers = new FilteredList<>(FXCollections.observableArrayList(
                departmentCertifierList
                        .getCertifierList()), certifier -> true);
        sortedCertifiers = new SortedList<>(filteredCertifiers);
        sortedCertifiers.setComparator(Comparator.comparingInt(certifier -> certifier.getPosition().length()));
        departmentCertifierTableView.getItems().addAll(sortedCertifiers);
    }

    @FXML
    public void onBrowseFileButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf","images PNG JPG", "*.png", "*.jpg", "*.jpeg"));
        Node source = (Node) event.getSource();
        File file = chooser.showOpenDialog(source.getScene().getWindow());
        if (file != null){
            try {
                File destDir = new File("data/confirmation");
                if (!destDir.exists()) destDir.mkdirs();
                String[] fileSplit = file.getName().split("\\.");
                String filename = LocalDate.now() + "_"+System.currentTimeMillis() + "."
                        + fileSplit[fileSplit.length - 1];
                Path target = FileSystems.getDefault().getPath(
                        destDir.getAbsolutePath()+System.getProperty("file.separator")+filename
                );
                Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING );
                fileNameLabel.setText(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void onSubmitButtonClick() {
        if (fileNameLabel.getText().equals("ยังไม่ได้อัพโหลดไฟล์")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("กรุณาอัพโหลดไฟล์ให้เรียบร้อยก่อนยืนยัน");
            alert.showAndWait();
        } if (departmentCertifierTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("กรุณาเลือกผู้รับรองจากตารางก่อนยืนยัน");
            alert.showAndWait();
        } else {
            dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("department-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    public void onCancelButtonClick() {
        try {
            FXRouter.loadPage("department-request");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
