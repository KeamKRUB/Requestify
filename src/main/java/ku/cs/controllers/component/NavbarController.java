package ku.cs.controllers.component;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import ku.cs.models.component.Navbar;
import ku.cs.models.user.User;
import ku.cs.models.user.UserSession;

public class NavbarController {

    @FXML
    private VBox topNavbar;

    @FXML
    private VBox bottomNavbar;

    @FXML
    public void initialize() {
        UserSession session = UserSession.getSession();
        User userSession = session.getLoggedInUser();
        Navbar.initializeInstance(topNavbar, bottomNavbar);

        Navbar navbar = Navbar.getInstance();
        navbar.setButton(userSession.getRole()); //Set button to Navbar with Role
    }
}
