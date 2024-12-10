package ku.cs.models.component;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ku.cs.services.FXRouter;

import java.io.IOException;

public class Navbar {

    private static Navbar instance;
    private VBox topNavbar;
    private VBox bottomNavbar;
    private Button activeButton;
    private String currentPath;

    private Navbar(VBox topNavbar, VBox bottomNavbar) {
        this.topNavbar = topNavbar;
        this.bottomNavbar = bottomNavbar;
    }

    public static Navbar getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Navbar not initialized");
        }
        return instance;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public static void initializeInstance(VBox topNavbar, VBox bottomNavbar) {
        if (instance == null) {
            instance = new Navbar(topNavbar, bottomNavbar);
        }
    }

    public static void resetInstance(){
        instance = null;
    }

    public void setButton(String role) {
        topNavbar.getChildren().clear();
        bottomNavbar.getChildren().clear();
        switch (role) {
            case "Admin":
                Button dashboardButton = new Button("Dashboard");
                setActiveButton(dashboardButton);
                addButtonToTopNavbar(dashboardButton, "Dashboard", "admin-dashboard");
                addButtonToTopNavbar(new Button("Account"), "Account", "admin-account");
                addButtonToTopNavbar(new Button("Faculty"), "Faculty", "admin-faculty");
                addButtonToTopNavbar(new Button("Department"), "Department", "admin-department");
                addButtonToTopNavbar(new Button("Advisor"), "Advisor", "admin-advisor");
                this.currentPath = "admin-dashboard";
                break;
            case "Student":
                Button studentRequestButton = new Button("Request");
                setActiveButton(studentRequestButton);
                addButtonToTopNavbar(studentRequestButton, "Request", "student-request");
                this.currentPath = "student-request";
                break;
            case "Advisor":
                Button advisorRequestButton = new Button("Request");
                setActiveButton(advisorRequestButton);
                addButtonToTopNavbar(advisorRequestButton, "Request", "advisor-request");
                addButtonToTopNavbar(new Button("Student"), "Student", "advisor-student");
                this.currentPath = "advisor-request";
                break;
            case "DepartmentStaff":
                Button dapartmentRequestButton = new Button("Request");
                setActiveButton(dapartmentRequestButton);
                addButtonToTopNavbar(dapartmentRequestButton, "Request", "department-request");
                addButtonToTopNavbar(new Button("Student"), "Student", "department-student");
                addButtonToTopNavbar(new Button("Certifier"), "Certifier", "department-certifier");
                this.currentPath = "department-request";
                break;
            case "FacultyStaff":
                Button facultyRequestButton = new Button("Request");
                setActiveButton(facultyRequestButton);
                addButtonToTopNavbar(facultyRequestButton, "Request", "faculty-request");
                addButtonToTopNavbar(new Button("Certifier"), "Certifier", "faculty-certifier");
                this.currentPath = "faculty-request";
                break;
        }
        addButtonToBottomNavbar(new Button("Setting"), "Setting", "setting");
        addButtonToBottomNavbar(new Button("Info"), "Info", "info");
    }

    private void addButtonToTopNavbar(Button button, String buttonName, String page) {
        button.setOnAction(e -> handleButtonAction(page,buttonName));
        button.getStyleClass().add("top-nav-button");
        topNavbar.getChildren().add(button);
    }

    private void handleButtonAction(String page, String buttonName) {
        try {
            FXRouter.loadPage(page);
            highlightButton(buttonName);
            this.currentPath = page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void addButtonToBottomNavbar(Button button, String buttonName, String page) {
        button.setOnAction(e -> handleButtonAction(page,buttonName));
        button.getStyleClass().add("bottom-nav-button");
        bottomNavbar.getChildren().add(button);
    }

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }
        button.getStyleClass().add("active");
        activeButton = button;
    }

    public void highlightButton(String route) {
        for (var node : topNavbar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText().equals(route)) {
                    setActiveButton(button);
                    break;
                }
            }
        }
    }
}
