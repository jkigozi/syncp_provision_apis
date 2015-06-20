Since it modifies the Registry, you need administrator level privileges to runt this example.

0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 5 parameters, in this order only.
   
   targetServer domain username passwd keytocreate

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead.
   
   keytocreate is the name of the key which will be created using this example.


3. To run, make sure c:\temp (sorry it's hardcoded) is present.

	yourinstallation\examples\WinReg:\> Execute targetServer domain username passwd keytocreate
 
4. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.

5. You are done.