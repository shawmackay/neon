<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:template match="/">
		<html>
			<head>
				<title>Sample</title>
			</head>
			<body>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="character">
		Name:
		<xsl:value-of select="name" />
		<br />
		Age:
		<xsl:value-of select="age" />
		<br />
		Role:
		<xsl:value-of select="career" />
		<br />
		<br />
		<xsl:apply-templates select="currentStats" />
		<xsl:apply-templates select="originalStats" />
	</xsl:template>
	<xsl:template match="currentStats">
		Current Statistics:
		<xsl:call-template name="displayStatsTable">
			<xsl:with-param name="stats" select="." />
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="originalStats">
		Original Statistics:
		<xsl:call-template name="displayStatsTable">
			<xsl:with-param name="stats" select="." />
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="displayStatsTable">
		<xsl:param name="stats" />
		<table cellpadding="2" cellspacing="0" style="border: 1px solid gray">
			<tr style="background-color:black; color: white">
				<xsl:for-each select="$stats/node()">
					<xsl:if test="name(.) ne ''">
						<td>
							<xsl:call-template name="getStatAbbreviation">
								<xsl:with-param name="name" select="name(.)" />
							</xsl:call-template>
						</td>
					</xsl:if>
				</xsl:for-each>
			</tr>
			<tr>
				<xsl:for-each select="$stats/node()">
					<xsl:if test="name(.) ne ''">
						<td align="center">
							<xsl:value-of select="." />
						</td>
					</xsl:if>
				</xsl:for-each>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="getStatAbbreviation">
		<xsl:param name="name" />
		<xsl:choose>
			<xsl:when test="$name eq 'movementAllowance'">
				MA
			</xsl:when>
			<xsl:when test="$name eq 'body'">
				BODY
			</xsl:when>
			<xsl:when test="$name eq 'empathy'">
				EMP
			</xsl:when>
			<xsl:when test="$name eq 'attractiveness'">
				ATTR
			</xsl:when>
			<xsl:when test="$name eq 'tech'">
				TECH
			</xsl:when>
			<xsl:when test="$name eq 'cool'">
				Cool
			</xsl:when>
			<xsl:when test="$name eq 'intelligence'">
				INT
			</xsl:when>
			<xsl:when test="$name eq 'reflexes'">
				REF
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>