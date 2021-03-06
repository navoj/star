package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;


/*
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

public class DisplayLocation implements IFunction
{
  public static final String name = "__display_location";

  @CafeEnter
  public static String enter(Location loc)
  {
    return loc.toString();
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return Factory.newString(enter((Location) args[0]));
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(Location.type, StandardTypes.rawStringType);
  }

}
