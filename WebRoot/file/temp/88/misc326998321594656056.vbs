Set objWMIService = GetObject("winmgmts:\\.\root\cimv2")
Set colItems = objWMIService.ExecQuery _ 
   ("Select * from Win32_BaseBoard") 
For Each objItem in colItems 
    Wscript.Echo objItem.SerialNumber 
    exit for  ' do the first cpu only! 
Next 
