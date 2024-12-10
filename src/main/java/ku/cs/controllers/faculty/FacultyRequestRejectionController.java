package ku.cs.controllers.faculty;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

public class FacultyRequestRejectionController {
    @FXML private TextField facultyReason;

    private Request request = (Request) FXRouter.getData();
    private DataRepository dataRepository;
    private LocalDateTime currentTime;
    private DateTimeFormatter formatter;
    String formattedDateTime;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
    }

    @FXML
    public void onCancelButtonClick() {
        try {
            FXRouter.loadPage("faculty-request");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onSubmitButtonClick() {
        currentTime = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formattedDateTime = currentTime.format(formatter);

        request.setRequestStatus("คำร้องถูกปฏิเสธโดยคณบดี");
        request.setRequestTo("คำร้องดำเนินการครบถ้วน");
        request.setRequestLastedDated("คำร้องถูกปฏิเสธโดยคณบดี",formattedDateTime);
        request.setRequestRejectionReason(facultyReason.getText());
        if (facultyReason.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Reject reason");
            alert.setContentText("กรุณาใส่เหตุผลในการปฏิเสธ");
            alert.showAndWait();
        }else{
            dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("faculty-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
