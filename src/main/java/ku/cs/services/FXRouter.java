package ku.cs.services;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FXRouter {
    private static final String WINDOW_TITLE = "";
    private static final Double WINDOW_WIDTH = 800.0D;
    private static final Double WINDOW_HEIGHT = 600.0D;
    private static final Double FADE_ANIMATION_DURATION = 800.0D;
    private static FXRouter router;
    private static Object mainRef;
    private static Stage window;
    private static String windowTitle;
    private static Double windowWidth;
    private static Double windowHeight;
    private static String animationType;
    private static Double animationDuration;
    private static AbstractMap<String, RouteScene> routes = new HashMap<>();
    private static RouteScene currentRoute;
    private static List<String> stylesheetPaths = new ArrayList<>();
    private static String fontSize = "medium-font";
    private static  String font = "font1";
    private static String currentPath;
    private FXRouter() {}

    public static void bind(Object ref, Stage win) {
        checkInstances(ref, win);
    }

    public static void bind(Object ref, Stage win, String winTitle) {
        checkInstances(ref, win);
        windowTitle = winTitle;
    }

    public static void bind(Object ref, Stage win, double winWidth, double winHeight) {
        checkInstances(ref, win);
        windowWidth = winWidth;
        windowHeight = winHeight;
    }

    public static void bind(Object ref, Stage win, String winTitle, double winWidth, double winHeight) {
        checkInstances(ref, win);
        windowTitle = winTitle;
        windowWidth = winWidth;
        windowHeight = winHeight;
    }

    private static void checkInstances(Object ref, Stage win) {
        if (mainRef == null) {
            mainRef = ref;
        }

        if (router == null) {
            router = new FXRouter();
        }

        if (window == null) {
            window = win;
        }
    }

    public static void when(String routeLabel, String scenePath) {
        RouteScene routeScene = new RouteScene(scenePath);
        routes.put(routeLabel, routeScene);
    }

    public static void when(String routeLabel, String scenePath, String lastPage) {
        RouteScene routeScene = new RouteScene(scenePath, lastPage);
        routes.put(routeLabel, routeScene);
    }

    public static void when(String routeLabel, String scenePath, double sceneWidth, double sceneHeight) {
        RouteScene routeScene = new RouteScene(scenePath, sceneWidth, sceneHeight);
        routes.put(routeLabel, routeScene);
    }

    public static void when(String routeLabel, String scenePath, String winTitle, double sceneWidth, double sceneHeight) {
        RouteScene routeScene = new RouteScene(scenePath, winTitle, sceneWidth, sceneHeight);
        routes.put(routeLabel, routeScene);
    }

    public static void goTo(String routeLabel) throws IOException {
        RouteScene route = routes.get(routeLabel);
        loadNewRoute(route);
    }

    public static void loadPage(String routeLabel) throws IOException {
        RouteScene route = routes.get(routeLabel);
        if (route != null) {
            loadContent(route);
        }
    }

    public static void loadPage(String routeLabel, Object data) throws IOException {
        RouteScene route = routes.get(routeLabel);
        route.data = data;
        loadContent(route);
    }

    public static void loadPage(String routeLabel, Object data, String lastPage) throws IOException {
        RouteScene route = routes.get(routeLabel);
        route.previousPage = lastPage;
        route.data = data;
        loadContent(route);
    }

    public static void goTo(String routeLabel, Object data) throws IOException {
        RouteScene route = routes.get(routeLabel);
        route.data = data;
        loadNewRoute(route);
    }

    private static void loadNewRoute(RouteScene route) throws IOException {
        currentRoute = route;
        String scenePath = "/" + route.scenePath;
        FXMLLoader loader = new FXMLLoader(FXRouter.class.getResource(scenePath));
        Parent resource = loader.load();
        Scene scene = new Scene(resource, route.sceneWidth, route.sceneHeight);

        if (!stylesheetPaths.isEmpty()) {
            for (String path : stylesheetPaths) {
                scene.getStylesheets().add(FXRouter.class.getResource(path).toExternalForm());
            }
        }

        window.setTitle(route.windowTitle);
        window.setScene(scene);
        window.show();
        routeAnimation(resource);
    }
    //
    private static void loadContent(RouteScene route) throws IOException {
        currentRoute = route;
        String scenePath = "/" + route.scenePath;
        FXMLLoader loader = new FXMLLoader(FXRouter.class.getResource(scenePath));
        Parent newContent = loader.load();

        if (!stylesheetPaths.isEmpty()) {
            newContent.getStylesheets().clear();
            newContent.getStyleClass().removeAll("small-font","medium-font","large-font");
            newContent.getStyleClass().removeAll("font1","font2","font3","font4");
            for (String path : stylesheetPaths) {
                newContent.getStylesheets().add(FXRouter.class.getResource(path).toExternalForm());
            }
            newContent.getStyleClass().add(fontSize);
            newContent.getStyleClass().add(font);
            System.out.println("Applied Stylesheets to new content: " + newContent.getStylesheets());
        }

        if (window != null && window.getScene() != null) {
            Parent root = window.getScene().getRoot();
            if (root != null) {
                root.getStylesheets().clear();
                for (String path : stylesheetPaths) {
                        root.getStylesheets().add(FXRouter.class.getResource(path).toExternalForm());
                    }
                if (root instanceof BorderPane) {
                    ((BorderPane) root).setCenter(newContent);
                } else if (root instanceof VBox) {
                    ((VBox) root).getChildren().setAll(newContent);
                } else {
                    throw new UnsupportedOperationException("Unsupported root node type: " + root.getClass().getName());
                }
            } else {
                throw new IllegalStateException("No root node available.");
            }
        } else {
            throw new IllegalStateException("No scene is currently loaded.");
        }
    }

    public static void startFrom(String routeLabel) throws Exception {
        goTo(routeLabel);
    }

    public static void startFrom(String routeLabel, Object data) throws Exception {
        goTo(routeLabel, data);
    }

    public static void setAnimationType(String anType) {
        animationType = anType;
    }

    public static void setAnimationType(String anType, double anDuration) {
        animationType = anType;
        animationDuration = anDuration;
    }

    private static void routeAnimation(Parent node) {
        String anType = animationType != null ? animationType.toLowerCase() : "";
        if ("fade".equals(anType)) {
            Double fd = animationDuration != null ? animationDuration : FADE_ANIMATION_DURATION;
            FadeTransition ftCurrent = new FadeTransition(Duration.millis(fd), node);
            ftCurrent.setFromValue(0.0D);
            ftCurrent.setToValue(1.0D);
            ftCurrent.play();
        }
    }

    public static Object getData() {
        return currentRoute.data;
    }

    public static String getPreviousPage() {
        return currentRoute.previousPage;
    }

    public static void setPreviousPage(String previousPage) {
        currentRoute.previousPage = previousPage;
    }

    public static void addStylesheet(String path) {
        stylesheetPaths.add(path);
    }

    public static void setFont(String newFont){
        font = newFont;
    }

    public static void setFontSize(String newFontSize){
        fontSize = newFontSize;
    }

    public static void removeStylesheet(String path) {
        stylesheetPaths.remove(path);
    }

    private static class RouteScene {
        private String scenePath;
        private String windowTitle;
        private double sceneWidth;
        private double sceneHeight;
        private Object data;
        private String previousPage;

        private RouteScene(String scenePath) {
            this(scenePath, getWindowTitle(), getWindowWidth(), getWindowHeight());
        }

        private RouteScene(String scenePath, String windowTitle) {
            this(scenePath, windowTitle, getWindowWidth(), getWindowHeight());
        }

        private RouteScene(String scenePath, double sceneWidth, double sceneHeight) {
            this(scenePath, getWindowTitle(), sceneWidth, sceneHeight);
        }

        private RouteScene(String scenePath, String windowTitle, double sceneWidth, double sceneHeight) {
            this.scenePath = scenePath;
            this.windowTitle = windowTitle;
            this.sceneWidth = sceneWidth;
            this.sceneHeight = sceneHeight;
        }

        private static String getWindowTitle() {
            return FXRouter.windowTitle != null ? FXRouter.windowTitle : "";
        }

        private static double getWindowWidth() {
            return FXRouter.windowWidth != null ? FXRouter.windowWidth : FXRouter.WINDOW_WIDTH;
        }

        private static double getWindowHeight() {
            return FXRouter.windowHeight != null ? FXRouter.windowHeight : FXRouter.WINDOW_HEIGHT;
        }
    }
}