package ku.cs.controllers.student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.request.SubjectList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import ku.cs.services.PdfUtil;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StudentRequestConfirmationController {
    private Request newRequest = (Request) FXRouter.getData();
    private DataRepository dataRepository;
    private SubjectList subjectList;

    private RequestList requestList;
    private LocalDateTime currentTime;
    private DateTimeFormatter formatter;
    String formattedDateTime;

    @FXML Label requestLabel;
    @FXML Label requestOptionLabel;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        requestList = dataRepository.getRequestList();
        subjectList = dataRepository.getSubjectList();

        requestOptionLabel.setText(newRequest.getRequestType());
        requestLabel.setText(newRequest.getRequestTopic());
    }

    @FXML
    public void onBackButtonClick(){
        try {
            FXRouter.loadPage("student-request-student-info",newRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onConfirmButtonClick() throws FileNotFoundException {
        currentTime = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formattedDateTime = currentTime.format(formatter);

        newRequest.setRequestTo("คำร้องส่งต่อให้อาจารย์ที่ปรึกษา");
        newRequest.setRequestStatus("ใบคำร้องใหม่");
        newRequest.setRequestDate(currentTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        newRequest.setRequestLastedDated("ใบคำร้องใหม่",formattedDateTime);
        requestList.addRequest(newRequest);
        subjectList.addAllSubject(newRequest.getSubjectList().getSubjects());

        try {
            PdfUtil requestPdf = new PdfUtil(newRequest);
            requestPdf.createRequestPDF();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dataRepository.writeData(SubjectList.class);
        dataRepository.writeData(RequestList.class);
        try {
            FXRouter.loadPage("student-request");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}