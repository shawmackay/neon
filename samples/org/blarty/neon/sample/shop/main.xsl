<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:import href="navbar.xsl"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Test Page for loading imported style sheet for Resource URL</title>
            </head>
            <link href="Shop.css" rel="stylesheet" type="text/css"/>
            <script type="text/javascript" src="shop.js"/>
            <body>
                <xsl:call-template name="generateNavBar">
                    <xsl:with-param name="list" select="/root/nav"/>
                </xsl:call-template>
                <xsl:apply-templates/>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="delivery">
        <form action="updateAddress" method="get">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>Addressee</td>
                    <td>
                        <input name="addressee"/>
                    </td>
                </tr>
                <tr>
                    <td>Address</td>
                    <td>
                        <input name="line1">
                            <xsl:attribute name="value" select="line1"/>
                            
                        </input>
                    </td>
                </tr>
                <tr>
                    <td/>&#160;
                    <td>
                        <input name="line2">
                            <xsl:attribute name="value" select="line2"/>
                           
                        </input>
                    </td>
                </tr>
                <tr>
                    <td>City</td>
                    <td>
                        <input name="town">
                            <xsl:attribute name="value" select="town"/>
                        </input>
                    </td>
                </tr>
                <tr>
                    <td>State</td>
                    <td>
                        <input name="stateorcounty">
                            <xsl:attribute name="value" select="stateOrCounty"/>
                            
                        </input>
                    </td>
                </tr>
                <tr>
                    <td>Post code</td>
                    <td>
                        <input name="ziporpostalcode">
                            <xsl:attribute name="value" select="zipOrPostalCode"/>
                            
                        </input>
                    </td>
                </tr>
            </table>
            <input type="submit" value="Update Address"/>
        </form>
    </xsl:template>

    <xsl:template match="items"> There are: <xsl:value-of select="count(entry)"/> different
        items <script language="javascript" type="text/javascript">
            var numitems = <xsl:value-of select="count(entry)"/>;
        </script>
        <!--<form action="add" method="get" id="addForm">
            <input name="reference" id="reference"/>
            <input name="quantityrtrt" id="quantity"/>
            </form>-->
        <form action="add" method="get" id="addForm">
            <table cellspacing="0" cellpadding="0">
                <tr>
                    <th>Select?</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Quantity<br/>Available</th>
                    <th style=" border-right: 1px solid silver;">Number</th>
                </tr>
                <xsl:for-each select="entry">
                    <xsl:variable name="hmm" select="position()"/> Num:<xsl:value-of select="$hmm"/>
                    <tr>
                        <td class="control">
                            <input type="checkbox">

                                <xsl:attribute name="id">select<xsl:value-of select="position()-1"
                                /></xsl:attribute>
                            </input>
                            <input type="hidden" name="item" value="{./value/reference}">

                                <xsl:attribute name="id">hidden<xsl:value-of select="position()-1"
                                /></xsl:attribute>
                            </input>
                        </td>
                        <td>
                            <xsl:value-of select="./value/name"/>
                        </td>
                        <td>
                            <xsl:value-of select="./value/unitprice"/>
                        </td>
                        <td class="number">
                            <xsl:value-of select="./value/quantityAvailable"/>
                        </td>
                        <td class="control" style="border-right: 1px solid silver;">
                            <input maxlength="2" size="2" name="quantity">
                                <xsl:attribute name="id">quantity<xsl:value-of select="position()-1"
                                    /></xsl:attribute>
                            </input>
                        </td>
                    </tr>
                </xsl:for-each>
                <xsl:apply-templates/>
            </table>
            <input type="submit" value="Update"/>
            <xsl:call-template name="createButton">
                <xsl:with-param name="color">clear</xsl:with-param>
                <xsl:with-param name="text">Add Items</xsl:with-param>
                <xsl:with-param name="img">images/basket_add.png</xsl:with-param>
                <xsl:with-param name="href">javascript:doAddSubmit();</xsl:with-param>

            </xsl:call-template>
            <xsl:call-template name="createButton">
                <xsl:with-param name="color">clear</xsl:with-param>
                <xsl:with-param name="text">Remove Items</xsl:with-param>
                <xsl:with-param name="img">images/basket_delete.png</xsl:with-param>
            </xsl:call-template>
        </form>
    </xsl:template>


    <xsl:template name="createButton">
        <xsl:param name="text"/>
        <xsl:param name="color"/>
        <xsl:param name="img"/>
        <xsl:param name="href"/>

        <span>
            <div>
                <xsl:attribute name="class">btn_l_<xsl:value-of select="$color"/></xsl:attribute>
            </div>
            <div>
                <xsl:attribute name="class">btn_c_<xsl:value-of select="$color"/></xsl:attribute>

                <div style="margin-top:13px;">
                    <img src="{$img}" alt="Button"/>&#160; <font size="+1">
                        <a href="{$href}">
                            <xsl:value-of select="$text"/>
                        </a>
                    </font>
                </div>
            </div>
            <div>
                <xsl:attribute name="class">btn_r_<xsl:value-of select="$color"/></xsl:attribute>
            </div>
        </span>
        <div class="floatClear"/>
    </xsl:template>


    <xsl:template match="entry"> </xsl:template>
</xsl:stylesheet>
