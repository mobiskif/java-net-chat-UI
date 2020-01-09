import javax.swing.text.JTextComponent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private boolean listening;
    private final JTextComponent textpane;
    private final String host;
    private final int port;
    PrintWriter out;
    private Socket socket;

    public Client(JTextComponent pane, String[] args) {
        this.textpane = pane;
        this.host = args[0];
        this.port = Integer.parseInt(args[1]);
        run();
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("=> " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

    public void run() {
        listening = true;
        try {
            socket = new Socket(host, port);
            new Thread(new SocketThread(socket, textpane)).start();
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("c4: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("c5: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (socket!=null) {
                socket.close();
                textpane.setText(textpane.getText() + "ClientSocket closed by request" + "\r\n");
                System.out.println("ClientSocket closed by request");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}