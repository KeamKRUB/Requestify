package ku.cs.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ku.cs.models.request.Request;
import ku.cs.models.student.Student;
import ku.cs.models.user.UserSession;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class StudentRequestStudentInfoController {

    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label idLabel;
    @FXML private Label advisorNameLabel;
    @FXML private Label studentEmailLabel;

    @FXML private TextField studentAcademicTextField;
    @FXML private Label studentFacultyLabel;
    @FXML private Label studentDepartmentLabel;
    @FXML private TextField studentPhoneNumberTextField;

    @FXML private Request newRequest = (Request) FXRouter.getData();
    @FXML private String previousPage = FXRouter.getPreviousPage();

    @FXML
    public void initialize() {
        UserSession session = UserSession.getSession();
        Student user = (Student) session.getLoggedInUser();
        requestTopicLabel.setText(newRequest.getRequestTopic());
        requestTypeLabel.setText(newRequest.getRequestType());
        studentAcademicTextField.setText(newRequest.getStudentAcademic());
        studentFacultyLabel.setText(user.getFaculty());
        studentDepartmentLabel.setText(user.getDepartment());
        studentPhoneNumberTextField.setText(newRequest.getStudentPhoneNumber());

        studentNameLabel.setText(user.getName());
        idLabel.setText(user.getStudentId());
        studentEmailLabel.setText(user.getEmail());
        advisorNameLabel.setText(user.getAdvisor());
    }

    @FXML
    public void onBackButtonClick(){
        newRequest.setStudentAcademic(studentAcademicTextField.getText());
        newRequest.setStudentFaculty(studentFacultyLabel.getText());
        newRequest.setStudentDepartment(studentDepartmentLabel.getText());
        newRequest.setStudentPhoneNumber(studentPhoneNumberTextField.getText());
        newRequest.setStudentId(idLabel.getText());
        newRequest.setStudentEmail(studentEmailLabel.getText());
        newRequest.setStudentAdvisor(advisorNameLabel.getText());
        newRequest.setStudentName(studentNameLabel.getText());
        try {
            FXRouter.loadPage(previousPage,newRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void onNextButtonClick(){
        newRequest.setStudentAcademic(studentAcademicTextField.getText());
        newRequest.setStudentFaculty(studentFacultyLabel.getText());
        newRequest.setStudentDepartment(studentDepartmentLabel.getText());
        newRequest.setStudentPhoneNumber(studentPhoneNumberTextField.getText());
        newRequest.setStudentId(idLabel.getText());
        newRequest.setStudentEmail(studentEmailLabel.getText());
        newRequest.setStudentAdvisor(advisorNameLabel.getText());
        newRequest.setStudentName(studentNameLabel.getText());
        try {
            FXRouter.loadPage("student-request-confirmation",newRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
