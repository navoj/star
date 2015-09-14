package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.StringWrap;
import org.star_lang.star.operators.CafeEnter;

/*
 * Implement display of stuff
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

public class DisplayTerm implements IFunction
{
  public static final String name = "__display";

  @CafeEnter
  public static StringWrap enter(IValue value) throws EvaluationException
  {
    return Factory.newString(ValueDisplay.display(value));
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter(args[0]);
  }

  @Override
  public IType getType()
  {
    return funType();
  }

  public static IType funType()
  {
    TypeVar tv = new TypeVar();
    return new UniversalType(tv, TypeUtils.functionType(tv, StandardTypes.stringType));
  }
}
