import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketThread implements Runnable, Observable {
    boolean auto_answer = false;
    private InvalidationListener listener;
    String inputLine;
    Socket threadSocket;

    public SocketThread(Socket socket) {
        this.threadSocket = socket;
    }

    @Override
    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        inputLine = "Connected "+ threadSocket.getRemoteSocketAddress() + " at " + time;
        if (listener!=null) listener.invalidated(this);
        else System.out.println(inputLine);
        try {

            PrintWriter out = new PrintWriter(threadSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                if (auto_answer) {
                    try {Thread.sleep(1000*1);}
                    catch (InterruptedException e) {e.printStackTrace();}
                    out.println(inputLine);
                }
                if (listener!=null) listener.invalidated(this);
                else System.out.println(inputLine);
            }
            stop();
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void stop() {
        inputLine = "ThreadSocket closed by request";
        if (listener!=null) listener.invalidated(this);
        else System.out.println(inputLine);
        try {threadSocket.close();}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.listener = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}