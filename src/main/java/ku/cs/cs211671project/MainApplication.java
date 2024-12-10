package ku.cs.cs211671project;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ku.cs.services.DataRepository;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXRouter.bind(this, stage, "CS211 Project", 1080, 720);
        configRoutes();

        Font.loadFont(getClass().getResource("/fonts/THSarabunPSK.ttf").toExternalForm(), 12);

        FXRouter.addStylesheet("/styles/blue-theme.css");
        FXRouter.addStylesheet("/styles/font-size.css");
        FXRouter.addStylesheet("/styles/font.css");
        FXRouter.addStylesheet("/styles/navbar.css");
        FXRouter.addStylesheet("/styles/global.css");
        FXRouter.goTo("login-page");
    }

    private void configRoutes() {
        String viewPath = "ku/cs/views/";
        FXRouter.when("main-layout", viewPath + "main-layout.fxml");

        FXRouter.when("login-page", viewPath + "login-new-page.fxml");
        FXRouter.when("register-first-page", viewPath + "register-first-page.fxml");
        FXRouter.when("register-second-page", viewPath + "register-second-page.fxml");
        FXRouter.when("profile-page", viewPath + "profile-page.fxml");
        FXRouter.when("setting", viewPath + "setting.fxml");
        FXRouter.when("info", viewPath + "info.fxml");
        FXRouter.when("team-members", viewPath + "team-members.fxml");
        FXRouter.when("template", viewPath + "template.fxml");

        FXRouter.when("student-profile-page", viewPath + "student-profile-page.fxml");
        FXRouter.when("student-request", viewPath + "student-request.fxml");
        FXRouter.when("student-request-choose-request-page", viewPath + "student-request-choose-request-page.fxml");
        FXRouter.when("student-request-ku-one-three-page", viewPath + "student-request-ku-one-three-page.fxml");
        FXRouter.when("student-request-fee-relaxing-page", viewPath + "student-request-fee-relaxing-page.fxml");
        FXRouter.when("student-request-reason-page", viewPath + "student-request-reason-page.fxml");
        FXRouter.when("student-request-student-info", viewPath + "student-request-student-info.fxml");
        FXRouter.when("student-request-confirmation", viewPath + "student-request-confirmation.fxml");
        FXRouter.when("student-request-details-ku-one-three", viewPath + "student-request-details-ku-one-three.fxml");
        FXRouter.when("student-request-details-fee-relaxing", viewPath + "student-request-details-fee-relaxing.fxml");
        FXRouter.when("student-request-details-reason", viewPath + "student-request-details-reason.fxml");
        FXRouter.when("student-request-details-change-faculty-major", viewPath + "student-request-details-change-faculty-major.fxml");
        FXRouter.when("student-request-change-faculty-major-page", viewPath + "student-request-change-faculty-major-page.fxml");
        FXRouter.when("student-request-leave-page", viewPath + "student-request-leave-absence-page.fxml");
        FXRouter.when("student-request-details-leave", viewPath + "student-request-details-leave-absence.fxml");

        FXRouter.when("advisor-profile-page", viewPath + "advisor-profile-page.fxml");
        FXRouter.when("advisor-request", viewPath + "advisor-request.fxml");
        FXRouter.when("advisor-request-confirmation", viewPath + "advisor-request-confirmation.fxml");
        FXRouter.when("advisor-request-rejection", viewPath + "advisor-request-rejection.fxml");
        FXRouter.when("advisor-student", viewPath + "advisor-student.fxml");
        FXRouter.when("advisor-student-request", viewPath + "advisor-student-request.fxml");
        FXRouter.when("advisor-request-details", viewPath + "advisor-request-details.fxml");

        FXRouter.when("faculty-profile-page", viewPath + "faculty-profile-page.fxml");
        FXRouter.when("faculty-certifier", viewPath + "faculty-certifier.fxml");
        FXRouter.when("faculty-certifier-edit", viewPath + "faculty-certifier-edit.fxml");
        FXRouter.when("faculty-request", viewPath + "faculty-request.fxml");
        FXRouter.when("faculty-request-confirmation", viewPath + "faculty-request-confirmation.fxml");
        FXRouter.when("faculty-request-rejection", viewPath + "faculty-request-rejection.fxml");
        FXRouter.when("faculty-request-confirmation-certifier", viewPath + "faculty-request-confirmation-certifier.fxml");
        FXRouter.when("faculty-certifier-edit-popup", viewPath + "faculty-certifier-edit-popup.fxml");

        FXRouter.when("departmentstaff-profile-page", viewPath + "departmentstaff-profile-page.fxml");
        FXRouter.when("department-student", viewPath + "department-student.fxml");
        FXRouter.when("department-student-edit", viewPath + "department-student-edit.fxml");
        FXRouter.when("department-certifier", viewPath + "department-certifier.fxml");
        FXRouter.when("department-certifier-edit", viewPath + "department-certifier-edit.fxml");
        FXRouter.when("department-request", viewPath + "department-request.fxml");
        FXRouter.when("department-request-confirmation", viewPath + "department-request-confirmation.fxml");
        FXRouter.when("department-request-rejection", viewPath + "department-request-rejection.fxml");
        FXRouter.when("department-request-confirmation-certifier", viewPath + "department-request-confirmation-sent-certifier.fxml");
        FXRouter.when("department-request-confirmation-sent-certifier", viewPath + "department-request-confirmation-sent-certifier.fxml");
        FXRouter.when("department-request-confirmation-accept-certifier", viewPath + "department-request-confirmation-accept-certifier.fxml");
        FXRouter.when("department-student-edit-popup",viewPath + "department-student-edit-popup.fxml");
        FXRouter.when("department-certifier-edit-popup",viewPath + "department-certifier-edit-popup.fxml");


        FXRouter.when("admin-profile-page", viewPath + "admin-profile-page.fxml");
        FXRouter.when("admin-dashboard", viewPath + "admin-dashboard.fxml");
        FXRouter.when("admin-account", viewPath + "admin-account.fxml");
        FXRouter.when("admin-department", viewPath + "admin-department.fxml");
        FXRouter.when("admin-department-edit", viewPath + "admin-department-edit.fxml");
        FXRouter.when("admin-department-edit-staff", viewPath + "admin-department-edit-staff.fxml");
        FXRouter.when("admin-department-edit-staff-popup", viewPath + "admin-department-edit-staff-popup.fxml");
        FXRouter.when("admin-advisor", viewPath + "admin-advisor.fxml");
        FXRouter.when("admin-advisor-edit-popup", viewPath + "admin-advisor-edit-popup.fxml");
        FXRouter.when("admin-faculty", viewPath + "admin-faculty.fxml");
        FXRouter.when("admin-faculty-edit", viewPath + "admin-faculty-edit.fxml");
        FXRouter.when("admin-faculty-edit-staff", viewPath + "admin-faculty-edit-staff.fxml");
        FXRouter.when("admin-faculty-edit-staff-popup", viewPath + "admin-faculty-edit-staff-popup.fxml");

    }

    public static void main(String[] args) {
        launch();
    }
}