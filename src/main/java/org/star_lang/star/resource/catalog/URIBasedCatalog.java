package org.star_lang.star.resource.catalog;

import java.io.File;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;
/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
