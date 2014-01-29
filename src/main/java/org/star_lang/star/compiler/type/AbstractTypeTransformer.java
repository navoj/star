package org.star_lang.star.compiler.type;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import com.starview.platform.data.type.ContractConstraint;
import com.starview.platform.data.type.ExistentialType;
import com.starview.platform.data.type.FieldConstraint;
import com.starview.platform.data.type.FieldTypeConstraint;
import com.starview.platform.data.type.HasKind;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeConstraint;
import com.starview.platform.data.type.InstanceOf;
import com.starview.platform.data.type.TupleConstraint;
import com.starview.platform.data.type.Type;
import com.starview.platform.data.type.TypeExp;
import com.starview.platform.data.type.TypeInterfaceType;
import com.starview.platform.data.type.TypeTransformer;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;

/**
 * Abstract basis for type transformation
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
public abstract class AbstractTypeTransformer<X> implements TypeTransformer<IType, ITypeConstraint, X>
{
  private final Stack<String> exclusions;

  protected AbstractTypeTransformer(Stack<String> exclusions)
  {
    this.exclusions = exclusions;
  }

  @Override
  public IType transformSimpleType(Type t, X cxt)
  {
    return t;
  }

  @Override
  public IType transformTypeExp(TypeExp t, X cxt)
  {
    IType tCon = t.getTypeCon().transform(this, cxt);
    boolean clean = tCon == t.getTypeCon();
    IType args[] = t.getTypeArgs();
    IType tArgs[] = new IType[t.typeArity()];
    for (int ix = 0; ix < tArgs.length; ix++) {
      IType tA = tArgs[ix] = args[ix].transform(this, cxt);
      clean &= tA == args[ix];
    }
    if (clean)
      return t;
    else
      return new TypeExp(tCon, tArgs);
  }

  @Override
  public IType transformTypeInterface(TypeInterfaceType t, X cxt)
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
  public IType transformTypeVar(TypeVar v, X cxt)
  {
    IType t = TypeUtils.deRef(v);
    if (t instanceof TypeVar) {
      v = (TypeVar) t;
      if (exclusions.contains(v.getVarName()))
        return v;
      else {
        return v;
      }
    } else
      return t.transform(this, cxt);
  }

  @Override
  public IType transformExistentialType(ExistentialType t, X cxt)
  {
    exclusions.push(t.getBoundVar().getVarName());
    IType boundType = t.getBoundType().transform(this, cxt);
    exclusions.pop();
    if (boundType == t.getBoundType())
      return t;
    else
      return new ExistentialType(t.getBoundVar(), boundType);
  }

  @Override
  public IType transformUniversalType(UniversalType t, X cxt)
  {
    exclusions.push(t.getBoundVar().getVarName());
    IType boundType = t.getBoundType().transform(this, cxt);
    exclusions.pop();
    if (boundType == t.getBoundType())
      return t;
    else
      return new UniversalType(t.getBoundVar(), boundType);
  }

  @Override
  public ITypeConstraint transformContractConstraint(ContractConstraint con, X cxt)
  {
    IType cType = con.getContract().transform(this, cxt);
    if (cType == con.getContract())
      return con;
    else
      return new ContractConstraint((TypeExp) cType);
  }

  @Override
  public ITypeConstraint transformHasKindConstraint(HasKind has, X cxt)
  {
    IType cV = has.getVar().transform(this, cxt);
    if (cV == has.getVar())
      return has;
    else
      return new HasKind((TypeVar) cV, has.getKind());
  }

  @Override
  public ITypeConstraint transformInstanceOf(InstanceOf inst, X cxt)
  {
    IType cV = inst.getVar().transform(this, cxt);
    IType in = inst.getType().transform(this, cxt);
    if (cV == inst.getVar() && in == inst.getType())
      return inst;
    else
      return new InstanceOf((TypeVar) cV, in);
  }

  @Override
  public ITypeConstraint transformFieldConstraint(FieldConstraint fc, X cxt)
  {
    TypeVar v = (TypeVar) fc.getVar().transform(this, cxt);
    IType t = fc.getType().transform(this, cxt);
    if (t == fc.getType() && v == fc.getVar())
      return fc;
    else
      return new FieldConstraint(v, fc.getField(), t);
  }

  @Override
  public ITypeConstraint transformFieldTypeConstraint(FieldTypeConstraint tc, X cxt)
  {
    TypeVar v = (TypeVar) tc.getVar().transform(this, cxt);
    IType t = tc.getType().transform(this, cxt);
    if (t == tc.getType() && v == tc.getVar())
      return tc;
    else
      return new FieldTypeConstraint(v, tc.getName(), t);
  }

  @Override
  public ITypeConstraint transformTupleContraint(TupleConstraint t, X cxt)
  {
    IType tV = t.getVar().transform(this, cxt);
    if (tV == t.getVar())
      return t;
    else
      return new TupleConstraint((TypeVar) tV);
  }
}
