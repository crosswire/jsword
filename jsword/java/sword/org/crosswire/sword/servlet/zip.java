package org.crosswire.sword.servlet;

import org.crosswire.sword.web.PackageMod;
import org.crosswire.sword.mgr.SWMgr;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class zip extends HttpServlet {

     //Initialize global variables
     public void init(ServletConfig config) throws ServletException {
          super.init(config);
     }


	public void service(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
		String modName = null;
		String pkgType = null;

		response.setContentType("application/zip");
		try {
			modName = request.getParameterValues("modName")[0];
			pkgType = request.getParameterValues("pkgType")[0];
			if (pkgType.equals("win")) {
				ZipOutputStream zstream = new ZipOutputStream(response.getOutputStream());
				File dataDir = new File("/home/sword/winmodinst/");
				String names[] = dataDir.list();

				for (int i = 0; i < names.length; i++) {
					if ((!names[i].equals(".")) && (!names[i].equals(".."))) {
						PackageMod.copyFileToZip(zstream, "/home/sword/winmodinst/", names[i], "");
					}
				}
				zstream.putNextEntry(new ZipEntry("data.zip"));
				PackageMod.sendToZipStream(zstream, SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, true);
				zstream.close();
			}
			else PackageMod.sendToZipStream(response.getOutputStream(), SWMgr.staticInstance, "/home/ftp/pub/sword/raw/", modName, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getOutputStream().close();
			synchronized(this) {
				FileOutputStream log = new FileOutputStream("/var/log/httpd/pkgDownloads", true);
				log.write(new String(pkgType + "|"+modName + "|" + new Date() + "\n").getBytes());
				log.close();
			}
		}
	}
}
