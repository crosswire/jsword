<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
     <ruleset name="Basic Rules">
       <description>An automatically generated ruleset containting everything</description>
       <xsl:apply-templates select="ruleset/rule"/>
     </ruleset>
  </xsl:template>

  <xsl:template match="/ruleset/rule">
    <!-- edit this to match the ruleset being processed -->
    <rule ref="rulesets/unusedcode.xml/{@name}"/>
  </xsl:template>

</xsl:stylesheet>
