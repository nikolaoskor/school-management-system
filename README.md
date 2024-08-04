# School Management System

## Description

This is a desktop application designed to manage the operations of a school. It provides functionalities to manage courses, students, and grades efficiently. Users can add and edit courses and student details, assign grades to students for their courses, and view the academic performance of students through a graphical user interface. The application facilitates easy data entry and retrieval, making school management more streamlined.

## Technologies Used

- **Java:** For developing the application and graphical user interface.
- **MySQL:** For storing and retrieving data.

## Tools Used

- **Eclipse IDE:** For Java development.
- **XAMPP:** For setting up the local server environment (including Apache and MySQL).
- **phpMyAdmin:** For managing the MySQL database.

## Features

1. **Course Management:**
   - Add, edit, and delete courses.

2. **Student Management:**
   - Add, edit, and delete students with details such as name, surname, date of birth, gender, and address.

3. **Grade Management:**
   - Assign and manage grades for courses taken by students.

4. **Data Entry and Viewing:**
   - User-friendly graphical interface for entering and displaying data.

5. **Student Course and Grade View:**
   - View and search for a student's courses and grades through a dedicated form.

## How to Run

Follow these steps to set up and run the application:

1. **Download the Application:**
   - Clone the repository to your local machine using the following command:
     ```bash
     git clone https://github.com/nikolaoskor/school-management-system.git
     ```
   - Alternatively, download the project as a ZIP file and extract it to your desired location.

2. **Install XAMPP:**
   - Download and install [XAMPP](https://www.apachefriends.org/index.html) if you haven't already.
   - Launch the XAMPP Control Panel and start both **Apache** and **MySQL** services.

3. **Create the Database:**
   - Open phpMyAdmin by navigating to `http://localhost/phpmyadmin` in your web browser.
   - Use the **Import** feature to execute the queries from the `schoolDB.sql` file:
     - Click on the **Import** tab in phpMyAdmin.
     - Click **Choose File** and navigate to the project folder on your local machine.
     - Select the `schoolDB.sql` file.
     - Click **Go** to execute the SQL script. This will create the database and tables.

4. **Configure the Application:**
   - Add the following JAR files to the `Referenced Libraries` in your Eclipse project:
     - Add `jcalendar-1.4.jar`  to  the `Classpath` (Required for calendar functionality)
     - Add `JPlanner.jar` to  the `Classpath` (Required for planning and scheduling functionality)
     - Add `mysql-connector-java-8.3.0.jar` to the `Modulepath` (This JAR file is essential for connecting to the MySQL database)
   -  These JAR files can be found in the libs folder of the project and are necessary for the application to function properly. 
   
5. **Update Database Connection Settings:**
   - Ensure that the database connection settings in the `DBConnection` class match your setup:
     ```java
     import java.sql.Connection;
     import java.sql.DriverManager;
     import java.sql.SQLException;

     public class DBConnection {
         private static final String URL = "jdbc:mysql://localhost:3306/";
         private static final String DB_NAME = "schoolDB";
         private static final String USERNAME = "root";
         private static final String PASSWORD = "";

         public static Connection getConnection() throws SQLException {
             return DriverManager.getConnection(URL + DB_NAME, USERNAME, PASSWORD);
         }
     }
     ```

6. **Run the Application:**
   - Open Eclipse IDE and import the project.
   - Run the project as a Java Application.
