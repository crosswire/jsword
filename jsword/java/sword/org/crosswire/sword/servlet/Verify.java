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

        response.setContentType("text/html");
        try
        {
            boolean rebuild = true;

            if (request.getParameterValues("beta")[0].length() > 0)
            {
                beta = true;
                betamgr = new SWMgr("/home/ftp/pub/sword/betaraw"); //***Not really sure if this is necessary or appropriate
            }
            modName = request.getParameterValues("modName")[0];
            pkgType = request.getParameterValues("pkgType")[0];
            File dest = new File("/home/ftp/pub/sword/" + ((beta) ? "betamodules/" : "modules/") + pkgType + "/" + modName + ".zip");
            if (dest.canRead())
            {
                long timeMade = dest.lastModified();
                if (beta)
                {
                    if (PackageMod.getLastModFile(betamgr, "/home/ftp/pub/sword/betaraw/", modName) < timeMade)
                    {
                        rebuild = false;
                    }
                }
                else
                {
                    if (PackageMod.getLastModFile(SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName) < timeMade)
                    {
                        rebuild = false;
                    }
                }
            }
            if (rebuild)
            {
                dest.delete();
                FileOutputStream outFile = new FileOutputStream(dest);
                if (pkgType.equals("win"))
                {
                    ZipOutputStream zstream = new ZipOutputStream(outFile);
                    File dataDir = new File("/home/sword/winmodinst/");
                    String names[] = dataDir.list();
                    for (int i = 0; i < names.length; i++)
                    {
                        if ((!names[i].equals(".")) && (!names[i].equals("..")))
                        {
                            PackageMod.copyFileToZip(zstream, "/home/sword/winmodinst/", names[i], "");
                        }
                    }
                    zstream.putNextEntry(new ZipEntry("data.zip"));
                    if (beta)
                    {
                        PackageMod.sendToZipStream(zstream, betamgr, "/home/ftp/pub/sword/betaraw/", modName, true);
                    }
                    else
                    {
                        PackageMod.sendToZipStream(zstream, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, true);
                    }
                    zstream.close();
                }
                else
                {
                    if (beta)
                    {
                        PackageMod.sendToZipStream(outFile, betamgr, "/home/ftp/pub/sword/betaraw/", modName, false);
                    }
                    else
                    {
                        PackageMod.sendToZipStream(outFile, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, false);
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
                String url = "http://www.crosswire.org/sword/download/ftpmirror/pub/sword/"
                        + ((beta) ? "betamodules/" : "modules/") + pkgType + "/" + modName + ".zip";
                synchronized (packageBuildSemephore)
                {
                    FileOutputStream log = new FileOutputStream("/var/log/httpd/pkgDownloads", true);
                    log.write(new String(pkgType + "|" + modName + "|" + new Date() + "\n").getBytes());
                    log.close();
                }

                response.getOutputStream()
                        .println("<HTML><HEAD><META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=" + url
                                + "\"></HEAD><BODY><A HREF=" + url
                                + ">Click here to download if it doesn't start automatically</A></BODY></HTML>");
                response.getOutputStream().close();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
