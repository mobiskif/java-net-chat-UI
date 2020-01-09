import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    private final JComboBox combobox;
    private boolean listening;
    private final JTextComponent textpane;
    private int port;
    private ServerSocket serverSocket;

    public Server(JTextComponent tp, JComboBox comboBox1) {
        this.port = 1966;
        this.textpane = tp;
        this.combobox = comboBox1;
    }

    public Server() {
        this.port = 1966;
        this.textpane = null;
        this.combobox = null;
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
                Socket socket = serverSocket.accept();
                Method m = Server.class.getMethod("close_socket", Socket.class);
                new Thread(new SocketThread(socket,textpane, m)).start();
                combobox.addItem(socket);
            }
            stop();
        }
        catch (IOException e) {System.err.println(e.getMessage());}
        catch (NoSuchMethodException e) {e.printStackTrace();}
    }

    public void stop() {
        listening = false;
        String status="Try closing socket";
        try {
            serverSocket.close();
            status = "ServerSocket closed by request";
            for (int i = 0; i < combobox.getItemCount() ; i++) ((Socket) combobox.getItemAt(i)).close();
        }
        catch (IOException e) {e.printStackTrace();}
        finally {
            combobox.removeAllItems();
            textpane.setText(textpane.getText() + status + "\r\n");
            System.out.println(status);
        }
    }

    public void close_socket(Socket s) {
        System.out.println(s + " -------");
    }
}