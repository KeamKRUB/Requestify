package ku.cs.controllers.faculty;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.faculty.FacultyCertifier;
import ku.cs.models.faculty.FacultyCertifierList;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.user.UserSession;
import ku.cs.services.DataRepository;

import java.io.IOException;
import java.util.Comparator;

public class FacultyCertifierController {
    @FXML private TableView<FacultyCertifier> facultyCertifierTableView;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField positionTextField;
    @FXML private TextField searchTextField;

    private DataRepository dataRepository;
    private FacultyCertifierList facultyCertifierList;

    private FilteredList<FacultyCertifier> filteredCertifiersList;
    private SortedList<FacultyCertifier> sortedCertifiers;

    private FacultyStaff user;
    @FXML
    public void initialize() {
        UserSession userSession = UserSession.getSession();
        user = (FacultyStaff) userSession.getLoggedInUser();

        dataRepository = DataRepository.getDataRepository();
        dataRepository.reloadData(FacultyCertifierList.class);

        facultyCertifierList = dataRepository.getFacultyCertifierList();

        TableColumn<FacultyCertifier, String> firstNameCol = new TableColumn<>("ชื่อ");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<FacultyCertifier, String> lastNameCol = new TableColumn<>("นามสกุล");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<FacultyCertifier, String> positionCol = new TableColumn<>("ตำแหน่ง");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        facultyCertifierTableView.getColumns().clear();
        facultyCertifierTableView.getColumns().addAll(firstNameCol, lastNameCol, positionCol);

        showFacultyCertifiers();

        facultyCertifierTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<FacultyCertifier>() {

                    @Override
                    public void changed(ObservableValue<? extends FacultyCertifier> observableValue, FacultyCertifier facultyCertifier, FacultyCertifier newValue) {
                        if (newValue != null) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/faculty-certifier-edit-popup.fxml"));
                                    Parent root = loader.load();
                                    FacultyCertifierEditController controller = loader.getController();
                                    controller.setCertifier(newValue);
                                    controller.setFacultyCertifierList(facultyCertifierList);

                                    Stage popupStage = new Stage();
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.setTitle("Certifier Editing");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.showAndWait();

                                    facultyCertifierTableView.getSelectionModel().clearSelection();
                                    showFacultyCertifiers();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
        );

        //ขยาย Table View ให้เหมาะกับข้อความ
        facultyCertifierTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = firstNameCol.getWidth() +
                    lastNameCol.getWidth() +
                    positionCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                facultyCertifierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                facultyCertifierTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    private void showFacultyCertifiers() {
        facultyCertifierTableView.getItems().clear();
        filteredCertifiersList = new FilteredList<>(FXCollections.observableArrayList(
                facultyCertifierList
                        .getCertifierListByFaculty(user.getFaculty())
                        .getCertifierList()), certifier -> true);

        sortedCertifiers = new SortedList<>(filteredCertifiersList);
        sortedCertifiers.setComparator(Comparator.comparingInt(certifier -> certifier.getPosition().length()));
        facultyCertifierTableView.getItems().addAll(sortedCertifiers);
    }

    @FXML
    public void onSearchButtonClick() {
        String searchText = searchTextField.getText().toLowerCase();

        filteredCertifiersList.setPredicate(certifier -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            else{
                return certifier.textToSearch().contains(searchText);
            }
        });

        facultyCertifierTableView.setItems(new SortedList<>(filteredCertifiersList));
    }

    public void onAddButtonClick() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String position = positionTextField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Faculty Certifier");
            alert.setContentText("กรุณาใส่ข้อมูลให้ครบทุกช่อง");
            alert.showAndWait();
        } else {
            String faculty = user.getFaculty();
            facultyCertifierList.addCertifier(firstName, lastName, faculty, position);
            dataRepository.writeData(FacultyCertifierList.class);

            showFacultyCertifiers();

            firstNameTextField.clear();
            lastNameTextField.clear();
            positionTextField.clear();
        }
    }
}
