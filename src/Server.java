import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    private final JComboBox combobox;
    private boolean listening;
    private final JTextComponent textpane;
    //private final String host;
    private int port;
    PrintWriter current_out;
    private ServerSocket serverSocket;
    ArrayList<Socket> sockets = new ArrayList<>();
    ArrayList<PrintWriter> outs = new ArrayList<>();

    public Server(JTextComponent tp, JComboBox comboBox1) {
        this.port = 1966;
        this.textpane = tp;
        this.combobox = comboBox1;
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
                current_out = new PrintWriter(socket.getOutputStream(), true);
                new Thread(new SocketThread(socket,textpane)).start();

                sockets.add(socket);
                outs.add(current_out);
                combobox.addItem(socket.getRemoteSocketAddress());
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

            for (Socket s: sockets ) s.close();
        }
        catch (IOException e) {e.printStackTrace();}
        finally {
            textpane.setText(textpane.getText() + status + "\r\n");
            System.out.println(status);
        }
    }

    public void setCurrent_out(String str) {
        int j=0;
        for (Socket s:sockets) {
            String a = s.getRemoteSocketAddress().toString();
            if (a.equals(str)) {
                current_out=outs.get(j);
                break;
            }
            j++;
        }
    }
}