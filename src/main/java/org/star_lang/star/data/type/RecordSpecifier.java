package org.star_lang.star.data.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

/*
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
public class RecordSpecifier extends ConstructorSpecifier
{
  private final SortedMap<String, Integer> index;

  /**
   * Convenience constructor used when the implementation details of a record constructor are not
   * known
   * 
   * @param loc
   *          where this constructor was defined
   * @param label
   *          the label of the constructor
   * @param source
   *          where is the constructor function itself?
   * @param conIx
   *          the index of the value specifier in the type description
   * @param conType
   *          the type signature of the constructor
   */
  public RecordSpecifier(Location loc, String label, IContentExpression source, int conIx, IType conType)
  {
    this(loc, label, conIx, source, Utils.javaIdentifierOf(label), null, null, null, conType);
  }

  public RecordSpecifier(Location loc, String label, int conIx, IContentExpression source, String javaSafeName,
      String javaClassName, String javaOwner, String javaConSig, IType conType)
  {
    this(loc, label, conIx, conType, javaClassName, javaConSig, javaOwner, javaSafeName);
  }

  /**
   * Construct a {@code RecordSpecifier} for a known constructor including all the details necessary
   * for accessing a compiled version of the description.
   * 
   * Note that a {@code RecordSpecifier} describes a constructor within an algebraic type;
   * specifically one that allows named access to elements of the value.
   * 
   * @param loc
   *          where the constructor was defined
   * @param label
   *          the label of the constructor
   * @param conIx
   *          the index of the constructor within its type. Should be a number from {@code 0} to
   *          {@code N} where {@code N+1} is the number of constructors defined for the algebraic
   *          type
   * @param source
   *          An expression that denotes how to access the constructor function itself
   * @param conType
   *          the type of the constructor. This is a function type of the form {@code (T1,..,Tn)=>T}
   *          where {@code Ti} is the type of the ith argument of the constructor and {@code T} is
   *          the type of the value i.e., the type that the constructor is part of.
   * @param cafeClass
   *          the java class used to implement this constructor.
   * @param cafeOwner
   *          All constructors are part of a type, that type is the owner of this constructor
   */
  public RecordSpecifier(Location loc, String label, int conIx, IContentExpression source, IType conType,
      Class<?> cafeClass, Class<?> cafeOwner)
  {
    super(loc, source, label, conIx, conType, cafeClass, cafeOwner);
    assert TypeUtils.getConstructorArgType(conType) instanceof TypeInterface;
    this.index = createIndex(conType);
  }

  public RecordSpecifier(Location loc, String label, int conIx, IType conType, String javaClassName, String javaConSig,
      String javaOwner, String javaSafeName)
  {
    super(loc, label, conIx, null, conType, javaSafeName, javaClassName, javaConSig, javaOwner);
    assert TypeUtils.unwrap(TypeUtils.getConstructorArgType(conType)) instanceof TypeInterface;
    this.index = createIndex(conType);
  }

  private static SortedMap<String, Integer> createIndex(IType conType)
  {
    return CompilerUtils.buildInterfaceIndex(((TypeInterfaceType) TypeUtils.unwrap(TypeUtils
        .getConstructorArgType(conType))).getAllFields());
  }

  public SortedMap<String, Integer> getIndex()
  {
    return index;
  }

  @Override
  public boolean hasMember(String att)
  {
    return index.containsKey(att);
  }

  public Integer getMemberIndex(String att)
  {
    return index.get(att);
  }

  @Override
  public String memberName(int ix)
  {
    for (Entry<String, Integer> entry : index.entrySet())
      if (entry.getValue() == ix)
        return entry.getKey();
    return super.memberName(ix);
  }

  public IType getMemberType(Dictionary cxt, IType tgtType, String att) throws TypeConstraintException
  {
    IType conType = Freshen.freshenForUse(getConType());

    Subsume.same(tgtType, TypeUtils.getConstructorResultType(conType), getLoc(), cxt);

    assert TypeUtils.isFunctionType(conType);

    TypeInterface face = (TypeInterface) TypeUtils.getConstructorArgType(conType);
    return face.getFieldType(att);
  }

  @Override
  public int arity()
  {
    return index.size();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getLabel());
    printFields(disp);
  }

  public void printFields(PrettyPrintDisplay disp)
  {
    Pair<IType, Map<String, Quantifier>> freshen = Freshen.freshen(getConType(), AccessMode.readOnly,
        AccessMode.readOnly);
    Map<String, TypeVar> used = new HashMap<>();
    for (Entry<String, Quantifier> entry : freshen.right.entrySet())
      used.put(entry.getKey(), entry.getValue().getVar());
    Pair<IType, Map<String, Quantifier>> argFresh = Freshen.freshen(TypeUtils.getConstructorArgType(freshen.left),
        AccessMode.readOnly, AccessMode.readOnly);

    LayeredMap<IType, String> quants = new LayeredHash<>();
    for (Entry<String, Quantifier> entry : argFresh.right.entrySet())
      quants.put(entry.getValue().getVar(), entry.getKey());
    DisplayType.display(disp, argFresh.left, used, quants);
  }

  public TypeInterfaceType getTypeInterface()
  {
    return (TypeInterfaceType) TypeUtils.unwrap(TypeUtils.getConstructorArgType(getConType()));
  }
}
