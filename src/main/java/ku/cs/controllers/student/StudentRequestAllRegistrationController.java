package ku.cs.controllers.student;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ku.cs.models.request.Subject;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.request.SubjectList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class StudentRequestAllRegistrationController {

    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;

    @FXML private TextField subjectIdTextField;
    @FXML private TextField subjectNameTextField;
    @FXML private MenuButton enrollTypeMenuButton;
    @FXML private TextField lectureSectionTextField;
    @FXML private TextField labSectionTextField;
    @FXML private TextField lectureCreditTextField;
    @FXML private TextField labCreditTextField;
    @FXML private TextField notationTextField;

    @FXML private Label errorIdLabel;
    @FXML private Label errorNameLabel;
    @FXML private Label errorTypeLabel;
    @FXML private Label errorSecLabel;
    @FXML private Label errorCredLabel;
    @FXML private Label errorListLabel;
    @FXML private TableView<Subject> requestSubjectTableView;

    private Request newRequest;
    private DataRepository dataRepository;

    private Subject previousSelectedSubject;
    private SubjectList requestSubjectList;
    private SortedList<Subject> sortedSubjects;

    @FXML
    public void initialize() {
        dataRepository = DataRepository.getDataRepository();

        if (FXRouter.getData() == null) {
            newRequest = new Request();
        } else {
            newRequest = (Request) FXRouter.getData();
        }

        if(newRequest.getSubjectList().getSize() != 0) {
            requestSubjectList = newRequest.getSubjectList();
        }
        else{
            requestSubjectList = new SubjectList();
        }

        requestTopicLabel.setText(newRequest.getRequestTopic());
        requestTypeLabel.setText(newRequest.getRequestType());
        notationTextField.setText(newRequest.getRequestNotation());

        errorIdLabel.setVisible(false);
        errorNameLabel.setVisible(false);
        errorTypeLabel.setVisible(false);
        errorSecLabel.setVisible(false);
        errorCredLabel.setVisible(false);
        errorListLabel.setVisible(false);

        TableColumn<Subject, String> subjectIdCol = new TableColumn<>("รหัสวิชา");
        subjectIdCol.setCellValueFactory(new PropertyValueFactory<>("subjectId"));

        TableColumn<Subject, String> subjectNameCol = new TableColumn<>("ชื่อวิชา");
        subjectNameCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<Subject, String> subjectEnrollTypeCol = new TableColumn<>("ประเภท");
        subjectEnrollTypeCol.setCellValueFactory(new PropertyValueFactory<>("enrollType"));

        TableColumn<Subject, String> subjectSectionCol = new TableColumn<>("หมู่เรียน");
        TableColumn<Subject, String> lectureSectionCol = new TableColumn<>("บรรยาย");
        lectureSectionCol.setCellValueFactory(new PropertyValueFactory<>("lectureSection"));

        TableColumn<Subject, String> labSectionCol = new TableColumn<>("ปฏิบัติ");
        labSectionCol.setCellValueFactory(new PropertyValueFactory<>("labSection"));

        subjectSectionCol.getColumns().addAll(lectureSectionCol, labSectionCol);

        TableColumn<Subject, String> subjectCreditCol = new TableColumn<>("หน่วยกิต");
        TableColumn<Subject, String> lectureCreditCol = new TableColumn<>("บรรยาย");
        lectureCreditCol.setCellValueFactory(new PropertyValueFactory<>("lectureCredit"));

        TableColumn<Subject, String> labCreditCol = new TableColumn<>("ปฏิบัติ");
        labCreditCol.setCellValueFactory(new PropertyValueFactory<>("labCredit"));

        subjectCreditCol.getColumns().addAll(lectureCreditCol, labCreditCol);

        requestSubjectTableView.getColumns().clear();
        requestSubjectTableView.getColumns().addAll(
                subjectIdCol,
                subjectNameCol,
                subjectEnrollTypeCol,
                subjectSectionCol,
                subjectCreditCol
        );

        showSubjectList();

        requestSubjectTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Subject> observable, Subject oldValue, Subject newValue) {
                        if (newValue != null) {
                            if (newValue != previousSelectedSubject) {
                                previousSelectedSubject = newValue;

                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/student-request-registration-edit-popup.fxml"));
                                        Parent root = loader.load();

                                        StudentRequestFullSubjectEditController controller = loader.getController();
                                        controller.setSubject(newValue);
                                        controller.setSubjectList(requestSubjectList);

                                        Stage popupStage = new Stage();
                                        popupStage.initModality(Modality.APPLICATION_MODAL);
                                        popupStage.setTitle("Subject Editing");
                                        popupStage.setScene(new Scene(root));

                                        popupStage.showAndWait();

                                        requestSubjectTableView.getSelectionModel().clearSelection();
                                        previousSelectedSubject = null;
                                        showSubjectList();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                    }
                }
        );

        //ขยาย Table View ให้เหมาะกับข้อความ
        requestSubjectTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = subjectIdCol.getWidth() +
                    subjectNameCol.getWidth() +
                    subjectEnrollTypeCol.getWidth() +
                    lectureCreditCol.getWidth() +
                    labCreditCol.getWidth() +
                    lectureSectionCol.getWidth() +
                    labSectionCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                requestSubjectTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                requestSubjectTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
    }

    private void showSubjectList(){
        requestSubjectTableView.getItems().clear();

        FilteredList<Subject> filteredSubjects = new FilteredList<>(FXCollections.observableArrayList(
                requestSubjectList
                        .getSubjects()), subject -> true);

        sortedSubjects = new SortedList<>(filteredSubjects);

        requestSubjectTableView.getItems().addAll(sortedSubjects);
    }

    @FXML
    void onAddButtonClick() {
        String subjectId = subjectIdTextField.getText();
        String subjectName = subjectNameTextField.getText();
        String lectureSection = lectureSectionTextField.getText();
        String labSection = labSectionTextField.getText();
        String lectureCredit = lectureCreditTextField.getText();
        String labCredit = labCreditTextField.getText();
        String enrollType = enrollTypeMenuButton.getText();

        Subject newSubject = new Subject();
        newSubject.setRequestId(newRequest.getRequestId());

        boolean hasError = false;

        try {
            if (subjectId == null || subjectId.isEmpty()) {
                throw new IllegalArgumentException("Enter Id");
            }
            Integer.parseInt(subjectId);
            newSubject.setSubjectId(subjectId);
            errorIdLabel.setVisible(false);
        } catch (NumberFormatException e) {
            errorIdLabel.setText("Numbers only");
            errorIdLabel.setVisible(true);
            hasError = true;
        } catch (IllegalArgumentException e) {
            errorIdLabel.setText(e.getMessage());
            errorIdLabel.setVisible(true);
            hasError = true;
        }

        try {
            if (subjectName == null || subjectName.isEmpty()) {
                throw new IllegalArgumentException("Enter Name");
            }
            newSubject.setSubjectName(subjectName);
            errorNameLabel.setVisible(false);
        } catch (IllegalArgumentException e) {
            errorNameLabel.setText(e.getMessage());
            errorNameLabel.setVisible(true);
            hasError = true;
        }

        try {
            if (enrollType.equals("ประเภท")) {
                throw new IllegalArgumentException("Choose");
            }
            errorTypeLabel.setVisible(false);
            newSubject.setEnrollType(enrollType);
        } catch (IllegalArgumentException e) {
            errorTypeLabel.setText(e.getMessage());
            errorTypeLabel.setVisible(true);
            hasError = true;
        }

        try {
            if ((lectureCredit == null || lectureCredit.isEmpty()) && (labCredit == null || labCredit.isEmpty())) {
                throw new IllegalArgumentException("Enter Credit");
            }
            newSubject.setLectureCredit(lectureCredit);
            newSubject.setLabCredit(labCredit);
            errorCredLabel.setVisible(false);
        } catch (IllegalArgumentException e) {
            errorCredLabel.setText(e.getMessage());
            errorCredLabel.setVisible(true);
            hasError = true;
        }

        try {
            if ((lectureSection == null || lectureSection.isEmpty()) && (labSection == null || labSection.isEmpty())) {
                throw new IllegalArgumentException("Enter Section");
            }
            newSubject.setLectureSection(lectureSection);
            newSubject.setLabSection(labSection);
            errorSecLabel.setVisible(false);
        } catch (IllegalArgumentException e) {
            errorSecLabel.setText(e.getMessage());
            errorSecLabel.setVisible(true);
            hasError = true;
        }

        if (!hasError) {
            requestSubjectList.addSubject(newSubject);
            requestSubjectTableView.getItems().add(newSubject);

            errorIdLabel.setVisible(false);
            errorNameLabel.setVisible(false);
            errorTypeLabel.setVisible(false);
            errorSecLabel.setVisible(false);
            errorCredLabel.setVisible(false);
            errorListLabel.setVisible(false);
            updateTextField();
        }
    }

    private void updateTextField(){
        subjectIdTextField.clear();
        subjectNameTextField.clear();
        enrollTypeMenuButton.setText("ประเภท");
        lectureSectionTextField.clear();
        labSectionTextField.clear();
        lectureCreditTextField.clear();
        labCreditTextField.clear();
        notationTextField.clear();
    }


    @FXML void onCreditMenuButtonClick(){
        enrollTypeMenuButton.setText("Credit");
    }

    @FXML void onAuditMenuButtonClick(){
        enrollTypeMenuButton.setText("Audit");
    }

    @FXML
    public void onBackButtonClick() {
        dataRepository.writeData(RequestList.class);
        try {
            FXRouter.loadPage("student-request-choose-request-page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onConfirmButtonClick() {
        newRequest.setRequestNotation(notationTextField.getText());
        newRequest.loadSubjectList(requestSubjectList);
        try {
            if (requestSubjectList.getSize() == 0) {
                throw new IllegalArgumentException("Subject list is empty");
            }

            try {
                FXRouter.loadPage("student-request-student-info", newRequest,"student-request-ku-one-three-page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }catch (IllegalArgumentException e) {
            errorListLabel.setText(e.getMessage());
            errorListLabel.setVisible(true);
        }
    }


}
