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
 * A CodeRepository should contain all the code of a project. It is not set up as a singleton; but
 * should be treated as such.
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
/**
 * Memory based code repository
 * 
 * @author fgm
 * 
 */

@SuppressWarnings("serial")
public class CodeRepositoryImpl extends AbstractCodeRepository implements PrettyPrintable
{
  private final Map<ResourceURI, RepositoryNode> nodes = new HashMap<ResourceURI, RepositoryNode>();
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
