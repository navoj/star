package org.star_lang.star.compiler.type;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.InstanceOf;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TupleConstraint;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.data.type.TypeExists;
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
public class TypeAliaser implements TypeTransformer<IType, ITypeConstraint, Void>
{
  private final Dictionary dict;
  private final Location loc;
  private final ErrorReport errors;
  private Stack<IType> exclusions = new Stack<>();

  public TypeAliaser(Location loc, ErrorReport errors, Dictionary dict)
  {
    this.dict = dict;
    this.loc = loc;
    this.errors = errors;
  }

  public static IType actualType(Location loc, ErrorReport errors, Dictionary dict, IType tp)
  {
    TypeAliaser sub = new TypeAliaser(loc, errors, dict);
    return tp.transform(sub, null);
  }

  private IType typeAlias(IType type)
  {
    type = TypeUtils.deRef(type);

    final String name = type.typeLabel();

    ITypeDescription typeSpec = dict.getTypeDescription(name);
    if (typeSpec instanceof ITypeAlias) {
      ITypeAlias alias = (ITypeAlias) typeSpec;
      try {
        return alias.apply(type, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(e.getWords()), loc);
        return type;
      }
    } else if (typeSpec instanceof TypeExists) {
      TypeExists exists = (TypeExists) typeSpec;
      IType eType = TypeUtils.deRef(exists.getType());

      if (type instanceof TypeExp) {
        TypeExp tE = (TypeExp) type;
        if (TypeUtils.isTypeVar(eType)) {
          TypeVar tV = (TypeVar) eType;

          Kind eK = tV.kind();
          if (eK == Kind.unknown)
            tV.setConstraint(new HasKind(tV, Kind.kind(tE.typeArity())));
          else if (!eK.check(tE.typeArity())) {
            errors.reportError(StringUtils.msg("name ", name, " expects ", eK.arity(), " type arguments"), loc);
          }
        } else
          try {
            exists.verifyType(type, loc, dict);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg(e.getWords()), loc);
          }
        return new TypeExp(eType, tE.getTypeArgs());
      } else
        return eType;
    } else if (type.kind() == Kind.type && typeSpec instanceof ITypeDescription) {
      ITypeDescription desc = (TypeDescription) typeSpec;

      try {
        Subsume.subsume(type, Freshen.freshenForUse(desc.getType()), loc, dict, true);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(e.getWords()), loc);
      }
      return type;
    } else
      return type;
  }

  @Override
  public IType transformSimpleType(Type t, Void cxt)
  {
    if (!exclusions.contains(t))
      return typeAlias(t);

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
      return typeAlias(t);
    else
      return typeAlias(new TypeExp(con, args));
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
    return v;
  }

  @Override
  public IType transformExistentialType(ExistentialType t, Void cxt)
  {
    exclusions.push(t.getBoundVar());
    IType bound = t.getBoundType().transform(this, cxt);
    exclusions.pop();
    if (bound == t.getBoundType())
      return t;
    else
      return new ExistentialType(t.getBoundVar(), bound);
  }

  @Override
  public IType transformUniversalType(UniversalType t, Void cxt)
  {
    exclusions.push(t.getBoundVar());
    IType bound = t.getBoundType().transform(this, cxt);
    exclusions.pop();
    if (bound == t.getBoundType())
      return t;
    else
      return new UniversalType(t.getBoundVar(), bound);
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
    } else
      return has;
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
