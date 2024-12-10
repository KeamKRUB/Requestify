package ku.cs.controllers.admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import ku.cs.models.advisor.AdvisorList;
import ku.cs.models.department.Department;
import ku.cs.models.department.DepartmentList;
import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AdminAdvisorController {

    @FXML private TableView<Advisor> adminAdvisorTableView;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField advisorIdTextField;
    @FXML private TextField searchTextField;
    @FXML private MenuButton facultyMenuButton;
    @FXML private MenuButton departmentMenuButton;

    private DataRepository dataRepository;

    private FilteredList<Advisor> filteredAdvisers;

    private AdvisorList advisorList;
    private UserList userList;
    private SettingList settingList;
    private FacultyList  facultyList;
    private DepartmentList departmentList;

    @FXML
    public void initialize() {

        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(AdvisorList.class);

        userList = dataRepository.getUserList();
        advisorList = dataRepository.getAdvisorList();
        facultyList = dataRepository.getFacultyList();
        departmentList = dataRepository.getDepartmentList();
        settingList = dataRepository.getSettingList();

        TableColumn<Advisor, String> advisorIdCol = new TableColumn<>("รหัสประจำตัว");
        advisorIdCol.setCellValueFactory(new PropertyValueFactory<>("advisorId"));

        TableColumn<Advisor, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Advisor, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Advisor, String> userNameCol = new TableColumn<>("ชื่อผู้ใช้");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Advisor, String> passwordCol = new TableColumn<>("รหัสผ่าน");
        passwordCol.setCellValueFactory(cellData -> {
            String fullPassword = cellData.getValue().getPassword();
            if (fullPassword.length() > 4) {
                String maskedPassword = fullPassword.substring(0, 4)
                        + "****"
                        + fullPassword.substring(fullPassword.length() - 4);
                return new SimpleStringProperty(maskedPassword);
            } else {
                return new SimpleStringProperty(fullPassword);
            }
        });

        TableColumn<Advisor, String> facultyCol = new TableColumn<>("คณะ");
        facultyCol.setCellValueFactory(new PropertyValueFactory<>("faculty"));

        TableColumn<Advisor, String> departmentCol = new TableColumn<>("ภาควิชา");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        adminAdvisorTableView.getColumns().clear();
        adminAdvisorTableView.getColumns().addAll(List.of(advisorIdCol, firstNameCol, lastNameCol, userNameCol, passwordCol, facultyCol, departmentCol));

        initFacultyMenuButton();
        showAdvisor();

        adminAdvisorTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Advisor> observableValue, Advisor advisor, Advisor newValue) {
                        if (newValue != null) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/admin-advisor-edit-popup.fxml"));
                                    Parent root = loader.load();
                                    AdminAdvisorEditController controller = loader.getController();
                                    controller.setAdvisor(newValue);
                                    controller.setAdvisorList(advisorList);

                                    Stage popupStage = new Stage();
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.setTitle("Advisor Editing");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.showAndWait();

                                    adminAdvisorTableView.getSelectionModel().clearSelection();
                                    showAdvisor();

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                }
        );

        //ขยาย Table View ให้เหมาะกับข้อความ
        adminAdvisorTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth =
                    firstNameCol.getWidth() +
                            lastNameCol.getWidth() +
                            userNameCol.getWidth() +
                            passwordCol.getWidth() +
                            advisorIdCol.getWidth() +
                            departmentCol.getWidth() +
                            facultyCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                adminAdvisorTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                adminAdvisorTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    public void showAdvisor() {
        adminAdvisorTableView.getItems().clear();
        filteredAdvisers = new FilteredList<>(FXCollections.observableArrayList(
                advisorList
                        .getAdvisorList()), advisor -> true);

        SortedList<Advisor> sortedAdvisors = new SortedList<>(filteredAdvisers);
        sortedAdvisors.setComparator(Comparator.comparing(Advisor::getAdvisorId));
        adminAdvisorTableView.getItems().addAll(sortedAdvisors);
    }

    public void initFacultyMenuButton() {
        facultyMenuButton.getItems().clear();
        //Sort a Faculty in dropdown
        List<Faculty> sortedFacultyList = facultyList.getFacultyList()
                .stream()
                .sorted(Comparator.comparing(Faculty::getFacultyName))
                .toList();

        for (Faculty faculty : sortedFacultyList) {
            MenuItem menuItem = new MenuItem(faculty.getFacultyName());
            menuItem.setOnAction(e -> handleFacultyMenuButton(menuItem.getText()));
            facultyMenuButton.getItems().add(menuItem);
        }
    }

    public void handleFacultyMenuButton(String facultyName){
        facultyMenuButton.setText(facultyName);
        initDepartmentMenuButton(facultyName);
    }

    public void initDepartmentMenuButton(String facultyName){
        departmentMenuButton.getItems().clear();
        //Sort a Department in dropdown
        List<Department> sortedDepartmentList = departmentList.getDepartmentListByFaculty(facultyName)
                .stream()
                .sorted(Comparator.comparing(Department::getDepartmentName))
                .toList();
        for (Department department : sortedDepartmentList) {
            MenuItem menuItem = new MenuItem(department.getDepartmentName());
            menuItem.setOnAction(e -> departmentMenuButton.setText(menuItem.getText()));
            departmentMenuButton.getItems().add(menuItem);
        }
    }


    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();

        filteredAdvisers.setPredicate(advisor -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return advisor.textToSearch().toLowerCase().contains(searchText);
        });

        adminAdvisorTableView.setItems(new SortedList<>(filteredAdvisers));
    }

    @FXML
    public void onAddButtonClick() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String advisorId = advisorIdTextField.getText();
        String faculty = facultyMenuButton.getText();
        String department = departmentMenuButton.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || advisorId.isEmpty() || faculty.equals("เลือกคณะ") || department.equals("เลือกภาควิชา")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Advisor");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else if(userList.findUserByUsername(username)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Advisor");
            alert.setContentText("มี username นี้อยู่ในระบบเรียบร้อยแล้ว");
            alert.showAndWait();
        } else if(advisorList.findAdvisorById(advisorId)!=null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Advisor");
            alert.setContentText("รหัสประจำตัวนี้ถูกใช้งานไปแล้ว");
            alert.showAndWait();
        } else {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            Advisor newAdvisor = new Advisor(username, hashedPassword, "Advisor", firstName, lastName, advisorId, faculty, department);
            Setting newSetting = new Setting(username);

            settingList.addSetting(newSetting);
            advisorList.addAdvisor(newAdvisor);
            userList.addUser(newAdvisor);

            dataRepository.writeData(AdvisorList.class);
            dataRepository.writeData(UserList.class);
            dataRepository.writeData(SettingList.class);

            firstNameTextField.clear();
            lastNameTextField.clear();
            usernameTextField.clear();
            passwordTextField.clear();
            advisorIdTextField.clear();

            showAdvisor();
        }
    }
}