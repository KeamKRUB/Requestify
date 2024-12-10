package ku.cs.controllers.admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ku.cs.models.advisor.Advisor;
import ku.cs.models.advisor.AdvisorList;
import ku.cs.models.department.Department;
import ku.cs.models.department.DepartmentList;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AdminAdvisorEditController {

    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField advisorIdTextField;
    @FXML private MenuButton facultyMenuButton;
    @FXML private MenuButton departmentMenuButton;

    private Advisor advisor;

    private SettingList settingList;
    private User user;
    private DataRepository dataRepository;
    private AdvisorList advisorList;
    private UserList userList;
    private FacultyList facultyList;
    private DepartmentList departmentList;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        settingList = dataRepository.getSettingList();
        userList = dataRepository.getUserList();
        advisorList = dataRepository.getAdvisorList();
        facultyList = dataRepository.getFacultyList();
        departmentList = dataRepository.getDepartmentList();

        if (advisor != null) {
            String hashedPassword = advisor.getPassword();
            String maskedPassword;
            if (hashedPassword.length() > 4) {
                maskedPassword = hashedPassword.substring(0, 4) + "****" + hashedPassword.substring(hashedPassword.length() - 4);
            } else {
                maskedPassword = hashedPassword;
            }

            firstNameTextField.setText(advisor.getFirstName());
            lastNameTextField.setText(advisor.getLastName());
            usernameTextField.setText(advisor.getUsername());
            passwordTextField.setText(maskedPassword);
            advisorIdTextField.setText(advisor.getAdvisorId());

            initFacultyMenuButton();
        }
    }

    public void initFacultyMenuButton() {
        facultyMenuButton.getItems().clear();
        //Sort a Faculty in dropdown
        List<Faculty> sortedFacultyList = facultyList.getFacultyList()
                .stream()
                .sorted(Comparator.comparing(Faculty::getFacultyName))
                .toList();

        for (Faculty faculty : sortedFacultyList) {
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
        //Sort a Department in dropdown
        List<Department> sortedDepartmentList = departmentList.getDepartmentListByFaculty(facultyName)
                .stream()
                .sorted(Comparator.comparing(Department::getDepartmentName))
                .toList();
        for (Department department : sortedDepartmentList) {
            MenuItem menuItem = new MenuItem(department.getDepartmentName());
            menuItem.setOnAction(e -> departmentMenuButton.setText(menuItem.getText()));
            departmentMenuButton.getItems().add(menuItem);
        }
    }

    public void setAdvisor (Advisor advisor) {
        this.advisor = advisor;
        firstNameTextField.setText(advisor.getFirstName());
        lastNameTextField.setText(advisor.getLastName());
        usernameTextField.setText(advisor.getUsername());
        passwordTextField.setText(advisor.getPassword());
        advisorIdTextField.setText(advisor.getAdvisorId());
        facultyMenuButton.setText(advisor.getFaculty());
        departmentMenuButton.setText(advisor.getDepartment());

        initFacultyMenuButton();
        initDepartmentMenuButton(advisor.getFaculty());

    }

    public void setAdvisorList (AdvisorList advisorList) {
        this.advisorList = advisorList;
    }

    @FXML
    public void onCancelButtonClick() {
        Stage stage = (Stage) firstNameTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onConfirmButtonClick() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText(); // Get the entered password
        String advisorId = advisorIdTextField.getText();
        String faculty = facultyMenuButton.getText();
        String department = departmentMenuButton.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || advisorId.isEmpty() || faculty.equals("เลือกคณะ") || department.equals("เลือกภาควิชา")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Edit Advisor");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(advisorList.findAdvisorById(advisorId)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Advisor");
            alert.setContentText("รหัสประจำตัวนี้ถูกใช้งานไปแล้ว");
            alert.showAndWait();
        } else {
            advisor.setFirstName(firstName);
            advisor.setLastName(lastName);
            advisor.setAdvisorId(advisorId);
            advisor.setFaculty(faculty);
            advisor.setDepartment(department);
            advisor.setUsername(username);

            if (!password.equals(advisor.getPassword())) {
                // Rehash password
                String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                advisor.setPassword(hashedPassword);
            }

            Setting userSetting = advisor.getUserSetting();
            userSetting.setUsername(username);

            dataRepository.writeData(AdvisorList.class);
            dataRepository.writeData(UserList.class);
            dataRepository.writeData(SettingList.class);

            Stage stage = (Stage) firstNameTextField.getScene().getWindow();
            stage.close();
        }
    }

    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove Advisor?");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบเที่ปรึกษา" + advisor.getFirstName() + " " + advisor.getLastName() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Setting newSetting = new Setting(advisor.getUsername());

            settingList.removeSetting(newSetting);
            userList.removeUser(advisor);
            advisorList.removeAdvisor(advisor);
            dataRepository.writeData(SettingList.class);
            dataRepository.writeData(AdvisorList.class);
            dataRepository.writeData(UserList.class);
            Stage stage = (Stage) firstNameTextField.getScene().getWindow();
            stage.close();
        }
    }

}

