package ku.cs.controllers.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import ku.cs.controllers.component.HeaderController;
import ku.cs.models.advisor.Advisor;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.student.Student;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ProfileController {
    //Information Line
    @FXML private HBox line1;
    @FXML private HBox line2;
    @FXML private HBox line3;
    @FXML private HBox line4;
    @FXML private HBox line5;
    @FXML private HBox line6;
    @FXML private HBox line7;
    @FXML private HBox line8;

    //Information Label
    @FXML private Label label11;
    @FXML private Label label12;
    @FXML private Label label21;
    @FXML private Label label22;
    @FXML private Label label31;
    @FXML private Label label32;
    @FXML private Label label41;
    @FXML private Label label42;
    @FXML private Label label51;
    @FXML private Label label52;
    @FXML private Label label61;
    @FXML private Label label62;
    @FXML private Label label71;
    @FXML private Label label72;
    @FXML private Label label81;
    @FXML private Label label82;

    @FXML private TextField oldPasswordTextField;
    @FXML private TextField newPasswordTextField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private Label oldPasswordErrorLabel;
    @FXML private Label newPasswordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;

    @FXML private VBox mainLayout;
    @FXML private Circle profileImage;
    private Image image;

    private User user;
    private DataRepository dataRepository;
    private String previousPath;

    public void initialize() {

        dataRepository = DataRepository.getDataRepository();
        previousPath = (String) FXRouter.getData();
        user = UserSession.getSession().getLoggedInUser();

        clearErrorLabel();
        loadUserProfile();
    }

    private void clearErrorLabel() {
        oldPasswordErrorLabel.setText("");
        newPasswordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");
    }

    private void loadUserProfile() {
        if(!user.isChangedProfile()){
            image = new Image(getClass().getResource(user.getProfilePath()).toExternalForm(), false);
        }
        else{
            image = new Image("file:" + user.getProfilePath(), false);
        }
        profileImage.setFill(new ImagePattern(image));

        if (user instanceof Student) {
            Student userByRole = (Student) user;
            label11.setText("First Name:");
            label12.setText(userByRole.getFirstName());
            label21.setText("Last Name:");
            label22.setText(userByRole.getLastName());
            label31.setText("Student ID:");
            label32.setText(userByRole.getStudentId());
            label41.setText("Email:");
            label42.setText(userByRole.getEmail());
            label51.setText("Advisor:");
            label52.setText(userByRole.getAdvisor());
            label61.setText("Faculty:");
            label62.setText(userByRole.getFaculty());
            label71.setText("Department:");
            label72.setText(userByRole.getDepartment());
            label81.setText("Username:");
            label82.setText(userByRole.getUsername());

        } else if (user instanceof Advisor) {
            Advisor userByRole = (Advisor) user;

            mainLayout.getChildren().removeAll(line7,line8);
            label11.setText("First Name:");
            label12.setText(userByRole.getFirstName());
            label21.setText("Last Name:");
            label22.setText(userByRole.getLastName());
            label31.setText("Advisor ID:");
            label32.setText(userByRole.getAdvisorId());
            label41.setText("Faculty:");
            label42.setText(userByRole.getFaculty());
            label51.setText("Department:");
            label52.setText(userByRole.getDepartment());
            label61.setText("Username:");
            label62.setText(userByRole.getUsername());

        } else if (user instanceof DepartmentStaff) {
            DepartmentStaff userByRole = (DepartmentStaff) user;

            mainLayout.getChildren().removeAll(line6,line7,line8);
            label11.setText("First Name:");
            label12.setText(userByRole.getFirstName());
            label21.setText("Last Name:");
            label22.setText(userByRole.getLastName());
            label31.setText("Faculty:");
            label32.setText(userByRole.getFaculty());
            label41.setText("Department:");
            label42.setText(userByRole.getDepartment());
            label51.setText("Username:");
            label52.setText(userByRole.getUsername());

        } else if (user instanceof FacultyStaff) {
            FacultyStaff userByRole = (FacultyStaff) user;
            mainLayout.getChildren().removeAll(line5,line6,line7,line8);

            label11.setText("First Name:");
            label12.setText(userByRole.getFirstName());
            label21.setText("Last Name:");
            label22.setText(userByRole.getLastName());
            label31.setText("Faculty:");
            label32.setText(userByRole.getFaculty());
            label41.setText("Username:");
            label42.setText(userByRole.getUsername());

        } else {
            mainLayout.getChildren().removeAll(line2,line3,line4,line5,line6,line7,line8);

            label11.setText("Username:");
            label12.setText(user.getUsername());
        }
    }

    @FXML
    public void onBackButtonClick() {
        try {
            FXRouter.loadPage(previousPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onChangeImageClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("images PNG JPG", "*.png", "*.jpg", "*.jpeg"));
        Node source = (Node) event.getSource();
        File file = chooser.showOpenDialog(source.getScene().getWindow());
        if (file != null) {
            try {
                File destDir = new File("data/images");
                if (!destDir.exists()) destDir.mkdirs();
                String[] fileSplit = file.getName().split("\\.");
                String filename = user.getUsername() + "." + fileSplit[fileSplit.length - 1];
                Path target = FileSystems.getDefault().getPath(
                        destDir.getAbsolutePath() + System.getProperty("file.separator") + filename
                );
                Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                Image image = new Image(target.toUri().toString());
                profileImage.setFill(new ImagePattern(image));
                user.setProfilePath(destDir + "/" + filename);
                user.setChangedProfile(true);
                dataRepository.writeData(UserList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HeaderController headerController = HeaderController.getInstance();
        headerController.updateHeader();
    }


    private boolean validateOldPassword() {
        BCrypt.Result result = BCrypt.verifyer().verify(oldPasswordTextField.getText().toCharArray(), user.getPassword());
        if (!result.verified) {
            oldPasswordErrorLabel.setText("Old password is incorrect.");
            oldPasswordErrorLabel.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean validateNewPassword() {
        if (newPasswordTextField.getText().isEmpty()) {
            newPasswordErrorLabel.setText("New password is required.");
            newPasswordErrorLabel.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (!newPasswordTextField.getText().equals(confirmPasswordTextField.getText())) {
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
            String hashedPassword = BCrypt.withDefaults().hashToString(12, newPasswordTextField.getText().toCharArray());
            user.setPassword(hashedPassword);
            dataRepository.writeData(UserList.class);

            oldPasswordTextField.clear();
            newPasswordTextField.clear();
            confirmPasswordTextField.clear();

        }
    }
}
