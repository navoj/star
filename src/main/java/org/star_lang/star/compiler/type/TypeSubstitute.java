package org.star_lang.star.compiler.type;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.InstanceOf;
import org.star_lang.star.data.type.TupleConstraint;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeTransformer;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/**
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
public class TypeSubstitute implements TypeTransformer<IType, ITypeConstraint, Void>
{
  private Map<IType, IType> substitution;
  private Stack<IType> exclusions;
  private Set<IType> visited = new HashSet<>();

  public TypeSubstitute(Map<IType, IType> substitutions)
  {
    this.substitution = substitutions;
    this.exclusions = new Stack<IType>();
  }

  public static IType substitute(Map<IType, IType> map, IType tp)
  {
    TypeSubstitute sub = new TypeSubstitute(map);
    return tp.transform(sub, null);
  }

  @Override
  public IType transformSimpleType(Type t, Void cxt)
  {
    if (!exclusions.contains(t)) {
      if (substitution.containsKey(t))
        return substitution.get(t);
    }
    return t;
  }

  @Override
  public IType transformTypeExp(TypeExp t, Void cxt)
  {
    IType con = t.getTypeCon().transform(this, cxt);
    boolean clean = con == t.getTypeCon();
    IType args[] = new IType[t.typeArity()];
    for (int ix = 0; ix < t.typeArity(); ix++) {
      args[ix] = t.getTypeArg(ix).transform(this, cxt);
      clean &= args[ix] == t.getTypeArg(ix);
    }
    if (clean)
      return t;
    else
      return new TypeExp(con, args);
  }

  @Override
  public IType transformTypeInterface(TypeInterfaceType t, Void cxt)
  {
    SortedMap<String, IType> nF = new TreeMap<>();
    SortedMap<String, IType> nT = new TreeMap<>();
    boolean clean = true;

    for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
      IType tA = entry.getValue().transform(this, cxt);
      clean &= tA == entry.getValue();
      nF.put(entry.getKey(), tA);
    }
    for (Entry<String, IType> entry : t.getAllTypes().entrySet()) {
      IType tA = entry.getValue().transform(this, cxt);
      clean &= tA == entry.getValue();
      nT.put(entry.getKey(), tA);
    }

    if (clean)
      return t;
    else
      return new TypeInterfaceType(nT, nF);
  }

  @Override
  public IType transformTypeVar(TypeVar v, Void cxt)
  {
    if (!exclusions.contains(v)) {
      IType sub = substitution.get(v);

      if (sub != null) {
        if (sub instanceof TypeVar) {
          if (!visited.contains(sub)) {
            visited.add(sub);
            TypeVar tv = (TypeVar) sub;
            if (v.hasConstraints() && !tv.hasConstraints())
              visitTypeVarConstraints(v, tv, cxt);
          }
        }
        return sub;
      }
    }
    return v;
  }

  private void visitTypeVarConstraints(TypeVar v, TypeVar tv, Void cxt)
  {
    for (ITypeConstraint con : v) {
      ITypeConstraint nCon = con.transform(this, cxt);
      if (nCon != null)
        tv.setConstraint(nCon);
    }
  }

  @Override
  public IType transformExistentialType(ExistentialType t, Void cxt)
  {
    TypeVar boundVar = t.getBoundVar();
    IType boundType = t.getBoundType();

    if (boundVar.hasConstraints()) {
      TypeVar bVar = new TypeVar(boundVar.getVarName(), boundVar.getOriginalName(), boundVar.getAccess());
      IType old = substitution.put(boundVar, bVar);
      visitTypeVarConstraints(boundVar, bVar, cxt);
      IType bound = boundType.transform(this, cxt);
      if (old != null)
        substitution.put(boundVar, old);
      else
        substitution.remove(boundVar);
      return new ExistentialType(bVar, bound);
    } else {
      exclusions.push(boundVar);
      IType bound = boundType.transform(this, cxt);
      exclusions.pop();
      if (bound == boundType)
        return t;
      else
        return new ExistentialType(boundVar, bound);
    }
  }

  @Override
  public IType transformUniversalType(UniversalType t, Void cxt)
  {
    TypeVar boundVar = t.getBoundVar();
    IType boundType = t.getBoundType();

    if (boundVar.hasConstraints()) {
      TypeVar bVar = new TypeVar(boundVar.getVarName(), boundVar.getOriginalName(), boundVar.getAccess());
      IType old = substitution.put(boundVar, bVar);
      visitTypeVarConstraints(boundVar, bVar, cxt);
      IType bound = boundType.transform(this, cxt);
      if (old != null)
        substitution.put(boundVar, old);
      else
        substitution.remove(boundVar);
      return new UniversalType((TypeVar) bVar, bound);
    } else {
      exclusions.push(boundVar);
      IType bound = boundType.transform(this, cxt);

      exclusions.pop();
      if (bound == boundType)
        return t;
      else
        return new UniversalType((TypeVar) boundVar, bound);
    }
  }

  @Override
  public ITypeConstraint transformContractConstraint(ContractConstraint contract, Void cxt)
  {
    TypeExp con = (TypeExp) contract.getContract().transform(this, cxt);
    if (con == contract.getContract())
      return contract;
    return new ContractConstraint(con);
  }

  @Override
  public ITypeConstraint transformHasKindConstraint(HasKind has, Void cxt)
  {
    TypeVar var = has.getVar();
    if (!exclusions.contains(var)) {
      IType v = var.transform(this, cxt);
      if (v == var)
        return has;
      else if (v instanceof TypeVar)
        return new HasKind((TypeVar) v, has.getKind());
      else
        return null;
    } else {
      if (substitution.containsKey(var))
        return new HasKind((TypeVar) substitution.get(var), has.getKind());
      return has;
    }
  }

  @Override
  public ITypeConstraint transformInstanceOf(InstanceOf inst, Void cxt)
  {
    TypeVar v = (TypeVar) inst.getVar().transform(this, cxt);
    IType tp = inst.getType().transform(this, cxt);
    if (v == inst.getVar() && tp == inst.getType())
      return inst;
    else
      return new InstanceOf(v, tp);
  }

  @Override
  public ITypeConstraint transformFieldConstraint(FieldConstraint fc, Void cxt)
  {
    TypeVar v = (TypeVar) fc.getVar().transform(this, cxt);
    IType t = fc.getType().transform(this, cxt);
    if (v == fc.getVar() && t == fc.getType())
      return fc;
    else
      return new FieldConstraint(v, fc.getField(), t);
  }

  @Override
  public ITypeConstraint transformFieldTypeConstraint(FieldTypeConstraint tc, Void cxt)
  {
    TypeVar v = (TypeVar) tc.getVar().transform(this, cxt);
    IType t = tc.getType().transform(this, cxt);
    if (v == tc.getVar() && t == tc.getType())
      return tc;
    else
      return new FieldTypeConstraint(v, tc.getName(), t);
  }

  @Override
  public ITypeConstraint transformTupleContraint(TupleConstraint t, Void cxt)
  {
    TypeVar v = (TypeVar) t.getVar().transform(this, cxt);
    if (v == t.getVar())
      return t;
    else
      return new TupleConstraint(v);
  }
}
