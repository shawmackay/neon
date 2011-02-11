<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="item">
		<table>
			<tr>
				<td>Name</td>
				<td>
					<xsl:value-of select="./name" />
				</td>
			</tr>
			<tr>
				<td>Desc</td>
				<td>
					<xsl:value-of select="./description" />
				</td>
			</tr>
			<tr>
				<td>Available</td>
				<td>
					<xsl:value-of select="./quantityavailable" />
				</td>
			</tr>
			<tr>
				<td>Price</td>
				<td>
					<xsl:value-of select="./unitprice" />
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>