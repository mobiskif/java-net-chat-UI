import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements InvalidationListener, Observable {
    private final String host;
    private final int port;
    private InvalidationListener listener;
    private Socket clientSocket;
    PrintWriter out;
    String inputLine;


    public Client(String[] args) {
        this.host = args[0];
        this.port = Integer.parseInt(args[1]);
        run();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(args);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(client.clientSocket.getInputStream()));

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            client.out.println(userInput);
            System.out.println("=> " + in.readLine());
        }
    }

    public void run() {
        try {
            clientSocket = new Socket(host, port);
            SocketThread ss = new SocketThread(clientSocket);
            ss.addListener(this);
            new Thread(ss).start();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            inputLine = e.getMessage();
            if (listener!=null) listener.invalidated(this);
            else System.err.println(inputLine);
        }
    }

    public void stop() {
        try {
            if (clientSocket !=null) {
                inputLine = "ClientSocket closed by request";
                clientSocket.close();
                if (listener!=null) listener.invalidated(this);
                else System.out.println(inputLine);
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void invalidated(Observable observable) {
        inputLine = ((SocketThread) observable).inputLine;
        if (listener!=null) listener.invalidated(this);
        else System.out.println(inputLine);
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.listener = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}