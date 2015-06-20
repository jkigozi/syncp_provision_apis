@echo off

%JAVA_HOME%\bin\java -classpath ../../lib;../../lib/j-interopdeps.jar;../../lib/jcifs-1.2.19.jar;../../lib/j-interop.jar org.jinterop.dcom.test.MSSysInfo %1 %2 %3 %4
