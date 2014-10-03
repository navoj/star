package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

/**
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


public abstract class BoolCompare
{
  private static final IType rawBoolType = StandardTypes.rawBoolType;

  public static class BoolEQ implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(boolean ix1, boolean ix2)
    {
      return Factory.newBool(ix1 == ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.boolValue(args[0]), Factory.boolValue(args[1]));
    }
  }

  public static class BoolNE implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(boolean ix1, boolean ix2)
    {
      return Factory.newBool(ix1 != ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.boolValue(args[0]), Factory.boolValue(args[1]));
    }
  }

  public static class BoolLE implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(boolean ix1, boolean ix2)
    {
      return Factory.newBool(ix1 ? ix2 : true); // ix1<=ix2
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.boolValue(args[0]), Factory.boolValue(args[1]));
    }
  }

  public static class BoolLT implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(boolean ix1, boolean ix2)
    {
      return Factory.newBool(ix1 ? false : ix2); // ix1<ix2
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.boolValue(args[0]), Factory.boolValue(args[1]));
    }
  }

  public static class BoolGT implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(boolean ix1, boolean ix2)
    {
      return Factory.newBool(ix1 ? ix2 : false);// ix1>ix2
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.boolValue(args[0]), Factory.boolValue(args[1]));
    }
  }

  public static class BoolGE implements IFunction
  {
    @CafeEnter
    public BoolWrap enter(boolean ix1, boolean ix2)
    {
      return Factory.newBool(ix1 ? true : ix2); // ix1>=ix2
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.boolValue(args[0]), Factory.boolValue(args[1]));
    }
  }
}
