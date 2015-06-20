I haven't got around to producing an IDL for the Java class yet (that is for the next version). 
But if you have an existing COM server (it's type library or IDL) , you can "replace" the interface pointer for that COM server 
with your Java implementation. That is:- 
 
a. You would implement the IDL\TypeLibrary in Java as Java class. 
 
b. Where ever the IUnknown* of that component is being moved around, you can send the interface pointer 
of your Java implementation instead. This can be done via JIInterfacePointer.getInterfacePointer(...). 


***********************************************************************************************************************************************


0. Please make sure that "Server" service is running, else goto Service Manager and start it. (Start->Run->Services.msc).

1. Then, change directory to your installation location and the sub directory of the example.

2. Execute.bat needs valid values for these 4 parameters, in this order only.
   
   targetServer domain username passwd

   First is the Server host (localhost for your local machine) , then your domain (again localhost for your local machine),username,password. 
   You can even pass IP Addresses instead.


3. To run, first build the TestJavaServer project. 

yourinstallation\examples\COMCallingJava:\> Execute targetServer domain username passwd
 
4. If you get any ACCESS_DENIED exceptions, please go through the main "readme.htm" at the root folder of the installation.


5. You are done.


This example, upon execution, you should see a message box from the Windows COM server and then you should see the same text
as was in the message box printed on the console.

For users using a Network login:- In this example, if the message box comes and then the application hangs, please use local authentication, it is highly likely that
you are on a machine presently not on the network and the COM server is trying to authenticate you to a Domain controller which it can't connect
to.