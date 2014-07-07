package org.star_lang.star.compiler;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.operators.Intrinsics;

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
public class UnifyTests
{
  @Test
  public void testInterfaceUnifications()
  {
    SortedMap<String, IType> mem1 = new TreeMap<String, IType>();
    mem1.put("name", StandardTypes.stringType);
    mem1.put("age", StandardTypes.integerType);

    IType t1 = TypeUtils.typeInterface(mem1);

    SortedMap<String, IType> mem2 = new TreeMap<String, IType>();
    mem2.put("name", StandardTypes.stringType);
    mem2.put("address", StandardTypes.stringType);
    IType t2 = TypeUtils.typeInterface(mem2);

    try {
      TypeUtils.unify(t1, t2, Location.nullLoc, Intrinsics.intrinsics());
      Assert.fail();
    } catch (TypeConstraintException e) {
      // Ok.
    }
  }
}
