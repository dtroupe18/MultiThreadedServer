/**
 * Created by Dave on 3/8/17.
 */

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MultiThreadedServer extends Application {
    // Text area to display content
    private TextArea textArea = new TextArea();

    // number the clients
    private int clientNo = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new ScrollPane(textArea), 450, 450);
        primaryStage.setTitle("MultiThreaded Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        // MULTI-THREADING
        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8675);
                textArea.appendText("Muti-Threaded Servered Started at " + new Date() + "\n");

                // FOREVER!! Listen for connections
                while (true) {
                    Socket socket = serverSocket.accept();
                    clientNo++; // we added a client
                    Platform.runLater(() -> {
                        // Display the number of clients
                        textArea.appendText("Starting thread for client: " + clientNo +
                                " at " + new Date() + "\n");
                        // Find the client's host name, and IP address
                        InetAddress inetAddress = socket.getInetAddress();
                        textArea.appendText("Client " + clientNo +"'s host name is " +
                                inetAddress.getHostName() +"\n");
                        textArea.appendText("Client " + clientNo +"'s IP Address is " +
                                inetAddress.getHostAddress() + "\n");
                    });

                    // Create a new thread for each connection
                    new Thread(new HandleAClient(socket)).start();
                }
            }
            catch (IOException exception) {
                System.err.println(exception);
            }
        }).start();
    }

    // Thread class for handling new client connections
    class HandleAClient implements Runnable {
        private Socket socket;

        // construct a thread
        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        // run a thread
        public  void run() {
            try {
                // create input and output data streams
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                // FOREVER serve the client
                while (true) {
                    // receive radius from client
                    double radius = inputFromClient.readDouble();
                    double area = radius * radius * Math.PI;

                    // send area back to the client
                    outputToClient.writeDouble(area);

                    Platform.runLater(() -> {
                        textArea.appendText("Radius received from client: " +
                        radius + "\n");

                        textArea.appendText("Area found: " + area + "\n");
                    });
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
