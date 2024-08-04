import java.awt.EventQueue;
import java.awt.Font;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.*;

public class Gui {

    JFrame frame;
    private JTextField nameField;
    private JTextField residencefield;
    private JTextField searchstudentfield;
    private JTable table;
    private JTable table_1;
    private JTextField createaCourse;
    static JComboBox<String> courseChoice;
    private JTextField Grade1;
    private JTextField surnameField;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Gui window = new Gui();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Gui() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("School");
        frame.setBounds(100, 100, 900, 595);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        table = new JTable();
        table.setBounds(736, 219, 0, 156);
        frame.getContentPane().add(table);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(254, 21, 355, 434);
        frame.getContentPane().add(scrollPane);

        table_1 = new JTable();
        scrollPane.setViewportView(table_1);

        Course.setFrame(frame);
        Course.setTable(table_1);
        Student.setFrame(frame);
        Student.setTable(table_1);

        table_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table_1.getSelectedRow();
                int column = table_1.getSelectedColumn();
                String columnName = table_1.getColumnName(column);

                if (columnName.equals("Course ID") || columnName.equals("Course Name")) {
                    int courseId = (int) table_1.getModel().getValueAt(row, 0);
                    String courseName = table_1.getModel().getValueAt(row, 1).toString();
                    Course.displayCourseDetails(courseId, courseName);
                } else {
                    String selectedName = table_1.getModel().getValueAt(row, 1).toString();
                    String selectedSurname = table_1.getModel().getValueAt(row, 2).toString();

                    String studentId = table_1.getModel().getValueAt(row, 0).toString();
                    String residence = table_1.getModel().getValueAt(row, 4).toString();
                    String birthday = table_1.getModel().getValueAt(row, 5).toString();
                    String gender = table_1.getModel().getValueAt(row, 3).toString();
                    Student.displayStudentDetails(selectedName, selectedSurname, studentId, residence, birthday, gender);
                }
            }
        });

        //STUDENT
        JLabel titleStudent = new JLabel("Student");
        titleStudent.setForeground(new Color(255, 0, 0));
        titleStudent.setFont(new Font("Tahoma", Font.BOLD, 26));
        titleStudent.setBounds(54, 34, 190, 41);
        frame.getContentPane().add(titleStudent);

        JLabel lblNewLabel_4 = new JLabel("Create a new Student");
        lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel_4.setBounds(28, 86, 190, 14);
        frame.getContentPane().add(lblNewLabel_4);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setBounds(94, 252, 106, 20);
        frame.getContentPane().add(dateChooser);

        JLabel birthdaylabel = new JLabel("Birthday");
        birthdaylabel.setBounds(28, 258, 56, 14);
        frame.getContentPane().add(birthdaylabel);

        JLabel residencelabel = new JLabel("Residence");
        residencelabel.setBounds(28, 227, 67, 14);
        frame.getContentPane().add(residencelabel);

        JLabel genderlabel = new JLabel("Gender");
        genderlabel.setBounds(28, 191, 46, 14);
        frame.getContentPane().add(genderlabel);

        JLabel namelabel = new JLabel("Name");
        namelabel.setBounds(28, 129, 46, 14);
        frame.getContentPane().add(namelabel);

        JLabel surnamelabel = new JLabel("Surname");
        surnamelabel.setBounds(28, 154, 67, 14);
        frame.getContentPane().add(surnamelabel);

        surnameField = new JTextField();
        surnameField.setColumns(10);
        surnameField.setBounds(94, 157, 106, 20);
        frame.getContentPane().add(surnameField);

        JComboBox<String> genderChoice = new JComboBox<>();
        genderChoice.addItem("Male");
        genderChoice.addItem("Female");
        genderChoice.setBounds(94, 188, 107, 20);
        frame.getContentPane().add(genderChoice);

        nameField = new JTextField();
        nameField.setBounds(94, 126, 106, 20);
        frame.getContentPane().add(nameField);
        nameField.setColumns(10);

        residencefield = new JTextField();
        residencefield.setBounds(94, 221, 106, 20);
        frame.getContentPane().add(residencefield);
        residencefield.setColumns(10);

        JButton createbtn = new JButton("Create");
        createbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String surname = surnameField.getText();
                String residence = residencefield.getText();
                java.util.Date birthday = dateChooser.getDate();
                String gender = (String) genderChoice.getSelectedItem();

                Student.createStudent(name, surname, residence, birthday, gender);

                surnameField.setText("");
                nameField.setText("");
                residencefield.setText("");
                dateChooser.setDate(null);
                genderChoice.setSelectedIndex(0);
            }
        });

        createbtn.setBounds(66, 283, 89, 23);
        frame.getContentPane().add(createbtn);

        JButton allstudentsbtn = new JButton("View all Students");
        allstudentsbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Student.displayAllStudents();

            }
        });
        allstudentsbtn.setBounds(27, 399, 191, 23);
        frame.getContentPane().add(allstudentsbtn);

        JLabel studentlabel = new JLabel("Search a Student");
        studentlabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        studentlabel.setBounds(53, 342, 176, 14);
        frame.getContentPane().add(studentlabel);

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        separator.setBounds(238, 21, 28, 434);
        frame.getContentPane().add(separator);

        JButton searchStudentBtn = new JButton("Search");
        searchStudentBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchName = searchstudentfield.getText().trim();
                if (!searchName.isEmpty()) {
                    Student.displayStudentsByName(searchName);
                } else {
                    Student.displayAllStudents();
                }
                searchstudentfield.setText(null);
            }
        });
        searchStudentBtn.setBounds(131, 367, 87, 23);
        frame.getContentPane().add(searchStudentBtn);

        searchstudentfield = new JTextField();
        searchstudentfield.setBounds(27, 368, 99, 20);
        frame.getContentPane().add(searchstudentfield);
        searchstudentfield.setColumns(10);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(27, 317, 173, 14);
        frame.getContentPane().add(separator_1);

        //COURSE
        JLabel titleCourse = new JLabel("Course");
        titleCourse.setForeground(Color.RED);
        titleCourse.setFont(new Font("Tahoma", Font.BOLD, 26));
        titleCourse.setBounds(704, 34, 190, 41);
        frame.getContentPane().add(titleCourse);

        JLabel courselabel = new JLabel("Create a new Course");
        courselabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        courselabel.setBounds(681, 86, 176, 14);
        frame.getContentPane().add(courselabel);

        JButton allcoursesbtn = new JButton("View all Courses");
        allcoursesbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Course.displayAllCourses();
            }
        });
        allcoursesbtn.setBounds(665, 263, 192, 23);
        frame.getContentPane().add(allcoursesbtn);

        createaCourse = new JTextField();
        createaCourse.setColumns(10);
        createaCourse.setBounds(704, 126, 106, 20);
        frame.getContentPane().add(createaCourse);

        JButton createcoursebtn = new JButton("Create");
        createcoursebtn.setBounds(714, 159, 89, 23);
        frame.getContentPane().add(createcoursebtn);

        JButton searchcoursebtn = new JButton("Search");
        searchcoursebtn.setBounds(775, 232, 82, 23);
        frame.getContentPane().add(searchcoursebtn);

        courseChoice = new JComboBox<>();
        courseChoice.setBounds(657, 232, 107, 20);
        frame.getContentPane().add(courseChoice);

        JSeparator separator_2 = new JSeparator();
        separator_2.setOrientation(SwingConstants.VERTICAL);
        separator_2.setBounds(619, 21, 28, 434);
        frame.getContentPane().add(separator_2);

        JLabel courselabel_1 = new JLabel("Search a Course");
        courselabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
        courselabel_1.setBounds(704, 207, 176, 14);
        frame.getContentPane().add(courselabel_1);

        JSeparator separator_1_1 = new JSeparator();
        separator_1_1.setBounds(673, 198, 173, 14);
        frame.getContentPane().add(separator_1_1);

        createcoursebtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String courseName = createaCourse.getText().trim();
                Course.createCourse(courseName, Gui.courseChoice, frame);
                createaCourse.setText(null);
            }
        });

        searchcoursebtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = (String) courseChoice.getSelectedItem();
                if (selectedCourse != null) {
                    Course.displayCourses(selectedCourse);
                }
            }
        });

        Course.populateCourseChoice();

        JSeparator separator_1_1_1 = new JSeparator();
        separator_1_1_1.setBounds(667, 317, 173, 14);
        frame.getContentPane().add(separator_1_1_1);

        //GRADE
        JLabel lblGrade = new JLabel("Grade");
        lblGrade.setForeground(Color.RED);
        lblGrade.setFont(new Font("Tahoma", Font.BOLD, 26));
        lblGrade.setBounds(721, 325, 190, 41);
        frame.getContentPane().add(lblGrade);

        JLabel addCourseLabel = new JLabel("Course:");
        addCourseLabel.setBounds(636, 368, 100, 20);
        frame.getContentPane().add(addCourseLabel);

        JComboBox<String> courseComboBox = new JComboBox<>();
        courseComboBox.setBounds(704, 368, 150, 20);
        frame.getContentPane().add(courseComboBox);

        Grade.populateCourseComboBox(courseComboBox);

        JLabel addStudentLabel = new JLabel("Student:");
        addStudentLabel.setBounds(636, 400, 100, 20);
        frame.getContentPane().add(addStudentLabel);

        JComboBox<String> studentComboBox = new JComboBox<String>();
        studentComboBox.setBounds(704, 400, 150, 20);
        frame.getContentPane().add(studentComboBox);

        Grade.populateStudentComboBox(studentComboBox);

        JButton addGrade = new JButton("Grade");
        addGrade.setBounds(775, 431, 82, 23);
        frame.getContentPane().add(addGrade);
        
        addGrade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Grade.addOrUpdateGrade(frame, studentComboBox, courseComboBox, Grade1);
            }
        });

        JLabel addaGrade = new JLabel("Grade:");
        addaGrade.setBounds(636, 431, 100, 20);
        frame.getContentPane().add(addaGrade);

        Grade1 = new JTextField();
        Grade1.setColumns(10);
        Grade1.setBounds(704, 431, 56, 20);
        frame.getContentPane().add(Grade1);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBounds(383, 466, 89, 23);
        frame.getContentPane().add(refreshButton);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Grade.populateCourseComboBox(courseComboBox);
                Grade.populateStudentComboBox(studentComboBox);
                Course.populateCourseChoice();
                Student.displayAllStudents();
            }
        });
    }
}
