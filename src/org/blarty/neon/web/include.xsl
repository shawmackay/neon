<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" exclude-result-prefixes="xi" xmlns:xi="http://www.w3.org/2001/XInclude" >
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match='root'>
        <xsl:element name="{@name}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match='subroot'>
        <xsl:element name="{@name}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    
    <xsl:template match="xi:include">
        <xsl:for-each select="document(@href)">
                <xsl:copy-of select="."/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
