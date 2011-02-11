
<html>
	<head>
		<title>Client Information screen</title>
	

		<link rel="stylesheet" type="text/css" href="neon.css" >

	</head>
	
		
	

<script LANGUAGE="JavaScript">

<!-- begin

function display() {

window.onerror=null;

colors = window.screen.colorDepth;

document.form.color.value = Math.pow (2, colors);

if (window.screen.fontSmoothingEnabled == true)

document.form.fonts.value = "Yes";

else document.form.fonts.value = "No";

document.form.colordepth.value = window.screen.colorDepth;

document.form.width.value = window.screen.width;

document.form.height.value = window.screen.height;

document.form.maxwidth.value = window.screen.availWidth;

document.form.maxheight.value = window.screen.availHeight;

if (navigator.javaEnabled() < 1) document.form.java.value="No";

if (navigator.javaEnabled() == 1) document.form.java.value="Yes";

if(navigator.javaEnabled() && (navigator.appName != "Microsoft Internet Explorer")) {

vartool=java.awt.Toolkit.getDefaultToolkit();

addr=java.net.InetAddress.getLocalHost();

host=addr.getHostName();

ip=addr.getHostAddress();

document.form.hostip.value = "Your host name is '" + host + "'\nYour IP address is " + ip; 

   }

}

// end -->

</script>
<script LANGUAGE="JavaScript1.0">

<!-- Begin

jsver = "1.0";

// End -->

</script>
<script LANGUAGE="JavaScript1.1">

<!-- Begin

jsver = "1.1";

// End -->

</script>
<script Language="JavaScript1.2">

<!-- Begin

jsver = "1.2";

// End -->

</script>
<script Language="JavaScript1.3">

<!-- Begin

jsver = "1.3";

// End -->

</script>
<script LANGUAGE="JavaScript">

<!-- Begin

var timerID = null;

var timerRunning = false;

function stopclock (){

if(timerRunning)

clearTimeout(timerID);

timerRunning = false;

}

function showtime () {

var now = new Date();

var hours = now.getHours();

var minutes = now.getMinutes();

var seconds = now.getSeconds()

var timeValue = "" + ((hours >12) ? hours -12 :hours)

if (timeValue == "0") timeValue = 12;

timeValue += ((minutes < 10) ? ":0" : ":") + minutes

timeValue += ((seconds < 10) ? ":0" : ":") + seconds

timeValue += (hours >= 12) ? " P.M." : " A.M."

document.clock.face.value = timeValue;

timerID = setTimeout("showtime()",1000);

timerRunning = true;

}

function startclock() {

stopclock();

showtime();

}

// End -->

</script>


<body OnLoad="display(),startclock()">

<%@ include file="header.html"%>

<!-- -->
<center><table BORDER=0 BGCOLOR="#FFFFFF" cellspacing="0" cellpadding="5">
<tr>
<th COLSPAN="3" BGCOLOR="#D2E6F8">
<center><b><font face="Arial,Helvetica"><font size=+1>Total System Info</font></font></b></center>
</th>
</tr>

<tr>
<td class="leftcol" WIDTH="270" ><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Current
Date and Time:</font></font></font></td>

<td WIDTH="450" class="embedded"><script LANGUAGE="JavaScript">

//original found at javascript source
//modified and updated by www.a1javascripts.com

<!-- Begin

var months=new Array(13);

months[1]="January";

months[2]="February";

months[3]="March";

months[4]="April";

months[5]="May";

months[6]="June";

months[7]="July";

months[8]="August";

months[9]="September";

months[10]="October";

months[11]="November";

months[12]="December";

var time=new Date();

var lmonth=months[time.getMonth() + 1];

var date=time.getDate();

var year=time.getYear();



// Y2K Fix by Isaac Powell

// http://onyx.idbsu.edu/~ipowell



if ((navigator.appName == "Microsoft Internet Explorer") && (year < 2000))		

year="19" + year;

if (navigator.appName == "Netscape")

year=1900 + year;

document.write(""+ lmonth + " ");

document.write(date + ", " + year);

// End -->

</script>
<form name="clock"><input type="text" name="face" size=13 value=""></form></td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Your
CPU Class is:</font></font></font></td>

<td class="embedded"><script>

document.write(navigator.cpuClass)

</script>
</td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Your
Operating System is:</font></font></font></td>

<td class="embedded"><script LANGUAGE="JavaScript">

<!--

function checkOS() {

  if(navigator.userAgent.indexOf('IRIX') != -1)

    { var OpSys = "Irix"; }

  else if((navigator.userAgent.indexOf('Win') != -1) &&

  (navigator.userAgent.indexOf('98') != -1))

    { var OpSys = "Windows 98"; }

  else if((navigator.userAgent.indexOf('Win') != -1) &&

  (navigator.userAgent.indexOf('95') != -1))

    { var OpSys = "Windows 95"; }

  else if(navigator.appVersion.indexOf("16") !=-1)

    { var OpSys = "Windows 3.1"; }

  else if (navigator.appVersion.indexOf("NT") !=-1)

    { var OpSys= "Windows NT"; }  

else if(navigator.appVersion.indexOf("SunOS") !=-1)

    { var OpSys = "SunOS"; }

  else if(navigator.appVersion.indexOf("Linux") !=-1)

    { var OpSys = "Linux"; }  

  else if(navigator.userAgent.indexOf('Mac') != -1)

    { var OpSys = "Macintosh"; }

  else if(navigator.appName=="WebTV Internet Terminal")

    { var OpSys="WebTV"; }

  else if(navigator.appVersion.indexOf("HP") !=-1)

    { var OpSys="HP-UX"; } 

else { var OpSys = "other"; }

  return OpSys;

}

// -->

</script>
<script LANGUAGE="JavaScript">

<!--

var OpSys = checkOS();

document.write(OpSys);

// -->

</script>
</td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>You
are currently using:</font></font></font></td>

<td class="embedded"><script Language="JavaScript">



        <!-- This is a combination of two scripts one written by Murat Muradoglu, wizkid10@hotmail.com.  I Jeff Bouton, yank2451@aol.com modified the script so that it will detect the AOL Browser as well as the new versions of Internet Explorer 5 and Netscape 4.61. further improved and undated by a1javascripts.com

        var version = 0;

                if(navigator.userAgent.indexOf('AOL 5') !=-1)  document.write("America Online 5.0");

                else if

                (navigator.userAgent.indexOf('AOL 4') != -1)  document.write("America Online 4.0");

		else if 

		(navigator.userAgent.indexOf('AOL 3') != -1)  document.write("America Online 3.0");

		else if 

		(navigator.userAgent.indexOf('MSIE 5.5') != -1) document.write("Internet Explorer 5.5");

                else if 

		(navigator.userAgent.indexOf('MSIE 5') != -1) document.write("Internet Explorer 5.0");    

		else if 

		(navigator.userAgent.indexOf('MSIE 4') != -1)  document.write("Internet Explorer 4.0");

                else if

                (navigator.userAgent.indexOf('MSIE 3') != -1)  document.write("Internet Explorer 3.0!");

                else if

                (navigator.userAgent.indexOf("Mozilla/4.73") != -1)  document.write("Netscape 4.73");

                else if

                (navigator.userAgent.indexOf("Mozilla/4.7") != -1)  document.write("Netscape 4.7");

                else if

                (navigator.userAgent.indexOf("Mozilla/4.61") != -1)  document.write("Netscape 4.61");

		else if

		(navigator.userAgent.indexOf("Mozilla/4.5") != -1)  document.write("Netscape 4.5")

                else if

                (navigator.userAgent.indexOf("Mozilla/4") != -1)  document.write("Netscape 4.0");

                else if

                (navigator.userAgent.indexOf("Mozilla/3") != -1)  document.write("Netscape 3.0");

                else if

                (navigator.userAgent.indexOf("Mozilla/2") != -1)  document.write("Netscape 2");

                else if

                (navigator.userAgent.indexOf("MSIE 4.5") != -1)  document.write("Microsoft Internet Explorer 4.5 for Macintosh"); 

		else if

		(navigator.appName=="WebTV Internet Terminal") document.write("WebTV Browser");		

		else if 

		(navigator.appName != "Microsoft Internet Explorer" && navigator.appName != "Netscape") document.write("You are not running IE, Netscape, AOL, or WebTV");                

		else version = 8;

        // -->

</script>
</td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>The
Application code of your browser is:</font></font></font></td>

<td class="embedded"><script>

document.write(navigator.appCodeName)

</script>
<script>

<!--

         var version = 0;

                 if

                (navigator.userAgent.indexOf("Mozilla/4.73") != -1)  document.write("4.73");

                else if

                (navigator.userAgent.indexOf("Mozilla/4.72") != -1)  document.write("4.72");

                else if

                (navigator.userAgent.indexOf("Mozilla/4.7") != -1)  document.write("4.7");

                else if

                (navigator.userAgent.indexOf("Mozilla/4.61") != -1)  document.write("4.61");

		else if

		(navigator.userAgent.indexOf("Mozilla/4.5") != -1)  document.write("4.5")

                else if

                (navigator.userAgent.indexOf("Mozilla/4") != -1)  document.write("4.0");

                else if

                (navigator.userAgent.indexOf("Mozilla/3") != -1)  document.write("3.0");

                else if

                (navigator.userAgent.indexOf("Mozilla/2") != -1)  document.write("2.0");

                else document.write("version undetermined");

				

        // -->

</script>
</td>
</tr>

<tr>
<td class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>The
Language of Your Browser is:</font></font></font></td>

<td class="embedded"> <script language="javascript">document.write(navigator.browserLanguage)</script>
</td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>You
arrived at this page from URL:</font></font></font></td>

<td class="embedded"><script Language="JavaScript">

<!--

document.write(""+ document.referrer +"" )

//-->

</script>
</td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Your
Javascript Version:</font></font></font></td>

<td class="embedded"><script LANGUAGE="JavaScript">

<!-- Begin

document.write("" + jsver + "")

// End -->

</script>

<center><form name=form></center>
</td>
</tr>

<tr>
<td  class="leftcol"><font face="Arial,Helvetica"><font color="#FFFFFF"><font size=-1>Hmm,
Cookies?</font></font></font></td>

<td class="embedded"><script Language="JavaScript">

<!-- hide script from non compliant broswers

var oneDay= 1*24*60*60*1000;

var expDate = new Date();

expDate.setTime (expDate.getTime() + oneDay);

var cookieExpires = expDate.toGMTString();

document.cookie="verifyCookie=test; expires="+cookieExpires

if (document.cookie.length>0)

document.write("Your browser supports cookies.");

else {

document.write("Your browser doesn't support cookies, ")

document.write("or they're currently disabled.");

document.write(document.cookie.substring(0,document.cookie.length)+"<BR><BR>");

}

document.cookie="verifyCookie=CLEAR; expires=Sun, 09-Nov-97 01:00:00 GMT";

//-->

</script>
</td>

</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Java
Enabled:</font></font></font></td>

<td class="embedded"><input type=text size=3 maxlength=3 name=java></td>
</tr>

<tr>
<td  class="leftcol"><font face="Arial,Helvetica"><font color="#FFFFFF"><font size=-1>You
Have Been Here:</font></font></font></td>

<td class="embedded"><script language="Javascript">
<!--

function getCookieVal (offset) {
  var endstr = document.cookie.indexOf (";", offset);
  if (endstr == -1)
    endstr = document.cookie.length;
  return unescape(document.cookie.substring(offset, endstr));
}

function GetCookie (name) {
  var arg = name + "=";
  var alen = arg.length;
  var clen = document.cookie.length;
  var i = 0;
  while (i < clen) {
    var j = i + alen;
    if (document.cookie.substring(i, j) == arg)
      return getCookieVal (j);
    i = document.cookie.indexOf(" ", i) + 1;
    if (i == 0) 
      break;
  }
  return null;
}

function SetCookie (name, value) {
  var argv = SetCookie.arguments;
  var argc = SetCookie.arguments.length;
  var expires = (argc > 2) ? argv[2] : null;
  var path = (argc > 3) ? argv[3] : null;
  var domain = (argc > 4) ? argv[4] : null;
  var secure = (argc > 5) ? argv[5] : false;
  document.cookie = name + "=" + escape (value) +
    ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
    ((path == null) ? "" : ("; path=" + path)) +
    ((domain == null) ? "" : ("; domain=" + domain)) +
    ((secure == true) ? "; secure" : "");
}

function DeleteCookie(name) {
  var exp = new Date();
  FixCookieDate (exp); // Correct for Mac bug
  exp.setTime (exp.getTime() - 1);  // This cookie is history
  var cval = GetCookie (name);
  if (cval != null)
    document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString();
}

var expdate = new Date();
var num_visits;
expdate.setTime(expdate.getTime() + (5*24*60*60*1000));
if (!(num_visits = GetCookie("num_visits")))
  num_visits = 0;
num_visits++;
SetCookie("num_visits",num_visits,expdate);
//-->
</script>
<script language="Javascript">
<!--

document.write(num_visits+" times");
//-->

</script>
</td>


</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Host/IP:</font></font></font></td>

<td class="embedded"><input type=text size=60 maxlength=100 name=hostip></td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Current
Resolution:</font></font></font></td>

<td class="embedded"><input type=text size=4 maxlength=4 name=width>x&nbsp;<input type=text size=4 maxlength=4 name=height></td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Max
Resolution:</font></font></font></td>

<td class="embedded"><input type=text size=4 maxlength=4 name=maxwidth>x&nbsp;<input type=text size=4 maxlength=4 name=maxheight></td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Color
Depth:</font></font></font></td>

<td class="embedded"><input type=text size=2 maxlength=2 name=colordepth>bit</td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Number
of Colors:</font></font></font></td>

<td class="embedded"><input type=text size=8 maxlength=8 name=color></td>
</tr>

<tr>
<td  class="leftcol"><font face="Helvetica"><font color="#FFFFFF"><font size=-1>Anti-aliasing
fonts:</font></font></font></td>

<td class="embedded"><input type=text size=3 maxlength=3 name=fonts></td>

</tr>

<tr>
<td class="leftcol" VALIGN=TOP  ><font face="Arial,Helvetica"><font color="#FFFFFF"><font size=-1>You
Have The Following Plugins Installed:</font></font></font></td>

<td class="embedded"><script language="JavaScript">
<!--
if(navigator.plugins.length > 1){
for(var i=0; i < navigator.plugins.length; i++){
document.write( "<li>" +navigator.plugins[i].name + "\n")
}
document.write("</ul>")
}
//-->
</script></td>


</tr>


</table></center>
<!-- -->
	</body>
</html>
