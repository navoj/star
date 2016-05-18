package org.star_lang.star.data.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.star_lang.star.compiler.cafe.type.ICafeConstructorSpecifier;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

/**
 * A straightforward implementation of the ITypeDescription specification
 * 
 * Note: TypeDescription implements functionality needed by the Cafe system that MUST NOT be invoked
 * by normal clients of a type description. This is in the ICafeTypeDescription and related
 * interfaces.
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
public class TypeDescription implements IAlgebraicType
{
  final private Location loc;
  final protected IType type;
  final private int arity;
  protected final SortedSet<IValueSpecifier> valueSpecifiers = new TreeSet<>(new CompareLabels());
  private TypeInterface typeInterface;

  /**
   * Construct an empty type description. This form should only be used by compilers that are
   * constructing type descriptions by parsing source texts.
   * 
   * @param loc
   *          the defining location for the type description
   * @param type
   *          the type being defined.
   */
  public TypeDescription(Location loc, IType type)
  {
    this(loc, type, new ArrayList<>());
  }

  public TypeDescription(Location loc, IType type, TypeInterface face)
  {
    this(loc, type, new ArrayList<>(), face);
  }

  /**
   * Construct an empty type description.
   * 
   * @param type
   */
  public TypeDescription(IType type)
  {
    this(Location.nullLoc, type);
  }

  /**
   * Construct a type description with a list of value specifiers. This should be the normal way
   * that a type description is created when all the value specifiers are known.
   * 
   * It is an error for there to be more than one non-algebraic value specifier in the list; nor may
   * algebraic specifiers be mixed with non-algebraic specifiers.
   * 
   * A non-algebraic specifier is one such as a function specifier, or a scalar specifier.
   * 
   * @param loc
   *          the defining location for this type
   * @param type
   *          the type being defined. Should be a quantified type expression in the case that the
   *          type is generic.
   * @param face
   *          the interface type associated with the records in the type
   * @param constructors
   *          the list of constructors for the type.
   */

  public TypeDescription(Location loc, IType type, Collection<IValueSpecifier> constructors, TypeInterface face)
  {
    this.type = type;
    this.loc = loc;
    this.valueSpecifiers.addAll(constructors);
    this.typeInterface = face;

    type = unwrapQuants(type);
    this.arity = TypeUtils.typeArity(type);
  }

  public TypeDescription(Location loc, IType type, Collection<IValueSpecifier> constructors)
  {
    this(loc, type, constructors, null);
  }

  /**
   * A convenience constructor that supports the scenario where a type is associated with a
   * non-algebraic specifier.
   * 
   * @param loc
   * @param type
   * @param valueSpecifier
   */
  public TypeDescription(Location loc, IType type, IValueSpecifier valueSpecifier)
  {
    this(loc, type, FixedList.create(valueSpecifier));
  }

  public TypeDescription(IType type, IValueSpecifier constructor)
  {
    this(Location.nullLoc, type, constructor);
  }

  /**
   * A type description may be associated with a defining source location.
   * 
   * @return the location where the type definition was specified. May be null, in which case it is
   *         not possible to identify in user-visible source where the type was defined.
   */
  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public Kind kind()
  {
    return Kind.kind(arity);
  }

  /**
   * Define one of the value specifiers of this type.
   * 
   * @param label
   *          Each value specifier has a label, this is it
   * @param specifier
   *          The specifier being set on the type description
   */
  public void defineValueSpecifier(String label, IValueSpecifier specifier)
  {
    assert getValueSpecifier(label) == null;
    valueSpecifiers.add(specifier);

    typeInterface = null; // reset until needed
  }

  /**
   * Get the value specifier associated with a given label.
   * 
   * @param label
   *          the label of the value specifier
   * @return the value specifier, or null if it is not known
   */
  @Override
  public IValueSpecifier getValueSpecifier(String label)
  {
    for (IValueSpecifier spec : valueSpecifiers)
      if (spec instanceof ConstructorSpecifier)
        if (spec.getLabel().equals(label))
          return spec;
    return null;
  }

  public IValueSpecifier getOnlyValueSpecifier()
  {
    for (IValueSpecifier spec : valueSpecifiers)
      if (spec instanceof ConstructorSpecifier)
        return spec;
    return null;
  }

  /**
   * Get the list of known value specifiers for this type.
   */
  @Override
  public Collection<IValueSpecifier> getValueSpecifiers()
  {
    return valueSpecifiers;
  }

  public int maxConIx()
  {
    int maxIx = 0;
    for (IValueSpecifier con : getValueSpecifiers()) {
      if (((ICafeConstructorSpecifier) con).getConIx() > maxIx)
        maxIx = ((ICafeConstructorSpecifier) con).getConIx();
    }
    return maxIx;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public IType verifyType(IType type, Location loc, Dictionary dict) throws TypeConstraintException
  {
    IType proto = Freshen.freshenForUse(getType());
    Subsume.subsume(proto, type, loc, dict, true);
    return type;
  }

  @Override
  public TypeInterface getTypeInterface()
  {
    if (typeInterface == null)
      computeTypeInterface();
    return typeInterface;
  }

  private void computeTypeInterface()
  {
    SortedMap<String, IType> fields = new TreeMap<>();
    SortedMap<String, IType> types = new TreeMap<>();

    for (IValueSpecifier con : getValueSpecifiers()) {
      if (con instanceof RecordSpecifier) {
        RecordSpecifier record = (RecordSpecifier) con;
        TypeInterfaceType recordFace = record.getTypeInterface();
        fields.putAll(recordFace.getAllFields());
        types.putAll(recordFace.getAllTypes());
      }
    }
    this.typeInterface = new TypeInterfaceType(types, fields);
  }

  /**
   * Return the NAME of the type in this description.
   * 
   * @return a string identifying the type.
   */
  public String getTypeLabel()
  {
    return type.typeLabel();
  }

  @Override
  public int typeArity()
  {
    return arity;
  }

  @Override
  public String getName()
  {
    return type.typeLabel();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);

    Pair<IType, Map<String, Quantifier>> pair = Freshen.freshen(getType(), AccessMode.readWrite, AccessMode.readOnly);

    IType type = pair.left;
    LayeredMap<IType, String> quants = new LayeredHash<>();
    for (Entry<String, Quantifier> e : pair.right.entrySet()) {
      quants.put(e.getValue().getVar(), e.getKey());
    }

    disp.appendWord("type ");
    DisplayType.display(disp, type);
    disp.appendWord(StandardNames.IS);

    String sep = "";

    for (IValueSpecifier entry : valueSpecifiers) {
      disp.append(sep);
      sep = "\nor ";
      IType conType = Freshen.freshenForUse(entry.getConType());
      try {
        Subsume.same(type, TypeUtils.getConstructorResultType(conType), loc, null);
      } catch (TypeConstraintException e) {
      }

      disp.appendId(entry.getLabel());
      List<Quantifier> conQuants = new ArrayList<>();
      IType conArgType = TypeUtils.unwrap(TypeUtils.getConstructorArgType(conType), conQuants);
      LayeredMap<IType, String> cQuants = quants.fork();

      for (Quantifier q : conQuants)
        cQuants.put(q.getVar(), q.getVar().getVarName());

      if (!(TypeUtils.isTupleType(conArgType) && TypeUtils.tupleTypeArity(conArgType) == 0))
        DisplayType.display(disp, conArgType, new HashMap<>(), cQuants);
    }

    disp.popIndent(mark);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((valueSpecifiers == null) ? 0 : valueSpecifiers.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final TypeDescription other = (TypeDescription) obj;

    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (valueSpecifiers == null) {
      if (other.valueSpecifiers != null)
        return false;
    } else if (!valueSpecifiers.equals(other.valueSpecifiers))
      return false;
    return true;
  }

  private static IType unwrapQuants(IType type)
  {
    do {
      if (type instanceof UniversalType) {
        type = ((UniversalType) type).getBoundType();
      } else
        break;
    } while (true);
    return type;
  }

  private static class CompareLabels implements Comparator<IValueSpecifier>, Serializable
  {
    @Override
    public int compare(IValueSpecifier o1, IValueSpecifier o2)
    {
      return o1.getLabel().compareTo(o2.getLabel());
    }
  }
}
