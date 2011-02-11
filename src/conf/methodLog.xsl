<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="html" indent="yes"/>
<xsl:template match="/">	
	<body>
<!--		<div class="mainbody">-->
			<xsl:apply-templates/>
			<div class="neonback3"><a href="index.jsp">Back</a></div>
		<!--</div>-->
</body>
</xsl:template>
 

<xsl:template match="log">
	<h1>Method Log:</h1>
	<center>
		<table rules="rows" cellpadding="5" cellspacing="1">
			<tr>
				<th >
					Name
				</th>
				<th>
					Number of Invocations
				</th>
			</tr>
			<xsl:apply-templates/>
	</table>
	<br/>
	<xsl:apply-templates  select="facets"/>	
</center>

</xsl:template>

<xsl:template name="invoke" match="invoke">	
			<tr >
				<td>
					<xsl:value-of select="methodname"/>
				</td>
				<td>
				<center>
					<i>
						<xsl:value-of select="numinvoked"/>
					</i>
					</center>
				</td>
			</tr>	

</xsl:template>

</xsl:stylesheet>
