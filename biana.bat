
if "%2"=="" goto :not_defined

:defined
"%~dp0\ext\Python2.5\python.exe" %* 
goto :end

:not_defined
cd "%~dp0\ext\mysql\bin\"
start mysqld.exe --verbose --datadir="%appdata%\..\BIANA"

echo not_defined
"%~dp0\ext\Python2.5\python.exe" -i

:end


