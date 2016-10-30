package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.DefaultAbstractVisitor;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.InstanceOf;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TupleConstraint;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeAlias;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.data.type.TypeExists;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.type.Kind.Mode;
import org.star_lang.star.operators.assignment.runtime.RefCell.Cell;

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

public class TypeParser {
  public static IAlgebraicType parseTypeDefinition(IAbstract stmt, Map<String, Pair<IAbstract, IType>> defaults,
                                                   Map<String, IAbstract> integrity, Dictionary dict, Dictionary outer, ErrorReport errors, boolean suppress) {
    LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    Dictionary tmpCxt = dict.fork();
    Location loc = stmt.getLoc();

    TypeNameHandler varHandler = new RegularTypeName(tmpCxt, typeVars, AccessMode.readOnly, suppress, errors);
    TypeNameHandler defHandler = new DefiningTypeName(tmpCxt, typeVars, AccessMode.readOnly);

    IType thisType = typeHead(CompilerUtils.typeDefnType(stmt), tmpCxt, errors, defHandler);

    IAbstract constructors = CompilerUtils.typeDefnConstructors(stmt);

    if (Abstract.isBinary(constructors, StandardNames.WHERE)) {
      parseConstraints(Abstract.binaryRhs(constructors), dict, errors, varHandler);
      constructors = Abstract.binaryLhs(constructors);
    }

    ITypeDescription desc = dict.getTypeDescription(thisType.typeLabel());

    if (!(desc instanceof IAlgebraicType)) {
      desc = new TypeDescription(loc, Refresher.generalize(thisType, typeVars), new TypeInterfaceType());
      dict.defineType(desc);
    }

    IAlgebraicType algebraicType = (IAlgebraicType) desc;
    parseAlgebraicConstructors(constructors, thisType, algebraicType, tmpCxt, outer, errors, typeVars);
    parseDefaults(constructors, thisType, tmpCxt, algebraicType, defaults, integrity, errors);

    return algebraicType;
  }

  public static IType parseType(IAbstract tp, Dictionary cxt, ErrorReport errors, AccessMode access) {
    Map<String, TypeVar> typeVars = new HashMap<>();
    TypeNameHandler varHandler = new RegularTypeName(cxt, typeVars, access, false, errors);
    return parseType(tp, cxt, errors, varHandler);
  }

  public static IType parseType(IAbstract tp, Dictionary cxt, ErrorReport errors, AccessMode access, boolean suppress) {
    final LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    TypeNameHandler varHandler = new RegularTypeName(cxt, typeVars, access, suppress, errors);
    return parseType(tp, cxt, errors, varHandler);
  }

  public static IType parseType(IAbstract tp, LayeredMap<String, TypeVar> typeVars, Dictionary dict,
                                ErrorReport errors, AccessMode access) {
    TypeNameHandler varHandler = new RegularTypeName(dict, typeVars, access, false, errors);
    return parseType(tp, dict, errors, varHandler);
  }

  public static class RegularTypeName implements TypeNameHandler {
    private final Dictionary dict;
    private final Map<String, TypeVar> typeVars;
    private final AccessMode access;
    private final boolean suppress;
    private final ErrorReport errors;

    public RegularTypeName(Dictionary dict, Map<String, TypeVar> typeVars, AccessMode access, boolean supress,
                           ErrorReport errors) {
      this.dict = dict;
      this.typeVars = typeVars;
      this.access = access;
      this.suppress = supress;
      this.errors = errors;
    }

    @Override
    public IType typeByName(String name, Location loc) {
      if (typeVars.containsKey(name))
        return typeVars.get(name);
      else {
        ITypeDescription desc = dict.getTypeDescription(name);

        if (desc == null) {
          if (!TypeUtils.isStdType(name) && !suppress)
            errors.reportError("type " + name + " not declared", loc);
          return new Type(name, Kind.unknown);
        }

        IType type = new Type(name, desc.kind());

        try {
          type = typeAlias(dict, type, loc);
        } catch (TypeConstraintException e) {
          errors.reportError(e.getMessage(), Location.merge(loc, e.getLocs()));
          return type;
        }
        return type;
      }
    }

    @Override
    public IType newTypeVar(String name, Location loc, Kind kind) {
      if (typeVars.containsKey(name)) {
        TypeVar typeVar = typeVars.get(name);
        if (typeVar.kind().equals(Kind.unknown) && !kind.equals(Kind.unknown))
          typeVar.setConstraint(new HasKind(typeVar, kind));
        return typeVar;
      } else if (dict.typeExists(name)) {
        ITypeDescription desc = dict.getTypeDescription(name);
        return desc.getType();
      } else {
        TypeVar v = new TypeVar(name, access);
        if (kind.mode() == Mode.typefunction)
          v.setConstraint(new HasKind(v, kind));
        defineType(name, v);
        return v;
      }
    }

    @Override
    public void addEntries(Map<String, TypeVar> sub) {
      for (Entry<String, TypeVar> e : sub.entrySet())
        defineType(e.getKey(), e.getValue());
    }

    @Override
    public void defineType(String name, TypeVar v) {
      typeVars.put(name, v);
    }

    @Override
    public void removeTypeVar(String var) {
      typeVars.remove(var);
    }

    @Override
    public void removeEntries(Map<String, TypeVar> rem) {
      for (Entry<String, TypeVar> e : rem.entrySet())
        typeVars.remove(e.getKey());
    }

    @Override
    public AccessMode access() {
      return access;
    }

    @Override
    public Map<String, TypeVar> typeVars() {
      return typeVars;
    }

    @Override
    public boolean suppressWarnings() {
      return suppress;
    }

    @Override
    public TypeNameHandler fork() {
      return new RegularTypeName(dict, new HashMap<>(typeVars), access, suppress, errors);
    }
  }

  public static class DefiningTypeName implements TypeNameHandler {
    private final Dictionary dict;
    private final Map<String, TypeVar> typeVars;
    private final AccessMode access;

    public DefiningTypeName(Dictionary dict, Map<String, TypeVar> typeVars, AccessMode access) {
      this.dict = dict;
      this.typeVars = typeVars;
      this.access = access;
    }

    @Override
    public IType typeByName(String name, Location loc) {
      if (typeVars.containsKey(name))
        return typeVars.get(name);
      else {
        ITypeDescription desc = dict.getTypeDescription(name);

        if (desc == null)
          return newTypeVar(name, loc, Kind.unknown);

        return new Type(name, Kind.unknown);
      }
    }

    @Override
    public IType newTypeVar(String name, Location loc, Kind kind) {
      if (typeVars.containsKey(name))
        return typeVars.get(name);
      else if (name.equals(StandardNames.ANONYMOUS)) {
        TypeVar v = TypeVar.var(kind, access);
        defineType(name, v);
        return v;
      } else {
        TypeVar v = TypeVar.var(name, kind, access);
        defineType(name, v);
        return v;
      }
    }

    @Override
    public void addEntries(Map<String, TypeVar> sub) {
      for (Entry<String, TypeVar> e : sub.entrySet())
        defineType(e.getKey(), e.getValue());
    }

    @Override
    public void defineType(String name, TypeVar v) {
      typeVars.put(name, v);
    }

    @Override
    public void removeTypeVar(String var) {
      typeVars.remove(var);
    }

    @Override
    public void removeEntries(Map<String, TypeVar> rem) {
      for (Entry<String, TypeVar> e : rem.entrySet())
        typeVars.remove(e.getKey());
    }

    @Override
    public AccessMode access() {
      return access;
    }

    @Override
    public Map<String, TypeVar> typeVars() {
      return typeVars;
    }

    @Override
    public boolean suppressWarnings() {
      return true;
    }

    @Override
    public TypeNameHandler fork() {
      return new DefiningTypeName(dict, new HashMap<>(typeVars), access);
    }
  }

  public static IType parseType(IAbstract tp, Dictionary dict, ErrorReport errors, TypeNameHandler varHandler) {
    final Location loc = tp.getLoc();
    if (tp instanceof Name) {
      String tpName = ((Name) tp).getId();

      if (tpName.equals(StandardNames.BRACES))
        return new TypeInterfaceType();
      else
        return varHandler.typeByName(tpName, loc);
    } else if (CompilerUtils.isTypeVar(tp))
      return varHandler.newTypeVar(CompilerUtils.typeVarName(tp), loc, Kind.unknown);
    else if (CompilerUtils.isTypeFunVar(tp))
      return varHandler.newTypeVar(CompilerUtils.typeVarName(tp), loc, Kind.unknown);
    else if (CompilerUtils.isRef(tp)) {
      IAbstract ref = Abstract.deParen(CompilerUtils.referencedTerm(tp));

      IType refType = parseType(ref, dict, errors, varHandler);

      return TypeUtils.referenceType(refType);
    } else if (Abstract.isBinary(tp, StandardNames.OF)) {
      IAbstract con = Abstract.binaryLhs(tp);
      List<IType> argTypes = parseArgTypes(Abstract.binaryRhs(tp), dict, errors, varHandler);

      IType tyCon = parseType(con, dict, errors, varHandler);

      try {

        if (!tyCon.kind().check(argTypes.size())) {
          errors.reportError(StringUtils.msg(con, " expects ", tyCon.kind().arity(), " type arguments, got ", argTypes
              .size()), loc);
          return new TypeVar();
        }

        IType type = checkConstraints(TypeUtils.typeExp(tyCon, argTypes), dict, loc, errors);

        return typeAlias(dict, type, loc);
      } catch (TypeConstraintException e) {
        errors.reportError(e.getMessage(), Location.merge(loc, e.getLocs()));
        return new TypeVar();
      }
    } else if (Abstract.isBinary(tp, StandardNames.OF)) {
      IAbstract con = Abstract.binaryLhs(tp);
      List<IType> argTypes = parseArgTypes(Abstract.binaryRhs(tp), dict, errors, varHandler);

      IType tyCon = parseType(con, dict, errors, varHandler);

      try {

        if (!tyCon.kind().check(argTypes.size())) {
          errors.reportError(StringUtils.msg(con, " expects ", tyCon.kind().arity(), " type arguments, got ", argTypes
              .size()), loc);
          return new TypeVar();
        }

        IType type = checkConstraints(TypeUtils.typeExp(tyCon, argTypes), dict, loc, errors);

        return typeAlias(dict, type, loc);
      } catch (TypeConstraintException e) {
        errors.reportError(e.getMessage(), Location.merge(loc, e.getLocs()));
        return new TypeVar();
      }
    } else if (CompilerUtils.isUniversalType(tp)) {
      IAbstract tArg = CompilerUtils.universalTypeVars(tp);
      IAbstract bndArg = CompilerUtils.universalBoundType(tp);
      Map<String, TypeVar> bndTypes = parseQuantifiers(tArg, errors, dict, varHandler);

      IType boundType = parseType(bndArg, dict, errors, varHandler);
      varHandler.removeEntries(bndTypes);

      if (TypeUtils.isReferenceType(boundType)) {
        IType replace = TypeUtils.referenceType(UniversalType.universal(bndTypes.values(), TypeUtils
            .referencedType(boundType)));
        errors.reportError(StringUtils.msg("may not quantify ", boundType, "\nshould be replaced with ", replace), loc);
        return replace;
      }

      return UniversalType.universal(bndTypes.values(), boundType);
    } else if (CompilerUtils.isExistentialType(tp)) {
      IAbstract tArg = CompilerUtils.existentialTypeVars(tp);
      IAbstract bndArg = CompilerUtils.existentialBoundType(tp);
      Map<String, TypeVar> bndTypes = parseQuantifiers(tArg, errors, dict, varHandler);

      IType boundType = parseType(bndArg, dict, errors, varHandler);
      varHandler.removeEntries(bndTypes);

      if (TypeUtils.isReferenceType(boundType)) {
        IType replace = TypeUtils.referenceType(ExistentialType.exist(bndTypes.values(), TypeUtils
            .referencedType(boundType)));
        errors.reportError(StringUtils.msg("may not quantify ", boundType, "\nshould be replaced with ", replace), loc);
        return replace;
      }

      return ExistentialType.exist(bndTypes.values(), boundType);
    } else if (Abstract.isBinary(tp, StandardNames.FUN_ARROW)) {
      IType argsType = parseFunArgType(Abstract.binaryLhs(tp), dict, errors, varHandler);

      IType resltType = parseType(Abstract.binaryRhs(tp), dict, errors, varHandler);

      return TypeUtils.funcType(argsType, resltType);
    } else if (Abstract.isBinary(tp, StandardNames.OVERLOADED_TYPE)) {
      IType conArgType = parseFunArgType(Abstract.binaryLhs(tp), dict, errors, varHandler);
      IType resType = parseType(Abstract.binaryRhs(tp), dict, errors, varHandler);

      return TypeUtils.overloadedType(conArgType, resType);
    } else if (Abstract.isRoundTerm(tp, StandardNames.ACTION_TYPE)) {
      List<IType> argTypes = new ArrayList<>();
      for (IValue tpArg : Abstract.getArgs(tp))
        argTypes.add(parseType((IAbstract) tpArg, dict, errors, varHandler));
      return TypeUtils.procedureType(argTypes);
    } else if (Abstract.isBinary(tp, StandardNames.PTN_TYPE)) {
      IType argsType = parseFunArgType(Abstract.binaryLhs(tp), dict, errors, varHandler);
      IType ptnType = parseType(Abstract.binaryRhs(tp), dict, errors, varHandler);
      return TypeUtils.patternType(argsType, ptnType);
    } else if (Abstract.isBinary(tp, StandardNames.CONSTRUCTOR_TYPE)) {
      IType argsType = parseFunArgType(Abstract.binaryLhs(tp), dict, errors, varHandler);
      IType resltType = parseType(Abstract.binaryRhs(tp), dict, errors, varHandler);

      return TypeUtils.constructorType(argsType, resltType);
    } else if (CompilerUtils.isFieldAccess(tp) && Abstract.isIdentifier(CompilerUtils.fieldRecord(tp))
        && Abstract.isIdentifier(CompilerUtils.fieldField(tp))) {
      String record = Abstract.getId(CompilerUtils.fieldRecord(tp));
      String field = Abstract.getId(CompilerUtils.fieldField(tp));

      // We need to handle this carefully because we would like to avoid unnecessary skolemization

      DictInfo info = dict.getVar(record);

      if (info instanceof VarInfo)
        return ((VarInfo) info).localType(loc, field, dict, errors);
      else {
        errors.reportError("type " + field + " from " + record + " is not valid here", loc);
        return new TypeVar();
      }
    } else if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      IType type = parseType(Abstract.binaryLhs(tp), dict, errors, varHandler);
      parseConstraints(Abstract.binaryRhs(tp), dict, errors, varHandler);
      return type;
    } else if (Abstract.isParenTerm(tp)) {
      // We unwrap just one level -- in case we have a singleton tuple
      tp = Abstract.unaryArg(tp);
      if (Abstract.isParenTerm(tp)) {
        IType type = parseType(Abstract.unaryArg(tp), dict, errors, varHandler);
        return TypeUtils.tupleType(type);
      } else
        return parseType(tp, dict, errors, varHandler);
    } else if (Abstract.isTupleTerm(tp)) {
      IList tpl = Abstract.tupleArgs(tp);
      IType elTypes[] = new IType[tpl.size()];
      for (int ix = 0; ix < elTypes.length; ix++)
        elTypes[ix] = parseType((IAbstract) tpl.getCell(ix), dict, errors, varHandler);
      return TypeUtils.tupleType(elTypes);
    } else if (CompilerUtils.isInterfaceType(tp))
      return parseInterfaceType(loc, CompilerUtils.interfaceTypeElements(tp), errors, dict, varHandler,
          new NullCollector());
    else if (Abstract.isBinary(tp, StandardNames.DETERMINES)) {
      IAbstract lhs = Abstract.binaryLhs(tp);
      if (Abstract.isBinary(lhs, StandardNames.OF) && Abstract.isIdentifier(Abstract.binaryLhs(lhs))) {
        List<IType> argTypes = parseArgTypes(Abstract.binaryRhs(lhs), dict, errors, varHandler);
        IType detType = TypeUtils.typeExp(StandardNames.DETERMINES, parseArgTypes(Abstract.binaryRhs(tp), dict, errors,
            varHandler));
        argTypes.add(detType);

        IType tyCon = parseType(Abstract.binaryLhs(lhs), dict, errors, varHandler);
        IType type = checkConstraints(TypeUtils.typeExp(tyCon, argTypes), dict, loc, errors);

        try {
          type = typeAlias(dict, type, loc);
        } catch (TypeConstraintException e) {
          errors.reportError(e.getMessage(), Location.merge(loc, e.getLocs()));
        }

        return type;
      } else {
        errors.reportError("cannot understand type expression: " + tp, loc);
        return new TypeVar();
      }
    } else {
      errors.reportError("cannot understand type expression: " + tp, loc);
      return new TypeVar();
    }
  }

  private static IType checkConstraints(IType type, Dictionary dict, Location loc, ErrorReport errors) {
    ITypeDescription desc = dict.getTypeDescription(type.typeLabel());
    if (desc != null) {
      try {
        desc.verifyType(type, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("parsed type ", type, " not consistent with declared type ", desc.getType(),
            "\nbecause ", e.getWords()), loc);
      }
    }
    return type;
  }

  private static Map<String, TypeVar> parseQuantifiers(IAbstract tp, ErrorReport errors, Dictionary dict, TypeNameHandler varHandler) {
    Map<String, TypeVar> bndTypes = new HashMap<>();
    parseQuants(tp, errors, dict, bndTypes, varHandler);
    return bndTypes;
  }

  private static void parseQuants(IAbstract tp, ErrorReport errors, Dictionary dict, Map<String, TypeVar> vars, TypeNameHandler varHandler) {
    if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      parseQuants(Abstract.binaryLhs(tp), errors, dict, vars, varHandler);
      parseConstraints(Abstract.binaryRhs(tp), dict, errors, varHandler);
    } else if (Abstract.isBinary(tp, StandardNames.COMMA)) {
      parseQuants(Abstract.binaryLhs(tp), errors, dict, vars, varHandler);
      parseQuants(Abstract.binaryRhs(tp), errors, dict, vars, varHandler);
    } else if (CompilerUtils.isTypeVar(tp)) {
      String vrName = CompilerUtils.typeVarName(tp);
      TypeVar var = new TypeVar(vrName, vrName, AccessMode.readOnly);
      vars.put(vrName, var);
      varHandler.defineType(vrName, var);
    } else if (CompilerUtils.isTypeFunVar(tp)) {
      String vrName = CompilerUtils.typeFunVarName(tp);
      TypeVar var = TypeVar.var(vrName, 1, AccessMode.readOnly);
      vars.put(vrName, var);
      varHandler.defineType(vrName, var);
    } else if (Abstract.isIdentifier(tp)) {
      String vrName = Abstract.getId(tp);
      TypeVar var = new TypeVar(vrName, vrName, AccessMode.readOnly);
      vars.put(vrName, var);
      varHandler.defineType(vrName, var);
    } else
      errors.reportError("invalid bound type variable: " + tp, tp.getLoc());
  }

  private static IType parseFunArgType(IAbstract tp, Dictionary cxt, ErrorReport errors, TypeNameHandler varHandler) {
    IType type = parseType(tp, cxt, errors, varHandler);

    // put the parens back
    if (Abstract.isParenTerm(tp))
      return TypeUtils.tupleType(type);

    IType unwrapped = TypeUtils.unwrap(type);
    if (!TypeUtils.isTupleType(unwrapped) && !TypeUtils.isTypeInterface(unwrapped)) {
      errors.reportError(StringUtils.msg("invalid argument type ", type), tp.getLoc());
      return TypeUtils.tupleType(type);
    }

    return type;
  }

  /**
   * Valid constraints include
   * <p>
   * con over (t1,..,tn)
   * <p>
   * {field has type tp; ... ; field has type tpn}
   * <p>
   * %t depends on tp
   * <p>
   * constraint and constraint
   *
   * @param varHandler callback to handle occurrences of names
   */
  private static void parseConstraints(IAbstract cons, final Dictionary cxt, final ErrorReport errors,
                                       TypeNameHandler varHandler) {
    final Location loc = cons.getLoc();

    if (Abstract.isParenTerm(cons))
      parseConstraints(Abstract.deParen(cons), cxt, errors, varHandler);
    else if (Abstract.isBinary(cons, StandardNames.AND)) {
      parseConstraints(Abstract.binaryLhs(cons), cxt, errors, varHandler);
      parseConstraints(Abstract.binaryRhs(cons), cxt, errors, varHandler);
    } else if (Abstract.isBinary(cons, StandardNames.IMPLEMENTS)
        && (CompilerUtils.isTypeVar(Abstract.binaryLhs(cons)) || Abstract.isIdentifier(Abstract.binaryLhs(cons)))
        && CompilerUtils.isInterfaceType(Abstract.binaryRhs(cons))) {
      IType constrainedType = parseType(Abstract.binaryLhs(cons), cxt, errors, varHandler);

      if (constrainedType instanceof TypeVar) {
        final TypeVar tVar = (TypeVar) constrainedType;

        ITypeCollector hndlr = new ITypeCollector() {
          @Override
          public void fieldAnnotation(Location loc, String name, IType type) {
            try {
              TypeUtils.addFieldConstraint(tVar, loc, name, type, cxt, true);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg("could not add constraint ", name, " has type ", type, "\nbecause ", e
                  .getWords()), Location.merge(loc, e.getLocs()));
            }
          }

          @Override
          public void kindAnnotation(Location loc, String name, IType type) {
            try {
              TypeUtils.addTypeConstraint(tVar, loc, name, type, cxt, true);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg("could not add constraint ", name, " has kind ", type.kind(),
                  "\nbecause ", e.getWords()), Location.merge(loc, e.getLocs()));
            }
          }

          @Override
          public void completeInterface(Location loc) {
          }
        };
        findMemberTypes(loc, CompilerUtils.interfaceTypeElements(Abstract.binaryRhs(cons)), hndlr, cxt, errors,
            varHandler);
      } else {
        errors.reportError(StringUtils.msg(constrainedType, " cannot have constraint ", Abstract.binaryRhs(cons),
            " because it is not a type variable"), loc);
      }
    } else if (CompilerUtils.isKindAnnotation(cons)) {
      IType bndType = parseType(CompilerUtils.kindAnnotatedTerm(cons), cxt, errors, varHandler);
      if (bndType instanceof TypeVar) {
        TypeVar tv = (TypeVar) bndType;

        IAbstract tpSpec = CompilerUtils.kindAnnotation(cons);

        if (Abstract.isIdentifier(tpSpec, StandardNames.TYPE)) {
          tv.setConstraint(new HasKind(tv, Kind.type));
        } else if (Abstract.isBinary(tpSpec, StandardNames.OF)) {
          IAbstract kindArgs = Abstract.deParen(Abstract.binaryRhs(tpSpec));
          int arity = 0;
          if (Abstract.isIdentifier(kindArgs, StandardNames.TYPE))
            arity = 1;
          else if (Abstract.isTupleTerm(kindArgs))
            arity = Abstract.tupleArity(kindArgs);
          else
            errors.reportError(StringUtils.msg("invalid declaration of kind: ", tpSpec), loc);
          tv.setConstraint(new HasKind(tv, Kind.kind(arity)));
        } else
          errors.reportError(StringUtils.msg("invalid kind specification: ", tpSpec), loc);

        IAbstract constraint = CompilerUtils.kindAnnotatedConstraint(cons);
        if (constraint != null)
          parseConstraints(CompilerUtils.kindAnnotatedConstraint(cons), cxt, errors, varHandler);
      } else
        errors.reportError(StringUtils.msg("invalid kind specification: ", cons), loc);
    } else if (Abstract.isBinary(cons, StandardNames.INSTANCE_OF) && CompilerUtils.isTypeVar(Abstract.binaryLhs(cons))) {
      final TypeVar var = (TypeVar) parseType(Abstract.binaryLhs(cons), cxt, errors, varHandler);

      IType general = parseType(Abstract.binaryRhs(cons), cxt, errors, varHandler);
      ITypeConstraint con = new InstanceOf(var, general);
      try {
        var.addConstraint(con, loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError("could not add constraint " + cons + "\nbecause " + e.getMessage(), Location.merge(loc, e
            .getLocs()));
      }
    } else if (Abstract.isBinary(cons, StandardNames.OVER)) {
      IAbstract conTp = Abstract.binaryRhs(cons);
      String contractName = Abstract.getId(Abstract.binaryLhs(cons));
      TypeContract contract = cxt.getContract(contractName);
      if (contract == null && !varHandler.suppressWarnings())
        errors.reportError(StringUtils.msg("contract ", contractName, " is not known"), loc);
      if (Abstract.isBinary(conTp, StandardNames.DETERMINES)) {
        List<IType> argTypes = parseArgTypes(Abstract.binaryLhs(conTp), cxt, errors, varHandler);

        List<IType> depTypes = parseArgTypes(Abstract.binaryRhs(conTp), cxt, errors, varHandler);
        argTypes.add(TypeUtils.typeExp(StandardNames.DETERMINES, depTypes));

        TypeExp contractType = (TypeExp) TypeUtils.typeExp(contractName, argTypes);

        if (contract != null)
          try {
            Subsume.subsume(contractType, Freshen.freshenForUse(contract.getContractType().getType()), loc, cxt, true);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("contract constraint ", cons, " not consistent with contract ",
                contractName, "\nbecause ", e.getWords()), loc);
          }

        ITypeConstraint con = new ContractConstraint(contractType);

        for (IType tp : argTypes)
          if (TypeUtils.isTypeVar(tp))
            ((TypeVar) TypeUtils.deRef(tp)).setConstraint(con);
          else if (TypeUtils.isTypeExp(tp) && TypeUtils.isTypeVar(TypeUtils.getTypeCon(tp)))
            ((TypeVar) TypeUtils.deRef(TypeUtils.getTypeCon(tp))).setConstraint(con);
      } else {
        List<IType> argTypes = parseArgTypes(Abstract.binaryRhs(cons), cxt, errors, varHandler);
        TypeExp contractType = (TypeExp) TypeUtils.typeExp(contractName, argTypes);

        if (contract != null)
          try {
            Subsume.subsume(Freshen.freshenForUse(contract.getContractType().getType()), contractType, loc, cxt, true);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("contract constraint ", cons, " not consistent with contract ",
                contractName, "\nbecause ", e.getWords()), loc);
          }
        ITypeConstraint con = new ContractConstraint(contractType);

        for (IType tp : argTypes)
          if (TypeUtils.isTypeVar(tp))
            ((TypeVar) TypeUtils.deRef(tp)).setConstraint(con);
          else if (TypeUtils.isTypeExp(tp) && TypeUtils.isTypeVar(TypeUtils.getTypeCon(tp)))
            ((TypeVar) TypeUtils.deRef(TypeUtils.getTypeCon(tp))).setConstraint(con);
      }
    } else if (Abstract.isUnary(cons, StandardNames.TUPLE) && CompilerUtils.isTypeVar(Abstract.unaryArg(cons))) {

      final TypeVar tVar = (TypeVar) parseType(Abstract.unaryArg(cons), cxt, errors, varHandler);
      TupleConstraint con = new TupleConstraint(tVar);
      tVar.setConstraint(con);
    } else
      errors.reportError("not a valid type constraint: " + cons, loc);
  }

  public static TypeExp parseContractType(IAbstract tp, Dictionary cxt, ErrorReport errors, TypeNameHandler varHandler) {
    if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      TypeExp type = parseContractType(Abstract.binaryLhs(tp), cxt, errors, varHandler);
      parseConstraints(Abstract.binaryRhs(tp), cxt, errors, varHandler);
      return type;
    } else if (Abstract.isBinary(tp, StandardNames.OVER)) {
      IAbstract conTp = Abstract.binaryRhs(tp);
      if (Abstract.isBinary(conTp, StandardNames.DETERMINES)) {
        List<IType> argTypes = parseArgTypes(Abstract.binaryLhs(conTp), cxt, errors, varHandler);
        List<IType> depTypes = parseArgTypes(Abstract.binaryRhs(conTp), cxt, errors, varHandler);
        argTypes.add(TypeUtils.typeExp(StandardNames.DETERMINES, depTypes));
        return (TypeExp) TypeUtils.typeExp(Abstract.getId(Abstract.binaryLhs(tp)), argTypes);
      } else {
        List<IType> argTypes = parseArgTypes(Abstract.binaryRhs(tp), cxt, errors, varHandler);
        return (TypeExp) TypeUtils.typeExp(Abstract.getId(Abstract.binaryLhs(tp)), argTypes);
      }
    } else {
      errors.reportError("not a valid contract specification: " + tp, tp.getLoc());
      return (TypeExp) StandardTypes.voidType;
    }
  }

  public static IType parseContractImplType(IAbstract tp, Dictionary cxt, ErrorReport errors, boolean suppress,
                                            boolean isFallback) {
    Dictionary dict = cxt.fork();
    TypeNameHandler varHandler = new RegularTypeName(dict, new HashMap<>(), AccessMode.readOnly,
        suppress, errors);
    return parseContractImplType(tp, dict, varHandler, isFallback, errors);
  }

  private static IType parseContractImplType(IAbstract tp, Dictionary cxt, TypeNameHandler varHandler,
                                             boolean isFallback, ErrorReport errors) {
    if (CompilerUtils.isUniversalType(tp)) {
      IAbstract tArg = CompilerUtils.universalTypeVars(tp);
      tp = CompilerUtils.universalBoundType(tp);
      Map<String, TypeVar> bndTypes = parseQuantifiers(tArg, errors, cxt, varHandler);

      IType boundType = parseContractImplType(tp, cxt, varHandler, isFallback, errors);
      varHandler.removeEntries(bndTypes);

      return UniversalType.universal(bndTypes.values(), boundType);
    } else if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      IType type = parseContractImplType(Abstract.binaryLhs(tp), cxt, varHandler, isFallback, errors);
      parseConstraints(Abstract.binaryRhs(tp), cxt, errors, varHandler);
      return type;
    } else if (Abstract.isBinary(tp, StandardNames.OVER)
        && Abstract.isBinary(Abstract.binaryRhs(tp), StandardNames.DETERMINES)) {
      IAbstract implType = Abstract.binaryLhs(Abstract.binaryRhs(tp));
      List<IType> argTypes = parseArgTypes(implType, cxt, errors, varHandler);
      IType dependent = TypeUtils.typeExp(StandardNames.DETERMINES, parseArgTypes(Abstract.binaryRhs(Abstract
          .binaryRhs(tp)), cxt, errors, varHandler));

      if (isFallback) {
        for (IType argType : argTypes)
          if (!TypeUtils.isTypeVar(argType)) {
            errors.reportError("fallback implementation must be generic", tp.getLoc());
            break;
          }
      }

      argTypes.add(dependent);
      return TypeUtils.typeExp(TypeContracts.contractImplTypeName(Abstract.getId(Abstract.binaryLhs(tp))), argTypes);
    } else if (Abstract.isBinary(tp, StandardNames.OVER)) {
      List<IType> argTypes = parseArgTypes(Abstract.binaryRhs(tp), cxt, errors, varHandler);

      if (isFallback) {
        for (IType argType : argTypes)
          if (!TypeUtils.isTypeVar(argType)
              && (!(argType instanceof TypeExp) || TypeUtils.isTypeVar(((TypeExp) argType).getTypeCon()))) {
            errors.reportWarning("default implementation should be generic", tp.getLoc());
            break;
          }
      }
      return TypeUtils.typeExp(TypeContracts.contractImplTypeName(Abstract.getId(Abstract.binaryLhs(tp))), argTypes);
    } else {
      errors.reportError("not a valid contract implementation: " + tp, tp.getLoc());
      return StandardTypes.voidType;
    }
  }

  private static void parseAlgebraicConstructors(IAbstract tp, IType type, IAlgebraicType desc, Dictionary cxt,
                                                 Dictionary outer, ErrorReport errors, Map<String, TypeVar> typeVars) {
    for (IAbstract con : CompilerUtils.unWrap(tp, StandardNames.OR)) {
      con = CompilerUtils.stripVisibility(con);
      if (CompilerUtils.isBraceTerm(con))
        parseRecordConstructor(con, type, desc, cxt, errors, typeVars);
      else if (con instanceof Name)
        parsePositional0(con, type, desc, cxt, errors);
      else if (con instanceof Apply && ((Apply) con).getOperator() instanceof Name)
        parsePositional(con, type, desc, cxt, outer, errors, typeVars);
      else
        errors.reportError("invalid element of algebraic type definition: " + con, con.getLoc());
    }
  }

  private static void parsePositional(IAbstract tp, IType type, IAlgebraicType desc, Dictionary dict, Dictionary outer,
                                      ErrorReport errors, Map<String, TypeVar> typeVars) {
    Apply apply = (Apply) tp;
    String conName = apply.getOp();

    Dictionary conDict = dict.fork();
    TypeNameHandler varHandler = new RegularTypeName(conDict, typeVars, AccessMode.readOnly, true, errors);

    List<IType> typeArgs = parseArgTypes(apply.getArgs(), conDict, errors, varHandler);
    IType conType = Freshen.generalizeType(TypeUtils.constructorType(typeArgs, type), dict);
    Collection<IValueSpecifier> specs = desc.getValueSpecifiers();

    Location loc = tp.getLoc();
    final ConstructorSpecifier cons = new ConstructorSpecifier(loc, conName, specs.size(), conType, null);

    if (dict.isDefinedVar(conName)) {
      DictInfo info = dict.getVar(conName);
      errors.reportError(StringUtils
          .msg(conName, " already defined with type, ", info.getType(), " at ", info.getLoc()), Location.merge(loc,
          info.getLoc()));
    }

    specs.add(cons);
  }

  private static void parsePositional0(IAbstract tp, IType type, IAlgebraicType desc, Dictionary cxt, ErrorReport errors) {
    String label = ((Name) tp).getId();
    IType conType = Freshen.generalizeType(TypeUtils.constructorType(type));

    Collection<IValueSpecifier> specs = desc.getValueSpecifiers();

    ConstructorSpecifier cons = new ConstructorSpecifier(tp.getLoc(), label, specs.size(), conType, null);
    specs.add(cons);

    if (cxt.isDeclaredVar(label))
      errors.reportError("'" + label + "' already defined at " + cxt.getVar(label).getLoc(), tp.getLoc());
  }

  private static void parseRecordConstructor(IAbstract tp, IType type, final IAlgebraicType desc,
                                             final Dictionary dict, final ErrorReport errors, Map<String, TypeVar> typeVars) {
    final String conName = Abstract.getId(CompilerUtils.braceLabel(tp));
    final Dictionary conDict = dict.fork();
    Location loc = tp.getLoc();

    if (dict.isDefinedVar(conName)) {
      DictInfo info = dict.getVar(conName);
      errors.reportError(StringUtils
          .msg(conName, " already defined with type, ", info.getType(), " at ", info.getLoc()), Location.merge(loc,
          info.getLoc()));
    }

    HashMap<String, TypeVar> localVars = new HashMap<>(typeVars);
    TypeNameHandler varHandler = new RegularTypeName(conDict, localVars, AccessMode.readOnly, true, errors);
    ITypeCollector faceCollector = new ITypeCollector() {
      TypeInterface face = desc.getTypeInterface();

      @Override
      public void kindAnnotation(Location loc, String name, IType type) {
      }

      @Override
      public void fieldAnnotation(Location loc, String name, IType type) {
        SortedMap<String, IType> allTypes = face.getAllFields();

        if (allTypes.containsKey(name)) {
          try {
            Subsume.same(allTypes.get(name), type, loc, conDict);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("type of ", name, ":", type, " not consistent with existing type ",
                allTypes.get(name)), loc);
          }
        } else
          allTypes.put(name, type);

      }

      @Override
      public void completeInterface(Location loc) {
      }
    };
    IType face = parseInterfaceType(loc, CompilerUtils.braceArg(tp), errors, conDict, varHandler, faceCollector);

    Map<String, TypeVar> localTVars = stripoutExistentialVar(localVars, face);

    checkForExtraTypeVars(localTVars, typeVars, loc, errors);

    IType conType = Freshen.generalizeType(TypeUtils.constructorType(face, type), dict);

    Collection<IValueSpecifier> valueSpecifiers = desc.getValueSpecifiers();
    RecordSpecifier cons = new RecordSpecifier(tp.getLoc(), conName, null, valueSpecifiers.size(), conType);
    valueSpecifiers.add(cons);
  }

  private static void checkForExtraTypeVars(Map<String, TypeVar> localVars, Map<String, TypeVar> typeVars,
                                            Location loc, ErrorReport errors) {
    SortedSet<String> extra = new TreeSet<>();
    for (Entry<String, TypeVar> entry : localVars.entrySet())
      if (!typeVars.containsKey(entry.getKey()))
        extra.add(entry.getKey());
    if (!extra.isEmpty())
      errors.reportError(StringUtils.msg("type variable", (extra.size() > 1 ? "s " : " "), StringUtils.interleave(
          ",  ", extra), " must be explicitly quantified"), loc);
  }

  private static IType parseInterfaceType(Location loc, IAbstract tps, ErrorReport errors, final Dictionary dict,
                                          TypeNameHandler varHandler, ITypeCollector typeCollector) {
    final SortedMap<String, IType> fields = new TreeMap<>();
    final SortedMap<String, IType> types = new TreeMap<>();

    Dictionary rCxt = dict.fork();
    ITypeCollector handler = new TypeCollector(fields, types, errors, rCxt, typeCollector);

    findMemberTypes(loc, tps, handler, rCxt, errors, varHandler);

    return Freshen.existentializeType(new TypeInterfaceType(types, fields), rCxt);
  }

  private static void findMemberTypes(Location loc, IAbstract types, ITypeCollector collector, Dictionary dict,
                                      ErrorReport errors, TypeNameHandler varHandler) {
    for (IAbstract el : CompilerUtils.unWrap(types)) {
      Location elLoc = el.getLoc();
      if (CompilerUtils.isKindAnnotation(el)) {
        String fieldName = Abstract.getId(CompilerUtils.kindAnnotatedTerm(el));

        IAbstract tpSpec = CompilerUtils.kindAnnotation(el);
        if (Abstract.isIdentifier(tpSpec, StandardNames.TYPE)) {
          IType typeEl = TypeUtils.deRef(varHandler.newTypeVar(fieldName, elLoc, Kind.type));
          collector.kindAnnotation(elLoc, fieldName, typeEl);
        } else if (Abstract.isBinary(tpSpec, StandardNames.OF)) {
          IAbstract kindArgs = Abstract.deParen(Abstract.binaryRhs(tpSpec));
          int arity = 0;
          if (Abstract.isIdentifier(kindArgs, StandardNames.TYPE))
            arity = 1;
          else if (Abstract.isTupleTerm(kindArgs))
            arity = Abstract.tupleArity(kindArgs);
          else
            errors.reportError("invalid declaration of type: " + el, elLoc);
          IType fieldType = varHandler.newTypeVar(fieldName, elLoc, Kind.kind(arity));
          collector.kindAnnotation(elLoc, fieldName, fieldType);
        } else
          errors.reportError("invalid declaration of member type: " + el, elLoc);

        IAbstract constraint = CompilerUtils.kindAnnotatedConstraint(el);
        if (constraint != null)
          parseConstraints(constraint, dict, errors, varHandler);
      } else if (CompilerUtils.isTypeEquality(el)) {
        String fieldName = Abstract.getId(CompilerUtils.typeEqualField(el));
        IType tp = parseType(CompilerUtils.typeEqualType(el), dict, errors, varHandler);

        collector.kindAnnotation(elLoc, fieldName, tp);
      }
    }
    for (IAbstract tp : CompilerUtils.unWrap(types)) {
      if (CompilerUtils.isTypeAnnotation(tp)) {
        String fieldName = Abstract.getId(CompilerUtils.typeAnnotatedTerm(tp));
        IAbstract tpSpec = CompilerUtils.typeAnnotation(tp);

        IType memberType = parseType(tpSpec, dict, errors, varHandler);

        collector.fieldAnnotation(tpSpec.getLoc(), fieldName, memberType);
      }
    }

    collector.completeInterface(loc);
  }

  public static TypeContract parseTypeContractHead(IAbstract con, Dictionary dict, ErrorReport errors) {
    assert CompilerUtils.isContractStmt(con);

    IAbstract tpTerm = CompilerUtils.contractForm(con);

    Map<String, TypeVar> tVars = new HashMap<>();
    TypeNameHandler defHandler = new DefiningTypeName(dict, tVars, AccessMode.readOnly);
    TypeExp conType = parseContractType(tpTerm, dict, errors, defHandler);

    if (conType != null) {
      String contractName = conType.typeLabel();
      String contractImplName = TypeContracts.contractImplTypeName(contractName);
      IType conImplType = TypeUtils.typeExp(contractImplName, conType.getTypeArgs());

      Location loc = con.getLoc();
      TypeDescription contractType = new TypeDescription(loc, Freshen.generalizeType(conImplType, dict));

      return new TypeContract(loc, contractName, contractType);
    }
    return null;
  }

  public static TypeContract parseTypeContract(IAbstract con, Dictionary dict, ErrorReport errors,
                                               Map<String, Pair<IAbstract, IType>> defaultFuns, Map<String, IAbstract> integrity) {
    assert CompilerUtils.isContractStmt(con);

    IAbstract tpTerm = CompilerUtils.contractForm(con);
    IAbstract specTerm = CompilerUtils.contractSpec(con);

    Map<String, TypeVar> tVars = new HashMap<>();
    TypeNameHandler varHandler = new RegularTypeName(dict, tVars, AccessMode.readOnly, true, errors);
    TypeNameHandler defHandler = new DefiningTypeName(dict, tVars, AccessMode.readOnly);
    TypeExp conType = parseContractType(tpTerm, dict, errors, defHandler);

    if (conType != null) {
      String contractName = conType.typeLabel();
      String contractImplName = TypeContracts.contractImplTypeName(contractName);
      IType conImplType = TypeUtils.typeExp(contractImplName, conType.getTypeArgs());

      IType face = parseInterfaceType(con.getLoc(), specTerm, errors, dict, varHandler, new NullCollector());

      Location loc = con.getLoc();
      RecordSpecifier contractRecord = new RecordSpecifier(loc, contractName, null, 0, Freshen.generalizeType(TypeUtils
          .constructorType(face, conImplType)));
      TypeDescription contractType = new TypeDescription(loc, Freshen.generalizeType(conImplType), contractRecord);

      parseDefaults(CompilerUtils.braceTerm(loc, new Name(loc, contractImplName), specTerm), conImplType, dict,
          contractType, defaultFuns, integrity, errors);

      return new TypeContract(loc, contractName, contractType);
    }
    return null;
  }

  public static void fleshoutTypeContract(IAbstract con, TypeContract contract, Dictionary dict, ErrorReport errors,
                                          Map<String, Pair<IAbstract, IType>> defaultFuns, Map<String, IAbstract> integrity) {
    assert CompilerUtils.isContractStmt(con);

    IAbstract tpTerm = CompilerUtils.contractForm(con);
    IAbstract specTerm = CompilerUtils.contractSpec(con);

    Map<String, TypeVar> tVars = new HashMap<>();
    TypeNameHandler varHandler = new RegularTypeName(dict, tVars, AccessMode.readOnly, true, errors);
    TypeNameHandler defHandler = new DefiningTypeName(dict, tVars, AccessMode.readOnly);
    TypeExp conType = parseContractType(tpTerm, dict, errors, defHandler);
    Location loc = con.getLoc();

    if (conType != null) {
      String contractName = conType.typeLabel();
      String contractImplName = TypeContracts.contractImplTypeName(contractName);
      IType conImplType = TypeUtils.typeExp(contractImplName, conType.getTypeArgs());

      TypeNameHandler local = varHandler.fork();
      IType face = parseInterfaceType(con.getLoc(), specTerm, errors, dict, local, new NullCollector());
      Map<String, TypeVar> localTVars = stripoutExistentialVar(local.typeVars(), face);

      checkForExtraTypeVars(localTVars, tVars, loc, errors);

      RecordSpecifier contractRecord = new RecordSpecifier(loc, contractName, null, 0, Freshen.generalizeType(TypeUtils
          .constructorType(face, conImplType)));
      TypeDescription contractType = (TypeDescription) contract.getContractType();
      contractType.defineValueSpecifier(contractImplName, contractRecord);

      parseDefaults(CompilerUtils.braceTerm(loc, new Name(loc, contractImplName), specTerm), conImplType, dict,
          contractType, defaultFuns, integrity, errors);
    }
  }

  private static Map<String, TypeVar> stripoutExistentialVar(Map<String, TypeVar> typeVars, IType face) {
    while (face instanceof ExistentialType) {
      ExistentialType exists = (ExistentialType) face;

      typeVars.remove(exists.getBoundVar().getVarName());
      face = exists.getBoundType();
    }
    return typeVars;
  }

  public static TypeAlias parseTypeAlias(IAbstract stmt, Dictionary cxt, ErrorReport errors) {
    assert CompilerUtils.isTypeAlias(stmt);

    Dictionary tmpCxt = cxt.fork();

    LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    TypeNameHandler varHandler = new RegularTypeName(cxt, typeVars, AccessMode.readOnly, true, errors);

    IType thisType = typeHead(CompilerUtils.typeAliasType(stmt), tmpCxt, errors, varHandler);

    IType replacement = parseType(CompilerUtils.typeAliasAlias(stmt), tmpCxt, errors, varHandler);
    return new TypeAlias(stmt.getLoc(), Freshen.generalizeType(TypeUtils.typeExp(StandardNames.ALIAS, thisType,
        replacement), cxt));
  }

  public static void parseDefaults(IAbstract spec, IType thisType, Dictionary cxt, IAlgebraicType desc,
                                   Map<String, Pair<IAbstract, IType>> defaultFuns, Map<String, IAbstract> integrity, ErrorReport errors) {
    for (IAbstract con : CompilerUtils.unWrap(spec, StandardNames.OR)) {
      if (CompilerUtils.isBraceTerm(con)) {
        Location loc = con.getLoc();
        final String conName = Abstract.getId(CompilerUtils.braceLabel(con));
        String typeLabel = thisType.typeLabel();
        RecordSpecifier cons = (RecordSpecifier) desc.getValueSpecifier(conName);
        IType conType = Freshen.freshenForUse(cons.getConType());
        try {
          Subsume.same(TypeUtils.getConstructorResultType(conType), thisType, loc, cxt);
        } catch (TypeConstraintException e) {
          assert false : "should never happen";
        }

        Map<String, Integer> memberIndex = cons.getIndex();
        Map<String, IAbstract> defaults = new HashMap<>();

        locateDefaults(CompilerUtils.braceArg(con), memberIndex.keySet(), defaults, errors);
        locateIntegrityFuns(CompilerUtils.braceArg(con), memberIndex, integrity, CompilerUtils.integrityLabel(
            typeLabel, conName), TypeUtils.getConstructorArgType(conType));

        // Each default becomes a function from the non-default values to the
        // default value

        // Keep track of non-default variables
        List<String> nonDefaults = new ArrayList<>();
        for (Entry<String, Integer> entry : memberIndex.entrySet())
          if (!defaults.containsKey(entry.getKey()))
            nonDefaults.add(entry.getKey());

        IType conArgType = TypeUtils.getConstructorArgType(conType);
        TypeInterface conFace = (TypeInterface) TypeUtils.unwrap(conArgType);

        // Build the special pattern for this record
        String argPtns[] = nonDefaults.toArray(new String[nonDefaults.size()]);
        IType argTypes[] = new IType[argPtns.length];
        for (int ix = 0; ix < argPtns.length; ix++)
          argTypes[ix] = conFace.getFieldType(argPtns[ix]);

        for (Entry<String, IAbstract> entry : defaults.entrySet()) {
          final String member = entry.getKey();
          String defName = CompilerUtils.defaultLabel(typeLabel, conName, member);

          IAbstract args[] = new IAbstract[argPtns.length];
          for (int ix = 0; ix < args.length; ix++)
            args[ix] = DefFinder.isFound(nonDefaults, entry.getValue()) ? CompilerUtils.varPtn(new Name(loc,
                argPtns[ix])) : CafeSyntax.anonymous(loc);

          // (F1,..,Fn) => <deflt>
          IAbstract defFun = CompilerUtils.lambda(loc, Abstract.tupleTerm(loc, args), entry.getValue());
          defaultFuns.put(defName, Pair.pair(defFun, Freshen.generalizeType(TypeUtils.functionType(argTypes, conFace
              .getFieldType(member)))));
        }
      }
    }
  }

  static void locateDefaults(IAbstract content, Collection<String> members, Map<String, IAbstract> defaults,
                             ErrorReport errors) {
    for (IAbstract el : CompilerUtils.unWrap(content)) {
      Location loc = el.getLoc();
      if (CompilerUtils.isFunctionStatement(el) && CompilerUtils.isDefaultRule(CompilerUtils.functionRules(el))) {
        String att = Abstract.getId(CompilerUtils.functionName(el));
        if (members.contains(att)) {
          // Convert the function to a lambda
          defaults.put(att, CompilerUtils.convertToLambda(CompilerUtils.functionRules(el)));
        } else
          errors.reportError("no such member: " + att, loc);
      } else if (Abstract.isBinary(el, StandardNames.ASSIGN) && CompilerUtils.isDefaultRule(el)) {
        IAbstract def = CompilerUtils.defaultRulePtn(el);
        def = Abstract.deParen(def);
        if (CompilerUtils.isIdentifier(def)) {
          String att = Abstract.getId(def);
          if (members.contains(att))
            defaults.put(att, Abstract.unary(el.getLoc(), Cell.label, CompilerUtils.defaultRuleValue(el)));
          else
            errors.reportError("no such member: " + att, loc);
        } else
          errors.reportError("invalid declaration declaration of default: " + el, loc);
      } else if (CompilerUtils.isDefaultRule(el)) {
        IAbstract def = CompilerUtils.defaultRulePtn(el);
        if (Abstract.isIdentifier(def)) {
          String att = Abstract.getId(def);
          if (members.contains(att))
            defaults.put(att, CompilerUtils.defaultRuleValue(el));
          else
            errors.reportError("no such member: " + att, loc);
        }
      }
    }
  }

  private static void locateIntegrityFuns(IAbstract content, Map<String, Integer> memberIndex,
                                          Map<String, IAbstract> integrity, final String conName, IType type) {
    Wrapper<IAbstract> ass = Wrapper.create(null);

    for (IAbstract el : CompilerUtils.unWrap(content)) {
      if (CompilerUtils.isAssert(el))
        CompilerUtils.extendCondition(ass, CompilerUtils.asserted(el));
    }
    if (!CompilerUtils.isTrivial(ass.get())) {
      Location loc = content.getLoc();
      IAbstract recArg = new Name(loc, GenSym.genSym("__"));
      List<IAbstract> args = new ArrayList<>();
      args.add(recArg);
      List<IAbstract> els = new ArrayList<>();
      TypeInterface face = (TypeInterface) TypeUtils.deRef(type);

      for (String nm : memberIndex.keySet()) {
        IAbstract name = new Name(loc, nm);
        IAbstract fieldExp = CompilerUtils.fieldExp(loc, recArg, name);

        IType elType = face.getFieldType(nm);

        if (TypeUtils.isReferenceType(elType))
          fieldExp = CompilerUtils.shriekTerm(loc, fieldExp);

        IAbstract nmDef = CompilerUtils.defStatement(loc, name, fieldExp);
        els.add(nmDef);
      }
      els.add(CompilerUtils.assertion(loc, ass.get()));
      IAbstract test = CompilerUtils.blockTerm(loc, els);
      IAbstract assertion = CompilerUtils.actionRule(loc, Abstract.tupleTerm(loc, args), test);

      integrity.put(conName, assertion);
    }
  }

  private static class DefFinder extends DefaultAbstractVisitor {
    private final List<String> refs;
    private boolean found = false;

    public DefFinder(List<String> refs) {
      this.refs = refs;
    }

    public static boolean isFound(List<String> refs, IAbstract trm) {
      DefFinder finder = new DefFinder(refs);
      trm.accept(finder);
      return finder.found;
    }

    @Override
    public void visitName(Name name) {
      if (!found && refs.contains(name.getId()))
        found = true;
    }
  }

  // Either a single type or a tuple of types
  private static List<IType> parseArgTypes(IAbstract tp, Dictionary cxt, ErrorReport errors, TypeNameHandler varHandler) {
    List<IType> argTypes = new ArrayList<>();
    if (Abstract.isTupleTerm(tp)) {
      IList args = ((Apply) tp).getArgs();
      if (!args.isEmpty())
        for (IValue a : args)
          argTypes.add(parseType((IAbstract) a, cxt, errors, varHandler));
      else
        argTypes.add(StandardTypes.unitType);
    } else
      argTypes.add(parseType(tp, cxt, errors, varHandler));
    return argTypes;
  }

  public static ITypeDescription declareType(Dictionary tmpCxt, Dictionary cxt, IAbstract tp, ErrorReport errors) {
    Location loc = tp.getLoc();

    LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();
    TypeNameHandler varHandler = new RegularTypeName(cxt, typeVars, AccessMode.readWrite, false, errors);
    IType type = typeHead(CompilerUtils.typeDefnType(tp), tmpCxt, errors, varHandler);

    if (type != null) {
      String name = type.typeLabel();
      if (cxt.getTypeDescription(name) != null)
        errors.reportWarning("type `" + type + "' already declared at " + cxt.getTypeDescription(name).getLoc(), loc);

      TypeDescription desc = new TypeDescription(loc, Refresher.generalize(type, typeVars));
      cxt.defineType(desc);
      return desc;
    } else {
      errors.reportError("bad type definition statement", loc);
      return null;
    }
  }

  private static IType typeHead(IAbstract tp, Dictionary cxt, ErrorReport errors, TypeNameHandler varHandler) {
    tp = Abstract.deParen(tp);
    if (tp instanceof Name)
      return TypeUtils.typeExp(Abstract.getId(tp));
    else if (Abstract.isParenTerm(tp))
      return typeHead(Abstract.deParen(tp), cxt, errors, varHandler);
    else if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      IType head = typeHead(Abstract.binaryLhs(tp), cxt, errors, varHandler);
      parseConstraints(Abstract.binaryRhs(tp), cxt, errors, varHandler);
      return head;
    } else if (Abstract.isBinary(tp, StandardNames.OF) && Abstract.isIdentifier(Abstract.binaryLhs(tp))) {
      List<IType> argTypes = new ArrayList<>();
      IAbstract headArgs = Abstract.binaryRhs(tp);

      if (Abstract.isTupleTerm(headArgs)) {
        for (IValue a : ((Apply) headArgs).getArgs())
          argTypes.add(headArg((IAbstract) a, errors, varHandler));
      } else
        argTypes.add(headArg(headArgs, errors, varHandler));

      return TypeUtils.typeExp(Abstract.getId(Abstract.binaryLhs(tp)), argTypes);
    } else {
      errors.reportError("invalid type spec: " + tp, tp.getLoc());
      return null;
    }
  }

  private static IType headArg(IAbstract arg, ErrorReport errors, TypeNameHandler varHandler) {
    Location loc = arg.getLoc();

    if (CompilerUtils.isTypeVar(arg)) {
      String tvName = CompilerUtils.typeVarName(arg);
      return varHandler.newTypeVar(tvName, loc, Kind.unknown);
    } else if (CompilerUtils.isTypeFunVar(arg)) {
      String name = CompilerUtils.typeFunVarName(arg);
      return varHandler.newTypeVar(name, loc, Kind.kind(1));
    } else if (Abstract.isIdentifier(arg))
      return varHandler.newTypeVar(Abstract.getId(arg), loc, Kind.unknown);
    else {
      errors.reportError("expecting a type variable, not: " + arg, loc);
      return new TypeVar();
    }
  }

  private static List<IType> parseArgTypes(IList argTuple, Dictionary cxt, ErrorReport errors,
                                           TypeNameHandler varHandler) {
    List<IType> types = new ArrayList<>();

    for (IValue a : argTuple)
      types.add(parseType((IAbstract) a, cxt, errors, varHandler));

    return types;
  }

  private static IType typeAlias(Dictionary cxt, IType type, Location loc) throws TypeConstraintException {
    type = TypeUtils.deRef(type);

    final String name = type.typeLabel();

    ITypeDescription typeSpec = cxt.getTypeDescription(name);
    if (typeSpec instanceof ITypeAlias) {
      ITypeAlias alias = (ITypeAlias) typeSpec;
      return alias.apply(type, loc, cxt);
    } else if (typeSpec instanceof TypeExists)
      return type;
    else if (type.kind().equals(Kind.type) && typeSpec != null) {
      Subsume.subsume(type, Freshen.freshenForUse(typeSpec.getType()), loc, cxt, true);
      return type;
    } else
      return type;
  }
}
