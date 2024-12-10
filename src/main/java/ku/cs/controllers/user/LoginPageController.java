package ku.cs.controllers.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginPageController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Button showButton;

    private UserList userList;
    private DataRepository dataRepository;

    public void initialize() {
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        passwordTextField.setText(passwordField.getText());

        passwordField.textProperty().addListener((observable, oldValue, newValue) ->
                passwordTextField.setText(newValue)
        );

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) ->
                passwordField.setText(newValue)
        );

        dataRepository = DataRepository.getDataRepository();
        userList = dataRepository.getUserList();
    }

    private void showErrorLabel(Label label) {
        label.setVisible(true);
    }

    private void showChangePasswordPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/changepassword-popup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Force focus on the popup
            popupStage.setTitle("Change Password");
            popupStage.setScene(new Scene(root));

            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public  void onShowButtonClick(){
        if (passwordField.isVisible()) {
            passwordField.setVisible(false);
            passwordTextField.setVisible(true);
            showButton.setText("hide");
        } else {
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
            showButton.setText("show");
        }
    }

    @FXML
    public void onLoginButtonClick() {
        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);

        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean hasError = false;

        if (username.isEmpty()) {
            usernameErrorLabel.setText("Please enter a username.");
            showErrorLabel(usernameErrorLabel);
            hasError = true;
        }

        if (password.isEmpty()) {
            passwordErrorLabel.setText("Please enter a password.");
            showErrorLabel(passwordErrorLabel);
            hasError = true;
        }

        if (hasError) {
            return;
        }

        User user = userList.findUserByUsername(username);

        if (user != null) {
            if (!user.isAvailable()) {
                Alert suspendedAlert = new Alert(Alert.AlertType.ERROR);
                suspendedAlert.setTitle("Account Suspended");
                suspendedAlert.setHeaderText(null);
                suspendedAlert.setContentText("Your account has been suspended. Please contact support.");
                suspendedAlert.showAndWait();
                return;
            }

            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

            if (result.verified) {
                UserSession.createSession(user);

                String role = user.getRole();
                FXRouter.removeStylesheet("/styles/blue-theme.css");
                FXRouter.addStylesheet(user.getUserSetting().getTheme());

                FXRouter.setFont(user.getUserSetting().getFontFamily());
                FXRouter.setFontSize(user.getUserSetting().getFontSize());

                if (user.getIsChangePassword()) {
                    showChangePasswordPopup();
                } else {
                    try {
                        LocalDateTime currentTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String lastestLogin = currentTime.format(formatter);
                        user.setLastestLogin(lastestLogin);
                        user.setLoggedIn(true);

                        FXRouter.goTo("main-layout");
                        switch (role) {
                            case "Admin" -> FXRouter.loadPage("admin-dashboard");
                            case "Student" -> FXRouter.loadPage("student-request");
                            case "Advisor" -> FXRouter.loadPage("advisor-request");
                            case "DepartmentStaff" -> FXRouter.loadPage("department-request");
                            case "FacultyStaff" -> FXRouter.loadPage("faculty-request");
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Incorrect Password");
                alert.setHeaderText(null);
                alert.setContentText("รหัสผ่านไม่ถูกต้อง");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not Found");
            alert.setHeaderText(null);
            alert.setContentText("ไม่พบบัญชีนี้");
            alert.showAndWait();
        }
    }

    @FXML
    public void onRegisterButtonClick() {
        try {
            FXRouter.loadPage("register-first-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
