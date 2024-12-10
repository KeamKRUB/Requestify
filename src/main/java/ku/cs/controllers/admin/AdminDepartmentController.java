package ku.cs.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ku.cs.models.department.Department;
import ku.cs.models.department.DepartmentList;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AdminDepartmentController {
    @FXML private TableView<Department> adminDepartmentTableView;
    @FXML private TextField departmentIdTextField;
    @FXML private TextField departmentNameTextField;
    @FXML private MenuButton facultyMenuButton;
    @FXML private TextField searchTextField;

    private FacultyList facultyList;
    private DepartmentList departmentList;
    private DataRepository dataRepository;

    private FilteredList<Department> filteredDepartments;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(DepartmentList.class);

        departmentList = dataRepository.getDepartmentList();
        facultyList = dataRepository.getFacultyList();

        initFacultyMenuButton();

        TableColumn<Department, String> facultyNameCol = new TableColumn<>("คณะ");
        facultyNameCol.setCellValueFactory(new PropertyValueFactory<>("facultyName"));

        TableColumn<Department, String> departmentIdCol = new TableColumn<>("รหัสภาควิชา");
        departmentIdCol.setCellValueFactory(new PropertyValueFactory<>("departmentId"));

        TableColumn<Department, String> departmentNameCol = new TableColumn<>("ชื่อภาควิชา");
        departmentNameCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));



        adminDepartmentTableView.getColumns().clear();
        adminDepartmentTableView.getColumns().addAll(List.of(departmentIdCol, departmentNameCol, facultyNameCol));

        showList();

        adminDepartmentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try {
                        // FXRouter.loadPage สามารถส่งข้อมูลไปยังหน้าที่ต้องการได้ โดยกำหนดเป็น parameter ที่สอง
                        FXRouter.loadPage("admin-department-edit",newValue);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        //ขยาย Table View ให้เหมาะกับข้อความ
        adminDepartmentTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = departmentIdCol.getWidth()
                    + departmentNameCol.getWidth()
                    + facultyNameCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                adminDepartmentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                adminDepartmentTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    public void onAddButtonClick() {
        String departmentId = departmentIdTextField.getText();
        String departmentName = departmentNameTextField.getText();
        String facultyName = facultyMenuButton.getText();

        if (departmentId.isEmpty() || departmentName.isEmpty() || facultyName.equals("เลือกคณะ")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Department");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(departmentList.findDepartmentById(departmentId)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Department");
            alert.setContentText("รหัสภาควิชานี้ถูกใช้งานไปแล้ว");
            alert.showAndWait();
        } else {
            Department newDepartment = new Department(departmentId, departmentName ,facultyName);
            departmentList.addDepartment(newDepartment);
            dataRepository.writeData(DepartmentList.class);

            departmentIdTextField.clear();
            departmentNameTextField.clear();

            showList();
        }
    }

    public void showList(){
        filteredDepartments = new FilteredList<>(FXCollections.observableArrayList(departmentList
                .getDepartmentList()), department -> true);

        SortedList<Department> sortedDepartments = new SortedList<>(filteredDepartments);
        sortedDepartments.setComparator(Comparator.comparing(Department::getFacultyName)
                .thenComparing(Department::getDepartmentId));
        adminDepartmentTableView.setItems(sortedDepartments);
    }

    public void initFacultyMenuButton() {
        facultyMenuButton.getItems().clear();

        List<Faculty> sortedFacultyList = facultyList.getFacultyList()
                .stream()
                .sorted(Comparator.comparing(Faculty::getFacultyName))
                .toList();

        for (Faculty faculty : sortedFacultyList) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> facultyMenuButton.setText(menuItem.getText()));
            facultyMenuButton.getItems().add(menuItem);
        }
    }


    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredDepartments.setPredicate(department -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return department.textToSearch().contains(searchText);
        });
        adminDepartmentTableView.setItems(new SortedList<>(filteredDepartments));
    }
}