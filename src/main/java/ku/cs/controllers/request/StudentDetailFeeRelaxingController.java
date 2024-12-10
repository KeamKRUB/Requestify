package ku.cs.controllers.request;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.advisor.Advisor;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import ku.cs.services.PdfUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class StudentDetailFeeRelaxingController {
    private Request request = (Request) FXRouter.getData();
    private User user;
    private DataRepository dataRepository;

    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;
    @FXML private Label requestStatusLabel;
    @FXML private Label requestNotationLabel;
    @FXML private Label yearLabel;
    @FXML private Label semesterLabel;
    @FXML private Label requestToLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label studentAcademyLabel;
    @FXML private Label studentFacultyLabel;
    @FXML private Label studentDepartmentLabel;
    @FXML private Label studentPhoneNumberLabel;
    @FXML private Label studentEmailLabel;

    @FXML private VBox mainLayout;
    @FXML private BorderPane confirmationField;
    @FXML private HBox rightConfirmationField;
    @FXML private VBox timeStampVbox;

    @FXML private Button backButton;
    @FXML private Button approveButton;
    @FXML private Button sentButton;
    @FXML private Button rejectButton;
    @FXML private Button cancelButton;

    private LocalDateTime currentTime;
    private DateTimeFormatter formatter;
    String formattedDateTime;

    @FXML
    public void initialize() {
        UserSession userSession = UserSession.getSession();
        user = userSession.getLoggedInUser();

        dataRepository = DataRepository.getDataRepository();

        handleConfirmationButton(user);

        String[] fullText = request.getSpecialTopic().split("-");

        requestTopicLabel.setText(request.getRequestTopic());
        requestTypeLabel.setText(request.getRequestType());
        requestNotationLabel.setText(request.getRequestNotation());
        semesterLabel.setText(fullText[0]);
        yearLabel.setText(fullText[1]);
        requestStatusLabel.setText(request.getRequestTo());

        requestToLabel.setText(request.getStudentAdvisor());
        studentNameLabel.setText(request.getStudentName());
        studentIdLabel.setText(request.getStudentId());
        studentAcademyLabel.setText(request.getStudentAcademic());
        studentFacultyLabel.setText(request.getStudentFaculty());
        studentDepartmentLabel.setText(request.getStudentDepartment());
        studentPhoneNumberLabel.setText(request.getStudentPhoneNumber());
        studentEmailLabel.setText(request.getStudentEmail());

        showDetail();
        showTimeStamp();
    }

    private void handleConfirmationButton(User user) {
        cancelButton.setOnAction(e -> handleCancelButtonAction(user));
        if(user instanceof Advisor) {
            backButton.setVisible(false);
            if(request.getRequestStatus().equals("ใบคำร้องใหม่")) {
                rightConfirmationField.getChildren().remove(approveButton);

                sentButton.setOnAction(e -> handleSentButtonAction(user));
                rejectButton.setOnAction(e -> handleRejectButtonAction(user));
            }
        } else if(user instanceof DepartmentStaff) {
            backButton.setVisible(false);

            approveButton.setOnAction(e -> handleApproveButtonAction(user));
            sentButton.setOnAction(e -> handleSentButtonAction(user));
            rejectButton.setOnAction(e -> handleRejectButtonAction(user));
        } else if (user instanceof FacultyStaff) {
            backButton.setVisible(false);
            rightConfirmationField.getChildren().remove(sentButton);

            approveButton.setOnAction(e -> handleApproveButtonAction(user));
            rejectButton.setOnAction(e -> handleRejectButtonAction(user));
        } else {
            mainLayout.getChildren().remove(confirmationField);
        }
    }

    private void handleSentButtonAction(User user) {
        currentTime = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formattedDateTime = currentTime.format(formatter);
        if (user instanceof Advisor && request.getRequestStatus().equals("ใบคำร้องใหม่")) {
            request.setRequestStatus("อนุมัติโดยอาจารย์ที่ปรึกษา");
            request.setRequestTo("คำร้องส่งต่อให้หัวหน้าภาควิชา");
            request.setRequestLastedDated("อนุมัติโดยอาจารย์ที่ปรึกษา",formattedDateTime);
            requestToLabel.setText("หัวหน้าภาควิชา"+request.getStudentDepartment());
            try {
                PdfUtil requestPdf = new PdfUtil(request);
                requestPdf.createRequestPDF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            dataRepository.writeData(RequestList.class);
            try {
                FXRouter.loadPage("advisor-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (user instanceof DepartmentStaff) {
            request.setRequestStatus("อนุมัติโดยหัวหน้าภาควิชา");
            request.setRequestTo("คำร้องส่งต่อให้คณบดี");
            request.setRequestLastedDated("อนุมัติโดยหัวหน้าภาควิชา", formattedDateTime);
            requestToLabel.setText("คณบดีคณะ"+request.getStudentFaculty());
            try {
                FXRouter.loadPage("department-request-confirmation-sent-certifier",request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleApproveButtonAction(User user) {
        currentTime = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formattedDateTime = currentTime.format(formatter);
        if(user instanceof DepartmentStaff) {
            request.setRequestStatus("อนุมัติโดยหัวหน้าภาควิชา");
            request.setRequestTo("คำร้องดำเนินการครบถ้วน");
            request.setRequestLastedDated("อนุมัติโดยหัวหน้าภาควิชา",formattedDateTime);
            try {
                FXRouter.loadPage("department-request-confirmation-accept-certifier",request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (user instanceof FacultyStaff) {
            request.setRequestStatus("อนุมัติโดยคณบดี");
            request.setRequestTo("คำร้องดำเนินการครบถ้วน");
            request.setRequestLastedDated("คำร้องดำเนินการครบถ้วน",formattedDateTime);
            try {
                FXRouter.loadPage("faculty-request-confirmation-certifier",request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleRejectButtonAction(User user) {
        if(user instanceof Advisor && request.getRequestStatus().equals("ใบคำร้องใหม่")) {
            try {
                FXRouter.loadPage("advisor-request-rejection",request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(user instanceof DepartmentStaff) {
            try {
                FXRouter.loadPage("department-request-rejection",request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(user instanceof FacultyStaff) {
            try {
                FXRouter.loadPage("faculty-request-rejection",request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleCancelButtonAction(User user) {
        if(user instanceof Advisor && request.getRequestStatus().equals("ใบคำร้องใหม่")) {
            try {
                FXRouter.loadPage("advisor-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(user instanceof DepartmentStaff) {
            try {
                FXRouter.loadPage("department-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(user instanceof FacultyStaff) {
            try {
                FXRouter.loadPage("faculty-request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void onBackButtonClick(){
        try {
            FXRouter.loadPage("student-request");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void showDetail(){
        if (request.isReject()){
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
        else {
            if (request.getRequestTo().equals("คำร้องดำเนินการครบถ้วน")) {
                requestStatusLabel.setStyle("-fx-text-fill: Green;");
            } else {
                requestStatusLabel.setStyle("-fx-text-fill: Orange;");
            }
        }
    }

    //แสดงเวลาที่คำร้องถูกดำเนินการ
    private void showTimeStamp() {
        ArrayList<Map.Entry<String, String>> timeStampEntries = new ArrayList<>(request.getTimeStampMap().entrySet());
        timeStampVbox.getChildren().clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        timeStampEntries.sort((entry1, entry2) -> {
            LocalDateTime date1 = LocalDateTime.parse(entry1.getValue(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(entry2.getValue(), formatter);
            return date1.compareTo(date2);
        });

        for (int i = 0; i < timeStampEntries.size(); i++) {
            Map.Entry<String, String> entry = timeStampEntries.get(i);
            HBox entryBox = new HBox(10);

            Label keyLabel = new Label(entry.getKey() + ": ");
            Label valueLabel = new Label(entry.getValue());

            if (i == timeStampEntries.size() - 1) {
                keyLabel.setText("(ล่าสุด) " + entry.getKey());            }

            entryBox.getChildren().addAll(keyLabel, valueLabel);
            timeStampVbox.getChildren().add(entryBox);
        }
    }

    @FXML
    public void onShowPdfButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/student-request-pdf-popup.fxml"));
        Parent root = loader.load();
        PdfPopupController controller = loader.getController();
        String pdfPath = "data/requests/requestNo_" + request.getRequestId() + ".pdf";
        controller.setRequestPath(pdfPath);
        try {
            File pdfFile = new File(pdfPath);
            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 120);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                ImageView pdfImageView = new ImageView();
                pdfImageView.setFitWidth(1000);
                pdfImageView.setImage(image);
                controller.addImageView(pdfImageView);
            }

            document.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Pdf Viewer");
            popupStage.setScene(new Scene(root));

            popupStage.showAndWait();
        });
    }

}
