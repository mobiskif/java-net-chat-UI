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

public class Server implements Runnable, InvalidationListener {
    private final JComboBox combobox;
    boolean listening;
    private final JTextComponent textpane;
    private int port;
    ServerSocket serverSocket;

    public Server(JTextComponent tp, JComboBox cb) {
        this.port = 1966;
        this.textpane = tp;
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
                    System.err.println("s2: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("s1: " + e.getMessage());
        }
    }

    public void stop() {
        listening = false;
        try {
            for (int i = 0; i < combobox.getItemCount(); i++) ((Socket) combobox.getItemAt(i)).close();
            if (serverSocket!=null) {
                serverSocket.close();
                System.out.println("ServerSocket closed by request");
            }
            combobox.removeAllItems();
        }
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void invalidated(Observable observable) {
        //System.out.println("== invalidated ==" + observable);
        textpane.setText(textpane.getText() + "<-" + ((SocketThread) observable).inputLine + "\r\n");
    }
}