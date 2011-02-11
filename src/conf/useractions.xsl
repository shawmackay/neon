<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:output method="html" indent="yes"/>
<xsl:param name="submit" select="index"/>
<xsl:template match="/">	
Hello
	<xsl:if test="$submit='add'">
			<xsl:call-template name="add"/>
	</xsl:if>
	<div class="neonback3"><a href="index.jsp">Back</a></div>
</xsl:template>


<xsl:template name="add" match="add">
	Adding a user
</xsl:template>

<xsl:template match="delete">
	Deleting a user
</xsl:template>

<xsl:template match="modify">
	Changing a user
</xsl:template>
</xsl:stylesheet>