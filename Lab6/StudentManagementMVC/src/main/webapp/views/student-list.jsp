<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h1 { color: #333; }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-bottom: 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
        }
        th {
            background-color: #007bff;
            color: white;
            padding: 12px;
            text-align: left;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        tr:hover { background-color: #f8f9fa; }
        .action-link {
            color: #007bff;
            text-decoration: none;
            margin-right: 10px;
        }
        .delete-link { color: #dc3545; }
        .filter-box {
            background-color: white;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            display: inline-block;
        }
        .filter-box label {
            margin-right: 10px;
            font-weight: bold;
        }
        .filter-box select {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-right: 10px;
        }
        th a {
            color: white;
            text-decoration: none;
        }
        th a:hover {
            text-decoration: underline;
        }
        .sort-indicator {
            font-size: 12px;
        }
        .pagination {
            margin: 20px 0;
            text-align: center;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
        }
        .pagination a {
            padding: 8px 12px;
            margin: 0 4px;
            border: 1px solid #ddd;
            text-decoration: none;
            color: #007bff;
            border-radius: 3px;
            display: inline-block;
        }
        .pagination a:hover {
            background-color: #007bff;
            color: white;
        }
        .pagination strong {
            padding: 8px 12px;
            margin: 0 4px;
            background-color: #007bff;
            color: white;
            border: 1px solid #007bff;
            border-radius: 3px;
            display: inline-block;
        }
        .pagination .disabled {
            padding: 8px 12px;
            margin: 0 4px;
            border: 1px solid #ddd;
            color: #6c757d;
            cursor: not-allowed;
            border-radius: 3px;
            display: inline-block;
        }
        .page-info {
            text-align: center;
            color: #666;
            margin: 10px 0;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <h1>📚 Student Management System (MVC)</h1>
    
    <c:if test="${not empty param.message}">
        <div class="message success">
            ${param.message}
        </div>
    </c:if>
    
    <c:if test="${not empty param.error}">
        <div class="message error">
            ${param.error}
        </div>
    </c:if>
    
    <a href="student?action=new" class="btn">➕ Add New Student</a>
    <a href="export" class="btn" style="background-color: #28a745;">📥 Export to Excel</a>
    
    <!-- Filter by Major -->
    <div class="filter-box">
        <form action="student" method="get" style="margin: 0;">
            <input type="hidden" name="action" value="filter">
            <label>🎓 Filter by Major:</label>
            <select name="major">
                <option value="">All Majors</option>
                <option value="Computer Science" ${selectedMajor == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                <option value="Information Technology" ${selectedMajor == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                <option value="Software Engineering" ${selectedMajor == 'Software Engineering' ? 'selected' : ''}>Software Engineering</option>
                <option value="Business Administration" ${selectedMajor == 'Business Administration' ? 'selected' : ''}>Business Administration</option>
                <option value="Data Science" ${selectedMajor == 'Data Science' ? 'selected' : ''}>Data Science</option>
            </select>
            <button type="submit" class="btn" style="margin: 0;">Apply Filter</button>
            <c:if test="${not empty selectedMajor}">
                <a href="student?action=list" class="btn" style="background-color: #6c757d; margin-left: 5px;">Clear Filter</a>
            </c:if>
        </form>
    </div>
    
    <!-- Search Form -->
    <form action="student" method="GET" style="margin-bottom: 20px; display: inline-block;">
        <input type="hidden" name="action" value="search">
        <input type="text" name="keyword" placeholder="🔍 Search by name or student code..." 
               value="${keyword}" style="padding: 10px; width: 350px; border: 1px solid #ddd; border-radius: 5px;">
        <button type="submit" class="btn" style="margin: 0;">Search</button>
        <c:if test="${not empty keyword}">
            <a href="student?action=list" class="btn" style="background-color: #6c757d; margin-left: 5px;">Clear</a>
        </c:if>
    </form>
    
    <c:if test="${not empty keyword}">
        <p style="color: #666; font-style: italic;">Search results for: "<strong>${keyword}</strong>"</p>
    </c:if>
    
    <c:if test="${not empty selectedMajor}">
        <p style="color: #666; font-style: italic;">Showing students in: <strong>${selectedMajor}</strong></p>
    </c:if>
    
    <table>
        <thead>
            <tr>
                <th>
                    <a href="student?action=sort&sortBy=id&order=${sortBy == 'id' && order == 'asc' ? 'desc' : 'asc'}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">
                        ID
                        <c:if test="${sortBy == 'id'}">
                            <span class="sort-indicator">${order == 'asc' ? '▲' : '▼'}</span>
                        </c:if>
                    </a>
                </th>
                <th>
                    <a href="student?action=sort&sortBy=student_code&order=${sortBy == 'student_code' && order == 'asc' ? 'desc' : 'asc'}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">
                        Student Code
                        <c:if test="${sortBy == 'student_code'}">
                            <span class="sort-indicator">${order == 'asc' ? '▲' : '▼'}</span>
                        </c:if>
                    </a>
                </th>
                <th>
                    <a href="student?action=sort&sortBy=full_name&order=${sortBy == 'full_name' && order == 'asc' ? 'desc' : 'asc'}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">
                        Full Name
                        <c:if test="${sortBy == 'full_name'}">
                            <span class="sort-indicator">${order == 'asc' ? '▲' : '▼'}</span>
                        </c:if>
                    </a>
                </th>
                <th>
                    <a href="student?action=sort&sortBy=email&order=${sortBy == 'email' && order == 'asc' ? 'desc' : 'asc'}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">
                        Email
                        <c:if test="${sortBy == 'email'}">
                            <span class="sort-indicator">${order == 'asc' ? '▲' : '▼'}</span>
                        </c:if>
                    </a>
                </th>
                <th>
                    <a href="student?action=sort&sortBy=major&order=${sortBy == 'major' && order == 'asc' ? 'desc' : 'asc'}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">
                        Major
                        <c:if test="${sortBy == 'major'}">
                            <span class="sort-indicator">${order == 'asc' ? '▲' : '▼'}</span>
                        </c:if>
                    </a>
                </th>
                <th>Photo</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="student" items="${students}">
                <tr>
                    <td>${student.id}</td>
                    <td>${student.studentCode}</td>
                    <td>${student.fullName}</td>
                    <td>${student.email != null ? student.email : 'N/A'}</td>
                    <td>${student.major != null ? student.major : 'N/A'}</td>
                    <td style="text-align: center;">
                        <c:choose>
                            <c:when test="${not empty student.photo}">
                                <img src="${pageContext.request.contextPath}/uploads/${student.photo}" 
                                     alt="${student.fullName}" 
                                     style="width: 50px; height: 50px; object-fit: cover; border-radius: 5px; border: 1px solid #ddd;">
                            </c:when>
                            <c:otherwise>
                                <span style="color: #999;">👤</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="student?action=edit&id=${student.id}" class="action-link">✏️ Edit</a>
                        <a href="student?action=delete&id=${student.id}" 
                           class="action-link delete-link"
                           onclick="return confirm('Are you sure?')">🗑️ Delete</a>
                    </td>
                </tr>
            </c:forEach>
            
            <c:if test="${empty students}">
                <tr>
                    <td colspan="7" style="text-align: center;">
                        No students found.
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
    
    <!-- Pagination Controls -->
    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <!-- Previous button -->
            <c:choose>
                <c:when test="${currentPage > 1}">
                    <a href="student?action=${not empty selectedMajor ? 'filter' : 'list'}&page=${currentPage - 1}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">« Previous</a>
                </c:when>
                <c:otherwise>
                    <span class="disabled">« Previous</span>
                </c:otherwise>
            </c:choose>
            
            <!-- First page -->
            <c:if test="${currentPage > 3}">
                <a href="student?action=${not empty selectedMajor ? 'filter' : 'list'}&page=1${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">1</a>
                <c:if test="${currentPage > 4}">
                    <span>...</span>
                </c:if>
            </c:if>
            
            <!-- Page numbers around current page -->
            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <strong>${i}</strong>
                        </c:when>
                        <c:otherwise>
                            <a href="student?action=${not empty selectedMajor ? 'filter' : 'list'}&page=${i}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:forEach>
            
            <!-- Last page -->
            <c:if test="${currentPage < totalPages - 2}">
                <c:if test="${currentPage < totalPages - 3}">
                    <span>...</span>
                </c:if>
                <a href="student?action=${not empty selectedMajor ? 'filter' : 'list'}&page=${totalPages}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">${totalPages}</a>
            </c:if>
            
            <!-- Next button -->
            <c:choose>
                <c:when test="${currentPage < totalPages}">
                    <a href="student?action=${not empty selectedMajor ? 'filter' : 'list'}&page=${currentPage + 1}${not empty selectedMajor ? '&major='.concat(selectedMajor) : ''}">Next »</a>
                </c:when>
                <c:otherwise>
                    <span class="disabled">Next »</span>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Page info -->
        <div class="page-info">
            <c:set var="startRecord" value="${(currentPage - 1) * 10 + 1}" />
            <c:set var="endRecord" value="${currentPage * 10}" />
            <c:if test="${endRecord > totalRecords}">
                <c:set var="endRecord" value="${totalRecords}" />
            </c:if>
            Showing ${startRecord} - ${endRecord} of ${totalRecords} records | Page ${currentPage} of ${totalPages}
        </div>
    </c:if>
    
    <!-- Display total when no pagination -->
    <c:if test="${totalPages <= 1 && totalRecords > 0}">
        <div class="page-info">
            Total: ${totalRecords} record${totalRecords > 1 ? 's' : ''}
        </div>
    </c:if>
</body>
</html>
