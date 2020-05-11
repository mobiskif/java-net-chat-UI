import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientUI implements InvalidationListener {
    private JPanel panel1;
    private JRadioButton serverOnRadioButton;
    private JPanel panel2;
    private JTextPane textpane;

    private JTextField textField1;
    Client client;

    public ClientUI() {
        String[] args = {"localhost", "1966"};
        client = new Client(args);
        client.addListener(ClientUI.this);

        serverOnRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (serverOnRadioButton.isSelected()) {
                    new Thread(client).start();
                } else {
                    client.stop();
                    textField1.setEnabled(false);
                }
            }
        });

        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                textpane.setText(textpane.getText() + "=> " + actionEvent.getActionCommand() + " \r\n");
                client.repeat(actionEvent.getActionCommand());
                textField1.setText("");
            }
        });
    }

    @Override
    public void invalidated(Observable observable) {
        String s = ((Client) observable).inputLine;
        textpane.setText(textpane.getText() + s + "\r\n");
        textField1.setEnabled(true);

        if (s.contains("Socket closed") || s == null) {
            textField1.setEnabled(false);
            serverOnRadioButton.setSelected(false);
        }
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
        textField1 = new JTextField();
        textField1.setEnabled(false);
        textField1.setText("");
        toolBar1.add(textField1);
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
