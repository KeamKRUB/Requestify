package ku.cs.controllers.student;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StudentRequestController {
    @FXML private TableView<Request> studentRequestTableView;
    @FXML private Label nameTextLabel;
    @FXML private TextField searchTextField;
    @FXML private MenuButton statusMenuButton;

    private RequestList studentRequests;
    private DataRepository dataRepository;
    private FilteredList<Request> filteredStudents;
    private SortedList<Request> sortedStudents;
    private Student user;
    @FXML private HBox testHBox;
    @FXML
    public void initialize() {
        UserSession session = UserSession.getSession();
        user = (Student) session.getLoggedInUser();
        nameTextLabel.setText(user.getName());

        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(StudentList.class);
        studentRequests = dataRepository.getRequestList();

        TableColumn<Request, Integer> requestDateCol = new TableColumn<>("วันที่สร้าง");
        requestDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<Request, String> requestTopicCol = new TableColumn<>("คำร้อง");
        requestTopicCol.setCellValueFactory(new PropertyValueFactory<>("requestTopic"));

        TableColumn<Request, String> requestTypeCol = new TableColumn<>("วัตถุประสงค์");
        requestTypeCol.setCellValueFactory(new PropertyValueFactory<>("requestType"));

        TableColumn<Request, String> requestToCol = new TableColumn<>("ผู้รับผิดชอบต่อไป");
        requestToCol.setCellValueFactory(new PropertyValueFactory<>("requestTo"));

        TableColumn<Request, String> requestStatusCol = new TableColumn<>("สถานะ");
        requestStatusCol.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));

        requestDateCol.setReorderable(false);
        requestTopicCol.setReorderable(false);
        requestTypeCol.setReorderable(false);
        requestToCol.setReorderable(false);
        requestStatusCol.setReorderable(false);

        studentRequestTableView.getColumns().clear();
        studentRequestTableView.getColumns().addAll(requestDateCol, requestTopicCol, requestTypeCol, requestToCol, requestStatusCol);

        setupStatusMenu();
        showStudentRequestList();

        studentRequestTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Request>() {
            @Override
            public void changed(ObservableValue observable, Request oldValue, Request newValue) {
                if (newValue != null) {
                    switch (newValue.getRequestType()) {
                        case "ผ่อนผันค่าธรรมเนียมการศึกษา":
                            try {
                                FXRouter.loadPage("student-request-details-fee-relaxing",newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ลงทะเบียนเรียนเกิน 22 หน่วยกิต":
                        case "ลงทะเบียนเรียนล่าช้า", "ถอนรายวิชาล่าช้า (Drop)", "เพิ่มรายวิชาล่าช้า (Add)":
                            try {
                                FXRouter.loadPage("student-request-details-ku-one-three",newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ลงทะเบียนต่ำกว่า 9 หน่วยกิต":
                            try {
                                FXRouter.loadPage("student-request-details-reason",newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ย้ายคณะ หรือเปลี่ยนวิชาเอก":
                            try {
                                FXRouter.loadPage("student-request-details-change-faculty-major",newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ลาพักการศึกษา":
                            try {
                                FXRouter.loadPage("student-request-details-leave",newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "คำร้องทั่วไป":
                            try {
                                FXRouter.loadPage("student-request-details-reason",newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        default:
                            break;
                    }
                }
            }
        });

        //ขยาย Table View ให้เหมาะกับข้อความ
        studentRequestTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = requestDateCol.getWidth() +
                    requestTopicCol.getWidth() +
                    requestTypeCol.getWidth() +
                    requestToCol.getWidth() +
                    requestStatusCol.getWidth();
            if (totalColumnsWidth < tableWidth) {
                studentRequestTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                studentRequestTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    @FXML
    public void onAddButtonClick(){
        try {
            user.hasAdvisor();
            FXRouter.loadPage("student-request-choose-request-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            showAlert("เกิดข้อผิดพลาด",e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showStudentRequestList(){
        filteredStudents = new FilteredList<>(FXCollections.observableArrayList(
                studentRequests
                        .getRequestByStudentId(user.getStudentId())
                        .getRequests()), request -> true);
        sortedStudents = new SortedList<>(filteredStudents);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sortedStudents.setComparator((request1, request2) -> {
            LocalDateTime date1 = LocalDateTime.parse(request1.getRequestLastedDated(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(request2.getRequestLastedDated(), formatter);
            return date2.compareTo(date1); // เรียงจากมากไปน้อย (วันที่ล่าสุดก่อน)
        });

        studentRequestTableView.setItems(sortedStudents);
    }

    @FXML
    public void onSearchButtonClick(){
        String searchText = searchTextField.getText().toLowerCase();
        filteredStudents.setPredicate(request -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return  request.textToSearch().contains(searchText);
        });
    }

    @FXML
    public void onReloadButtonClick(){
        dataRepository.reloadData(RequestList.class);
        studentRequests = dataRepository.getRequestList();
        showStudentRequestList();
    }

    private void setupStatusMenu() {
        statusMenuButton.getItems().clear();

        MenuItem allItem = new MenuItem("ทั้งหมด");
        MenuItem approvedByAdvisor = new MenuItem("อนุมัติโดยอาจารย์ที่ปรึกษา");
        MenuItem approvedByDepartment = new MenuItem("อนุมัติโดยหัวหน้าภาควิชา");
        MenuItem approvedByFaculty = new MenuItem("อนุมัติโดยคณบดี");
        MenuItem rejectedByAdvisor = new MenuItem("ปฏิเสธโดยอาจารย์ที่ปรึกษา");
        MenuItem rejectedByDepartment = new MenuItem("ปฏิเสธโดยหัวหน้าภาควิชา");
        MenuItem rejectedByFaculty = new MenuItem("ปฏิเสธโดยคณบดี");
        MenuItem newRequest = new MenuItem("ใบคำร้องใหม่");

        allItem.setOnAction(event -> filterTableByStatus("ทั้งหมด"));
        approvedByAdvisor.setOnAction(event -> filterTableByStatus("อนุมัติโดยอาจารย์ที่ปรึกษา"));
        approvedByDepartment.setOnAction(event -> filterTableByStatus("อนุมัติโดยหัวหน้าภาควิชา"));
        approvedByFaculty.setOnAction(event -> filterTableByStatus("อนุมัติโดยคณบดี"));
        rejectedByAdvisor.setOnAction(event -> filterTableByStatus("ปฏิเสธโดยอาจารย์ที่ปรึกษา"));
        rejectedByDepartment.setOnAction(event -> filterTableByStatus("ปฏิเสธโดยหัวหน้าภาควิชา"));
        rejectedByFaculty.setOnAction(event -> filterTableByStatus("ปฏิเสธโดยคณบดี"));
        newRequest.setOnAction(even -> filterTableByStatus("ใบคำร้องใหม่"));

        statusMenuButton.getItems().addAll(allItem,
                approvedByAdvisor,
                approvedByDepartment,
                approvedByFaculty,
                rejectedByAdvisor,
                rejectedByDepartment,
                rejectedByFaculty,
                newRequest);
    }

    private void filterTableByStatus(String status) {
        statusMenuButton.setText(status);
        ArrayList<Request> allRequests = studentRequests.getRequestByStudentId(user.getStudentId()).getRequests();
        filteredStudents = new FilteredList<>(FXCollections.observableArrayList(allRequests), request -> {
            if (status.equals("ทั้งหมด")) {
                return true; // แสดงทุกสถานะ
            } else {
                return request.getRequestStatus().equals(status); // กรองตามสถานะที่เลือก
            }
        });

        sortedStudents = new SortedList<>(filteredStudents);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sortedStudents.setComparator((request1, request2) -> {
            LocalDateTime date1 = LocalDateTime.parse(request1.getRequestLastedDated(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(request2.getRequestLastedDated(), formatter);
            return date2.compareTo(date1); // เรียงจากมากไปน้อย (วันที่ล่าสุดก่อน)
        });

        studentRequestTableView.setItems(sortedStudents);
    }

}
