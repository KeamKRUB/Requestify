package ku.cs.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.faculty.FacultyStaffList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.Optional;

public class AdminFacultyEditStaffController {
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

    private FacultyStaff facultyStaff;
    private FacultyStaffList facultyStaffList;
    private UserList userList;
    private FacultyList facultyList;
    private SettingList settingList;
    private DataRepository dataRepository;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        settingList = dataRepository.getSettingList();
        facultyStaffList = dataRepository.getFacultyStaffList();
        userList = dataRepository.getUserList();
        facultyList = dataRepository.getFacultyList();


        if (facultyStaff != null){
            String hashedPassword = facultyStaff.getPassword();
            String maskedPassword;
            if (hashedPassword.length() > 4) {
                maskedPassword = hashedPassword.substring(0, 4)
                        + "****"
                        + hashedPassword.substring(hashedPassword.length() - 4);
            } else {
                maskedPassword = hashedPassword;
            }
            userFirstnameTextField.setText(facultyStaff.getFirstName());
            userLastnameTextField.setText(facultyStaff.getLastName());
            userNameTextField.setText(facultyStaff.getUsername());
            userPasswordTextField.setText(maskedPassword);
            facultyMenuButton.setText(facultyStaff.getFaculty());
        }
    }

    public void setFacultyStaff (FacultyStaff facultyStaff) {
        this.facultyStaff = facultyStaff;
        userFirstnameTextField.setText(facultyStaff.getFirstName());
        userLastnameTextField.setText(facultyStaff.getLastName());
        userNameTextField.setText(facultyStaff.getUsername());
        userPasswordTextField.setText(facultyStaff.getPassword());
        facultyMenuButton.setText(facultyStaff.getFaculty());
        if (facultyStaff.getFaculty() != null && !facultyStaff.getFaculty().isEmpty()) {
            facultyMenuButton.setText(facultyStaff.getFaculty());
        }else {
            facultyMenuButton.setText("เลือกคณะ");
        }

        initFacultyMenuButton();

    }

    public void initFacultyMenuButton(){
        facultyMenuButton.getItems().clear();
        for (Faculty faculty : facultyList.getFacultyList()) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> facultyMenuButton.setText(menuItem.getText()));
            facultyMenuButton.getItems().add(menuItem);
        }
    }

    public void setFacultyStaffList(FacultyStaffList facultyStaffList) {
        this.facultyStaffList = facultyStaffList;
    }

    //ActionButton
    @FXML
    public void onConfirmButtonClick() {
        String firstName = userFirstnameTextField.getText();
        String lastName = userLastnameTextField.getText();
        String username = userNameTextField.getText();
        String faculty = facultyMenuButton.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || faculty.equals("เลือกคณะ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Edit Faculty Staff");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(userList.findUserByUsername(username)!=null  && !username.equals(facultyStaff.getUsername())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Faculty Staff");
            alert.setContentText("มี username นี้อยู่ในระบบเรียบร้อยแล้ว");
            alert.showAndWait();
        } else {
            facultyStaff.setUsername(username);
            facultyStaff.setFirstName(firstName);
            facultyStaff.setLastName(lastName);
            facultyStaff.setFaculty(faculty);

            dataRepository.writeData(FacultyStaffList.class);
            dataRepository.writeData(UserList.class);
            dataRepository.writeData(SettingList.class);
            Stage stage = (Stage) userFirstnameTextField.getScene().getWindow();
            stage.close();
            }
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
        alert.setHeaderText("Remove Faculty Staff");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบเจ้าหน้าที่คณะ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Setting setting = new Setting(facultyStaff.getUsername());
            settingList.removeSetting(setting);
            facultyStaffList.removeStaff(facultyStaff);
            userList.removeUser(facultyStaff);

            dataRepository.writeData(SettingList.class);
            dataRepository.writeData(FacultyStaffList.class);
            dataRepository.writeData(UserList.class);
            Stage stage = (Stage) userFirstnameTextField.getScene().getWindow();
            stage.close();
        }
    }
}
