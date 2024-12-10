package ku.cs.controllers.user;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.io.IOException;

public class RegisterSecondPageController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private Label usernameErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private Button showButton;
    @FXML private Button confirmShowButton;

    private Student studentUser;
    private StudentList studentList;
    private UserList userList;
    private SettingList settingList;
    private DataRepository dataRepository;

    public void initialize() {
        dataRepository = DataRepository.getDataRepository();
        studentList = dataRepository.getStudentList();
        userList = dataRepository.getUserList();
        settingList = dataRepository.getSettingList();

        studentUser = (Student) FXRouter.getData();

        passwordTextField.setText(passwordField.getText());

        passwordField.textProperty().addListener((observable, oldValue, newValue) ->
                passwordTextField.setText(newValue)
        );

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) ->
                passwordField.setText(newValue)
        );

        confirmPasswordTextField.setText(confirmPasswordField.getText());

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) ->
                confirmPasswordTextField.setText(newValue)
        );

        confirmPasswordTextField.textProperty().addListener((observable, oldValue, newValue) ->
                confirmPasswordField.setText(newValue)
        );


        clearErrorLabels();
    }

    private void clearErrorLabels() {
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setVisible(false);
    }

    private void showErrorLabel(Label label) {
        label.setVisible(true);
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), label);
        slideIn.setFromX(-100);
        slideIn.setToX(0);
        slideIn.play();
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
    public  void onShowConfirmButtonClick(){
        if (confirmPasswordField.isVisible()) {
            confirmPasswordField.setVisible(false);
            confirmPasswordTextField.setVisible(true);
            confirmShowButton.setText("hide");
        } else {
            confirmPasswordField.setVisible(true);
            confirmPasswordTextField.setVisible(false);
            confirmShowButton.setText("show");
        }
    }

    @FXML
    public void onBackButtonClick() {
        try {
            FXRouter.loadPage("register-first-page", studentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onRegisterButtonClick() {
        boolean isValid = true;

        clearErrorLabels();

        if (usernameTextField.getText().isEmpty()) {
            usernameErrorLabel.setText("Username is required");
            showErrorLabel(usernameErrorLabel);
            isValid = false;
        }

        if (!userList.isValid(usernameTextField.getText())){
            usernameErrorLabel.setText("This username is already taken");
            showErrorLabel(usernameErrorLabel);
            isValid = false;
        }

        if (passwordField.getText().isEmpty()) {
            passwordErrorLabel.setText("Password is required");
            showErrorLabel(passwordErrorLabel);
            isValid = false;
        }

        if (confirmPasswordField.getText().isEmpty()) {
            confirmPasswordErrorLabel.setText("Confirm Password is required");
            showErrorLabel(confirmPasswordErrorLabel);
            isValid = false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordErrorLabel.setText("Passwords do not match");
            showErrorLabel(confirmPasswordErrorLabel);
            isValid = false;
        }

        if (isValid) {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, passwordField.getText().toCharArray());

            studentUser.setUsername(usernameTextField.getText());
            studentUser.setPassword(hashedPassword);
            studentUser.setRole("Student");

            Setting setting = new Setting(studentUser.getUsername());
            settingList.addSetting(setting);

            userList.addUser(studentUser);

            dataRepository.writeData(SettingList.class);
            dataRepository.writeData(UserList.class);

            try {
                FXRouter.goTo("login-page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onLoginButtonClick() {
        try {
            FXRouter.goTo("login-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
