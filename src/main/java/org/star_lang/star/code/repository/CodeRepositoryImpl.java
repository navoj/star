package org.star_lang.star.code.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.CatalogException;


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

/**
 * Memory based code repository
 * 
 * @author fgm
 * 
 */

@SuppressWarnings("serial")
public class CodeRepositoryImpl extends AbstractCodeRepository implements PrettyPrintable
{
  private final Map<ResourceURI, RepositoryNode> nodes = new HashMap<>();
  private final RepositoryNode generics = new RepositoryNodeImpl(ResourceURI.noUriEnum, null, new CodeMemory());

  public CodeRepositoryImpl(ClassLoader loader, boolean isSolo, ErrorReport errors)
  {
    super(loader, errors);
    if (isSolo)
      setupStarLib();
  }

  @Override
  public RepositoryNode addRepositoryNode(ResourceURI uri, String hash, CodeCatalog code) throws RepositoryException
  {
    RepositoryNode node = new RepositoryNodeImpl(uri, hash, code);
    mergeStdTypes(code);
    nodes.put(uri, node);

    recordVersion(uri);
    triggerUpdates(node);

    return node;
  }

  @Override
  public void removeRepositoryNode(ResourceURI uri) throws CatalogException
  {
    RepositoryNode node = findNode(uri);
    if (node != null) {
      if (triggerDelete(node)) {
        nodes.remove(uri);
        removeVersion(uri);
      }
    }
  }

  @Override
  public RepositoryNode findNode(ResourceURI uri)
  {
    return nodes.get(uri);
  }

  @Override
  public CodeCatalog synthCodeCatalog()
  {
    return generics.getCode();
  }

  @Override
  public synchronized Iterator<RepositoryNode> iterator()
  {
    List<RepositoryNode> snapShot = new ArrayList<>(nodes.values());
    return snapShot.iterator();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("{\n");
    for (Entry<ResourceURI, RepositoryNode> entry : nodes.entrySet()) {
      entry.getKey().prettyPrint(disp);
      disp.append(":");
      entry.getValue().prettyPrint(disp);
      disp.append("\n");
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
