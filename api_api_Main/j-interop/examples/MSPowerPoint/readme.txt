Created with MSPowerPoint 2003

0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 4 parameters, in this order only.
   
   targetServer domain username passwd

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead.


3. To run, make sure c:\temp (sorry it's hardcoded) is present.

	yourinstallation\examples\MSPowerPoint:\> Execute targetServer domain username passwd
 
4. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.

5. If you see a MS PowerPoint window on the target server...don't read any further....goto 6.

okay ! so you don't see any window of Power Point, don't panic...it's just a matter of DCOM settings...On the target server... type "DCOMCNFG" on run.

	.click on "Component Services" and keep clicking till you reach list showing "DCOM config".
	.Open this folder and select "Microsoft Power Point Application".
	.Do a right click and select properties.
	.A dialog will open showing you all the excel stuff...just go to the last tab , saying "Identity". 
	.Select the "The Interactive User".
	.Press on "Okay". 
	.And retry the example.


6. You are done.