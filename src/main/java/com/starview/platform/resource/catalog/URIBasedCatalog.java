package com.starview.platform.resource.catalog;

import java.io.File;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.Resources;
import com.starview.platform.resource.URIUtils;

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

@SuppressWarnings("serial")
public class URIBasedCatalog implements Catalog
{
  private final ResourceURI base;
  private final Catalog starCatalog = StarRules.starCatalog();
  private final Catalog fallback;

  public URIBasedCatalog(ResourceURI base, Catalog fallback)
  {
    this.base = base;
    this.fallback = fallback;
  }

  public URIBasedCatalog(ResourceURI base, String version, String owner)
  {
    this(base, null);
  }

  @Override
  public String getVersion()
  {
    return null;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("catalog based on ");
    base.prettyPrint(disp);
    if (base.getScheme().equals(Resources.FILE)) {
      File dir = new File(base.getPath());
      if (dir.isDirectory()) {
        String[] content = dir.list();
        String sep = "";
        disp.append("[");
        for (String fl : content) {
          disp.append(sep);
          sep = ",";
          disp.append(fl);
        }
        disp.append("]");
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public String getName()
  {
    return URIUtils.getPackageName(base);
  }

  @Override
  public ResourceURI resolve(String name) throws CatalogException
  {
    try {
      return starCatalog.resolve(name);
    } catch (CatalogException e) {
      for (String ext : StarCompiler.standardExtensions) {
        try {
          ResourceURI trial = base.resolve(name + ext);
          if (Resources.exists(trial))
            return trial;
        } catch (ResourceException ee) {
        }
      }

      if (fallback != null)
        return fallback.resolve(name);
      else
        throw new CatalogException(e.getMessage());
    }
  }

  @Override
  public ResourceURI resolveNoFallback(String name) throws ResourceException
  {
    return null;
  }

  @Override
  public ResourceURI resolve(ResourceURI uri) throws ResourceException
  {
    return base.resolve(uri);
  }

  @Override
  public void addEntry(String name, ResourceURI uri) throws CatalogException
  {
    throw new CatalogException("not permitted");
  }

}
