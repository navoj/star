package org.star_lang.star.operators.string.runtime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.Option;
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
public class DateOps
{
  public static class Date2String implements IFunction
  {

    @CafeEnter
    public static String enter(long date) throws EvaluationException
    {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(date));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawStringType);
    }
  }

  public static class FormatDate implements IFunction
  {

    @CafeEnter
    public static String enter(long date, String format) throws EvaluationException
    {
      DateFormat formatter = new SimpleDateFormat(format);
      return formatter.format(new Date(date));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(enter(Factory.lngValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      return TypeUtils
          .functionType(StandardTypes.rawLongType, StandardTypes.rawStringType, StandardTypes.rawStringType);
    }
  }

  public static class String2Date implements IFunction
  {

    @CafeEnter
    public static IValue enter(String date) throws EvaluationException
    {
      try {
        return Option.some(Factory.newLng(DateFormat.getInstance().parse(date).getTime()));
      } catch (ParseException e) {
        try {
          return Option.some(Factory.newLng(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
              .parse(date).getTime()));
        } catch (ParseException ee) {
          return Option.noneEnum;
        }
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, TypeUtils.optionType(StandardTypes.longType));
    }
  }

  public static class ParseDate implements IFunction
  {

    @CafeEnter
    public static long enter(String date, String format) throws EvaluationException
    {
      try {
        DateFormat formatter = new SimpleDateFormat(format);

        return formatter.parse(date).getTime();
      } catch (ParseException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter(Factory.stringValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      return TypeUtils
          .functionType(StandardTypes.rawStringType, StandardTypes.rawStringType, StandardTypes.rawLongType);
    }
  }

  public static void main(String args[])
  {
    String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(Long.MAX_VALUE));
    System.out.println(date);
  }
}
