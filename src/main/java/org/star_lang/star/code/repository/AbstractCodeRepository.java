package org.star_lang.star.code.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.star_lang.star.StarMake;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.sources.JavaImport;
import org.star_lang.star.compiler.sources.JavaInfo;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.URIUtils;

/**
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
public abstract class AbstractCodeRepository implements CodeRepository
{
  private final List<RepositoryListener> listeners = new ArrayList<>();
  private final Map<String, JavaInfo> javaInfo = new HashMap<>();
  private final Map<ResourceURI, SortedSet<Version>> latest = new HashMap<>();
  protected final ClassLoader outerLoader;
  protected RepositoryClassLoader loader;
  protected final ErrorReport errors;

  protected AbstractCodeRepository(ClassLoader outerLoader, ErrorReport errors)
  {
    this.outerLoader = outerLoader;
    this.errors = errors;
    this.loader = new RepositoryClassLoader(this, outerLoader);
    addListener(loader);
  }

  protected void setupStarLib()
  {
    StarMake.setupRepository(this, errors);
  }

  @Override
  public void addListener(RepositoryListener listener)
  {
    if (!listeners.contains(listener))
      listeners.add(listener);
  }

  @Override
  public void removeListener(RepositoryListener listener)
  {
    listeners.remove(listener);
  }

  protected void triggerUpdates(RepositoryNode node)
  {
    for (RepositoryListener listener : listeners)
      listener.nodeUpdated(node);
  }

  protected boolean triggerDelete(RepositoryNode node)
  {
    for (RepositoryListener listener : listeners)
      if (!listener.removeNode(node))
        return false;
    return true;
  }

  @Override
  public JavaInfo locateJava(String className) throws RepositoryException
  {
    JavaInfo pkg = javaInfo.get(className);

    if (pkg == null) {
      pkg = JavaImport.importJavaSchema(className, outerLoader, errors);
      if (pkg == null)
        throw new RepositoryException("cannot find java class " + className);
      javaInfo.put(className, pkg);
    }
    return pkg;
  }

  @Override
  public RepositoryClassLoader classLoader()
  {
    return loader;
  }

  // Reload the class loader to get rid of old class files
  public RepositoryClassLoader refreshClassLoader()
  {
    removeListener(loader);
    this.loader = new RepositoryClassLoader(this, outerLoader);
    addListener(this.loader);
    for (RepositoryNode node : this)
      this.loader.nodeUpdated(node);
    return loader;
  }

  @Override
  public ITypeContext loaderContext(ResourceURI pkg)
  {
    return new CodeDictionary(this, loader, pkg, errors);
  }

  @Override
  public RepositoryNode findLatestNode(ResourceURI uri)
  {
    if (URIUtils.hasKeyword(uri, StandardNames.VERSION))
      return findNode(uri);
    else if (latest.containsKey(uri))
      return findNode(URIUtils.setKeyword(uri, StandardNames.VERSION, latest.get(uri).last().get()));
    else
      return findNode(uri);
  }

  @Override
  public CodeTree findCode(ResourceURI uri)
  {
    RepositoryNode node = findLatestNode(uri);
    if (node != null)
      return node.getCode();
    return null;
  }

  @Override
  public String findHash(ResourceURI uri)
  {
    RepositoryNode node = findLatestNode(uri);
    if (node != null)
      return node.getHash();
    else
      return null;
  }

  protected void recordVersion(ResourceURI uri)
  {
    if (URIUtils.hasKeyword(uri, StandardNames.VERSION)) {
      Version version = new Version(URIUtils.queryKeyword(uri, StandardNames.VERSION));
      ResourceURI versionFree = URIUtils.stripKeyword(uri, StandardNames.VERSION);

      SortedSet<Version> lUri = latest.get(versionFree);
      if (lUri == null) {
        lUri = new TreeSet<>();
        lUri.add(version);
        latest.put(versionFree, lUri);
      } else
        lUri.add(version);
    }
  }

  protected void removeVersion(ResourceURI uri)
  {
    if (URIUtils.hasKeyword(uri, StandardNames.VERSION)) {
      ResourceURI versionFree = URIUtils.stripKeyword(uri, StandardNames.VERSION);
      if (latest.containsKey(versionFree)) {
        SortedSet<Version> versions = latest.get(versionFree);
        versions.remove(new Version(URIUtils.queryKeyword(uri, StandardNames.VERSION)));
        if (versions.isEmpty())
          latest.remove(versionFree);
      }
    }
  }

  protected void mergeStdTypes(CodeCatalog code) throws RepositoryException
  {
    CodeCatalog genCat = synthCodeCatalog();

    CodeTree compiledCode = ((CodeCatalog) code).resolve(RepositoryManager.COMPILED, null);

    if (compiledCode instanceof CodeCatalog) {
      for (Entry<String, CodeTree> entry : (CodeCatalog) compiledCode) {
        String name = entry.getKey();
        if (Types.isStdType(name) && !genCat.isPresent(name))
          genCat.addCodeEntry(name, entry.getValue());
      }
    }
  }
}
