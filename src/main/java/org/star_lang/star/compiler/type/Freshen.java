package org.star_lang.star.compiler.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.type.FindTypeVars.VarHandler;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.HistoricalMap;
import org.star_lang.star.compiler.util.Lambda;
import org.star_lang.star.compiler.util.Lambda2;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.indextree.IndexSet;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.InstanceOf;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.QuantifiedType;
import org.star_lang.star.data.type.Quantifier;
import org.star_lang.star.data.type.TupleConstraint;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeTransformer;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.type.Quantifier.Existential;
import org.star_lang.star.data.type.Quantifier.Universal;

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
public class Freshen implements TypeTransformer<IType, ITypeConstraint, IndexSet<String>>
{
  private final Map<String, Quantifier> bound;
  private final Set<IType> vars;

  /**
   * 'Refresh' a type expression, generating a new copy of the type.
   * 
   * @param type
   * @param accessExist
   *          Variables that are existentially quantified are replaced by variables that are marked
   *          with accessExist access.
   * @param accessUniv
   *          Variables that are universally quantified are replaced by variables that are marked
   *          with accessUniv access.
   * @return a pair consisting of the rewritten type and a map of variable names to quantifiers.
   */
  public static Pair<IType, Map<String, Quantifier>> freshen(IType type, AccessMode accessExist, AccessMode accessUniv)
  {
    Map<String, Quantifier> bounds = new HashMap<>();
    IType tp = TypeUtils.deRef(type);

    while (tp instanceof ExistentialType || tp instanceof UniversalType) {
      if (tp instanceof ExistentialType) {
        QuantifiedType exists = (QuantifiedType) tp;
        TypeVar v = exists.getBoundVar();
        String varName = v.getVarName();
        TypeVar nv = new TypeVar(GenSym.genSym(varName), v.getOriginalName(), accessExist);
        bounds.put(varName, new Existential(nv));
        tp = TypeUtils.deRef(exists.getBoundType());
      } else {
        UniversalType univ = (UniversalType) tp;
        TypeVar v = univ.getBoundVar();
        TypeVar nv = new TypeVar(GenSym.genSym(v.getVarName()), v.getOriginalName(), accessUniv);
        bounds.put(v.getVarName(), new Universal(nv));
        tp = TypeUtils.deRef(univ.getBoundType());
      }
    }
    Freshen trans = new Freshen(bounds, new HashSet<IType>());
    IndexSet<String> exclusions = new IndexSet<>();
    IType transformed = TypeUtils.deRef(tp.transform(trans, exclusions));

    // We traverse the original type vars again, inserting the constraints
    tp = TypeUtils.deRef(type);
    Set<ITypeConstraint> handled = new HashSet<>();

    while (tp instanceof QuantifiedType) {
      QuantifiedType quan = (QuantifiedType) tp;
      TypeVar v = quan.getBoundVar();

      for (ITypeConstraint con : v) {
        if (!handled.contains(con)) { // avoid duplicate constraints
          handled.add(con);
          ITypeConstraint nCon = con.transform(trans, exclusions);
          if (nCon != null)
            for (TypeVar cV : con.affectedVars()) {
              Quantifier qV = bounds.get(cV.getVarName());
              qV.getVar().setConstraint(nCon);
            }
        }
      }
      tp = quan.getBoundType();
    }

    return Pair.pair(transformed, bounds);
  }

  public static IType freshenForUse(IType type)
  {
    Pair<IType, Map<String, Quantifier>> fresh = freshen(type, AccessMode.readOnly, AccessMode.readWrite);

    if (TypeUtils.isTypeInterface(fresh.left)) {
      TypeInterfaceType face = (TypeInterfaceType) TypeUtils.deRef(fresh.left);

      // Convert constructor field types to label implements constraints
      for (Entry<String, IType> tEntry : face.getAllTypes().entrySet()) {
        IType eType = TypeUtils.deRef(tEntry.getValue());
        if (eType instanceof TypeVar) {
          TypeVar tVar = (TypeVar) eType;

          for (Entry<String, IType> fEntry : face.getAllFields().entrySet()) {
            if (TypeUtils.isRecordConstructorType(fEntry.getValue())) {
              IType conType = fEntry.getValue();

              if (TypeUtils.getConstructorResultType(conType).equals(tVar)) {
                TypeInterfaceType conFace = (TypeInterfaceType) TypeUtils.getConstructorArgType(conType);
                for (Entry<String, IType> cEntry : conFace.getAllFields().entrySet())
                  tVar.setConstraint(new FieldConstraint(tVar, cEntry.getKey(), cEntry.getValue()));

                for (Entry<String, IType> cEntry : conFace.getAllTypes().entrySet())
                  tVar.setConstraint(new FieldTypeConstraint(tVar, cEntry.getKey(), cEntry.getValue()));
              }
            }
          }
        }
      }
    }

    return fresh.left;
  }

  public static IType freshenForEvidence(IType type)
  {
    return freshen(type, AccessMode.readWrite, AccessMode.readOnly).left;
  }

  public static IType openType(IType type)
  {
    Map<IType, IType> map = new HashMap<>();

    IType tp = TypeUtils.deRef(type);

    while (tp instanceof ExistentialType || tp instanceof UniversalType) {
      if (tp instanceof ExistentialType) {
        ExistentialType exists = (ExistentialType) tp;
        TypeVar v = exists.getBoundVar();
        IType nv = new Type(v.getOriginalName(), TypeUtils.typeKind(v));
        map.put(v, nv);
        tp = TypeUtils.deRef(exists.getBoundType());
      } else {
        UniversalType univ = (UniversalType) tp;
        TypeVar v = univ.getBoundVar();
        TypeVar nv = new TypeVar(GenSym.genSym(v.getVarName()), v.getOriginalName(), AccessMode.readWrite);

        Kind k = TypeUtils.typeKind(v);
        if (k != Kind.type)
          nv.setConstraint(new HasKind(nv, k));

        map.put(v, nv);
        tp = TypeUtils.deRef(univ.getBoundType());
      }
    }

    TypeSubstitute subTrans = new TypeSubstitute(map);

    IType openType = tp.transform(subTrans, null);

    // We traverse the original type vars again, inserting the constraints
    tp = TypeUtils.deRef(type);
    Set<ITypeConstraint> handled = new HashSet<>();

    while (tp instanceof QuantifiedType) {
      QuantifiedType quan = (QuantifiedType) tp;

      if (tp instanceof UniversalType) {
        TypeVar v = quan.getBoundVar();

        for (ITypeConstraint con : v) {
          if (!handled.contains(con)) { // avoid duplicate constraints
            handled.add(con);
            ITypeConstraint nCon = con.transform(subTrans, null);
            if (nCon != null) {
              for (TypeVar cV : con.affectedVars()) {
                IType qV = map.get(cV);
                if (TypeUtils.isTypeVar(qV))
                  ((TypeVar) TypeUtils.deRef(qV)).setConstraint(nCon);
              }
            }
          }
        }
      }
      tp = quan.getBoundType();
    }

    return openType;
  }

  // unseal is a little like openType except that a new type is generated for existentials
  public static IType unsealType(IType type)
  {
    Map<IType, IType> map = new HashMap<>();

    IType tp = TypeUtils.deRef(type);

    while (tp instanceof ExistentialType) {
      ExistentialType exists = (ExistentialType) tp;
      TypeVar v = exists.getBoundVar();
      IType nv = new Type(GenSym.genSym(v.getOriginalName()), TypeUtils.typeKind(v));
      map.put(v, nv);

      tp = TypeUtils.deRef(exists.getBoundType());
    }

    return TypeSubstitute.substitute(map, tp);
  }

  public static IType generalizeType(IType type, Dictionary dict)
  {
    Map<String, Quantifier> bounds = FindTypeVars.findTypeVars(type, new HandleVars());

    for (Iterator<Entry<String, Quantifier>> it = bounds.entrySet().iterator(); it.hasNext();) {
      if (dict.isTypeVarInScope(it.next().getValue().getVar()))
        it.remove();
    }

    Freshen freshen = new Freshen(bounds, new HashSet<IType>());
    IType gen = type.transform(freshen, new IndexSet<String>());

    return requant(gen, bounds);
  }

  public static IType generalizeType(IType type)
  {
    Map<String, Quantifier> bounds = FindTypeVars.findTypeVars(type, new HandleVars());

    Freshen freshen = new Freshen(bounds, new HashSet<IType>());
    IType gen = type.transform(freshen, new IndexSet<String>());

    return requant(gen, bounds);
  }

  public static IType existentializeType(TypeInterfaceType face, ITypeContext dict)
  {
    if (!face.getAllTypes().isEmpty()) {
      Map<IType, IType> subMap = new HashMap<>();
      SortedMap<String, IType> exists = new TreeMap<>();

      for (Entry<String, IType> tp : face.getAllTypes().entrySet()) {
        String name = tp.getKey();
        IType val = TypeUtils.deRef(tp.getValue());
        Kind k = TypeUtils.typeKind(val);

        if (val instanceof TypeVar) {
          TypeVar v = (TypeVar) val;
          if (!v.getVarName().equals(name)) {
            TypeVar nv = new TypeVar(name, name, AccessMode.readOnly);
            if (k != Kind.type && k != Kind.unknown)
              nv.setConstraint(new HasKind(nv, k));
            subMap.put(val, nv);
            exists.put(name, nv);
          } else
            exists.put(name, v);
        } else {
          IType tpLabel = TypeUtils.typeLabel(val);
          TypeVar nv = new TypeVar(name, name, AccessMode.readOnly);
          if (k != Kind.type && k != Kind.unknown)
            nv.setConstraint(new HasKind(nv, k));
          exists.put(name, nv);
          subMap.put(tpLabel, nv);
        }
      }

      if (!subMap.isEmpty()) {
        face = (TypeInterfaceType) TypeSubstitute
            .substitute(subMap, new TypeInterfaceType(exists, face.getAllFields()));
      }

      IType txType = face;

      for (Entry<String, IType> e : exists.entrySet())
        txType = new ExistentialType((TypeVar) e.getValue(), txType);

      return txType;
    } else
      return face;
  }

  private static class HandleVars implements VarHandler<Map<String, Quantifier>>
  {
    protected final Map<String, Quantifier> foundTypes = new HistoricalMap<>();

    @Override
    public boolean checkVar(TypeVar var)
    {
      return !foundTypes.containsKey(var.getVarName());
    }

    @Override
    public void foundVar(TypeVar var)
    {
      String name = var.getVarName();
      if (!foundTypes.containsKey(name)) {
        // var.markReadOnly();
        foundTypes.put(name, new Universal(new TypeVar(name, var.getOriginalName(), AccessMode.readOnly)));
      }
    }

    @Override
    public void foundExists(String name, TypeVar var)
    {
      String vName = var.getVarName();
      if (!foundTypes.containsKey(vName))
        foundTypes.put(vName, new Existential(new TypeVar(name, AccessMode.readOnly)));
    }

    @Override
    public Map<String, Quantifier> readOff()
    {
      return foundTypes;
    }
  }

  // We need to ensure that the quantified form honors the found order of variables
  public static IType requant(IType type, Map<String, Quantifier> quants)
  {
    return requant(type, quants.entrySet().iterator());
  }

  private static IType requant(IType type, Iterator<Entry<String, Quantifier>> it)
  {
    if (it.hasNext()) {
      Entry<String, Quantifier> n = it.next();
      return n.getValue().wrap(requant(type, it));
    } else
      return type;
  }

  private Freshen(Map<String, Quantifier> bound, Set<IType> vars)
  {
    this.bound = bound;
    this.vars = vars;
  }

  @Override
  public IType transformSimpleType(Type t, IndexSet<String> cxt)
  {
    return t;
  }

  @Override
  public IType transformTypeExp(TypeExp t, IndexSet<String> cxt)
  {
    IType con = t.getTypeCon().transform(this, cxt);
    boolean clean = con == t.getTypeCon();
    IType[] typeArgs = t.getTypeArgs();
    IType args[] = new IType[typeArgs.length];
    for (int ix = 0; ix < typeArgs.length; ix++) {
      args[ix] = typeArgs[ix].transform(this, cxt);
      clean &= args[ix] == typeArgs[ix];
    }
    if (clean)
      return t;
    else
      return new TypeExp(con, args);
  }

  @Override
  public IType transformTupleType(TupleType t, IndexSet<String> cxt)
  {
    boolean clean = true;
    IType[] typeArgs = t.getElTypes();
    IType args[] = new IType[typeArgs.length];
    for (int ix = 0; ix < typeArgs.length; ix++) {
      args[ix] = typeArgs[ix].transform(this, cxt);
      clean &= args[ix] == typeArgs[ix];
    }
    if (clean)
      return t;
    else
      return new TupleType(args);
  }

  @Override
  public IType transformTypeInterface(TypeInterfaceType t, IndexSet<String> cxt)
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
  public IType transformTypeVar(TypeVar var, IndexSet<String> exclusions)
  {
    if (!exclusions.isMember(var.getVarName())) {
      Quantifier b = bound.get(var.getVarName());
      if (b != null) {
        IType refreshed = b.getVar();
        if (var.hasConstraints() && refreshed instanceof TypeVar && !((TypeVar) refreshed).hasConstraints()
            && !vars.contains(refreshed)) {
          vars.add(refreshed);
          TypeVar refreshedVar = (TypeVar) refreshed;

          for (ITypeConstraint con : var) {
            ITypeConstraint nCon = con.transform(this, exclusions);
            if (nCon != null)
              refreshedVar.setConstraint(nCon);
          }
        } else
          vars.add(refreshed);

        return refreshed;
      }
    }
    return var;
  }

  @Override
  public IType transformExistentialType(ExistentialType t, IndexSet<String> exclusions)
  {
    return transformQuantified(t, exclusions, new Lambda<TypeVar, Quantifier>() {
      @Override
      public Quantifier apply(TypeVar v)
      {
        return new Existential(v);
      }
    }, new Lambda2<TypeVar, IType, IType>() {
      @Override
      public IType apply(TypeVar v, IType bndType)
      {
        return new ExistentialType(v, bndType);
      }
    });
  }

  @Override
  public IType transformUniversalType(UniversalType t, IndexSet<String> exclusions)
  {
    return transformQuantified(t, exclusions, new Lambda<TypeVar, Quantifier>() {
      @Override
      public Quantifier apply(TypeVar v)
      {
        return new Universal(v);
      }
    }, new Lambda2<TypeVar, IType, IType>() {
      @Override
      public IType apply(TypeVar v, IType bndType)
      {
        return new UniversalType(v, bndType);
      }
    });
  }

  private IType transformQuantified(QuantifiedType t, IndexSet<String> exclusions, Lambda<TypeVar, Quantifier> quant,
      Lambda2<TypeVar, IType, IType> quantifier)
  {
    TypeVar v = t.getBoundVar();
    String varName = v.getVarName();
    if (v.hasConstraints()) {
      TypeVar clone = new TypeVar(varName, v.getOriginalName(), v.getAccess());
      Quantifier old = bound.put(varName, quant.apply(clone));
      for (ITypeConstraint con : v)
        clone.setConstraint(con.transform(this, exclusions));
      IType bndType = t.getBoundType().transform(this, exclusions);
      if (old != null)
        bound.put(varName, old);
      else
        bound.remove(varName);
      return quantifier.apply(clone, bndType);
    } else {
      IType bndType = t.getBoundType().transform(this, exclusions);
      if (bndType != t.getBoundType())
        return quantifier.apply(v, bndType);
      else
        return t;
    }
  }

  @Override
  public ITypeConstraint transformContractConstraint(ContractConstraint con, IndexSet<String> exclusions)
  {
    IType conType = con.getContract().transform(this, exclusions);
    if (conType == con.getContract())
      return con;
    else
      return new ContractConstraint((TypeExp) conType);
  }

  @Override
  public ITypeConstraint transformHasKindConstraint(HasKind has, IndexSet<String> exclusions)
  {
    TypeVar v = (TypeVar) has.getVar().transform(this, exclusions);
    if (v == has.getVar())
      return has;
    else
      return new HasKind(v, has.getKind());
  }

  @Override
  public ITypeConstraint transformInstanceOf(InstanceOf inst, IndexSet<String> exclusions)
  {
    IType instType = inst.getType().transform(this, exclusions);
    TypeVar v = (TypeVar) inst.getVar().transform(this, exclusions);
    if (v == inst.getVar() && instType == inst.getType())
      return inst;
    else
      return new InstanceOf(v, instType);
  }

  @Override
  public ITypeConstraint transformFieldConstraint(FieldConstraint fc, IndexSet<String> exclusions)
  {
    TypeVar v = (TypeVar) fc.getVar().transform(this, exclusions);
    IType t = fc.getType().transform(this, exclusions);
    if (t == fc.getType() && v == fc.getVar())
      return fc;
    else
      return new FieldConstraint(v, fc.getField(), t);
  }

  @Override
  public ITypeConstraint transformFieldTypeConstraint(FieldTypeConstraint tc, IndexSet<String> exclusions)
  {
    TypeVar v = (TypeVar) tc.getVar().transform(this, exclusions);
    IType t = tc.getType().transform(this, exclusions);
    if (t == tc.getType() && v == tc.getVar())
      return tc;
    else
      return new FieldTypeConstraint(v, tc.getName(), t);
  }

  @Override
  public ITypeConstraint transformTupleContraint(TupleConstraint t, IndexSet<String> exclusions)
  {
    TypeVar v = (TypeVar) t.getVar().transform(this, exclusions);
    if (v == t.getVar())
      return t;
    else
      return new TupleConstraint(v);
  }

}
