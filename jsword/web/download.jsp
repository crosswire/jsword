<%@ page import="java.util.*, java.io.*, java.text.*" %>
<!-- org.crosswire.jsword.view.web.Download,  -->

<jsp:include page="header.jsp">
  <jsp:param name="title" value="JSword - Download" />
</jsp:include>

<%!

    /**
     * Set needed variables
     */
    public static void init(String directory, String newprefix) throws IOException
    {
        dir = new File(directory);
        if (!dir.isDirectory())
        {
            throw new IOException(directory+" is not a directory");
        }
        
        prefix = newprefix;
    }
    
    /**
     * Get an Iterator over all the Downloads in the specified Directory
     */
    public static Iterator getDownloads()
    {
        File[] files = dir.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.canWrite() && file.getName().endsWith(EXTENSIONS[0]);
            }
        });

        List reply = new ArrayList();
        for (int i = 0; i < files.length; i++)
        {
            try
            {
                reply.add(new Download(files[i]));
            }
            catch (ParseException ex)
            {
                //log.error("Ignoring file: "+files[i], ex);
            }
        }

        return reply.iterator();
    }

public static class Download
{
    /**
     * Use init() and then getDownloads() rather than the ctor.
     */
    private Download(File file) throws ParseException
    {
        String whole = file.getName();

        int dash = whole.indexOf('-');
        if (dash == -1)
        {
            throw new ParseException("Missing -", 0);
        }

        Date date = dfin.parse(whole.substring(dash));
        datestr = dfout.format(date);

        this.base = whole.substring(0, whole.length() - EXTENSIONS[0].length());
    }
        
    /**
     * The date as a String
     */
    public String getDateString()
    {
        return datestr;
    }
        
    /**
     * The URL as a String
     */
    public String getURLString(String extension)
    {
        return prefix + base + extension;
    }
        
    /**
     * The file size as a string
     */
    public String getSizeString(String extension)
    {
        File file = new File(dir, base + extension);
        float size = (float) file.length() / (1024F * 1024F);
        return nf.format(size) + "Mb";
    }

    private String base;
    private String datestr;

}

    protected static String prefix;

    protected static File dir;

    public static final String BIN_ZIP = "-bin.zip"; 
    public static final String BIN_TGZ = "-bin.tgz"; 
    public static final String SRC_ZIP = "-src.zip"; 
    public static final String SRC_TGZ = "-src.tgz"; 
    public static final String DOC_ZIP = "-doc.zip"; 
    public static final String DOC_TGZ = "-doc.tgz"; 

    public static String[] EXTENSIONS = 
    {
        BIN_ZIP,
        BIN_TGZ,
        SRC_ZIP,
        SRC_TGZ,
        DOC_ZIP,
        DOC_TGZ,
    };

    protected static final NumberFormat nf = NumberFormat.getNumberInstance();
    protected static final DateFormat dfin = new SimpleDateFormat("yyyyMMdd");
    protected static final DateFormat dfout = new SimpleDateFormat("dd MMM yyyy");

    static
    {
        nf.setMaximumFractionDigits(1);
    }

    /** The log stream */
    //protected static Logger log = Logger.getLogger(Download.class);
%>

<h1>Download</h1>

<h3>Official Releases</h3>
<p>
The first beta release is due out soon, pending completion of the
Sword drivers.
</p>

<h3>Nightly Releases</h3>
<p>Regular releases are made and stored for a short time:</p>

<%
/*Download.*/init("@DOWNLOAD@/nightly", "@BASEURL@/nightly");
Iterator it = /*Download.*/getDownloads();
%>

<table width="90%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
	<td width="25%">Date</td>
	<td width="25%">Binary</td>
	<td width="25%">Source</td>
	<td width="25%">Documentation</td>
  </tr>
  <tr>
    <% while (it.hasNext()) { Download d = (Download) it.next(); %>
	<td><%= d.getDateString() %></td>
	<td>
	  <a href="<%= d.getURLString(/*Download.*/BIN_ZIP) %>">zip</a> (<%= d.getSizeString(/*Download.*/BIN_ZIP) %>)
	  <br>
	  <a href="<%= d.getURLString(/*Download.*/BIN_TGZ) %>">tar.gz</a> (<%= d.getSizeString(/*Download.*/BIN_TGZ) %>)
    </td>
	<td>
	  <a href="<%= d.getURLString(/*Download.*/SRC_ZIP) %>">zip</a> (<%= d.getSizeString(/*Download.*/SRC_ZIP) %>)
	  <br>
	  <a href="<%= d.getURLString(/*Download.*/SRC_TGZ) %>">tar.gz</a> (<%= d.getSizeString(/*Download.*/SRC_TGZ) %>)
    </td>
	<td>
	  <a href="<%= d.getURLString(/*Download.*/DOC_ZIP) %>">zip</a> (<%= d.getSizeString(/*Download.*/DOC_ZIP) %>)
	  <br>
	  <a href="<%= d.getURLString(/*Download.*/DOC_TGZ) %>">tar.gz</a> (<%= d.getSizeString(/*Download.*/DOC_TGZ) %>)
    </td>
    <% } %>
  </tr>
</table>

<h3>CVS Access</h3>
<p>
The most up to date access is via CVS. There are CVS access instruction 
on the <a href="devt.jsp">Getting Involved</a> page.
</p>

<h3>Modules</h3>
<p>
Sword modules are available
<a href="http://www.crossire.org/sword/modules/index.jsp">here</a>. 
Most of the Bibles and Concordances are now working, and the next priority is the
Lexicons and Dictionaries.
</p>

<jsp:include page="footer.jsp" />
