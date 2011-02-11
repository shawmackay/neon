<?xml version="1.0" encoding = "UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="/">
		<link href="tree.css" rel="stylesheet" type="text/css"/>
		<link href="neon.css" rel="stylesheet" type="text/css"/>
		
		<body>
			
			
			<script>
		<![CDATA[
    
    function expandBranch(id){
        var div = document.getElementById('expand'+id);
         var imgdiv=document.getElementById('bullet'+id);
        if(div.style.display=='none'){
        div.style.display="block";
        div.style.visibility='visible';
        imgdiv.src="images/bullet_toggle_minus.png"
        } else {
        div.style.display="none";
        div.style.visibility='hidden';
        imgdiv.src="images/bullet_toggle_plus.png"
        }
       
        
    }
				
				var currentTree;
				
				function showTree(id){
        var div = document.getElementById('tree'+id);
				var olddiv = document.getElementById('tree'+currentTree);
         	
				if(id!=currentTree){
				if(currentTree!=null){
				olddiv.style.display='none';
				olddiv.style.visibility='hidden';
				var ldivid = 'tab_l_id_' +id;
				
				
				
				
				var ldiv = document.getElementById('tab_l_id_' +currentTree);
				ldiv.setAttribute("class", "tab_l_clear");
				ldiv.setAttribute("className", "tab_l_clear");
				
				ldiv = document.getElementById('tab_c_id_' +currentTree);
				ldiv.setAttribute("class", "tab_c_clear");
				ldiv.setAttribute("className", "tab_c_clear");
				
				ldiv = document.getElementById('tab_r_id_' +currentTree);
				ldiv.setAttribute("class", "tab_r_clear");
				ldiv.setAttribute("className", "tab_r_clear");
				
				}
				}
				var ldiv = document.getElementById('tab_l_id_' +id);
				ldiv.setAttribute("class", "tab_l_select");
				ldiv.setAttribute("className", "tab_l_select");
				
				ldiv = document.getElementById('tab_c_id_' +id);
				ldiv.setAttribute("class", "tab_c_select");
				ldiv.setAttribute("className", "tab_c_select");
				
				ldiv = document.getElementById('tab_r_id_' +id);
				ldiv.setAttribute("class", "tab_r_select");
				ldiv.setAttribute("className", "tab_r_select");
        div.style.display="block";
        div.style.visibility='visible';
				currentTree = id;
         
     
        
    }
    
function showLink(id){
        var div = document.getElementById(id);
         var filldiv = document.getElementById('agentInfo');
				
        filldiv.innerHTML = div.innerHTML;
        div = document.getElementById(id);
        div.style.display="block";
        div.style.visibility='visible';
        
    }				
				
    ]]>
	</script>
			
				
				<xsl:apply-templates/>
				
			
		</body>
	</xsl:template>


	<xsl:template match='tree'>
		<div>
			<xsl:call-template name="createButton">
				<xsl:with-param name="color">green</xsl:with-param>
				<xsl:with-param name="text">Selected Agent</xsl:with-param>
			</xsl:call-template>
			<center>
		<div id='agentInfo'/>
				</center>
		</div>
	<div class="treetabs">
		
		<xsl:for-each select="./agent-tree">
			<xsl:call-template name="createTab">
				<xsl:with-param name="selected">false</xsl:with-param>
				<xsl:with-param name="text" select="@domain"/>
				<xsl:with-param name="onclick">showTree('<xsl:value-of select="@domain"/>');</xsl:with-param>
				<xsl:with-param name="id" select="@domain"/>
			</xsl:call-template>
		
		</xsl:for-each>
		<xsl:variable name="defaultTab" select="./agent-tree[position() eq 1]/@domain"/>
		
		<div class='floatClear'/>	
		<xsl:apply-templates/>
		<script>
			showTree(&apos;<xsl:value-of select="$defaultTab"/>&apos;);
		</script>
	</div>	
			
	</xsl:template>

	<xsl:template match="agent-tree">
		<div class="treestructure">
			<xsl:attribute name="id" select="concat('tree',@domain)"/>		
			
			<xsl:for-each select="namespace">
				<xsl:call-template name="branch">
					<xsl:with-param name="level" select="0"/>
					<xsl:with-param name='prefix' select="concat(../@domain,@name)"/>
				</xsl:call-template>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template name="branch">
		<xsl:param name="level"/>
		<xsl:param name="prefix"/>
	<div>
				<xsl:attribute name="style"> padding-left:<xsl:choose>
					<xsl:when test="$level gt 0">16</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				
				<xsl:attribute name="id">
					<xsl:value-of select="concat('branch',$prefix)"/>
				</xsl:attribute>
				<a>
					<xsl:attribute name="href">javascript:expandBranch('<xsl:value-of select="$prefix"
						/>');</xsl:attribute>
					<img src="images/bullet_toggle_plus.png" border="0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('bullet',concat($prefix,@name))"/>
						</xsl:attribute>
					</img><strong>
						<xsl:value-of select="@name"/>
					</strong>
				</a>
				<span style="display:none;visibility:hidden">
					<xsl:attribute name="id" select="concat('expand',$prefix)"/>
					<xsl:for-each select="node()">
						<xsl:if test="string(node-name(.)) eq 'namespace'">

							<xsl:call-template name="branch">
								<xsl:with-param name="level" select="number($level) +1"/>
								<xsl:with-param name="prefix" select="concat($prefix,@name)"/>
							</xsl:call-template>

						</xsl:if>
						<xsl:if test="string(node-name(.)) eq 'agentinformation'">
							<xsl:call-template name="leaf">
								<xsl:with-param name="level" select="number($level) +1"/>
								<xsl:with-param name="prefix" select="concat($prefix,@name)"/>
								<xsl:with-param name="node" select="."/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
				</span>
			</div>
	</xsl:template>


	<xsl:template name="leaf">
		<xsl:param name="level"/>
		<xsl:param name="node"/>
		<xsl:param name="prefix"/>
		<div>
			<xsl:attribute name="style"> padding-left:<xsl:value-of select="32"/>
			</xsl:attribute>
			<a>
				<xsl:attribute name="href">
					<xsl:choose>
						<xsl:when test="exists(./location)">
							<xsl:value-of select="./location/text()"/>
						</xsl:when>
						<xsl:when test="exists(./script)">javascript:<xsl:value-of
								select="./script/text()"/></xsl:when>
						<xsl:otherwise>javascript:showLink('info<xsl:value-of select="$node/@ID"
							/>');</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="exists(./meta/metaitem[@name='name'])">
						<xsl:value-of select="./meta/metaitem"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring(@ID,0,12)"/>...
					</xsl:otherwise>
				</xsl:choose>
				
			</a>
			<xsl:apply-templates select="/monitor/domain/agents/agent[id eq $node/@ID]"/>
		</div>
	</xsl:template>


	<xsl:template match="monitor">
		<div>
		<xsl:attribute name="id">
			<xsl:choose>
				<xsl:when test="exists(/monitor/tree)">maincontent</xsl:when>
			<xsl:otherwise>mainbody</xsl:otherwise>
			</xsl:choose>
			
		</xsl:attribute>
			<xsl:choose>
				<xsl:when test="exists(/monitor/tree)">
			<img src="images/imageEffectsBelow_neonemboss_small_blue.png" alt="" id="id3" style="border: none; height: 152px; left: 316px; opacity: 1.00; position: fixed; top: 4px; width: 224px; z-index: 1; " />
			
			<img src="images/neonemboss_small_blue.jpg" alt="" style="border: none; height: 100px; left: 324px; opacity: 1.00; position: fixed; top: 12px; width: 200px; z-index: 2; " />
			
			<img src="images/imageEffectsAbove_neonemboss_small_blue.png" alt="" id="id4" style="border: none; height: 152px; left: 316px; opacity: 1.00; position: fixed; top: 4px; width: 224px; z-index: 1; " />
				</xsl:when>
				<xsl:otherwise>
					
					<img src="images/imageEffectsBelow_neonemboss_small_blue.png" alt="" id="id3" style="border: none; height: 152px; left: 16px; opacity: 1.00; position: fixed; top: 4px; width: 224px; z-index: 1; " />
					
					<img src="images/neonemboss_small_blue.jpg" alt="" style="border: none; height: 100px; left: 24px; opacity: 1.00; position: fixed; top: 12px; width: 200px; z-index: 2; " />
					
					<img src="images/imageEffectsAbove_neonemboss_small_blue.png" alt="" id="id4" style="border: none; height: 152px; left: 16px; opacity: 1.00; position: fixed; top: 4px; width: 224px; z-index: 1; " />
					
					</xsl:otherwise>
			</xsl:choose>

		<xsl:call-template name="createButton">
			<xsl:with-param name="text">Monitor Results</xsl:with-param>
			<xsl:with-param name="color">aqua</xsl:with-param>

		</xsl:call-template>
		<xsl:apply-templates/>
		
			</div>
	</xsl:template>

	<!-- Main start page administration -->

	<xsl:template name="createButton">
		<xsl:param name="text"/>
		<xsl:param name="color"/>
		<span>
			<div>
				<xsl:attribute name="class">btn_l_<xsl:value-of select="$color"/></xsl:attribute>
			</div>
			<div>
				<xsl:attribute name="class">btn_c_<xsl:value-of select="$color"/></xsl:attribute>

				<div style="margin-top:13px;">
					<font size="+1">
						<xsl:value-of select="$text"/>
					</font>
				</div>
			</div>
			<div>
				<xsl:attribute name="class">btn_r_<xsl:value-of select="$color"/></xsl:attribute>
			</div>
		</span>
		<div class="floatClear"/>
	</xsl:template>

<xsl:template name="createTab">
		<xsl:param name="text"/>
		<xsl:param name="selected"/>
	<xsl:param name="onclick"/>
	<xsl:param name='id'/>
		<xsl:variable name='type'>
			<xsl:choose>
				<xsl:when test="$selected eq 'true'" >select</xsl:when>
				<xsl:otherwise>clear</xsl:otherwise>
			</xsl:choose>
			</xsl:variable>
		<span>
				
			<div>
				<xsl:attribute name="class">tab_l_<xsl:value-of select="$type"/></xsl:attribute>
				<xsl:attribute name="onclick" select="$onclick"/>
				<xsl:attribute name='id' select="concat('tab_l_id_', $id)"/>
			</div>			<div>
				<xsl:attribute name="class">tab_c_<xsl:value-of select="$type"/></xsl:attribute>
				<xsl:attribute name="onclick" select="$onclick"/>
				<xsl:attribute name='id' select="concat('tab_c_id_', $id)"/>
				<div style="margin-top:13px;">
					<font size="-1">
						<xsl:value-of select="$text"/>
					</font>
				</div>
			</div><div>
				<xsl:attribute name="class">tab_r_<xsl:value-of select="$type"/></xsl:attribute>
				<xsl:attribute name="onclick" select="$onclick"/>
				<xsl:attribute name='id' select="concat('tab_r_id_', $id)"/>
			</div>
				</span>
		
	</xsl:template>


	<xsl:template match="host"> System currently running on machine <xsl:value-of select="."/>
		<hr/>
	</xsl:template>
	<xsl:template match="memory">
		<center>
			<table width="60%">
				<tr>
					<th colspan="2">Memory Information</th>
				</tr>
				<tr>
					<td width="50%">Free Memory</td>
					<td>
						<xsl:value-of select="memfree"/>
					</td>
				</tr>
				<tr>
					<td width="50%">Total Memory</td>
					<td>
						<xsl:value-of select="totalmem"/>
					</td>
				</tr>
			</table>
		</center>
	</xsl:template>

	<xsl:template match="java">
		<center>
			<table width="60%">
				<tr>
					<th colspan="2">Java Information</th>
				</tr>
				<tr>
					<td width="50%">Virtual Machine Version</td>
					<td>
						<xsl:value-of select="vm.version"/>
					</td>
				</tr>
				<tr>
					<td width="50%">Vendor</td>
					<td>
						<xsl:value-of select="vm.vendor"/>
					</td>
				</tr>
				<tr>
					<td width="50%">VM Name</td>
					<td>
						<xsl:value-of select="vm.name"/>
					</td>
				</tr>
			</table>
		</center>
	</xsl:template>

	<xsl:template name="os" match="os">
		<center>
			<table width="60%" bgcolor="eeeeff">
				<tr>
					<th colspan="2">OS Information</th>
				</tr>

				<tr>
					<td width="50%">Operating System</td>
					<td>
						<xsl:value-of select="hostingos"/>
					</td>
				</tr>
				<tr>
					<td>System Version</td>
					<td>
						<xsl:value-of select="osversion"/>
					</td>
				</tr>
				<tr>
					<td>System Architecture</td>
					<td>
						<xsl:value-of select="osarch"/>
					</td>
				</tr>

			</table>
			<br/>
			<br/>
		</center>
	</xsl:template>
	<xsl:template name="domain" match="domain">
		
		<xsl:apply-templates/>
		
	</xsl:template>

	<xsl:template name="domainDetails" match="domainDetails">
		<xsl:call-template name="createButton">
			<xsl:with-param name="color">clear</xsl:with-param>
			<xsl:with-param name="text">Domain: <xsl:value-of select="@name"/></xsl:with-param>
		</xsl:call-template>
		<div style="left:40px;margin-left:40px;">
			<xsl:apply-templates/>
		</div>
		<br/>
	</xsl:template>

	<xsl:template match="agent">
		<div style="display:none;visibility:hidden">
			<div>
				<xsl:attribute name="id">info<xsl:value-of select="id"/></xsl:attribute>
				<table>
					<tr>
						<td class="leftcol">Name</td>
						<td class="othercol">
							<xsl:value-of select="name"/>
						</td>
					</tr>
					<tr>
						<td class="leftcol">Namespace</td>
						<td class="othercol">
							<i>
								<xsl:value-of select="namespace"/>
							</i>
						</td>
					</tr>
					<tr>
						<td class="leftcol">ID</td>
						<td class="othercol">
							<xsl:value-of select="id"/>
						</td>
					</tr>
					<tr>
						<td class="leftcol">State</td>
						<td class="othercol">
							<b>
								<xsl:value-of select="state"/>
							</b>
						</td>
					</tr>
					<tr>
						<td>
							<form action="agents.jsp" METHOD="GET">
								<div style="visibility:hidden;display:none;">
									<textarea name="id">
										<xsl:value-of select="id"/>
									</textarea>
								</div>
								<input type="SUBMIT" name="submit" value="Info"/>
							</form>
						</td>
						<xsl:if test="render/text()='true'">
							<td>
								<form action="Render.do" METHOD="GET">
									<div style="visibility:hidden;display:none;">
										<textarea name="agentid">
											<xsl:value-of select="id"/>
										</textarea>
									</div>
									<input type="SUBMIT" name="submit" value="Show"/>
								</form>
							</td>
						</xsl:if>
					</tr>
				</table>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="domains" match="domains">
		<h2>Domains</h2>
		
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="allowin" match="allowin">
		<span class="neonback4">
			Allow these domains to request agents:
		</span>
		<br/>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="aclname" match="aclname">
		<span style="left:40px;margin-left:40px;padding-top:12px;top:12px;">
			<xsl:value-of select="."/>
		</span>
		<br/>
	</xsl:template>


	<xsl:template name="allowout" match="allowout">
		<span class="neonback4">
			 Can request agents from these domains:
		</span>
		<br/>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template name="numberAgents" match="numberAgents">
		<h2>Number of agents Registered in this domain: <xsl:value-of select="."/></h2>
	</xsl:template>
</xsl:stylesheet>
