package org.star_lang.star.compiler.type;

import java.util.Map.Entry;
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
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeTransformer;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

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
  public IType transformTupleType(TupleType t, X cxt)
  {
    boolean clean = true;
    IType args[] = t.getElTypes();
    IType tArgs[] = new IType[t.arity()];
    for (int ix = 0; ix < tArgs.length; ix++) {
      IType tA = tArgs[ix] = args[ix].transform(this, cxt);
      clean &= tA == args[ix];
    }
    if (clean)
      return t;
    else
      return new TupleType(tArgs);
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
