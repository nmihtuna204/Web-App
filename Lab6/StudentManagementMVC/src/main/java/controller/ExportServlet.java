package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Servlet for exporting student data to Excel format
 */
@WebServlet("/export")
public class ExportServlet extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get all students from database
        List<Student> students = studentDAO.getAllStudents();
        
        // Create Excel workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        // Create data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Student Code", "Full Name", "Email", "Major", "Created At"};
        
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(student.getId());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(student.getStudentCode());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(student.getFullName());
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(student.getEmail() != null ? student.getEmail() : "N/A");
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(student.getMajor() != null ? student.getMajor() : "N/A");
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(student.getCreatedAt() != null ? dateFormat.format(student.getCreatedAt()) : "N/A");
            cell5.setCellStyle(dataStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Set response headers for file download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=students.xlsx");
        
        // Write workbook to response output stream
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            workbook.close();
        }
    }
}
