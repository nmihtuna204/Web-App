cd "C:\Users\Minh Tuan\Documents\NetBeansProjects\StudentManagementMVC"; & "C:\Program Files\NetBeans-12.5\netbeans\java\maven\bin\mvn.cmd" clean package -DskipTests

cd "C:\Users\Minh Tuan\Documents\NetBeansProjects\StudentManagementMVC"; & "C:\Program Files\NetBeans-12.5\netbeans\java\maven\bin\mvn.cmd" clean package -DskipTests -q

cd "C:\Users\Minh Tuan\Downloads\apache-tomcat-11.0.13\apache-tomcat-11.0.13\bin"; $env:JAVA_HOME='C:\Program Files\Java\jdk-22'; .\shutdown.bat; Start-Sleep -Seconds 2; Remove-Item -Recurse -Force "..\webapps\StudentManagementMVC" -ErrorAction SilentlyContinue; Remove-Item -Force "..\webapps\StudentManagementMVC.war" -ErrorAction SilentlyContinue; Copy-Item "C:\Users\Minh Tuan\Documents\NetBeansProjects\StudentManagementMVC\target\StudentManagementMVC-1.0-SNAPSHOT.war" -Destination "..\webapps\StudentManagementMVC.war" -Force; .\startup.bat

Start-Sleep -Seconds 5; try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/StudentManagementMVC/student' -UseBasicParsing -TimeoutSec 10; Write-Output "✅ Search feature deployed! Status: $($r.StatusCode)"; } catch { Write-Output "⏳ Waiting for deployment... $($_.Exception.Message)" }

http://localhost:8080/StudentManagementMVC/student