package org.star_lang.star.data.value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.value.BigNumWrap.BigNumWrapper;
import org.star_lang.star.data.value.BigNumWrap.NonDecimalWrapper;
import org.star_lang.star.data.value.BinaryWrap.BinaryWrapper;
import org.star_lang.star.data.value.BinaryWrap.NonBinaryWrapper;
import org.star_lang.star.data.value.CharWrap.CharWrapper;
import org.star_lang.star.data.value.FloatWrap.FloatWrapper;
import org.star_lang.star.data.value.IntWrap.IntWrapper;
import org.star_lang.star.data.value.LongWrap.LongWrapper;
import org.star_lang.star.data.value.StringWrap.NonStringWrapper;
import org.star_lang.star.data.value.StringWrap.StringWrapper;

/**
 * The Factory has two roles: to support the creation of values and to support accessing scalar
 * values.
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
public class Factory
{
  public static final IValue trueValue = BoolWrap.trueEnum;
  public static final IValue falseValue = BoolWrap.falseEnum;

  /**
   * Construct a wrapped boolean value. Since boolean is essentially an enumeration, this factory
   * function simply returns one of two publicly available instances of BoolWrap.
   * 
   * @param trueVal
   * @return truth if {@code trueVal} otherwise {@code falseness}.
   */
  public static BoolWrap newBool(boolean trueVal)
  {
    if (trueVal)
      return BoolWrap.trueEnum;
    else
      return BoolWrap.falseEnum;
  }

  public static boolean boolValue(IValue val) throws EvaluationException
  {
    try {
      return ((BoolWrap) val).trueVal;
    } catch (Exception e) {
      throw new EvaluationException("not a boolean scalar");
    }
  }

  /**
   * Construct a char IValue
   * 
   * @param ch
   *          the character. This is a `code point' rather than simply a 16 bit character.
   * @return a @code{char} that conforms to the IValue interface.
   */
  public static CharWrap newChar(int ch)
  {
    return new CharWrapper(ch);
  }

  /**
   * Construct a char IValue.
   * 
   * This function distinguishes between @code{null} and non-@code{null} inputs. In the former case,
   * a @code{nonChar} value is returned.
   * 
   * @param ch
   * @return The character as a @code{char} value.
   */
  public static CharWrap newCharacter(Integer ch)
  {
    if (ch == null)
      return CharWrap.nonCharEnum;
    else
      return new CharWrapper(ch);
  }

  /**
   * Return the special @code{char} that corresponds to the non-character character.
   * 
   * @return
   */
  public static CharWrap nullChar()
  {
    return CharWrap.nonCharEnum;
  }

  /**
   * Return the {@code char} value of a character scalar value.
   * 
   * @param scalar
   * @return the char corresponding to the value.
   * @throws EvaluationException
   *           if the passed in value is not a character, or if it corresponds to @code{nonChar}
   */
  public static int charValue(IValue scalar) throws EvaluationException
  {
    try {
      return ((CharWrapper) scalar).getValue();
    } catch (Exception e) {
      throw new EvaluationException("not a character");
    }
  }

  /**
   * Return the character value of the @code{char} IValue -- as a code point.
   * 
   * @param scalar
   * @return the character as a code point, or null if the input was @code{nonChar}
   * @throws EvaluationException
   */
  public static Integer characterValue(IValue scalar) throws EvaluationException
  {
    if (scalar.equals(CharWrap.nonCharEnum))
      return null;
    else if (scalar instanceof CharWrapper)
      return ((CharWrapper) scalar).getValue();
    else
      throw new EvaluationException("not a character");
  }

  /**
   * Construct an integer IValue.
   * 
   * @param i
   * @return an integer that conforms to the IValue interface, using minimum amount of wrapping
   *         paper.
   */
  public static IntWrap newInt(int i)
  {
    return new IntWrapper(i);
  }

  /**
   * Construct an @ode{integer} IValue.
   * 
   * This function distinguishes between @code{null} and non-@code{null} inputs. In the former case,
   * a @code{nonInteger} value is returned. In the latter case, a normal @code{integer} value is
   * returned.
   * 
   * @param ch
   * @return The integer as a @code{integer} value.
   */
  public static IntWrap newInteger(Integer i)
  {
    if (i == null)
      return IntWrap.nonIntegerEnum;
    else
      return new IntWrapper(i);
  }

  /**
   * The special non-integer @code{integer}.
   * 
   * @return
   */
  public static IntWrap nullInt()
  {
    return IntWrap.nonIntegerEnum;
  }

  /**
   * Return the {@code int} value of a numeric scalar value.
   * 
   * @param scalar
   * @return the int corresponding to the value. Only numeric scalar values are permitted: i.e.,
   *         integers, longs and floats
   * @throws EvaluationException
   *           if the passed in value is not a number.
   */
  public static int intValue(IValue scalar) throws EvaluationException
  {
    try {
      return ((IntWrapper) scalar).getValue();
    } catch (Exception e) {
      throw new EvaluationException("not a integer scalar");
    }
  }

  /**
   * Return the integer value of the @code{integer} IValueoin.
   * 
   * @param scalar
   * @return the integer as an @code{Integer}, or null if the input was @code{nonInteger}
   * @throws EvaluationException
   */
  public static Integer integerValue(IValue scalar)
  {
    try {
      return ((IntWrapper) scalar).getValue();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Construct a long IValue
   * 
   * @param i
   * @return a long that conforms to the IValue interface, using minimum amount of wrapping paper.
   */
  public static LongWrap newLng(long i)
  {
    return new LongWrapper(i);
  }

  public static LongWrap newLong(Long i)
  {
    if (i == null)
      return LongWrap.nonLongEnum;
    else
      return new LongWrapper(i);
  }

  public static LongWrap nullLong()
  {
    return LongWrap.nonLongEnum;
  }

  /**
   * Return the {@code long} value of a numeric scalar value.
   * 
   * @param scalar
   * @return the {@code long} corresponding to the value. Only numeric scalar values are permitted:
   *         i.e., integers, longs and floats
   * @throws EvaluationException
   *           if the passed in value is not a number.
   */
  public static long lngValue(IValue scalar) throws EvaluationException
  {
    try {
      return ((LongWrapper) scalar).getValue();
    } catch (Exception e) {
      try {
        return ((IntWrapper) scalar).getValue();
      } catch (Exception w) {
        throw new EvaluationException("not a long value");
      }
    }
  }

  public static Long longValue(IValue scalar)
  {
    try {
      return ((LongWrapper) scalar).getValue();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Construct a floating point IValue.
   * 
   * There is only one form of floating point number supported: the equivalent of {@code double}.
   * 
   * @param f
   * @return a floating point value that conforms to the IValue interface, using minimum amount of
   *         wrapping paper.
   */
  public static FloatWrap newFlt(double f)
  {
    return new FloatWrapper(f);
  }

  public static FloatWrap newFloat(Double d)
  {
    if (d == null)
      return FloatWrap.nonFloatEnum;
    else
      return new FloatWrapper(d);
  }

  public static FloatWrap nullFloat()
  {
    return FloatWrap.nonFloatEnum;
  }

  /**
   * Return the {@code double} value of a numeric scalar value.
   * 
   * @param scalar
   * @return the {@code double} corresponding to the value. Only numeric scalar values are
   *         permitted: i.e., integers, longs and floats
   * @throws EvaluationException
   *           if the passed in value is not a number.
   */
  public static double fltValue(IValue scalar) throws EvaluationException
  {
    try {
      return ((FloatWrapper) scalar).getValue();
    } catch (Exception e) {
      throw new EvaluationException("not a float scalar");
    }
  }

  public static Double floatValue(IValue scalar)
  {
    try {
      return ((FloatWrapper) scalar).getValue();
    } catch (Exception e) {
      return null;
    }
  }

  public static BigDecimal decimalValue(IValue scalar) throws EvaluationException
  {
    if (scalar instanceof BigNumWrapper)
      return ((BigNumWrapper) scalar).getValue();
    else if (scalar instanceof NonDecimalWrapper)
      return null;
    else
      throw new EvaluationException("not a decimal");
  }

  public static BigNumWrap newDecimal(BigDecimal d)
  {
    return new BigNumWrapper(d);
  }

  /**
   * A non-decimal decimal
   * 
   * @return
   */
  public static BigNumWrap nullDecimal()
  {
    return BigNumWrap.nonDecimalEnum;
  }

  /**
   * Construct a string IValue
   * 
   * @param s
   * @return a string that conforms to the IValue interface, using minimum amount of wrapping paper.
   */
  public static StringWrap newString(String s)
  {
    if (s == null)
      return StringWrap.nonStringEnum;
    else
      return new StringWrapper(s);
  }

  public static StringWrap nullString()
  {
    return StringWrap.nonStringEnum;
  }

  public static String stringValue(IValue scalar) throws EvaluationException
  {
    if (scalar instanceof StringWrapper)
      return ((StringWrapper) scalar).getValue();
    else if (scalar instanceof NonStringWrapper)
      return null;
    else
      throw new EvaluationException("not a string scalar");
  }

  public static IValue newBinary(Object o)
  {
    if (o == null)
      return BinaryWrap.nonBinaryEnum;
    else
      return new BinaryWrapper(o);
  }

  public static IValue nullBinary()
  {
    return BinaryWrap.nonBinaryEnum;
  }

  public static Object binaryValue(IValue scalar) throws EvaluationException
  {
    if (scalar instanceof BinaryWrapper)
      return ((BinaryWrapper) scalar).getValue();
    else if (scalar instanceof NonBinaryWrapper)
      return null;
    else
      throw new EvaluationException("not a binary");
  }

  /**
   * Return a new instance of a given type, using the value specifier associated with the label.
   * 
   * The fields in the record are specified as an alternating sequence of string names and values.
   * For example, to construct an instance of {@code someone} one might use:
   * 
   * <pre>
   * IValue name = Factory.newString(&quot;fred&quot;);
   * IValue age = Factory.newInt(34);
   * IValue R = Factory.newRecord(personType, &quot;someone&quot;, &quot;name&quot;, name, &quot;age&quot;, age);
   * </pre>
   * 
   * @param type
   *          a type description for the expected type.
   * @param label
   *          the label of the record being constructed. For convenience, this may be null if and
   *          only if there is exactly one value specifier associated with the type.
   * @param args
   *          the alternating sequence of names and argument values used to construct the record.
   * 
   * @return the new IValue.
   * @throws ValueException
   */
  public static IRecord newRecord(IAlgebraicType type, String label, Object... args) throws EvaluationException
  {
    IValueSpecifier spec = type.getValueSpecifier(label);
    if (spec instanceof RecordSpecifier) {
      RecordSpecifier rspec = (RecordSpecifier) spec;
      IValue[] params = new IValue[rspec.arity()];
      for (int ix = 0; ix < args.length; ix += 2) {
        if (!(args[ix] instanceof String && args[ix + 1] instanceof IValue)) {
          throw new EvaluationException("invalid arguments in newRecord");
        } else {
          params[rspec.getIndex().get(args[ix])] = (IValue) args[ix + 1];
        }
      }
      IRecord record = (IRecord) ((RecordSpecifier) spec).newInstance(params);
      return record;
    } else
      throw new EvaluationException(label + " not a record in type " + type.getName());
  }

  /**
   * Construct a new anonymous record. The record must have been previously `declared' by being part
   * of some Star program.
   * 
   * @param cxt
   *          The type context to use for resolving the code references for the anonymous record.
   * @param args
   *          An alternating sequence of string labels and IValue elements.
   * @return A new anonymous record containing the data elements
   * @throws EvaluationException
   */

  public static IRecord newRecord(ITypeContext cxt, Object... args) throws EvaluationException
  {
    int arity = args.length / 2;
    String[] fields = new String[arity];
    IValue[] vals = new IValue[arity];
    for (int ix = 0; ix < arity; ix++) {
      fields[ix] = (String) args[2 * ix];
      vals[ix] = (IValue) args[ix + ix + 1];
    }

    return new AnonRecord(TypeUtils.anonRecordLabel(new String[] {}, fields), fields, vals);
  }

  /**
   * Create an anonymous record from a sorted map of fields and values
   * 
   * @param values
   * @return an anonymous record
   */

  public static IRecord newRecord(SortedMap<String, IValue> values)
  {
    int arity = values.size();
    String[] fields = new String[arity];
    IValue[] vals = new IValue[arity];
    int ix = 0;
    for (Entry<String, IValue> entry : values.entrySet()) {
      fields[ix] = entry.getKey();
      vals[ix++] = entry.getValue();
    }
    return new AnonRecord(TypeUtils.anonRecordLabel(new String[] {}, fields), fields, vals);
  }

  /**
   * Construct a new instance of the indicated algebraic type.
   * 
   * @param type
   *          the type description for the type of the new value.
   * @param label
   *          the label corresponding to the particular constructor function to use in building the
   *          value. This label must be known to the type description that is in {@code type}.
   * @param elements
   *          the elements, in order, of the constructor function. If this method is used to
   *          construct a new record, then the elements must be in the expected order (i.e.,
   *          alphabetical by field name).
   * @return a new instance of the value.
   * @throws EvaluationException
   *           if there is a problem in creating the value
   */
  public static IConstructor newValue(IAlgebraicType type, String label, IValue... elements) throws EvaluationException
  {
    IValueSpecifier spec = type.getValueSpecifier(label);
    if (spec instanceof ConstructorSpecifier)
      return (IConstructor) ((ConstructorSpecifier) spec).newInstance(elements);
    else
      throw new EvaluationException(label + " not a known constructor function for type " + type.getName());
  }

  /**
   * Construct a new instance of the indicated algebraic type.
   * 
   * It is possible to create either a record or a regular constructor using this function. In the
   * former case, the values must be presented in the alphabetic order of the fields of the record.
   * 
   * @param cxt
   *          the type context in which to find the description of the type
   * @param typeName
   *          the name of the type. Note: only non-polymorphic types may be instantiated using this
   *          function.
   * @param label
   *          the label corresponding to the particular constructor function to use in building the
   *          value. This label must correspond to a regular constructor from the type.
   * @param elements
   *          the elements, in order, of the constructor function. If this method is used to
   *          construct a new record, then the elements must be in the expected order (i.e.,
   *          alphabetical by field name).
   * @return a new instance of the value.
   * @throws EvaluationException
   *           if there is a problem in creating the value
   */

  public static IValue newValue(ITypeContext cxt, String typeName, String label, IValue... elements)
      throws EvaluationException
  {
    ITypeDescription type = cxt.getTypeDescription(typeName);
    if (type instanceof IAlgebraicType) {
      IValueSpecifier spec = ((IAlgebraicType) type).getValueSpecifier(label);
      if (spec instanceof ConstructorSpecifier)
        return (IConstructor) ((ConstructorSpecifier) spec).newInstance(elements);
      else
        throw new EvaluationException(label + " not a known constructor function for type " + type.getName());
    } else
      throw new EvaluationException(typeName + " not a known type");
  }

  /**
   * Return a new array value.
   * 
   * @param elementType
   *          This is a type description handle of the <emph>element</emph> type of the array, not
   *          the description of the array itself.
   * @param elements
   *          The initial elements of the array.
   * @return an IArray containing the indicated elements.
   * @throws EvaluationException
   *           if a problem arose in initializing the array with the elements.
   */
  public static IArray newArray(IType elementType, IValue... elements) throws EvaluationException
  {
    return Array.newArray(elements);
  }

  public static IArray newArray(IType elementType, List<IValue> elements)
  {
    return Array.newArray(elements);
  }

  /**
   * Construct a new map value.
   * 
   * @param keyType
   *          The type description appropriate for the type of <emph>keys</emph> of the map.
   * @param valType
   *          The type description associated with the type of stored <emph>values</emph> in the
   *          map.
   * @param keyValuePairs
   *          An alternating sequence of keys and values that will be used to provide the initial
   *          contents of the map. For example, to create a map with entries corresponding to "john"
   *          and "peter", one might use:
   * 
   *          <pre>
   * IValue peter = Factory.newString(&quot;peter&quot;);
   * IValue pAge = Factory.newInt(34);
   * IValue john = Factory.newString(&quot;john&quot;);
   * IValue jAge = Factory.newInt(43);
   * IMap ages = Factory.newMap(stringType, integerType, peter, pAge, john, jAge);
   * </pre>
   * @return a new IMap of the appropriate type with the initial contents.
   * @throws EvaluationException
   *           if something goes wrong
   */
  public static IMap newMap(IType keyType, IType valType, IValue... keyValuePairs) throws EvaluationException
  {
    IMap map = new HashTree();

    assert keyValuePairs.length % 2 == 0;

    for (int ix = 0; ix < keyValuePairs.length; ix += 2)
      map = map.setMember(keyValuePairs[ix], keyValuePairs[ix + 1]);

    return map;
  }

  public static IConstructor newTuple(ITypeContext typeContext, IValue... values) throws EvaluationException
  {
    return NTuple.tuple(values);
  }

  public static IValue getNth(IConstructor con, int ix)
  {
    return con.getCell(ix);
  }
}
