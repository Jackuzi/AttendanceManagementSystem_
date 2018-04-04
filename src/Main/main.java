package Main;

import GUI.WelcomePage;

import javax.swing.*;

/**
 * Created by kamilek on 28/09/2016.
 */
public class main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            WelcomePage welcome = new WelcomePage();
            welcome.startPage();
        });
    }
}
