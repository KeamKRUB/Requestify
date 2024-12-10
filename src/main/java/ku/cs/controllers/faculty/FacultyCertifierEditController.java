package ku.cs.controllers.faculty;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ku.cs.models.faculty.FacultyCertifier;
import ku.cs.models.faculty.FacultyCertifierList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;
import java.util.Optional;

public class FacultyCertifierEditController {
    @FXML private TextField certifierFirstName;
    @FXML private TextField certifierLastName;
    @FXML private TextField certifierPosition;
    private FacultyCertifier certifier = (FacultyCertifier) FXRouter.getData();
    private FacultyCertifierList facultyCertifierList;

    private DataRepository dataRepository;

    @FXML
    public void initialize() {

        dataRepository = DataRepository.getDataRepository();
        facultyCertifierList = dataRepository.getFacultyCertifierList();

    }
    //ActionButton
    @FXML
    public void onConfirmButtonClick() {
        String firstName = certifierFirstName.getText();
        String lastName = certifierLastName.getText();
        String position = certifierPosition.getText();
        if(firstName.isEmpty() || lastName.isEmpty() || position.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Edit Faculty Certifier");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else{
            certifier.setFirstName(firstName);
            certifier.setLastName(lastName);
            certifier.setPosition(position);
            dataRepository.writeData(FacultyCertifierList.class);

            Stage stage = (Stage) certifierFirstName.getScene().getWindow();
            stage.close();
        }
    }

    public void setCertifier (FacultyCertifier certifier) {
        this.certifier = certifier;
        certifierFirstName.setText(certifier.getFirstName());
        certifierLastName.setText(certifier.getLastName());
        certifierPosition.setText(certifier.getPosition());


    }

    public void setFacultyCertifierList (FacultyCertifierList facultyCertifierList) {
        this.facultyCertifierList = facultyCertifierList;
    }

    @FXML
    public void onCancelButtonClick() {
        Stage stage = (Stage) certifierFirstName.getScene().getWindow();
        stage.close();
    }

    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove Faculty Staff?");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบเจ้าหน้าที่คณะ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            facultyCertifierList.removeCertifier(certifier);
            dataRepository.writeData(FacultyCertifierList.class);
            Stage stage = (Stage) certifierFirstName.getScene().getWindow();
            stage.close();
        }
    }
}
