package ku.cs.controllers.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import ku.cs.models.advisor.Advisor;
import ku.cs.models.component.Navbar;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.student.Student;
import ku.cs.models.user.User;
import ku.cs.models.user.UserSession;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeaderController {
    private static HeaderController instance;

    @FXML
    private Label roleTextLabel;
    @FXML
    private Label pageTextLabel;
    @FXML
    private Circle profileImage;
    @FXML
    private MenuButton profileMenuButton;

    private UserSession session = UserSession.getSession();
    private User userSession = session.getLoggedInUser();

    public static HeaderController getInstance() {
        return instance;
    }

    public void initialize() {
        instance = this;
        roleTextLabel.setText(userSession.getRole());
        updateHeader();
    }

    public void updateHeader() {
        UserSession session = UserSession.getSession();
        User userSession = session.getLoggedInUser();
        if (userSession instanceof Student) {
            Student user = (Student) userSession;
            profileMenuButton.setText(user.getName());
        } else if (userSession instanceof Advisor) {
            Advisor user = (Advisor) userSession;
            profileMenuButton.setText(user.getName());
        } else if (userSession instanceof DepartmentStaff) {
            DepartmentStaff user = (DepartmentStaff) userSession;
            profileMenuButton.setText(user.getName());
        } else if (userSession instanceof FacultyStaff) {
            FacultyStaff user = (FacultyStaff) userSession;
            profileMenuButton.setText(user.getName());
        } else {
            profileMenuButton.setText(userSession.getUsername());
        }

        Image image;
        if(!userSession.isChangedProfile()){
            image = new Image(getClass().getResource(userSession.getProfilePath()).toExternalForm(), false);
        }
        else{
            image = new Image("file:" + userSession.getProfilePath(), false);
        }
        profileImage.setFill(new ImagePattern(image));
    }

    @FXML
    public void onProfileButtonClick() {
        try {
            FXRouter.loadPage("profile-page",Navbar.getInstance().getCurrentPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onLogoutButtonClick() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String lastestLogin = currentTime.format(formatter);
        userSession.setLastestLogin(lastestLogin);
        userSession.setLoggedIn(false);
        UserSession.clearSession();
        Navbar.resetInstance();
        try {
            FXRouter.goTo("login-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


