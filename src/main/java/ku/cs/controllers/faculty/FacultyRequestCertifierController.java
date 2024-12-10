package ku.cs.controllers.faculty;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import ku.cs.models.faculty.FacultyCertifier;
import ku.cs.models.faculty.FacultyCertifierList;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class FacultyRequestCertifierController {
    @FXML private TableView<FacultyCertifier> facultyCertifierTableView;
    @FXML private Label fileNameLabel;
    @FXML private TextField searchTextField;

    private DataRepository dataRepository;
    private FacultyCertifierList facultyCertifierList;
    private FilteredList<FacultyCertifier> filteredCertifiers;
    private SortedList<FacultyCertifier> sortedCertifiers;

    private FacultyStaff user;

    public void initialize() {
        fileNameLabel.setText("ยังไม่ได้อัพโหลดไฟล์");

        UserSession userSession = UserSession.getSession();
        user = (FacultyStaff) userSession.getLoggedInUser();

        dataRepository = DataRepository.getDataRepository();

        facultyCertifierList = dataRepository.getFacultyCertifierList();

        TableColumn<FacultyCertifier, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<FacultyCertifier, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<FacultyCertifier, String> positionCol = new TableColumn<>("ตำแหน่ง");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        facultyCertifierTableView.getColumns().clear();
        facultyCertifierTableView.getColumns().addAll(firstNameCol, lastNameCol, positionCol);

        showFacultyCertifiers();

        //ขยาย Table View ให้เหมาะกับข้อความ
        facultyCertifierTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = positionCol.getWidth() +
                    firstNameCol.getWidth() +
                    lastNameCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                facultyCertifierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                facultyCertifierTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });

    }

    private void showFacultyCertifiers(){
        filteredCertifiers = new FilteredList<>(FXCollections.observableArrayList(
                facultyCertifierList
                        .getCertifierListByFaculty(user.getFaculty())
                        .getCertifierList()), certifier -> true);

        sortedCertifiers = new SortedList<>(filteredCertifiers);
        facultyCertifierTableView.setItems(sortedCertifiers);
    }

    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredCertifiers.setPredicate(certifier -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return  certifier.getFaculty().toLowerCase().contains(searchText)
                    || certifier.getFirstName().toLowerCase().contains(searchText)
                    || certifier.getLastName().toLowerCase().contains(searchText)
                    || certifier.getPosition().toLowerCase().contains(searchText);
        });
    }

    //Action Button
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
        } else {
            dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("faculty-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void onCancelButtonClick() {
        try {
            FXRouter.loadPage("faculty-request-confirmation");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
