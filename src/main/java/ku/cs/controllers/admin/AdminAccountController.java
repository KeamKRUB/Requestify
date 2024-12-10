package ku.cs.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminAccountController {
    @FXML private Label userRole;
    @FXML private Label userName;
    @FXML private Label userId;
    @FXML private Label usernameText;
    @FXML private Label userLoggedIn;
    @FXML private Circle statusColor;
    @FXML private Circle profileImage;
    @FXML private Circle profileBackground;
    @FXML private Button onAvailableButton;
    @FXML private TableView<User> adminStudentTableView;
    @FXML private MenuButton roleMenuButton;
    @FXML private TextField searchTextField;
    @FXML private Label timeLabel;

    private UserList userList;
    private DataRepository dataRepository;

    private FilteredList<User> filteredUsers;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        userList  = dataRepository.getUserList();

        userRole.setVisible(false);
        userName.setVisible(false);
        userId.setVisible(false);
        usernameText.setVisible(false);
        userLoggedIn.setVisible(false);
        statusColor.setVisible(false);
        onAvailableButton.setVisible(false);
        profileImage.setVisible(false);
        profileBackground.setVisible(false);

        setupRoleMenu();

        TableColumn<User, Integer> userRoleCol = new TableColumn<>("ตำแหน่ง");
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Integer> usernameCol = new TableColumn<>("username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        adminStudentTableView.getColumns().clear();
        adminStudentTableView.getColumns().addAll(List.of(userRoleCol, usernameCol, firstNameCol, lastNameCol));

        filterTableByRole("All");

        adminStudentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                userRole.setText(newValue.getRole());
                userId.setText(newValue.getId());
                usernameText.setText(newValue.getUsername());
                userName.setText(newValue.getName());
                Image image;
                if(!newValue.isChangedProfile()){
                    image = new Image(getClass().getResource(newValue.getProfilePath()).toExternalForm(), false);
                }
                else{
                    image = new Image("file:" + newValue.getProfilePath(), false);
                }
                profileImage.setFill(new ImagePattern(image));

                userRole.setVisible(true);
                userName.setVisible(true);
                userId.setVisible(true);
                usernameText.setVisible(true);
                profileImage.setVisible(true);
                profileBackground.setVisible(true);
                infoUpdate();
            }
        });

        adminStudentTableView.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = usernameCol.getWidth() +
                    firstNameCol.getWidth() +
                    lastNameCol.getWidth() +
                    userRoleCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                adminStudentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                adminStudentTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    private void setupRoleMenu() {
        roleMenuButton.getItems().clear();

        MenuItem allItem = new MenuItem("All");
        MenuItem studentItem = new MenuItem("Student");
        MenuItem advisorItem = new MenuItem("Advisor");
        MenuItem departmentStaffItem = new MenuItem("DepartmentStaff");
        MenuItem facultyStaffItem = new MenuItem("FacultyStaff");

        allItem.setOnAction(event -> filterTableByRole("All"));
        studentItem.setOnAction(event -> filterTableByRole("Student"));
        advisorItem.setOnAction(event -> filterTableByRole("Advisor"));
        departmentStaffItem.setOnAction(event -> filterTableByRole("DepartmentStaff"));
        facultyStaffItem.setOnAction(event -> filterTableByRole("FacultyStaff"));

        roleMenuButton.getItems().addAll(allItem,studentItem,advisorItem,departmentStaffItem,facultyStaffItem);
    }

    private void filterTableByRole(String role) {
        roleMenuButton.setText(role);
        ArrayList<User> allUser = userList.getUserListByRole(role);

        filteredUsers = new FilteredList<>(FXCollections.observableArrayList(allUser), user -> true);

        SortedList<User> sortedUsers = new SortedList<>(filteredUsers);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sortedUsers.setComparator((user1, user2) -> {
            LocalDateTime date1 = LocalDateTime.parse(user1.getLastestLogin(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(user2.getLastestLogin(), formatter);
            return date2.compareTo(date1); // เรียงจากมากไปน้อย (วันที่ล่าสุดก่อน)
        });
        adminStudentTableView.setItems(sortedUsers);
    }

    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredUsers.setPredicate(user -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return user.textToSearch().contains(searchText);
        });
    }

    public void infoUpdate(){
        User newValue = adminStudentTableView.getSelectionModel().getSelectedItem();
        onAvailableButton.setVisible(true);
        if (newValue.isLoggedIn()) {
            statusColor.setStyle("-fx-fill: green;");
            userLoggedIn.setStyle("-fx-text-fill: green;");
            timeLabel.setVisible(false);
            userLoggedIn.setText("กำลังใช้งาน");
        }else {
            String time[] = newValue.getLastestLogin().split(" ");
            statusColor.setStyle("-fx-fill: grey;");
            userLoggedIn.setStyle("-fx-text-fill: grey;");
            timeLabel.setStyle("-fx-fill: grey;");
            timeLabel.setStyle("-fx-text-fill: grey;");
            timeLabel.setVisible(true);
            userLoggedIn.setText("ใช้งานล่าสุดเมื่อ " + time[0]);
            timeLabel.setText(time[1]);
        }
        userLoggedIn.setVisible(true);
        statusColor.setVisible(true);

        if (newValue.isAvailable()) {
            onAvailableButton.setStyle("-fx-background-color: red;");
            onAvailableButton.setText("ระงับสิทธิ์");
        } else {
            onAvailableButton.setStyle("-fx-background-color: green;");
            onAvailableButton.setText("คืนสิทธิ์");
        }
    }
    @FXML
    public void onAvailableButtonClick() {
        User user = adminStudentTableView.getSelectionModel().getSelectedItem();

        user.setAvailable(!user.getAvailable());
        infoUpdate();
        dataRepository.writeData(UserList.class);
    }
}