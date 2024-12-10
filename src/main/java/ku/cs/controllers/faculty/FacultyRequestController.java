package ku.cs.controllers.faculty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FacultyRequestController{

    @FXML private TableView<Request> facultyRequestTableView;
    @FXML private TextField searchTextField;

    private FilteredList<Request> filteredRequests;
    private SortedList<Request> sortedRequests;
    private RequestList facultyRequestList;
    private FacultyStaff user;

    private DataRepository dataRepository;

    @FXML
    public void initialize() {
        user = (FacultyStaff) UserSession.getSession().getLoggedInUser();

        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(RequestList.class);

        facultyRequestList = dataRepository.getRequestList();

        TableColumn<Request, Integer> requestDateCol = new TableColumn<>("วันที่");
        requestDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<Request, String> studentIdCol = new TableColumn<>("รหัสนิสิต");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Request, String> studentNameCol = new TableColumn<>("ชื่อ-นามสกุล");
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<Request, String> requestTopicCol = new TableColumn<>("หัวข้อ");
        requestTopicCol.setCellValueFactory(new PropertyValueFactory<>("requestTopic"));

        TableColumn<Request, String> requestTypeCol = new TableColumn<>("ประเภท");
        requestTypeCol.setCellValueFactory(new PropertyValueFactory<>("requestType"));

        facultyRequestTableView.getColumns().clear();
        facultyRequestTableView.getColumns().addAll(requestDateCol, studentIdCol, studentNameCol, requestTopicCol, requestTypeCol);

        showFacultyRequest();

        facultyRequestTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Request>() {
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
                        case "ลงทะเบียนต่ำกว่า 9 หน่วยกิต","คำร้องทั่วไป":
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
                        default:
                            break;

                    }
                }
            }
        });

        //ขยาย Table View ให้เหมาะกับข้อความ
        facultyRequestTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = requestDateCol.getWidth() +
                    studentIdCol.getWidth() +
                    studentNameCol.getWidth() +
                    requestTopicCol.getWidth() +
                    requestTypeCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                facultyRequestTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                facultyRequestTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    private void showFacultyRequest() {
        filteredRequests = new FilteredList<>(FXCollections.observableArrayList(
                facultyRequestList
                        .getRequestByFaculty(user.getFaculty())
                        .getRequestsByRequestTo("คำร้องส่งต่อให้คณบดี")
                        .getRequests()), request -> true);

        sortedRequests = new SortedList<>(filteredRequests);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sortedRequests.setComparator((request1, request2) -> {
            LocalDateTime date1 = LocalDateTime.parse(request1.getRequestLastedDated(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(request2.getRequestLastedDated(), formatter);
            return date2.compareTo(date1);
        });

        facultyRequestTableView.setItems(sortedRequests);
    }

    @FXML
    public void onReloadButtonClick(){
        dataRepository.reloadData(RequestList.class);
        facultyRequestList = dataRepository.getRequestList();
        showFacultyRequest();
    }

    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredRequests.setPredicate(request -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return  request.staffTextToSearch().contains(searchText);
        });
    }

}
