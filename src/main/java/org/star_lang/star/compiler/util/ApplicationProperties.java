package org.star_lang.star.compiler.util;

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public class ApplicationProperties
{
  public static final String WD = getProperty("SET_WD", System.getProperty("user.dir"));

  static {
    try {
      ApplicationProperties.wdURI = ResourceURI.parseURI("file:"
          + WD.replace(System.getProperty("file.separator"), "/") + "/catalog");
    } catch (ResourceException e) {
      wdURI = ResourceURI.noUriEnum;
    }
  }

  public static boolean getProperty(String name, boolean deflt)
  {
    String prop = System.getProperty(name);
    if (prop == null)
      return deflt;
    else if (prop.equals(""))
      return true;
    else
      return prop.equalsIgnoreCase("true");
  }

  public static int getProperty(String name, int deflt)
  {
    String prop = System.getProperty(name);
    if (prop == null)
      return deflt;
    else
      return Integer.parseInt(prop);
  }

  public static String getProperty(String name, String deflt)
  {
    String prop = System.getProperty(name);
    if (prop == null)
      return deflt;
    else
      return prop;
  }

  public static ResourceURI wdURI;

  public static String getWd()
  {
    return WD;
  }

  public static void setWd(ResourceURI uri)
  {
    wdURI = uri;
  }
}
