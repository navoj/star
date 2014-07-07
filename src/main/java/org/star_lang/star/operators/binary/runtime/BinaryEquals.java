package org.star_lang.star.operators.binary.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.BinaryWrap;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.BinaryWrap.BinaryWrapper;
import org.star_lang.star.data.value.BinaryWrap.NonBinaryWrapper;
import org.star_lang.star.operators.CafeEnter;

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
public abstract class BinaryEquals implements IFunction
{

  @CafeEnter
  public static BoolWrap enter(BinaryWrap l, BinaryWrap r) throws EvaluationException
  {
    if (l instanceof BinaryWrapper && r instanceof BinaryWrapper) {
      Object lContent = ((BinaryWrapper) l).getValue();
      Object rContent = ((BinaryWrapper) r).getValue();

      if (lContent != null) {
        if (rContent != null)
          return Factory.newBool(lContent.equals(rContent));
        else
          return BoolWrap.falseEnum;
      } else
        return Factory.newBool(rContent == null);
    } else
      return Factory.newBool(l instanceof NonBinaryWrapper && r instanceof NonBinaryWrapper);
  }

  @Override
  public IType getType()
  {
    return TypeUtils.functionType(StandardTypes.binaryType, StandardTypes.binaryType, StandardTypes.booleanType);
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((BinaryWrap) args[0], (BinaryWrap) args[1]);
  }
}
