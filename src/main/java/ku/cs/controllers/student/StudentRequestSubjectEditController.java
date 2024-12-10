package ku.cs.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ku.cs.models.request.Subject;
import ku.cs.models.request.SubjectList;

public class StudentRequestSubjectEditController {

    @FXML private TextField subjectIdTextField;
    @FXML private TextField subjectNameTextField;

    private Subject subject;
    private SubjectList subjectList;

    @FXML
    private void initialize() {
        subject = new Subject();
        subjectNameTextField.setText(subject.getSubjectName());
        subjectIdTextField.setText(subject.getSubjectId());
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        subjectIdTextField.setText(subject.getSubjectId());
        subjectNameTextField.setText(subject.getSubjectName());
    }

    public void setSubjectList(SubjectList subjectList) {
        this.subjectList = subjectList;
    }

    @FXML
    public void onConfirmButtonClick(){
        subject.setSubjectId(subjectIdTextField.getText());
        subject.setSubjectName(subjectNameTextField.getText());

        Stage stage = (Stage) subjectIdTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCancelButtonClick(){
        System.out.println("test");
        Stage stage = (Stage) subjectIdTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onDeleteButtonClick(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove Subject?");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบวิชา" + " " + "("+subject.getSubjectId() +
                ")"+ " " + subject.getSubjectName() + " "+ "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                subjectList.removeSubject(subject);
                System.out.println("Subject removed.");

                Stage stage = (Stage) subjectIdTextField.getScene().getWindow();
                stage.close();
            }
        });
    }


}
