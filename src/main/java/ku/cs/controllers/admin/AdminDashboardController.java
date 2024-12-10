package ku.cs.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import ku.cs.models.advisor.AdvisorList;
import ku.cs.models.department.Department;
import ku.cs.models.department.DepartmentList;
import ku.cs.models.department.DepartmentStaffList;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.models.faculty.FacultyStaffList;
import ku.cs.models.request.RequestList;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.Comparator;
import java.util.List;

public class AdminDashboardController {

    @FXML private Label totalRequestLabel;
    @FXML private Label continueRequestLabel;
    @FXML private Label approveRequestLabel;
    @FXML private Label rejectRequestLabel;
    @FXML private Label totalRequestLabelByFaculty;
    @FXML private Label confirmedRequestDepartment;
    @FXML private Label confirmedRequestFaculty;

    @FXML private Label totalUserLabel;
    @FXML private Label totalStudentLabel;
    @FXML private Label totalAdvisorLabel;
    @FXML private Label totalDepartmentStaffLabel;
    @FXML private Label totalFacultyStaffLabel;

    @FXML private MenuButton facultyMenuButton;
    @FXML private MenuButton departmentMenuButton;
    @FXML private MenuButton facultyUserMenuButton;
    @FXML private MenuButton departmentUserMenuButton;

    private FacultyList facultyList;
    private DepartmentList departmentList;
    private RequestList requestList;
    private UserList userList;
    private StudentList studentList;
    private AdvisorList advisorList;
    private DepartmentStaffList departmentStaffList;
    private FacultyStaffList facultyStaffList;

    public void initialize() {
        DataRepository dataRepository = DataRepository.getDataRepository();
        requestList = dataRepository.getRequestList();
        userList = dataRepository.getUserList();
        studentList = dataRepository.getStudentList();
        departmentStaffList = dataRepository.getDepartmentStaffList();
        facultyStaffList = dataRepository.getFacultyStaffList();
        advisorList = dataRepository.getAdvisorList();

        facultyList = dataRepository.getFacultyList();
        departmentList = dataRepository.getDepartmentList();

        initFacultyMenuButton();
        initFacultyUserMenuButton();

        totalRequestLabel.setText(String.valueOf(requestList.getTotalRequests()));
        continueRequestLabel.setText(String.valueOf(requestList.getTotalRequestsByRequestTo("ส่งต่อ")));
        approveRequestLabel.setText(String.valueOf(requestList.getRequestsByRequestTo("คำร้องดำเนินการครบถ้วน").getTotalRequestsByStatus("อนุมัติ")));
        rejectRequestLabel.setText(String.valueOf(requestList.getTotalRequestsByStatus("ปฏิเสธ")));

        totalStudentLabel.setText(String.valueOf(studentList.getRegisteredStudents().getStudentList().size()));
        totalDepartmentStaffLabel.setText(String.valueOf(departmentStaffList.getDepartmentStaffList().size()));
        totalFacultyStaffLabel.setText(String.valueOf(facultyStaffList.getFacultyStaffList().size()));
        totalAdvisorLabel.setText(String.valueOf(advisorList.getAdvisorList().size()));
        totalUserLabel.setText(String.valueOf(
                Integer.parseInt(totalStudentLabel.getText()) +
                        Integer.parseInt(totalDepartmentStaffLabel.getText()) +
                        Integer.parseInt(totalAdvisorLabel.getText()) +
                        Integer.parseInt(totalFacultyStaffLabel.getText())
        ));
    }

    private void initFacultyMenuButton() {
        facultyMenuButton.getItems().clear();
        List<Faculty> sortedFacultyList = facultyList.getFacultyList()
                .stream()
                .sorted(Comparator.comparing(Faculty::getFacultyName))
                .toList();

        for (Faculty faculty : sortedFacultyList) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> handleFacultyMenuButton(menuItem.getText()));
            facultyMenuButton.getItems().add(menuItem);
        }
        facultyMenuButton.setText(sortedFacultyList.getFirst().getFacultyName());
        handleFacultyMenuButton(sortedFacultyList.getFirst().getFacultyName());
    }

    private void initFacultyUserMenuButton() {
        facultyUserMenuButton.getItems().clear();
        List<Faculty> sortedFacultyList = facultyList.getFacultyList()
                .stream()
                .sorted(Comparator.comparing(Faculty::getFacultyName))
                .toList();

        for (Faculty faculty : sortedFacultyList) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> handleFacultyUserMenuButton(menuItem.getText()));
            facultyUserMenuButton.getItems().add(menuItem);
        }
    }

    private void handleFacultyMenuButton(String facultyName) {
        facultyMenuButton.setText(facultyName);
        initRequestByFaculty(facultyName);
        departmentMenuButton.setText("เลือกภาควิชา");
        initDepartmentMenuButton(facultyName);
    }

    private void handleFacultyUserMenuButton(String facultyName) {
        facultyUserMenuButton.setText(facultyName);
        initUserByFaculty(facultyName);
        departmentUserMenuButton.setText("เลือกภาควิชา");
        initDepartmentUserMenuButton(facultyName);
    }

    private void initRequestByFaculty(String facultyName) {
        confirmedRequestFaculty.setText(String.valueOf(requestList.getRequestByFaculty(facultyName)
                .getRequestsByRequestTo("คำร้องดำเนินการครบถ้วน").getTotalRequestsByStatus("อนุมัติโดยคณบดี")));
        confirmedRequestDepartment.setText("0");
        totalRequestLabelByFaculty.setText(confirmedRequestFaculty.getText());
    }

    private void initUserByFaculty(String facultyName) {
        totalFacultyStaffLabel.setText(String.valueOf(facultyStaffList.getFacultyStaffListByFaculty(facultyName).getFacultyStaffList().size()));
        totalStudentLabel.setText(String.valueOf(studentList.getStudentListByFaculty(facultyName).getRegisteredStudents().getStudentList().size()));
        totalDepartmentStaffLabel.setText(String.valueOf(departmentStaffList.getDepartmentStaffListByFaculty(facultyName).getDepartmentStaffList().size()));
        totalAdvisorLabel.setText(String.valueOf(advisorList.getAdvisorListByFaculty(facultyName).size()));
        totalUserLabel.setText(String.valueOf(
                Integer.parseInt(totalStudentLabel.getText()) +
                        Integer.parseInt(totalDepartmentStaffLabel.getText()) +
                        Integer.parseInt(totalAdvisorLabel.getText()) +
                        Integer.parseInt(totalFacultyStaffLabel.getText())
        ));
    }

    private void initDepartmentMenuButton(String facultyName) {
        departmentMenuButton.getItems().clear();
        List<Department> sortedDepartmentList = departmentList.getDepartmentListByFaculty(facultyName)
                .stream()
                .sorted(Comparator.comparing(Department::getDepartmentName))
                .toList();
        for (Department department : sortedDepartmentList) {
            MenuItem menuItem = new MenuItem(department.getDepartmentName());
            menuItem.setOnAction(e -> handleDepartmentMenuButton(menuItem.getText()));
            departmentMenuButton.getItems().add(menuItem);
        }
        departmentMenuButton.setText(sortedDepartmentList.getFirst().getDepartmentName());
        initRequestByDepartment(sortedDepartmentList.getFirst().getDepartmentName());
    }

    private void initDepartmentUserMenuButton(String facultyName) {
        departmentUserMenuButton.getItems().clear();
        List<Department> sortedDepartmentList = departmentList.getDepartmentListByFaculty(facultyName)
                .stream()
                .sorted(Comparator.comparing(Department::getDepartmentName))
                .toList();
        for (Department department : sortedDepartmentList) {
            MenuItem menuItem = new MenuItem(department.getDepartmentName());
            menuItem.setOnAction(e -> handleDepartmentUserMenuButton(menuItem.getText()));
            departmentUserMenuButton.getItems().add(menuItem);
        }
    }

    private void initRequestByDepartment(String departmentName) {
        departmentMenuButton.setText(departmentName);
        confirmedRequestDepartment.setText(String.valueOf(requestList.getRequestByDepartment(departmentName)
                .getRequestsByRequestTo("คำร้องดำเนินการครบถ้วน").getTotalRequestsByStatus("อนุมัติโดยหัวหน้าภาควิชา")));
        totalRequestLabelByFaculty.setText(String.valueOf(Integer.parseInt(confirmedRequestDepartment.getText()) + Integer.parseInt(confirmedRequestFaculty.getText())));
    }

    private void initUserByDepartment(String departmentName) {
        departmentUserMenuButton.setText(departmentName);
        totalStudentLabel.setText(String.valueOf(studentList.getStudentListByDepartment(departmentName).getRegisteredStudents().getStudentList().size()));
        totalDepartmentStaffLabel.setText(String.valueOf(departmentStaffList.getDepartmentStaffListByDepartment(departmentName).getDepartmentStaffList().size()));
        totalAdvisorLabel.setText(String.valueOf(advisorList.getAdvisorListByDepartment(departmentName).size()));
        totalUserLabel.setText(String.valueOf(
                Integer.parseInt(totalStudentLabel.getText()) +
                        Integer.parseInt(totalDepartmentStaffLabel.getText()) +
                        Integer.parseInt(totalAdvisorLabel.getText()) +
                        Integer.parseInt(totalFacultyStaffLabel.getText())
        ));
    }

    private void handleDepartmentMenuButton(String departmentName) {
        initRequestByDepartment(departmentName);
        departmentMenuButton.setText(departmentName);
    }

    private void handleDepartmentUserMenuButton(String departmentName) {
        initUserByDepartment(departmentName);
        departmentUserMenuButton.setText(departmentName);
    }
}
