package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

/*
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
 */
/**
 * The definition of an algebraic type
 * 
 * @author fgm
 * 
 */
@SuppressWarnings("serial")
public class TypeDefinition extends EnvironmentEntry
{
  final IType type;
  final IAlgebraicType typeDescription;
  final private String name;
  final private boolean imported;
  final private boolean fromContract;

  public TypeDefinition(String name, Location loc, IType type, IAlgebraicType typeDescription, Visibility visibility,
      boolean imported, boolean fromContract)
  {
    super(loc, visibility);
    this.type = type;
    this.name = name;
    this.typeDescription = typeDescription;
    this.imported = imported;
    this.fromContract = fromContract;
  }

  public String getName()
  {
    return name;
  }

  public IType getType()
  {
    return type;
  }

  public boolean isImported()
  {
    return imported;
  }

  public boolean isFromContract()
  {
    return fromContract;
  }

  public IAlgebraicType getTypeDescription()
  {
    return typeDescription;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitTypeEntry(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformTypeEntry(this, context);
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

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    typeDescription.prettyPrint(disp);
  }
}