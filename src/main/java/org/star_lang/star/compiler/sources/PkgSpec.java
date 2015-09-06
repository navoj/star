package org.star_lang.star.compiler.sources;

import java.util.Collection;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.Catalog;

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
public class PkgSpec implements PrettyPrintable
{
  private final Collection<ResourceURI> dependants;
  private final ResourceURI uri;
  private final Catalog catalog;
  private final String srcText;
  private final String srcHash;

  public PkgSpec(ResourceURI uri, String srcText, String srcHash, Catalog catalog, Collection<ResourceURI> dependants)
  {
    this.dependants = dependants;
    this.uri = uri;
    this.srcText = srcText;
    this.srcHash = srcHash;
    this.catalog = catalog;
  }

  public Collection<ResourceURI> getDeps()
  {
    return dependants;
  }

  public ResourceURI getUri()
  {
    return uri;
  }

  public String getSrcText()
  {
    return srcText;
  }

  public String getSrcHash()
  {
    return srcHash;
  }

  public Catalog getCatalog()
  {
    return catalog;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    uri.prettyPrint(disp);
    if (!dependants.isEmpty()) {
      disp.append("<-[");
      disp.prettyPrint(dependants, ",");
      disp.append("]");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
