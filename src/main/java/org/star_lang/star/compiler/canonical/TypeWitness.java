package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class TypeWitness extends EnvironmentEntry
{
  private final IType type;
  private final IType witness;

  public TypeWitness(Location loc, IType type, IType witness, Visibility visibility)
  {
    super(loc, visibility);
    this.type = type;
    this.witness = witness;
  }

  public IType getType()
  {
    return type;
  }

  public IType getWitness()
  {
    return witness;
  }

  @Override
  public boolean defines(String name)
  {
    return name.equals(this.type.typeLabel());
  }

  @Override
  public Collection<String> definedFields()
  {
    return FixedList.create();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return FixedList.create(type.typeLabel());
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformWitness(this, context);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitTypeWitness(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (getVisibility() == Visibility.priVate)
      disp.appendWord(StandardNames.PRIVATE);
    disp.appendWord(StandardNames.TYPE);
    DisplayType tpDisp = new DisplayType(disp);
    witness.accept(tpDisp, null);
    disp.appendWord(StandardNames.COUNTS_AS);
    type.accept(tpDisp, null);
  }

}
