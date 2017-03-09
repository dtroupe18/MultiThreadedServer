/**
 * Created by Dave on 3/8/17.
 */

import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client extends Application {
    // IO streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: red");
        paneForTextField.setLeft(new Label("Enter a radius: "));

        TextField textField = new TextField();
        textField.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setCenter(textField);

        BorderPane mainPane = new BorderPane();
        // display contents
        TextArea textArea = new TextArea();
        mainPane.setCenter(new ScrollPane(textArea));
        mainPane.setTop(paneForTextField);

        // create scene and place it on the stage
        Scene scene = new Scene(mainPane, 450, 300);
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        textField.setOnAction(e -> {
            try {
                double radius = Double.parseDouble(textField.getText().trim());

                // send radius to server
                toServer.writeDouble(radius);
                toServer.flush();

                // get area from server
                double area = fromServer.readDouble();

                // display in the text area
                textArea.appendText("Radius is " + radius + "\n");
                textArea.appendText("Area received from the server is " + area + "\n");
            }
            catch (IOException exception) {
                System.err.println(exception);
            }
        });

        try {
            // create socket to connect to the server
            Socket socket = new Socket("localhost", 8675);

            // create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // create output stream
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException ex) {
            textArea.appendText(ex.toString() + "\n");
        }
    }
}
