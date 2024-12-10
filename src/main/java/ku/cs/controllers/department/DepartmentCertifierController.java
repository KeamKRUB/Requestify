package ku.cs.controllers.department;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.department.*;

import ku.cs.models.student.Student;
import ku.cs.models.user.UserSession;
import ku.cs.services.*;

import java.io.IOException;
import java.util.Comparator;

public class DepartmentCertifierController {

    @FXML private TableView<DepartmentCertifier> departmentCertifierTableView;
    @FXML private TextField departmentStaffFirstName;
    @FXML private TextField departmentStaffLastName;
    @FXML private TextField departmentStaffPosition;
    @FXML private TextField searchTextField;

    private DataRepository dataRepository;
    private DepartmentCertifierList departmentCertifierList;

    private FilteredList<DepartmentCertifier> filteredCertifiers;
    private SortedList<DepartmentCertifier> sortedCertifiers;
    private DepartmentStaff user;
    @FXML
    public void initialize() {
        UserSession userSession = UserSession.getSession();
        user = (DepartmentStaff) userSession.getLoggedInUser();

        dataRepository = DataRepository.getDataRepository();
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

        departmentCertifierTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<DepartmentCertifier>() {

                    @Override
                    public void changed(ObservableValue<? extends DepartmentCertifier> observableValue, DepartmentCertifier departmentCertifier, DepartmentCertifier newValue) {
                        if (newValue != null) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/department-certifier-edit-popup.fxml"));
                                    Parent root = loader.load();
                                    DepartmentCertifierEditController controller = loader.getController();
                                    controller.setCertifier(newValue);
                                    controller.setDepartmentCertifierList(departmentCertifierList);

                                    Stage popupStage = new Stage();
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.setTitle("Certifier Editing");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.showAndWait();

                                    departmentCertifierTableView.getSelectionModel().clearSelection();
                                    showDepartmentCertifiers();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
        );

        //ขยาย Table View ให้เหมาะกับข้อความ
        departmentCertifierTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = firstNameCol.getWidth() +
                    lastNameCol.getWidth() +
                    positionCol.getWidth();

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
    public void onAddButtonClick() {
        String firstName = departmentStaffFirstName.getText();
        String lastName = departmentStaffLastName.getText();
        String department = user.getDepartment();
        String position = departmentStaffPosition.getText();
        //check empty word

        if (firstName.isEmpty() || lastName.isEmpty() || department.isEmpty() || position.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Department Certifier");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        }else {
            DepartmentCertifier newCertifier = new DepartmentCertifier(firstName, lastName, department, position);
            departmentCertifierList.addCertifier(newCertifier);
            dataRepository.writeData(DepartmentCertifierList.class);

            departmentStaffFirstName.clear();
            departmentStaffLastName.clear();
            departmentStaffPosition.clear();
            showDepartmentCertifiers();
        }
    }


    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredCertifiers.setPredicate(request -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return  request.toString().toLowerCase().contains(searchText);
        });
    }

}
