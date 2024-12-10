package ku.cs.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Arrays;

public class StudentRequestFeeRelaxingController {
    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;
    @FXML private TextField semesterTextField;
    @FXML private TextField yearTextField;
    @FXML private TextField notationTextField;
    @FXML private Label errorSemesterLabel;
    @FXML private Label errorYearLabel;

    private Request newRequest;
    private DataRepository dataRepository;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();


        if (FXRouter.getData() == null) {
            newRequest = new Request();
        } else {
            newRequest = (Request) FXRouter.getData();
        }

        requestTopicLabel.setText(newRequest.getRequestTopic());
        requestTypeLabel.setText(newRequest.getRequestType());
        notationTextField.setText(newRequest.getRequestNotation());

        errorSemesterLabel.setVisible(false);
        errorYearLabel.setVisible(false);

        String[] fullString = newRequest.getSpecialTopic().split("-");
        if (Arrays.stream(fullString).count() == 2){
        semesterTextField.setText(fullString[0]);
        yearTextField.setText(fullString[1]);
        }
    }

    @FXML
    public void onBackButtonClick() {
        dataRepository.writeData(RequestList.class);
        try {
            FXRouter.loadPage("student-request-choose-request-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onConfirmButtonClick() {
        newRequest.setSpecialReason(semesterTextField.getText() + "-" + yearTextField.getText());
        newRequest.setRequestNotation(notationTextField.getText());
        dataRepository.writeData(RequestList.class);

        boolean hasErrors = false;

        try{
            if (semesterTextField.getText().isEmpty() || semesterTextField.getText() == null){
                throw new IllegalArgumentException("Fill the From Field");
            }
            Integer.parseInt(semesterTextField.getText());
            errorSemesterLabel.setVisible(false);
        }catch (NumberFormatException e){
            errorSemesterLabel.setVisible(true);
            errorSemesterLabel.setText("Numbers Only");
            hasErrors = true;
        }catch (IllegalArgumentException e) {
            errorSemesterLabel.setVisible(true);
            errorSemesterLabel.setText(e.getMessage());
            hasErrors = true;
        }


        try {
            if (yearTextField.getText().isEmpty() || yearTextField.getText() == null) {
                throw new IllegalArgumentException("Fill the To Field");
            }
            Integer.parseInt(yearTextField.getText());
            errorYearLabel.setVisible(false);
        }catch (NumberFormatException e){
            errorYearLabel.setVisible(true);
            errorYearLabel.setText("Numbers Only");
            hasErrors = true;
        }catch (IllegalArgumentException e){
            errorYearLabel.setVisible(true);
            errorYearLabel.setText(e.getMessage());
            hasErrors = true;
        }

        if (!hasErrors){
            try {
                FXRouter.loadPage("student-request-student-info", newRequest,"student-request-fee-relaxing-page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
