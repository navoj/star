package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
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
public abstract class CharCompare
{
  private static final IType rawCharType = StandardTypes.rawCharType;
  private static final IType booleanType = StandardTypes.booleanType;

  public static class CharEQ implements IFunction
  {
    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 == ix2;
    }

    public static final String name = "__char_eq";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharNE implements IFunction
  {
    public static final String name = "__char_ne";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 != ix2;
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharLE implements IFunction
  {
    public static final String name = "__char_le";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 <= ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharLT implements IFunction
  {
    public static final String name = "__char_lt";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 < ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharGT implements IFunction
  {
    public static final String name = "__char_gt";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 > ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharGE implements IFunction
  {
    public static final String name = "__char_ge";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 >= ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharMin implements IFunction
  {
    public static final String name = "__char_min";

    @CafeEnter
    public static int enter(int ix1, int ix2)
    {
      return ix1 > ix2 ? ix2 : ix1;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, rawCharType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newChar(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

}
