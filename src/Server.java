import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable, InvalidationListener, Observable {
    private final String host;
    private final int port;
    private InvalidationListener listener;
    ServerSocket serverSocket;
    ArrayList<Socket> sockets = new ArrayList<Socket>();
    String inputLine;
    boolean listening;
    boolean sockets_changed=false;

    public Server(String[] args) {
        this.host = args[0];
        this.port = Integer.parseInt(args[1]);
    }

    public static void main(String[] args) {
        new Thread(new Server(args)).start();
    }

    @Override
    public void run() {
        listening = true;
        inputLine = "ServerSocket started";
        if (listener != null) listener.invalidated(this);
        else System.out.println(inputLine);
        try {
            serverSocket = new ServerSocket(port);
            while (listening) {
                Socket socket = serverSocket.accept();
                SocketThread st = new SocketThread(socket);
                st.addListener(this);
                new Thread(st).start();
                sockets.add(socket);
                this.sockets_changed = true;
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    public void stop() {
        listening = false;
        inputLine = "ServerSocket closed by request";
        if (listener != null) listener.invalidated(this);
        else System.out.println(inputLine);
        try {
            for (Socket s : sockets) s.close();
            sockets_changed=true;
            if (!serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {e.printStackTrace();}

    }

    @Override
    public void invalidated(Observable observable) {
        SocketThread st = (SocketThread) observable;
        inputLine = st.inputLine;
        if (inputLine.contains("closed by"))
            for (Socket s: sockets)
                if (st.threadSocket==s) {
                    sockets.remove(s);
                    this.sockets_changed = true;
                    break;
                }
        if (listener != null) listener.invalidated(this);
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