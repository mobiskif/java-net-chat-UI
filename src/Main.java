import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        //Node node = new Node(new Model());
        JFrame f = new JFrame("ServerUI");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setBounds(100,100,480,240);
        f.setContentPane(new ServerUI().$$$getRootComponent$$$());
        f.setVisible(true);
    }
}
