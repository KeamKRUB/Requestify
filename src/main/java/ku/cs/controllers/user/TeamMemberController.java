package ku.cs.controllers.user;

import javafx.fxml.FXML;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class TeamMemberController {
    private String previousPath = (String) FXRouter.getData();

    @FXML
    public void onBackButtonClick() {
        try {
            FXRouter.loadPage(previousPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
