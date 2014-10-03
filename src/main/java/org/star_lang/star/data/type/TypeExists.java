package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;

/**
 * The TypeExists class is used to represent cases where there is a type but it is opaquely defined.
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
public class TypeExists implements ITypeDescription
{
  private final Location loc;
  private final String name;
  private final IType type;

  public TypeExists(Location loc, String name, Kind kind)
  {
    this.loc = loc;
    this.name = name;
    this.type = new TypeVar();
    ((TypeVar) type).setConstraint(new HasKind((TypeVar) type, kind));
  }

  public TypeExists(Location loc, String name, IType type)
  {
    this.loc = loc;
    this.name = name;
    this.type = type;
    assert type != null && name != null;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(name);
    disp.appendWord("=");
    DisplayType.display(disp, type);
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
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public int typeArity()
  {
    return type.kind().arity();
  }

  @Override
  public Kind kind()
  {
    return type.kind();
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public IType verifyType(IType type, Location loc, Dictionary dict) throws TypeConstraintException
  {
    Kind kind = this.type.kind();

    if (kind != Kind.unknown) {
      if (kind == Kind.type) {
        if (!(type instanceof TypeVar || type instanceof Type))
          throw new TypeConstraintException(StringUtils.msg(type, "not consistent with ", this.type), loc);
      } else if (type instanceof TypeExp) {
        if (((TypeExp) type).typeArity() != kind.arity())
          throw new TypeConstraintException(StringUtils.msg(type, "not consistent with ", this.type), loc);
      } else
        throw new TypeConstraintException(StringUtils.msg(type, "not consistent with ", this.type), loc);
    }
    return type;
  }
}
