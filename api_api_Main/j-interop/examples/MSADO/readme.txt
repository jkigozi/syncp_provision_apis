Created with MSAccess 2003. 

0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 4 parameters, in this order only.
   
   targetServer domain username passwd

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead.


3. Copy the products.mdb to C:\\temp\\products.mdb (sorry, this is hardcoded).

4. To run, goto

yourinstallation\examples\MSADO:\> Execute targetServer domain username passwd
 
5. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.

6. You are done.



Please note that whenever you work with j-Interop, it's j-interop.jar should precede all the jars in the lib folder.
