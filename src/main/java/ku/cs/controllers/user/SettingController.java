package ku.cs.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ku.cs.models.user.*;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.io.IOException;

public class SettingController {

    @FXML private TextField oldPasswordField;
    @FXML private TextField newPasswordField;
    @FXML private TextField confirmPasswordField;
    @FXML private Label oldPasswordErrorLabel;
    @FXML private Label newPasswordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private RadioButton blueThemeRadio;
    @FXML private RadioButton greenThemeRadio;
    @FXML private RadioButton darkBlueThemeRadio;
    @FXML private RadioButton smallFontRadio;
    @FXML private RadioButton normalFontRadio;
    @FXML private RadioButton largeFontRadio;
    @FXML private RadioButton arialFontRadio;
    @FXML private RadioButton sansSerifFontRadio;
    @FXML private RadioButton verdanaFontRadio;

    private ToggleGroup themeGroup;
    private ToggleGroup fontSizeGroup;
    private ToggleGroup fontFamilyGroup;

    private User user;
    private DataRepository dataRepository;
    private Setting userSetting;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();

        user = UserSession.getSession().getLoggedInUser();
        userSetting = user.getUserSetting();

        themeGroup = new ToggleGroup();
        fontSizeGroup = new ToggleGroup();
        fontFamilyGroup = new ToggleGroup();

        blueThemeRadio.setToggleGroup(themeGroup);
        greenThemeRadio.setToggleGroup(themeGroup);
        darkBlueThemeRadio.setToggleGroup(themeGroup);

        smallFontRadio.setToggleGroup(fontSizeGroup);
        normalFontRadio.setToggleGroup(fontSizeGroup);
        largeFontRadio.setToggleGroup(fontSizeGroup);

        arialFontRadio.setToggleGroup(fontFamilyGroup);
        sansSerifFontRadio.setToggleGroup(fontFamilyGroup);
        verdanaFontRadio.setToggleGroup(fontFamilyGroup);

        userSetting = user.getUserSetting();

        clearErrorLabel();

        applySettings(userSetting);
    }

    private void applySettings(Setting setting) {
        switch (setting.getTheme()) {
            case "/styles/blue-theme.css": blueThemeRadio.setSelected(true); break;
            case "/styles/green-theme.css": greenThemeRadio.setSelected(true); break;
            case "/styles/dark-blue-theme.css": darkBlueThemeRadio.setSelected(true); break;
        }

        switch (setting.getFontSize()) {
            case "small-font": smallFontRadio.setSelected(true); break;
            case "medium-font": normalFontRadio.setSelected(true); break;
            case "large-font": largeFontRadio.setSelected(true); break;
        }

        switch (setting.getFontFamily()) {
            case "font4": arialFontRadio.setSelected(true); break;
            case "font2": sansSerifFontRadio.setSelected(true); break;
            case "font3": verdanaFontRadio.setSelected(true); break;
        }
    }

    private void saveCurrentSettings() {
        String oldStylesheet = userSetting.getTheme();

        if (blueThemeRadio.isSelected()) {
            userSetting.setTheme("/styles/blue-theme.css");
        } else if (greenThemeRadio.isSelected()) {
            userSetting.setTheme("/styles/green-theme.css");
        } else if (darkBlueThemeRadio.isSelected()) {
            userSetting.setTheme("/styles/dark-blue-theme.css");
        }

        if (smallFontRadio.isSelected()) {
            userSetting.setFontSize("small-font");
        } else if (normalFontRadio.isSelected()) {
            userSetting.setFontSize("medium-font");
        } else if (largeFontRadio.isSelected()) {
            userSetting.setFontSize("large-font");
        }

        if (arialFontRadio.isSelected()) {
            userSetting.setFontFamily("font4");
        } else if (sansSerifFontRadio.isSelected()) {
            userSetting.setFontFamily("font2");
        } else if (verdanaFontRadio.isSelected()) {
            userSetting.setFontFamily("font3");
        }

        FXRouter.removeStylesheet(oldStylesheet);
        FXRouter.addStylesheet(userSetting.getTheme());

        FXRouter.setFont(userSetting.getFontFamily());
        FXRouter.setFontSize(userSetting.getFontSize());

        try {
            FXRouter.loadPage("setting");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        saveUserSetting();
    }

    private void clearErrorLabel() {
        oldPasswordErrorLabel.setText("");
        newPasswordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");
    }

    private void saveUserSetting() {
        dataRepository.writeData(SettingList.class);
    }

    @FXML
    public void onConfirmButtonClick() {
        saveCurrentSettings();
    }

    private boolean validateOldPassword() {
        BCrypt.Result result = BCrypt.verifyer().verify(oldPasswordField.getText().toCharArray(), user.getPassword());
        if (oldPasswordField.getText().equals("")) {
            oldPasswordErrorLabel.setText("Old password is required.");
            oldPasswordErrorLabel.setVisible(true);
            return false;
        }
        if (!result.verified) {
            oldPasswordErrorLabel.setText("Old password is incorrect.");
            oldPasswordErrorLabel.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean validateNewPassword() {
        if (newPasswordField.getText().isEmpty()) {
            newPasswordErrorLabel.setText("New password is required.");
            newPasswordErrorLabel.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordErrorLabel.setText("Passwords do not match.");
            confirmPasswordErrorLabel.setVisible(true);
            return false;
        }
        return true;
    }


    public void onChangeButtonClick() {
        clearErrorLabel();
        boolean isValid = validateOldPassword() && validateNewPassword() && validateConfirmPassword();
        if (isValid) {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, newPasswordField.getText().toCharArray());
            user.setPassword(hashedPassword);
            dataRepository.writeData(UserList.class);

            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

        }
    }


    @FXML
    public void onCancelButtonClick() {
        applySettings(userSetting);

        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        oldPasswordErrorLabel.setVisible(false);
        newPasswordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setVisible(false);
    }
}
