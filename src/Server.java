import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server implements Runnable {
    private boolean listening;
    private final JTextComponent textpane;
    //private final String host;
    private int port;
    PrintWriter out;
    private ServerSocket serverSocket;

    public Server(JTextComponent tp) {
        this.port = 1966;
        this.textpane = tp;
    }

    public static void main(String[] args) {
        new Thread(new Server(new JTextField())).start();
    }

    @Override
    public void run() {
        listening = true;
        try {
            serverSocket = new ServerSocket(port);
            while (listening) {
                Socket socket = serverSocket.accept();
                out = new PrintWriter(socket.getOutputStream(), true);
                new Thread(new SocketThread(socket,textpane)).start();
            }
            stop();
        }
        catch (IOException e) {System.err.println(e.getMessage());}
    }

    public void stop() {
        listening = false;
        String status="Try closing socket";
        try {
            serverSocket.close();
            status = "ServerSocket closed by request";
        }
        catch (IOException e) {e.printStackTrace();}
        finally {
            textpane.setText(textpane.getText() + status + "\r\n");
            System.out.println(status);
        }
    }
}