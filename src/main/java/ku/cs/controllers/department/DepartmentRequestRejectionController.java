package ku.cs.controllers.department;

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

public class DepartmentRequestRejectionController {
    @FXML private TextField departmentReason;

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
            FXRouter.loadPage("department-request");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onSubmitButtonClick() {
        currentTime = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formattedDateTime = currentTime.format(formatter);

        request.setRequestStatus("คำร้องถูกปฏิเสธโดยหัวหน้าภาควิชา");
        request.setRequestTo("คำร้องดำเนินการครบถ้วน");
        request.setRequestLastedDated("คำร้องถูกปฏิเสธโดยหัวหน้าภาควิชา",formattedDateTime);
        request.setRequestRejectionReason(departmentReason.getText());
        if (departmentReason.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Reject reason");
            alert.setContentText("กรุณาใส่เหตุผลในการปฏิเสธ");
            alert.showAndWait();
        }else{
            dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("department-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}