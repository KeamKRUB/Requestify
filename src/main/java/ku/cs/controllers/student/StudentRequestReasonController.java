package ku.cs.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class StudentRequestReasonController {
    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;
    @FXML private TextField notationTextField;

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
    }

    @FXML
    public void onBackButtonClick(){
        dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("student-request-choose-request-page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
    @FXML
    public void onConfirmButtonClick(){
        newRequest.setRequestNotation(notationTextField.getText());
        dataRepository.writeData(RequestList.class);
        try {
            FXRouter.loadPage("student-request-student-info",newRequest,"student-request-reason-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
