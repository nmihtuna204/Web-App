package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management_mvc";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Octiu123";
    
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }
    }
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                
                students.add(student);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                return student;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_code, full_name, email, major, photo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            pstmt.setString(5, student.getPhoto());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET full_name = ?, email = ?, major = ?, photo = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getMajor());
            pstmt.setString(4, student.getPhoto());
            pstmt.setInt(5, student.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                students.add(student);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    /**
     * Get students with sorting capability
     * @param sortBy Column to sort by (id, student_code, full_name, email, major)
     * @param order Sort order (asc or desc)
     * @return List of students sorted by specified column
     */
    public List<Student> getStudentsSorted(String sortBy, String order) {
        // Validate sortBy and order parameters
        sortBy = validateSortBy(sortBy);
        order = validateOrder(order);
        
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY " + sortBy + " " + order;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    /**
     * Get students filtered by major
     * @param major The major to filter by
     * @return List of students with the specified major
     */
    public List<Student> getStudentsByMajor(String major) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, major);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    /**
     * Get students with both filtering and sorting (BONUS METHOD)
     * @param major Major to filter by (null or empty for all)
     * @param sortBy Column to sort by
     * @param order Sort order (asc or desc)
     * @return List of students filtered and sorted
     */
    public List<Student> getStudentsFiltered(String major, String sortBy, String order) {
        // Validate parameters
        sortBy = validateSortBy(sortBy);
        order = validateOrder(order);
        
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students");
        
        // Add WHERE clause if major is specified
        boolean hasMajor = (major != null && !major.trim().isEmpty());
        if (hasMajor) {
            sql.append(" WHERE major = ?");
        }
        
        // Add ORDER BY clause
        sql.append(" ORDER BY ").append(sortBy).append(" ").append(order);
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            // Set parameter if filtering by major
            if (hasMajor) {
                pstmt.setString(1, major);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    /**
     * Validate sortBy parameter against allowed columns
     * @param sortBy The column name to validate
     * @return Valid column name or "id" as default
     */
    private String validateSortBy(String sortBy) {
        String[] validColumns = {"id", "student_code", "full_name", "email", "major"};
        
        if (sortBy != null) {
            for (String col : validColumns) {
                if (col.equals(sortBy)) {
                    return sortBy;
                }
            }
        }
        
        return "id"; // default
    }
    
    /**
     * Validate order parameter
     * @param order The sort order to validate
     * @return "ASC" or "DESC"
     */
    private String validateOrder(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return "DESC";
        }
        return "ASC"; // default
    }
    
    /**
     * Helper method to map ResultSet to Student object
     * @param rs ResultSet from query
     * @return Student object
     * @throws SQLException if database access error occurs
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentCode(rs.getString("student_code"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setMajor(rs.getString("major"));
        student.setPhoto(rs.getString("photo"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        return student;
    }
    
    /**
     * Get total count of students in database
     * @return Total number of student records
     */
    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get paginated list of students
     * @param offset Starting position (0-based)
     * @param limit Number of records to return
     * @return List of students for the specified page
     */
    public List<Student> getStudentsPaginated(int offset, int limit) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    /**
     * Get total count of students filtered by major
     * @param major The major to filter by
     * @return Total number of students in that major
     */
    public int getTotalStudentsByMajor(String major) {
        String sql = "SELECT COUNT(*) FROM students WHERE major = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, major);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get paginated and filtered students by major
     * @param major Major to filter by
     * @param offset Starting position
     * @param limit Number of records
     * @return Paginated list of students in specified major
     */
    public List<Student> getStudentsByMajorPaginated(String major, int offset, int limit) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, major);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
}
