package org.star_lang.star.compiler;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;

/**
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
