import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable, InvalidationListener, Observable {
    boolean listening;
    private final String host;
    private final int port;
    private InvalidationListener listener;
    ServerSocket serverSocket;
    ArrayList<PrintWriter> outs = new ArrayList<PrintWriter>();
    String inputLine;

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
        try {
            serverSocket = new ServerSocket(port);
            while (listening) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread ss = new SocketThread(socket);
                    ss.addListener(this);
                    new Thread(ss).start();
                    //combobox.addItem(socket);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    outs.add(out);
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
        try {
            //for (int i = 0; i < combobox.getItemCount(); i++) ((Socket) combobox.getItemAt(i)).close();
            for (PrintWriter s: outs
                 ) {
                s.close();
            }
            if (serverSocket!=null) {
                inputLine = "ServerSocket closed by request";
                serverSocket.close();
                if (listener!=null) listener.invalidated(this);
                else System.out.println(inputLine);
            }
            //combobox.removeAllItems();
            listener.invalidated(this);
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