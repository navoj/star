package org.star_lang.star.code.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.code.repository.CodeRepository.RepositoryListener;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeManifest;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.cafe.type.ICafeConstructorSpecifier;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.Intrinsics;

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
@SuppressWarnings("serial")
public class CodeDictionary implements ITypeContext, RepositoryListener{
  private final CodeRepository repository;
  private final Map<String, CafeTypeDescription> types = new HashMap<String, CafeTypeDescription>();
  private final ClassLoader loader;
  private final ResourceURI uri; // scope-defining uri
  private final Set<ResourceURI> packages = new TreeSet<ResourceURI>();
  private final List<ResourceURI> refreshing = new ArrayList<ResourceURI>();
  private final ErrorReport errors;

  public CodeDictionary(CodeRepository repository, ClassLoader loader, ResourceURI uri,ErrorReport errors)
  {
    this.repository = repository;
    this.loader = loader;
    this.uri = uri;
    this.errors = errors;
    findPackages(uri);
    populateTypes(packages);
    repository.addListener(this);
  }

  @Override
  public ITypeDescription getTypeDescription(String name)
  {
    synchronized (refreshing) {
      if (!refreshing.isEmpty()) {
        synchronized (types) {
          populateTypes(refreshing);
        }
        refreshing.clear();
      }
    }

    synchronized (types) {
      CafeTypeDescription desc = types.get(name);

      if (desc != null) {
        populateDescription(desc);
        return desc;
      }
    }

    return Intrinsics.intrinsics().getTypeDescription(name);
  }

  @Override
  public boolean typeExists(String name)
  {
    return getTypeDescription(name) != null;
  }

  private void populateDescription(IAlgebraicType desc)
  {
    for (IValueSpecifier spec : desc.getValueSpecifiers()) {
      if (spec instanceof ICafeConstructorSpecifier) {
        ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) spec;
        if (con.getCafeClass() == null)
          try {
            con.setCafeClass(loader.loadClass(Utils.javaPublicName(con.getJavaType())));
          } catch (ClassNotFoundException e) {
            errors.reportWarning("problem in populating dictionary: " + e.getMessage());
          }
      }
    }
  }

  @Override
  public void nodeUpdated(RepositoryNode node)
  {
    synchronized (refreshing) {
      refreshing.add(node.getUri());
    }
  }

  @Override
  public boolean removeNode(RepositoryNode node)
  {
    synchronized (refreshing) {
      refreshing.add(node.getUri());
    }
    return true;
  }

  private void findPackages(ResourceURI uri)
  {
    if (uri != null && !packages.contains(uri)) {
      packages.add(uri);
      CafeManifest manifest = RepositoryManager.locateCafeManifest(repository, uri);
      if (manifest != null) {
        for (ResourceURI impUri : manifest.getImports())
          findPackages(impUri);
      }
    }
  }

  private void populateTypes(Collection<ResourceURI> refreshing)
  {
    for (ResourceURI uri : refreshing) {
      CafeManifest manifest = RepositoryManager.locateCafeManifest(repository, uri);
      if (manifest != null) {
        for (Entry<String, CafeTypeDescription> tEntry : manifest.getCleanedTypes().entrySet()) {
          String typeName = tEntry.getKey();
          // System.out.println("loading type " + typeName);
          if (!types.containsKey(typeName))
            types.put(typeName, tEntry.getValue());
        }
      }
    }
  }

  @Override
  public void defineType(ITypeDescription desc)
  {
    if (desc instanceof CafeTypeDescription)
      types.put(desc.getName(), (CafeTypeDescription) desc);
  }

  public ClassLoader getLoader()
  {
    return loader;
  }

  @Override
  public boolean isConstructor(String name)
  {
    return Intrinsics.intrinsics().isConstructor(name);
  }

  @Override
  public IValueSpecifier getConstructor(String name)
  {
    return Intrinsics.intrinsics().getConstructor(name);
  }

  @Override
  public void declareConstructor(ConstructorSpecifier cons)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void defineTypeAlias(Location loc, ITypeAlias alias)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public ITypeContext fork()
  {
    return new CodeDictionary(repository, loader, uri, errors);
  }

  @Override
  public TypeContract getContract(String name)
  {
    return Intrinsics.intrinsics().getContract(name);
  }

  @Override
  public Map<String, TypeContract> allContracts()
  {
    return Intrinsics.intrinsics().allContracts();
  }

  @Override
  public void defineTypeContract(TypeContract contract)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public Map<String, ITypeDescription> getAllTypes()
  {
    return null;
  }

  /**
   * Call close when this dictionary no longer needs to listen to the repository
   */
  public void close()
  {
    repository.removeListener(this);
  }

  @Override
  protected void finalize() throws Throwable
  {
    close();
    super.finalize();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    String sep = "";
    disp.append("Repository dictionary\n");
    for (Entry<String, CafeTypeDescription> entry : types.entrySet()) {
      disp.append(sep);
      sep = "\n";
      ITypeDescription desc = entry.getValue();
      if (desc instanceof CafeTypeDescription) {
        CafeTypeDescription cafeDesc = (CafeTypeDescription) desc;
        disp.append(cafeDesc.getTypeLabel());
        disp.append(" --> ");
        disp.append(cafeDesc.getJavaName());
      } else
        desc.prettyPrint(disp);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
