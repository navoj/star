package com.starview.platform.data.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import org.star_lang.star.compiler.type.TypeUtils;

/**
 * An implementation of the {@link ITypeVisitor} that is used to rewrite type expressions, in
 * particular applying type aliases.
 * 
 * It is similar to type refreshing, except that it is not type variables but type expressions that
 * are renamed.
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
  private final Map<IType, IType> bound;
  private final Stack<IType> exclusions;

  private TypeSubstitute(Map<IType, IType> bound, Stack<IType> exclusions)
  {
    this.bound = bound;
    this.exclusions = exclusions;
  }

  /**
   * Refresh a type while updating a supplied map with the variables that were found and their
   * replacements.
   * 
   * @param type
   *          the type to apply renaming to
   * @param bound
   *          a map which contains the renaming mapping
   * @return the type original expression with types substituted as determined by the mapping
   */
  public static IType rename(IType type, Map<IType, IType> bound)
  {
    Stack<IType> exclusions = new Stack<IType>();
    TypeSubstitute renamer = new TypeSubstitute(bound, exclusions);
    return type.transform(renamer, null);
  }

  @Override
  public IType transformSimpleType(Type t, Void cxt)
  {
    if (!exclusions.contains(t)) {
      IType renamed = bound.get(t);
      if (renamed != null)
        return renamed;
      else
        return t;
    } else
      return t;
  }

  @Override
  public IType transformTypeExp(TypeExp t, Void cxt)
  {
    IType tyCon = t.getTypeCon().transform(this, cxt);

    IType typeArgs[] = t.getTypeArgs();
    IType newArgs[] = new IType[typeArgs.length];
    for (int ix = 0; ix < typeArgs.length; ix++)
      newArgs[ix] = typeArgs[ix].transform(this, null);

    return TypeUtils.typeExp(tyCon, newArgs);
  }

  @Override
  public IType transformTypeInterface(TypeInterfaceType t, Void cxt)
  {
    SortedMap<String, IType> nF = new TreeMap<String, IType>();
    SortedMap<String, IType> nT = new TreeMap<String, IType>();

    for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
      nF.put(entry.getKey(), entry.getValue().transform(this, cxt));
    }
    for (Entry<String, IType> entry : t.getAllTypes().entrySet()) {
      nT.put(entry.getKey(), entry.getValue().transform(this, cxt));
    }

    return new TypeInterfaceType(nT, nF);
  }

  @Override
  public IType transformTypeVar(TypeVar v, Void cxt)
  {
    return v;
  }

  @Override
  public IType transformExistentialType(ExistentialType t, Void cxt)
  {
    int mark = exclusions.size();
    TypeVar boundVar = t.getBoundVar();
    exclusions.push(boundVar);
    IType renamed = t.getBoundType().transform(this, cxt);
    exclusions.setSize(mark);
    return new ExistentialType(boundVar, renamed);
  }

  @Override
  public IType transformUniversalType(UniversalType t, Void cxt)
  {
    int mark = exclusions.size();
    TypeVar boundVar = t.getBoundVar();
    exclusions.push(boundVar);
    IType renamed = t.getBoundType().transform(this, cxt);
    exclusions.setSize(mark);
    return new UniversalType(boundVar, renamed);
  }

  @Override
  public ITypeConstraint transformContractConstraint(ContractConstraint con, Void cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public ITypeConstraint transformHasKindConstraint(HasKind has, Void cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public ITypeConstraint transformInstanceOf(InstanceOf inst, Void cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public ITypeConstraint transformFieldConstraint(FieldConstraint fc, Void cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public ITypeConstraint transformFieldTypeConstraint(FieldTypeConstraint tc, Void cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public ITypeConstraint transformTupleContraint(TupleConstraint t, Void cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }
}
