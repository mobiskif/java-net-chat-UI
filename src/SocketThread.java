import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.text.JTextComponent;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketThread implements Runnable, Observable {
    //final JTextComponent textpane;
    Socket socket;
    boolean auto_answer = false;
    private InvalidationListener listener;
    String inputLine;

    public SocketThread(Socket socket) {
        this.socket = socket;
        //this.textpane = textpane;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);

        if (listener!=null) {
            inputLine = "Connected "+socket.getRemoteSocketAddress() + " at " + time;
            listener.invalidated(this);
        }
        System.out.println("Connected "+socket.getRemoteSocketAddress() + " at " + time);
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("bye")) break;
                //textpane.setText(textpane.getText() + "->" + inputLine + "\r\n");
                if (auto_answer) {
                    try {Thread.sleep(1000*1);}
                    catch (InterruptedException e) {e.printStackTrace();}
                    out.println(inputLine);
                    //textpane.setText(textpane.getText() + "<=" + inputLine + "\r\n");
                }
                if (listener!=null) listener.invalidated(this);
            }
            stop();
        }
        catch (IOException e) {System.err.println("t6: "+e.getMessage());}
    }

    public void stop() {
        try {
            socket.close();
            if (listener!=null) {
                inputLine = "Socket " + socket.getPort() + " closed by respondent";
                listener.invalidated(this);
            }
            System.out.println("Socket " + socket.getPort() + " closed by respondent");
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