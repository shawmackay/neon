<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:import  href="sub-rendering.xsl"/>

<xsl:output method="html" indent="yes"/>



<xsl:template match="/">	
<html>
	<head>
		<title>SimpleWeb Output</title>
	<link rel="stylesheet" type="text/css" href="/neon/neon.css"/>
	</head>
	<body>	
		<img src="/images/neonemboss_small_blue.jpg"/>
		<div class="maintitle"><center>SimpleWeb</center></div>
		<div class="mainbody">		
			<xsl:apply-templates/>
 
		
			<div class="neonback3"><a href="#" onClick="history.back()">Back</a></div>
			</div>
		</body>
		</html>
</xsl:template>
 

<xsl:template match="entries">
<table>
<xsl:apply-templates/>
</table>
</xsl:template>


<xsl:template match="entry">
	<tr>
	<td><xsl:value-of select="@name"/></td>
	<td><xsl:value-of select="@value"/></td>	
	</tr>
</xsl:template>

<xsl:template match="parameters">
<table>
	<tr><th>Parameter</th><th>Value</th></tr>
<xsl:apply-templates/>
</table>
</xsl:template>



		
</xsl:stylesheet>	