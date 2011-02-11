<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
	<xsl:template match="parameter">
		<tr>
			<td>
				<strong>
					<xsl:value-of select="@name" />
				</strong>
			</td>
			<td>
				<xsl:value-of select="@value" />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>