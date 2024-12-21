package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.*;
import java.sql.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

 class DatabaseStudentsProfiles extends javax.swing.JFrame {
     
     private List<String> suggestions = new ArrayList<>();
     private JPopupMenu suggestionsPopup = new JPopupMenu();
     
        public class DatabaseHelper {
            public static Connection getConnection() throws SQLException {
                Connection con = null;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection("PLACEHOLDERlocation", "PLACEHOLDERusername", "PLACEHOLDERpassword");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
                return con;
            }
        }

        public class PrintDialog extends JDialog {

            private JButton csvButton;
            private JButton excelButton;
            private boolean csvSelected;

            public PrintDialog(Frame parent) {
                super(parent, "Choose Print Format", true);
                initComponents();
            }

            private void initComponents() {
                csvButton = new JButton("Print CSV");
                excelButton = new JButton("Print Excel");

                csvButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        csvSelected = true;
                        dispose();
                    }
                });

                excelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        csvSelected = false;
                        dispose();
                    }
                });

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(2, 1));
                panel.add(csvButton);
                panel.add(excelButton);

                getContentPane().add(panel, BorderLayout.CENTER);

                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                pack();
                setLocationRelativeTo(null);
            }

            public boolean isCsvSelected() {
                return csvSelected;
            }
        }

    public DatabaseStudentsProfiles() {
        initComponents();
        this.setLocationRelativeTo(null);
        searchDatabase.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });

        searchDatabase.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                suggestionsPopup.setVisible(false);
            }
        });
        
        suggestionsPopup.setFocusable(false);
    }
    
    private void showPopup() {
        if (!suggestionsPopup.isVisible() && !suggestions.isEmpty()) {
            int x = searchDatabase.getLocationOnScreen().x;
            int y = searchDatabase.getLocationOnScreen().y + searchDatabase.getHeight();
            suggestionsPopup.show(searchDatabase, 0, searchDatabase.getHeight());
        }
    }

    private void search() {
    String searchText = searchDatabase.getText();
    suggestions.clear();
    suggestionsPopup.removeAll();

    if (searchText.isEmpty()) {
        suggestionsPopup.setVisible(false);
        return;
    }

    try (Connection con = DatabaseHelper.getConnection()) {
        String query = "SELECT `Full Name` FROM studentsinfo WHERE `Full Name` LIKE ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, "%" + searchText + "%");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String fullName = rs.getString("Full Name");
            suggestions.add(fullName);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    for (String suggestion : suggestions) {
        JMenuItem menuItem = new JMenuItem(suggestion);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDatabase.setText(suggestion);
                suggestionsPopup.setVisible(false);
            }
        });
        suggestionsPopup.add(menuItem);
    }

    suggestionsPopup.revalidate();
    suggestionsPopup.repaint();
    showPopup();
}
    
    private String retrieveDataFromDatabaseAndConvertToCSV() {
        StringBuilder csvData = new StringBuilder();
        try (Connection con = DatabaseHelper.getConnection()) {
            String query = "SELECT * FROM studentsinfo";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                csvData.append(rs.getMetaData().getColumnLabel(i));
                if (i < columnCount) {
                    csvData.append(",");
                }
            }
            csvData.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnValue = rs.getString(i);
                    if (rs.getMetaData().getColumnName(i).equalsIgnoreCase("address")) {
                        columnValue = "\"" + columnValue + "\"";
                    }
                    csvData.append(columnValue);
                    if (i < columnCount) {
                        csvData.append(",");
                    }
                }
                csvData.append("\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return csvData.toString();
    }
    
    private File writeCSVToTemporaryFile(String csvData) {
        try {
            File tempFile = File.createTempFile("studentsinfo", ".csv");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"))) {
                writer.write("\uFEFF");
                writer.write(csvData);
            }
            return tempFile;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private void openCSVFileInChrome(File file) {
        try {
            Desktop.getDesktop().browse(file.toURI());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening file. Please try again.");
        }
    }

            private void exportDataToSheet(Sheet sheet) {
                try {
                    try (Connection con = DatabaseHelper.getConnection()) {
                        String query = "SELECT * FROM studentsinfo";
                        try (PreparedStatement pstmt = con.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int numberOfColumns = metaData.getColumnCount();

                            // Create header row
                            Row headerRow = sheet.createRow(0);
                            for (int i = 1; i <= numberOfColumns; i++) {
                                headerRow.createCell(i - 1).setCellValue(metaData.getColumnLabel(i));
                            }

                            // Fill data rows
                            int rowNum = 1;
                            while (rs.next()) {
                                Row row = sheet.createRow(rowNum++);
                                for (int i = 1; i <= numberOfColumns; i++) {
                                    String value = rs.getString(i);
                                    if (metaData.getColumnName(i).equalsIgnoreCase("LRN")) {
                                        row.createCell(i - 1, CellType.STRING).setCellValue(value);
                                    } else {
                                        try {
                                            // Attempt to parse value as a number
                                            double numericValue = Double.parseDouble(value);
                                            row.createCell(i - 1, CellType.NUMERIC).setCellValue(numericValue);
                                        } catch (NumberFormatException e) {
                                            // If parsing fails, set value as string
                                            row.createCell(i - 1, CellType.STRING).setCellValue(value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        
            private void printDataInExcelFormat() {
            try {
                // Create a new Excel workbook
                XSSFWorkbook workbook = new XSSFWorkbook();

                // Create a new sheet
                XSSFSheet sheet = workbook.createSheet("Students Info");

                // Export data to the sheet

                exportDataToSheet(sheet);

                // Autofit all columns
                int numberOfColumns = sheet.getRow(0).getLastCellNum();
                for (int i = 0; i < numberOfColumns; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write workbook to a temporary file
                File excelFile = writeExcelToTemporaryFile(workbook);

                // Open the temporary Excel file in Chrome
                openCSVFileInChrome(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting data to Excel. Please try again.");
            }
        }
           
        private File writeExcelToTemporaryFile(XSSFWorkbook workbook) throws IOException {
        File tempFile = File.createTempFile("studentsinfo", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            workbook.write(fos);
        }
        return tempFile;
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Parent = new javax.swing.JPanel();
        firstpanel = new javax.swing.JPanel();
        searchDatabase = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        nameTextField = new javax.swing.JTextField();
        secstrandTextField = new javax.swing.JTextField();
        lrnTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        gradeLevelTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        ageTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        insertButton = new javax.swing.JButton();
        yes4Ps = new javax.swing.JCheckBox();
        no4Ps = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        femalecheckbox = new javax.swing.JCheckBox();
        malecheckbox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        contactTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        yesIndi = new javax.swing.JCheckBox();
        noIndi = new javax.swing.JCheckBox();
        emailTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        thirdpanel = new javax.swing.JPanel();
        parentsearchButton = new javax.swing.JButton();
        parentsearchDatabase = new javax.swing.JTextField();
        secondpanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        printButon = new javax.swing.JButton();
        studentButton = new javax.swing.JButton();
        parentButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Students' Profiles");
        setResizable(false);

        Parent.setLayout(new java.awt.CardLayout());

        firstpanel.setBackground(new java.awt.Color(204, 204, 204));
        firstpanel.setPreferredSize(new java.awt.Dimension(775, 580));

        searchDatabase.setText("Search...");
        searchDatabase.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchDatabaseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchDatabaseFocusLost(evt);
            }
        });

        searchButton.setBackground(new java.awt.Color(51, 51, 51));
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        nameTextField.setText("Full Name...");
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusLost(evt);
            }
        });

        secstrandTextField.setText("Strand/Section...");
        secstrandTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                secstrandTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                secstrandTextFieldFocusLost(evt);
            }
        });

        lrnTextField.setText("LRN...");
        lrnTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                lrnTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                lrnTextFieldFocusLost(evt);
            }
        });

        jLabel1.setText("Full Name");

        jLabel2.setText("Strand/Section");

        jLabel3.setText("LRN");

        gradeLevelTextField.setText("Grade Level...");
        gradeLevelTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                gradeLevelTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                gradeLevelTextFieldFocusLost(evt);
            }
        });

        jLabel4.setText("Grade Level");

        ageTextField.setText("Age...");
        ageTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ageTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ageTextFieldFocusLost(evt);
            }
        });

        jLabel5.setText("Age");

        updateButton.setBackground(new java.awt.Color(51, 51, 51));
        updateButton.setForeground(new java.awt.Color(255, 255, 255));
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        insertButton.setBackground(new java.awt.Color(51, 51, 51));
        insertButton.setForeground(new java.awt.Color(255, 255, 255));
        insertButton.setText("Add");
        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }
        });

        yes4Ps.setText("Yes");
        yes4Ps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yes4PsActionPerformed(evt);
            }
        });

        no4Ps.setText("No");
        no4Ps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                no4PsActionPerformed(evt);
            }
        });

        jLabel6.setText("Member of 4Ps?");

        clearButton.setBackground(new java.awt.Color(51, 51, 51));
        clearButton.setForeground(new java.awt.Color(255, 255, 255));
        clearButton.setText("Clear All");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("Sex?");

        femalecheckbox.setText("Female");
        femalecheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                femalecheckboxActionPerformed(evt);
            }
        });

        malecheckbox.setText("Male");
        malecheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malecheckboxActionPerformed(evt);
            }
        });

        jLabel8.setText("Contact No.");

        contactTextField.setText("Contact No.");
        contactTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                contactTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                contactTextFieldFocusLost(evt);
            }
        });

        jLabel9.setText("Address");

        addressTextField.setText("Address...");
        addressTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                addressTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                addressTextFieldFocusLost(evt);
            }
        });

        jLabel10.setText("Member of Indigenous Community?");

        yesIndi.setText("Yes");
        yesIndi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesIndiActionPerformed(evt);
            }
        });

        noIndi.setText("No");
        noIndi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noIndiActionPerformed(evt);
            }
        });

        emailTextField.setText("Email Address...");
        emailTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                emailTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                emailTextFieldFocusLost(evt);
            }
        });

        jLabel11.setText("Email Address");

        javax.swing.GroupLayout firstpanelLayout = new javax.swing.GroupLayout(firstpanel);
        firstpanel.setLayout(firstpanelLayout);
        firstpanelLayout.setHorizontalGroup(
            firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstpanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addGap(15, 15, 15))
            .addGroup(firstpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, firstpanelLayout.createSequentialGroup()
                        .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, firstpanelLayout.createSequentialGroup()
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(secstrandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(ageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(lrnTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(gradeLevelTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, firstpanelLayout.createSequentialGroup()
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(no4Ps, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(yes4Ps, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(62, 62, 62)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(firstpanelLayout.createSequentialGroup()
                                            .addComponent(malecheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(yesIndi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(firstpanelLayout.createSequentialGroup()
                                            .addComponent(femalecheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(44, 44, 44)
                                            .addComponent(noIndi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(firstpanelLayout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(89, 89, 89)
                                        .addComponent(jLabel10)))
                                .addGap(190, 190, 190))
                            .addGroup(firstpanelLayout.createSequentialGroup()
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(contactTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, firstpanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        firstpanelLayout.setVerticalGroup(
            firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(firstpanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gradeLevelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(firstpanelLayout.createSequentialGroup()
                        .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secstrandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(firstpanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lrnTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(firstpanelLayout.createSequentialGroup()
                        .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(yes4Ps)
                            .addComponent(malecheckbox)
                            .addComponent(yesIndi))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(no4Ps)
                            .addComponent(femalecheckbox)
                            .addComponent(noIndi))
                        .addGap(44, 44, 44))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, firstpanelLayout.createSequentialGroup()
                        .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contactTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                .addGroup(firstpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        Parent.add(firstpanel, "card2");

        thirdpanel.setBackground(new java.awt.Color(204, 204, 204));

        parentsearchButton.setBackground(new java.awt.Color(51, 51, 51));
        parentsearchButton.setForeground(new java.awt.Color(255, 255, 255));
        parentsearchButton.setText("Search");

        parentsearchDatabase.setText("Search...");
        parentsearchDatabase.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                parentsearchDatabaseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                parentsearchDatabaseFocusLost(evt);
            }
        });

        javax.swing.GroupLayout thirdpanelLayout = new javax.swing.GroupLayout(thirdpanel);
        thirdpanel.setLayout(thirdpanelLayout);
        thirdpanelLayout.setHorizontalGroup(
            thirdpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thirdpanelLayout.createSequentialGroup()
                .addContainerGap(536, Short.MAX_VALUE)
                .addComponent(parentsearchDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parentsearchButton)
                .addContainerGap())
        );
        thirdpanelLayout.setVerticalGroup(
            thirdpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thirdpanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(thirdpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parentsearchDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parentsearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(535, Short.MAX_VALUE))
        );

        Parent.add(thirdpanel, "card4");

        secondpanel.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout secondpanelLayout = new javax.swing.GroupLayout(secondpanel);
        secondpanel.setLayout(secondpanelLayout);
        secondpanelLayout.setHorizontalGroup(
            secondpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 775, Short.MAX_VALUE)
        );
        secondpanelLayout.setVerticalGroup(
            secondpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 597, Short.MAX_VALUE)
        );

        Parent.add(secondpanel, "card3");

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setLayout(null);

        printButon.setBackground(new java.awt.Color(51, 51, 51));
        printButon.setForeground(new java.awt.Color(255, 255, 255));
        printButon.setText("Print");
        printButon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButonActionPerformed(evt);
            }
        });
        jPanel4.add(printButon);
        printButon.setBounds(10, 520, 72, 39);

        studentButton.setBackground(new java.awt.Color(51, 51, 51));
        studentButton.setForeground(new java.awt.Color(255, 255, 255));
        studentButton.setText("Students' Data");
        studentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentButtonActionPerformed(evt);
            }
        });
        jPanel4.add(studentButton);
        studentButton.setBounds(10, 50, 110, 39);

        parentButton.setBackground(new java.awt.Color(51, 51, 51));
        parentButton.setForeground(new java.awt.Color(255, 255, 255));
        parentButton.setText("Parents' Data");
        parentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentButtonActionPerformed(evt);
            }
        });
        jPanel4.add(parentButton);
        parentButton.setBounds(10, 100, 110, 39);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Parent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Parent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void parentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parentButtonActionPerformed
        Parent.removeAll();
        Parent.add(thirdpanel);
        Parent.repaint();
        Parent.revalidate();
    }//GEN-LAST:event_parentButtonActionPerformed

    private void printButonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButonActionPerformed
        PrintDialog printDialog = new PrintDialog(this);
        printDialog.setVisible(true);

        if (printDialog.isCsvSelected()) {
            // Retrieve data from SQL database and convert it to CSV
            String csvData = retrieveDataFromDatabaseAndConvertToCSV();

            // Write CSV data to a temporary file
            File csvFile = writeCSVToTemporaryFile(csvData);

            // Open the temporary CSV file in Chrome
            openCSVFileInChrome(csvFile);
        } else {
            // Print data in Excel format
            printDataInExcelFormat();
        }
    }//GEN-LAST:event_printButonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String searchText = searchDatabase.getText().trim();

        if (searchText.isEmpty() || searchText.equals("Search...")) {
            JOptionPane.showMessageDialog(this, "Please enter a search query.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/datainfo", "em4mer", "10201973Kk");
            String query = "SELECT `Full Name`, `Strand/Section`, LRN, `Grade Level`, Age, `Member of 4PS`, Sex, `Member of Indigenous Community`, `Contact No.`, Address, `Email Address` FROM studentsinfo WHERE `Full Name` LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, "%" + searchText + "%");

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameTextField.setText(rs.getString("Full Name"));
                secstrandTextField.setText(rs.getString("Strand/Section"));
                lrnTextField.setText(rs.getString("LRN"));
                gradeLevelTextField.setText(rs.getString("Grade Level"));
                ageTextField.setText(rs.getString("Age"));
                contactTextField.setText(rs.getString("Contact No."));
                addressTextField.setText(rs.getString("Address"));
                emailTextField.setText(rs.getString("Email Address"));

                String memberOf4PS = rs.getString("Member of 4PS");
                if (memberOf4PS.equalsIgnoreCase("yes")) {
                    yes4Ps.setSelected(true);
                    no4Ps.setSelected(false);
                } else {
                    yes4Ps.setSelected(false);
                    no4Ps.setSelected(true);
                }

                String sex = rs.getString("Sex");
                if (sex.equalsIgnoreCase("male")) {
                    malecheckbox.setSelected(true);
                    femalecheckbox.setSelected(false);
                } else {
                    malecheckbox.setSelected(false);
                    femalecheckbox.setSelected(true);
                }

                String memberOfIndigenousCommunity = rs.getString("Member of Indigenous Community");
                if (memberOfIndigenousCommunity.equalsIgnoreCase("yes")) {
                    yesIndi.setSelected(true);
                    noIndi.setSelected(false);
                } else {
                    yesIndi.setSelected(false);
                    noIndi.setSelected(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No matching records found.");
            }

            rs.close();
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching database. Please try again.");
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchDatabaseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchDatabaseFocusGained
        if (searchDatabase.getText().equals("Search...")) {
        searchDatabase.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        searchDatabase.setForeground(color);
        }
    }//GEN-LAST:event_searchDatabaseFocusGained

    private void searchDatabaseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchDatabaseFocusLost
        if (searchDatabase.getText().isEmpty()) {
        searchDatabase.setText("Search...");
        java.awt.Color color = java.awt.Color.BLACK;
        searchDatabase.setForeground(color);
        }
    }//GEN-LAST:event_searchDatabaseFocusLost

    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
        if (nameTextField.getText().equals("Full Name...")) {
        nameTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        nameTextField.setForeground(color);
        }
    }//GEN-LAST:event_nameTextFieldFocusGained

    private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusLost
       if (nameTextField.getText().isEmpty()) {
        nameTextField.setText("Full Name...");
        java.awt.Color color = java.awt.Color.BLACK;
        nameTextField.setForeground(color);
        }
    }//GEN-LAST:event_nameTextFieldFocusLost

    private void secstrandTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_secstrandTextFieldFocusGained
        if (secstrandTextField.getText().equals("Strand/Section...")) {
        secstrandTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        secstrandTextField.setForeground(color);
        }
    }//GEN-LAST:event_secstrandTextFieldFocusGained

    private void secstrandTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_secstrandTextFieldFocusLost
        if (secstrandTextField.getText().isEmpty()) {
        secstrandTextField.setText("Strand/Section...");
        java.awt.Color color = java.awt.Color.BLACK;
        secstrandTextField.setForeground(color);
        }
    }//GEN-LAST:event_secstrandTextFieldFocusLost

    private void lrnTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lrnTextFieldFocusGained
        if (lrnTextField.getText().equals("LRN...")) {
        lrnTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        lrnTextField.setForeground(color);
        }
    }//GEN-LAST:event_lrnTextFieldFocusGained

    private void lrnTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lrnTextFieldFocusLost
        if (lrnTextField.getText().isEmpty()) {
        lrnTextField.setText("LRN...");
        java.awt.Color color = java.awt.Color.BLACK;
        lrnTextField.setForeground(color);
        }
    }//GEN-LAST:event_lrnTextFieldFocusLost

    private void studentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentButtonActionPerformed
        Parent.removeAll();
        Parent.add(firstpanel);
        Parent.repaint();
        Parent.revalidate();
    }//GEN-LAST:event_studentButtonActionPerformed

    private void gradeLevelTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gradeLevelTextFieldFocusGained
        if (gradeLevelTextField.getText().equals("Grade Level...")) {
        gradeLevelTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        gradeLevelTextField.setForeground(color);
        }
    }//GEN-LAST:event_gradeLevelTextFieldFocusGained

    private void gradeLevelTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gradeLevelTextFieldFocusLost
        if (gradeLevelTextField.getText().isEmpty()) {
        gradeLevelTextField.setText("Grade Level...");
        java.awt.Color color = java.awt.Color.BLACK;
        gradeLevelTextField.setForeground(color);
        }
    }//GEN-LAST:event_gradeLevelTextFieldFocusLost

    private void ageTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ageTextFieldFocusGained
        if (ageTextField.getText().equals("Age...")) {
        ageTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        ageTextField.setForeground(color);
        }
    }//GEN-LAST:event_ageTextFieldFocusGained

    private void ageTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ageTextFieldFocusLost
        if (ageTextField.getText().isEmpty()) {
        ageTextField.setText("Age...");
        java.awt.Color color = java.awt.Color.BLACK;
        ageTextField.setForeground(color);
        }
    }//GEN-LAST:event_ageTextFieldFocusLost

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        String fullName = nameTextField.getText();
        String strandSection = secstrandTextField.getText();
        String lrn = lrnTextField.getText();
        String gradeLevel = gradeLevelTextField.getText();
        String age = ageTextField.getText();

        if (lrn.isEmpty() || lrn.length() != 12) {
            JOptionPane.showMessageDialog(this, "LRN must be 12 characters long and cannot be empty.");
            return; 
        }

        if (fullName.isEmpty() || strandSection.isEmpty() || lrn.isEmpty() || gradeLevel.isEmpty() || age.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        String memberOf4Ps;
        if (yes4Ps.isSelected()) {
            memberOf4Ps = "Yes";
        } else if (no4Ps.isSelected()) {
            memberOf4Ps = "No";
        } else {
            JOptionPane.showMessageDialog(this, "Please select an option for 'Member of 4PS'.");
            return;
        }

        String sex;
        if (malecheckbox.isSelected()) {
            sex = "Male";
        } else if (femalecheckbox.isSelected()) {
            sex = "Female";
        } else {
            JOptionPane.showMessageDialog(this, "Please select a gender.");
            return;
        }

        String memberOfIndigenousCommunity;
        if (yesIndi.isSelected()) {
            memberOfIndigenousCommunity = "Yes";
        } else if (noIndi.isSelected()) {
            memberOfIndigenousCommunity = "No";
        } else {
            JOptionPane.showMessageDialog(this, "Please select an option for 'Member of Indigenous Community'.");
            return;
        }

        String contactNo = contactTextField.getText();
        String address = addressTextField.getText();
        String emailAddress = emailTextField.getText();

        try (Connection con = DatabaseHelper.getConnection()) {
            String query = "UPDATE studentsinfo SET `Strand/Section` = ?, LRN = ?, `Grade Level` = ?, Age = ?, `Member of 4PS` = ?, Sex = ?, `Member of Indigenous Community` = ?, `Contact No.` = ?, Address = ?, `Email Address` = ? WHERE `Full Name` = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, strandSection);
            pstmt.setString(2, lrn);
            pstmt.setString(3, gradeLevel);
            pstmt.setString(4, age);
            pstmt.setString(5, memberOf4Ps);
            pstmt.setString(6, sex);
            pstmt.setString(7, memberOfIndigenousCommunity);
            pstmt.setString(8, contactNo);
            pstmt.setString(9, address);
            pstmt.setString(10, emailAddress);
            pstmt.setString(11, fullName);

            int updatedRows = pstmt.executeUpdate();

            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Record updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update record. No matching record found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating record. Please try again.");
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void insertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertButtonActionPerformed
        String fullName = nameTextField.getText();
        String strandSection = secstrandTextField.getText();
        String lrn = lrnTextField.getText();
        String gradeLevel = gradeLevelTextField.getText();
        String age = ageTextField.getText();
        String memberOf4PS;

        if (lrn.isEmpty() || lrn.length() != 12) {
            JOptionPane.showMessageDialog(this, "LRN must be 12 characters long and cannot be empty.");
            return; 
        }

        if (fullName.isEmpty() || strandSection.isEmpty() || gradeLevel.isEmpty() || age.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (yes4Ps.isSelected()) {
            memberOf4PS = "Yes";
        } else if (no4Ps.isSelected()) {
            memberOf4PS = "No";
        } else {
            JOptionPane.showMessageDialog(this, "Please select an option for 'Member of 4PS'.");
            return;
        }

        String sex;
        if (malecheckbox.isSelected()) {
            sex = "Male";
        } else if (femalecheckbox.isSelected()) {
            sex = "Female";
        } else {
            JOptionPane.showMessageDialog(this, "Please select a gender.");
            return;
        }

        String memberOfIndigenousCommunity;
        if (yesIndi.isSelected()) {
            memberOfIndigenousCommunity = "Yes";
        } else if (noIndi.isSelected()) {
            memberOfIndigenousCommunity = "No";
        } else {
            JOptionPane.showMessageDialog(this, "Please select an option for 'Member of Indigenous Community'.");
            return;
        }

        String contactNo = contactTextField.getText();
        String address = addressTextField.getText();
        String emailAddress = emailTextField.getText();

        try (Connection con = DatabaseHelper.getConnection()) {
            String query = "INSERT INTO studentsinfo (`Full Name`, `Strand/Section`, `LRN`, `Grade Level`, `Age`, `Member of 4PS`, `Sex`, `Member of Indigenous Community`, `Contact No.`, `Address`, `Email Address`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, fullName);
            pstmt.setString(2, strandSection);
            pstmt.setString(3, lrn);
            pstmt.setString(4, gradeLevel);
            pstmt.setString(5, age);
            pstmt.setString(6, memberOf4PS);
            pstmt.setString(7, sex);
            pstmt.setString(8, memberOfIndigenousCommunity);
            pstmt.setString(9, contactNo);
            pstmt.setString(10, address);
            pstmt.setString(11, emailAddress);

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Record inserted successfully.");
                nameTextField.setText("");
                secstrandTextField.setText("");
                lrnTextField.setText("");
                gradeLevelTextField.setText("");
                ageTextField.setText("");
                yes4Ps.setSelected(false);
                no4Ps.setSelected(false);
                malecheckbox.setSelected(false);
                femalecheckbox.setSelected(false);
                yesIndi.setSelected(false);
                noIndi.setSelected(false);
                contactTextField.setText("");
                addressTextField.setText("");
                emailTextField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to insert record.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting record. Please try again.");
        }
    }//GEN-LAST:event_insertButtonActionPerformed

    private void yes4PsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yes4PsActionPerformed
        if (yes4Ps.isSelected()) {
        no4Ps.setSelected(false);
        }
    }//GEN-LAST:event_yes4PsActionPerformed

    private void no4PsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_no4PsActionPerformed
        if (no4Ps.isSelected()) {
        yes4Ps.setSelected(false);
        }
    }//GEN-LAST:event_no4PsActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        nameTextField.setText("");
        secstrandTextField.setText("");
        lrnTextField.setText("");
        gradeLevelTextField.setText("");
        ageTextField.setText("");
        yes4Ps.setSelected(false);
        no4Ps.setSelected(false);
        malecheckbox.setSelected(false);
        femalecheckbox.setSelected(false);
        yesIndi.setSelected(false);
        noIndi.setSelected(false);
        contactTextField.setText("");
        addressTextField.setText("");
        emailTextField.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    private void parentsearchDatabaseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_parentsearchDatabaseFocusGained
        if (parentsearchDatabase.getText().equals("Search...")) {
        parentsearchDatabase.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        parentsearchDatabase.setForeground(color);
        }
    }//GEN-LAST:event_parentsearchDatabaseFocusGained

    private void parentsearchDatabaseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_parentsearchDatabaseFocusLost
        if (parentsearchDatabase.getText().isEmpty()) {
        parentsearchDatabase.setText("Grade Level...");
        java.awt.Color color = java.awt.Color.BLACK;
        parentsearchDatabase.setForeground(color);
        }
    }//GEN-LAST:event_parentsearchDatabaseFocusLost

    private void malecheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_malecheckboxActionPerformed
        if (malecheckbox.isSelected()) {
            femalecheckbox.setSelected(false);
        }
    }//GEN-LAST:event_malecheckboxActionPerformed

    private void femalecheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_femalecheckboxActionPerformed
        if (femalecheckbox.isSelected()) {
            malecheckbox.setSelected(false);
        }
    }//GEN-LAST:event_femalecheckboxActionPerformed

    private void yesIndiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesIndiActionPerformed
        if (yesIndi.isSelected()) {
            noIndi.setSelected(false);
        }
    }//GEN-LAST:event_yesIndiActionPerformed

    private void noIndiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noIndiActionPerformed
        if(noIndi.isSelected()) {
            yesIndi.setSelected(false);
        }
    }//GEN-LAST:event_noIndiActionPerformed

    private void contactTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_contactTextFieldFocusGained
        if (contactTextField.getText().equals("Contact No.")) {
        contactTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        contactTextField.setForeground(color);
        }
    }//GEN-LAST:event_contactTextFieldFocusGained

    private void contactTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_contactTextFieldFocusLost
        if (contactTextField.getText().isEmpty()) {
        contactTextField.setText("Contact No.");
        java.awt.Color color = java.awt.Color.BLACK;
        contactTextField.setForeground(color);
        }
    }//GEN-LAST:event_contactTextFieldFocusLost

    private void addressTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTextFieldFocusGained
        if (addressTextField.getText().equals("Address...")) {
        addressTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        addressTextField.setForeground(color);
        }
    }//GEN-LAST:event_addressTextFieldFocusGained

    private void addressTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTextFieldFocusLost
        if (addressTextField.getText().isEmpty()) {
        addressTextField.setText("Address...");
        java.awt.Color color = java.awt.Color.BLACK;
        addressTextField.setForeground(color);
        }
    }//GEN-LAST:event_addressTextFieldFocusLost

    private void emailTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_emailTextFieldFocusGained
        if (emailTextField.getText().equals("Email Address...")) {
        emailTextField.setText("");
        java.awt.Color color = java.awt.Color.BLACK;
        emailTextField.setForeground(color);
        }
    }//GEN-LAST:event_emailTextFieldFocusGained

    private void emailTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_emailTextFieldFocusLost
        if (emailTextField.getText().isEmpty()) {
        emailTextField.setText("Email Address...");
        java.awt.Color color = java.awt.Color.BLACK;
        emailTextField.setForeground(color);
        }
    }//GEN-LAST:event_emailTextFieldFocusLost

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DatabaseStudentsProfiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DatabaseStudentsProfiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DatabaseStudentsProfiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DatabaseStudentsProfiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DatabaseStudentsProfiles().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Parent;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JTextField ageTextField;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField contactTextField;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JCheckBox femalecheckbox;
    private javax.swing.JPanel firstpanel;
    private javax.swing.JTextField gradeLevelTextField;
    private javax.swing.JButton insertButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField lrnTextField;
    private javax.swing.JCheckBox malecheckbox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JCheckBox no4Ps;
    private javax.swing.JCheckBox noIndi;
    private javax.swing.JButton parentButton;
    private javax.swing.JButton parentsearchButton;
    private javax.swing.JTextField parentsearchDatabase;
    private javax.swing.JButton printButon;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchDatabase;
    private javax.swing.JPanel secondpanel;
    private javax.swing.JTextField secstrandTextField;
    private javax.swing.JButton studentButton;
    private javax.swing.JPanel thirdpanel;
    private javax.swing.JButton updateButton;
    private javax.swing.JCheckBox yes4Ps;
    private javax.swing.JCheckBox yesIndi;
    // End of variables declaration//GEN-END:variables
}
