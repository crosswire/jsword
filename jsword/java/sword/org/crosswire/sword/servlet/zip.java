package org.crosswire.sword.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crosswire.sword.mgr.SWMgr;
import org.crosswire.sword.web.PackageMod;

public class zip extends HttpServlet
{
    //Initialize global variables
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String modName = null;
        String pkgType = null;

        response.setContentType("application/zip");
        try
        {
            modName = request.getParameterValues("modName")[0];
            pkgType = request.getParameterValues("pkgType")[0];
            if (pkgType.equals("win"))
            {
                ZipOutputStream zstream = new ZipOutputStream(response.getOutputStream());
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
                PackageMod.sendToZipStream(zstream, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, true);
                zstream.close();
            }
            else
                PackageMod.sendToZipStream(response.getOutputStream(), SWMgr.staticInstance,
                        "/home/ftp/pub/sword/raw/", modName, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            response.getOutputStream().close();
            synchronized (this)
            {
                FileOutputStream log = new FileOutputStream("/var/log/httpd/pkgDownloads", true);
                log.write(new String(pkgType + "|" + modName + "|" + new Date() + "\n").getBytes());
                log.close();
            }
        }
    }
}
