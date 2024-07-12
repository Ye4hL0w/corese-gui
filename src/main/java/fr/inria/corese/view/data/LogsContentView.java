package fr.inria.corese.view.data;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The {@link LogsContentView} class provides a view for displaying logs within the application.
 * <p>
 * It allows logging of various events, such as file loading, and displays these logs
 * with timestamps.
 * </p>
 */
public class LogsContentView {
    public VBox logContent;

    /**
     * Creates the content view for displaying logs.
     *
     * @return the {@link VBox} containing the logs UI
     */
    public VBox createLogsContent() {
        VBox vbLogs = new VBox();
        VBox.setVgrow(vbLogs, Priority.ALWAYS);
        vbLogs.setPadding(new Insets(10));
        vbLogs.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbLogs.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));

        logContent = new VBox();
        scrollPane.setContent(logContent);

        vbLogs.getChildren().add(scrollPane);

        addLogMessage("Initialization:", Color.BLACK);

        scrollPane.layout();
        scrollPane.setVvalue(1.0);

        return vbLogs;
    }

    /* Show the logs */

    /**
     * Adds a log message to the log view.
     *
     * @param message the log message to add
     * @param color   the color of the log message text
     */
    private void addLogMessage(String message, Color color) {
        TextFlow logMessageFlow = new TextFlow();
        logMessageFlow.setMaxWidth(Double.MAX_VALUE);

        Text timestamp = new Text(getCurrentTimestamp());
        timestamp.setFont(Font.font("Arial", 16));
        timestamp.setFill(color);

        Text logText = new Text(message);
        logText.setFont(Font.font("Arial", 16));
        logText.setFill(color);

        logMessageFlow.getChildren().addAll(timestamp, logText);

        logContent.getChildren().add(logMessageFlow);
    }

    /**
     * Gets the current timestamp formatted as HH:mm:ss.
     *
     * @return the current timestamp as a string
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(new Date()) + "] ";
    }

    /**
     * Logs the file loading event with the file's path and extension.
     *
     * @param file the file that is being loaded
     */
    public void logFileLoading(File file) {
//        String loadingStr = "Loading " + getFileExtension(file) + " File from path : ";
//        Text loadingText = new Text(loadingStr);
//        loadingText.setFill(Color.rgb(128,128,128));
        addLogMessage("Loading "+ getFileExtension(file) + " File from path: " + file.getAbsolutePath()  + "\n", Color.rgb(184,134,11));
        addLogMessage("Loading is done\n", Color.rgb(128,128,128));
//        addLogMessage(" ", Color.WHITE);
    }

    /**
     * Gets the file extension of a given file.
     *
     * @param file the file whose extension is to be retrieved
     * @return the file extension as a string
     */
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

}
