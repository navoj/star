package org.star_lang.star.compiler.type;

import java.util.Set;
import java.util.TreeSet;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.Type;
import com.starview.platform.data.type.TypeVar;

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
public class BindingLocations extends AbstractTypeVisitor<Set<Location>>
{
  private Set<Location> bindingLocations = new TreeSet<Location>();

  public static Set<Location> bindingLocations(IType type)
  {
    BindingLocations visitor = new BindingLocations();
    type.accept(visitor, visitor.bindingLocations);
    return visitor.bindingLocations;
  }

  @Override
  public void visitSimpleType(Type t, Set<Location> cxt)
  {
  }

  @Override
  public void visitTypeVar(TypeVar v, Set<Location> cxt)
  {
    if (v.isBound()) {
      Location loc = v.getBindingLocation();
      if (loc != null)
        bindingLocations.add(loc);
      v.getBoundValue().accept(this, cxt);
    }
  }
}
