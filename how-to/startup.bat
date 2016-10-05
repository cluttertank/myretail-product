@echo off
setLocal


set "SERVICE_NAME=%1"
set "SERVICE_PORT=%2"
set "JAVA_OPTS=%~3"

set "RUNTIME_HOME="
set "LOG_LOCATION=%RUNTIME_HOME%/tomcat/logs"
set "CONF_LOCATION=%RUNTIME_HOME%/tomcat/conf"

set "CATALINA_BASE=%RUNTIME_HOME%/tomcat/%SERVICE_NAME%"

set "JAVA_OPTS=%JAVA_OPTS% -Xms128M -Xmx256M -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -verbose:gc -Xloggc:%LOG_LOCATION%/%SERVICE_NAME%_gc.log"
set "JAVA_OPTS=%JAVA_OPTS% -Dserver.connector.port=%SERVICE_PORT% -Dlogback.configurationFile=%CATALINA_BASE%/conf/logback.xml"
set "JAVA_OPTS=%JAVA_OPTS% -Dservice.name=%SERVICE_NAME% -Dservice.instance.name=%SERVICE_NAME%_%SERVICE_PORT% -Dlog.location=%LOG_LOCATION%"

echo JAVA_OPTS = %JAVA_OPTS%

echo "Starting Service ->" %SERVICE_NAME%
call %CATALINA_HOME%\bin\startup.bat


