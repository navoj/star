/**
 *
 */
package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.*;
import org.star_lang.star.data.value.ResourceURI;

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
public abstract class EnvironmentEntry implements IStatement
{
  final Location loc;
  final private Visibility visibility;

  public EnvironmentEntry(Location loc, Visibility visibility)
  {
    this.loc = loc;
    this.visibility = visibility;
  }

  @Override
  public String toString()
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    prettyPrint(disp);
    disp.append("@");
    loc.prettyPrint(disp);
    return disp.toString();
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public Visibility getVisibility()
  {
    return visibility;
  }

  public static EnvironmentEntry createTypeEntry(String name, Location loc, IType type, IAlgebraicType algebraicType,
      Visibility visibility, boolean imported, boolean fromContract)
  {
    return new TypeDefinition(name, loc, type, algebraicType, visibility, imported, fromContract);
  }

  public static class TypeAliasEntry extends EnvironmentEntry
  {
    final private ITypeAlias typeAlias;
    final private String name;

    public TypeAliasEntry(String name, Location loc, ITypeAlias typeAlias, Visibility visibility)
    {
      super(loc, visibility);
      this.name = name;
      this.typeAlias = typeAlias;
    }

    public String getName()
    {
      return name;
    }

    public ITypeAlias getTypeAlias()
    {
      return typeAlias;
    }

    @Override
    public void accept(CanonicalVisitor visitor)
    {
      visitor.visitTypeAliasEntry(this);
    }

    @Override
    public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
    {
      return transform.transformTypeAliasEntry(this, context);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      typeAlias.prettyPrint(disp);
    }

    @Override
    public boolean defines(String name)
    {
      return this.name.equals(name);
    }

    @Override
    public Collection<String> definedFields()
    {
      return FixedList.create();
    }

    @Override
    public Collection<String> definedTypes()
    {
      return FixedList.create(name);
    }
  }

  public static EnvironmentEntry createTypeAliasEntry(String name, Location loc, ITypeAlias typeAlias,
      Visibility visibility)
  {
    return new TypeAliasEntry(name, loc, typeAlias, visibility);
  }

  public static class ContractEntry extends EnvironmentEntry
  {
    private final TypeContract contract;
    private final String name;

    public ContractEntry(String name, Location loc, TypeContract contract, Visibility visibility)
    {
      super(loc, visibility);
      this.name = name;
      this.contract = contract;
    }

    public TypeContract getContract()
    {
      return contract;
    }

    public String getName()
    {
      return name;
    }

    @Override
    public boolean defines(String name)
    {
      return this.name.equals(name);
    }

    @Override
    public Collection<String> definedTypes()
    {
      return FixedList.create();
    }

    @Override
    public Collection<String> definedFields()
    {
      return FixedList.create(name);
    }

    @Override
    public void accept(CanonicalVisitor visitor)
    {
      visitor.visitContractEntry(this);
    }

    @Override
    public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
    {
      return transform.transformContractDefn(this, context);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      contract.prettyPrint(disp);
    }
  }

  public static ContractEntry createContractEntry(Location loc, String name, TypeContract contract,
      Visibility visibility)
  {
    return new ContractEntry(name, loc, contract, visibility);
  }

  public static class ImplementationEntry extends EnvironmentEntry
  {
    private final ContractImplementation implementation;

    public ImplementationEntry(Location loc, ContractImplementation implementation, Visibility visibility)
    {
      super(loc, visibility);
      this.implementation = implementation;
    }

    public ContractImplementation getImplementation()
    {
      return implementation;
    }

    @Override
    public boolean defines(String name)
    {
      return false;
    }

    @Override
    public Collection<String> definedFields()
    {
      return FixedList.create(implementation.getImplementation().getName());
    }

    @Override
    public Collection<String> definedTypes()
    {
      return FixedList.create();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      implementation.prettyPrint(disp);
    }

    @Override
    public void accept(CanonicalVisitor visitor)
    {
      visitor.visitContractImplementation(this);
    }

    @Override
    public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
    {
      return transform.transformContractImplementation(this, context);
    }
  }

  public static class ImportEntry extends EnvironmentEntry
  {
    private final String pkgName;
    private final IType pkgType;
    private final ResourceURI uri;

    public ImportEntry(Location loc, String name, IType pkgType, ResourceURI uri, Visibility visibility)
    {
      super(loc, visibility);
      this.pkgName = name;
      this.pkgType = pkgType;
      this.uri = uri;
      assert name != null;
    }

    public String getPkgName()
    {
      return pkgName;
    }

    public IType getPkgType()
    {
      return pkgType;
    }

    public ResourceURI getUri()
    {
      return uri;
    }

    @Override
    public boolean defines(String name)
    {
      return pkgName.equals(name);
    }

    @Override
    public Collection<String> definedFields()
    {
      return FixedList.create(pkgName);
    }

    @Override
    public Collection<String> definedTypes()
    {
      return FixedList.create();
    }

    @Override
    public void accept(CanonicalVisitor visitor)
    {
      visitor.visitImportEntry(this);
    }

    @Override
    public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
    {
      return transform.transformImportEntry(this, context);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(StandardNames.IMPORT);
      disp.appendWord(pkgName);
    }
  }
}
