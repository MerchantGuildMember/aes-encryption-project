package project;

import javax.swing.*;

public class windowedProject {

    public static void main(String[] args) {
        GUI();
    }

    public static void GUI() {
        JFrame frame = new JFrame("test");                  // creates instance of JFrame and passes the title in
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // tells the program to exit when the window closes
        frame.setSize(800, 600);                    // size of window
        frame.setResizable(false);

        JPanel panel = new JPanel();

        JPanel textPanel = new JPanel();
        panel.add(textPanel);

        JTextArea textArea = new JTextArea("Welcome to my Java Project! Here you can en/decrypt and compress file(s)!");
        textPanel.add(textArea);

        JPanel buttonsPanel = new JPanel();
        panel.add(buttonsPanel);

        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);

        JButton button1 = new JButton("Button1");
        JButton button2 = new JButton("Button2");

        buttonsPanel.add(button1);
        buttonsPanel.add(button2);

        frame.add(panel);

        frame.setVisible(true);
    }
}
