<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:template name="generateNavBar">

        <xsl:param name="list"/>
        Hello
        <xsl:value-of select = "$list"/>
        <xsl:for-each select="$list/navpoint" >
                    <xsl:value-of select="."/>
                    <a>
                    <xsl:attribute name="href" select="@ref"/>
                    <xsl:value-of select="@name"/>
                    </a>&#160;           
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
