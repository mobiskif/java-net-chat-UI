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
    ArrayList<PrintWriter> outs = new ArrayList<PrintWriter>();
    String inputLine;
    boolean listening;

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
        if (listener!=null) listener.invalidated(this);
        else System.out.println(inputLine);
        try {
            serverSocket = new ServerSocket(port);
            while (listening) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread st = new SocketThread(socket);
                    st.addListener(this);
                    new Thread(st).start();
                    outs.add(new PrintWriter(socket.getOutputStream(), true));
                    if (listener!=null) listener.invalidated(this);
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
        inputLine = "ServerSocket stopped by request";
        if (listener!=null) listener.invalidated(this);
        else System.out.println(inputLine);
        try {
            for (PrintWriter s: outs) s.close();
            if (serverSocket!=null) serverSocket.close();
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
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.listener = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}