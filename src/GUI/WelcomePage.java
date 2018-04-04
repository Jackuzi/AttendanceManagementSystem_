package GUI;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kamilek on 28/09/2016.
 */
public class WelcomePage implements ActionListener {
    private JButton loginButton;
    private JPanel panel1;
    private JPasswordField passwordField1;
    private JButton exitButton;
    private JTextField textField1;
    private JLabel passwordLabel;
    private JLabel loginLabel;
    private JLabel justLabel;
    private JLabel justField;
    private JButton a0Button;
    private JButton a4Button;
    private JButton a6Button;
    private JButton a7Button;
    private JButton a3Button;
    private JButton a1Button;
    private JButton a2Button;
    private JButton a5Button;
    private JButton a8Button;
    private JButton a9Button;
    private JButton DELButton;
    private JButton OKButton;
    private JTextField textField2;
    private JTable logTable;
    private JTextArea logedUsersField2;
    private JTextField loggedUsersFiled;
    private String login;
    private int status = 0;
    private JFrame frame;
    private DefaultListModel listModel;
    private ArrayList usersLogged;
    private int count = 0;
    private ArrayList timeLogged;
    private DefaultTableModel model;

    public WelcomePage() {

        usersLogged = new ArrayList();
        timeLogged = new ArrayList();

        frame = new JFrame("WelcomePage");
        defineButtons();
        createTable();

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
                //System.out.println("clicked");
                char[] password = passwordField1.getPassword();
                login = textField1.getText();

                try {
                    Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM Organisation_Management_System.dbo.tblSystemAccess where username='" + login + "' AND password='" + String.valueOf(password) + "'");

                    while (rs.next()) {
                        int columnName1 = rs.getInt("employeeID");
                        String columnName2 = rs.getString("username");
                        String columnName3 = rs.getString("password");
                        System.out.println(columnName1 + "\t" + columnName2 + "\t" + columnName3 + "\t");

                        if (!rs.next()) {
                            rs = st.executeQuery("SELECT * FROM Organisation_Management_System.dbo.tblEmployee where employeeID=" + columnName1);
                            while (rs.next()) {
                                String employeeName = rs.getString("employeeName");
                                String employeeSurname = rs.getString("employeeSurname");
                                if (!columnName2.equals("0")) {
                                    String message = "U r now logged as: " + employeeName + " " + employeeSurname;
                                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                                    dialog.pack();
                                    dialog.setVisible(true);
                                    Management.startMenu();
                                } else {
                                    String message = "Access Denied. Check Your Details";
                                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                                    dialog.pack();
                                    dialog.setVisible(true);
                                }
                            }
                        }
                    }

                } catch (SQLException t) {
                    t.printStackTrace();
                } finally {
                    textField1.setText("");
                    passwordField1.setText("");
                }
            }
        });

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                status = 1;
                System.exit(status);
            }
        });
    }

    private void defineButtons() {
        JButton[] buttons = new JButton[12];
        buttons[0] = a0Button;
        buttons[1] = a1Button;
        buttons[2] = a2Button;
        buttons[3] = a3Button;
        buttons[4] = a4Button;
        buttons[5] = a5Button;
        buttons[6] = a6Button;
        buttons[7] = a7Button;
        buttons[8] = a8Button;
        buttons[9] = a9Button;
        buttons[10] = DELButton;
        buttons[11] = OKButton;
        for (int i = 0; i < buttons.length; i++) buttons[i].addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {

        String text = textField2.getText();
        // System.out.println("clicked3");

        if (e.getSource() instanceof JButton && e.getSource() != OKButton && e.getSource() != DELButton)

        {
            textField2.setText(text + ((JButton) e.getSource()).getText());
        }
        if (e.getSource() == DELButton && text.length() >= 1) {
            textField2.setText(text.substring(0, text.length() - 1));

        }
        if (e.getSource() == OKButton) {

            registerCheck();
            textField2.setText("");
            //read String, look for id in database, add record to database with check in and add some option to checkout like list of currently logged on.
        }


    }

    private void registerCheck() {
        if (!textField2.getText().equals("")) {
            int loginCode = Integer.parseInt(textField2.getText());

            try {
                Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Organisation_Management_System.dbo.tblEmployeeLogins where loginCode=" + loginCode);

                while (rs.next()) {
                    //int columnName1 = rs.getInt("loginCode");
                    int columnName2 = rs.getInt("loginID");
                    String empID = rs.getString("employeeID");
                    //System.out.println(columnName1 + " " + columnName2);

                    if (!rs.next()) {
                        rs = st.executeQuery("SELECT * FROM Organisation_Management_System.dbo.tblEmployee where employeeID=" + empID);
                        if (rs.next()) {
                            int employeeID = rs.getInt("employeeID");
                            // add record to tblRegister
                            String name = rs.getString("employeeName");
                            String Surname = rs.getString("employeeSurname");
                            String fName = name + " " + Surname;
                            String Full = fName + "      ";
                            String time = "logged on at:" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "\n";

                            if (!usersLogged.contains(Full)) {
                                currentlyLogged(Full, time);
                                st.executeUpdate("INSERT INTO Organisation_Management_System.dbo.tblRegister(employeeID) VALUES ('" + employeeID + "')  ;");
                                // System.out.println(employeeID + "\t");
                                System.out.println("inserting values " + employeeID + " into tblRegister");
                                return;
                            } else if (usersLogged.contains(Full)) {
                                Calendar cal = Calendar.getInstance();
                                java.sql.Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                                //System.out.println(employeeID + " employeeID");
                                //System.out.println("log out that");
                                currentlyLogged(Full, time);
                                st.executeUpdate("UPDATE Organisation_Management_System.dbo.tblRegister SET dateOut='" + timestamp + "' Where employeeID='" + employeeID + "' And  dateOut IS NULL");
                                return;
                            }
                        }
                    }
                }

            } catch (SQLException t) {
                t.printStackTrace();
            } finally {

            }
        }
    }

    public void createTable() {
        model = new DefaultTableModel();
        logTable.setModel(model);
        model.addColumn("Employee");
        model.addColumn("Time");
        model.isCellEditable(0, 1);
        logTable.setDefaultEditor(Object.class, null);


    }

    public void currentlyLogged(String Full, String time) {

        if (!usersLogged.contains(Full)) {
            usersLogged.add(Full);
            model.addRow(new Object[]{Full, time});
        } else {
            String message = "Logged out: " + Full;
            DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
            dialog.pack();
            dialog.setVisible(true);
            //System.out.println("name already exists, " + "\n" + "ready to log out...");
            usersLogged.remove(Full);
            showList(Full);
        }
        //System.out.println("\n count: " + count);

    }


    private void showList(String Full) {
        try {
            for (int i = 0; i < logTable.getRowCount(); i++) {//For each row
                for (int j = 0; j < logTable.getColumnCount(); j++) {//For each column in that row
                    if (logTable.getModel().getValueAt(i, j).equals(Full)) {//Search the model
                        model.removeRow(i);
                        //System.out.println(logTable.getModel().getValueAt(i, j));//Print if found string

                    }
                }//For loop inner
            }//For loop outer

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array empty");

        }
    }


    public void startPage() {

        //UIManager.put("nimbusBase", new Color(115, 164, 209));
        //UIManager.put("nimbusBlueGrey", new Color(115, 164, 209));
        //UIManager.put("control", new Color(57, 105, 138));


        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());


                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        frame.setContentPane(new WelcomePage().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.pack();
        frame.setLocationRelativeTo(null); //centre window in the middle of screen
        frame.setVisible(true);
    }
}
