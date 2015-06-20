@echo off

%JAVA_HOME%\bin\java -classpath ../../lib;../../lib/j-interopdeps.jar;../../lib/jcifs-1.2.19.jar;../../lib/j-interop.jar -Djcifs.encoding=Cp850 org.jinterop.dcom.test.MSInternetExplorer %1 %2 %3 %4
