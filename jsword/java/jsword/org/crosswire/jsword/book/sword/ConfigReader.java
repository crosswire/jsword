package org.crosswire.jsword.book.sword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A utility class for loading Sword module config data.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class ConfigReader 
{
	private BufferedReader bufferedReader;
	private Hashtable table = new Hashtable();
	
	public ConfigReader(InputStream is) throws IOException
	{
		InputStreamReader r = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(r);
		this.bufferedReader = br;
		
		String currentLine = "";
		while(currentLine!=null){
			currentLine = bufferedReader.readLine();
			if(currentLine!=null) parseLine(currentLine);
		}
	}

	/**
	 * Method parseLine.
	 * @param currentLine
	 */
	private void parseLine(String currentLine) 
	{
		// don't use a tokenizer - a value may have an =
		int eqIdx = currentLine.indexOf('=');
		if(eqIdx>=0&&eqIdx<currentLine.length()-1)
		{
			String key = currentLine.substring(0,eqIdx).trim();
			if(key.length()>0)
			{
				String value = currentLine.substring(eqIdx+1);
				// check to see if there already values for this key...
				ArrayList list = (ArrayList) table.get(key);
				if(list!=null)
				{
					list.add(value);
				}
				else
				{
					list = new ArrayList();
					list.add(value);
					table.put(key,list);
				}
			}
		}
	}
	
	/**
	 * Returns an Enumeration of all the keys found in the config file.
	 */
	public Enumeration getKeys()
	{
		return table.keys();
	}
	
	/**
	 * Returns only one value for the key (for cases where only one value is expected).
	 */
	public String getFirstValue(String key)
	{
		String retVal = null;
		ArrayList list = (ArrayList) table.get(key);
		if(list!=null)
		{
			return (String) list.get(0);
		}
		return retVal;
	}
	
	/**
	 * Returns all values for the key (for cases where many values are expected).
	 */
	public Iterator getAllValues(String key)
	{
		ArrayList list = (ArrayList) table.get(key);
		if(list!=null)
		{
			return list.iterator();
		}
		return  new Iterator()
		{
			public boolean hasNext()
			{
				return false;
			}
			public Object next()
			{
				throw new NoSuchElementException();
			}
			public void remove()
			{
			}
		};
	}
	
	public static void main(String[] args) 
	{
		// A little test
		// TODO: MDG.
		// JUnit tests..... (ask joe about a ${project.root}/misc/testdata dir - this (and many other tests) are data dependant)
		try
		{
			File f = new File("/usr/share/sword/mods.d/kjv.conf");
			FileInputStream fis= new FileInputStream(f);
			ConfigReader reader = new ConfigReader(fis);
			
			Enumeration en = reader.getKeys();
			while(en.hasMoreElements())
			{
				String key = (String) en.nextElement();
				Iterator it = reader.getAllValues(key);
				while(it.hasNext()){
					String value = (String) it.next();
					System.out.println(key+" = "+value);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
