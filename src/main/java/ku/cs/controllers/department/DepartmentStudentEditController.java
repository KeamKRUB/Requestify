package ku.cs.controllers.department;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ku.cs.models.advisor.Advisor;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.Optional;

public class DepartmentStudentEditController {
    @FXML private TextField studentId;
    @FXML private TextField studentFirstname;
    @FXML private TextField studentLastname;
    @FXML private TextField studentEmail;
    @FXML private TextField studentDepartment;
    @FXML private MenuButton advisorMenuButton;

    private UserList userList;
    private Student student;
    private DataRepository dataRepository;
    private StudentList studentList;
    private SettingList settingList;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        userList = dataRepository.getUserList();
        settingList = dataRepository.getSettingList();
    }


    @FXML
    public void onConfirmButtonClick() {
        String id = studentId.getText();
        String firstName = studentFirstname.getText();
        String lastName = studentLastname.getText();
        String email = studentEmail.getText();
        String advisor = advisorMenuButton.getText();

        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || advisor.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("แก้ไข Student");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(studentList.findStudentById(id)!=null && !id.equals(student.getId())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Student");
            alert.setContentText("รหัสนิสิตนี้ถูกใช้งานไปแล้ว");
            alert.showAndWait();
        } else{
            student.setStudentId(id);
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setEmail(email);
            student.setAdvisor(advisor);

            dataRepository.writeData(StudentList.class);

            Stage stage = (Stage) studentId.getScene().getWindow();
            stage.close();
        }

    }
    public void initAdvisorMenuButton(){
        advisorMenuButton.getItems().clear();
        for (User user : userList.getUserList()) {
            if(user.getRole().equals("Advisor")){
                Advisor advisor = (Advisor) user;
                if (advisor.getDepartment().equals(student.getDepartment())) {
                    MenuItem menuItem = new MenuItem(advisor.getName());
                    menuItem.setOnAction(e -> handleAdvisorMenuButton(menuItem.getText()));
                    advisorMenuButton.getItems().add(menuItem);
                }

            }

        }
    }

    public void handleAdvisorMenuButton(String advisorName){
        advisorMenuButton.setText(advisorName);
    }


    public void setStudent (Student student) {
        this.student = student;
        studentId.setText(student.getStudentId());
        studentFirstname.setText(student.getFirstName());
        studentLastname.setText(student.getLastName());
        studentEmail.setText(student.getEmail());
        studentDepartment.setText(student.getDepartment());
        advisorMenuButton.setText(student.getAdvisor());

        initAdvisorMenuButton();
    }

    public void setStudentList(StudentList studentList) {
        this.studentList = studentList;
    }

    public void removeStudentFromUserList(Student student) {
        boolean studentExistsInUserList = false;

        for (User user : userList.getUserList()) {
            if (user.getUsername().equals(student.getUsername())) {
                studentExistsInUserList = true;
                Setting setting = user.getUserSetting();

                settingList.removeSetting(setting);
                userList.removeUser(user);

                dataRepository.writeData(SettingList.class);
                dataRepository.writeData(UserList.class);
                break;
            }
        }

        if (!studentExistsInUserList) {
            System.out.println("Student not found in user list, no removal needed.");
        }
    }



    @FXML
    public void onCancelButtonClick() {
        Stage stage = (Stage) studentId.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove Student");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบนิสิต ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            studentList.removeStudent(student);
            dataRepository.writeData(StudentList.class);

            removeStudentFromUserList(student);

            Stage stage = (Stage) studentId.getScene().getWindow();
            stage.close();
        }
    }

}
