import javax.swing.text.JTextComponent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketThread implements Runnable {
    final JTextComponent textpane;
    private final Method method;
    Socket socket;
    boolean ping = false;

    public SocketThread(Socket socket, JTextComponent textpane, Method method) {
        this.socket = socket;
        this.textpane = textpane;
        this.method = method;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);

        String status = "Connected "+socket.getRemoteSocketAddress() + " at " + time;
        textpane.setText(textpane.getText() + status +"\r\n");
        System.out.println(status);
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("exit")) break;
                if (!ping) textpane.setText(textpane.getText() + "->" + inputLine + "\r\n");

                if (ping) {
                    try {Thread.sleep(1000*1);}
                    catch (InterruptedException e) {e.printStackTrace();}
                    out.println(inputLine);
                    textpane.setText(textpane.getText() + "<=" + inputLine + "\r\n");
                }
            }
            stop();
        }
        catch (IOException e) {System.err.println(e.getMessage());}
    }

    public void stop() {
        String status="Try closing socket";
        try {
            socket.close();
            status = "Socket " + socket.getPort() + " closed by respondent";
            System.out.println(method.getName() + " " + method.getParameterCount()+ "###");
            try {
                method.invoke(new Server());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {e.printStackTrace();}
        finally {
            textpane.setText(textpane.getText() + status + "\r\n");
            System.out.println(status);
        }
    }

}