package org.crosswire.sword.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.crosswire.sword.mgr.SWMgr;
import org.crosswire.sword.web.PackageMod;

public class Verify extends HttpServlet
{
    static Object packageBuildSemephore = new Object();

    //Initialize global variables
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }

    public void service(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
    {
        String modName = null;
        String pkgType = null;
        boolean beta = false;

        SWMgr betamgr = null;

        response.setContentType("text/html"); //$NON-NLS-1$
        try
        {
            boolean rebuild = true;

            if (request.getParameterValues("beta")[0].length() > 0) //$NON-NLS-1$
            {
                beta = true;
                betamgr = new SWMgr("/home/ftp/pub/sword/betaraw"); //***Not really sure if this is necessary or appropriate //$NON-NLS-1$
            }
            modName = request.getParameterValues("modName")[0]; //$NON-NLS-1$
            pkgType = request.getParameterValues("pkgType")[0]; //$NON-NLS-1$
            File dest = new File("/home/ftp/pub/sword/" + ((beta) ? "betamodules/" : "modules/") + pkgType + "/" + modName + ".zip"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            if (dest.canRead())
            {
                long timeMade = dest.lastModified();
                if (beta)
                {
                    if (PackageMod.getLastModFile(betamgr, "/home/ftp/pub/sword/betaraw/", modName) < timeMade) //$NON-NLS-1$
                    {
                        rebuild = false;
                    }
                }
                else
                {
                    if (PackageMod.getLastModFile(SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName) < timeMade) //$NON-NLS-1$
                    {
                        rebuild = false;
                    }
                }
            }
            if (rebuild)
            {
                dest.delete();
                FileOutputStream outFile = new FileOutputStream(dest);
                if (pkgType.equals("win")) //$NON-NLS-1$
                {
                    ZipOutputStream zstream = new ZipOutputStream(outFile);
                    File dataDir = new File("/home/sword/winmodinst/"); //$NON-NLS-1$
                    String names[] = dataDir.list();
                    for (int i = 0; i < names.length; i++)
                    {
                        if ((!names[i].equals(".")) && (!names[i].equals(".."))) //$NON-NLS-1$ //$NON-NLS-2$
                        {
                            PackageMod.copyFileToZip(zstream, "/home/sword/winmodinst/", names[i], ""); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                    zstream.putNextEntry(new ZipEntry("data.zip")); //$NON-NLS-1$
                    if (beta)
                    {
                        PackageMod.sendToZipStream(zstream, betamgr, "/home/ftp/pub/sword/betaraw/", modName, true); //$NON-NLS-1$
                    }
                    else
                    {
                        PackageMod.sendToZipStream(zstream, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, true); //$NON-NLS-1$
                    }
                    zstream.close();
                }
                else
                {
                    if (beta)
                    {
                        PackageMod.sendToZipStream(outFile, betamgr, "/home/ftp/pub/sword/betaraw/", modName, false); //$NON-NLS-1$
                    }
                    else
                    {
                        PackageMod.sendToZipStream(outFile, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, false); //$NON-NLS-1$
                    }
                }
                outFile.flush();
                outFile.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                String url = "http://www.crosswire.org/sword/download/ftpmirror/pub/sword/" //$NON-NLS-1$
                        + ((beta) ? "betamodules/" : "modules/") + pkgType + "/" + modName + ".zip"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                synchronized (packageBuildSemephore)
                {
                    FileOutputStream log = new FileOutputStream("/var/log/httpd/pkgDownloads", true); //$NON-NLS-1$
                    log.write(new String(pkgType + "|" + modName + "|" + new Date() + "\n").getBytes()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    log.close();
                }

                response.getOutputStream()
                        .println("<HTML><HEAD><META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=" + url //$NON-NLS-1$
                                + "\"></HEAD><BODY><A HREF=" + url //$NON-NLS-1$
                                + ">Click here to download if it doesn't start automatically</A></BODY></HTML>"); //$NON-NLS-1$
                response.getOutputStream().close();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
