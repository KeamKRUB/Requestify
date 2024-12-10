package ku.cs.controllers.request;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class PdfPopupController {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox container;
    private String requestPath;

    @FXML
    public void initialize() {
        requestPath = "";
    }

    public void addImageView(ImageView imageView) {
        container.getChildren().add(imageView);
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    @FXML
    public void downloadPdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        Node source = (Node) event.getSource();
        File file = fileChooser.showSaveDialog(source.getScene().getWindow());
        if (file != null){
            try {
                Files.copy(Paths.get(requestPath), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
