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

        response.setContentType("application/zip"); //$NON-NLS-1$
        try
        {
            modName = request.getParameterValues("modName")[0]; //$NON-NLS-1$
            pkgType = request.getParameterValues("pkgType")[0]; //$NON-NLS-1$
            if (pkgType.equals("win")) //$NON-NLS-1$
            {
                ZipOutputStream zstream = new ZipOutputStream(response.getOutputStream());
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
                PackageMod.sendToZipStream(zstream, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, true); //$NON-NLS-1$
                zstream.close();
            }
            else
                PackageMod.sendToZipStream(response.getOutputStream(), SWMgr.staticInstance,
                        "/home/ftp/pub/sword/raw/", modName, false); //$NON-NLS-1$
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
                FileOutputStream log = new FileOutputStream("/var/log/httpd/pkgDownloads", true); //$NON-NLS-1$
                log.write(new String(pkgType + "|" + modName + "|" + new Date() + "\n").getBytes()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                log.close();
            }
        }
    }
}
