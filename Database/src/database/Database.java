package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    public static void main(String[] args) {
        Database dat = new Database();
        new DatabaseLogin().setVisible(true);
        dat.createConnection();
    }
    
    void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("PLACEHOLDERlocation", "PLACEHOLDERusername", "PLACEHOLDERpassword");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM studentsinfo");
        
            System.out.println("Unique ID\tFull Name\tStrand/Section\tLRN\tGrade Level\tAge");
        
            while (rs.next()) {
                int uniqueID = rs.getInt("Unique ID");
                String fullName = rs.getString("Full Name");
                String strandSection = rs.getString("Strand/Section");
                String lrn = rs.getString("LRN");
                int gradeLevel = rs.getInt("Grade Level");
                int age = rs.getInt("Age");
            
                System.out.printf("%d\t\t%s\t\t%s\t\t%s\t\t%d\t\t%d%n", uniqueID, fullName, strandSection, lrn, gradeLevel, age);
            }
            rs.close();
            stmt.close();
            con.close();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
