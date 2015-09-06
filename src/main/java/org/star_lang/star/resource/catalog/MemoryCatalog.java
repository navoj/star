package org.star_lang.star.resource.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
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
public class MemoryCatalog implements Catalog, CatalogListenable
{
  private final Map<String, ResourceURI> contents = new HashMap<>();
  private final List<CatalogListener> listeners = new ArrayList<>();
  private final String name;
  private final String version;
  private final ResourceURI base;
  private final Catalog fallback;

  public MemoryCatalog(String name, ResourceURI base)
  {
    this(name, null, base, null, null);
  }

  public MemoryCatalog(String name, String version, ResourceURI base, Catalog fallback, Map<String, ResourceURI> entries)
  {
    this.name = name;
    this.version = version;
    this.base = base;
    this.fallback = fallback;
    if (entries != null)
      contents.putAll(entries);
  }

  @Override
  public String getVersion()
  {
    return version;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(name);
    disp.appendWord(StandardNames.IS);
    int mark1 = disp.markIndent(2);
    disp.appendWord("catalog{\n");
    int mark2 = disp.markIndent(2);

    disp.append("content is hash{\n");
    for (Entry<String, ResourceURI> entry : contents.entrySet()) {
      disp.appendQuoted(entry.getKey());
      disp.append(" -> ");
      disp.appendQuoted(entry.getValue().toString());
      disp.append(";\n");
    }
    disp.popIndent(mark2);
    disp.append("}\n");
    disp.popIndent(mark1);
    disp.append("}\n");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public ResourceURI resolve(String name) throws CatalogException
  {
    ResourceURI uri = contents.get(name);
    if (uri != null)
      try {
        if (base != null)
          return base.resolve(uri);
        else
          return uri;
      } catch (ResourceException e) {
        throw new CatalogException(name + " not in catalog");
      }
    else if (fallback != null)
      return fallback.resolve(name);
    else
      throw new CatalogException(name + " not in catalog");
  }

  @Override
  public ResourceURI resolve(ResourceURI uri) throws ResourceException
  {
    if (base != null)
      return base.resolve(uri);
    else if (fallback != null)
      return fallback.resolve(uri);
    else
      return uri;
  }

  @Override
  public ResourceURI resolveNoFallback(String name) throws ResourceException
  {
    return base.resolve(contents.get(name));
  }

  @Override
  public void addEntry(String name, ResourceURI uri) throws CatalogException
  {
    contents.put(name, uri);
    pushListener(name, uri);
  }

  @Override
  public void addListener(CatalogListener listener)
  {
    if (!listeners.contains(listener))
      listeners.add(listener);
  }

  @Override
  public void removeListener(CatalogListener listener)
  {
    listeners.remove(listener);
  }

  private void pushListener(String name, ResourceURI uri)
  {
    for (CatalogListener listener : listeners)
      listener.addCatalogEntry(name, uri);
  }

  public Collection<ResourceURI> entries()
  {
    return contents.values();
  }
}
