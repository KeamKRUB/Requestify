package ku.cs.controllers.student;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class StudentRequestChooseRequestController {
    @FXML private MenuButton requestTopicMenuButton;
    @FXML private MenuButton requestTypeMenuButton;

    private Request newRequest;
    private RequestList requestList;
    private DataRepository dataRepository;


    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        requestList = dataRepository.getRequestList();

        newRequest = new Request();
        newRequest.setRequestId(String.valueOf(requestList.getSize()+1));
    }

    @FXML
    private void handleGeneralRequest() {
        requestTypeMenuButton.setText("คำร้องทั่วไป");
        newRequest.setRequestType("คำร้องทั่วไป");
        requestTopicMenuButton.setText("คำร้องทั่วไป");
        newRequest.setRequestTopic("คำร้องทั่วไป");
        updateMenuForGeneralRequest();
    }

    @FXML
    private void handleRegistrationRequest() {
        requestTypeMenuButton.setText("โปรดเลือกความประสงค์");
        newRequest.setRequestType("");
        requestTopicMenuButton.setText("คำร้องขอลงทะเบียนเรียน");
        newRequest.setRequestTopic("คำร้องขอลงทะเบียนเรียน");
        updateMenuForRegistration();
    }

    @FXML
    private void handleLeaveOfAbsenceRequest(){
        requestTypeMenuButton.setText("ลาพักการศึกษา");
        newRequest.setRequestType("ลาพักการศึกษา");
        requestTopicMenuButton.setText("คำร้องลาพักการศึกษา");
        newRequest.setRequestTopic("คำร้องลาพักการศึกษา");
        updateMenuForLeaveOfAbsenceRequest();
    }

    private void updateMenuForLeaveOfAbsenceRequest(){
        requestTypeMenuButton.setDisable(true);
    }

    private void updateMenuForGeneralRequest() {
        requestTypeMenuButton.setDisable(true);
    }

    private void updateMenuForRegistration() {
        requestTypeMenuButton.getItems().clear();
        requestTypeMenuButton.setDisable(false);

        MenuItem lateRegistration = new MenuItem("ลงทะเบียนเรียนล่าช้า");
        MenuItem overRegistration = new MenuItem("ลงทะเบียนเรียนเกิน 22 หน่วยกิต");
        MenuItem feeRelaxation = new MenuItem("ผ่อนผันค่าธรรมเนียมการศึกษา");
        MenuItem lessThanNine = new MenuItem("ลงทะเบียนต่ำกว่า 9 หน่วยกิต");
        MenuItem changeFacultyMajor = new MenuItem("ย้ายคณะ หรือเปลี่ยนวิชาเอก");
        MenuItem addLate = new MenuItem("เพิ่มรายวิชาล่าช้า (Add)");
        MenuItem dropLate = new MenuItem("ถอนรายวิชาล่าช้า (Drop)");

        lateRegistration.setOnAction(e -> handleOptionSelection("ลงทะเบียนเรียนล่าช้า"));
        overRegistration.setOnAction(e -> handleOptionSelection("ลงทะเบียนเรียนเกิน 22 หน่วยกิต"));
        feeRelaxation.setOnAction(e -> handleOptionSelection("ผ่อนผันค่าธรรมเนียมการศึกษา"));
        lessThanNine.setOnAction(e -> handleOptionSelection("ลงทะเบียนต่ำกว่า 9 หน่วยกิต"));
        changeFacultyMajor.setOnAction(e -> handleOptionSelection("ย้ายคณะ หรือเปลี่ยนวิชาเอก"));
        addLate.setOnAction(e -> handleOptionSelection("เพิ่มรายวิชาล่าช้า (Add)"));
        dropLate.setOnAction(e -> handleOptionSelection("ถอนรายวิชาล่าช้า (Drop)"));

        requestTypeMenuButton.getItems().addAll(lateRegistration, overRegistration, feeRelaxation,lessThanNine,changeFacultyMajor,addLate,dropLate);
    }

    private void handleOptionSelection(String option) {
        newRequest.setRequestType(option);
        requestTypeMenuButton.setText(option);
    }
    @FXML
    public void onConfirmButtonClick(){
        switch (newRequest.getRequestType()) {
            case "ผ่อนผันค่าธรรมเนียมการศึกษา":
                try {
                    FXRouter.loadPage("student-request-fee-relaxing-page",newRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "ลงทะเบียนเรียนเกิน 22 หน่วยกิต", "ลงทะเบียนเรียนล่าช้า", "ถอนรายวิชาล่าช้า (Drop)",
                 "เพิ่มรายวิชาล่าช้า (Add)":
                try {
                    FXRouter.loadPage("student-request-ku-one-three-page",newRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "ลงทะเบียนต่ำกว่า 9 หน่วยกิต","คำร้องทั่วไป":
                try {
                    FXRouter.loadPage("student-request-reason-page",newRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "ย้ายคณะ หรือเปลี่ยนวิชาเอก":
                try {
                    FXRouter.loadPage("student-request-change-faculty-major-page",newRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "ลาพักการศึกษา":
                try {
                    FXRouter.loadPage("student-request-leave-page",newRequest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a request...");
                alert.setContentText("โปรดเลือกใบคำร้อง");
                alert.showAndWait();
                break;
        }

    }
}
