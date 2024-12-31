package database;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseJFrame extends javax.swing.JFrame {
    
    Connection con;
    DefaultTableModel tableModel;
    public DatabaseJFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
        createConnection();
        RefreshTable();
    }
    
        public class ContactInfoDialog extends JDialog {

        public ContactInfoDialog(JFrame parent) {
            super(parent, "Contact Information", true);
            initComponents();
        }

        private void initComponents() {
            JLabel emailLabel = new JLabel("Email: database10@gmail.com");
            JLabel contactLabel = new JLabel("Contact Number: ");

            JPanel panel = new JPanel();
            panel.add(emailLabel);
            panel.add(contactLabel);

            getContentPane().add(panel);
            pack();
            setLocationRelativeTo(null);
        }
    }
        
    public class LegendsDialog extends JDialog {

    public LegendsDialog(JFrame parent) {
        super(parent, "Legends", true);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JLabel insertingLabel = new JLabel("Inserting Text Fields:");
        JButton insertingButton = new JButton("?");
        insertingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Inserting Text Fields", "Allows you to input data into the fields provided.");
            }
        });
        panel.add(insertingLabel);
        panel.add(insertingButton);

        JLabel addButtonLabel = new JLabel("Add Button:");
        JButton addButton = new JButton("?");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Add Button", "Adds the inputted data into the table.");
            }
        });
        panel.add(addButtonLabel);
        panel.add(addButton);

        JLabel deleteButtonLabel = new JLabel("Delete Button:");
        JButton deleteButton = new JButton("?");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Delete Button", "Deletes the selected row from the table.");
            }
        });
        panel.add(deleteButtonLabel);
        panel.add(deleteButton);

        JLabel tableLabel = new JLabel("The Table:");
        JButton tableButton = new JButton("?");
        tableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("The Table", "Displays data in a tabular format.");
            }
        });
        panel.add(tableLabel);
        panel.add(tableButton);

        JLabel editButtonLabel = new JLabel("Edit Button:");
        JButton editButton = new JButton("?");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Edit Button", "Allows you to edit the selected row in the table.");
            }
        });
        panel.add(editButtonLabel);
        panel.add(editButton);

        JLabel updateButtonLabel = new JLabel("Update Button:");
        JButton updateButton = new JButton("?");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Update Button", "Updates the data of the selected row in the table.");
            }
        });
        panel.add(updateButtonLabel);
        panel.add(updateButton);

        JLabel editingLabel = new JLabel("Editing Text Fields:");
        JButton editingButton = new JButton("?");
        editingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Editing Text Fields", "Allows you to edit the data in the fields provided.");
            }
        });
        panel.add(editingLabel);
        panel.add(editingButton);

        JLabel refreshLabel = new JLabel("Refresh Table:");
        JButton refreshButton = new JButton("?");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Refresh Table", "Updates the table with the latest data from the database.");
            }
        });
        panel.add(refreshLabel);
        panel.add(refreshButton);

        JLabel profilesLabel = new JLabel("Students' Profiles Button:");
        JButton profilesButton = new JButton("?");
        profilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTooltip("Students' Profiles Button", "Opens a window displaying detailed profiles of students.");
            }
        });
        panel.add(profilesLabel);
        panel.add(profilesButton);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void showTooltip(String title, String text) {
        JOptionPane.showMessageDialog(this, text, title, JOptionPane.INFORMATION_MESSAGE);
    }
}

    private void RefreshTable() {
        tableModel = (DefaultTableModel) Table.getModel();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT `Unique ID`, `Full Name`, `Strand/Section`, `LRN`, `Grade Level`, `Age` FROM studentsinfo");

            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] rowData = {
                    rs.getInt("Unique ID"),
                    rs.getString("Full Name"),
                    rs.getString("Strand/Section"),
                    rs.getString("LRN"),
                    rs.getString("Grade Level"),
                    rs.getString("Age")
                };
                tableModel.addRow(rowData);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    void createConnection() {
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("PLACEHOLDERlocation", "PLACEHOLDERusername", "PLACEHOLDERpassword");
        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateData(int uniqueID, String columnName, Object newValue) {
        try {
            String query = "UPDATE studentsinfo SET " + columnName + " = ? WHERE `Unique ID` = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setObject(1, newValue);
            pstmt.setInt(2, uniqueID);
            pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Data updated successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating data. Please try again.");
        }
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        FullName = new javax.swing.JTextField();
        StrandSection = new javax.swing.JTextField();
        Insert = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        Refresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        editFullName = new javax.swing.JTextField();
        editStrandSection = new javax.swing.JTextField();
        Update = new javax.swing.JButton();
        Edit = new javax.swing.JButton();
        editLRN = new javax.swing.JTextField();
        editGradeLevel = new javax.swing.JTextField();
        editAGE = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Delete = new javax.swing.JButton();
        LRN = new javax.swing.JTextField();
        Profile = new javax.swing.JButton();
        GradeLevel = new javax.swing.JTextField();
        AGE = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        helpContactButton = new javax.swing.JButton();
        legendManualButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Unique ID", "Full Name", "Strand/Section", "LRN", "Grade Level", "Age"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table);

        FullName.setText("Full Name...");
        FullName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                FullNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                FullNameFocusLost(evt);
            }
        });

        StrandSection.setText("Strand/Section...");
        StrandSection.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                StrandSectionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                StrandSectionFocusLost(evt);
            }
        });

        Insert.setBackground(new java.awt.Color(153, 255, 153));
        Insert.setText("Add");
        Insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InsertActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        Refresh.setBackground(new java.awt.Color(204, 255, 204));
        Refresh.setText("Refresh Table");
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Editing Textfields");

        Update.setText("Update");
        Update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateActionPerformed(evt);
            }
        });

        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        editLRN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLRNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(editGradeLevel)
                        .addGap(188, 188, 188))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Refresh, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(editFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(editLRN, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(editStrandSection, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(editAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(6, 6, 6))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Update, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editStrandSection, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editLRN, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editGradeLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Update, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel2.setText("Inserting Data Textfields");

        Delete.setBackground(new java.awt.Color(255, 0, 51));
        Delete.setForeground(new java.awt.Color(255, 255, 255));
        Delete.setText("Delete");
        Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteActionPerformed(evt);
            }
        });

        LRN.setText("LRN...");
        LRN.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                LRNFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                LRNFocusLost(evt);
            }
        });
        LRN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LRNActionPerformed(evt);
            }
        });

        Profile.setBackground(new java.awt.Color(255, 255, 153));
        Profile.setText("Students' Profiles");
        Profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProfileActionPerformed(evt);
            }
        });

        GradeLevel.setText("Grade Level...");
        GradeLevel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                GradeLevelFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                GradeLevelFocusLost(evt);
            }
        });

        AGE.setText("Age...");
        AGE.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                AGEFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                AGEFocusLost(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(0, 102, 255));
        jLabel3.setText("Help?");

        helpContactButton.setForeground(new java.awt.Color(0, 51, 255));
        helpContactButton.setText("Contact Me.");
        helpContactButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpContactButtonActionPerformed(evt);
            }
        });

        legendManualButton.setText("Legends Manual");
        legendManualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                legendManualButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(GradeLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(LRN, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(FullName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(StrandSection, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AGE, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Insert, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Profile, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(legendManualButton, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(helpContactButton)
                        .addGap(11, 11, 11))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Profile, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(helpContactButton)
                            .addComponent(legendManualButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FullName, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StrandSection, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AGE, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                            .addComponent(LRN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GradeLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Insert, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void InsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InsertActionPerformed
        try {
        String fullName = FullName.getText();
        String strandSection = StrandSection.getText();
        String lrn = LRN.getText().trim();
        String gradelevel = GradeLevel.getText();
        String age = AGE.getText();

        if (lrn.isEmpty() || lrn.length() != 12) {
            JOptionPane.showMessageDialog(this, "LRN must be 12 characters long and cannot be empty.");
            return; 
        }

        String dbop = "INSERT INTO studentsinfo (`Full Name`, `Strand/Section`, `LRN`, `Grade Level`, `Age`) VALUES (?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = con.prepareStatement(dbop);
        
        pstmt.setString(1, fullName);
        pstmt.setString(2, strandSection);
        pstmt.setString(3, lrn);
        pstmt.setString(4, gradelevel);
        pstmt.setString(5, age);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Data inserted successfully.");
            FullName.setText("");
            StrandSection.setText("");
            LRN.setText("");
            GradeLevel.setText("");
            AGE.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to insert data. Please try again.");
        }
        
        System.out.println("Inserted Data Successfully.");
        
        pstmt.close();
        
        RefreshTable();
    } catch (SQLException ex) {
        Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_InsertActionPerformed

    private void FullNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_FullNameFocusGained
        if (FullName.getText().equals("Full Name...")) {
        FullName.setText("");
        FullName.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_FullNameFocusGained

    private void FullNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_FullNameFocusLost
        if (FullName.getText().isEmpty()) {
        FullName.setText("Full Name...");
        FullName.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_FullNameFocusLost

    private void StrandSectionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_StrandSectionFocusGained
        if (StrandSection.getText().equals("Strand/Section...")) {
        StrandSection.setText("");
        StrandSection.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_StrandSectionFocusGained

    private void StrandSectionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_StrandSectionFocusLost
        if (StrandSection.getText().isEmpty()) {
        StrandSection.setText("Strand/Section...");
        StrandSection.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_StrandSectionFocusLost

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        RefreshTable();
    }//GEN-LAST:event_RefreshActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) Table.getModel();
    int selectedRow = Table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row to update.");
        return;
    }

    int uniqueID = (int) tableModel.getValueAt(selectedRow, 0);
    String fullName = editFullName.getText();
    String strandSection = editStrandSection.getText();
    String lrn = editLRN.getText();
    String gradeLevel = editGradeLevel.getText();
    String age = editAGE.getText();

    if (lrn.isEmpty() || lrn.length() != 12) {
        JOptionPane.showMessageDialog(this, "LRN must be 12 characters long and cannot be empty.");
        return;
    }

    try {
        String query = "UPDATE studentsinfo SET `Full Name` = ?, `Strand/Section` = ?, `LRN` = ?, `Grade Level` = ?, `Age` = ? WHERE `Unique ID` = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, fullName);
        pstmt.setString(2, strandSection);
        pstmt.setString(3, lrn);
        pstmt.setString(4, gradeLevel);
        pstmt.setString(5, age);
        pstmt.setInt(6, uniqueID);

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Data updated successfully!");
            tableModel.setValueAt(fullName, selectedRow, 1);
            tableModel.setValueAt(strandSection, selectedRow, 2);
            tableModel.setValueAt(lrn, selectedRow, 3);
            tableModel.setValueAt(gradeLevel, selectedRow, 4);
            tableModel.setValueAt(age, selectedRow, 5);
        } else {
            JOptionPane.showMessageDialog(this, "No rows updated. Verify the Unique ID.");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating data. Please try again.");
    }
    }//GEN-LAST:event_UpdateActionPerformed

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        int row = Table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }

        String fullName = (String) model.getValueAt(row, 1).toString();
        String strandSection = (String) model.getValueAt(row, 2).toString();
        String LRN = (String) model.getValueAt(row, 3).toString();
        String gradeLevel = (String) model.getValueAt(row, 4).toString();
        String age = (String) model.getValueAt(row, 5).toString();

        editFullName.setText(fullName);
        editStrandSection.setText(strandSection);
        editLRN.setText(LRN);
        editGradeLevel.setText(gradeLevel);
        editAGE.setText(age);
    }//GEN-LAST:event_EditActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed
        int selectedRowIndex = Table.getSelectedRow();
    
        if (selectedRowIndex != -1) {
            DefaultTableModel tableModel = (DefaultTableModel) Table.getModel();
            int uniqueID = (int) tableModel.getValueAt(selectedRowIndex, 0);
        
            try {
                PreparedStatement pstmt = con.prepareStatement("DELETE FROM studentsinfo WHERE `Unique ID` = ?");
                pstmt.setInt(1, uniqueID);
                pstmt.executeUpdate();
                pstmt.close();
            
                tableModel.removeRow(selectedRowIndex);

                JOptionPane.showMessageDialog(this, "Row deleted successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting row. Please try again.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }//GEN-LAST:event_DeleteActionPerformed

    private void LRNFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LRNFocusGained
       if (LRN.getText().equals("LRN...")) {
        LRN.setText("");
        LRN.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_LRNFocusGained

    private void LRNFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LRNFocusLost
        if (LRN.getText().isEmpty()) {
        LRN.setText("LRN...");
        LRN.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_LRNFocusLost

    private void LRNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LRNActionPerformed
        String lrn = LRN.getText().trim();
        if (lrn.length() > 12) {
        lrn = lrn.substring(0, 12);
        LRN.setText(lrn);
    }
    }//GEN-LAST:event_LRNActionPerformed

    private void editLRNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLRNActionPerformed
        String lrn = LRN.getText().trim();
        if (lrn.length() > 12) {
        lrn = lrn.substring(0, 12);
        editLRN.setText(lrn);
    }
    }//GEN-LAST:event_editLRNActionPerformed

    private void ProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProfileActionPerformed
        new DatabaseStudentsProfiles().setVisible(true);
    }//GEN-LAST:event_ProfileActionPerformed

    private void TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMouseClicked
        if (evt.getClickCount() == 2) {
        JTable target = (JTable) evt.getSource();
        int row = target.getSelectedRow();
        
        if (row != -1) {
            DefaultTableModel model = (DefaultTableModel) target.getModel();

            String firstName = model.getValueAt(row, 1).toString();
            String strandSection = model.getValueAt(row, 2).toString();
            String lrn = model.getValueAt(row, 3).toString();
            String gradeLevel = model.getValueAt(row, 4).toString(); // Retrieve Grade Level
            String age = model.getValueAt(row, 5).toString(); // Retrieve Age

            editFullName.setText(firstName);
            editStrandSection.setText(strandSection);
            editLRN.setText(lrn);
            editGradeLevel.setText(gradeLevel);
            editAGE.setText(age);
        }
    }
    }//GEN-LAST:event_TableMouseClicked

    private void helpContactButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpContactButtonActionPerformed
        ContactInfoDialog contactDialog = new ContactInfoDialog(this);
        contactDialog.setVisible(true);
    }//GEN-LAST:event_helpContactButtonActionPerformed

    private void legendManualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_legendManualButtonActionPerformed
        LegendsDialog legendsDialog = new LegendsDialog(this);
        legendsDialog.setVisible(true);
    }//GEN-LAST:event_legendManualButtonActionPerformed

    private void AGEFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AGEFocusGained
        if (AGE.getText().equals("Age...")) {
        AGE.setText("");
        AGE.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_AGEFocusGained

    private void AGEFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AGEFocusLost
        if (AGE.getText().isEmpty()) {
        AGE.setText("Age...");
        AGE.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_AGEFocusLost

    private void GradeLevelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_GradeLevelFocusGained
        if (GradeLevel.getText().equals("Grade Level...")) {
        GradeLevel.setText("");
        GradeLevel.setForeground(Color.BLACK);
        } 
    }//GEN-LAST:event_GradeLevelFocusGained

    private void GradeLevelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_GradeLevelFocusLost
        if (GradeLevel.getText().isEmpty()) {
        GradeLevel.setText("Grade Level...");
        GradeLevel.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_GradeLevelFocusLost

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
            java.util.logging.Logger.getLogger(DatabaseJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DatabaseJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DatabaseJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DatabaseJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DatabaseJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AGE;
    private javax.swing.JButton Delete;
    private javax.swing.JButton Edit;
    private javax.swing.JTextField FullName;
    private javax.swing.JTextField GradeLevel;
    private javax.swing.JButton Insert;
    private javax.swing.JTextField LRN;
    private javax.swing.JButton Profile;
    private javax.swing.JButton Refresh;
    private javax.swing.JTextField StrandSection;
    private javax.swing.JTable Table;
    private javax.swing.JButton Update;
    private javax.swing.JTextField editAGE;
    private javax.swing.JTextField editFullName;
    private javax.swing.JTextField editGradeLevel;
    private javax.swing.JTextField editLRN;
    private javax.swing.JTextField editStrandSection;
    private javax.swing.JButton helpContactButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton legendManualButton;
    // End of variables declaration//GEN-END:variables
}
