package org.star_lang.star.data.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.ICafeConstructorSpecifier;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.VoidWrap;

/**
 * Part of a type description, a {@code ConstructorSpecifier} describes an algebraic constructor for
 * a type.
 * 
 * A {@code ConstructorSpecifier} can be modeled logically as a function, for example in the type:
 * 
 * <pre>
 * type person is noone or someone{NAME has type string; age has type integer}
 * </pre>
 * 
 * there are two constructor functions that relate to creating values of type person: {@code noone}
 * and {@code someone}.
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

@SuppressWarnings("serial")
public class ConstructorSpecifier implements IValueSpecifier, ICafeConstructorSpecifier
{
  private final IType conType;
  private final String typeLabel;
  private final String label;
  private final Location loc;

  private final IContentExpression source;

  // Cafe specific stuff
  private transient Class<?> cafeClass;
  private final int conIx;
  private final String javaClassName;
  private final String javaOwner;
  private final String javaConSig;
  private final String javaSafeName;
  public static final String CONARG = "__";

  public ConstructorSpecifier(Location loc, IContentExpression source, String label, int conIx, IType conType,
      Class<?> cafeClass, Class<?> cafeOwner)
  {
    assert label.equals(VoidWrap.label) || TypeUtils.isConstructorType(conType);
    this.conIx = conIx;
    this.label = label;
    this.loc = loc;
    this.conType = conType;
    this.cafeClass = cafeClass;
    if (cafeClass != null) {
      this.javaClassName = Utils.javaInternalClassName(cafeClass);
      this.javaOwner = Utils.javaInternalClassName(cafeOwner);
      this.javaSafeName = Utils.javaIdentifierOf(label)
          + (TypeUtils.arityOfConstructorType(conType) == 0 ? Types.ENUM_SFX : "");
    } else {
      this.javaClassName = null;
      this.javaOwner = null;
      this.javaSafeName = null;
    }
    this.javaConSig = Types.javaConstructorSig(conType);
    this.typeLabel = TypeUtils.unwrap(TypeUtils.getConstructorResultType(conType)).typeLabel();
    this.source = source;
  }

  public ConstructorSpecifier(Location loc, String label, int conIx, IContentExpression source, IType conType,
      String javaSafeName, String javaClassName, String javaConSig, String javaOwner)
  {
    assert TypeUtils.isConstructorType(conType);
    this.conIx = conIx;
    this.label = label;
    this.loc = loc;
    this.conType = conType;
    this.cafeClass = null;
    this.javaClassName = javaClassName;
    this.javaConSig = javaConSig;
    this.javaOwner = javaOwner;
    this.javaSafeName = javaSafeName;
    this.typeLabel = TypeUtils.unwrap(TypeUtils.getConstructorResultType(conType)).typeLabel();
    this.source = source;
  }

  public ConstructorSpecifier(Location loc, String label, int conIx, IType conType, IContentExpression source)
  {
    this(loc, label, conIx, source, conType, null, null, null, null);
  }

  public ConstructorSpecifier(Location loc, String label, IType conType, IContentExpression source)
  {
    this(loc, label, -1, source, conType, null, null, null, null);
  }

  /**
   * Return the type of the constructor itself. This is always a function type. In the case of
   * {@code someone} above, this signature will be:
   * 
   * <pre>
   * someone has type (integer,string) => person
   * </pre>
   * 
   * since attributes in an anonymous record type are always sorted alphabetically.
   * 
   * @return the type signature of the constructor function associated with this value specifier.
   */
  @Override
  public IType getConType()
  {
    return conType;
  }

  @Override
  public String getTypeLabel()
  {
    return typeLabel;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public IContentExpression source()
  {
    return source;
  }

  public int arity()
  {
    IType conType = TypeUtils.unwrap(getConType());
    return TypeUtils.getConstructorArgTypes(conType).length;
  }

  @Override
  public String memberName(int ix)
  {
    return CONARG + ix;
  }

  /**
   * Most constructors are implemented using a specific class that has been generated by the Cafe
   * compiler.
   * 
   * This class should be used to construct instances of this constructor.
   * 
   * @return the class that underlies instances of this constructor.
   */
  @Override
  public Class<?> getCafeClass()
  {
    return cafeClass;
  }

  @Override
  public void setCafeClass(Class<?> cafeClass)
  {
    this.cafeClass = cafeClass;
  }

  public String getJavaClassName()
  {
    return javaClassName;
  }

  @Override
  public String getJavaType()
  {
    if (javaClassName != null)
      return javaClassName.replace('.', '/');
    else
      return Types.IVALUE;
  }

  public String getJavaConSig()
  {
    return javaConSig;
  }

  public String getJavaOwner()
  {
    return javaOwner.replace('.', '/');
  }

  public String getJavaSafeName()
  {
    return javaSafeName;
  }

  @Override
  public boolean hasMember(String id)
  {
    return false;
  }

  /**
   * Get the NAME of this constructor
   * 
   * @return The constructor NAME
   */
  @Override
  public String getLabel()
  {
    return label;
  }

  /**
   * Each constructor has a unique 'constructor index' (conIx) which identifies which constructor
   * within the type it is.
   * 
   * @return the conIx for this constructor.
   */

  @Override
  public int getConIx()
  {
    return conIx;
  }

  /**
   * Generic method for constructing an instance of a constructor based on the code generated by the
   * Cafe compiler.
   * 
   * @param args
   *          the arguments to the constructor. If this is a record being constructed, then the
   *          elements passed in must match the alphabetic ordering of fields within the record. If
   *          this is a positional constructor, then the order must match the arguments of the
   *          constructor function
   * @return a new constructor value.
   * @throws EvaluationException
   *           if something goes wrong with the creation of the value
   */
  public IValue newInstance(IValue... args) throws EvaluationException
  {
    if (cafeClass == null)
      try {
        cafeClass = Class.forName(getJavaClassName());
      } catch (ClassNotFoundException e1) {
        throw new EvaluationException("cannot find code for class: " + getJavaClassName()
            + " because class not found, " + e1.getMessage());
      }
    try {
      assert cafeClass != null;
      Class<?>[] conClasses = new Class<?>[args.length];
      for (int ix = 0; ix < args.length; ix++)
        conClasses[ix] = IValue.class;
      Constructor<?> con = null;
      try {
        con = cafeClass.getConstructor(conClasses);
      } catch (NoSuchMethodException e) {
        for (Constructor<?> c : cafeClass.getConstructors())
          if (c.getParameterTypes().length == args.length) {
            con = c;
            break;
          }
      }
      if (con == null) {
        throw new EvaluationException("could not find a matching constructor");
      }
      return (IValue) con.newInstance((Object[]) args);
    } catch (IllegalAccessException e) {
      throw new EvaluationException("not permitted", e);
    } catch (SecurityException e) {
      throw new EvaluationException("security violation", e);
    } catch (IllegalArgumentException e) {
      throw new EvaluationException("illegal argument", e);
    } catch (InstantiationException | InvocationTargetException e) {
      throw new EvaluationException("problem in initialization", e);
    }
  }

  @Override
  public ICafeConstructorSpecifier cleanCopy()
  {
    return new ConstructorSpecifier(loc, label, conIx, source, conType, javaSafeName, javaClassName, javaConSig,
        javaOwner);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(label);

    if (TypeUtils.isConstructorType(conType)) {
      IType argType = TypeUtils.getConstructorArgType(TypeUtils.unwrap(conType));
      DisplayType.display(disp, argType);
    } else {
      disp.append(":");
      DisplayType.display(disp, conType);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
