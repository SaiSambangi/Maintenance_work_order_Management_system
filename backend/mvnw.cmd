@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script, version 3.2.0
@REM This script downloads Maven automatically if it's not already cached.
@REM You don't need Maven installed globally — just run this script.
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET ClassName=org.apache.maven.wrapper.MavenWrapperMain
@SET WrapperJar="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@IF EXIST %WrapperJar% (GOTO WrapperJarFound)
@SET WrapperJar="%~dp0.mvn\wrapper\maven-wrapper.jar"
:WrapperJarFound

@SET JAVA_HOME_RESOLVED=%JAVA_HOME%
@IF "%JAVA_HOME_RESOLVED%"=="" (
    @SET JAVA_HOME_RESOLVED=C:\Program Files\Java\jdk-17
)
@SET JAVA_CMD="%JAVA_HOME_RESOLVED%\bin\java.exe"

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF NOT "%MAVEN_PROJECTBASEDIR:~-1%"=="\" (SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%\)

@%JAVA_CMD% -classpath %WrapperJar% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% %ClassName% %MAVEN_CONFIG% %*
@IF ERRORLEVEL 1 GOTO error
@GOTO end

:error
@SET EXIT_CODE=%ERRORLEVEL%
@EXIT /B %EXIT_CODE%

:end
@EXIT /B 0
