/*
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
/**
<p>Config allows an application to separate the responsibilities of setting
itself up, and providing a user interface from the real work that it needs
to do.</p>

<h2>Introduction</h2>
<p>Config is (mostly) all kept in a few packages in the util source tree. The
 design aims for the following goals:</p>
<ul>
  <li>Application Transparency - It should be possible to add a configuration
    dialog to an application without adding hundreds of hooks either to your
    application to read the current state, or to the configuration system to
    work with the application. This is achieved via an xml config file and a
    healthy dose of reflection.</li>
  <li>View Independance - Currently there are a number of Swing front ends -
    a Mozilla style config dialog with a tree, a more conventional tabbed dialog,
    and a prototype wizard style interface. There has also been a servlet front-end
    however the code to do this has suffered bit-rot, and should not be considered
    useful. It does however prove the view independance concept.</li>
</ul>

<h3>How To Use Config</h3>
<p>There are a number of simple steps. First a config.xml file is needed to tell
the config system what to configure and how.</p>

<pre>
&lt;config&gt;

  <span style="color: #336600">&lt;!-- A configuration is a set of options ... --&gt;</span>
  <span style="color: #336600">&lt;!-- The key is a dot separated name - Imaging this in a Mozilla tree or some nested tabs. --&gt;</span>
  &lt;option key=&quot;Bibles.Sword.Base Directory&quot; type=&quot;string&quot;&gt;
    <span style="color: #336600">&lt;!-- The type (above) along with the introspect line configures what JavaBean methods will be called --&gt;</span>
    &lt;introspect class=&quot;org.crosswire.jsword.book.sword.SwordBibleDriver&quot; property=&quot;SwordDir&quot;/&gt;
    <span style="color: #336600">&lt;!-- The tool-tip (or similar) describing what is going on --&gt;</span>
    &lt;help&gt;Where is the SWORD Project base directory.&lt;/help&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- Another option, this time it is a boolean option which will show up as a tickbox --&gt;</span>
  &lt;option key=&quot;Bibles.Display.Persistent Naming&quot; level=&quot;advanced&quot; type=&quot;boolean&quot;&gt;
    &lt;introspect class=&quot;org.crosswire.jsword.passage.PassageUtil&quot; property=&quot;PersistentNaming&quot;/&gt;
    &lt;help&gt;True if the passage editor re-writes the references to conform to its notation.&lt;/help&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- Another type again this one for the look and feel. --&gt;
  &lt;!-- The reason for the helper class here is to alter windows that are not currently mapped --&gt;</span>
    &lt;option key=&quot;Looks.Look and Feel&quot; type=&quot;class&quot;&gt;
    &lt;introspect class=&quot;org.crosswire.common.swing.LookAndFeelUtil&quot; property=&quot;LookAndFeel&quot;/&gt;
    &lt;help&gt;The look and feel of the application&lt;/help&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- When we have have an Enum style config option ... --&gt;</span>
    &lt;option key=&quot;Bibles.Display.Book Case&quot; level=&quot;advanced&quot; type=&quot;int-options&quot;&gt;
    &lt;introspect class=&quot;org.crosswire.jsword.passage.Books&quot; property=&quot;Case&quot;/&gt;
    &lt;help&gt;What case should we use to display the references.&lt;/help&gt;
    &lt;alternative number=&quot;0&quot; name=&quot;lower&quot;/&gt;
    &lt;alternative number=&quot;1&quot; name=&quot;Sentence&quot;/&gt;
    &lt;alternative number=&quot;2&quot; name=&quot;UPPER&quot;/&gt;
    &lt;alternative number=&quot;3&quot; name=&quot;mIXeD&quot;/&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- The options here are more complex and need to be provided as a string array by Java code (see below) --&gt;</span>
    &lt;option key=&quot;Bibles.Default&quot; type=&quot;string-options&quot;&gt;
    &lt;introspect class=&quot;org.crosswire.jsword.book.Bibles&quot; property=&quot;DefaultName&quot;/&gt;
    &lt;help&gt;Which of the available Bibles is the default.&lt;/help&gt;
    &lt;map name=&quot;biblenames&quot;/&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- This option is 'advanced' which means it is not visible to all users (see below) --&gt;</span>
    &lt;option key=&quot;Advanced.Source Path&quot; level=&quot;advanced&quot; type=&quot;path&quot;&gt;
    &lt;introspect class=&quot;org.crosswire.common.swing.DetailedExceptionPane&quot; property=&quot;SourcePath&quot;/&gt;
    &lt;help&gt;The directories to search for source code in when investigating an exception.&lt;/help&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- When the choice is very custom you can always do your own implementation --&gt;
  &lt;!-- This allows us to set users levels so not everyone gets asked hard questions --&gt;</span>
    &lt;option key=&quot;Advanced.User Level&quot; type=&quot;custom&quot; class=&quot;org.crosswire.common.util.UserLevel$UserLevelChoice&quot;&gt;
    &lt;help&gt;How advanced is your knowledge of this program.&lt;/help&gt;
  &lt;/option&gt;

  <span style="color: #336600">&lt;!-- There are other examples in config.xml --&gt;</span>
&lt;/config&gt;
</pre>

<p>Then you need to add the Java code:</p>
<pre><span style="color: #336600">
// To load the config.xml file:
</span>Config config = new Config(&quot;Tool Shed Options&quot;);
Document xmlconfig = Project.resource().getDocument(&quot;config&quot;); <span style="color: #336600">// Or whatever to get a JDOM Document</span>
config.add(xmlconfig);

<span style="color: #336600">// To load a saved config</span>
config.setProperties(Project.resource().getProperties(&quot;desktop&quot;)); <span style="color: #336600">// Or however you get a Properties</span>
config.localToApplication(true);

<span style="color: #336600">// And display it ...</span>
URL configurl = Project.resource().getPropertiesURL(&quot;desktop&quot;); <span style="color: #336600">// URL of the Properties file to save to</span>
SwingConfig.showDialog(config, parentWind, configurl);

<span style="color: #336600">// The code above needed help in setting up a string choice. This is how ...</span>
ChoiceFactory.getDataMap().put(&quot;biblenames&quot;, Bibles.getBibleNames());
</pre>
<p>There are more examples in <code>org.crosswire.bibledesktop.desktop.OptionsAction.</code></p>

*/
package org.crosswire.common.config;
