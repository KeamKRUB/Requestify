package ku.cs.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AdminFacultyController {
    @FXML private TableView<Faculty> adminFacultyTableView;
    @FXML private TextField facultyIdTextField;
    @FXML private TextField facultyNameTextField;

    @FXML private TextField searchTextFieldTextField;

    private FacultyList facultyList;
    private DataRepository dataRepository;

    private FilteredList<Faculty> filteredFaculties;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(FacultyList.class);

        facultyList = dataRepository.getFacultyList();

        TableColumn<Faculty, String> facultyIdCol = new TableColumn<>("รหัสคณะ");
        facultyIdCol.setCellValueFactory(new PropertyValueFactory<>("facultyId"));

        TableColumn<Faculty, String> facultyNameCol = new TableColumn<>("ชื่อคณะ");
        facultyNameCol.setCellValueFactory(new PropertyValueFactory<>("facultyName"));

        adminFacultyTableView.getColumns().clear();
        adminFacultyTableView.getColumns().addAll(List.of(facultyIdCol, facultyNameCol));

        showFaculty();

        adminFacultyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try {
                        FXRouter.loadPage("admin-faculty-edit",newValue);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        //ขยาย Table View ให้เหมาะกับข้อความ
        adminFacultyTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = facultyIdCol.getWidth()
                    + facultyNameCol.getWidth()
                    + facultyNameCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                adminFacultyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                adminFacultyTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    public void onAddButtonClick() {
        String facultyId = facultyIdTextField.getText();
        String facultyName = facultyNameTextField.getText();
        if (facultyId.isEmpty() || facultyName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Faculty");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(facultyList.findFacultyById(facultyId)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Faculty");
            alert.setContentText("รหัสคณะนี้ถูกใช้งานไปแล้ว");
            alert.showAndWait();
        } else {
            Faculty newFaculty = new Faculty(facultyId, facultyName);
            facultyIdTextField.clear();
            facultyNameTextField.clear();

            facultyList.addFaculty(newFaculty);
            dataRepository.writeData(FacultyList.class);

            showFaculty();
        }
    }
    public void showFaculty(){
        filteredFaculties = new FilteredList<>(FXCollections.observableArrayList(facultyList
                .getFacultyList()), faculty -> true);

        SortedList<Faculty> sortedFaculties = new SortedList<>(filteredFaculties);
        sortedFaculties.setComparator(Comparator.comparing(Faculty::getFacultyId)
                .thenComparing(Faculty::getFacultyName));
        adminFacultyTableView.setItems(sortedFaculties);
    }

    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextFieldTextField.getText().toLowerCase();
        filteredFaculties.setPredicate(faculty -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return faculty.textToSearch().contains(searchText);
        });
    }
}