package GUI;

import DatePicker.DateLabelFormatter;
import Main.ProfileImage;
import Payslip.Payement;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by kamilek on 06/10/2016.
 */
public class Management {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JPanel employeeManageTab;
    private JPanel salaryTab;
    private JPanel AboutTab;
    private JButton addNewEmployeeButton;
    private JButton showAllEmployeesButton;
    private JButton removeEmployeeRecordButton;
    private JTextField addNameField;
    private JTextField addSurnameField;
    private JTextField addGenderField;
    private JComboBox addPosBox;
    private JComboBox addGroupBox;
    private JTextField searchNameField;
    private JTextField reasultField;
    private JTable allEmployeesTable;
    private JComboBox addGenderBox;
    private JTextField searchSurnameField;
    private JTable payTable;
    private JButton printButton;
    private JButton addDobField;
    private JTextField addressField;
    private JTextField postcodeField;
    private JTextField cityField;
    private JTextField phoneField;
    private JTextField ninField;
    private JButton datePickerButtonFrom;
    private JButton datePickerButtonTo;
    private JButton showResButton;
    private JTextField filterField;
    private JButton saveAsCSVButton;
    private JButton datePickerButtonYearFrom;
    private JButton datePickerButtonYearTo;
    private JPanel panelYear;
    private JLabel year;
    private JButton updateRecordButton;
    private JTextField loginCodeField;
    private JTextField systemAccessField;
    private JTextField taxCodeField;
    private JTextField systemAccessUsername;
    private JCheckBox permCheckbox;
    private JLabel avatarLabel;
    private JLabel browseImageLabel;
    private JPanel yearPanel;
    private JTextField fieldY;
    private JDatePanelImpl datePanel;
    private JDatePickerImpl datePicker;
    private HashMap recordsMap;
    private boolean proceed = true;
    private ImageIcon photo;


    private Payement paye;

    public Management() {
        recordsMap = new HashMap();
        paye = new Payement(payTable, filterField,
                printButton, saveAsCSVButton,
                datePickerButtonFrom, datePickerButtonTo,
                datePickerButtonYearFrom);
        createDatePicker(addDobField, 1);
        populateTable();
        populateDropDownBoxes();
        tabChangeListener();
        buttonsListeners();

    }


    private void tabChangeListener() {
        ChangeListener changeListener = changeEvent -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            // System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
            emptyBoxes();
            if (sourceTabbedPane.getTitleAt(index).equals("Salary management")) {
            }
        };
        tabbedPane1.addChangeListener(changeListener);
    }


    private void createDatePicker(JButton addDobField, int opt) {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        if (opt == 1) {
            datePanel = new JDatePanelImpl(model, p);
            datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            addDobField.add(datePicker);

        }
    }


    private void buttonsListeners() {
        allEmployeesTable.getSelectionModel().addListSelectionListener(event -> {
            //allEmployeesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            if (allEmployeesTable.getSelectedRow() > -1) {
                if (!event.getValueIsAdjusting()) {
                    // System.out.println(allEmployeesTable.getValueAt(allEmployeesTable.getSelectedRow(), 1).toString());
                    //System.out.println(allEmployeesTable.getValueAt(allEmployeesTable.getSelectedRow(), 2).toString());
                    String searchName = allEmployeesTable.getValueAt(allEmployeesTable.getSelectedRow(), 1).toString();
                    String searchSurname = allEmployeesTable.getValueAt(allEmployeesTable.getSelectedRow(), 2).toString();
                    displayEmpDetails(searchName, searchSurname);
                    createUpdateMap();
                    //avatarLabel.repaint();
                }
            }
        });

        permCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e.getStateChange() == ItemEvent.SELECTED
                        ? "SELECTED" : "DESELECTED");
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    systemAccessField.setEnabled(true);
                    systemAccessUsername.setEnabled(true);

                } else {
                    systemAccessField.setEnabled(false);
                    systemAccessUsername.setEnabled(false);

                }
            }
        });

        browseImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(recordsMap);
                if (!recordsMap.isEmpty()) {
                    int empID = Integer.parseInt(recordsMap.get("empID").toString());
                    //empID = Integer.parseInt(empID);
                    new ProfileImage(empID);
                    //avatarLabel.repaint();
                } else System.out.println("Please choose employee from table first");

            }
        });

        addNewEmployeeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                employeeAdd();
            }
        });

        removeEmployeeRecordButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                employeeRemove();
            }
        });

        showAllEmployeesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                populateTable();
                emptyBoxes();
                System.out.println("map2 " + recordsMap.toString());


                String path = "src/icons/avatar2.png";
                File file = new File(path);
                BufferedImage image = null;
                try {
                    image = ImageIO.read(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                avatarLabel.setIcon(new ImageIcon(image));

            }
        });
        showResButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                filterField.setText("");
                paye.populatePayTable(payTable);
            }
        });

        updateRecordButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                compareMaps();
            }
        });
    }

    private void displayEmpDetails(String searchName, String searchSurname) {
        //populate empty fields
        //then create listener for update and if value is different in fields than in table;
        emptyBoxes();
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("" +
                    "SELECT tblEmployee.employeeID, employeeName, employeeSurname, dateOfBirth,gender,address,postcode, phoneNo,Nin,city,posName,taxCode,loginCode,password,username\n" +
                    "FROM Organisation_Management_System.dbo.tblEmployee, Organisation_Management_System.dbo.tblEmployeeLogins," +
                    "Organisation_Management_System.dbo.tblSystemAccess, Organisation_Management_System.dbo.tblEarnings,Organisation_Management_System.dbo.tblPosition\n" +
                    "WHERE employeeName='" + searchName + "' AND employeeSurname='" + searchSurname + "' " +
                    "AND tblEarnings.employeeID=tblEmployee.employeeID \n" +
                    "AND tblSystemAccess.employeeID=tblEmployee.employeeID \n" +
                    "AND tblEmployeeLogins.employeeID=tblEmployee.employeeID\n" +
                    "AND tblEmployee.positionID=tblPosition.positionID; ");
            while (rs.next()) {
                int empID = rs.getInt("employeeID");
                String eN = rs.getString("employeeName");
                String eS = rs.getString("employeeSurname");
                String dob = rs.getString("dateofBirth");
                String g = rs.getString("gender");
                String adres = rs.getString("address");
                String post = rs.getString("postcode");
                int phoneNo = rs.getInt("phoneNo");
                String nin = rs.getString("Nin");
                String city = rs.getString("city");
                String pos = rs.getString("posName");
                String tax = rs.getString("taxCode");
                int login = rs.getInt("loginCode");
                String pass = rs.getString("password");
                String username = rs.getString("username");

                int trigger = 1;
                ProfileImage profiles = new ProfileImage(avatarLabel, empID, trigger);

                if (!pass.equals("")) {
                    addNameField.setText(eN);
                    addSurnameField.setText(eS);
                    datePicker.getJFormattedTextField().setText(dob);
                    //System.out.println(g);
                    addGenderBox.setSelectedItem(g.toString());
                    addressField.setText(adres);
                    postcodeField.setText(post);
                    phoneField.setText(String.valueOf(phoneNo));
                    ninField.setText(String.valueOf(nin));
                    cityField.setText(city);
                    addPosBox.setSelectedItem(pos);
                    taxCodeField.setText(tax);
                    loginCodeField.setText(String.valueOf(login));
                    systemAccessField.setText(pass);
                    systemAccessUsername.setText(username);
                    //System.out.println(pass);
                    recordsMap.put("empID", empID);
                }
            }
        } catch (
                SQLException t
                ) {
            t.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateDropDownBoxes() {
        addPosBox.addItem(" ");
        addPosBox.addItem("Managing Director");
        addPosBox.addItem("Sales Director");
        addPosBox.addItem("Accounts Manager");
        addPosBox.addItem("Senior Accountant");
        addPosBox.addItem("Secretary");
        addPosBox.addItem("Process Operator");
        addGenderBox.addItem(" ");
        addGenderBox.addItem("M");
        addGenderBox.addItem("F");
    }

    private void employeeRemove() {
        String searchName = searchNameField.getText();
        String searchSurname = searchSurnameField.getText();
        //String resultS = reasultField.getText();
        int trigger = 1;
        isExisting(null, null, null, null, 0, searchName, searchSurname, trigger);
        reasultField.setText(searchName + " " + searchSurname);
    }

    private void employeeAdd() {
        String addName = addNameField.getText();
        String addSurname = addSurnameField.getText();
        String addDob = datePicker.getJFormattedTextField().getText();
        String addGender = addGenderBox.getSelectedItem().toString();
        int addPos = addPosBox.getSelectedIndex();
        int trigger = 0;
        isExisting(addName, addSurname, addDob, addGender, addPos, null, null, trigger);
    }


    private boolean isFieldEmpty() {
        boolean empty = false;
        if ((addNameField.getText().isEmpty())
                || (addSurnameField.getText().isEmpty())
                || (datePicker.getJFormattedTextField().getText().isEmpty())
                || (addGenderBox.getSelectedItem() == "")
                || (addPosBox.getSelectedItem() == "")
                || (addressField.getText().isEmpty())
                || (cityField.getText().isEmpty())
                || (postcodeField.getText().isEmpty())
                || (ninField.getText().isEmpty())
                || (loginCodeField.getText().isEmpty())
                || (taxCodeField.getText().isEmpty())) {
            empty = true;
        }
        return empty;
    }

    private void createUpdateMap() {

        recordsMap.put("name", addNameField.getText());
        recordsMap.put("surname", addSurnameField.getText());
        recordsMap.put("dob", datePicker.getJFormattedTextField().getText());
        recordsMap.put("gender", addGenderBox.getSelectedItem().toString());
        recordsMap.put("position", addPosBox.getSelectedIndex());
        recordsMap.put("address", addressField.getText());
        recordsMap.put("city", cityField.getText());
        recordsMap.put("post", postcodeField.getText());
        recordsMap.put("phone", phoneField.getText());
        recordsMap.put("nin", ninField.getText());
        recordsMap.put("tax", taxCodeField.getText());
        recordsMap.put("login", loginCodeField.getText());
        recordsMap.put("password", systemAccessField.getText());
        recordsMap.put("username", systemAccessUsername.getText());


        //System.out.println("map " + recordsMap.toString());
    }

    private void compareMaps() {
        HashMap recordsMap2 = new HashMap();
        recordsMap2.put("name", addNameField.getText());
        recordsMap2.put("surname", addSurnameField.getText());
        recordsMap2.put("dob", datePicker.getJFormattedTextField().getText());
        recordsMap2.put("gender", addGenderBox.getSelectedItem().toString());
        recordsMap2.put("position", addPosBox.getSelectedIndex());
        recordsMap2.put("address", addressField.getText());
        recordsMap2.put("city", cityField.getText());
        recordsMap2.put("post", postcodeField.getText());
        recordsMap2.put("phone", phoneField.getText());
        recordsMap2.put("nin", ninField.getText());
        recordsMap2.put("tax", taxCodeField.getText());
        recordsMap2.put("login", loginCodeField.getText());
        recordsMap2.put("password", systemAccessField.getText());
        recordsMap2.put("username", systemAccessUsername.getText());
        if ((!recordsMap2.isEmpty()) && (!recordsMap.isEmpty())) {
            if (recordsMap.equals(recordsMap2)) {
                String message = "No changes were made. Values have to be changed before updating";
                DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                dialog.pack();
                dialog.setVisible(true);
            } else {
                // update query
                updateRecords();
            }
        }
    }

    private void checkDuplicates(String nin, String loginCode, String accessPass, String accessUsername, String taxCode, String empNameF, String empSurnameF) {
        proceed = true;
        ArrayList ninList = new ArrayList();
        ArrayList userPassList = new ArrayList();
        ArrayList taxCodeList = new ArrayList();
        ArrayList loginCodeList = new ArrayList();

        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT employeeName, employeeSurname, loginCode, username, password, taxCode, NIN " +
                    "FROM Organisation_Management_System.dbo.tblEmployee, Organisation_Management_System.dbo.tblEmployeeLogins," +
                    " Organisation_Management_System.dbo.tblSystemAccess, Organisation_Management_System.dbo.tblEarnings " +
                    "WHERE tblEmployee.employeeID = tblEmployeeLogins.employeeID AND tblEmployee.employeeID = tblSystemAccess.employeeID" +
                    " AND tblEmployee.employeeID = tblEarnings.employeeID AND tblEmployee.employeeName!='" + empNameF + "'" +
                    " AND tblEmployee.employeeSurname!='" + empSurnameF + "'");
            while (rs.next()) {
                int lCode = rs.getInt("loginCode");
                String lNin = rs.getString("NIN");
                String lUser = rs.getString("username");
                String lPass = rs.getString("password");
                String lTax = rs.getString("taxCode");

                loginCodeList.add(lCode);
                ninList.add(lNin);
                userPassList.add(lUser);
                userPassList.add(lPass);
                taxCodeList.add(lTax);

                if ((ninList.contains(nin))
                        || (loginCodeList.contains(Integer.valueOf(loginCode)))
                        || (taxCodeList.contains(taxCode))) {

                    proceed = false;
                }
            }
        } catch (SQLException t) {
            t.printStackTrace();
            String message = "Error. SQL";
            DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
            dialog.pack();
            dialog.setVisible(true);
        }

    }


    private void updateRecords() {
        String empNameF = addNameField.getText();
        String empSurnameF = addSurnameField.getText();
        String addDob = datePicker.getJFormattedTextField().getText();
        String addGender = addGenderBox.getSelectedItem().toString();
        int addPos = addPosBox.getSelectedIndex();
        String address = addressField.getText();
        String city = cityField.getText();
        String postcode = postcodeField.getText();
        String phoneNo = phoneField.getText();
        String nin = ninField.getText();
        String taxCode = taxCodeField.getText();
        String loginCode = loginCodeField.getText();
        String accessPass = systemAccessField.getText();
        String accessUsername = systemAccessUsername.getText();

        String empName = recordsMap.get("name").toString();
        String empSurname = recordsMap.get("surname").toString();

        checkDuplicates(nin, loginCode, accessPass, accessUsername, taxCode, empNameF, empSurnameF);
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT employeeID FROM Organisation_Management_System.dbo.tblEmployee " +
                    "Where employeeName = '" + empName + "' and employeeSurname = '" + empSurname + "'");
            while (rs.next()) {
                int empId = rs.getInt("employeeID");


                if (proceed == true) {
                    PreparedStatement ps = con.prepareStatement("UPDATE Organisation_Management_System.dbo.tblEmployee" +
                            " SET employeeName = ?, employeeSurname = ?, dateOfBirth = ?, gender = ?, positionID = ?, address = ?, postcode = ?, city = ?," +
                            "phoneNo = ? " +
                            "WHERE employeeID = ?");

                    ps.setString(1, empNameF);
                    ps.setString(2, empSurnameF);
                    ps.setString(3, addDob);
                    ps.setString(4, addGender);
                    ps.setInt(5, addPos);
                    ps.setString(6, address);
                    ps.setString(7, postcode);
                    ps.setString(8, city);
                    ps.setString(9, phoneNo);
                    ps.setInt(10, empId);


                    ps.executeUpdate();
                    ps.close();

                    //update 2
                    ps = con.prepareStatement("UPDATE Organisation_Management_System.dbo.tblEarnings" +
                            " SET NIN = ?, taxCode = ? WHERE employeeID = ?");
                    ps.setString(1, nin);
                    ps.setString(2, taxCode);
                    ps.setInt(3, empId);

                    ps.executeUpdate();
                    ps.close();

                    //Update 3
                    ps = con.prepareStatement("UPDATE Organisation_Management_System.dbo.tblEmployeeLogins" +
                            " SET loginCode = ? WHERE employeeID = ?");
                    ps.setString(1, loginCode);
                    ps.setInt(2, empId);

                    ps.executeUpdate();
                    ps.close();

                    //Update 4
                    ps = con.prepareStatement("UPDATE Organisation_Management_System.dbo.tblSystemAccess" +
                            " SET username = ?, password = ? WHERE employeeID = ?");
                    ps.setString(1, accessUsername);
                    ps.setString(2, accessPass);
                    ps.setInt(3, empId);

                    ps.executeUpdate();
                    ps.close();

                } else {
                    String message = "Duplicate values. Please check details";
                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                    dialog.pack();
                    dialog.setVisible(true);
                    return;
                }
            }
        } catch (
                SQLException t
                ) {
            t.printStackTrace();
            String message = "Error. Correct details";
            DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
            dialog.pack();
            dialog.setVisible(true);
        } finally {
            // System.out.println(isFieldEmpty());
        }
    }

    private void isExisting(String addName, String addSurname, String addDob, String addGender, int addPos, String searchName, String searchSurname, int trigger) {
        ArrayList list = new ArrayList();
        ArrayList ninList = new ArrayList();
        ArrayList userPassList = new ArrayList();
        ArrayList taxCodeList = new ArrayList();
        ArrayList loginCodeList = new ArrayList();

        String address = addressField.getText();
        String city = cityField.getText();
        String postcode = postcodeField.getText();
        String phoneNo = phoneField.getText();
        String nin = ninField.getText();
        String taxCode = taxCodeField.getText();
        String loginCode = loginCodeField.getText();
        String accessPass = systemAccessField.getText();
        String accessUsername = systemAccessUsername.getText();

        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT employeeName, employeeSurname, loginCode, username, password, taxCode, NIN " +
                    "FROM Organisation_Management_System.dbo.tblEmployee, Organisation_Management_System.dbo.tblEmployeeLogins," +
                    " Organisation_Management_System.dbo.tblSystemAccess, Organisation_Management_System.dbo.tblEarnings " +
                    "WHERE tblEmployee.employeeID = tblEmployeeLogins.employeeID AND tblEmployee.employeeID = tblSystemAccess.employeeID" +
                    " AND tblEmployee.employeeID = tblEarnings.employeeID");
            while (rs.next()) {

                int lCode = rs.getInt("loginCode");
                String lNin = rs.getString("NIN");
                String lUser = rs.getString("username");
                String lPass = rs.getString("password");
                String lTax = rs.getString("taxCode");

                loginCodeList.add(lCode);
                ninList.add(lNin);
                userPassList.add(lUser + lPass);
                taxCodeList.add(lTax);

                String eN = rs.getString("employeeName");
                String eS = rs.getString("employeeSurname");
                String enes = eN + eS;
                list.add(enes);
                String searchNS = searchName + searchSurname;
                // System.out.println(enes.toString());
                if ((trigger == 1) && list.contains(searchNS)) {
                    String message = "Are you sure you want to delete this record: " + searchName + " " + searchSurname;
                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                    dialog.pack();
                    dialog.setVisible(true);
                    if (dialog.getGo()) {
                        // System.out.println("Can delete");
                        emptyBoxes();
                        st.executeUpdate("DELETE FROM Organisation_Management_System.dbo.tblEmployee WHERE employeeName='" + searchName + "' AND employeeSurname='" + searchSurname + "'; ");
                        populateTable();
                        return;
                    } else //System.out.println("Deletion cancelled");
                        return;
                }
            }
            if (trigger == 0) {
                if ((list.contains(addName + addSurname)) || (ninList.contains(nin))
                        || (loginCodeList.contains(loginCode))
                        || (userPassList.contains(accessUsername + accessPass))
                        || (taxCodeList.contains(taxCode))) {
                    //System.out.println("record exists");
                    String message = "Record already exists or duplicated unique values";
                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                    dialog.pack();
                    dialog.setVisible(true);
                } else if ((!isFieldEmpty()) && (!ninList.contains(nin))) {
                    String message = "Record added: " + addName + " " + addSurname;
                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                    dialog.pack();
                    dialog.setVisible(true);
                    st.executeUpdate("INSERT INTO Organisation_Management_System.dbo.tblEmployee" +
                            "(employeeName,employeeSurname,dateOfBirth,gender,positionID,address,postcode,city,phoneNo) " +
                            "VALUES ('" + addName + "','" + addSurname + "','" + addDob + "','" + addGender + "','" + addPos + "','" + address + "','" + postcode + "','" + city + "','" + Integer.parseInt(phoneNo) + "');");
                    ResultSet idMax = st.executeQuery("SELECT COALESCE(max(employeeID),0) FROM Organisation_Management_System.dbo.tblEmployee");
                    int empID = 0;
                    if (idMax.next()) {
                        empID = idMax.getInt(1);
                    }
                    // System.out.println(empID);
                    st.executeUpdate("INSERT INTO Organisation_Management_System.dbo.tblEarnings(NIN,employeeID,taxCode)" +
                            " VALUES('" + nin + "','" + empID + "','" + taxCode + "');");

                    st.executeUpdate("Insert INTO Organisation_Management_System.dbo.tblEmployeeLogins(loginCode,employeeID)" +
                            "Values('" + loginCode + "','" + empID + "')");
                    if ((systemAccessField.getText().equals("")) && (systemAccessUsername.getText().equals(""))) {
                        accessPass = "0";
                        accessUsername = "0";

                    }
                    st.executeUpdate("INSERT INTO Organisation_Management_System.dbo.tblSystemAccess(username,password,employeeID)" +
                            "VALUES ('" + accessUsername + "','" + accessPass + "','" + empID + "');");


                } else {
                    //System.out.println("Something filled wrong or unique values already exists");
                    String message = "Something filled wrong or unique values already exists";
                    DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
                    dialog.pack();
                    dialog.setVisible(true);
                }
                emptyBoxes();
            }
        } catch (
                SQLException t
                ) {
            t.printStackTrace();
            String message = "Something needs to be corrected";
            DeleteConfirmDialog dialog = new DeleteConfirmDialog(message);
            dialog.pack();
            dialog.setVisible(true);
        } finally {
            // System.out.println(isFieldEmpty());
        }
    }

    public void emptyBoxes() {
        addNameField.setText("");
        addSurnameField.setText("");
        datePicker.getJFormattedTextField().setText("");
        addPosBox.setSelectedIndex(0);
        searchNameField.setText("");
        reasultField.setText("");
        addGenderBox.setSelectedIndex(0);
        searchSurnameField.setText("");
        addressField.setText("");
        postcodeField.setText("");
        phoneField.setText("");
        cityField.setText("");
        ninField.setText("");
        taxCodeField.setText("");
        loginCodeField.setText("");
        systemAccessField.setText("");
        systemAccessUsername.setText("");
        //populateTable();
    }

    public int check(String addPos) {
        int result = 0;
        if (addPos.equals("Process Operator")) {

            int addPos1 = Integer.parseInt("6");
            result = addPos1;
        }
        return result;
    }

    private void populateTable() {


        DefaultTableModel model = new DefaultTableModel();
        allEmployeesTable.setModel(model);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Surname");
        model.addColumn("position");
        model.addColumn("group");
        allEmployeesTable.setDefaultEditor(Object.class, null);
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT employeeID, employeeName, employeeSurname, posName, groupName  FROM Organisation_Management_System.dbo.tblEmployee, Organisation_Management_System.dbo.tblPosition, Organisation_Management_System.dbo.tblGroups\n" +
                    "WHERE tblPosition.positionID = tblEmployee.positionID AND tblPosition.groupID = tblGroups.groupID;");
            while (rs.next()) {
                int eID = rs.getInt("employeeID");
                String eN = rs.getString("employeeName");
                String eS = rs.getString("employeeSurname");
                String groupName = rs.getString("groupName");
                String posName = rs.getString("posName");
                model.addRow(new Object[]{eID, eN, eS, posName, groupName});
            }
        } catch (SQLException t) {
            t.printStackTrace();
        } finally {
           /* try {
                allEmployeesTable.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }*/
        }
    }

    public static void startMenu() {
        JFrame frameM = new JFrame("Management");
        JScrollPane pane = new JScrollPane(new Management().panel1);
        frameM.setContentPane(pane);
        frameM.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameM.setPreferredSize(new Dimension(1400, 1000));

        frameM.pack();
        frameM.setLocationRelativeTo(null);
        frameM.setVisible(true);

    }


}


