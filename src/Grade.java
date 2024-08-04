import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.*;

import java.sql.*;

public class Grade {

    public static void populateCourseComboBox(JComboBox<String> courseComboBox) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, course_name FROM courses")) {
            courseComboBox.removeAllItems();
            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String courseName = rs.getString("course_name");
                courseComboBox.addItem(courseId + ", " + courseName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void populateStudentComboBox(JComboBox<String> studentComboBox) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, name, surname FROM students")) {
            studentComboBox.removeAllItems();
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("name");
                String studentSurname = rs.getString("surname");
                studentComboBox.addItem(studentName + " " + studentSurname + ", " + studentId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static int getStudentId(String studentInfo) throws SQLException {
        String[] parts = studentInfo.split(", ");
        String[] nameParts = parts[0].split(" ");
        String studentName = nameParts[0];
        String studentSurname = nameParts[1];

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT student_id FROM students WHERE name = ? AND surname = ?")) {
            pstmt.setString(1, studentName);
            pstmt.setString(2, studentSurname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                } else {
                    return -1;
                }
            }
        }
    }
    
    public static int getCourseId(String courseInfo) throws SQLException {
        String[] parts = courseInfo.split(", ");
        String courseName = parts[1];

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT course_id FROM courses WHERE course_name = ?")) {
            pstmt.setString(1, courseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                } else {
                    return -1;
                }
            }
        }
    }
    
    public static void addOrUpdateGrade(JFrame frame, JComboBox<String> studentComboBox, JComboBox<String> courseComboBox, JTextField gradeField) {
        Connection conn = null;
        try {
            String selectedStudent = (String) studentComboBox.getSelectedItem();
            String selectedCourse = (String) courseComboBox.getSelectedItem();
            String gradeValue = gradeField.getText();

            int courseId;
            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(frame, "Please select a course.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                courseId = getCourseId(selectedCourse);
            }

            double grade = Double.parseDouble(gradeValue);
            if (grade < 0 || grade > 10) {
                JOptionPane.showMessageDialog(frame, "Please enter a grade from 0 to 10.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int studentId;
            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(frame, "Please select a student.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                studentId = getStudentId(selectedStudent);
            }

            if (studentId == -1) {
                JOptionPane.showMessageDialog(frame, "The selected student was not found. Please refresh.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (courseId == -1) {
                JOptionPane.showMessageDialog(frame, "The selected course was not found. Please refresh.", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }

            conn = DBConnection.getConnection();
            String checkDuplicateQuery = "SELECT * FROM grades WHERE student_id = ? AND course_id = ?";
            PreparedStatement checkDuplicateStmt = conn.prepareStatement(checkDuplicateQuery);
            checkDuplicateStmt.setInt(1, studentId);
            checkDuplicateStmt.setInt(2, courseId);
            ResultSet duplicateResult = checkDuplicateStmt.executeQuery();

            if (duplicateResult.next()) {
                int choice = JOptionPane.showConfirmDialog(frame, "This student is already enrolled in the course. Do you want to give a new grade?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    String newGradeValue = JOptionPane.showInputDialog(frame, "Enter the new grade (0 to 10):");
                    double newGrade = Double.parseDouble(newGradeValue);
                    if (newGrade < 0 || newGrade > 10) {
                        JOptionPane.showMessageDialog(frame, "Please enter a grade from 0 to 10.", "Warning", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String updateGradeQuery = "UPDATE grades SET grade = ? WHERE student_id = ? AND course_id = ?";
                    PreparedStatement updateGradeStmt = conn.prepareStatement(updateGradeQuery);
                    updateGradeStmt.setString(1, newGradeValue);
                    updateGradeStmt.setInt(2, studentId);
                    updateGradeStmt.setInt(3, courseId);
                    updateGradeStmt.executeUpdate();

                    JOptionPane.showMessageDialog(frame, "Grade updated successfully.", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }

            String insertGradeQuery = "INSERT INTO grades (student_id, course_id, grade) VALUES (?, ?, ?)";
            PreparedStatement insertGradeStmt = conn.prepareStatement(insertGradeQuery);
            insertGradeStmt.setInt(1, studentId);
            insertGradeStmt.setInt(2, courseId);
            insertGradeStmt.setString(3, gradeValue);
            insertGradeStmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Grade added successfully.", "Message", JOptionPane.INFORMATION_MESSAGE);
            gradeField.setText("");

            insertGradeStmt.close();
            duplicateResult.close();
            checkDuplicateStmt.close();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a number from 0 to 10.", "Warning", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
