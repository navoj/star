package org.star_lang.star.compiler.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.InstanceOf;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TupleConstraint;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeTransformer;
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

public class TypeAbstract<T> implements TypeTransformer<IAbstract, IAbstract, T>
{
  private final Location loc;

  protected TypeAbstract(Location loc)
  {
    this.loc = loc;
  }

  public static <T> IAbstract typeToAbstract(Location loc, IType type, ErrorReport errors, T cxt)
  {
    TypeAbstract<T> generator = new TypeAbstract<T>(loc);
    return generator.convertType(type, cxt);
  }

  public IAbstract convertType(IType type, T cxt)
  {
    return type.transform(this, cxt);
  }

  @Override
  public IAbstract transformSimpleType(Type t, T cxt)
  {
    return Abstract.name(loc, t.typeLabel());
  }

  @Override
  public IAbstract transformTypeExp(TypeExp t, T cxt)
  {
    if (TypeUtils.isOverloadedType(t)) {
      IAbstract arg = TypeUtils.getOverloadedContract(t).transform(this, cxt);
      IAbstract res = TypeUtils.getOverloadedType(t).transform(this, cxt);

      return CafeSyntax.apply(loc, Names.ARROW, arg, res);
    } else {
      IAbstract tyCon = t.getTypeCon().transform(this, cxt);
      List<IAbstract> typeArgs = new ArrayList<IAbstract>();
      for (IType tA : t.getTypeArgs())
        typeArgs.add(tA.transform(this, cxt));

      return CafeSyntax.apply(loc, tyCon, typeArgs);
    }
  }

  @Override
  public IAbstract transformTupleType(TupleType t, T cxt)
  {
    List<IAbstract> typeArgs = new ArrayList<IAbstract>();
    for (IType tA : t.getElTypes())
      typeArgs.add(tA.transform(this, cxt));

    return CafeSyntax.apply(loc, TypeUtils.tupleLabel(t.arity()), typeArgs);
  }

  @Override
  public IAbstract transformTypeInterface(TypeInterfaceType t, T cxt)
  {
    String label = t.typeLabel();

    Map<String, IType> members = t.getAllFields();
    IType[] argTypes = new IType[members.size()];
    int ix = 0;
    for (Entry<String, IType> entry : members.entrySet()) {
      argTypes[ix++] = entry.getValue();
    }
    return TypeUtils.typeExp(label, argTypes).transform(this, cxt);
  }

  @Override
  public IAbstract transformTypeVar(TypeVar v, T cxt)
  {
    String name = v.getVarName();
    Location loc = v.getBindingLocation();
    if (loc == null)
      loc = this.loc;

    switch (v.kind().mode()) {
    case type:
    case unknown:
    default:
      return CafeSyntax.typeVar(loc, name);
    case typefunction:
      return CafeSyntax.typeFunVar(loc, name, v.typeArity());
    }
  }

  @Override
  public IAbstract transformExistentialType(ExistentialType t, T cxt)
  {
    IAbstract tV = t.getBoundVar().transform(this, cxt);

    IAbstract bound = t.getBoundType().transform(this, cxt);

    return CafeSyntax.existentialType(loc, tV, bound);
  }

  @Override
  public IAbstract transformUniversalType(UniversalType t, T cxt)
  {
    if (TypeUtils.hasContractDependencies(t) && !TypeUtils.isConstructorType(TypeUtils.unwrap(t)))
      t = (UniversalType) Over.computeDictionaryType(t, loc, AccessMode.readOnly);

    IAbstract tV = t.getBoundVar().transform(this, cxt);

    IAbstract bound = t.getBoundType().transform(this, cxt);

    return CafeSyntax.universalType(loc, tV, bound);
  }

  @Override
  public IAbstract transformContractConstraint(ContractConstraint con, T cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public IAbstract transformHasKindConstraint(HasKind has, T cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public IAbstract transformInstanceOf(InstanceOf inst, T cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public IAbstract transformFieldConstraint(FieldConstraint fc, T cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public IAbstract transformFieldTypeConstraint(FieldTypeConstraint tc, T cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public IAbstract transformTupleContraint(TupleConstraint t, T cxt)
  {
    throw new UnsupportedOperationException("not implemented");
  }
}