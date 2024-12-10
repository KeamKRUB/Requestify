package ku.cs.controllers.admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import ku.cs.models.department.Department;
import ku.cs.models.department.DepartmentList;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.department.DepartmentStaffList;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminDepartmentEditController {

    @FXML private TableView<DepartmentStaff> adminDepartmentStaffTableView;

    @FXML private TextField userFirstnameTextField;
    @FXML private TextField userLastnameTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField userPasswordTextField;

    @FXML private TextField departmentIdTextField;
    @FXML private TextField departmentNameTextField;
    @FXML private MenuButton facultyMenuButton;

    @FXML private TextField searchTextField;

    Department department = (Department) FXRouter.getData();

    private DepartmentStaffList departmentStaffList;
    private DepartmentList departmentList;
    private FacultyList facultyList;

    private DataRepository dataRepository;


    private FilteredList<DepartmentStaff> filteredStaffs;

    private UserList userList;
    private SettingList settingList;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        departmentList = dataRepository.getDepartmentList();
        departmentStaffList = dataRepository.getDepartmentStaffList();

        userList = dataRepository.getUserList();
        settingList = dataRepository.getSettingList();
        departmentList = dataRepository.getDepartmentList();
        facultyList = dataRepository.getFacultyList();

        facultyMenuButton.setText(department.getFacultyName());
        departmentIdTextField.setText(department.getDepartmentId());
        departmentNameTextField.setText(department.getDepartmentName());

        initFacultyMenuButton();

        TableColumn<DepartmentStaff, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<DepartmentStaff, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<DepartmentStaff, String> userNameCol = new TableColumn<>("ชื่อผู้ใช้");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<DepartmentStaff, String> passwordCol = new TableColumn<>("รหัสผ่าน");
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

        TableColumn<DepartmentStaff, String> facultyCol = new TableColumn<>("คณะ");
        facultyCol.setCellValueFactory(new PropertyValueFactory<>("faculty"));

        TableColumn<DepartmentStaff, String> departmentCol = new TableColumn<>("ภาควิชา");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        adminDepartmentStaffTableView.getColumns().clear();
        adminDepartmentStaffTableView.getColumns().addAll(List.of(firstNameCol, lastNameCol, userNameCol,passwordCol,facultyCol,departmentCol));

        showList();

        adminDepartmentStaffTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/admin-department-edit-staff-popup.fxml"));
                                    Parent root = loader.load();
                                    AdminDepartmentEditStaffController controller = loader.getController();
                                    controller.setDepartmentStaff(newValue);
                                    controller.setDepartmentStaffList(departmentStaffList);

                                    Stage popupStage = new Stage();
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.setTitle("Department Staff Editing");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.showAndWait();

                                    adminDepartmentStaffTableView.getSelectionModel().clearSelection();
                                    showList();

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    });

        //ขยาย Table View ให้เหมาะกับข้อความ
        adminDepartmentStaffTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth =
                    firstNameCol.getWidth() +
                            lastNameCol.getWidth() +
                            userNameCol.getWidth() +
                            passwordCol.getWidth() +
                            departmentCol.getWidth() +
                            facultyCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                adminDepartmentStaffTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                adminDepartmentStaffTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    public void showList() {
        adminDepartmentStaffTableView.getItems().clear();
        filteredStaffs = new FilteredList<>(FXCollections.observableArrayList(
                departmentStaffList
                        .getDepartmentStaffListByDepartment(department.getDepartmentName())
                        .getDepartmentStaffList()), staff -> true);

        SortedList<DepartmentStaff> sortedStaffs = new SortedList<>(filteredStaffs);
        adminDepartmentStaffTableView.getItems().addAll(sortedStaffs);
    }

    public void initFacultyMenuButton(){
        facultyMenuButton.getItems().clear();
        for (Faculty faculty : facultyList.getFacultyList()) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> facultyMenuButton.setText(menuItem.getText()));
            facultyMenuButton.getItems().add(menuItem);
        }
    }

    //ActionButton
    @FXML
    public void onConfirmButtonClick() {
        String departmentId = departmentIdTextField.getText();
        String departmentName = departmentNameTextField.getText();
        String facultyName = facultyMenuButton.getText();

        if (departmentId.isEmpty() || departmentName.isEmpty() || facultyName.equals("เลือกคณะ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Edit Department");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else {
            if(department.getDepartmentName().equals(departmentName)) {
                department.setDepartmentId(departmentId);
                department.setDepartmentName(departmentName);
                department.setFacultyName(facultyName);

                dataRepository.writeData(DepartmentList.class);
            }
            try {
                FXRouter.loadPage("admin-department");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void onAddButtonClick() {
        String username = usernameTextField.getText();
        String userPassword = userPasswordTextField.getText();
        String firstName = userFirstnameTextField.getText();
        String lastName = userLastnameTextField.getText();

        if (username.isEmpty() || userPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Department Staff");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(userList.findUserByUsername(username)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Department Staff");
            alert.setContentText("มี username นี้อยู่ในระบบเรียบร้อยแล้ว");
            alert.showAndWait();
        } else {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, userPasswordTextField.getText().toCharArray());
            DepartmentStaff newDepartmentStaff = new DepartmentStaff(
                    usernameTextField.getText(),
                    hashedPassword,
                    "DepartmentStaff",
                    userFirstnameTextField.getText(),
                    userLastnameTextField.getText(),
                    department.getFacultyName(),
                    department.getDepartmentName()
            );

            departmentStaffList.addStaff(newDepartmentStaff);
            userList.addUser(newDepartmentStaff);
            settingList.addSetting(new Setting(newDepartmentStaff.getUsername()));

            dataRepository.writeData(DepartmentStaffList.class);
            dataRepository.writeData(UserList.class);
            dataRepository.writeData(SettingList.class);

            userFirstnameTextField.clear();
            userLastnameTextField.clear();
            usernameTextField.clear();
            userPasswordTextField.clear();

            showList();

        }
    }

    @FXML
    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("โปรดยืนยัน");
        alert.setHeaderText("Remove Department");
        alert.setContentText("ถ้าหากลบภาควิชา เจ้าหน้าที่ภาควิชาจะหายไปด้วย!\nคุณแน่ใจหรือไม่ว่าต้องการลบภาควิชานี้?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if(!departmentStaffList.getDepartmentStaffListByDepartment(department.getDepartmentName()).getDepartmentStaffList().isEmpty()) {
                departmentStaffList.removeAllStaff(
                        departmentStaffList
                                .getDepartmentStaffListByDepartment(department.getDepartmentName())
                                .getDepartmentStaffList());

                dataRepository.writeData(DepartmentStaffList.class);
                dataRepository.writeData(Setting.class);
                dataRepository.writeData(UserList.class);
            }

            departmentList.removeDepartment(department);
            dataRepository.writeData(DepartmentList.class);

            try {
                FXRouter.loadPage("admin-department");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredStaffs.setPredicate(departmentStaff -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return departmentStaff.textToSearch().contains(searchText);
        });
        adminDepartmentStaffTableView.setItems(new SortedList<>(filteredStaffs));
    }
}
