package org.star_lang.star.compiler;

import org.star_lang.star.compiler.type.TypeUtils;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;

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

public class SimpleFuns
{
  public static String javaFoo(int x, int y)
  {
    System.out.println("javaFoo called with x=" + x + ", y=" + y);
    return Integer.toString(x * y);
  }

  public static String javaString(int x)
  {
    System.out.println("calling " + x);
    return Integer.toString(x);
  }

  public static void doSomething(String s, double d)
  {
    System.out.println("We are supposed to " + s + " to " + d);
  }

  public static class ifunc implements IFunction
  {
    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      int i1 = Factory.intValue(args[0]);
      int i2 = Factory.intValue(args[1]);
      return Factory.newInt(i1 + i2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.integerType, StandardTypes.integerType, StandardTypes.integerType);
    }
  }
}
