package org.star_lang.star.operators.misc.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.BoolWrap;
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
public class MiscOps
{
  public static class IsNull implements IFunction
  {

    @CafeEnter
    public static BoolWrap enter(IValue arg)
    {
      return Factory.newBool(arg == null);
    }

    public static final String name = "__isNull";

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(tv, StandardTypes.booleanType));
    }
  }

  public static class Id implements IFunction
  {
    public static final String name = "__id";

    @CafeEnter
    public static IValue enter(IValue x)
    {
      return x;
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
      TypeVar t = new TypeVar();
      return new UniversalType(t, TypeUtils.functionType(t, t));
    }
  }
}
