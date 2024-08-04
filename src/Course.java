import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;

public class Course {

    static JFrame frame;
    private static JTable table_1;

    public static void setFrame(JFrame frame) {
        Course.frame = frame;
    }

    public static void setTable(JTable table) {
        Course.table_1 = table;
    }
    
    public static void createCourse(String courseName, JComboBox<String> courseChoice, JFrame frame) {
        if (!courseName.isEmpty()) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO courses (course_name) VALUES (?)")) {
                 
                preparedStatement.setString(1, courseName);
                preparedStatement.executeUpdate();

                populateCourseChoice();

                JOptionPane.showMessageDialog(frame, "Course saved successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving course: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a course name.");
        }
    }

    static void displayCourses(String selectedCourse) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Course ID");
        model.addColumn("Course Name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM courses WHERE course_name = ?")) {
             
            pstmt.setString(1, selectedCourse);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("course_id"),
                            rs.getString("course_name")
                    });
                }
                if (table_1 != null) {
                    table_1.setModel(model);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static void populateCourseChoice() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_name FROM courses")) {
             
            Gui.courseChoice.removeAllItems();

            while (rs.next()) {
                Gui.courseChoice.addItem(rs.getString("course_name"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static void displayAllCourses() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Course ID");
        model.addColumn("Course Name");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
             
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name")
                });
            }
            if (table_1 != null) {
                table_1.setModel(model);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static void displayCourseDetails(int courseId, String courseName) {
        String query = "SELECT students.student_id, students.name AS student_name, students.surname AS student_surname, grades.grade " +
                       "FROM students " +
                       "JOIN grades ON students.student_id = grades.student_id " +
                       "WHERE grades.course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
             
            preparedStatement.setInt(1, courseId);
            try (ResultSet rs = preparedStatement.executeQuery()) {

                JFrame courseFrame = new JFrame("Course Details: " + courseName);
                courseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                courseFrame.getContentPane().setLayout(new BorderLayout());

                JPanel courseInfoPanel = new JPanel(new GridLayout(3, 2)); 
                JLabel courseIdLabel = new JLabel("Course ID: ");
                JTextField courseIdField = new JTextField(Integer.toString(courseId));
                JLabel courseNameLabel = new JLabel("Course Name: ");
                JTextField courseNameField = new JTextField(courseName);

                courseIdField.setEditable(false);
                courseNameField.setEditable(false);

                courseInfoPanel.add(courseIdLabel);
                courseInfoPanel.add(courseIdField);
                courseInfoPanel.add(courseNameLabel);
                courseInfoPanel.add(courseNameField);

                DefaultListModel<String> studentListModel = new DefaultListModel<>();
                JList<String> studentList = new JList<>(studentListModel);

                while (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String studentName = rs.getString("student_name");
                    String studentSurname = rs.getString("student_surname");
                    String grade = rs.getString("grade");
                    studentListModel.addElement("Student ID: " + studentId + ", Student Name: " + studentName + " " + studentSurname + ", Grade: " + grade);
                }

                JScrollPane studentScrollPane = new JScrollPane(studentList);

                JButton deleteCourseButton = new JButton("Delete Course");
                deleteCourseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this course?", "Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try (Connection conn = DBConnection.getConnection()) {
                                conn.setAutoCommit(false);

                                try (PreparedStatement deleteGradesStatement = conn.prepareStatement("DELETE FROM grades WHERE course_id = ?")) {
                                    deleteGradesStatement.setInt(1, courseId);
                                    deleteGradesStatement.executeUpdate();
                                }

                                try (PreparedStatement deleteCourseStatement = conn.prepareStatement("DELETE FROM courses WHERE course_id = ?")) {
                                    deleteCourseStatement.setInt(1, courseId);
                                    deleteCourseStatement.executeUpdate();
                                }

                                conn.commit();
                                JOptionPane.showMessageDialog(null, "Course deleted successfully.");
                                courseFrame.dispose();

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error deleting course.");
                                try {
                                    conn.rollback();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });

                courseFrame.getContentPane().add(courseInfoPanel, BorderLayout.NORTH);
                courseFrame.getContentPane().add(studentScrollPane, BorderLayout.CENTER);
                courseFrame.getContentPane().add(deleteCourseButton, BorderLayout.SOUTH);

                courseFrame.setSize(400, 300);
                courseFrame.setLocationRelativeTo(null); 
                courseFrame.setVisible(true);

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
