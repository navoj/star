package org.star_lang.star.compiler.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExists;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/*
 * Type subsumption algorithm 
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
public class Subsume
{
  private final Dictionary dict;
  private final Location loc;
  private final Stack<Pair<TypeVar, Integer>> resets = new Stack<>();
  private final boolean allow;

  private Subsume(Dictionary face, Location loc, boolean allow)
  {
    this.dict = face;
    this.loc = loc;
    this.allow = allow;
  }

  /**
   * Unify two type expressions.
   * 
   * Guaranteed to have no effect should the unification itself fail. I.e., partial unifications
   * will be thrown away.
   * 
   * @param t1
   * @param t2
   * @param loc
   *          the location where the unification takes place. This is helpful in tracking down the
   *          sources of a type error.
   * @param cxt
   * @throws TypeConstraintException
   */
  public static void subsume(IType t1, IType t2, Location loc, Dictionary cxt) throws TypeConstraintException
  {
    subsume(t1, t2, loc, cxt, false);
  }

  public static void same(IType t1, IType t2, Location loc, Dictionary dict) throws TypeConstraintException
  {
    Subsume sub = new Subsume(dict, loc, false);
    sub.subsume(t1, t2);
    sub.subsume(t2, t1);
  }

  /**
   * Test to see if one type expression subsumes another.
   * 
   * For non-quantified types, this is equivalent to unification. However, if one or both have
   * explicit quantifiers then the algorithm is somewhat more complex.
   * 
   * Guaranteed to have no effect should the test itself fail. I.e., partial subsumption tests will
   * be thrown away.
   * 
   * @param t1
   * @param t2
   *          should be at least as polymorphic as t1
   * @param loc
   *          the location where the unification takes place. This is helpful in tracking down the
   *          sources of a type error.
   * @param cxt
   * @param allow
   *          whether to allow type variables to be constrained with type constraints
   * @throws TypeConstraintException
   */
  public static void subsume(IType t1, IType t2, Location loc, Dictionary cxt, boolean allow)
      throws TypeConstraintException
  {
    Subsume sub = new Subsume(cxt, loc, allow);
    try {
      sub.subsume(t1, t2);
    } catch (TypeConstraintException e) {
      sub.resetBindings();
      throw e;
    }
  }

  public static boolean test(IType t1, IType t2, Location loc, Dictionary dict)
  {
    Subsume sub = new Subsume(dict, loc, true);
    try {
      sub.subsume(t1, t2);
      return true;
    } catch (TypeConstraintException e) {
      return false;
    } finally {
      sub.resetBindings();
    }
  }

  public void resetBindings()
  {
    resetBindings(0);
  }

  private void resetBindings(int limit)
  {
    while (resets.size() > limit) {
      Pair<TypeVar, Integer> v = resets.pop();
      v.left.reset(v.right);
    }
  }

  /**
   * tp2 subsumes tp1 iff tp2 is 'more' general than tp1.
   * 
   * @param tp1
   * @param tp2
   * @throws TypeConstraintException
   */

  private void subsume(IType t1, IType t2) throws TypeConstraintException
  {
    IType tp1 = checkAlias(t1);
    IType tp2 = checkAlias(t2);

    if (tp1 == tp2)
      return; // quick exit

    if (tp1 instanceof TypeVar) {
      TypeVar v1 = (TypeVar) tp1;

      if (tp2 instanceof TypeVar) {
        TypeVar v2 = (TypeVar) tp2;

        if (!v1.isReadOnly()) {
          if (!v2.isReadOnly()) {
            if (TypeUtils.deRefChainLength(t1) > TypeUtils.deRefChainLength(t2)) {
              bindVar(v2, tp1); // bind the shorter chain to the longer one
              return;
            }
          }
          bindVar(v1, tp2);
        } else if (v1 != v2)
          bindVar(v2, tp1);
      } else
        bindVar(v1, tp2);
    } else if (tp2 instanceof TypeVar)
      bindVar((TypeVar) tp2, tp1);
    else if (tp2 instanceof UniversalType) {
      // tp2 subsumes tp1 iff refreshed tp2 subsumes tp1 without binding its variables
      IType rTp2 = Freshen.freshen(tp2, AccessMode.readWrite, AccessMode.readOnly).left();

      subsume(tp1, rTp2);
    } else if (tp1 instanceof UniversalType) {
      // tp2 subsumes tp1 iff refreshed tp1 subsumed by tp2
      IType rTp1 = Freshen.freshen(tp1, AccessMode.readOnly, AccessMode.readWrite).left();
      subsume(rTp1, tp2);
    } else if (tp2 instanceof ExistentialType) {
      // tp2 subsumes tp1 iff refreshed tp2 subsumes tp1 without binding its variables
      int mark = resets.size();
      IType rTp2 = Freshen.freshen(tp2, AccessMode.readWrite, AccessMode.readOnly).left();

      subsume(tp1, rTp2);

      resetBindings(mark);
    } else if (tp1 instanceof ExistentialType) {
      // tp2 subsumes tp1 iff refreshed tp1 subsumed by tp2
      int mark = resets.size();
      IType rTp1 = Freshen.freshen(tp1, AccessMode.readOnly, AccessMode.readWrite).left();
      subsume(rTp1, tp2);
      resetBindings(mark);
    } else if (tp1 instanceof Type && tp2 instanceof Type) {
      if (!tp1.typeLabel().equals(tp2.typeLabel())) {
        throw new TypeConstraintException(FixedList.create(tp1, " not equal to ", tp2));
      }
    } else if (tp1 instanceof TypeExp && tp2 instanceof TypeExp) {
      TypeExp c1 = (TypeExp) tp1;
      TypeExp c2 = (TypeExp) tp2;
      int arity1 = c1.typeArity();
      int arity2 = c2.typeArity();
      IType c1TyCon = c1.getTypeCon();
      IType c2TyCon = c2.getTypeCon();

      if (arity1 == arity2) {
        try {
          subsume(c1TyCon, c2TyCon);
        } catch (TypeConstraintException e) {
          throw new TypeConstraintException(FixedList.create(tp1, " is not consistent with ", tp2, "\nbecause ", e
              .getMessage()));
        }

        IType lArgs[] = c1.getTypeArgs();
        IType rArgs[] = c2.getTypeArgs();

        String tpLabel = c1TyCon.typeLabel();

        // Special cases, e.g. for function types -- they are contravariant
        if ((tpLabel.equals(StandardNames.FUN_ARROW) || tpLabel.equals(StandardNames.OVERLOADED_TYPE)) && arity1 == 2) {
          subsume(rArgs[0], lArgs[0]);
          subsume(lArgs[1], rArgs[1]);
        } else if (tpLabel.equals(StandardNames.PTN_TYPE) && arity1 == 2) {
          subsume(lArgs[0], rArgs[0]);
          subsume(rArgs[1], lArgs[1]);
        } else if (tpLabel.equals(StandardNames.REF) && arity1 == 1) {
          // ref types must be identical
          subsume(lArgs[0], rArgs[0]);
          subsume(rArgs[0], lArgs[0]);
        } else if (tpLabel.equals(StandardNames.CONSTRUCTOR_TYPE) && arity1 == 2) {
          // constructor types must be identical
          subsume(lArgs[0], rArgs[0]);
          subsume(rArgs[0], lArgs[0]);
          subsume(lArgs[1], rArgs[1]);
          subsume(rArgs[1], lArgs[1]);
        } else {
          for (int ix = 0; ix < arity1; ix++)
            subsume(lArgs[ix], rArgs[ix]);
        }
      } else
        throw new TypeConstraintException(FixedList.create("arity of ", c1TyCon, "/", arity1,
            " is different to arity of ", c2TyCon, "/", arity2));
    } else if (tp1 instanceof TupleType && tp2 instanceof TupleType) {
      TupleType c1 = (TupleType) tp1;
      TupleType c2 = (TupleType) tp2;
      int arity1 = c1.arity();
      int arity2 = c2.arity();

      if (arity1 == arity2) {

        IType lArgs[] = c1.getElTypes();
        IType rArgs[] = c2.getElTypes();

        for (int ix = 0; ix < arity1; ix++)
          subsume(lArgs[ix], rArgs[ix]);
      } else
        throw new TypeConstraintException(FixedList.create("arity of ", tp1, "/", arity1, " is different to arity of ",
            tp2, "/", arity2));
    } else if (tp1 instanceof TypeInterface && tp2 instanceof TypeInterface) {
      TypeInterface tI1 = (TypeInterface) tp1;
      TypeInterface tI2 = (TypeInterface) tp2;

      if (tI1.numOfFields() > tI2.numOfFields())
        throw new TypeConstraintException(FixedList.create(tp2, " has too few fields compared to ", tp1));
      else if (tI1.numOfTypes() > tI2.numOfTypes())
        throw new TypeConstraintException(FixedList.create(tp2, " has too few types compared to ", tp1));
      else {
        for (Entry<String, IType> entry : tI2.getAllTypes().entrySet()) {
          IType lspec = entry.getValue();
          IType rspec = tI1.getType(entry.getKey());
          if (rspec == null)
            throw new TypeConstraintException(StringUtils.msg(tp1, " does not contain type ", entry.getKey()));
          else if (!lspec.kind().checkKind(rspec.kind()))
            throw new TypeConstraintException(StringUtils.msg(tp1, " is not consistent with ", entry.getKey()));
        }

        Map<String, IType> lFields = tI1.getAllFields();
        Map<String, IType> rFields = tI2.getAllFields();

        for (Entry<String, IType> fieldEntry : rFields.entrySet()) {
          IType otherField = lFields.get(fieldEntry.getKey());
          if (otherField == null)
            throw new TypeConstraintException(FixedList.create(tp1, "\nnot consistent with ", tp2,
                "\nbecause former does not have member for ", fieldEntry.getKey()));
          subsume(fieldEntry.getValue(), otherField);
        }
      }
    } else if (tp2 instanceof TypeInterface)
      subsume(TypeUtils.interfaceOfType(loc, tp1, dict), tp2);
    else
      throw new TypeConstraintException(FixedList.create(tp1, " not consistent with ", tp2));
  }

  private IType checkAlias(IType type) throws TypeConstraintException
  {
    type = TypeUtils.deRef(type);
    if (dict != null) {
      if (type instanceof TypeExp) {
        TypeExp tExp = (TypeExp) type;
        ITypeDescription spec = dict.getTypeDescription(tExp.typeLabel());
        if (spec instanceof ITypeAlias)
          return checkAlias(((ITypeAlias) spec).apply(type, loc, dict));
      } else if (type instanceof Type) {
        Type t = (Type) type;
        ITypeDescription spec = dict.getTypeDescription(t.typeLabel());
        if (spec instanceof ITypeAlias) {
          IType aliased = ((ITypeAlias) spec).apply(type, loc, dict);
          if (aliased != type)
            return checkAlias(aliased);
          else
            return type;
        } else if (spec instanceof TypeExists) {
          IType existent = ((TypeExists) spec).getType();
          if (!existent.equals(type))
            return checkAlias(existent);
        }
      }
    }
    return type;
  }

  private void bindVar(TypeVar v, IType tp) throws TypeConstraintException
  {
    if (OccursCheck.occursCheck(tp, v))
      throw new TypeConstraintException("type would be circular");
    else if (v.isReadOnly()) { // some special hacking needed here 'cos readonly type var ~= Type
      if (tp instanceof Type) {
        Type type = (Type) tp;
        if (type.typeLabel().equals(v.getVarName()))
          return;
      }
      throw new TypeConstraintException(FixedList.create(v, " cannot be bound to ", tp, "\nbecause ", v,
          " is read only"));
    } else {
      int reset = v.bind(tp, loc, dict, allow);
      resets.push(Pair.pair(v, reset));
    }
  }
}
