package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringSequence;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.QuantifiedType;
import org.star_lang.star.data.type.Quantifier;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Quantifier.Existential;
import org.star_lang.star.data.type.Quantifier.Universal;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.Cons;
import org.star_lang.star.data.value.Option;
import org.star_lang.star.data.value.Result;
import org.star_lang.star.operators.Intrinsics;

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

public class TypeUtils {

  public static boolean isType(IType type, String name) {
    type = deRef(type);
    return type.typeLabel().equals(name);
  }

  public static boolean isType(IType type, String name, int arity) {
    type = unwrap(type);
    return type.typeLabel().equals(name) && TypeUtils.typeArity(type) == arity;
  }

  public static boolean isType(IType type, IType tf, int arity) {
    type = unwrap(type);
    if (type instanceof TypeExp) {
      TypeExp tpExp = (TypeExp) type;
      IType tyCon = deRef(tpExp.getTypeCon());
      if (tyCon instanceof Type) {
        Type ty = (Type) tyCon;
        return ty.typeLabel().equals(tf.typeLabel()) && ty.getArity() == arity && tpExp.typeArity() == arity;
      }
    }
    return false;
  }

  public static boolean isTypeExp(IType type) {
    type = deRef(type);
    return type instanceof TypeExp;
  }

  public static IType getTypeArg(IType type, int ix) {
    type = deRef(type);
    assert type instanceof TypeExp;

    return ((TypeExp) type).getTypeArg(ix);
  }

  public static boolean isTypeVar(IType type) {
    type = deRef(type);
    return type instanceof TypeVar;
  }

  public static int typeArity(IType type) {
    type = unwrap(type);

    if (type instanceof Type)
      return 0;
    else if (type instanceof TypeExp)
      return ((TypeExp) type).typeArity();
    else if (type instanceof TupleType)
      return ((TupleType) type).arity();
    else if (type instanceof TypeInterfaceType)
      return ((TypeInterfaceType) type).arity();
    else
      return -1;
  }

  public static Kind typeKind(IType type) {
    type = unwrap(type);
    if (type instanceof TypeVar) {
      TypeVar tv = (TypeVar) type;
      for (ITypeConstraint con : tv)
        if (con instanceof HasKind)
          return ((HasKind) con).getKind();
    }
    return type.kind();
  }

  public static IType typeLabel(IType type) {
    type = unwrap(type);
    if (type instanceof TypeExp)
      return ((TypeExp) type).getTypeCon();
    else if (type instanceof TupleType)
      return new Type(tupleLabel(tupleTypeArity(type)));
    else if (type instanceof Type)
      return type;

    return null;
  }

  public static IType[] typeArgs(IType type) {
    type = deRef(type);

    if (type instanceof TypeExp)
      return ((TypeExp) type).getTypeArgs();
    else if (type instanceof TupleType)
      return ((TupleType) type).getElTypes();
    else
      return new IType[]{};
  }

  public static IType getTypeCon(IType type) {
    if (type instanceof Type)
      return type;
    else if (type instanceof TypeExp)
      return ((TypeExp) type).getTypeCon();
    else if (type instanceof TupleType)
      return new Type(tupleLabel(tupleTypeArity(type)));
    else
      return type;
  }

  public static IType typeCon(String label, int arity) {
    return new Type(label, Kind.kind(arity));
  }

  public static IType typeExp(String name, IType... args) {
    assert !isTupleLabel(name);

    if (args.length == 0)
      return new Type(name);
    else
      return new TypeExp(name, args);
  }

  public static IType typeExp(IType con, IType... args) {
    if (!con.kind().check(args.length))
      throw new IllegalArgumentException("bad type constructor");

    if (con instanceof Type && args.length == 0)
      return con;
    else
      return new TypeExp(con, args);
  }

  public static IType typeExp(String label, List<IType> args) {
    return typeExp(new Type(label, Kind.kind(args.size())), args);
  }

  public static IType typeExp(IType con, List<IType> args) {
    if (!con.kind().check(args.size()))
      throw new IllegalArgumentException(StringUtils.msg(con, " expects ", con.kind().arity(), " type arguments, got ",
          args.size()));
    else if (con instanceof Type && args.isEmpty())
      return con;
    else
      return new TypeExp(con, args.toArray(new IType[args.size()]));
  }

  public static IType functionType(IType... argTypes) {
    IType args[] = new IType[argTypes.length - 1];
    for (int ix = 0; ix < args.length; ix++)
      args[ix] = argTypes[ix];
    return typeExp(StandardNames.FUN_ARROW, tupleType(args), argTypes[args.length]);
  }

  public static IType funcType(IType argTypes, IType resType) {
    return typeExp(StandardNames.FUN_ARROW, argTypes, resType);
  }

  public static IType functionType(IType[] argTypes, IType resType) {
    return typeExp(StandardNames.FUN_ARROW, tupleType(argTypes), resType);
  }

  public static IType funType(TypeInterfaceType face, IType resType) {
    return typeExp(StandardNames.FUN_ARROW, face, resType);
  }

  public static IType functionType(List<IType> argTypes, IType resType) {
    return typeExp(StandardNames.FUN_ARROW, tupleType(argTypes), resType);
  }

  public static IType[] getFunArgTypes(IType type) {
    type = unwrap(type);

    assert isFunType(type) && isTupleType(getTypeArg(type, 0));
    return tupleTypes(getTypeArg(type, 0));
  }

  public static IType getFunResultType(IType type) {
    type = unwrap(type);

    assert isFunType(type) : "not function type but " + type;

    return getTypeArg(type, 1);
  }

  public static IType getFunArgType(IType type) {
    type = unwrap(type);
    assert isFunType(type);
    return deRef(getTypeArg(type, 0));
  }

  public static boolean isFunType(IType type) {
    return isType(unwrap(type), StandardNames.FUN_ARROW, 2);
  }

  public static boolean isFunctionType(IType type) {
    type = unwrap(type);
    return isFunType(type) && !isProcedureReturnType(getFunResultType(type));
  }

  public static IType actionType(IType resType) {
    return typeExp(StandardNames.ACTION_TYPE, resType);
  }

  public static boolean isTupleFunctionType(IType type) {
    type = unwrap(type);
    return isFunType(type) && isTupleType(getFunArgType(type));
  }

  public static boolean isRecordFunctionType(IType type) {
    type = unwrap(type);
    return isFunType(type) && isTypeInterface(getFunArgType(type));
  }

  public static TypeInterface getRecordFunctionArgs(IType type) {
    type = unwrap(type);
    assert isRecordFunctionType(type);
    return (TypeInterface) unwrap(getFunArgType(type));
  }

  public static int arityOfFunctionType(IType type) {
    assert isFunType(type);

    type = unwrap(type);

    IType argTypes = getTypeArg(type, 0);
    if (isTupleType(argTypes))
      return tupleTypeArity(argTypes);
    else if (isTypeInterface(argTypes))
      return typeInterfaceSize(argTypes);
    else
      throw new IllegalStateException("invalid form of function type");
  }

  public static boolean isConstructorType(IType type) {
    return isType(unwrap(type), StandardNames.CONSTRUCTOR_TYPE, 2);
  }

  public static IType constructorType(List<IType> argTypes, IType resType) {
    return typeExp(StandardNames.CONSTRUCTOR_TYPE, tupleType(argTypes), resType);
  }

  public static IType constructorType(IType argTypes[], IType resType) {
    return typeExp(StandardNames.CONSTRUCTOR_TYPE, tupleType(argTypes), resType);
  }

  public static IType constructorType(IType argsType, IType resType) {
    return typeExp(StandardNames.CONSTRUCTOR_TYPE, argsType, resType);
  }

  public static IType funTypeFromConType(IType type) {
    assert isConstructorType(type);
    return funcType(getConstructorArgType(type), getConstructorResultType(type));
  }

  public static IType tupleConstructorType(IType... types) {
    assert types.length > 0;
    IType argTypes[] = new IType[types.length - 1];
    for (int ix = 0; ix < types.length - 1; ix++)
      argTypes[ix] = types[ix];
    return typeExp(StandardNames.CONSTRUCTOR_TYPE, tupleType(argTypes), types[types.length - 1]);
  }

  public static IType constructorType(IType resType) {
    return typeExp(StandardNames.CONSTRUCTOR_TYPE, tupleType(), resType);
  }

  public static IType[] getConstructorArgTypes(IType type) {
    type = unwrap(type);

    IType arg = unwrap(getTypeArg(type, 0));
    assert isConstructorType(type);

    if (isTupleType(arg))
      return tupleTypes(arg);
    else {
      assert isTypeInterface(arg);
      return tupleOfInterface((TypeInterfaceType) arg);
    }
  }

  public static IType getConstructorResultType(IType type) {
    type = unwrap(type);

    assert isConstructorType(type) : "not constructor type but " + type;

    return getTypeArg(type, 1);
  }

  public static IType getConstructorArgType(IType type) {
    type = unwrap(type);
    assert isConstructorType(type);
    return deRef(getTypeArg(type, 0));
  }

  public static boolean isTupleConstructorType(IType type) {
    type = unwrap(type);
    return isConstructorType(type) && isTupleType(getConstructorArgType(type));
  }

  public static boolean isRecordConstructorType(IType type) {
    type = unwrap(type);
    return isConstructorType(type) && isTypeInterface(getConstructorArgType(type));
  }

  public static TypeInterfaceType getRecordConstructorArgs(IType type) {
    type = unwrap(type);
    assert isRecordConstructorType(type);
    return (TypeInterfaceType) unwrap(getConstructorArgType(type));
  }

  public static int arityOfConstructorType(IType type) {
    assert isConstructorType(type);

    type = unwrap(type);

    IType argTypes = getTypeArg(type, 0);
    if (isTupleType(argTypes))
      return tupleTypeArity(argTypes);
    else if (isTypeInterface(argTypes))
      return typeInterfaceSize(argTypes);
    else
      throw new IllegalStateException("invalid form of constructor type");
  }

  public static boolean isProcedureType(IType type) {
    type = unwrap(type);

    return isFunType(type) && isProcedureReturnType(getTypeArg(type, 1));
  }

  public static IType procedureType(IType... argTypes) {
    return functionType(argTypes, StandardTypes.unitType);
  }

  public static IType procedureType(List<IType> argTypes) {
    return functionType(argTypes, StandardTypes.unitType);
  }

  public static IType[] getProcedureArgTypes(IType type) {
    return getFunArgTypes(type);
  }

  public static IType getProcArgType(IType type) {
    return getFunArgType(type);
  }

  public static int arityOfProcedureType(IType type) {
    type = deRef(type);

    assert isProcedureType(type);
    return arityOfFunctionType(type);
  }

  public static boolean isProcedureReturnType(IType type) {
    type = deRef(type);
    return type.equals(StandardTypes.unitType);
  }

  public static boolean isProgramType(IType t) {
    return isFunType(t) || isPatternType(t) || isConstructorType(t);
  }

  public static boolean isDetermines(IType t) {
    t = deRef(t);
    return isType(t, StandardNames.DETERMINES);
  }

  public static IType[] determinedTypes(IType t) {
    t = deRef(t);
    assert isDetermines(t);

    return typeArgs(t);
  }

  public static IType threadType(IType argType) {
    return typeExp(StandardNames.THREAD, argType);
  }

  public static boolean isTypeInterface(IType type) {
    type = unwrap(type);

    return type instanceof TypeInterface;
  }

  public static int typeInterfaceSize(IType type) {
    type = unwrap(type);
    assert type instanceof TypeInterface;
    return ((TypeInterface) type).numOfFields();
  }

  public static IType interfaceOfType(Location loc, IType tipe, Dictionary dict,ErrorReport errors) {
    List<Quantifier> quants = new ArrayList<>();
    IType type = unwrap(tipe, quants);

    if (type instanceof TypeInterfaceType)
      return deRef(tipe);
    else if (type instanceof TypeVar) {
      SortedMap<String, IType> members = new TreeMap<>();
      SortedMap<String, IType> types = new TreeMap<>();

      for (ITypeConstraint constraint : (TypeVar) type) {
        if (constraint instanceof FieldConstraint) {
          FieldConstraint fieldCon = (FieldConstraint) constraint;
          members.put(fieldCon.getField(), fieldCon.getType());
        } else if (constraint instanceof FieldTypeConstraint) {
          FieldTypeConstraint fieldCon = (FieldTypeConstraint) constraint;
          types.put(fieldCon.getName(), fieldCon.getType());
        }
      }
      return requant(quants, new TypeInterfaceType(types, members));
    } else {
      ITypeDescription desc = dict.getTypeDescription(type.typeLabel());

      if (desc instanceof IAlgebraicType) {
        IAlgebraicType algDesc = (IAlgebraicType) desc;
        SortedMap<String, IType> members = new TreeMap<>();
        SortedMap<String, IType> types = new TreeMap<>();
        for (IValueSpecifier spec : algDesc.getValueSpecifiers()) {
          if (spec instanceof RecordSpecifier) {
            RecordSpecifier record = (RecordSpecifier) spec;

            IType conType = Freshen.freshenForUse(record.getConType());
            try {
              unify(getConstructorResultType(conType), type, loc, dict);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg(e.getWords()), loc);
            }

            IType conArgType = TypeUtils.getConstructorArgType(conType);

            while (conArgType instanceof ExistentialType) {
              ExistentialType exists = (ExistentialType) conArgType;
              quants.add(new Existential(exists.getBoundVar()));
              conArgType = exists.getBoundType();
            }

            TypeInterfaceType face = (TypeInterfaceType) conArgType;

            for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
              IType fldType = members.get(entry.getKey());
              if (fldType != null)
                try {
                  Subsume.same(entry.getValue(), fldType, loc, dict);
                } catch (TypeConstraintException e) {
                  errors.reportError(StringUtils.msg(e.getWords()), loc);
                }
              else
                members.put(entry.getKey(), entry.getValue());
            }

            for (Entry<String, IType> entry : face.getAllTypes().entrySet()) {
              String tpName = entry.getKey();
              IType tp = entry.getValue();
              IType typeField = types.get(tpName);
              if (typeField != null)
                assert typeField.equals(tp) : "internal error";
              else
                types.put(tpName, tp);
            }
          }
        }
        return requant(quants, new TypeInterfaceType(types, members));
      }
    }
    return new TypeInterfaceType();
  }

  public static IType[] tupleOfInterface(TypeInterfaceType face) {
    SortedMap<String, IType> fieldMap = face.getAllFields();
    IType[] els = new IType[fieldMap.size()];

    int ix = 0;
    for (IType elType : fieldMap.values())
      els[ix++] = elType;

    return els;
  }

  public static IType getInterfaceField(IType type, String name) {
    type = deRef(type);
    if (type instanceof TypeInterfaceType)
      return ((TypeInterface) type).getFieldType(name);
    return null;
  }

  public static IType getInterfaceMemberType(IType type, String name) {
    type = deRef(type);
    if (type instanceof TypeInterfaceType)
      return ((TypeInterface) type).getType(name);
    return null;
  }

  public static SortedMap<String, IType> getInterfaceFields(IType type) {
    type = deRef(type);
    assert type instanceof TypeInterfaceType;
    return ((TypeInterfaceType) type).getAllFields();
  }

  public static String anonRecordLabel(IType type) {
    TypeInterfaceType face = (TypeInterfaceType) unwrap(type);
    return anonRecordLabel(face.getAllTypes(), face.getAllFields());
  }

  public static String anonRecordLabel(Map<String, IType> types, Map<String, IType> members) {
    StringBuilder bldr = new StringBuilder();
    bldr.append(StandardNames.RECORD_LABEL);
    int hash = 0;

    for (Entry<String, IType> entry : types.entrySet())
      hash = hash * 37 + entry.getKey().hashCode();
    for (Entry<String, IType> entry : members.entrySet()) {
      String fieldName = entry.getKey();
      hash = hash * 37 + fieldName.hashCode();
    }
    bldr.append(Math.abs(hash));
    bldr.append("_");
    bldr.append(members.size());

    return bldr.toString();
  }

  public static String anonRecordLabel(String[] types, String[] members) {
    StringBuilder bldr = new StringBuilder();
    bldr.append(StandardNames.RECORD_LABEL);
    int hash = 0;

    for (String entry : types)
      hash = hash * 37 + entry.hashCode();
    for (String entry : members) {
      hash = hash * 37 + entry.hashCode();
    }
    bldr.append(Math.abs(hash));
    bldr.append("_");
    bldr.append(members.length);

    return bldr.toString();
  }

  public static IType deRef(IType t) {
    if (t instanceof TypeVar)
      return ((TypeVar) t).deRef();
    else
      return t;
  }

  public static int deRefChainLength(IType t) {
    int len = 0;
    while (t instanceof TypeVar) {
      len++;
      t = ((TypeVar) t).getBoundValue();
    }
    return len;
  }

  public static void unify(IType t1, IType t2, Location loc, Dictionary cxt) throws TypeConstraintException {
    UnifyTypes.unify(t1, t2, loc, cxt, false);
  }

  public static boolean isUniversalType(IType type) {
    type = deRef(type);

    return type instanceof UniversalType;
  }

  public static IType unwrap(IType type) {
    type = deRef(type);
    while (type instanceof QuantifiedType)
      type = deRef(((QuantifiedType) type).getBoundType());

    return type;
  }

  public static IType unwrap(IType type, Collection<TypeVar> tVars) {
    type = deRef(type);

    while (type instanceof QuantifiedType) {
      QuantifiedType univ = (QuantifiedType) type;
      tVars.add(univ.getBoundVar());
      type = univ.getBoundType();
    }

    return type;
  }

  public static IType unwrap(IType type, List<Quantifier> quants) {
    type = deRef(type);

    while (type instanceof QuantifiedType) {
      if (type instanceof UniversalType) {
        UniversalType univ = (UniversalType) type;
        quants.add(new Universal(univ.getBoundVar()));
        type = univ.getBoundType();
      } else {
        ExistentialType exist = (ExistentialType) type;
        quants.add(new Existential(exist.getBoundVar()));
        type = exist.getBoundType();
      }
    }

    return type;
  }

  public static IType unwrapQuant(IType type, Map<String, Quantifier> quants) {
    type = deRef(type);

    while (type instanceof QuantifiedType) {
      if (type instanceof UniversalType) {
        UniversalType univ = (UniversalType) type;
        quants.put(univ.getBoundVar().getVarName(), new Universal(univ.getBoundVar()));
        type = univ.getBoundType();
      } else {
        ExistentialType exist = (ExistentialType) type;
        quants.put(exist.getBoundVar().getVarName(), new Existential(exist.getBoundVar()));
        type = exist.getBoundType();
      }
    }

    return type;
  }

  public static IType unwrap(IType type, Map<String, TypeVar> tVars) {
    type = deRef(type);

    while (type instanceof UniversalType) {
      UniversalType univ = (UniversalType) type;
      TypeVar bndVar = univ.getBoundVar();
      tVars.put(bndVar.getVarName(), bndVar);
      type = univ.getBoundType();
    }

    return type;
  }

  public static IType rewrap(IType type, Map<String, TypeVar> tVars) {
    return UniversalType.univ(tVars.values(), type);
  }

  public static IType requant(List<Quantifier> quants, IType type) {
    for (int ix = quants.size(); ix > 0; ix--) {
      type = quants.get(ix - 1).wrap(type);
    }
    return type;
  }

  public static IType dictionaryType(IType ky, IType vl) {
    return typeExp(StandardNames.DICTIONARY, ky, vl);
  }

  public static IType setType(IType vl) {
    return typeExp(StandardNames.SET, vl);
  }

  public static IType optionType(IType vl) {
    return typeExp(Option.typeLabel, vl);
  }

  public static IType resultType(IType vl) {
    return typeExp(Result.typeLabel, vl);
  }

  public static boolean isUnitType(IType tp) {
    return deRef(tp).equals(StandardTypes.unitType);
  }

  public static boolean unifyUnitType(IType tp, Location loc, Dictionary cxt) {
    return UnifyTypes.testUnify(tp, StandardTypes.unitType, loc, cxt);
  }

  public static IType arrayType(IType elType) {
    return typeExp(Array.label, elType);
  }

  public static IType consType(IType elType) {
    return typeExp(Cons.typeLabel, elType);
  }

  public static IType tupleType(IType... elTypes) {
    return new TupleType(elTypes);
  }

  public static IType tupleType(List<IType> types) {
    return new TupleType(types.toArray(new IType[types.size()]));
  }

  public static boolean isTupleType(IType type) {
    type = deRef(type);

    return type instanceof TupleType;
  }

  public static boolean isTupleLabel(String label) {
    if (label.startsWith(StandardNames.TUPLE_LABEL)) {
      for (StringSequence it = new StringSequence(label, StandardNames.TUPLE_LABEL.length()); it.hasNext(); )
        if (!Character.isDigit(it.next()))
          return false;
      return true;
    }
    return false;
  }

  public static int tupleTypeArity(IType type) {
    assert isTupleType(type);
    return ((TupleType) type).arity();
  }

  public static String tupleLabel(int arity) {
    switch (arity) {
      case 0:
        return StandardNames.TUPLE_LABEL + "0";
      case 1:
        return StandardNames.TUPLE_LABEL + "1";
      case 2:
        return StandardNames.TUPLE_LABEL + "2";
      default:
        return StandardNames.TUPLE_LABEL + arity;
    }
  }

  public static IType nthTplType(IType type, int ix) {
    assert isTupleType(type);

    return ((TupleType) type).nth(ix);
  }

  public static IType[] tupleTypes(IType type) {
    type = deRef(type);

    assert isTupleType(type);

    return ((TupleType) type).getElTypes();
  }

  public static boolean isStdType(String type) {
    if (isTupleLabel(type))
      return true;
    else if (type.equals(StandardNames.FUN_ARROW) || type.equals(StandardNames.PTN_TYPE) || type.equals(
        StandardNames.CONSTRUCTOR_TYPE))
      return true;
    else
      return Intrinsics.isIntrinsicType(type);
  }

  public static boolean isAnonRecordLabel(String label) {
    if (label.startsWith(StandardNames.RECORD_LABEL)) {
      for (StringSequence it = new StringSequence(label, StandardNames.RECORD_LABEL.length()); it.hasNext(); ) {
        int ch = it.next();
        if (!Character.isDigit(ch) && ch != '_')
          return false;
      }

      return true;
    }
    return false;
  }

  public static IType getAttributeType(Dictionary cxt, IType type, String att, boolean testOnly)
      throws TypeConstraintException {
    type = deRef(type);

    if (type instanceof TypeInterfaceType)
      return ((TypeInterface) type).getFieldType(att);
    else if (type instanceof TypeVar) {
      TypeVar tVar = (TypeVar) type;
      for (ITypeConstraint con : tVar) {
        if (con instanceof FieldConstraint) {
          FieldConstraint fieldCon = (FieldConstraint) con;
          if (fieldCon.getField().equals(att))
            return fieldCon.getType();
        }
      }
      if (!testOnly && !tVar.isReadOnly()) {
        TypeVar attType = new TypeVar();
        try {
          TypeUtils.addFieldConstraint(tVar, Location.nullLoc, att, attType, cxt, false);
        } catch (TypeConstraintException e) {
          e.printStackTrace();
        }
        return attType;
      }
    } else {
      ITypeDescription spec = cxt.getTypeDescription(type.typeLabel());
      if (spec instanceof IAlgebraicType) {
        IAlgebraicType tCon = (IAlgebraicType) spec;

        for (IValueSpecifier valueSpec : tCon.getValueSpecifiers()) {
          if (valueSpec instanceof RecordSpecifier) {
            RecordSpecifier aggCon = (RecordSpecifier) valueSpec;
            if (aggCon.hasMember(att)) {
              IType conType = Freshen.freshenForUse(aggCon.getConType());

              Subsume.same(TypeUtils.getConstructorResultType(conType), type, Location.nullLoc, cxt);

              IType conArgType = TypeUtils.getConstructorArgType(conType);

              if (conArgType instanceof ExistentialType) {
                Pair<IType, Map<String, Quantifier>> ref = Freshen.freshen(conArgType, AccessMode.readOnly,
                    AccessMode.readWrite);
                conArgType = ref.left;
                if (conArgType instanceof TypeInterface) {
                  return Freshen.requant(((TypeInterface) conArgType).getFieldType(att), ref.right);
                }
              }
              return ((TypeInterface) conArgType).getFieldType(att);
            }
          }
        }
        return null;
      }
    }
    return null;
  }

  public static boolean hasAttributeType(Dictionary cxt, IType type, String att) {
    type = deRef(type);

    if (type instanceof TypeInterfaceType)
      return ((TypeInterface) type).getFieldType(att) != null;
    else if (type instanceof TypeVar) {
      TypeVar tVar = (TypeVar) type;
      for (ITypeConstraint con : tVar) {
        if (con instanceof FieldConstraint) {
          FieldConstraint fieldCon = (FieldConstraint) con;
          if (fieldCon.getField().equals(att))
            return true;
        }
      }
      return false;
    } else {
      ITypeDescription spec = cxt.getTypeDescription(type.typeLabel());
      if (spec instanceof IAlgebraicType) {
        IAlgebraicType tCon = (IAlgebraicType) spec;

        for (IValueSpecifier valueSpec : tCon.getValueSpecifiers()) {
          if (valueSpec instanceof RecordSpecifier) {
            RecordSpecifier aggCon = (RecordSpecifier) valueSpec;
            if (aggCon.hasMember(att)) {
              IType conType = Freshen.freshenForUse(aggCon.getConType());

              try {
                Subsume.same(TypeUtils.getConstructorResultType(conType), type, Location.nullLoc, cxt);
              } catch (TypeConstraintException e) {
                return false;
              }

              IType conArgType = TypeUtils.unwrap(TypeUtils.getConstructorArgType(conType));

              if (conArgType instanceof TypeInterface)
                return ((TypeInterface) conArgType).getFieldType(att) != null;
            }
          }
        }
      }
      return false;
    }
  }

  public static IType getFieldTypeMember(Dictionary cxt, IType type, String att, boolean testOnly) {
    type = deRef(type);

    if (type instanceof TypeInterfaceType)
      return ((TypeInterfaceType) type).getType(att);
    else if (type instanceof TypeVar) {
      TypeVar tVar = (TypeVar) type;
      for (ITypeConstraint con : tVar) {
        if (con instanceof FieldTypeConstraint) {
          FieldTypeConstraint fieldCon = (FieldTypeConstraint) con;
          if (fieldCon.getName().equals(att))
            return fieldCon.getType();
        }
      }
      if (!testOnly && !tVar.isReadOnly()) {
        TypeVar attType = new TypeVar();
        try {
          TypeUtils.addTypeConstraint(tVar, Location.nullLoc, att, attType, cxt, false);
        } catch (TypeConstraintException e) {
          e.printStackTrace();
        }
        return attType;
      }
    } else {
      ITypeDescription spec = cxt.getTypeDescription(type.typeLabel());
      if (spec instanceof IAlgebraicType) {
        IAlgebraicType tCon = (IAlgebraicType) spec;

        for (IValueSpecifier valueSpec : tCon.getValueSpecifiers()) {
          if (valueSpec instanceof RecordSpecifier) {
            RecordSpecifier aggCon = (RecordSpecifier) valueSpec;
            if (aggCon.hasMember(att)) {
              IType aggType = Freshen.freshenForUse(aggCon.getConType());

              try {
                Subsume.same(TypeUtils.getConstructorResultType(aggType), type, Location.nullLoc, cxt);
              } catch (TypeConstraintException e) {
                return null;
              }

              IType conArgType = TypeUtils.getConstructorArgType(aggType);

              if (conArgType instanceof ExistentialType) {
                Pair<IType, Map<String, Quantifier>> ref = Freshen.freshen(conArgType, AccessMode.readOnly,
                    AccessMode.readWrite);
                conArgType = ref.left;
                if (conArgType instanceof TypeInterface) {
                  return Freshen.requant(((TypeInterface) conArgType).getType(att), ref.right);
                }
              }
              return ((TypeInterface) conArgType).getType(att);
            }
          }
        }
        return null;
      }
    }
    return null;
  }

  public static boolean hasContractDependencies(IType type) {
    type = deRef(type);

    if (!isOverloadedType(unwrap(type))) {
      while (type instanceof UniversalType) {
        UniversalType univ = (UniversalType) type;
        for (ITypeConstraint con : univ.getBoundVar())
          if (con instanceof ContractConstraint)
            return true;
        type = univ.getBoundType();
      }
    }
    return false;
  }

  public static boolean isUnresolved(IType type) {
    final Wrapper<Boolean> isResolved = Wrapper.create(true);

    ITypeVisitor<Void> visitor = new AbstractTypeVisitor<Void>() {
      @Override
      public void visitTypeVar(TypeVar var, Void cxt) {
        IType type = var.deRef();

        if (type instanceof TypeVar) {
          var = (TypeVar) type;
          String varName = var.getVarName();

          if (isNotExcluded(varName)) {
            if (isResolved.get()) {
              for (ITypeConstraint con : var)
                if (con instanceof ContractConstraint) {
                  isResolved.set(false);
                  return;
                }
            }
          }
        } else
          type.accept(this, cxt);
      }
    };
    type.accept(visitor, null);
    return !isResolved.get();
  }

  public static boolean implementsContract(String type, String contract, Dictionary dict) {
    String instName = Over.instanceFunName(contract, type);
    return dict.isDefinedVar(instName);
  }

  public static boolean isPatternType(IType type) {
    type = unwrap(type);
    return isType(type, StandardNames.PTN_TYPE) && isTupleType(((TypeExp) type).getTypeArg(0));
  }

  public static IType patternType(IType resultType, IType ptnType) {
    assert isTupleType(resultType);
    return typeExp(StandardNames.PTN_TYPE, resultType, ptnType);
  }

  public static IType getPtnResultType(IType type) {
    type = unwrap(type);

    assert isPatternType(type);
    return ((TypeExp) type).getTypeArg(0);
  }

  public static IType getPtnMatchType(IType type) {
    type = unwrap(type);

    assert isPatternType(type);

    TypeExp funType = (TypeExp) type;
    return funType.getTypeArg(1);
  }

  public static TypeInterfaceType typeInterface(SortedMap<String, IType> members) {
    return new TypeInterfaceType(members);
  }

  public static SortedMap<String, Integer> getMemberIndex(IType type) {
    type = unwrap(type);
    assert type instanceof TypeInterfaceType;
    TypeInterfaceType face = (TypeInterfaceType) type;
    SortedMap<String, Integer> memberIndex = new TreeMap<>();
    Map<String, IType> members = face.getAllFields();

    for (Entry<String, IType> entry : members.entrySet()) {
      int ix = memberIndex.size();
      memberIndex.put(entry.getKey(), ix);
    }
    return memberIndex;
  }

  public static void defineTypeContract(Dictionary dict, TypeContract contract) {
    String name = contract.getName();
    dict.defineTypeContract(contract);

    IAlgebraicType conDesc = contract.getContractType();
    Location loc = contract.getLoc();

    Map<String, TypeVar> bound = new HashMap<>();

    RecordSpecifier spec = (RecordSpecifier) conDesc.getValueSpecifier(name);

    assert spec != null;

    IType specType = TypeUtils.unwrap(spec.getConType(), bound);
    assert TypeUtils.isConstructorType(specType);

    IType specConType = TypeUtils.getConstructorResultType(specType);
    TypeInterface face = (TypeInterface) TypeUtils.getConstructorArgType(specType);

    for (Entry<String, Integer> ix : spec.getIndex().entrySet()) {
      String mtd = ix.getKey();
      IType fieldType = face.getFieldType(ix.getKey());
      IType conType = TypeUtils.rewrap(TypeUtils.overloadedType(TypeUtils.tupleType(specConType), fieldType), bound);

      MethodVariable mVar = MethodVariable.create(loc, mtd, TypeUtils.rewrap(fieldType, bound), name, conType);
      dict.declareVar(mVar.getName(), mVar, AccessMode.readOnly, Visibility.pUblic, true);
    }
    dict.defineType(conDesc);
  }

  public static IType overloadedType(List<IType> contractRefs, IType type) {
    return typeExp(StandardNames.OVERLOADED_TYPE, tupleType(contractRefs), type);
  }

  public static IType overloadedType(IType contract, IType deliver) {
    return typeExp(StandardNames.OVERLOADED_TYPE, contract, deliver);
  }

  public static boolean isOverloadedType(IType type) {
    type = unwrap(type);

    return isType(type, StandardNames.OVERLOADED_TYPE, 2);
  }

  public static IType getOverloadArgType(IType type) {
    assert isOverloadedType(type);

    type = unwrap(type);

    return getTypeArg(type, 0);
  }

  public static IType[] getOverloadRequirements(IType type) {
    assert isOverloadedType(type);

    type = unwrap(type);

    return typeArgs(getTypeArg(type, 0));
  }

  public static IType getOverloadedType(IType type) {
    assert isOverloadedType(type);

    type = unwrap(type);

    return ((TypeExp) type).getTypeArg(1);
  }

  public static IType getOverloadedContract(IType type) {
    assert isOverloadedType(type) && isTupleType(getTypeArg(type, 0));
    type = unwrap(type);
    return getTypeArg(type, 0);
  }

  public static IType getContract(IType type) {
    type = unwrap(type);

    assert isOverloadedType(type) && isTupleType(getTypeArg(type, 0)) && tupleTypeArity(getTypeArg(type, 0)) == 1;

    return nthTplType(getTypeArg(type, 0), 0);
  }

  public static IType refreshOverloaded(IType overload) {
    return Freshen.freshenForUse(overloadedRefresh(overload).left);
  }

  public static Pair<IType, Map<String, Quantifier>> overloadedRefresh(IType overload) {
    Pair<IType, Map<String, Quantifier>> freshen = Freshen.freshen(overload, AccessMode.readOnly, AccessMode.readWrite);
    IType[] requirements = getOverloadRequirements(freshen.left);
    for (IType requirement : requirements) {
      assert requirement instanceof TypeExp;
      TypeExp contract = (TypeExp) requirement;
      ContractConstraint constraint = new ContractConstraint(contract);

      IType argTypes[] = contract.getTypeArgs();
      for (IType aType : argTypes) {
        aType = deRef(aType);
        if (isTypeVar(aType))
          ((TypeVar) aType).setConstraint(constraint);
      }
    }
    return freshen;
  }

  public static IType determinedType(IType... types) {
    return typeExp(StandardNames.DETERMINES, types);
  }

  public static TypeExp determinedContractType(String contract, IType type, IType... detType) {
    return new TypeExp(contract, type, typeExp(StandardNames.DETERMINES, detType));
  }

  public static IType referenceType(IType varType) {
    return typeExp(StandardNames.REF, varType);
  }

  public static boolean isReferenceType(IType type) {
    type = deRef(type);

    return isType(type, StandardNames.REF, 1);
  }

  public static IType referencedType(IType type) {
    assert isReferenceType(type);

    type = deRef(type);

    return ((TypeExp) type).getTypeArg(0);
  }

  public static boolean isRawBoolType(IType type) {
    return isType(type, Names.RAW_BOOL_TYPE);
  }

  public static boolean isRawCharType(IType type) {
    return isType(type, Names.RAW_CHAR_TYPE);
  }

  public static boolean isRawIntType(IType type) {
    return isType(type, Names.RAW_INT_TYPE);
  }

  public static boolean isRawLongType(IType type) {
    return isType(type, Names.RAW_LONG_TYPE);
  }

  public static boolean isRawFloatType(IType type) {
    return isType(type, Names.RAW_FLOAT_TYPE);
  }

  public static boolean isRawDecimalType(IType type) {
    return isType(type, Names.RAW_DECIMAL_TYPE);
  }

  public static boolean isRawBinaryType(IType type) {
    return isType(type, Names.RAW_BINARY_TYPE);
  }

  public static boolean isRawFileType(IType type) {
    return isType(type, Names.RAW_FILE_TYPE);
  }

  public static boolean isRawStringType(IType type) {
    return isType(type, Names.RAW_STRING_TYPE);
  }

  public static boolean isRawBoolType(String name) {
    return name.equals(Names.RAW_BOOL_TYPE);
  }

  public static boolean isRawCharType(String name) {
    return name.equals(Names.RAW_CHAR_TYPE);
  }

  public static boolean isRawIntType(String name) {
    return name.equals(Names.RAW_INT_TYPE);
  }

  public static boolean isRawLongType(String name) {
    return name.equals(Names.RAW_LONG_TYPE);
  }

  public static boolean isRawFloatType(String name) {
    return name.equals(Names.RAW_FLOAT_TYPE);
  }

  public static boolean isRawDecimalType(String name) {
    return name.equals(Names.RAW_DECIMAL_TYPE);
  }

  public static boolean isRawBinaryType(String name) {
    return name.equals(Names.RAW_BINARY_TYPE);
  }

  public static boolean isRawFileType(String name) {
    return name.equals(Names.RAW_FILE_TYPE);
  }

  public static boolean isRawStringType(String name) {
    return name.equals(Names.RAW_STRING_TYPE);
  }

  public static boolean isRawType(IType type) {
    type = deRef(type);

    return type instanceof Type && (isRawBoolType(type) || isRawCharType(type) || isRawIntType(type) || isRawLongType(
        type) || isRawFloatType(type) || isRawDecimalType(type) || isRawStringType(type) || isRawFileType(type)
        || isRawBinaryType(type));
  }

  public static IType cookedType(IType type) {
    switch (Types.varType(type)) {
      case rawBool:
        return StandardTypes.booleanType;
      case rawChar:
        return StandardTypes.charType;
      case rawInt:
        return StandardTypes.integerType;
      case rawLong:
        return StandardTypes.longType;
      case rawFloat:
        return StandardTypes.floatType;
      case rawDecimal:
        return StandardTypes.decimalType;
      case rawString:
        return StandardTypes.stringType;
      case rawBinary:
        return StandardTypes.binaryType;
      default:
        return type;
    }
  }

  public static IType rawType(IType type) {
    type = deRef(type);
    if (type.equals(StandardTypes.booleanType))
      return StandardTypes.rawBoolType;
    else if (type.equals(StandardTypes.charType))
      return StandardTypes.rawCharType;
    else if (type.equals(StandardTypes.integerType))
      return StandardTypes.rawIntegerType;
    else if (type.equals(StandardTypes.longType))
      return StandardTypes.rawLongType;
    else if (type.equals(StandardTypes.floatType))
      return StandardTypes.rawFloatType;
    else if (type.equals(StandardTypes.decimalType))
      return StandardTypes.rawDecimalType;
    else if (type.equals(StandardTypes.stringType))
      return StandardTypes.rawStringType;
    else if (type.equals(StandardTypes.binaryType))
      return StandardTypes.rawBinaryType;
    else
      return null;
  }

  public static boolean isCookedType(IType type, IType rawType) {
    type = deRef(type);
    rawType = deRef(rawType);
    if (type.equals(StandardTypes.booleanType))
      return StandardTypes.rawBoolType.equals(rawType);
    else if (type.equals(StandardTypes.charType))
      return StandardTypes.rawCharType.equals(rawType);
    else if (type.equals(StandardTypes.integerType))
      return StandardTypes.rawIntegerType.equals(rawType);
    else if (type.equals(StandardTypes.longType))
      return StandardTypes.rawLongType.equals(rawType);
    else if (type.equals(StandardTypes.floatType))
      return StandardTypes.rawFloatType.equals(rawType);
    else if (type.equals(StandardTypes.decimalType))
      return StandardTypes.rawDecimalType.equals(rawType);
    else if (type.equals(StandardTypes.stringType))
      return StandardTypes.rawStringType.equals(rawType);
    else if (type.equals(StandardTypes.binaryType))
      return StandardTypes.rawBinaryType.equals(rawType);
    else
      return false;
  }

  public static boolean isGroundSurface(IType tp) {
    if (tp instanceof TypeExp) {
      TypeExp tExp = (TypeExp) tp;
      if (isTypeVar(tExp.getTypeCon()))
        return false;
      for (IType aT : tExp.getTypeArgs()) {
        if (isTypeVar(aT))
          return false;
      }
      return true;
    } else
      return tp instanceof Type;
  }

  public static boolean isVarSurface(IType tp) {
    tp = deRef(tp);
    if (tp instanceof TypeExp) {
      TypeExp tExp = (TypeExp) tp;
      for (IType aT : tExp.getTypeArgs()) {
        if (!isTypeVar(deRef(aT)))
          return false;
      }
      return true;
    }
    return false;
  }

  public static IType checkAlias(IType type, Dictionary face, Location loc) throws TypeConstraintException {
    type = deRef(type);
    if (type instanceof TypeExp) {
      TypeExp tExp = (TypeExp) type;
      ITypeDescription spec = face.getTypeDescription(tExp.typeLabel());
      if (spec instanceof ITypeAlias)
        return ((ITypeAlias) spec).apply(type, loc, face);
    } else if (type instanceof Type) {
      Type t = (Type) type;
      ITypeDescription spec = face.getTypeDescription(t.typeLabel());
      if (spec instanceof ITypeAlias)
        return ((ITypeAlias) spec).apply(type, loc, face);
    }
    return type;
  }

  public static IType iterstateType(IType stType) {
    return typeExp(StandardNames.ITERSTATE, stType);
  }

  public static boolean isIterstateType(IType type) {
    return isType(type, StandardNames.ITERSTATE, 1);
  }

  public static void addFieldConstraint(TypeVar var, Location loc, String att, IType type, Dictionary cxt,
                                        boolean allow) throws TypeConstraintException {
    var.addConstraint(new FieldConstraint(var, att, type), allow, loc, cxt);
  }

  public static void setFieldConstraint(TypeVar var, Location loc, String att, IType type) {
    var.setConstraint(new FieldConstraint(var, att, type));
  }

  public static void addTypeConstraint(TypeVar var, Location loc, String att, IType type, Dictionary cxt, boolean allow)
      throws TypeConstraintException {
    var.addConstraint(new FieldTypeConstraint(var, att, type), allow, loc, cxt);
  }

  public static void setTypeConstraint(TypeVar var, String att, IType type) {
    var.setConstraint(new FieldTypeConstraint(var, att, type));
  }

  public static class CompareTypes implements Comparator<IType> {

    @Override
    public int compare(IType o1, IType o2) {
      o1 = TypeUtils.deRef(o1);
      o2 = TypeUtils.deRef(o2);

      if (o1 instanceof Type) {
        if (o2 instanceof Type)
          return o1.typeLabel().compareTo(o2.typeLabel());
        else
          return -1;
      } else if (o1 instanceof TypeExp) {
        if (o2 instanceof TypeExp) {
          TypeExp t1 = (TypeExp) o1;
          TypeExp t2 = (TypeExp) o2;
          int comp = t1.typeArity() == t2.typeArity() ? compare(t1.getTypeCon(), t2.getTypeCon())
              : t1.typeArity() - t2.typeArity();

          if (comp == 0) {
            for (int ix = 0; ix < t1.typeArity() && comp == 0; ix++)
              comp = compare(t1.getTypeArg(ix), t2.getTypeArg(ix));
          }
          return comp;
        } else
          return -1;
      } else if (o1 instanceof TypeInterfaceType) {
        if (o2 instanceof TypeInterfaceType) {
          TypeInterfaceType f1 = (TypeInterfaceType) o1;
          TypeInterfaceType f2 = (TypeInterfaceType) o2;
          if (f1.numOfTypes() < f2.numOfTypes())
            return -1;
          else if (f1.numOfTypes() > f2.numOfTypes())
            return 1;
          else if (f1.numOfFields() < f2.numOfFields())
            return -1;
          else if (f1.numOfFields() > f2.numOfFields())
            return 1;
          else {
            for (Entry<String, IType> e1 : f1.getAllTypes().entrySet()) {
              IType et2 = f2.getType(e1.getKey());
              if (et2 == null)
                return 1;
              else {
                int comp = compare(e1.getValue(), et2);
                if (comp != 0)
                  return comp;
              }
            }
            return 0;
          }
        } else
          return -1;
      } else if (o1 instanceof TypeVar) {
        if (o2 instanceof TypeVar)
          return o1.typeLabel().compareTo(o2.typeLabel());
        else
          return -1;
      } else
        return o1.typeLabel().compareTo(o2.typeLabel());
    }
  }
}
