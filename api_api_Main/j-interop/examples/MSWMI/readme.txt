0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 4 parameters, in this order only.
   
   targetServer domain username passwd

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead.


3. Since WMI is an Inproc server (DLL), you will need to read FAQ 6 of readme.htm in root folder of this installation.
  
   In this example "autoRegistration" is preconfigured in the example.

4. To run, goto

yourinstallation\examples\WSWMI:\> Execute targetServer domain username passwd
 
5. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.

6. If you get  "0x80041003 - Access Denied"

   This means WMI has rejected your connection request. This is not a j-Interop related problem. Make
   sure you have access to WMI on both the managing and the managed machines. On the WMI enabled 
   machines, open "Control Panel/Administrative Tools/Computer Management/", then click on  "Services 
   and Applications", then right-click on the "WMI Control", choose "Properties"; open the "Security" 
   pane, click on the "Security" Button, and add the necessary account. If you are still having 
   troubles, please consult WMI documentation at Platform SDK: Windows Management Instrumentation 
   (http://msdn.microsoft.com/library/en-us/wmisdk/wmi/wmi_start_page.asp)

7. You are done.


This example prints out the Win32 Process list.