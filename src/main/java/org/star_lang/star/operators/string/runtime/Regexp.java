package org.star_lang.star.operators.string.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.operators.CafeEnter;

/*
 * The regexp builtin pattern is used to implement pattern matching against lists
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


public class Regexp implements IPattern
{
  private final Pattern pattern;
  public static final String name = "__regexpMatch";

  public Regexp(String regexp)
  {
    if (regexp != null)
      this.pattern = Pattern.compile(regexp, java.util.regex.Pattern.DOTALL);
    else
      this.pattern = null;
  }

  @CafeEnter
  public String[] match(String tgt)
  {
    Matcher matcher = pattern.matcher(tgt);

    if (matcher.matches()) {
      String[] result = new String[matcher.groupCount()];

      for (int ix = 0; ix < matcher.groupCount(); ix++)
        result[ix] = matcher.group(ix + 1);

      return result;
    } else
      return null;
  }

  @Override
  public IValue match(IValue arg) throws EvaluationException
  {
    Matcher matcher = pattern.matcher(Factory.stringValue(arg));

    if (matcher.matches()) {
      IValue[] result = new IValue[matcher.groupCount()];

      for (int ix = 0; ix < matcher.groupCount(); ix++)
        result[ix] = Factory.newString(matcher.group(ix + 1));

      return NTuple.tuple(result);
    } else
      return null;
  }

  @Override
  public IType getType()
  {
    return TypeUtils.patternType(TypeUtils.tupleType(), StandardTypes.stringType);
  }
}
