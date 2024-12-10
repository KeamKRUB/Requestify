package ku.cs.controllers.user;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class RegisterFirstPageController {
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField studentIdTextField;
    @FXML private TextField emailTextField;
    @FXML private Label firstNameErrorLabel;
    @FXML private Label lastNameErrorLabel;
    @FXML private Label studentIdErrorLabel;
    @FXML private Label emailErrorLabel;

    private Student newStudent;
    private DataRepository dataRepository;
    private StudentList studentList;
    private UserList userList;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        studentList = dataRepository.getStudentList();
        userList = dataRepository.getUserList();

        if (FXRouter.getData() == null) {
            newStudent = new Student();
        } else {
            newStudent = (Student) FXRouter.getData();
        }
        firstNameTextField.setText(newStudent.getFirstName());
        lastNameTextField.setText(newStudent.getLastName());
        studentIdTextField.setText(newStudent.getStudentId());
        emailTextField.setText(newStudent.getEmail());

        clearErrorLabels();
    }

    private void clearErrorLabels() {
        firstNameErrorLabel.setVisible(false);
        lastNameErrorLabel.setVisible(false);
        studentIdErrorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
    }

    private void showErrorLabel(Label label) {
        label.setVisible(true);
    }

    @FXML
    public void onNextButtonClick() {
        clearErrorLabels();
        boolean hasError = false;

        if (firstNameTextField.getText().isEmpty()) {
            firstNameErrorLabel.setText("First name is required.");
            showErrorLabel(firstNameErrorLabel);
            hasError = true;
        }
        if (lastNameTextField.getText().isEmpty()) {
            lastNameErrorLabel.setText("Last name is required.");
            showErrorLabel(lastNameErrorLabel);
            hasError = true;
        }
        if (studentIdTextField.getText().isEmpty()) {
            studentIdErrorLabel.setText("Student ID is required.");
            showErrorLabel(studentIdErrorLabel);
            hasError = true;
        } else if (!studentIdTextField.getText().matches("\\d{10}")) {
            studentIdErrorLabel.setText("Student ID must be a 10-digit number.");
            showErrorLabel(studentIdErrorLabel);
            hasError = true;
        }
        if (emailTextField.getText().isEmpty()) {
            emailErrorLabel.setText("Email is required.");
            showErrorLabel(emailErrorLabel);
            hasError = true;
        }

        if (!hasError) {
            Student student = new Student();
            student.setFirstName(firstNameTextField.getText());
            student.setLastName(lastNameTextField.getText());
            student.setStudentId(studentIdTextField.getText());
            student.setEmail(emailTextField.getText());

            newStudent = studentList.findByStudent(student);
            if (newStudent == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Not Found");
                alert.setHeaderText(null);
                alert.setContentText("ไม่พบรายชื่อนิสิต โปรดติดต่อผู้ดูแลระบบ");
                alert.showAndWait();
            } else {
                if(userList.findUserByUsername(newStudent.getUsername()) == null) {
                    try {
                        FXRouter.loadPage("register-second-page", newStudent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Already Registered");
                    alert.setHeaderText(null);
                    alert.setContentText("รายชื่อนี้ได้ลงทะเบียนเรียบร้อยแล้ว");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    public void onLoginButtonClick() {
        try {
            FXRouter.goTo("login-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
