package ku.cs.controllers.advisor;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ku.cs.models.request.Subject;
import ku.cs.models.request.Request;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class AdvisorStudentRequestDetailsController {
    private Request request = (Request) FXRouter.getData();
    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;
    @FXML private Label requestNotationLabel;
    @FXML private Label requestStatusLabel;
    @FXML private Label requestDateLabel;
    @FXML private TableView<Subject> requestSubjectTableView;
    @FXML private VBox mainLayout;

    @FXML
    public void initialize() {
        requestTopicLabel.setText(request.getRequestTopic());
        requestTypeLabel.setText(request.getRequestType());
        requestNotationLabel.setText(request.getRequestNotation());
        requestStatusLabel.setText(request.getRequestStatus());
        requestDateLabel.setText("(ล่าสุด) " + request.getRequestLastedDated());

        TableColumn<Subject, Integer> subjectIndexCol = new TableColumn<>("Index");
        subjectIndexCol.setCellValueFactory(new PropertyValueFactory<>("index"));

        TableColumn<Subject, String> subjectIdCol = new TableColumn<>("รหัสวิชา");
        subjectIdCol.setCellValueFactory(new PropertyValueFactory<>("subjectId"));

        TableColumn<Subject, String> subjectNameCol = new TableColumn<>("ชื่อวิชา");
        subjectNameCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<Subject, String> subjectEnrollTypeCol = new TableColumn<>("ประเภทการลงทะเบียน");
        subjectEnrollTypeCol.setCellValueFactory(new PropertyValueFactory<>("enrollType"));

        TableColumn<Subject, String> subjectSectionCol = new TableColumn<>("หมู่เรียน");
        TableColumn<Subject, String> lectureSectionCol = new TableColumn<>("บรรยาย");
        lectureSectionCol.setCellValueFactory(new PropertyValueFactory<>("lectureSection"));

        TableColumn<Subject, String> labSectionCol = new TableColumn<>("ปฏิบัติ");
        labSectionCol.setCellValueFactory(new PropertyValueFactory<>("labSection"));

        subjectSectionCol.getColumns().addAll(lectureSectionCol, labSectionCol);

        TableColumn<Subject, String> subjectCreditCol = new TableColumn<>("หน่วยกิต");
        TableColumn<Subject, String> lectureCreditCol = new TableColumn<>("บรรยาย");
        lectureCreditCol.setCellValueFactory(new PropertyValueFactory<>("lectureCredit"));

        TableColumn<Subject, String> labCreditCol = new TableColumn<>("ปฏิบัติ");
        labCreditCol.setCellValueFactory(new PropertyValueFactory<>("labCredit"));

        subjectCreditCol.getColumns().addAll(lectureCreditCol, labCreditCol);

        requestSubjectTableView.getColumns().clear();
        requestSubjectTableView.getColumns().addAll(
                subjectIndexCol,
                subjectIdCol,
                subjectNameCol,
                subjectEnrollTypeCol,
                subjectSectionCol,
                subjectCreditCol
        );

        requestSubjectTableView.getItems().addAll(request.getSubjectList());

        showRejectionReason();
    }
    @FXML
    public void onBackButtonClick() {
        try {
            FXRouter.loadPage("advisor-student-request");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void showRejectionReason(){
        prepareDetails(request, requestStatusLabel, mainLayout);
    }

    @FXML
    public void prepareDetails(Request request, Label requestStatusLabel, VBox mainLayout) {
        if (request.isReject()){
            setStyle(request, requestStatusLabel, mainLayout);
        }
        else {
            if (request.getRequestTo().equals("คำร้องดำเนินการครบถ้วน")) {
                requestStatusLabel.setStyle("-fx-text-fill: Green;");
            } else {
                requestStatusLabel.setStyle("-fx-text-fill: Orange;");
            }
        }
    }

    public void setStyle(Request request, Label requestStatusLabel, VBox mainLayout) {
        requestStatusLabel.setStyle("-fx-text-fill: red;");
        VBox VBoxTitle = new VBox();
        VBox VBoxLabel = new VBox();
        VBoxTitle.setPrefWidth(778);
        VBoxLabel.setPrefWidth(778);
        VBoxLabel.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5");

        Label rejectionLabel = new Label(request.getRequestRejectionReason());
        Label title = new Label("*เหตุผลการปฏิเสธ");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: red;");
        rejectionLabel.setWrapText(true);
        rejectionLabel.setPadding(new Insets(10, 10, 10, 10));

        VBoxTitle.getChildren().add(title);
        VBoxTitle.getChildren().add(VBoxLabel);
        VBoxLabel.getChildren().add(rejectionLabel);

        mainLayout.getChildren().add(VBoxTitle);
    }
}
