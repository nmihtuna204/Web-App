package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet("/student")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listStudents(request, response);
                break;
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
                searchStudents(request, response);
                break;
            case "sort":
                sortStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "insert";
        }
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get page parameter (default to 1)
        String pageParam = request.getParameter("page");
        int currentPage = 1;
        
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        // Records per page
        int recordsPerPage = 10;
        
        // Calculate offset
        int offset = (currentPage - 1) * recordsPerPage;
        
        // Get paginated data
        List<Student> students = studentDAO.getStudentsPaginated(offset, recordsPerPage);
        int totalRecords = studentDAO.getTotalStudents();
        
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        
        // Handle case where currentPage exceeds totalPages
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
            offset = (currentPage - 1) * recordsPerPage;
            students = studentDAO.getStudentsPaginated(offset, recordsPerPage);
        }
        
        // Set attributes for view
        request.setAttribute("students", students);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student student = studentDAO.getStudentById(id);
        
        request.setAttribute("student", student);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        
        // Handle photo upload
        String photoFilename = handleFileUpload(request, "photo");
        if (photoFilename != null) {
            student.setPhoto(photoFilename);
        }
        
        // Validate student data
        if (!validateStudent(student, request)) {
            // Validation failed - preserve entered data and show errors
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        // Validation passed - proceed with insert
        if (studentDAO.addStudent(student)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=new&error=Failed to add student");
        }
    }
    
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student();
        student.setId(id);
        student.setStudentCode(studentCode);
        student.setFullName(fullName);
        student.setEmail(email);
        student.setMajor(major);
        
        // Handle photo upload - if new photo uploaded, use it; otherwise keep existing
        String photoFilename = handleFileUpload(request, "photo");
        if (photoFilename != null) {
            student.setPhoto(photoFilename);
        } else {
            // Keep existing photo
            Student existingStudent = studentDAO.getStudentById(id);
            student.setPhoto(existingStudent.getPhoto());
        }
        
        // Validate student data
        if (!validateStudent(student, request)) {
            // Validation failed - preserve entered data and show errors
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        // Validation passed - proceed with update
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=edit&id=" + id + "&error=Failed to update");
        }
    }
    
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        List<Student> students;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentDAO.searchStudents(keyword);
            request.setAttribute("keyword", keyword);
        } else {
            students = studentDAO.getAllStudents();
        }
        
        request.setAttribute("students", students);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Sort students by specified column and order (with optional filtering by major)
     */
    private void sortStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        String major = request.getParameter("major");
        
        List<Student> students;
        
        // Check if filtering by major is requested
        if (major != null && !major.trim().isEmpty()) {
            // Use combined filter + sort method
            students = studentDAO.getStudentsFiltered(major, sortBy, order);
            request.setAttribute("selectedMajor", major);
        } else {
            // Just sort, no filter
            students = studentDAO.getStudentsSorted(sortBy, order);
        }
        
        // Set attributes for view
        request.setAttribute("students", students);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Filter students by major with pagination support
     */
    private void filterStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String major = request.getParameter("major");
        
        // Get page parameter (default to 1)
        String pageParam = request.getParameter("page");
        int currentPage = 1;
        
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        // Records per page
        int recordsPerPage = 10;
        int offset = (currentPage - 1) * recordsPerPage;
        
        List<Student> students;
        int totalRecords;
        int totalPages;
        
        // If major is empty or null, show all students with pagination
        if (major == null || major.trim().isEmpty()) {
            students = studentDAO.getStudentsPaginated(offset, recordsPerPage);
            totalRecords = studentDAO.getTotalStudents();
        } else {
            students = studentDAO.getStudentsByMajorPaginated(major, offset, recordsPerPage);
            totalRecords = studentDAO.getTotalStudentsByMajor(major);
            request.setAttribute("selectedMajor", major);
        }
        
        // Calculate total pages
        totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        
        // Handle case where currentPage exceeds totalPages
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        // Set attributes
        request.setAttribute("students", students);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Validates student data before insert or update operations.
     * Sets error attributes for each validation failure.
     * 
     * @param student The student object to validate
     * @param request The HTTP request to set error attributes
     * @return true if all validations pass, false otherwise
     */
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;
        
        // Validate student code
        String studentCode = student.getStudentCode();
        if (studentCode == null || studentCode.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else {
            // Pattern: 2 uppercase letters + 3 or more digits (e.g., SV001, IT123)
            String codePattern = "[A-Z]{2}[0-9]{3,}";
            if (!studentCode.matches(codePattern)) {
                request.setAttribute("errorCode", "Invalid format. Use 2 letters + 3+ digits (e.g., SV001)");
                isValid = false;
            }
        }
        
        // Validate full name
        String fullName = student.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            request.setAttribute("errorName", "Full name must be at least 2 characters");
            isValid = false;
        }
        
        // Validate email (only if provided)
        String email = student.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            // Simple email pattern: must contain @ and .
            String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }
        
        // Validate major
        String major = student.getMajor();
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Handle file upload and save to uploads directory
     * @param request HTTP request containing the file
     * @param fieldName Name of the file input field
     * @return Filename of saved file, or null if no file uploaded
     */
    private String handleFileUpload(HttpServletRequest request, String fieldName) {
        try {
            Part filePart = request.getPart(fieldName);
            
            if (filePart == null || filePart.getSize() == 0) {
                return null; // No file uploaded
            }
            
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            
            // Validate file type (only images)
            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return null; // Not an image
            }
            
            // Validate file extension
            String fileExtension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = fileName.substring(dotIndex);
            }
            
            String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
            boolean validExtension = false;
            for (String ext : allowedExtensions) {
                if (fileExtension.equalsIgnoreCase(ext)) {
                    validExtension = true;
                    break;
                }
            }
            
            if (!validExtension) {
                return null; // Invalid file extension
            }
            
            // Generate unique filename
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Create uploads directory if it doesn't exist
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Save file
            Path filePath = Paths.get(uploadPath, uniqueFileName);
            Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return uniqueFileName;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
