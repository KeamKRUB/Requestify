package ku.cs.controllers.admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.faculty.FacultyStaffList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AdminFacultyEditController {
    @FXML private TableView<FacultyStaff> adminFacultyStaffTableView;

    @FXML private TextField userFirstNameTextField;
    @FXML private TextField userLastNameTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField userPasswordTextField;

    @FXML private TextField facultyIdTextField;
    @FXML private TextField facultyNameTextField;
    @FXML private TextField searchTextField;

    private Faculty faculty = (Faculty) FXRouter.getData();
    private FacultyStaffList facultyStaffList;
    private FacultyList facultyList;
    private DataRepository dataRepository;

    private FilteredList<FacultyStaff> filteredStaffs;

    private UserList userList;
    private SettingList settingList;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(FacultyStaffList.class);

        facultyStaffList = dataRepository.getFacultyStaffList();
        facultyList = dataRepository.getFacultyList();
        userList = dataRepository.getUserList();
        settingList = dataRepository.getSettingList();

        faculty = facultyList.findByObject(faculty);

        facultyIdTextField.setText(faculty.getFacultyId());
        facultyNameTextField.setText(faculty.getFacultyName());


        TableColumn<FacultyStaff, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<FacultyStaff, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<FacultyStaff, String> userNameCol = new TableColumn<>("ชื่อผู้ใช้");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<FacultyStaff, String> passwordCol = new TableColumn<>("รหัสผ่าน");
        passwordCol.setCellValueFactory(cellData -> {
                    String fullPassword = cellData.getValue().getPassword();
                    if (fullPassword.length() > 4) {
                        String maskedPassword = fullPassword.substring(0, 4)
                                + "****"
                                + fullPassword.substring(fullPassword.length() - 4);
                        return new SimpleStringProperty(maskedPassword);
                    } else {
                        return new SimpleStringProperty(fullPassword);
                    }
                });
        TableColumn<FacultyStaff, String> facultyCol = new TableColumn<>("คณะ");
        facultyCol.setCellValueFactory(new PropertyValueFactory<>("faculty"));

        adminFacultyStaffTableView.getColumns().clear();
        adminFacultyStaffTableView.getColumns().addAll(List.of(firstNameCol, lastNameCol, userNameCol, passwordCol,facultyCol));

        showList();

        adminFacultyStaffTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<>() {

                    @Override
                    public void changed(ObservableValue<? extends FacultyStaff> observableValue, FacultyStaff facultyStaff, FacultyStaff newValue) {
                        if (newValue != null) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/admin-faculty-edit-staff-popup.fxml"));
                                    Parent root = loader.load();
                                    AdminFacultyEditStaffController controller = loader.getController();
                                    controller.setFacultyStaff(newValue);
                                    controller.setFacultyStaffList(facultyStaffList);

                                    Stage popupStage = new Stage();
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.setTitle("Faculty Staff Editing");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.showAndWait();

                                    adminFacultyStaffTableView.getSelectionModel().clearSelection();
                                    showList();

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                }
        );

        //ขยาย Table View ให้เหมาะกับข้อความ
        adminFacultyStaffTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth =
                    firstNameCol.getWidth() +
                            lastNameCol.getWidth() +
                            userNameCol.getWidth() +
                            passwordCol.getWidth() +
                            facultyCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                adminFacultyStaffTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                adminFacultyStaffTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    //ActionButton
    @FXML
    public void onConfirmButtonClick() {
        String facultyId = facultyIdTextField.getText();
        String facultyName = facultyNameTextField.getText();

        if (facultyId.isEmpty() || facultyName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Edit Faculty");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else {
            faculty.setFacultyId(facultyId);
            faculty.setFacultyName(facultyName);

            dataRepository.writeData(FacultyList.class);
            try {
                FXRouter.loadPage("admin-faculty");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    public void onAddButtonClick() {
        String username = usernameTextField.getText();
        String firstName = userFirstNameTextField.getText();
        String lastName = userLastNameTextField.getText();
        String password = userPasswordTextField.getText();
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Faculty Staff");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(userList.findUserByUsername(username)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Faculty Staff");
            alert.setContentText("มี username นี้อยู่ในระบบเรียบร้อยแล้ว");
            alert.showAndWait();
        } else {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, userPasswordTextField.getText().toCharArray());
            FacultyStaff newFacultyStaff = new FacultyStaff(
                    usernameTextField.getText(),
                    hashedPassword,
                    "FacultyStaff",
                    userFirstNameTextField.getText(),
                    userLastNameTextField.getText(),
                    faculty.getFacultyName()
            );
            Setting setting = new Setting(newFacultyStaff.getUsername());
            facultyStaffList.addStaff(newFacultyStaff);
            userList.addUser(newFacultyStaff);
            settingList.addSetting(setting);

            dataRepository.writeData(FacultyStaffList.class);
            dataRepository.writeData(UserList.class);
            dataRepository.writeData(SettingList.class);

            userFirstNameTextField.clear();
            userLastNameTextField.clear();
            usernameTextField.clear();
            userPasswordTextField.clear();

            showList();
        }
    }

    public void showList() {
        adminFacultyStaffTableView.getItems().clear();
        filteredStaffs = new FilteredList<>(FXCollections.observableArrayList(
                facultyStaffList
                        .getFacultyStaffListByFaculty(faculty.getFacultyName())
                        .getFacultyStaffList()), staff -> true);

        SortedList<FacultyStaff> sortedStaffs = new SortedList<>(filteredStaffs);
        sortedStaffs.setComparator(Comparator.comparing(FacultyStaff::getFaculty));

        adminFacultyStaffTableView.getItems().addAll(sortedStaffs);
    }

    @FXML
    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("โปรดยืนยัน");
        alert.setHeaderText("Remove Faculty");
        alert.setContentText("ถ้าหากลบคณะ เจ้าหน้าที่คณะจะหายไปด้วย!\nคุณแน่ใจหรือไม่ว่าต้องการลบคณะนี้?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if(!facultyStaffList.getFacultyStaffListByFaculty(faculty.getFacultyName()).getFacultyStaffList().isEmpty()) {
                facultyStaffList.removeAllStaff(
                        facultyStaffList
                                .getFacultyStaffListByFaculty(faculty.getFacultyName())
                                .getFacultyStaffList());

                dataRepository.writeData(FacultyStaffList.class);
                dataRepository.writeData(Setting.class);
                dataRepository.writeData(UserList.class);
            }
            facultyList.removeFaculty(faculty);
            dataRepository.writeData(FacultyList.class);

            try {
                FXRouter.loadPage("admin-faculty");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredStaffs.setPredicate(facultyStaff -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return facultyStaff.textToSearch().toLowerCase().contains(searchText);
        });
    }

}
