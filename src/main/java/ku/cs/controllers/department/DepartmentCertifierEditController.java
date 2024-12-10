package ku.cs.controllers.department;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ku.cs.models.advisor.AdvisorList;
import ku.cs.models.department.DepartmentCertifier;
import ku.cs.models.department.DepartmentCertifierList;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.user.UserList;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;
import java.util.Optional;

public class DepartmentCertifierEditController {
    @FXML
    private TextField certifierFirstName;
    @FXML
    private TextField certifierLastName;
    @FXML
    private TextField certifierPosition;

    private DepartmentCertifier certifier;
    private DepartmentCertifierList departmentCertifierList;
    private DataRepository dataRepository;
    private DepartmentStaff user;

    @FXML
    public void initialize() {


        dataRepository = DataRepository.getDataRepository();
        departmentCertifierList = dataRepository.getDepartmentCertifierList();

    }

    @FXML
    public void onConfirmButtonClick() {
        certifier.setFirstName(certifierFirstName.getText());
        certifier.setLastName(certifierLastName.getText());
        certifier.setPosition(certifierPosition.getText());

        String firstName = certifierFirstName.getText();
        String lastName = certifierLastName.getText();
        String position = certifierPosition.getText();
        //check empty word

        if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("แก้ไข Certifier");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else {
            certifier.setFirstName(firstName);
            certifier.setLastName(lastName);
            certifier.setPosition(position);
            dataRepository.writeData(DepartmentCertifierList.class);

            Stage stage = (Stage) certifierFirstName.getScene().getWindow();
            stage.close();
        }

    }

    public void setCertifier(DepartmentCertifier certifier) {
        this.certifier = certifier;
        certifierFirstName.setText(certifier.getFirstName());
        certifierLastName.setText(certifier.getLastName());
        certifierPosition.setText(certifier.getPosition());


    }

    public void setDepartmentCertifierList(DepartmentCertifierList departmentCertifierList) {
        this.departmentCertifierList = departmentCertifierList;
    }

    @FXML
    public void onCancelButtonClick() {
        Stage stage = (Stage) certifierFirstName.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Remove Department Staff");
        alert.setContentText("คุณแน่ใจหรือไม่ว่าต้องการลบเจ้าหน้าที่ภาควิชา ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            departmentCertifierList.removeCertifier(certifier);
            dataRepository.writeData(DepartmentCertifierList.class);
            Stage stage = (Stage) certifierFirstName.getScene().getWindow();
            stage.close();

        }
    }
}
