package ku.cs.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

import java.util.Optional;

public class AdminDepartmentEditStaffController {
    @FXML
    private TextField userFirstnameTextField;
    @FXML
    private TextField userLastnameTextField;
    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField userPasswordTextField;
    @FXML
    private MenuButton facultyMenuButton;
    @FXML
    private MenuButton departmentMenuButton;

    private DepartmentStaff departmentStaff ;

    private FacultyList facultyList;
    private DepartmentList departmentList;
    private DepartmentStaffList departmentStaffList;
    private UserList userList;
    private SettingList settingList;


    private DataRepository dataRepository;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        departmentStaffList = dataRepository.getDepartmentStaffList();
        userList = dataRepository.getUserList();
        settingList = dataRepository.getSettingList();
        facultyList = dataRepository.getFacultyList();
        departmentList = dataRepository.getDepartmentList();

        if (departmentStaff != null) {
            String hashedPassword = departmentStaff.getPassword();
            String maskedPassword;
            if (hashedPassword.length() > 4) {
                maskedPassword = hashedPassword.substring(0, 4)
                        + "****" + hashedPassword.substring(hashedPassword.length() - 4);
            } else {
                maskedPassword = hashedPassword;
            }

            userFirstnameTextField.setText(departmentStaff.getFirstName());
            userLastnameTextField.setText(departmentStaff.getLastName());
            userNameTextField.setText(departmentStaff.getUsername());
            userPasswordTextField.setText(maskedPassword);
            facultyMenuButton.setText(departmentStaff.getFaculty());
            departmentMenuButton.setText(departmentStaff.getDepartment());
        }
    }


    public void initFacultyMenuButton(){
        facultyMenuButton.getItems().clear();
        for (Faculty faculty : facultyList.getFacultyList()) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> handleFacultyMenuButton(menuItem.getText()));
            facultyMenuButton.getItems().add(menuItem);
        }
    }

    public void handleFacultyMenuButton(String facultyName) {
        facultyMenuButton.setText(facultyName);
        departmentMenuButton.setText("เลือกภาควิชา");
        initDepartmentMenuButton(facultyName);
    }

    public void initDepartmentMenuButton(String facultyName){
        departmentMenuButton.getItems().clear();
        for (Department department : departmentList.getDepartmentListByFaculty(facultyName)) {
            MenuItem menuItem = new MenuItem(department.getDepartmentName());
            menuItem.setOnAction(e -> departmentMenuButton.setText(menuItem.getText()));
            departmentMenuButton.getItems().add(menuItem);
        }
    }

    //ActionButton
    @FXML
    public void onConfirmButtonClick() {
        String username = userNameTextField.getText();
        String firstName = userFirstnameTextField.getText();
        String lastName = userLastnameTextField.getText();
        String facultyName = facultyMenuButton.getText();
        String departmentName = departmentMenuButton.getText();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || facultyName.equals("เลือกคณะ") || departmentName.equals("เลือกภาควิชา")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Edit Department Staff");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(userList.findUserByUsername(username) != null && !username.equals(departmentStaff.getUsername())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Department Staff");
            alert.setContentText("มี username นี้อยู่ในระบบเรียบร้อยแล้ว");
            alert.showAndWait();
        } else {
            departmentStaff.setUsername(username);
            departmentStaff.setFirstName(firstName);
            departmentStaff.setLastName(lastName);
            departmentStaff.setFaculty(facultyName);
            departmentStaff.setDepartment(departmentName);

            Setting userSetting = departmentStaff.getUserSetting();
            userSetting.setUsername(username);

            dataRepository.writeData(DepartmentStaffList.class);
            dataRepository.writeData(UserList.class);
            dataRepository.writeData(SettingList.class);
            Stage stage = (Stage) userFirstnameTextField.getScene().getWindow();
            stage.close();
        }
    }

    public void setDepartmentStaff (DepartmentStaff departmentStaff) {
        this.departmentStaff = departmentStaff;
        userFirstnameTextField.setText(departmentStaff.getFirstName());
        userLastnameTextField.setText(departmentStaff.getLastName());
        userNameTextField.setText(departmentStaff.getUsername());
        userPasswordTextField.setText(departmentStaff.getPassword());
        facultyMenuButton.setText(departmentStaff.getFaculty());
        departmentMenuButton.setText(departmentStaff.getDepartment());
        if (departmentStaff.getFaculty() != null && !departmentStaff.getFaculty().isEmpty()) {
            facultyMenuButton.setText(departmentStaff.getFaculty());
            initDepartmentMenuButton(departmentStaff.getFaculty());
        }else {
            facultyMenuButton.setText("เลือกคณะ");
        }

        if (departmentStaff.getDepartment() != null && !departmentStaff.getDepartment().isEmpty()) {
            departmentMenuButton.setText(departmentStaff.getDepartment());
        } else {
            departmentMenuButton.setText("เลือกภาควิชา");
        }

        initFacultyMenuButton();

    }

    public void setDepartmentStaffList(DepartmentStaffList departmentStaffList) {
        this.departmentStaffList = departmentStaffList;
    }


    @FXML
    public void onCancelButtonClick() {
        Stage stage = (Stage) userFirstnameTextField.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("โปรดยืนยัน");
        alert.setHeaderText("Remove Department Staff");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบเจ้าหน้าที่ภาควิชา?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Setting newSetting = new Setting(departmentStaff.getUsername());
            settingList.removeSetting(newSetting);
            userList.removeUser(departmentStaff);
            departmentStaffList.removeStaff(departmentStaff);

            dataRepository.writeData(SettingList.class);
            dataRepository.writeData(DepartmentStaffList.class);
            dataRepository.writeData(UserList.class);

            Stage stage = (Stage) userFirstnameTextField.getScene().getWindow();
            stage.close();
        }
    }
}
