package project;

import javax.swing.JFrame;

public class windowedProject {

    public static void main(String[] args) {

        JFrame frame = new JFrame("test");                  // creates instance of JFrame and passes the title in
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // tells the program to exit when the window closes
        frame.setSize(800, 600);                    // size of window
        frame.setVisible(true);                                 // shows the window to the user
    }
}
