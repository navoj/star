package org.star_lang.star.compiler.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.HistoricalMap;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.QuantifiedType;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeSubstitute;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

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
public class UnifyTypes
{
  private final Dictionary cxt;
  private final Location loc;
  private final Map<TypeVar, Integer> resets = new HashMap<TypeVar, Integer>();
  private final boolean allow;

  private static final boolean UNIVERSAL = true;

  private UnifyTypes(Dictionary face, Location loc, boolean allow)
  {
    this.cxt = face;
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
   * @param allow
   *          whether to allow type variables to be constrained with type constraints
   * @throws TypeConstraintException
   */
  public static void unify(IType t1, IType t2, Location loc, Dictionary cxt, boolean allow)
      throws TypeConstraintException
  {
    UnifyTypes univ = new UnifyTypes(cxt, loc, allow);
    try {
      univ.unify(t1, t2);
    } catch (TypeConstraintException e) {
      univ.resetBindings();
      throw e;
    }
  }

  public static boolean testUnify(IType t1, IType t2, Location loc, Dictionary dict)
  {
    UnifyTypes univ = new UnifyTypes(dict, loc, true);
    try {
      univ.unify(t1, t2);
      return true;
    } catch (TypeConstraintException e) {
      return false;
    } finally {
      univ.resetBindings();
    }
  }

  public void resetBindings()
  {
    for (Entry<TypeVar, Integer> v : resets.entrySet())
      v.getKey().reset(v.getValue());
  }

  private void unify(IType tp1, IType tp2) throws TypeConstraintException
  {
    tp1 = checkAlias(tp1);
    tp2 = checkAlias(tp2);

    if (tp1 == tp2)
      return; // quick exit

    if (tp1 instanceof TypeVar) {
      TypeVar v1 = (TypeVar) tp1;

      if (tp2 instanceof TypeVar) {
        TypeVar v2 = (TypeVar) tp2;

        if (!v1.isReadOnly())
          bindVar(v1, tp2);
        else if (v1 != v2)
          bindVar(v2, tp1);
      } else if (!v1.isReadOnly())
        bindVar(v1, tp2);
      else
        throw new TypeConstraintException(FixedList.create(v1, " cannot be bound to ", tp2, "\nbecause ", v1,
            " is read only"));
    } else if (tp2 instanceof TypeVar)
      bindVar((TypeVar) tp2, tp1);
    else if (tp1 instanceof UniversalType) {
      UniversalType u1 = (UniversalType) tp1;
      if (tp2 instanceof UniversalType) {
        UniversalType u2 = (UniversalType) tp2;

        HistoricalMap<String, TypeVar> b1 = new HistoricalMap<String, TypeVar>();
        HistoricalMap<String, TypeVar> b2 = new HistoricalMap<String, TypeVar>();
        IType un1 = Refresher.refresh(tp1, b1);
        IType un2 = Refresher.refresh(tp2, b2);

        unify(un1, un2);

        if (b1.size() != b2.size())
          throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
              "\nbecause they have different numbers of quantifiers"));

        Iterator<Entry<String, TypeVar>> it1 = b1.entrySet().iterator();
        Iterator<Entry<String, TypeVar>> it2 = b2.entrySet().iterator();

        while (it1.hasNext() && it2.hasNext()) {
          Entry<String, TypeVar> vr1 = it1.next();
          Entry<String, TypeVar> vr2 = it2.next();

          if (!vr1.getValue().equals(vr2.getValue())) {
            throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
                "\nbecause quantifier %", vr1.getKey(), " should be linked to ", vr2.getKey()));
          } else if (!TypeUtils.isTypeVar(vr1.getValue()))
            throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
                "\nbecause quantifier %", vr1.getKey(), " should not be constrained"));

        }

        // Check type constraints ... the number of type constraints should not change with each
        // bound variable

        IType U1 = u1;
        while (U1 instanceof UniversalType) {
          UniversalType e = (UniversalType) U1;
          TypeVar tv = e.getBoundVar();
          int conCount = tv.numConstraints();
          TypeVar btv = b1.get(tv.getVarName());
          if (btv.numConstraints() != conCount)
            throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
                "\nbecause quantifier %", tv.getVarName(), " should not be constrained"));
          U1 = e.getBoundType();
        }

        IType U2 = u2;
        while (U2 instanceof UniversalType) {
          UniversalType e = (UniversalType) U2;
          TypeVar tv = e.getBoundVar();
          int conCount = tv.numConstraints();
          TypeVar btv = b2.get(tv.getVarName());
          if (btv.numConstraints() != conCount)
            throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
                "\nbecause quantifier %", tv.getVarName(), " should not be constrained"));
          U2 = e.getBoundType();
        }
      } else if (UNIVERSAL) {
        Map<String, TypeVar> lhsBound = new HashMap<String, TypeVar>();
        IType rTp1 = Refresher.refresh(tp1, AccessMode.readOnly, lhsBound);

        unify(rTp1, tp2);
      } else
        throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2));
    } else if (tp2 instanceof UniversalType) {
      if (UNIVERSAL) {
        Map<String, TypeVar> rhsBound = new HashMap<String, TypeVar>();
        IType rTp2 = Refresher.refresh(tp2, AccessMode.readOnly, rhsBound);

        unify(tp1, rTp2);
      } else
        throw new TypeConstraintException(FixedList.create(tp1, " not equal to ", tp2));
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
          unify(c1TyCon, c2TyCon);
        } catch (TypeConstraintException e) {
          throw new TypeConstraintException(FixedList.create(tp1, " is not consistent with ", tp2, "\nbecause ", e
              .getWords()));
        }
        IType lArgs[] = c1.getTypeArgs();
        IType rArgs[] = c2.getTypeArgs();
        for (int ix = 0; ix < arity1; ix++)
          unify(lArgs[ix], rArgs[ix]);
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
          unify(lArgs[ix], rArgs[ix]);
      } else
        throw new TypeConstraintException(FixedList.create("arity of ", tp1, "/", arity1, " is different to arity of ",
            tp2, "/", arity2));
    } else if (tp1 instanceof TypeInterface && tp2 instanceof TypeInterface) {
      TypeInterface tI1 = (TypeInterface) tp1;
      TypeInterface tI2 = (TypeInterface) tp2;

      if (tI1.numOfFields() != tI2.numOfFields())
        throw new TypeConstraintException(FixedList.create(tp1, " has different number of fields to ", tp2));
      else if (tI1.numOfTypes() != tI2.numOfTypes())
        throw new TypeConstraintException(FixedList.create(tp1, " has different number of types to ", tp2));
      else {
        for (Entry<String, IType> entry : tI1.getAllTypes().entrySet()) {
          IType lspec = entry.getValue();
          IType rspec = tI2.getType(entry.getKey());
          if (rspec == null)
            throw new TypeConstraintException(StringUtils.msg(tp2, " does not contain type ", entry.getKey()));
          else if (!lspec.kind().equals(rspec.kind()))
            throw new TypeConstraintException(StringUtils.msg(tp2, " does not consistent with ", entry.getKey()));
        }

        Map<String, IType> lFields = tI1.getAllFields();
        Map<String, IType> rFields = tI2.getAllFields();

        for (Entry<String, IType> fieldEntry : lFields.entrySet()) {
          IType otherField = rFields.get(fieldEntry.getKey());
          if (otherField == null)
            throw new TypeConstraintException(FixedList.create(tp1, "\nnot equal to ", tp2,
                "\nbecause latter does not have member for ", fieldEntry.getKey()));
          unify(fieldEntry.getValue(), otherField);
        }
      }
    } else if (tp1 instanceof ExistentialType && tp2 instanceof ExistentialType) {
      QuantifiedType u1 = (QuantifiedType) tp1;
      QuantifiedType u2 = (QuantifiedType) tp2;

      HistoricalMap<String, TypeVar> b1 = new HistoricalMap<String, TypeVar>();
      HistoricalMap<String, TypeVar> b2 = new HistoricalMap<String, TypeVar>();
      IType un1 = Refresher.refresh(tp1, b1);
      IType un2 = Refresher.refresh(tp2, b2);

      unify(un1, un2);

      if (b1.size() != b2.size())
        throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
            "\nbecause they have different numbers of quantifiers"));

      Iterator<Entry<String, TypeVar>> it1 = b1.entrySet().iterator();
      Iterator<Entry<String, TypeVar>> it2 = b2.entrySet().iterator();

      while (it1.hasNext() && it2.hasNext()) {
        Entry<String, TypeVar> vr1 = it1.next();
        Entry<String, TypeVar> vr2 = it2.next();

        if (!vr1.getValue().equals(vr2.getValue())) {
          throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
              "\nbecause quantifier %", vr1.getKey(), " should be linked to ", vr2.getKey()));
        } else if (!TypeUtils.isTypeVar(vr1.getValue()))
          throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
              "\nbecause quantifier %", vr1.getKey(), " should not be constrained"));

      }

      // Check type constraints ... the number of type constraints should not change with each
      // bound variable

      IType E1 = u1;
      while (E1 instanceof ExistentialType) {
        QuantifiedType e = (QuantifiedType) E1;
        TypeVar tv = e.getBoundVar();
        int conCount = tv.numConstraints();
        TypeVar btv = b1.get(tv.getVarName());
        if (btv.numConstraints() != conCount)
          throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
              "\nbecause quantifier %", tv.getVarName(), " should not be constrained"));
        E1 = e.getBoundType();
      }

      IType E2 = u2;
      while (E2 instanceof ExistentialType) {
        QuantifiedType e = (QuantifiedType) E2;
        TypeVar tv = e.getBoundVar();
        int conCount = tv.numConstraints();
        TypeVar btv = b2.get(tv.getVarName());
        if (btv.numConstraints() != conCount)
          throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2,
              "\nbecause quantifier %", tv.getVarName(), " should not be constrained"));
        E2 = e.getBoundType();
      }
    } else
      throw new TypeConstraintException(FixedList.create(tp1, " does not unify with ", tp2));
  }

  private IType checkAlias(IType type) throws TypeConstraintException
  {
    type = TypeUtils.deRef(type);
    if (type instanceof TypeExp) {
      TypeExp tExp = (TypeExp) type;
      ITypeDescription spec = cxt.getTypeDescription(tExp.typeLabel());
      if (spec instanceof ITypeAlias)
        return ((ITypeAlias) spec).apply(type, loc, cxt);
    } else if (type instanceof Type) {
      Type t = (Type) type;
      ITypeDescription spec = cxt.getTypeDescription(t.typeLabel());
      if (spec instanceof ITypeAlias)
        return ((ITypeAlias) spec).apply(type, loc, cxt);
    }
    return type;
  }

  @SuppressWarnings("unused")
  private Map<String, IType> rename(Map<String, IType> fields, Map<IType, IType> map)
  {
    Map<String, IType> newMap = new HashMap<String, IType>();
    for (Entry<String, IType> entry : fields.entrySet())
      newMap.put(entry.getKey(), TypeSubstitute.rename(entry.getValue(), map));
    return newMap;
  }

  private void bindVar(TypeVar v1, IType tp) throws TypeConstraintException
  {
    if (OccursCheck.occursCheck(tp, v1))
      throw new TypeConstraintException("type would be circular");
    else {
      int reset = v1.bind(tp, loc, cxt, allow);
      resets.put(v1, reset);
    }
  }
}
