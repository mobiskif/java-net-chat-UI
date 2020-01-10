import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable, InvalidationListener, Observable {
    private final JComboBox combobox;
    boolean listening;
    //private final JTextComponent textpane;
    private int port;
    ServerSocket serverSocket;
    String inputLine;
    private InvalidationListener listener;

    public Server(JTextComponent tp, JComboBox cb) {
        this.port = 1966;
        //this.textpane = tp;
        this.combobox = cb;
    }

    public static void main(String[] args) {
        new Thread(new Server(new JTextField(), new JComboBox())).start();
    }

    @Override
    public void run() {
        listening = true;
        try {
            serverSocket = new ServerSocket(port);
            while (listening) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread ss = new SocketThread(socket);
                    ss.addListener(this);
                    new Thread(ss).start();
                    combobox.addItem(socket);
                } catch (IOException e) {
                    inputLine = e.getMessage();
                    if (listener!=null) listener.invalidated(this);
                    else System.err.println(inputLine);
                }
            }
        } catch (IOException e) {
            inputLine = e.getMessage();
            if (listener!=null) listener.invalidated(this);
            else System.err.println(inputLine);
        }
    }

    public void stop() {
        listening = false;
        try {
            for (int i = 0; i < combobox.getItemCount(); i++) ((Socket) combobox.getItemAt(i)).close();
            if (serverSocket!=null) {
                inputLine = "ServerSocket closed by request";
                serverSocket.close();
                if (listener!=null) listener.invalidated(this);
                else System.out.println(inputLine);
            }
            combobox.removeAllItems();
        }
        catch (IOException e) {
            inputLine = e.getMessage();
            if (listener!=null) listener.invalidated(this);
            else System.err.println(inputLine);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        inputLine = ((SocketThread) observable).inputLine;
        if (listener!=null) listener.invalidated(this);
        else System.out.println(inputLine);

        //textpane.setText(textpane.getText() + ((SocketThread) observable).inputLine + "\r\n");
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.listener = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}