package org.star_lang.star.operators.binary.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.BinaryWrap;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.StringWrap;
import com.starview.platform.data.value.BinaryWrap.BinaryWrapper;

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
public abstract class BinaryCoercion
{

  public static class Binary2String implements IFunction
  {
    @CafeEnter
    public static IValue enter(BinaryWrap bin) throws EvaluationException
    {
      if (bin instanceof BinaryWrapper) {
        Object content = ((BinaryWrapper) bin).getValue();
        if (content instanceof String)
          return Factory.newString((String) content);
        else
          throw new EvaluationException("not a string");
      } else
        return StringWrap.nonStringEnum;
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.binaryType, StandardTypes.stringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((BinaryWrap) args[0]);
    }
  }

  public static class String2Binary implements IFunction
  {
    @CafeEnter
    public static IValue enter(StringWrap str) throws EvaluationException
    {
      if (str == null || str instanceof StringWrap.NonStringWrapper)
        return BinaryWrap.nonBinaryEnum;
      else
        return Factory.newBinary(Factory.stringValue(str));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.binaryType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((StringWrap) args[0]);
    }
  }
}
