import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketThread implements Runnable, Observable {
    Socket socket;
    boolean auto_answer = false;
    private InvalidationListener listener;
    String inputLine;

    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        if (listener!=null) {
            inputLine = listener + ": connection width "+socket.getRemoteSocketAddress() + " at " + time;
            listener.invalidated(this);
        }
        else System.out.println(listener + ": connection width "+socket.getRemoteSocketAddress() + " at " + time);
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("bye")) break;
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
        catch (IOException e) {System.err.println("t6: "+e.getMessage());}
    }

    public void stop() {
        try {
            socket.close();
            if (listener!=null) {
                inputLine = listener + ": socket " + socket.getPort() + " closed by respondent";
                listener.invalidated(this);
            }
            else System.out.println(listener + ": socket " + socket.getPort() + " closed by respondent");
        }
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