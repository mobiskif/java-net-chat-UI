import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerUI implements InvalidationListener {
    private JPanel panel1;
    private JRadioButton serverOnRadioButton;
    private JPanel panel2;
    private JTextPane textpane;
    private JButton button1;
    private JTextField textField1;
    private JComboBox combobox;
    private PrintWriter current_out;
    Server server;

    public ServerUI() {
        String[] args = {"localhost", "1966"};
        server = new Server(args);
        server.addListener(ServerUI.this);

        serverOnRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (serverOnRadioButton.isSelected()) new Thread(server).start();
                else {
                    server.stop();
                    textField1.setEnabled(false);
                }
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame f = new JFrame("ClientUI");
                f.setBounds(100 + 640 + 20, 100, 320, 320);
                f.setContentPane(new ClientUI().$$$getRootComponent$$$());
                f.setVisible(true);
            }
        });

        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                current_out.println(actionEvent.getActionCommand());
                textpane.setText(textpane.getText() + "=> " + actionEvent.getActionCommand() + " \r\n");
                textField1.setText("");
            }
        });

        combobox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                current_out = (PrintWriter) itemEvent.getItem();
            }
        });
    }

    @Override
    public void invalidated(Observable observable) {
        Server s = ((Server) observable);
        textField1.setEnabled(true);
        combobox.removeAllItems();
        for (Socket socket : s.sockets) {
            try {
                combobox.addItem(new PrintWriter(socket.getOutputStream(), true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        textpane.setText(textpane.getText() + s.inputLine + "\r\n");
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final JToolBar toolBar1 = new JToolBar();
        panel1.add(toolBar1, BorderLayout.SOUTH);
        serverOnRadioButton = new JRadioButton();
        serverOnRadioButton.setText("On");
        toolBar1.add(serverOnRadioButton);
        button1 = new JButton();
        button1.setText("+C");
        toolBar1.add(button1);
        textField1 = new JTextField();
        textField1.setEnabled(false);
        textField1.setText("");
        toolBar1.add(textField1);
        combobox = new JComboBox();
        toolBar1.add(combobox);
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, BorderLayout.CENTER);
        textpane = new JTextPane();
        textpane.setEditable(false);
        textpane.setEnabled(true);
        scrollPane1.setViewportView(textpane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
