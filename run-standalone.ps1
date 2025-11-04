# Script to run the application standalone using embedded Tomcat
# Note: This requires adding tomcat-embed dependencies to pom.xml

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Student Management System - Standalone Runner" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$warFile = ".\target\StudentManagement-1.0-SNAPSHOT.war"

if (!(Test-Path $warFile)) {
    Write-Host "ERROR: WAR file not found!" -ForegroundColor Red
    Write-Host "Please run 'mvn clean install' first`n" -ForegroundColor Yellow
    exit 1
}

Write-Host "WAR file found: $warFile" -ForegroundColor Green
Write-Host "`nTo deploy this application:" -ForegroundColor Yellow
Write-Host "1. Copy the WAR file to your Tomcat webapps folder" -ForegroundColor White
Write-Host "2. Start Tomcat" -ForegroundColor White
Write-Host "3. Access: http://localhost:8080/StudentManagement-1.0-SNAPSHOT/list_student.jsp`n" -ForegroundColor White

Write-Host "Or in NetBeans:" -ForegroundColor Yellow
Write-Host "1. Go to Services tab (Ctrl+5)" -ForegroundColor White
Write-Host "2. Right-click 'Servers' -> Add Server" -ForegroundColor White  
Write-Host "3. Add your Tomcat installation" -ForegroundColor White
Write-Host "4. Right-click project -> Run`n" -ForegroundColor White

# Ask if user wants to open browser
Write-Host "Press any key to open browser to localhost:8080..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
Start-Process "http://localhost:8080/StudentManagement-1.0-SNAPSHOT/list_student.jsp"
