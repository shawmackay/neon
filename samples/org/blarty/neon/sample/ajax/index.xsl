<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:template match="/">
		<html>
			<head>
				<title>Item Emporium</title>
			</head>
			<script language="JavaScript" type="text/javascript">
			function display(id){			 
   				var url = "display?id=" + id;
   				if (typeof XMLHttpRequest != "undefined") {
       				req = new XMLHttpRequest();
   				} else if (window.ActiveXObject) {
       				req = new ActiveXObject("Microsoft.XMLHTTP");
   				}
   				req.open("GET", url, true);
   				req.onreadystatechange = callback;
   				req.send(null);
			}
			
			function callback() {
    			if (req.readyState == 4) {
        			if (req.status == 200) {
            			var idField = document.getElementById("itemarea");
						var message = req.responseText;
						idField.innerHTML = message;
       				 }
    			}
			}
			</script>
			<body>
				<h1>Please select an item to show information</h1>
				<xsl:apply-templates />
				<span id="itemarea" ></span>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="items">
		<table border="1px solid navy" cellspacing="0" cellpadding="0">
			<tr>
				<th>Item</th>
			</tr>
			<xsl:apply-templates />
		</table>
	</xsl:template>
	<xsl:template match="displayableItem">
		<tr>
			<td>
				<button>
					<xsl:attribute name="onclick">javascript:display('<xsl:value-of
						select="./identifier" />');</xsl:attribute>
					<xsl:value-of select="./name" />
				</button>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>