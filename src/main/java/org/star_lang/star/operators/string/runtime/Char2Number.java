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
 * Runtime parts of the String2Number functions that parse strings into different kinds of numbers
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

public class Char2Number
{
  public static class Char2Int implements IFunction
  {
    public static final String name = "__char_integer";

    @CafeEnter
    public static int enter(int ch)
    {
      return ch;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawCharType, StandardTypes.rawIntegerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.charValue(args[0])));
    }
  }

  public static class Int2Char implements IFunction
  {
    public static final String name = "__integer_char";

    @CafeEnter
    public static int enter(int ch)
    {
      return ch;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawCharType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newChar(enter(Factory.intValue(args[0])));
    }
  }
}
