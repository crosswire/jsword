
var x = new ActiveXObject("MSXML2.DOMDocument.4.0");

x.async = false;
x.validateOnParse = true;
x.load("kjv.xml");

if (x.parseError.errorCode != 0)
{
    WScript.Echo(
        "Error at line " + x.parseError.line
        // + ", char " + x.parseError.lineChar
         + "\n" + x.parseError.reason
        // + "\n" + x.parseError.src
        );
}
else
{
    WScript.Echo("No Errors");
}
