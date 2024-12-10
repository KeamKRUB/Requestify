module ku.cs {
    requires javafx.controls;
    requires javafx.fxml;
    requires bcrypt;
    requires java.prefs;
    requires java.desktop;
    requires java.compiler;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires kernel;
    requires layout;
    requires io;


    opens ku.cs.cs211671project to javafx.fxml;
    exports ku.cs.cs211671project;
    exports ku.cs.controllers;
    opens ku.cs.controllers to javafx.fxml;

    exports ku.cs.controllers.admin;
    opens ku.cs.controllers.admin to javafx.fxml;
    exports ku.cs.controllers.advisor;
    opens ku.cs.controllers.advisor to javafx.fxml;
    exports ku.cs.controllers.department;
    opens ku.cs.controllers.department to javafx.fxml;
    exports ku.cs.controllers.faculty;
    opens ku.cs.controllers.faculty to javafx.fxml;
    exports ku.cs.controllers.component;
    opens ku.cs.controllers.component to javafx.fxml;
    exports ku.cs.controllers.user;
    opens ku.cs.controllers.user to javafx.fxml;
    exports ku.cs.controllers.student;
    opens ku.cs.controllers.student to javafx.fxml;

    exports ku.cs.models.advisor;
    opens ku.cs.models.advisor to javafx.base;
    exports ku.cs.models.department;
    opens ku.cs.models.department to javafx.base;
    exports ku.cs.models.faculty;
    opens ku.cs.models.faculty to javafx.base;
    exports ku.cs.models.student;
    opens ku.cs.models.student to javafx.base;
    exports ku.cs.models.user;
    opens ku.cs.models.user to javafx.base;
    exports ku.cs.models.component;
    opens ku.cs.models.component to javafx.base;
    exports ku.cs.models.request;
    opens ku.cs.models.request to javafx.base;
    exports ku.cs.controllers.request;
    opens ku.cs.controllers.request to javafx.fxml;
}