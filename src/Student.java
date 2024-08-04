import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Student {


    //private static JFrame frame;
    private static JTable table_1;
    static JFrame frame;


    public static void setFrame(JFrame frame) {
        Student.frame = frame;
    }

    public static void setTable(JTable table) {
        Student.table_1 = table;
    }
    
    public static void createStudent(String name, String surname, String residence, java.util.Date birthday, String gender) {
        if (name.isEmpty() || surname.isEmpty() || residence.isEmpty() || birthday == null || gender == null) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedBirthday = sdf.format(birthday);

        try (Connection conn = DBConnection.getConnection()) {
            String insertStudentSQL = "INSERT INTO students (name, surname, gender, residence, birthday) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertStudentSQL)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setString(3, gender);
                preparedStatement.setString(4, residence);
                preparedStatement.setString(5, formattedBirthday);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "New student added successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add new student.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
    
    static void displayAllStudents() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Student ID");
        model.addColumn("Name");
        model.addColumn("Surname");
        model.addColumn("Gender");
        model.addColumn("Residence");
        model.addColumn("Birthday");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("gender"),
                        rs.getString("residence"),
                        rs.getString("birthday")
                });
            }

            if (table_1 != null) {
                table_1.setModel(model);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static void displayStudentsByName(String searchName) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Student ID");
        model.addColumn("Name");
        model.addColumn("Surname");
        model.addColumn("Gender");
        model.addColumn("Residence");
        model.addColumn("Birthday");

        String query = "SELECT * FROM students WHERE name LIKE ? OR surname LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchName + "%");
            stmt.setString(2, "%" + searchName + "%");
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("gender"),
                            rs.getString("residence"),
                            rs.getString("birthday")
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
    
    static void displayStudentDetails(String name, String surname, String studentId, String residence, String birthday, String gender) {
        JTextArea textArea = new JTextArea();

        String query = "SELECT students.student_id, students.name AS student_name, students.surname AS student_surname, students.gender, students.residence, students.birthday, " +
                       "courses.course_id, courses.course_name, grades.grade " +
                       "FROM students " +
                       "LEFT JOIN grades ON students.student_id = grades.student_id " +
                       "LEFT JOIN courses ON grades.course_id = courses.course_id " +
                       "WHERE name = ? OR surname = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            try (ResultSet rs = preparedStatement.executeQuery()) {

                JFrame studentFrame = new JFrame("Student Details: " + name + " " + surname);
                studentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                studentFrame.getContentPane().setLayout(new BorderLayout());

                JPanel studentInfoPanel = new JPanel(new GridLayout(10, 4));
                JLabel nameLabel = new JLabel("Name: ");
                JTextField nameField = new JTextField();
                JLabel surnameLabel = new JLabel("Surname: ");
                JTextField surnameField = new JTextField();
                JLabel idLabel = new JLabel("Student ID: ");
                JTextField idField = new JTextField();
                JLabel genderLabel = new JLabel("Gender: ");
                JTextField genderField = new JTextField();
                JLabel residenceLabel = new JLabel("Residence: ");
                JTextField residenceField = new JTextField();
                JLabel birthdayLabel = new JLabel("Birthday: ");
                JTextField birthdayField = new JTextField();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "No result");
                } else {
                    boolean hasCourses = false;

                    do {
                        nameField.setText(rs.getString("student_name"));
                        surnameField.setText(rs.getString("student_surname"));
                        idField.setText(rs.getString("student_id"));
                        genderField.setText(rs.getString("gender"));
                        residenceField.setText(rs.getString("residence"));
                        birthdayField.setText(rs.getString("birthday"));

                        int courseId = rs.getInt("course_id");
                        String courseName = rs.getString("course_name");
                        String grade = rs.getString("grade");

                        if (courseId != 0) {
                            textArea.append("Course ID: " + courseId + ", Course Name: " + courseName + ", Grade: " + grade + "\n");
                            hasCourses = true;
                        }
                    } while (rs.next());

                    if (!hasCourses) {
                        textArea.append("No courses registered.\n");
                    }
                }

                nameField.setEditable(false);
                surnameField.setEditable(false);
                idField.setEditable(false);
                genderField.setEditable(false);
                birthdayField.setEditable(false);
                residenceField.setEditable(true);

                studentInfoPanel.add(nameLabel);
                studentInfoPanel.add(nameField);
                studentInfoPanel.add(surnameLabel);
                studentInfoPanel.add(surnameField);
                studentInfoPanel.add(idLabel);
                studentInfoPanel.add(idField);
                studentInfoPanel.add(genderLabel);
                studentInfoPanel.add(genderField);
                studentInfoPanel.add(residenceLabel);
                studentInfoPanel.add(residenceField);
                studentInfoPanel.add(birthdayLabel);
                studentInfoPanel.add(birthdayField);

                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                studentFrame.getContentPane().add(studentInfoPanel, BorderLayout.NORTH);
                studentFrame.getContentPane().add(new JSeparator(), BorderLayout.CENTER);
                studentFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try (Connection conn = DBConnection.getConnection();
                             PreparedStatement updateStatement = conn.prepareStatement("UPDATE students SET residence = ? WHERE student_id = ?")) {
                            updateStatement.setString(1, residenceField.getText());
                            updateStatement.setString(2, idField.getText());

                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(null, "The student's residence was successfully updated.");
                            }

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error updating the student's residence.");
                        }
                    }
                });
                studentInfoPanel.add(saveButton);

                JButton deleteStudentButton = new JButton("Delete Student");
                deleteStudentButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the student?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try (Connection conn = DBConnection.getConnection()) {
                                conn.setAutoCommit(false);
                                try (PreparedStatement deleteGradesStatement = conn.prepareStatement("DELETE FROM grades WHERE student_id = ?")) {
                                    deleteGradesStatement.setString(1, idField.getText());
                                    deleteGradesStatement.executeUpdate();
                                }

                                try (PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
                                    deleteStatement.setString(1, idField.getText());
                                    deleteStatement.executeUpdate();
                                }

                                conn.commit();
                                JOptionPane.showMessageDialog(null, "The student was successfully deleted.");
                                studentFrame.dispose();

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error deleting student.");
                                try {
                                    conn.rollback();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(saveButton);
                buttonPanel.add(deleteStudentButton);
                studentFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

                studentFrame.setSize(600, 400);
                studentFrame.setLocationRelativeTo(null);
                studentFrame.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } 
}
