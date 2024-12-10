package ku.cs.controllers.department;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.advisor.Advisor;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;

import java.io.IOException;
import java.util.Comparator;

public class DepartmentStudentController {

    @FXML private TableView<Student> departmentStudentTableView;
    @FXML private TextField studentId;
    @FXML private TextField studentFirstName;
    @FXML private TextField studentLastName;
    @FXML private TextField studentEmail;
    @FXML private TextField studentDepartment;
    @FXML private TextField searchTextField;
    @FXML private MenuButton advisorMenuButton;

    private UserList userList;
    private StudentList studentList;
    private DataRepository dataRepository;
    private FilteredList<Student> filteredStudentList;
    private SortedList<Student> sortedStudentList;
    private DepartmentStaff user;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();

        UserSession session = UserSession.getSession();
        user = (DepartmentStaff) session.getLoggedInUser();
        userList = dataRepository.getUserList();


        studentDepartment.setText(user.getDepartment());
        dataRepository.reloadData(StudentList.class);

        studentList = dataRepository.getStudentList();

        TableColumn<Student, Integer> studentIdCol = new TableColumn<>("รหัสนิสิต");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

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

        departmentStudentTableView.getColumns().clear();
        departmentStudentTableView.getColumns().addAll(studentIdCol, firstNameCol, lastNameCol, emailCol, departmentCol, advisorCol);

        initAdvisorMenuButton();

        showStudent();
        departmentStudentTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Student>() {

                    @Override
                    public void changed(ObservableValue<? extends Student> observableValue, Student student, Student newValue) {
                        if (newValue != null) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/department-student-edit-popup.fxml"));
                                    Parent root = loader.load();
                                    DepartmentStudentEditController controller = loader.getController();
                                    controller.setStudent(newValue);
                                    controller.setStudentList(studentList);

                                    Stage popupStage = new Stage();
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.setTitle("Student Editing");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.showAndWait();

                                    departmentStudentTableView.getSelectionModel().clearSelection();
                                    showStudent();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
        );

        //ขยาย Table View ให้เหมาะกับข้อความ
        departmentStudentTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = studentIdCol.getWidth() +
                    firstNameCol.getWidth() +
                    lastNameCol.getWidth() +
                    emailCol.getWidth() +
                    departmentCol.getWidth() +
                    advisorCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                departmentStudentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                departmentStudentTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    public void showStudent() {
        departmentStudentTableView.getItems().clear();
        filteredStudentList = new FilteredList<>(FXCollections.observableArrayList(
                studentList
                        .getStudentListByDepartment(user.getDepartment())
                        .getStudentList()), student -> true);

        sortedStudentList = new SortedList<>(filteredStudentList);
        sortedStudentList.setComparator(Comparator.comparing(Student::getStudentId));
        departmentStudentTableView.getItems().addAll(sortedStudentList);
    }

    public void initAdvisorMenuButton(){
        advisorMenuButton.getItems().clear();
        for (User user1 : userList.getUserList()) {
            if(user1.getRole().equals("Advisor")){
                Advisor advisor = (Advisor) user1;
                if (advisor.getDepartment().equals(user.getDepartment())) {
                    MenuItem menuItem = new MenuItem(advisor.getName());
                    menuItem.setOnAction(e -> handleAdvisorMenuButton(menuItem.getText()));
                    advisorMenuButton.getItems().add(menuItem);
                }

            }

        }
    }

    public void handleAdvisorMenuButton(String advisorName){
        advisorMenuButton.setText(advisorName);
    }


    @FXML
    public void onAddButtonClick(){
        String id = studentId.getText();
        String firstName = studentFirstName.getText();
        String lastName = studentLastName.getText();
        String email = studentEmail.getText();
        String advisor = advisorMenuButton.getText();

        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || advisor.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Student");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(studentList.findStudentById(id)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Student");
            alert.setContentText("รหัสนิสิตนี้ถูกใช้งานไปแล้ว");
            alert.showAndWait();
        } else{
            Student newStudent = new Student();
            newStudent.setStudentId(id);
            newStudent.setFirstName(firstName);
            newStudent.setLastName(lastName);
            newStudent.setEmail(email);
            newStudent.setDepartment(user.getDepartment());
            newStudent.setFaculty(user.getFaculty());
            newStudent.setAdvisor(advisor);
            studentList.addStudent(newStudent);
            dataRepository.writeData(StudentList.class);

            studentId.clear();
            studentFirstName.clear();
            studentLastName.clear();
            studentEmail.clear();
            advisorMenuButton.setText("ยังไม่เลือก");

            showStudent();
        }

    }
    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();

        filteredStudentList.setPredicate(student -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            return student.textToSearch().contains(searchText);
        });

        departmentStudentTableView.setItems(new SortedList<>(filteredStudentList));
    }

}
