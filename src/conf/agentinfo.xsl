<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:output method="html" indent="yes"/>
<xsl:template match="/">	
	<body>
		
<img src="images/imageEffectsBelow_neonemboss_small_blue.png" alt="" id="id3" style="border: none; height: 152px; left: 16px; opacity: 1.00; position: fixed; top: 4px; width: 224px; z-index: 1; " />
					
					<img src="images/neonemboss_small_blue.jpg" alt="" style="border: none; height: 100px; left: 24px; opacity: 1.00; position: fixed; top: 12px; width: 200px; z-index: 2; " />
					
					<img src="images/imageEffectsAbove_neonemboss_small_blue.png" alt="" id="id4" style="border: none; height: 152px; left: 16px; opacity: 1.00; position: fixed; top: 4px; width: 224px; z-index: 1; " />
			<div id="mainbody">		
			<xsl:apply-templates/>
			
			
			<div class="neonback3"><a href="index.jsp">Back</a></div>
		</div>
</body>
</xsl:template>
 
	<xsl:template name="createButton">
		<xsl:param name="text"/>
		<xsl:param name='color'/>
		<span>
			<div >
				<xsl:attribute name="class">btn_l_<xsl:value-of select="$color"/></xsl:attribute>
			</div>
			<div >
				<xsl:attribute name="class">btn_c_<xsl:value-of select="$color"/></xsl:attribute>
				
				<div style="margin-top:13px;"><font size='+1'><xsl:value-of select="$text"/></font>
				</div>
			</div>
			<div>
				<xsl:attribute name="class">btn_r_<xsl:value-of select="$color"/></xsl:attribute>
			</div>
		</span>
		<div class='floatClear'/>
	</xsl:template>

<xsl:template match="agent">
	
	<xsl:call-template name="createButton">
		<xsl:with-param name="text">Agent Identity and State</xsl:with-param>
		<xsl:with-param name="color">aqua</xsl:with-param>
		
	</xsl:call-template>	
	<center>
		<table width="60%">
			<tr>
				<td width="50%">
					Name
				</td>
				<td>
					<xsl:value-of select="name"/>
				</td>
			</tr>
			<tr>
				<td width="50%">
					Namespace
				</td>
				<td>
					<i>
						<xsl:value-of select="namespace"/>
					</i>
				</td>
			</tr>	
			<tr>
				<td width="50%">
					Identity
				</td>
				<td>
					<i>
						<xsl:value-of select="id"/>
					</i>
				</td>
			</tr>	
			<tr>
				<td width="50%">
					Ext. Identity
				</td>
				<td>
					<i>
						<xsl:value-of select="extid"/>
					</i>
				</td>
			</tr>	
			<tr>
				<td width="50%">
					Primary State
				</td>
				<td>
					<i>
						<xsl:value-of select="primary"/>
					</i>
				</td>
			</tr>		
			<tr>
				<td width="50%">
					Secondary State
				</td>
				<td>
					<i>
						<xsl:value-of select="second"/>
					</i>
				</td>
			</tr>			
				<tr>
				<td width="50%">
					Transaction
				</td>
				<td>
					<i>
						<xsl:value-of select="txid"/>
					</i>
				</td>
			</tr>	
			<xsl:if test="slice">
			<tr>
				<td width="50%">
					Slice
				</td>
				<td>
					<i>
						<xsl:value-of select="slice"/>
					</i>
				</td>
			</tr>
			</xsl:if>	
			<tr><td></td><td><a href="invokeLog.jsp?id={id}">Method Log</a></td></tr>
	</table>
	<br/>
	<xsl:apply-templates  select="facets"/>	
</center>

</xsl:template>

<xsl:template name="facets" match="facets">	
	<div class="neonback3">Agent Facets: </div><br/>
	<xsl:apply-templates select="facet"/>	
</xsl:template>

<xsl:template name="facet" match="facet">
<center>
	<table width="60%" rules="rows">
		<xsl:call-template name="createButton">
			<xsl:with-param name="text"><xsl:value-of select="@name"/></xsl:with-param>
			<xsl:with-param name="color">clear</xsl:with-param>
			
		</xsl:call-template>	
		
	<tr><th>Name</th><th>Returning</th><th>Modifiers</th><th>Parameters</th></tr>	
	<xsl:apply-templates select="operation"/>		
	</table>
</center>
</xsl:template>

<xsl:template name="operation" match="operation">			
	<tr>
<td><xsl:value-of select="name"/></td>
<td><xsl:value-of select="return"/></td>
<td><xsl:value-of select="modifiers"/></td>
<td><xsl:apply-templates select="params"/></td>
	</tr>		
</xsl:template>


<xsl:template name="params" match="params">
	<table cellspacing="0" cellpadding="5" border="0" style="border-style: none;">
	<tr></tr>
		<xsl:apply-templates/>
	</table>
</xsl:template>

<xsl:template name="cname" match="cname">
	<tr><td class="othercol" style="margin-bottom: 2px;"><xsl:value-of select="."/></td></tr>
</xsl:template>
</xsl:stylesheet>
