/**
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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.install;

/**
 * An abstract factory to allow generic creation of Installers.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface InstallerFactory {
    /**
     * Create a new Installer with default values for editing via bean
     * properties.
     * 
     * @return A new Installer implemented to fit with this Factory
     */
    Installer createInstaller();

    /**
     * Create a new Installer with values from the given url to use for initial
     * values.
     * 
     * @param url
     *            The configuration string
     * @return A new Installer implemented to fit with this Factory
     */
    Installer createInstaller(String url);
}
