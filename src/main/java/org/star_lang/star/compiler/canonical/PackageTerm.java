package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeContract;
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
public class PackageTerm implements Canonical
{
  private final String name;
  private final String pkgName;
  private final IType pkgType;
  private final List<TypeDefinition> types;
  private final List<ITypeAlias> aliases;
  private final List<TypeContract> contracts;
  private final List<Pair<Location, ResourceURI>> imports;
  private final Location loc;
  private final IContentExpression pkgValue;
  private final ResourceURI uri;

  public PackageTerm(Location loc, String name, String pkgName, IType pkgType, IContentExpression pkgValue,
      List<TypeDefinition> types, List<ITypeAlias> aliases, List<TypeContract> contracts,
      List<Pair<Location, ResourceURI>> imports, ResourceURI uri)
  {
    this.loc = loc;
    this.name = name;
    this.pkgName = pkgName;
    this.pkgType = pkgType;
    this.pkgValue = pkgValue;
    this.types = types;
    this.aliases = aliases;
    this.contracts = contracts;
    this.imports = imports;
    this.uri = uri;

    assert pkgName != null;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  public String getName()
  {
    return name;
  }

  public String getPkgName()
  {
    return pkgName;
  }

  public IType getPkgType()
  {
    return pkgType;
  }

  public List<TypeDefinition> getTypes()
  {
    return types;
  }

  public boolean isExportedType(String name)
  {
    for (TypeDefinition type : types)
      if (type.getName().equals(name))
        return true;
    return false;
  }

  public List<ITypeAlias> getAliases()
  {
    return aliases;
  }

  public boolean isExportedAlias(String name)
  {
    for (ITypeAlias type : aliases)
      if (type.getName().equals(name))
        return true;
    return false;
  }

  public List<TypeContract> getContracts()
  {
    return contracts;
  }

  public boolean isExportedContract(String name)
  {
    for (TypeContract contract : contracts)
      if (contract.getName().equals(name))
        return true;
    return false;
  }

  public IContentExpression getPkgValue()
  {
    return pkgValue;
  }

  public List<Pair<Location, ResourceURI>> getImports()
  {
    return imports;
  }

  public ResourceURI getUri()
  {
    return uri;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("package: ").append(pkgName).append("\n");

    pkgValue.prettyPrint(disp);

    disp.append("\n  ");
    int mark = disp.markIndent();

    for (TypeDefinition entry : types) {
      entry.prettyPrint(disp);
      disp.append("\n");
    }

    for (ITypeAlias entry : aliases) {
      entry.prettyPrint(disp);
      disp.append("\n");
    }

    disp.popIndent(mark);
    disp.append("\n");
    disp.appendWord("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    assert false : "should not have done this";
  }
}
