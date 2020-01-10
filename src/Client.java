import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements InvalidationListener {
    private boolean listening;
    private final JTextComponent textpane;
    private final String host;
    private final int port;
    PrintWriter out;
    private Socket clientSocket;

    public Client(JTextComponent pane, String[] args) {
        this.textpane = pane;
        this.host = args[0];
        this.port = Integer.parseInt(args[1]);
        run();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(new JTextField(), args);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(client.clientSocket.getInputStream()));

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            client.out.println(userInput);
            System.out.println("=> " + in.readLine());
        }

    }


    public void run() {
        listening = true;
        try {
            clientSocket = new Socket(host, port);
            SocketThread ss = new SocketThread(clientSocket);
            ss.addListener(this);
            new Thread(ss).start();
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("c4: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("c5: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (clientSocket !=null) {
                clientSocket.close();
                System.out.println("ClientSocket closed by request");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invalidated(Observable observable) {
        textpane.setText(textpane.getText() + "<-" + ((SocketThread) observable).inputLine + "\r\n");
    }
}