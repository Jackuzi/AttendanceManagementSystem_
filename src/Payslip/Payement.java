package Payslip;

import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import org.apache.poi.hssf.usermodel.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

/**
 * Created by kamilek on 26/10/2016.
 */
public class Payement {
    private double tPaye;
    private double otHours;
    private double otPaye;
    private double grossPaye;
    private double netPaye;
    private double tax;
    private double ni;
    private double posRate;
    private double basicHours = 160;
    private double hoursWorked;
    private TableRowSorter<DefaultTableModel> sorter;
    private JMonthChooser monthChooserTo;
    private JMonthChooser monthChooserFrom;
    private JYearChooser yearChooserFrom;
    private JYearChooser yearChooserTo;

    public Payement(JTable payTable, JTextField filterField, JButton printButton,
                    JButton saveAsCsv,
                    JButton datePickerButtonFrom, JButton datePickerButtonTo,
                    JButton datePickerButtonYearFrom) {
        monthChooserTo = new JMonthChooser();
        monthChooserFrom = new JMonthChooser();
        yearChooserFrom = new JYearChooser();
        yearChooserTo = new JYearChooser();

        datePickerButtonYearFrom.add(yearChooserFrom);
        datePickerButtonTo.add(monthChooserTo);
        datePickerButtonFrom.add(monthChooserFrom);

        setFilterListener(payTable, filterField);
        setPrintListener(printButton, payTable, saveAsCsv);
    }

    private void setPrintListener(JButton printButton, JTable payTable, JButton saveAsCsvButton) {
        printButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    payTable.print();
                } catch (PrinterException e1) {
                    e1.printStackTrace();
                }
            }
        });

        saveAsCsvButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    writeCSVfile(payTable);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    private void setFilterListener(JTable payTable, JTextField filterField) {
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                newFilter(payTable, filterField);
            }

            public void removeUpdate(DocumentEvent e) {
                newFilter(payTable, filterField);
            }

            public void insertUpdate(DocumentEvent e) {
                newFilter(payTable, filterField);
            }
        });
    }

    public void newFilter(JTable payTable, JTextField filterField) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(payTable.getModel());
        sorter.setRowFilter(RowFilter.regexFilter(filterField.getText()));
        payTable.setRowSorter(sorter);
    }

    public double checkHours(double hoursWorked) {
        if (hoursWorked <= 160) {
            tPaye = hoursWorked * posRate;
            otHours = 0;
            otPaye = 0;
        } else if (hoursWorked > 160) {
            otHours = hoursWorked - basicHours;
            otPaye = otHours * (posRate * 1.5);
            tPaye = basicHours * (posRate);
        }
        grossPaye = tPaye + otPaye;
        return grossPaye;
    }


    public double getIns(double grossPaye) {
        if (grossPaye < 672) {
            ni = 0;
        } else if (grossPaye >= 672 && grossPaye <= 3583) {
            double grossPaye1 = grossPaye - 672.01;
            ni = (12 * grossPaye1) / 100;
        } else if (grossPaye > 3583) {
            double grossPaye1 = grossPaye - 672.01;
            double ni1 = (12 * grossPaye1) / 100;
            double ni2 = (2 * grossPaye1) / 100;
            ni = ni1 + ni2;
        }
        ni = (double) Math.round(ni * 100) / 100;
        return ni;
    }

    public double getTax(double grossPaye) {
        if (grossPaye < 917) {
            //System.out.println(tax + " no tax deducted");
            tax = 0;
        } else if (grossPaye >= 917 && grossPaye <= 3583) {
            double grossPaye3 = grossPaye - 916.67;
            tax = (2 * grossPaye3) / 10;
            //System.out.println(tax + " 20% no tax deducted");
        } else {
            double grossPaye3 = grossPaye - 916.67;
            tax = (4 * grossPaye3) / 10;
            //System.out.println(tax + " 40% no tax deducted");
        }
        tax = (double) Math.round(tax * 100) / 100;
        return tax;
    }

    public void populatePayTable(JTable payTable) {
        DefaultTableModel modelPay = new DefaultTableModel();
        payTable.setRowSorter(sorter);
        payTable.setModel(modelPay);

        modelPay.addColumn("Month");
        modelPay.addColumn("Year");
        modelPay.addColumn("Name");
        modelPay.addColumn("Surname");
        modelPay.addColumn("Position");
        modelPay.addColumn("Rate per hour");
        modelPay.addColumn("NIN");
        modelPay.addColumn("Tax Code");
        modelPay.addColumn("Total Hours");
        modelPay.addColumn("Overtime");
        modelPay.addColumn("Overtime Pay");
        modelPay.addColumn("Gross Pay");
        modelPay.addColumn("Net Pay");
        modelPay.addColumn("Tax deducted");
        modelPay.addColumn("NI deducted");

        payTable.setDefaultEditor(Object.class, null);
        System.out.println(yearChooserFrom.getYear());
        System.out.println(yearChooserTo.getYear());
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433", "test", "test");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT employeeName AS Name,\n" +
                    "employeeSurname AS Surname,\n" +
                    "DATENAME(mm,dateIN) AS Month,\n" +
                    "YEAR(dateIN) AS YEAR,\n" +
                    "posName AS position,\n" +
                    "posPayRate AS payRate,\n" +
                    "NIN AS NInumber,\n" +
                    "taxCode,\n" +
                    "SUM(hoursWorked) AS TotalHours,\n" +
                    "SUM(hoursWorked * posPayRate) AS GrossPaye\n" +
                    "FROM Organisation_Management_System.dbo.tblRegister ,Organisation_Management_System.dbo.tblPosition, " +
                    "Organisation_Management_System.dbo.tblEmployee, Organisation_Management_System.dbo.tblEarnings\n" +
                    "WHERE tblRegister.employeeID = tblEmployee.employeeID \n" +
                    "AND tblPosition.positionID = tblEmployee.positionID AND tblEarnings.employeeID=tblEmployee.employeeID " +
                    "AND Month(dateIN) >= '" + (monthChooserFrom.getMonth() + 1) + "' AND Month(dateIN) <= '" + (monthChooserTo.getMonth() + 1) + "'" +
                    "AND Year(dateIN) = '" + (yearChooserFrom.getYear()) + "'\n" +
                    "GROUP BY  employeeName, employeeSurname, posPayRate, DateName(mm,dateIN), Year(dateIN), posName, NIN, taxCode\n" +
                    "ORDER BY  employeeName, employeeSurname");
            while (rs.next()) {
                String eN = rs.getString("Name");
                String eS = rs.getString("Surname");
                String pos = rs.getString("position");
                String nin = rs.getString("NInumber");
                String taxCode = rs.getString("taxCode");
                hoursWorked = rs.getDouble("TotalHours");
                int year = rs.getInt("Year");
                String month = rs.getString("Month");
                posRate = rs.getDouble("payRate");
                grossPaye = checkHours(hoursWorked);
                tax = getTax(grossPaye);
                ni = getIns(grossPaye);

                netPaye = grossPaye - tax - ni;
                netPaye = (double) Math.round(netPaye * 100) / 100;
                grossPaye = (double) Math.round(grossPaye * 100) / 100;

                //System.out.println(netPaye + " netto");
                //System.out.println(grossPaye + " brutto");
                //System.out.println(tax + " tax");
                //System.out.println(ni + " ni");

                modelPay.addRow(new Object[]{month, year, eN, eS, pos, posRate, nin, taxCode, hoursWorked, otHours, otPaye, grossPaye, netPaye, tax, ni});
            }
        } catch (SQLException t) {
            t.printStackTrace();
        } finally {
        }
    }

    public void writeCSVfile(JTable table) throws IOException, ClassNotFoundException, SQLException {

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("/home/me/Documents"));
            int retrival = chooser.showSaveDialog(null);
            if (retrival == JFileChooser.APPROVE_OPTION) {
                HSSFWorkbook fWorkbook = new HSSFWorkbook();
                HSSFSheet fSheet = fWorkbook.createSheet("new Sheet");
                TableColumnModel tcm = table.getColumnModel();
                HSSFRow fRow = fSheet.createRow((short) 0);
                for (int j = 0; j < tcm.getColumnCount(); j++) {
                    HSSFCell cell = fRow.createCell((short) j);
                    cell.setCellValue(tcm.getColumn(j).getHeaderValue().toString());
                }
                File file = new File(chooser.getSelectedFile() + ".xls");
                HSSFCellStyle cellStyle = fWorkbook.createCellStyle();
                TableModel model = table.getModel();
                for (int row = 0; row < table.getRowCount(); row++) {
                    fRow = fSheet.createRow((short) row + 2);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        HSSFCell cell = fRow.createCell((short) j);
                        cell.setCellValue(table.getModel().getValueAt(table.convertRowIndexToModel(row), j).toString());
                        cell.setCellStyle(cellStyle);
                    }
                }
                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                fWorkbook.write(bos);
                bos.close();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





