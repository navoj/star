package org.star_lang.star.data.value;

import org.star_lang.star.data.*;
import org.star_lang.star.data.type.*;
import org.star_lang.star.data.value.BinaryWrap.BinaryWrapper;
import org.star_lang.star.data.value.FloatWrap.FloatWrapper;
import org.star_lang.star.data.value.IntWrap.IntWrapper;
import org.star_lang.star.data.value.LongWrap.LongWrapper;
import org.star_lang.star.data.value.StringWrap.NonStringWrapper;
import org.star_lang.star.data.value.StringWrap.StringWrapper;

import java.util.List;

/**
 * The Factory has two roles: to support the creation of values and to support accessing scalar
 * values.
 * <p>
 * Copyright (c) 2015. Francis G. McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class Factory {
  public static final IValue trueValue = BoolWrap.trueEnum;
  public static final IValue falseValue = BoolWrap.falseEnum;

  /**
   * Construct a wrapped boolean value. Since boolean is essentially an enumeration, this factory
   * function simply returns one of two publicly available instances of BoolWrap.
   *
   * @param trueVal truth value to wrap
   * @return truth if {@code trueVal} otherwise {@code falseness}.
   */
  public static BoolWrap newBool(boolean trueVal) {
    if (trueVal)
      return BoolWrap.trueEnum;
    else
      return BoolWrap.falseEnum;
  }

  public static boolean boolValue(IValue val) throws EvaluationException {
    try {
      return ((BoolWrap) val).trueVal;
    } catch (Exception e) {
      throw new EvaluationException("not a boolean scalar");
    }
  }

  /**
   * Construct an integer IValue.
   *
   * @param i integer to wrap
   * @return an integer that conforms to the IValue interface, using minimum amount of wrapping
   * paper.
   */
  public static IntWrap newInt(int i) {
    return new IntWrapper(i);
  }

  /**
   * Construct an @ode{integer} IValue.
   * <p/>
   * This function distinguishes between @code{null} and non-@code{null} inputs. In the former case,
   * a @code{nonInteger} value is returned. In the latter case, a normal @code{integer} value is
   * returned.
   *
   * @param i integer to wrap
   * @return The integer as a @code{integer} value.
   */
  public static IntWrap newInteger(Integer i) {
    if (i == null)
      return IntWrap.nonIntegerEnum;
    else
      return new IntWrapper(i);
  }

  /**
   * Return the {@code int} value of a numeric scalar value.
   *
   * @param scalar to unwrap
   * @return the int corresponding to the value. Only numeric scalar values are permitted: i.e.,
   * integers, longs and floats
   * @throws EvaluationException if the passed in value is not a number.
   */
  public static int intValue(IValue scalar) throws EvaluationException {
    try {
      return ((IntWrapper) scalar).getValue();
    } catch (Exception e) {
      throw new EvaluationException("not a integer scalar");
    }
  }

  /**
   * Return the integer value of the @code{integer} IValueoin.
   *
   * @param scalar to unwrap
   * @return the integer as an @code{Integer}, or null if the input was @code{nonInteger}
   * @throws EvaluationException
   */
  public static Integer integerValue(IValue scalar) {
    try {
      return ((IntWrapper) scalar).getValue();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Construct a long IValue
   *
   * @param i long value to wrap
   * @return a long that conforms to the IValue interface, using minimum amount of wrapping paper.
   */
  public static LongWrap newLng(long i) {
    return new LongWrapper(i);
  }

  public static LongWrap newLong(Long i) {
    return new LongWrapper(i);
  }

  /**
   * Return the {@code long} value of a numeric scalar value.
   * to unwrap
   *
   * @param scalar to unwrap
   * @return the {@code long} corresponding to the value. Only numeric scalar values are permitted:
   * i.e., integers, longs and floats
   * @throws EvaluationException if the passed in value is not a number.
   */
  public static long lngValue(IValue scalar) throws EvaluationException {
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

  public static Long longValue(IValue scalar) {
    try {
      return ((LongWrapper) scalar).getValue();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Construct a floating point IValue.
   * <p/>
   * There is only one form of floating point number supported: the equivalent of {@code double}.
   *
   * @param f number to wrap
   * @return a floating point value that conforms to the IValue interface, using minimum amount of
   * wrapping paper.
   */
  public static FloatWrap newFlt(double f) {
    return new FloatWrapper(f);
  }

  public static FloatWrap newFloat(Double d) {
    if (d == null)
      return FloatWrap.nonFloatEnum;
    else
      return new FloatWrapper(d);
  }

  /**
   * Return the {@code double} value of a numeric scalar value.
   *
   * @param scalar to unwrap
   * @return the {@code double} corresponding to the value. Only numeric scalar values are
   * permitted: i.e., integers, longs and floats
   * @throws EvaluationException if the passed in value is not a number.
   */
  public static double fltValue(IValue scalar) throws EvaluationException {
    try {
      return ((FloatWrapper) scalar).getValue();
    } catch (Exception e) {
      throw new EvaluationException("not a float scalar");
    }
  }

  /**
   * Construct a string IValue
   *
   * @param s string to wrap
   * @return a string that conforms to the IValue interface, using minimum amount of wrapping paper.
   */
  public static StringWrap newString(String s) {
    if (s == null)
      return StringWrap.nonStringEnum;
    else
      return new StringWrapper(s);
  }

  public static StringWrap nullString() {
    return StringWrap.nonStringEnum;
  }

  public static String stringValue(IValue scalar) throws EvaluationException {
    if (scalar instanceof StringWrapper)
      return ((StringWrapper) scalar).getValue();
    else if (scalar instanceof NonStringWrapper)
      return null;
    else
      throw new EvaluationException("not a string scalar");
  }

  public static IValue newBinary(Object o) {
    if (o == null)
      return BinaryWrap.nonBinaryEnum;
    else
      return new BinaryWrapper(o);
  }

  /**
   * Return a new instance of a given type, using the value specifier associated with the label.
   * <p/>
   * The fields in the record are specified as an alternating sequence of string names and values.
   * For example, to construct an instance of {@code someone} one might use:
   * <p/>
   * <pre>
   * IValue NAME = Factory.newString(&quot;fred&quot;);
   * IValue age = Factory.newInt(34);
   * IValue R = Factory.newRecord(personType, &quot;someone&quot;, &quot;NAME&quot;, NAME, &quot;age&quot;, age);
   * </pre>
   *
   * @param type  a type description for the expected type.
   * @param label the label of the record being constructed. For convenience, this may be null if and
   *              only if there is exactly one value specifier associated with the type.
   * @param args  the alternating sequence of names and argument values used to construct the record.
   * @return the new IValue.
   */
  public static IRecord newRecord(IAlgebraicType type, String label, Object... args) throws EvaluationException {
    IValueSpecifier spec = type.getValueSpecifier(label);
    if (spec instanceof RecordSpecifier) {
      RecordSpecifier rspec = (RecordSpecifier) spec;
      IValue[] params = new IValue[rspec.arity()];
      for (int ix = 0; ix < args.length; ix += 2) {
        if (!(args[ix] instanceof String && args[ix + 1] instanceof IValue)) {
          throw new EvaluationException("invalid arguments in newRecord");
        } else {
          params[rspec.getIndex().get((String)args[ix])] = (IValue) args[ix + 1];
        }
      }
      return (IRecord) ((RecordSpecifier) spec).newInstance(params);
    } else
      throw new EvaluationException(label + " not a record in type " + type.getName());
  }

  /**
   * Return a new array value.
   *
   * @param elementType This is a type description handle of the <emph>element</emph> type of the array, not
   *                    the description of the array itself.
   * @param elements    The initial elements of the array.
   * @return an IArray containing the indicated elements.
   * @throws EvaluationException if a problem arose in initializing the array with the elements.
   */
  public static IArray newArray(IType elementType, IValue... elements) throws EvaluationException {
    return Array.newArray(elements);
  }

  public static IArray newArray(IType elementType, List<IValue> elements) {
    return Array.newArray(elements);
  }

  /**
   * Construct a new map value.
   *
   * @param keyType       The type description appropriate for the type of <emph>keys</emph> of the map.
   * @param valType       The type description associated with the type of stored <emph>values</emph> in the
   *                      map.
   * @param keyValuePairs An alternating sequence of keys and values that will be used to provide the initial
   *                      contents of the map. For example, to create a map with entries corresponding to "john"
   *                      and "peter", one might use:
   *                      <p/>
   *                      <pre>
   *                      IValue peter = Factory.newString(&quot;peter&quot;);
   *                      IValue pAge = Factory.newInt(34);
   *                      </pre>
   * @return a new IMap of the appropriate type with the initial contents.
   * @throws EvaluationException if something goes wrong
   */
  public static IMap newMap(IType keyType, IType valType, IValue... keyValuePairs) throws EvaluationException {
    IMap map = new HashTree();

    assert keyValuePairs.length % 2 == 0;

    for (int ix = 0; ix < keyValuePairs.length; ix += 2)
      map = map.setMember(keyValuePairs[ix], keyValuePairs[ix + 1]);

    return map;
  }

  public static IConstructor newTuple(ITypeContext typeContext, IValue... values) throws EvaluationException {
    return NTuple.tuple(values);
  }

  public static IValue getNth(IConstructor con, int ix) {
    return con.getCell(ix);
  }
}
