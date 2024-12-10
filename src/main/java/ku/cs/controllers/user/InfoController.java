package ku.cs.controllers.user;

import javafx.fxml.FXML;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class InfoController {

    @FXML
    public void onTeamButtonClick() {
        try {
            FXRouter.loadPage("team-members","info");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
