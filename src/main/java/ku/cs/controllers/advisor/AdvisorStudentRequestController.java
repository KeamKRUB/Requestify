package ku.cs.controllers.advisor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.student.Student;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdvisorStudentRequestController {
    @FXML private TableView<Request> advisorStudentRequestTableView;
    @FXML private Label studentIdLabel;
    @FXML private Label studentNameLabel;
    @FXML private TextField searchTextField;

    private RequestList advisorRequestList;
    private FilteredList<Request> filteredRequests;
    private SortedList<Request> sortedRequests;
    private DataRepository dataRepository;
    private Student student = (Student) FXRouter.getData();

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(RequestList.class);
        advisorRequestList = dataRepository.getRequestList();
        studentIdLabel.setText(student.getStudentId());
        studentNameLabel.setText(student.getName());
        TableColumn<Request, Integer> requestDateCol = new TableColumn<>("วันที่");
        requestDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<Request, String> requestTopicCol = new TableColumn<>("คำร้อง");
        requestTopicCol.setCellValueFactory(new PropertyValueFactory<>("requestTopic"));

        TableColumn<Request, String> requestTypeCol = new TableColumn<>("วัตถุประสงค์");
        requestTypeCol.setCellValueFactory(new PropertyValueFactory<>("requestType"));

        TableColumn<Request, String> requestToCol = new TableColumn<>("ส่งถึง");
        requestToCol.setCellValueFactory(new PropertyValueFactory<>("requestTo"));

        TableColumn<Request, String> requestStatusCol = new TableColumn<>("สถานะ");
        requestStatusCol.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));

        advisorStudentRequestTableView.getColumns().clear();
        advisorStudentRequestTableView.getColumns().addAll(requestDateCol, requestTopicCol, requestTypeCol, requestToCol, requestStatusCol);

        showAdvisorRequest();

        advisorStudentRequestTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Request>() {
            @Override
            public void changed(ObservableValue observable, Request oldValue, Request newValue) {
                if (newValue != null) {
                    switch (newValue.getRequestType()) {
                        case "ผ่อนผันค่าธรรมเนียมการศึกษา":
                            try {
                                FXRouter.loadPage("student-request-details-fee-relaxing", newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ลงทะเบียนเรียนเกิน 22 หน่วยกิต":
                        case "ลงทะเบียนเรียนล่าช้า", "ถอนรายวิชาล่าช้า (Drop)", "เพิ่มรายวิชาล่าช้า (Add)":
                            try {
                                FXRouter.loadPage("student-request-details-ku-one-three", newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ลงทะเบียนต่ำกว่า 9 หน่วยกิต", "คำร้องทั่วไป":
                            try {
                                FXRouter.loadPage("student-request-details-reason", newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ย้ายคณะ หรือเปลี่ยนวิชาเอก":
                            try {
                                FXRouter.loadPage("student-request-details-change-faculty-major", newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "ลาพักการศึกษา":
                            try {
                                FXRouter.loadPage("student-request-details-leave", newValue);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        default:
                            break;

                    }
                }
            }
        });

        //ขยาย Table View ให้เหมาะกับข้อความ
        advisorStudentRequestTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = requestDateCol.getWidth() +
                    requestTopicCol.getWidth() +
                    requestTypeCol.getWidth() +
                    requestToCol.getWidth() +
                    requestStatusCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                advisorStudentRequestTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                advisorStudentRequestTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    private void showAdvisorRequest() {
        filteredRequests = new FilteredList<>(FXCollections.observableArrayList(
                advisorRequestList
                        .getRequestByStudentId(student.getStudentId())
                        .getRequests()), request -> true);
        sortedRequests = new SortedList<>(filteredRequests);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sortedRequests.setComparator((request1, request2) -> {
            LocalDateTime date1 = LocalDateTime.parse(request1.getRequestLastedDated(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(request2.getRequestLastedDated(), formatter);
            return date2.compareTo(date1); // เรียงจากมากไปน้อย (วันที่ล่าสุดก่อน)
        });

        advisorStudentRequestTableView.setItems(sortedRequests);
    }

    @FXML
    public void onReloadButtonClick(){
        dataRepository.reloadData(RequestList.class);
        advisorRequestList = dataRepository.getRequestList();
        showAdvisorRequest();
    }

    @FXML
    public void onSearchButtonClick(){
        String searchText = searchTextField.getText().toLowerCase();
        filteredRequests.setPredicate(request -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return  request.toString().toLowerCase().contains(searchText);
        });
    }

    @FXML
    public void onBackButtonClick(){
        try {
            FXRouter.loadPage("advisor-student");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
