package org.star_lang.star.operators.string.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IPattern;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.NTuple;

/**
 * The regexp builtin pattern is used to implement pattern matching against lists
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
