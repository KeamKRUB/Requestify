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
import ku.cs.models.request.RequestList;
import ku.cs.models.request.Subject;
import ku.cs.models.request.Request;
import ku.cs.models.request.SubjectList;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class StudentRequestLeaveAbsenceController {
    @FXML private Label requestTopicLabel;
    @FXML private Label requestTypeLabel;

    @FXML private TextField subjectIdTextField;
    @FXML private TextField lecturerNameTextField;
    @FXML private TextField notationTextField;

    @FXML private TextField amountTextField1;
    @FXML private TextField amountTextField2;
    @FXML private TextField amountTextField3;
    @FXML private TextField amountTextField4;
    @FXML private TextField amountTextField5;
    @FXML private TextField amountTextField6;
    @FXML private TextField amountTextField7;
    @FXML private TableView<Subject> requestSubjectTableView;

    @FXML private Label errorYearLabel;
    @FXML private Label errorIdLabel;
    @FXML private Label errorNameLabel;
    @FXML private Label errorListLabel;

    private Request newRequest;
    private DataRepository dataRepository;
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

        errorYearLabel.setVisible(false);
        errorIdLabel.setVisible(false);
        errorNameLabel.setVisible(false);
        errorListLabel.setVisible(false);

        String[] fullText = newRequest.getSpecialTopic().split("-");
        if (fullText.length == 7) {
            amountTextField1.setText(fullText[0]);
            amountTextField2.setText(fullText[1]);
            amountTextField3.setText(fullText[2]);
            amountTextField4.setText(fullText[3]);
            amountTextField5.setText(fullText[4]);
            amountTextField6.setText(fullText[5]);
            amountTextField7.setText(fullText[6]);
        }

        TableColumn<Subject, String> subjectIdCol = new TableColumn<>("รหัสวิชา\nCourse code");
        subjectIdCol.setCellValueFactory(new PropertyValueFactory<>("subjectId"));

        TableColumn<Subject, String> subjectNameCol = new TableColumn<>("ชื่ออาจารย์ผู้สอน");
        subjectNameCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        requestSubjectTableView.getColumns().clear();
        requestSubjectTableView.getColumns().addAll(
                subjectIdCol,
                subjectNameCol
        );

        showSubjectList();

        //ขยาย Table View ให้เหมาะกับข้อความ
        requestSubjectTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            double totalColumnsWidth = subjectIdCol.getWidth() +
                    subjectNameCol.getWidth();

            if (totalColumnsWidth < tableWidth) {
                requestSubjectTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                requestSubjectTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });

        requestSubjectTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Subject>() {
                    @Override
                    public void changed(ObservableValue<? extends Subject> observable, Subject oldValue, Subject newValue) {
                        if (newValue != null) {
                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ku/cs/views/student-request-leave-edit-popup.fxml"));
                                        Parent root = loader.load();
                                        StudentRequestSubjectEditController controller = loader.getController();
                                        controller.setSubject(newValue);
                                        controller.setSubjectList(requestSubjectList);

                                        Stage popupStage = new Stage();
                                        popupStage.initModality(Modality.APPLICATION_MODAL);
                                        popupStage.setTitle("Subject Editing");
                                        popupStage.setScene(new Scene(root));
                                        popupStage.showAndWait();

                                        requestSubjectTableView.getSelectionModel().clearSelection();
                                        showSubjectList();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                }
        );
    }

    private void showSubjectList(){
        requestSubjectTableView.getItems().clear();
        FilteredList<Subject> filteredSubjects = new FilteredList<>(FXCollections.observableArrayList(
                requestSubjectList
                        .getSubjects()), subject -> true);

        sortedSubjects = new SortedList<>(filteredSubjects);
        requestSubjectTableView.getItems().addAll(sortedSubjects);
    }


    @FXML void onAddButtonClick(){
        String subjectId = subjectIdTextField.getText();
        String lecturerName = lecturerNameTextField.getText();

        Subject newSubject = new Subject(subjectId,lecturerName,newRequest.getRequestId());

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
            if (lecturerName == null || lecturerName.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            newSubject.setSubjectName(lecturerName);
            errorNameLabel.setVisible(false);
        } catch (IllegalArgumentException e) {
            errorNameLabel.setText(e.getMessage());
            errorNameLabel.setVisible(true);
            hasError = true;
        }

        if (!hasError) {
            requestSubjectList.addSubject(newSubject);

            requestSubjectTableView.getItems().add(newSubject);

            errorIdLabel.setVisible(false);
            errorNameLabel.setVisible(false);
            errorListLabel.setVisible(false);
            updateTextField();
        }
    }

    private void updateTextField(){
        subjectIdTextField.clear();
        lecturerNameTextField.clear();
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
        String[] amounts = {
                amountTextField1.getText(),
                amountTextField2.getText(),
                amountTextField3.getText(),
                amountTextField4.getText(),
                amountTextField5.getText(),
                amountTextField6.getText(),
                amountTextField7.getText()
        };

        String fullText = String.join("-", amounts);
        newRequest.loadSubjectList(requestSubjectList);
        newRequest.setRequestNotation(notationTextField.getText());
        newRequest.setSpecialReason(fullText);

        for (String amount : amounts) {
            if (amount == null || amount.isEmpty()) {
                errorYearLabel.setText("Amount cannot be empty");
                errorYearLabel.setVisible(true);
                break;
            }
            try {
                Integer.parseInt(amount);
            } catch (NumberFormatException e) {
                errorYearLabel.setText("Numbers only");
                errorYearLabel.setVisible(true);
                break;
            }
        }

        try {
            if (requestSubjectList.getSize() == 0) {
                throw new IllegalArgumentException("Subject list is empty");
            }

            try {
                FXRouter.loadPage("student-request-student-info", newRequest,"student-request-leave-page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }catch (IllegalArgumentException e) {
            errorListLabel.setText(e.getMessage());
            errorListLabel.setVisible(true);
        }
        }
    }


