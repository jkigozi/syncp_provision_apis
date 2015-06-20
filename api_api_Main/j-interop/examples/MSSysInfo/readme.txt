0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 4 parameters, in this order only.
   
   targetServer domain username passwd

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead.


3. Since SysInfo is an Inproc server (OCX), you will need to read FAQ 6 of readme.htm in root folder of this installation.
  
   In this example "autoRegistration" is preconfigured in the example.

4. To run, goto

yourinstallation\examples\MSSysInfo:\> Execute targetServer domain username passwd
 
5. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.

6. You are done.


This example prints out the some information about your computer and has event callback on the 
date and power properties. You can change the system date and check.