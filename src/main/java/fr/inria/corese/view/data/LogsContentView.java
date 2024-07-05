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

public class LogsContentView {
    public VBox logContent;

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

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(new Date()) + "] ";
    }

    public void logFileLoading(File file) {
//        String loadingStr = "Loading " + getFileExtension(file) + " File from path : ";
//        Text loadingText = new Text(loadingStr);
//        loadingText.setFill(Color.rgb(128,128,128));
        addLogMessage("Loading "+ getFileExtension(file) + " File from path: " + file.getAbsolutePath()  + "\n", Color.rgb(184,134,11));
        addLogMessage("Loading is done\n", Color.rgb(128,128,128));
//        addLogMessage(" ", Color.WHITE);
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

}
