package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Created by kamilek on 10/11/2016.
 */
public class ProfileImage {

    public ProfileImage(JLabel avatarLabel, int empID, int trigger) throws IOException {

        if (trigger == 1) {
            try {
                setAvatar(avatarLabel, empID);
            } catch (IOException e) {
                e.printStackTrace();
                String path = "src/icons/avatar2.png";
                File file = new File(path);
                BufferedImage image = ImageIO.read(file);
                avatarLabel.setIcon(new ImageIcon(image));
            }
        }
    }

    public ProfileImage(int empID) {
        choosePicture(empID);
    }


    private void setAvatar(JLabel avatarLabel, int empID) throws IOException {
        //String path = "C:\\Users\\kamilek\\Desktop\\profiles\\avatar1.jpg";


        String path = getPath(empID);
        System.out.println(empID + " empID");
        System.out.println(path + " path");

        if (path != null) {
            File file = new File(path);
            if (file.exists() && !file.isDirectory()) {
                BufferedImage image = ImageIO.read(file);
                avatarLabel.setIcon(new ImageIcon(image));
            }
        } else if (path == null) {
            System.out.println(" Here");
            //avatarLabel.setIcon(new ImageIcon("avatar2.png"));
        }
    }

    private String getPath(int empID) {
        String pathSource = "src/icons/avatar2.png";

        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("" +
                    "SELECT fileName\n" +
                    "FROM Organisation_Management_System.dbo.tblEmpImages\n" +
                    "WHERE employeeID='" + empID + "'; ");
            while (rs.next()) {
                pathSource = rs.getString("fileName");
            }
        } catch (
                SQLException t
                ) {
            t.printStackTrace();
        }
        return pathSource;
    }


    public void choosePicture(int empID) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
        chooser.setCurrentDirectory(new File("/home/me/Documents"));
        chooser.setFileFilter(filter);
        int retrival = chooser.showOpenDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            File file = new File(chooser.getSelectedFile().getPath());
            System.out.println(file);
            setProfilePicture(empID, file);
        }
    }

    private void setProfilePicture(int empID, File file) {

        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            if (getPath(empID) == null || getPath(empID).equals("src/icons/avatar2.png")) {
                // insert path
                //System.out.println("Inserting empID: " + empID + ", getPath: " + getPath(empID));
                st.executeUpdate("INSERT INTO Organisation_Management_System.dbo.tblEmpImages(fileName,employeeID)" +
                        " VALUES ('" + file + "','" + empID + "')");
                //ps.setString(1, file.toString());
                //ps.setInt(2, empID);

            } else {
                //Update Path
                //System.out.println("Updating");
                PreparedStatement ps = con.prepareStatement("UPDATE Organisation_Management_System.dbo.tblEmpImages" +
                        " SET fileName = ? WHERE employeeID = ?");
                ps.setString(1, file.toString());
                ps.setInt(2, empID);
                ps.executeUpdate();
                ps.close();

            }
        } catch (SQLException t) {
            t.printStackTrace();
        }


    }
}
