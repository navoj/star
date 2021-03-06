package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.type.CafeConstructor;
import org.star_lang.star.compiler.cafe.type.CafeRecordSpecifier;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Refresher;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/*
 *
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

public class TypeAnalyser
{

  private TypeAnalyser()
  {
  }

  public static IType parseType(IAbstract tp, ITypeContext cxt, ErrorReport errors)
  {
    final LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    return parseType(tp, typeVars, cxt, errors);
  }

  private static IType parseType(IAbstract tp, LayeredMap<String, TypeVar> typeVars, ITypeContext cxt,
      ErrorReport errors)
  {
    final Location loc = tp.getLoc();
    if (tp instanceof Name) {
      String tpName = ((Name) tp).getId();
      if (!TypeUtils.isTupleLabel(tpName)) {
        final ITypeDescription typeDesc = cxt.getTypeDescription(tpName);

        if (typeDesc != null) {
          int definedArity = typeDesc.typeArity();
          if (definedArity != 0)
            return TypeUtils.typeCon(tpName, definedArity);
        }
      }
      return TypeUtils.typeExp(tpName);
    } else if (CafeSyntax.isTypeVar(tp)) {
      String varName = CafeSyntax.typeVarName(tp);
      if (typeVars.containsKey(varName))
        return typeVars.get(varName);
      else {
        TypeVar var = new TypeVar(varName, varName, AccessMode.readOnly);
        typeVars.put(varName, var);
        return var;
      }
    } else if (CafeSyntax.isTypeFunVar(tp)) {
      String varName = CafeSyntax.typeFunVarName(tp);
      if (typeVars.containsKey(varName))
        return typeVars.get(varName);
      else {
        TypeVar var = TypeVar.var(varName, CafeSyntax.typeFunArity(tp), AccessMode.readOnly);
        typeVars.put(varName, var);
        return var;
      }
    } else if (CompilerUtils.isRef(tp))
      return TypeUtils.referenceType(parseType(CompilerUtils.referencedTerm(tp), typeVars, cxt, errors));
    else if (Abstract.isBinary(tp, Names.REQUIRING)) {
      IType tVar = parseType(Abstract.binaryLhs(tp), typeVars, cxt, errors);

      if (tVar instanceof TypeVar) {
        TypeVar tV = (TypeVar) tVar;
        IAbstract constraints = Abstract.binaryRhs(tp);
        if (CompilerUtils.isBlockTerm(constraints)) {
          for (IAbstract c : CompilerUtils.unWrap(CompilerUtils.blockContent(constraints))) {
            if (c instanceof AApply) {
              AApply con = (AApply) c;
              String conName = con.getOp();
              TypeContract contract = cxt.getContract(conName);
              if (contract == null)
                errors.reportWarning("unknown type contract: " + conName, con.getLoc());
              else if (contract.getArity() != con.getArgs().size())
                errors.reportError("expecting " + contract.getArity() + " arguments to type constract", con.getLoc());

              List<IType> argTypes = new ArrayList<>();
              for (IValue arg : ((AApply) tp).getArgs())
                argTypes.add(parseType((IAbstract) arg, typeVars, cxt, errors));
              try {
                tV.addContractRequirement((TypeExp) TypeUtils.typeExp(conName, argTypes), loc, null);
              } catch (TypeConstraintException e) {
                errors.reportError(e.getMessage(), loc);
              }
            } else if (c instanceof Name) {
              String conName = ((Name) c).getId();
              TypeContract contract = cxt.getContract(conName);
              if (contract == null)
                errors.reportWarning("unknown type contract: " + conName, c.getLoc());
              else if (contract.getArity() != 0)
                errors.reportError("expecting " + contract.getArity() + " arguments to type constract", c.getLoc());

              try {
                tV.addContractRequirement((TypeExp) TypeUtils.typeExp(conName), loc, null);
              } catch (TypeConstraintException e) {
                errors.reportError(e.getMessage(), loc);
              }
            } else
              errors.reportError("invalid type constraint: " + c, c.getLoc());
          }
        }
      } else
        errors.reportError("expecting a type variable", Abstract.getArg(tp, 0).getLoc());
      return tVar;
    } else if (Abstract.isApply(tp, StandardNames.DETERMINES)) {
      List<IType> argTypes = new ArrayList<>();
      for (IValue arg : Abstract.getArgs(tp))
        argTypes.add(parseType((IAbstract) arg, typeVars, cxt, errors));
      return TypeUtils.typeExp(StandardNames.DETERMINES, argTypes);
    } else if (CafeSyntax.isUniversalType(tp)) {
      LayeredMap<String, TypeVar> subVars = typeVars.fork();
      List<TypeVar> qVars = new ArrayList<>();

      while (CafeSyntax.isUniversalType(tp)) {
        IType tV = parseType(CafeSyntax.universalBoundVar(tp), typeVars, cxt, errors);
        if (!(tV instanceof TypeVar))
          errors.reportError("expecting a type variable: " + tV, Abstract.getArg(tp, 0).getLoc());
        else
          qVars.add((TypeVar) tV);
        tp = CafeSyntax.universalBoundType(tp);
      }

      IType boundType = parseType(tp, subVars, cxt, errors);
      return UniversalType.universal(qVars, boundType);
    } else if (CafeSyntax.isExistentialType(tp)) {
      LayeredMap<String, TypeVar> subVars = typeVars.fork();
      List<TypeVar> qVars = new ArrayList<>();

      while (CafeSyntax.isExistentialType(tp)) {
        IType tV = parseType(CafeSyntax.existentialTypeVar(tp), typeVars, cxt, errors);
        if (!(tV instanceof TypeVar))
          errors.reportError("expecting a type variable: " + tV, Abstract.getArg(tp, 0).getLoc());
        else
          qVars.add((TypeVar) tV);
        tp = CafeSyntax.existentialBoundType(tp);
      }

      IType boundType = parseType(tp, subVars, cxt, errors);
      return ExistentialType.exist(qVars, boundType);
    } else if (Abstract.isBinary(tp, Names.ARROW)) {
      IType argTypes = parseType(Abstract.binaryLhs(tp), typeVars, cxt, errors);
      IType resType = parseType(Abstract.binaryRhs(tp), typeVars, cxt, errors);
      return TypeUtils.funcType(argTypes, resType);
    } else if (Abstract.isBinary(tp, Names.LARROW)) {
      IType argTypes = parseType(Abstract.binaryRhs(tp), typeVars, cxt, errors);
      IType resType = parseType(Abstract.binaryLhs(tp), typeVars, cxt, errors);
      return TypeUtils.patternType(resType, argTypes);
    } else if (Abstract.isBinary(tp, Names.BIARROW)) {
      IType argTypes = parseType(Abstract.binaryLhs(tp), typeVars, cxt, errors);
      IType resType = parseType(Abstract.binaryRhs(tp), typeVars, cxt, errors);
      return TypeUtils.constructorType(argTypes, resType);
    } else if (Abstract.isTupleTerm(tp)) {
      AApply tExp = (AApply) tp;

      List<IType> argTypes = new ArrayList<>();
      for (IValue arg : tExp.getArgs())
        argTypes.add(parseType((IAbstract) arg, typeVars, cxt, errors));

      return TypeUtils.tupleType(argTypes);
    } else if (tp instanceof AApply) {
      AApply tExp = (AApply) tp;

      IType typeCon = parseType(tExp.getOperator(), typeVars, cxt, errors);

      List<IType> argTypes = new ArrayList<>();
      for (IValue arg : tExp.getArgs())
        argTypes.add(parseType((IAbstract) arg, typeVars, cxt, errors));

      int arity = argTypes.size();

      if (typeCon instanceof Type) {
        String tpName = typeCon.typeLabel();
        if (TypeUtils.isTupleLabel(tpName))
          return TypeUtils.tupleType(argTypes);
        else {
          final ITypeDescription typeDesc = cxt.getTypeDescription(tpName);
          if (typeDesc != null) {
            if (arity == 0)
              return typeDesc.getType();
            else if (typeDesc.typeArity() != arity)
              errors.reportError(tpName + " should have " + typeDesc.typeArity() + " type arguments", loc);

            return TypeUtils.typeExp(typeCon, argTypes);
          } else
            return TypeUtils.typeExp(tpName, argTypes);
        }
      } else
        return TypeUtils.typeExp(typeCon, argTypes);
    } else {
      errors.reportError("cannot understand type expression: " + tp, loc);
      return new TypeVar();
    }
  }

  // An algebraic type looks like:
  // type <type> is <cases>
  public static CafeTypeDescription parseAlgebraicDefn(IAbstract tp, ITypeContext cxt, ErrorReport errors)
  {
    assert CafeSyntax.isTypeDef(tp);

    LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    List<IValueSpecifier> specs = new ArrayList<>();
    IType type = parseDefinedType(CafeSyntax.typeDefType(tp), typeVars, cxt, errors);

    for (IValue spec : CafeSyntax.typeDefSpecs(tp))
      parseValueSpecifier((IAbstract) spec, type, cxt, errors, specs);
    ITypeDescription oldDesc = cxt.getTypeDescription(type.typeLabel());
    String javaName = oldDesc instanceof CafeTypeDescription ? ((CafeTypeDescription) oldDesc).getJavaName() : "";

    return new CafeTypeDescription(tp.getLoc(), type, javaName, specs);
  }

  public static IType parseDefinedType(IAbstract tp, LayeredMap<String, TypeVar> typeVars, ITypeContext cxt,
      ErrorReport errors)
  {
    final Location loc = tp.getLoc();
    if (tp instanceof Name) {
      String tpName = ((Name) tp).getId();

      return TypeUtils.typeExp(tpName);
    } else if (CafeSyntax.isTypeVar(tp)) {
      errors.reportError("Type NAME expected, got type variable: " + tp, loc);
      return new TypeVar();
    } else if (CafeSyntax.isTypeExp(tp)) {
      String tpName = CafeSyntax.typeExpLabel(tp);

      List<IType> argTypes = new ArrayList<>();
      for (IValue arg : CafeSyntax.typeExpArgs(tp))
        argTypes.add(parseType((IAbstract) arg, typeVars, cxt, errors));

      return TypeUtils.typeExp(tpName, argTypes);
    } else {
      errors.reportError("invalid type expression: " + tp + " in type definition", loc);
      return new TypeVar();
    }
  }

  public static void defineType(IAbstract tp, CafeDictionary dict, ErrorReport errors)
  {
    LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    IType type = parseDefinedType(tp, typeVars, dict, errors);
    String javaSrTypeName = Types.javaTypeName(dict.getPath(), type.typeLabel());
    dict.declareType(tp.getLoc(), Refresher.generalize(type, typeVars), javaSrTypeName);
  }

  private static void parseValueSpecifier(IAbstract spec, IType type, ITypeContext cxt, ErrorReport errors,
      List<IValueSpecifier> specs)
  {
    Location loc = spec.getLoc();
    LayeredMap<String, TypeVar> existentials = new LayeredHash<>();

    while (CafeSyntax.isExistentialType(spec)) {
      String eVar = Abstract.getId(CafeSyntax.existentialTypeVar(spec));
      existentials.put(eVar, new TypeVar(eVar, AccessMode.readOnly));
      spec = CafeSyntax.existentialBoundType(spec);
    }

    if (CafeSyntax.isConstructorSpec(spec)) {
      String name = CafeSyntax.constructorSpecLabel(spec);
      List<IType> argTypes = new ArrayList<>();
      IList conArgs = CafeSyntax.constructorSpecArgs(spec);
      LayeredMap<String, TypeVar> typeVars = existentials.fork();
      for (int ix = 0; ix < conArgs.size(); ix++) {
        IAbstract arg = (IAbstract) conArgs.getCell(ix);
        if (CafeSyntax.isTypedTerm(arg)) {
          errors.reportError("not expecting a named field, just a type expression: " + arg, arg.getLoc());
          argTypes.add(parseType(CafeSyntax.typedType(arg), typeVars, cxt, errors));
        } else {
          argTypes.add(parseType(arg, typeVars, cxt, errors));
        }
      }
      IType conType = TypeUtils.constructorType(argTypes, type);
      specs.add(new ConstructorSpecifier(loc, name, specs.size(), conType, null));
    } else if (CafeSyntax.isRecord(spec)) {
      String name = CafeSyntax.recordLabel(spec);
      SortedMap<String, Integer> index = new TreeMap<>();
      SortedMap<String, IType> members = new TreeMap<>();
      LayeredMap<String, TypeVar> typeVars = existentials.fork();

      IList conArgs = CafeSyntax.recordArgs(spec);
      for (int ix = 0; ix < conArgs.size(); ix++) {
        IAbstract arg = (IAbstract) conArgs.getCell(ix);
        if (CafeSyntax.isTypedTerm(arg)) {
          IType argType = parseType(CafeSyntax.typedType(arg), typeVars, cxt, errors);
          IAbstract nme = CafeSyntax.typedTerm(arg);
          if (nme instanceof Name) {
            String field = ((Name) nme).getId();
            members.put(field, argType);
            index.put(field, ix);
          } else
            errors.reportError("expecting field NAME, not: " + nme, nme.getLoc());
        } else
          errors.reportError("expecting a named field, not: " + arg, arg.getLoc());
      }
      IType conType = TypeUtils.constructorType(ExistentialType.exist(existentials.values(), new TypeInterfaceType(
              new TreeMap<>(existentials), members)), type);
      conType = Freshen.generalizeType(conType);
      specs.add(new CafeRecordSpecifier(loc, name, specs.size(), index, conType));
    } else if (spec instanceof Name)
      specs.add(new CafeConstructor(loc, ((Name) spec).getId(), -1, TypeUtils.constructorType(type), null, null));
    else {
      errors.reportError("invalid form of constructor: " + spec, loc);
    }
  }
}
