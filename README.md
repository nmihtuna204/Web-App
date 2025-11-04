# Student Management System

A complete CRUD web application for managing student records using JSP, MySQL, and Tomcat.

---

## � HOW TO RUN THE PROJECT

### Step 1: Setup Database

1. **Make sure MySQL is running**
   - Default connection: `127.0.0.1:3306`
   - Username: `root`
   - Password: `Octiu123`

2. **Create database and table**
   - Open `student_management.sql` file
   - Execute the SQL script in your MySQL client (NetBeans, MySQL Workbench, or phpMyAdmin)
   - This will create:
     - Database: `student_management`
     - Table: `students` with 5 sample records

### Step 2: Build the Project

```bash
# Navigate to project directory
cd "C:\Users\Minh Tuan\Documents\NetBeansProjects\StudentManagement"

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-22"

# Build with Maven
mvn clean package
```

**Output:** `target/StudentManagement-1.0-SNAPSHOT.war`

### Step 3: Deploy to Tomcat

1. **Copy WAR file to Tomcat webapps:**
   ```bash
   Copy-Item "target\StudentManagement-1.0-SNAPSHOT.war" -Destination "C:\Users\Minh Tuan\Downloads\apache-tomcat-11.0.13\apache-tomcat-11.0.13\webapps\"
   ```

2. **Start Tomcat:**
   ```bash
   cd "C:\Users\Minh Tuan\Downloads\apache-tomcat-11.0.13\apache-tomcat-11.0.13\bin"
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-22"
   .\startup.bat
   ```

3. **Wait 5-10 seconds** for Tomcat to deploy the WAR file

### Step 4: Access the Application

**Open browser and navigate to:**
```
http://localhost:8080/StudentManagement-1.0-SNAPSHOT/list_student.jsp
```

---

## 📋 APPLICATION FLOW

### 1. **List Students Page** (Entry Point)
- **URL:** `/list_student.jsp`
- **Features:**
  - Displays all students in a table
  - Shows: ID, Student Code, Full Name, Email, Major, Created At
  - Action buttons: Add New Student, Edit, Delete
  - Success/error messages display at top

### 2. **Add Student Flow**

**Step 2.1:** Click "➕ Add New Student" button
- **Redirects to:** `add_student.jsp`

**Step 2.2:** Fill in the form
- **Required fields:**
  - Student Code (format: 2 uppercase letters + 3+ digits, e.g., SV001)
  - Full Name
- **Optional fields:**
  - Email
  - Major

**Step 2.3:** Click "💾 Save Student"
- **Submits to:** `process_add.jsp`
- **Validation:**
  - Client-side: HTML5 validation (required, pattern)
  - Server-side: Empty field check, duplicate student code check
- **Success:** Redirects to `list_student.jsp?message=Student added successfully`
- **Error:** Redirects to `add_student.jsp?error=...`

### 3. **Edit Student Flow**

**Step 3.1:** Click "✏️ Edit" on any student row
- **Redirects to:** `edit_student.jsp?id=X`

**Step 3.2:** Form pre-fills with current data
- Student Code field is readonly (cannot be changed)
- All other fields are editable

**Step 3.3:** Modify data and click "💾 Update"
- **Submits to:** `process_edit.jsp`
- **Validation:**
  - Full Name required
  - Valid student ID
- **Success:** Redirects to `list_student.jsp?message=Student updated successfully`
- **Error:** Redirects to `edit_student.jsp?id=X&error=...`

### 4. **Delete Student Flow**

**Step 4.1:** Click "🗑️ Delete" on any student row
- **JavaScript confirmation:** "Are you sure?"

**Step 4.2:** Confirm deletion
- **Executes:** `delete_student.jsp?id=X`
- **Action:** Deletes student from database
- **Success:** Redirects to `list_student.jsp?message=Student deleted successfully`
- **Error:** Redirects to `list_student.jsp?error=...`

---

## 🗂️ PROJECT STRUCTURE

```
StudentManagement/
├── src/main/webapp/
│   ├── list_student.jsp          # Main page - displays all students
│   ├── add_student.jsp            # Add student form
│   ├── process_add.jsp            # Processes add form submission
│   ├── edit_student.jsp           # Edit student form (pre-filled)
│   ├── process_edit.jsp           # Processes edit form submission
│   ├── delete_student.jsp         # Deletes student
│   └── META-INF/
│       └── context.xml            # Tomcat context configuration
├── pom.xml                        # Maven dependencies
├── student_management.sql         # Database schema + sample data
└── README.md                      # This file
```

---

## 🗄️ DATABASE SCHEMA

```sql
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_code VARCHAR(10) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    major VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Sample Data:** 5 students (SV001 through SV005)

---

## 🔄 COMPLETE USER JOURNEY

1. **User opens application** → `list_student.jsp`
2. **Sees table with 5 students** (from sample data)
3. **Clicks "Add New Student"** → `add_student.jsp`
4. **Fills form:** SV006, "Jane Doe", "jane@email.com", "Data Science"
5. **Clicks "Save Student"** → `process_add.jsp` → redirects to `list_student.jsp`
6. **Sees success message** and new student in table (now 6 students)
7. **Clicks "Edit" on Jane Doe** → `edit_student.jsp?id=6`
8. **Changes email** to "jane.doe@email.com"
9. **Clicks "Update"** → `process_edit.jsp` → redirects to `list_student.jsp`
10. **Sees updated information** in table
11. **Clicks "Delete" on Jane Doe** → JavaScript confirm appears
12. **Confirms deletion** → `delete_student.jsp?id=6` → redirects to `list_student.jsp`
13. **Sees "Student deleted successfully"** and table shows 5 students again

---

## ⚙️ TECHNICAL DETAILS

**Technology Stack:**
- **Backend:** JSP (JavaServer Pages)
- **Database:** MySQL 8.0
- **Server:** Apache Tomcat 11.0.13
- **Build:** Maven 3.x
- **Java:** JDK 22

**Database Connection:**
- Host: `127.0.0.1:3306`
- Database: `student_management`
- User: `root`
- Password: `Octiu123`

**Code Quality:**
- ✅ Uses PreparedStatement (prevents SQL injection)
- ✅ Proper resource cleanup (finally blocks)
- ✅ Client + Server validation
- ✅ User-friendly error messages

---

## 🛑 HOW TO STOP THE APPLICATION

```bash
cd "C:\Users\Minh Tuan\Downloads\apache-tomcat-11.0.13\apache-tomcat-11.0.13\bin"
.\shutdown.bat
```

---

## 📝 QUICK REFERENCE

**Main URL:**
```
http://localhost:8080/StudentManagement-1.0-SNAPSHOT/list_student.jsp
```

**All Pages:**
- List: `/list_student.jsp`
- Add: `/add_student.jsp`
- Edit: `/edit_student.jsp?id=X`
- Delete: `/delete_student.jsp?id=X`

**Build & Deploy:**
```powershell
# Build
mvn clean package

# Deploy
Copy-Item "target\StudentManagement-1.0-SNAPSHOT.war" -Destination "[TOMCAT]\webapps\"

# Start Tomcat
cd "[TOMCAT]\bin"
.\startup.bat
```

---

**That's it! Your application is now running. Happy coding! 🚀**
