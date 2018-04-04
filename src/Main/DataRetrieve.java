package Main;

import java.sql.*;

/**
 * Created by kamilek on 29/09/2016.
 */


//for testing purposes only


public class DataRetrieve {
    public DataRetrieve() {
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Organisation_Management_System.dbo.tblRegister");

            while (rs.next()) {
                int columnName1 = rs.getInt("employeeID");
                //String columnName2 = rs.getString("employeeName");
                //int columnName3= rs.getInt("gender");
                // System.out.println(columnName1 + "\t");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //dont forget the closing statements
        }

    }


}
