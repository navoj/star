package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.TypeInterfaceType;

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
public class OpenStatement implements IStatement
{
  private final IContentExpression record;
  private final TypeInterfaceType face;
  private final Location loc;
  private final Visibility visibility;

  public OpenStatement(Location loc, IContentExpression record, TypeInterfaceType face, Visibility visibility)
  {
    this.loc = loc;
    this.visibility = visibility;
    this.record = record;
    this.face = face;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  public IContentExpression getRecord()
  {
    return record;
  }

  public TypeInterfaceType getFace()
  {
    return face;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOpenStatement(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (visibility == Visibility.priVate)
      disp.appendWord(StandardNames.PRIVATE);
    disp.appendWord(StandardNames.OPEN);
    record.prettyPrint(disp);
  }

  @Override
  public boolean defines(String name)
  {
    return face.getAllFields().containsKey(name) || face.getAllTypes().containsKey(name);
  }

  @Override
  public Collection<String> definedFields()
  {
    return face.getAllFields().keySet();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return face.getAllTypes().keySet();
  }

  @Override
  public Visibility getVisibility()
  {
    return visibility;
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOpenStatement(this, context);
  }

}
