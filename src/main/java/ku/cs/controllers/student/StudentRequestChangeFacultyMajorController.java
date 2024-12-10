package ku.cs.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Arrays;

public class StudentRequestChangeFacultyMajorController {
    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;
    @FXML private TextField fromTextField;
    @FXML private TextField toTextField;
    @FXML private TextField notationTextField;
    @FXML private Label errorFromLabel;
    @FXML private Label errorToLabel;

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

        errorFromLabel.setVisible(false);
        errorToLabel.setVisible(false);

        String[] fullString = newRequest.getSpecialTopic().split("-");
        if (Arrays.stream(fullString).count() == 2){
            fromTextField.setText(fullString[0]);
            toTextField.setText(fullString[1]);
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
        newRequest.setSpecialReason(fromTextField.getText() + "-" + toTextField.getText());
        newRequest.setRequestNotation(notationTextField.getText());
        dataRepository.writeData(RequestList.class);

        boolean hasErrors = false;

        try{
            if (fromTextField.getText().isEmpty() || fromTextField.getText() == null){
                throw new IllegalArgumentException("Fill the From Field");
            }
            errorFromLabel.setVisible(false);
        }catch (IllegalArgumentException e){
            errorFromLabel.setVisible(true);
            errorFromLabel.setText(e.getMessage());
            hasErrors = true;
        }

        try {
            if (toTextField.getText().isEmpty() || toTextField.getText() == null){
                throw new IllegalArgumentException("Fill the To Field");
            }
            errorToLabel.setVisible(false);
        }catch (IllegalArgumentException e){
            errorToLabel.setVisible(true);
            errorToLabel.setText(e.getMessage());
            hasErrors = true;
        }
        if (!hasErrors){
            try {
                FXRouter.loadPage("student-request-student-info", newRequest,"student-request-change-faculty-major-page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
