package ku.cs.controllers.advisor;

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
import ku.cs.models.advisor.Advisor;
import ku.cs.models.request.RequestList;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Comparator;

public class AdvisorStudentController {

    @FXML private TableView<Student> advisorStudentTableView;
    @FXML private TextField searchTextField;

    private StudentList studentList;
    private DataRepository dataRepository;
    private FilteredList<Student> filteredStudents;
    private Advisor user;
    @FXML
    public void initialize() {
        UserSession session = UserSession.getSession();
        user = (Advisor) session.getLoggedInUser();
        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(StudentList.class);
        studentList = dataRepository.getStudentList();


        TableColumn<Student, Integer> indexCol = new TableColumn<>("รหัสนิสิต");
        indexCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, String> emailCol = new TableColumn<>("อีเมลล์");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> departmentCol = new TableColumn<>("ภาควิชา");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Student, String> advisorCol = new TableColumn<>("อาจารย์ที่ปรึกษา");
        advisorCol.setCellValueFactory(new PropertyValueFactory<>("advisor"));

        advisorStudentTableView.getColumns().clear();
        advisorStudentTableView.getColumns().addAll(indexCol, firstNameCol, lastNameCol, emailCol, departmentCol, advisorCol);
        showAdvisorStudent();
        advisorStudentTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue observable, Student oldValue, Student newValue) {
                if (newValue != null) {
                    try {
                        FXRouter.loadPage("advisor-student-request",newValue);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //ขยาย Table View ให้เหมาะกับข้อความ
        advisorStudentTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = indexCol.getWidth() +
                    firstNameCol.getWidth() +
                    lastNameCol.getWidth() +
                    emailCol.getWidth() +
                    departmentCol.getWidth() +
                    advisorCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                advisorStudentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                advisorStudentTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });

    }
    private void showAdvisorStudent() {
        filteredStudents = new FilteredList<>(FXCollections.observableArrayList(
                studentList
                        .getStudentsListByAdvisor(user.getName())
                        .getStudentList()), request -> true);

        SortedList<Student> sortedStudents = new SortedList<>(filteredStudents);
        sortedStudents.setComparator((Comparator.comparing(Student::getStudentId)));
        advisorStudentTableView.setItems(sortedStudents);
    }

    @FXML
    public void onReloadButtonClick(){
        dataRepository.reloadData(RequestList.class);
        studentList = dataRepository.getStudentList();
        showAdvisorStudent();
    }

    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredStudents.setPredicate(student -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            return student.textToSearch().contains(searchText.toLowerCase());
        });
    }
}
