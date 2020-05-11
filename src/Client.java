import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable, InvalidationListener, Observable {
    private final String host;
    private final int port;
    private InvalidationListener listener;
    //
    Socket clientSocket;
    String inputLine;

    PrintWriter out;

    public Client(String[] args) {
        this.host = args[0];
        this.port = Integer.parseInt(args[1]);
    }

    public static void main(String[] args) throws IOException {
        new Thread(new Client(args)).start();
    }

    public void run() {
        //listening = true;
        inputLine = "ClientSocket started";
        if (listener != null) listener.invalidated(this);
        System.out.println(getClass().getSimpleName() + " input: " + inputLine);
        try {
            clientSocket = new Socket(host, port);
                SocketThread st = new SocketThread(clientSocket);
                st.addListener(this);
                new Thread(st).start();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String userInput;
            while ((userInput = stdIn.readLine()) != ".") repeat(userInput);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void stop() {
        inputLine = "ClientSocket closed by request";
        if (listener != null) listener.invalidated(this);
        System.out.println(getClass().getSimpleName() + " input: " + inputLine);
        try {
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void repeat(String s) {
        if (out != null) out.println(s);
        System.out.println(getClass().getSimpleName() + " output: " + s);
    }

    @Override
    public void invalidated(Observable observable) {
        inputLine = ((SocketThread) observable).inputLine;
        if (listener != null) listener.invalidated(this);
        System.out.println(getClass().getSimpleName() + " input: " + inputLine);
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.listener = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}