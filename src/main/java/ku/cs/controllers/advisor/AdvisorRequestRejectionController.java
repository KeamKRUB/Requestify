package ku.cs.controllers.advisor;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.Optional;

public class AdvisorRequestRejectionController {
    @FXML private TextField advisorReason;

    private final Request request = (Request) FXRouter.getData();
    private DataRepository dataRepository;
    String formattedDateTime;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
    }

    @FXML
    public void onCancelButtonClick() {
        try {
            FXRouter.loadPage("advisor-request");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onSubmitButtonClick() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formattedDateTime = currentTime.format(formatter);

        request.setRequestStatus("ปฏิเสธโดยอาจารย์ที่ปรึกษา");
        request.setRequestTo("คำร้องดำเนินการครบถ้วน");
        request.setRequestLastedDated("ปฏิเสธโดยอาจารย์ที่ปรึกษา",formattedDateTime);
        request.setRequestRejectionReason(advisorReason.getText());
        if (advisorReason.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Reject reason");
            alert.setContentText("กรุณาใส่เหตุผลในการปฏิเสธ");
            alert.showAndWait();
        }else{
            dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("advisor-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
