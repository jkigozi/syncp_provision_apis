0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 5 parameters, in this order only.
   
   targetServer domain username passwd progIdOfApplication

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead. progIdOfApplication is "PowerPoint.Application" , "InternetExplorer.Application" etc.


3. To run, goto

yourinstallation\examples\Misc:\> Execute targetServer domain username passwd progIdOfApplication
 
4. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.


5. You are done.


This example prints out the type library of COM server identified with progIdOfApplication.