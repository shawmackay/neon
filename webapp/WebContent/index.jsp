<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/xhtml; charset=iso-8859-1"/>
<link rel="stylesheet" type="text/css" href="neon.css" />
<script language="JavaScript" type="text/JavaScript">
<!--
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
</head>
	<c:set var="pagetheme" value="${param.theme}"/>

<c:if test="${ empty param.theme}">
	<c:set var="pagetheme" value="green"/>
</c:if>



<body bgcolor="black" text="white" onLoad="MM_preloadImages('images/collab_${pagetheme}_high_small.jpg','images/host_${pagetheme}_high_small.jpg','images/slice_${pagetheme}_high_small.jpg','images/transfer_${pagetheme}_high_small.jpg','images/state_${pagetheme}_high_small.jpg')">
<p align="center">
<div style="background:black;">
<center>
<img src="images/neonemboss${pagetheme}.jpg" width="400" height="200"/></center></div></p>


	<div>
  <table bgcolor="black" border="0" cellspacing="0" width="100%">
    <tr bgcolor="black"><td bgcolor="black"><a href="hostlist.jsp" onMouseOut="MM_swapImgRestore()" onmouseover="MM_swapImage('collab','','images/collab_${pagetheme}_high_small.jpg',1)"><img src="images/collab_${pagetheme}_low_small.jpg" name="collab" width="80" height="85" border="0"></a>
    </td>
    <td><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('host','','images/host_${pagetheme}_high_small.jpg',1)"><img src="images/host_${pagetheme}_low_small.jpg" name="host" width="66" height="85" border="0"></a>
    </td>
    <td><a href="Slice.do?action=viewapps" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('slices','','images/slice_${pagetheme}_high_small.jpg',1)"><img src="images/slice_${pagetheme}_low_small.jpg" name="slices" width="85" height="85" border="0"></a>
    </td>
    <td><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('transfer','','images/transfer_${pagetheme}_high_small.jpg',1)"><img src="images/transfer_${pagetheme}_low_small.jpg" name="transfer" width="252" height="85" border="0"></a>
    </td>
    <td><a href="#" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('state','','images/state_${pagetheme}_high_small.jpg',1)"><img src="images/state_${pagetheme}_low_small.jpg" name="state" width="140" height="85" border="0"></a> </td>
    </tr><tr bgcolor="black">
    <td><div align="center">Agents</div></td><td><div align="center">Host</div></td><td><div align="center">Slices</div></td><td><div align="center">Transfer</div></td><td><div align="center">State</div></td></tr>
  </table>
  </div>

</body>
</html>
