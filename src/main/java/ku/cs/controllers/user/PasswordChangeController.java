package ku.cs.controllers.user;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import at.favre.lib.crypto.bcrypt.BCrypt; // สำหรับใช้ในการแฮชรหัสผ่าน


public class PasswordChangeController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private User user;
    private DataRepository dataRepository;

    public void initialize() {
        // ดึงข้อมูลผู้ใช้ที่เข้าสู่ระบบในปัจจุบัน
        user = UserSession.getSession().getLoggedInUser();
        dataRepository = DataRepository.getDataRepository();
    }

    @FXML
    private void handleChangePassword() {
        boolean isValid = validateNewPassword() && validateConfirmPassword();

        if (isValid) {
            // แฮชรหัสผ่านใหม่ก่อนบันทึก
            String hashedPassword = BCrypt.withDefaults().hashToString(12, newPasswordField.getText().toCharArray());
            user.setChangePassword(hashedPassword); // ตั้งค่ารหัสผ่านใหม่
            try {
                dataRepository.writeData(UserList.class);
                newPasswordField.clear();
                confirmPasswordField.clear();

                showAlert("Success", "Password changed successfully.");
                newPasswordField.getScene().getWindow().hide(); // ปิดหน้าต่างหลังจากเปลี่ยนรหัสผ่าน
            } catch (Exception e) {
                showAlert("Error", "Failed to save password. Please try again.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onCancelButtonClick() {
        newPasswordField.getScene().getWindow().hide();
    }

    private boolean validateNewPassword() {
        // ตรวจสอบว่ารหัสผ่านใหม่
        String newPassword = newPasswordField.getText();
        BCrypt.Result result = BCrypt.verifyer().verify(newPassword.toCharArray(), user.getPassword());

        if (result.verified) {
            showAlert("Error", "New password cannot be the same as the old password.");
            return false;
        }
        if (newPassword.isEmpty()) {
            showAlert("Error", "New password cannot be empty.");
            return false;
        }else{
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        // ตรวจสอบว่ารหัสผ่านยืนยันตรงกับรหัสผ่านใหม่หรือไม่
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            showAlert("Error", "Passwords do not match.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle(title);
            alert.showAndWait();
        });
    }
}
