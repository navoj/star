package org.star_lang.star.compiler.util;

import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;

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
