package org.star_lang.star.compiler.type;

import static org.star_lang.star.compiler.util.AccessMode.readOnly;
import static org.star_lang.star.compiler.util.AccessMode.readWrite;
import static org.star_lang.star.data.type.Location.merge;
import static org.star_lang.star.data.type.Location.type;
import static org.star_lang.star.data.type.StandardTypes.booleanType;
import static org.star_lang.star.data.type.StandardTypes.charType;
import static org.star_lang.star.data.type.StandardTypes.decimalType;
import static org.star_lang.star.data.type.StandardTypes.floatType;
import static org.star_lang.star.data.type.StandardTypes.integerType;
import static org.star_lang.star.data.type.StandardTypes.longType;
import static org.star_lang.star.data.type.StandardTypes.rawCharType;
import static org.star_lang.star.data.type.StandardTypes.rawDecimalType;
import static org.star_lang.star.data.type.StandardTypes.rawFloatType;
import static org.star_lang.star.data.type.StandardTypes.rawIntegerType;
import static org.star_lang.star.data.type.StandardTypes.rawLongType;
import static org.star_lang.star.data.type.StandardTypes.rawStringType;
import static org.star_lang.star.data.type.StandardTypes.stringType;
import static org.star_lang.star.data.type.StandardTypes.unitType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.star_lang.star.CompileDriver;
import org.star_lang.star.code.Manifest;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.FreeVariables;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.AssertAction;
import org.star_lang.star.compiler.canonical.Assignment;
import org.star_lang.star.compiler.canonical.CaseAction;
import org.star_lang.star.compiler.canonical.CastExpression;
import org.star_lang.star.compiler.canonical.CastPtn;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.ConditionalExp;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.ContentCondition;
import org.star_lang.star.compiler.canonical.DefaultVisitor;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.EnvironmentEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;
import org.star_lang.star.compiler.canonical.ExceptionHandler;
import org.star_lang.star.compiler.canonical.FieldAccess;
import org.star_lang.star.compiler.canonical.ForLoopAction;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.Ignore;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.JavaEntry;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MatchingPattern;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.NFA;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.OpenStatement;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.Overloaded;
import org.star_lang.star.compiler.canonical.OverloadedFieldAccess;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.PackageTerm;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.PatternApplication;
import org.star_lang.star.compiler.canonical.ProcedureCallAction;
import org.star_lang.star.compiler.canonical.ProgramLiteral;
import org.star_lang.star.compiler.canonical.RaiseAction;
import org.star_lang.star.compiler.canonical.RaiseExpression;
import org.star_lang.star.compiler.canonical.RecordPtn;
import org.star_lang.star.compiler.canonical.RecordSubstitute;
import org.star_lang.star.compiler.canonical.RecordTerm;
import org.star_lang.star.compiler.canonical.RegExpPattern;
import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.ScalarPtn;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.Sequence;
import org.star_lang.star.compiler.canonical.Shriek;
import org.star_lang.star.compiler.canonical.SyncAction;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.TypeDefinition;
import org.star_lang.star.compiler.canonical.TypeWitness;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.VoidExp;
import org.star_lang.star.compiler.canonical.WherePattern;
import org.star_lang.star.compiler.canonical.WhileAction;
import org.star_lang.star.compiler.canonical.Yield;
import org.star_lang.star.compiler.grammar.Tokenizer;
import org.star_lang.star.compiler.sources.JavaImport;
import org.star_lang.star.compiler.sources.JavaInfo;
import org.star_lang.star.compiler.sources.NestedBuiltin;
import org.star_lang.star.compiler.sources.Pkg;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.Computations;
import org.star_lang.star.compiler.transform.Definition;
import org.star_lang.star.compiler.transform.Dependencies;
import org.star_lang.star.compiler.transform.Dependencies.DependencyResults;
import org.star_lang.star.compiler.transform.FlowAnalysis;
import org.star_lang.star.compiler.transform.MatchCompiler;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.transform.OverContext;
import org.star_lang.star.compiler.transform.QueryPlanner;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.ConsList;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.Sequencer;
import org.star_lang.star.compiler.util.StringSequence;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Triple;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ContractImplementation;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Quantifier;
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
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.Cons.ConsCons;
import org.star_lang.star.data.value.Cons.Nil;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.assignment.runtime.RefCell;
import org.star_lang.star.operators.system.runtime.SimpleLog;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.CatalogException;

/*
 * The TypeChecker implements the type inference module for Star
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
 */
public class TypeChecker
{
  private static final IType actionType = TypeUtils.typeCon(StandardNames.ACTION_TYPE, 1);
  public static final int HEX_RADIX = 16;
  private final ErrorReport errors;
  private final Pkg pkg;

  private TypeChecker(Pkg pkg)
  {
    this.errors = pkg.getErrors();
    this.pkg = pkg;
  }

  public ErrorReport getErrorReport()
  {
    return errors;
  }

  public static PackageTerm typeOfPkg(IAbstract pkgTerm, final Pkg pkge, final ErrorReport errors)
  {
    Dict baseDict = Dict.baseDict();
    Dictionary dict = baseDict.fork();

    final TypeChecker typeChecker = new TypeChecker(pkge);
    final Location loc = pkgTerm.getLoc();

    final String pkgName = pkgUniqName(pkge.getUri());
    final TypeVar pkgFace = new TypeVar();

    BoundChecker<PackageTerm> pkgChecker = new BoundChecker<PackageTerm>() {

      @Override
      public PackageTerm typeBound(List<IStatement> definitions, List<IContentAction> localActions, Over overloader,
          Dictionary thetaCxt, Dictionary dict)
      {
        List<IStatement> exportedDefs = new ArrayList<>();
        List<TypeDefinition> exportedTypes = new ArrayList<>();
        List<ITypeAlias> exportedAliases = new ArrayList<>();
        List<TypeContract> exportedContracts = new ArrayList<>();
        Map<String, Set<ContractImplementation>> exportedImplementations = new HashMap<>();

        List<Pair<Location, ResourceURI>> pkgImports = new ArrayList<>();

        // construct a umain program if there is a main and no umain
        typeChecker.generateMain(pkgName, thetaCxt, definitions, overloader, pkgFace);

        Set<Variable> freePkgVars = new HashSet<>();
        for (IStatement def : definitions)
          FreeVariables.addFreeVars(def, thetaCxt, freePkgVars);

        for (IStatement def : definitions) {
          if (def.getVisibility() == Visibility.pUblic) {
            if (def instanceof TypeDefinition)
              exportedTypes.add((TypeDefinition) def);
            else if (def instanceof TypeAliasEntry)
              exportedAliases.add(((TypeAliasEntry) def).getTypeAlias());
            else if (def instanceof ContractEntry)
              exportedContracts.add(((ContractEntry) def).getContract());
            else if (def instanceof ImplementationEntry)
              recordContractImplementation(exportedImplementations, (ImplementationEntry) def);
            else if (def instanceof VarEntry) {
              VarEntry var = (VarEntry) def;
              isRawThetaType(var, errors);

              exportedDefs.add(def);
            } else if (def instanceof ImportEntry)
              pkgImports.add(Pair.pair(def.getLoc(), ((ImportEntry) def).getUri()));
          }
        }

        // privateLoop: for (Iterator<IStatement> it = definitions.iterator(); it.hasNext();) {
        // IStatement def = it.next();
        // if (def instanceof VarEntry && def.getVisibility() == Visibility.priVate) {
        // VarEntry var = (VarEntry) def;
        // for (Variable v : var.getDefined()) {
        // if (freePkgVars.contains(v))
        // continue privateLoop;
        // }
        // it.remove();
        // }
        // }

        SortedMap<String, IContentExpression> elements = new TreeMap<>();

        for (IStatement entry : exportedDefs) {
          if (entry instanceof VarEntry) {
            Collection<Variable> defined = ((VarEntry) entry).getDefined();
            for (Variable v : defined) {
              String vrName = v.getName();
              if (!Utils.isAnonymous(vrName)) {
                elements.put(vrName, v.underLoad());
              }
            }
          }
        }

        for (TypeDefinition def : exportedTypes) {
          IAlgebraicType algDesc = def.getTypeDescription();
          Location descLoc = algDesc.getLoc();
          for (IValueSpecifier spec : algDesc.getValueSpecifiers()) {
            elements.put(spec.getLabel(), new Variable(descLoc, spec.getConType(), spec.getLabel()));
          }
        }

        sealInterface(pkgFace, elements, definitions, thetaCxt, errors, loc);
        TypeInterfaceType pFace = (TypeInterfaceType) TypeUtils.interfaceOfType(loc, pkgFace, thetaCxt);

        IType sealed = Freshen.generalizeType(Freshen.existentializeType(pFace, thetaCxt), thetaCxt);

        assert elements.size() == pFace.getAllFields().size();

        IContentExpression record = new RecordTerm(loc, sealed, elements, pFace.getAllTypes());
        IContentExpression pkgTheta;
        if (!localActions.isEmpty()) {
          localActions.add(new ValisAction(loc, record));
          pkgTheta = new LetTerm(loc, new ValofExp(loc, sealed, new Sequence(loc,
              TypeUtils.typeExp(actionType, sealed), localActions)), definitions);
        } else
          pkgTheta = new LetTerm(loc, record, definitions);

        pkgTheta = pkgTheta.transform(overloader, new OverContext(thetaCxt, errors, 0));

        // SimplifyCxt simplify = new SimplifyCxt();
        // IContentExpression simplified = pkgTheta.transform(new Simplify(), simplify);
        // System.out.println(simplify.getResolved());

        return new PackageTerm(loc, pkgName(pkge.getUri()), pkgName, sealed, pkgTheta, exportedTypes, exportedAliases,
            exportedContracts, pkgImports, pkge.getUri());
      }
    };

    return typeChecker.checkTheta(pkgTerm, dict, baseDict, pkgFace, pkgChecker);
  }

  private static String pkgUniqName(ResourceURI uri)
  {
    int hashCode = uri.hashCode();
    String name = URIUtils.getPackageName(uri);

    if (hashCode >= 0)
      return name + hashCode;
    else
      return name + "_" + Math.abs(hashCode);
  }

  private static String pkgName(ResourceURI uri)
  {
    return URIUtils.getPackageName(uri);
  }

  /*
   * Construct a main program that performs type coercion of string arguments passed on command line
   */
  private void generateMain(String pkgName, Dictionary dict, List<IStatement> definitions, Over overloader,
      IType thetaFace)
  {
    IType mainType = dict.getVarType(StandardNames.MAIN);

    if (mainType != null && !isLocallyDefined(definitions, StandardNames.UMAIN) && TypeUtils.isProcedureType(mainType)) {
      int errCount = errors.errorCount();

      Location loc = dict.getVar(StandardNames.MAIN).getLoc();
      IType[] argTypes = TypeUtils.getProcedureArgTypes(Freshen.freshenForUse(mainType));
      List<IContentExpression> mainArgs = new ArrayList<>();

      IType listType = TypeUtils.consType(StandardTypes.stringType);
      IType umainType = TypeUtils.procedureType(listType);

      IContentPattern umainArg = new ConstructorPtn(loc, Nil.label, listType);

      Dictionary nDict = dict.fork();
      RuleVarHandler varHandler = new RuleVarHandler(nDict, errors);
      for (int ix = argTypes.length - 1; ix >= 0; ix--) {
        IType argType = TypeUtils.deRef(argTypes[ix]);
        if (argType instanceof TypeVar)
          errors.reportError(StringUtils.msg("cannot have a non-ground type for main procedure: ", argType), loc);
        else {
          String argName = "A" + ix;
          umainArg = new ConstructorPtn(loc, ConsCons.label, listType, typeOfPtn(Abstract.name(loc, argName),
              StandardTypes.stringType, null, nDict, dict, varHandler), umainArg);
          IContentExpression arg = typeOfExp(Abstract.unary(loc, StandardNames.COERCE, Abstract.name(loc, argName)),
              argType, nDict, dict);
          mainArgs.add(0, arg);
        }
      }
      IContentExpression body = new Application(loc, TypeUtils.typeExp(actionType, unitType), Variable.create(loc,
          mainType, StandardNames.MAIN), new ConstructorTerm(loc, mainArgs));

      Triple<IContentPattern[], ICondition, IContentExpression> mainDo = Triple.create(
          new IContentPattern[] { umainArg }, CompilerUtils.truth, body);
      List<Triple<IContentPattern[], ICondition, IContentExpression>> rules = new ArrayList<>();
      rules.add(mainDo);

      PrettyPrintDisplay disp = new PrettyPrintDisplay();
      disp.append("usage: <");
      disp.append(pkgName);
      disp.append(">");
      for (IType argType : argTypes) {
        disp.append(" ");
        DisplayType.display(disp, argType);
      }
      IContentExpression warning = new Scalar(loc, Factory.newString(disp.toString()));
      IType logMsgType = SimpleLog.type();
      IContentExpression logMsg = typeOfExp(new Name(loc, SimpleLog.name), logMsgType, dict, null);
      IContentExpression log = new Application(loc, unitType, logMsg, new IContentExpression[] { warning });

      Triple<IContentPattern[], ICondition, IContentExpression> deflt = Triple.create(new IContentPattern[] { Variable
          .anonymous(loc, listType) }, CompilerUtils.truth, log);

      ProgramLiteral umainProc = MatchCompiler.generateFunction(rules, deflt, umainType, nDict.getFreeVars(),
          StandardNames.UMAIN, loc, dict, dict, errors);

      IContentExpression umain = umainProc.transform(overloader, new OverContext(dict, errors, 0));

      if (errors.noNewErrors(errCount)) {
        Variable ref = Variable.create(loc, umainType, StandardNames.UMAIN);
        dict.declareVar(StandardNames.UMAIN, ref, readOnly, Visibility.pUblic, false);

        definitions.add(VarEntry.createVarEntry(loc, ref, umain, readOnly, Visibility.pUblic));
        addToThetaInterface(loc, StandardNames.UMAIN, umainType, thetaFace, dict);
      }
    }
  }

  private boolean isLocallyDefined(List<IStatement> definitions, String umain)
  {
    for (IStatement stmt : definitions)
      if (stmt.defines(umain)) {
        if (stmt instanceof VarEntry && ((VarEntry) stmt).getValue() instanceof FunctionLiteral)
          return true;
      }
    return false;
  }

  /**
   * Compute the type of an expression term
   * 
   * @param term
   *          the abstract syntax of the term to type check
   * @param expectedType
   *          what the expected type of this term is
   * @param dict
   *          the dictionary context to type check within
   * @param outer
   *          the dictionary context associated with the next outer definition layer (i.e., let
   *          term)
   * @return the type of the expression
   */
  public IContentExpression typeOfExp(IAbstract term, final IType expectedType, final Dictionary dict,
      final Dictionary outer)
  {
    final Location loc = term.getLoc();
    int errCount = errors.errorCount();

    if (CompilerUtils.isIdentifier(term))
      return typeOfName(loc, Abstract.getId(term), expectedType, dict, errors);
    else if (CompilerUtils.isReference(term))
      return lvalueType(CompilerUtils.referencedTerm(term), expectedType, dict, outer);
    else if (CompilerUtils.isShriek(term)) {
      IAbstract ref = Abstract.deParen(CompilerUtils.shriekTerm(term));

      return new Shriek(loc, typeOfExp(ref, TypeUtils.referenceType(expectedType), dict, outer));
    } else if (term instanceof IntegerLiteral) {
      IContentExpression scalar = CompilerUtils.integerLiteral(loc, ((IntegerLiteral) term).getLit());
      return verifyType(integerType, expectedType, loc, scalar, dict, errors);
    }
    if (term instanceof LongLiteral) {
      IContentExpression scalar = CompilerUtils.longLiteral(loc, ((LongLiteral) term).getLit());

      return verifyType(longType, expectedType, loc, scalar, dict, errors);
    } else if (term instanceof FloatLiteral) {
      final IContentExpression scalar = TypeCheckerUtils.floatLiteral(loc, ((FloatLiteral) term).getLit());

      return verifyType(floatType, expectedType, loc, scalar, dict, errors);
    } else if (term instanceof BigDecimalLiteral) {
      final IContentExpression scalar = TypeCheckerUtils.decimalLiteral(loc, ((BigDecimalLiteral) term).getLit());

      return verifyType(decimalType, expectedType, loc, scalar, dict, errors);
    } else if (term instanceof CharLiteral) {
      final IContentExpression scalar = TypeCheckerUtils.charLiteral(loc, ((CharLiteral) term).getLit());

      return verifyType(charType, expectedType, loc, scalar, dict, errors);
    } else if (term instanceof StringLiteral) {
      IContentExpression scalar = CompilerUtils.stringLiteral(loc, ((StringLiteral) term).getLit());

      return verifyType(stringType, expectedType, loc, scalar, dict, errors);
    } else if (CompilerUtils.isRawLiteral(term)) {
      IAbstract arg = CompilerUtils.rawTerm(term);
      final IContentExpression raw;

      if (arg instanceof IntegerLiteral)
        raw = new Scalar(loc, rawIntegerType, ((IntegerLiteral) arg).getLit());
      else if (arg instanceof LongLiteral)
        raw = new Scalar(loc, rawLongType, ((LongLiteral) arg).getLit());
      else if (arg instanceof FloatLiteral)
        raw = new Scalar(loc, rawFloatType, ((FloatLiteral) arg).getLit());
      else if (arg instanceof BigDecimalLiteral)
        raw = new Scalar(loc, rawDecimalType, ((BigDecimalLiteral) arg).getLit());
      else if (arg instanceof StringLiteral)
        raw = new Scalar(loc, rawStringType, ((StringLiteral) arg).getLit());
      else if (arg instanceof CharLiteral)
        raw = new Scalar(loc, rawCharType, ((CharLiteral) arg).getLit());
      else {
        errors.reportError(StringUtils.msg("not expecting ", term), loc);
        return new VoidExp(loc);
      }

      return verifyType(raw.getType(), expectedType, loc, raw, dict, errors);
    } else if (CompilerUtils.isRegexp(term)) {
      errors.reportError(StringUtils.msg("regular expression ", term, " not permitted as expression"), loc);
      return CompilerUtils.stringLiteral(loc, CompilerUtils.regexpExp(term));
    } else if (Abstract.isParenTerm(term)) {
      term = Abstract.unaryArg(term);// We only unwrap one level here
      if (Abstract.isTupleTerm(term))
        return typeOfTuple(term, expectedType, dict, outer);
      else
        return typeOfExp(term, expectedType, dict, outer);
    } else if (Abstract.isTupleTerm(term)) {
      return typeOfTuple(term, expectedType, dict, outer);
    } else if (isFunctionStmt(term))
      return typeOfFunction(term, expectedType, dict);
    else if (CompilerUtils.isLambdaExp(term))
      return typeOfLambda(term, expectedType, dict);
    else if (isProcedureStmt(term))
      return typeOfProcedure(Abstract.getId(CompilerUtils.procedureName(term)), term, expectedType, dict);
    else if (isPatternLambda(term))
      return typeOfPtnAbstraction(StandardNames.PATTERN, term, expectedType, dict);
    else if (CompilerUtils.isMemoExp(term))
      return typeOfMemo(term, expectedType, dict, outer);
    else if (CompilerUtils.isAnonAggConLiteral(term)) {
      IAbstract arg = CompilerUtils.anonAggEls(term);

      if (CompilerUtils.isThetaLiteral(term))
        return checkThetaLiteral(term, dict, expectedType);
      else if (TypeUtils.isTypeVar(expectedType)) {
        SortedMap<String, IContentExpression> els = new TreeMap<>();
        SortedMap<String, IType> memberTypes = new TreeMap<>();

        for (IAbstract el : CompilerUtils.unWrap(arg)) {
          if (CompilerUtils.isEquals(el)) {
            if (CompilerUtils.isIdentifier(Abstract.binaryLhs(el))) {
              String member = Abstract.getId(Abstract.binaryLhs(el));
              IType memType = new TypeVar();
              IContentExpression value = typeOfExp(Abstract.binaryRhs(el), memType, dict, outer);
              memberTypes.put(member, memType);
              els.put(member, value);
            } else
              errors.reportError(StringUtils.msg("invalid field name: ", Abstract.binaryLhs(el)), el.getLoc());
          } else if (CompilerUtils.isAssignment(el)) {
            if (CompilerUtils.isIdentifier(Abstract.binaryLhs(el))) {
              String member = Abstract.getId(Abstract.binaryLhs(el));
              IType memType = new TypeVar();
              IType refType = TypeUtils.referenceType(memType);
              IContentExpression value = typeOfExp(Abstract.binaryRhs(el), memType, dict, outer);
              memberTypes.put(member, refType);
              els.put(member, new ConstructorTerm(loc, RefCell.cellLabel(refType), refType, value));
            } else
              errors.reportError(StringUtils.msg("invalid field name: ", Abstract.binaryLhs(el)), el.getLoc());
          } else
            errors.reportError(StringUtils.msg("invalid member of aggregate value: ", el), el.getLoc());
        }

        TypeInterfaceType type = TypeUtils.typeInterface(memberTypes);

        try {
          Subsume.same(expectedType, type, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg(type, " not consistent with expected type ", expectedType, "\nbecause ", e
              .getWords()), Location.merge(loc, e.getLocs()));
        }
        return new RecordTerm(loc, type, els, new TreeMap<String, IType>());
      } else if (TypeUtils.isTypeInterface(expectedType)) {
        TypeInterfaceType face = (TypeInterfaceType) Freshen.freshenForEvidence(expectedType);
        final Map<String, ContractConstraint> constraintMap = constraintMap(face);

        SortedMap<String, IContentExpression> els = new TreeMap<>();
        Map<String, IType> memberTypes = face.getAllFields();
        Map<String, IType> types = face.getAllTypes();

        for (IAbstract el : CompilerUtils.unWrap(arg)) {
          if (CompilerUtils.isEquals(el)) {
            if (CompilerUtils.isIdentifier(Abstract.binaryLhs(el))) {
              String member = Abstract.getId(Abstract.binaryLhs(el));
              IType memType = memberTypes.get(member);

              if (memType == null) {
                errors.reportError(StringUtils.msg(member, " not part of expected type"), el.getLoc());
                memType = new TypeVar();
              }
              IContentExpression value = typeOfExp(Abstract.binaryRhs(el), memType, dict, outer);
              els.put(member, value);
            }
          } else if (CompilerUtils.isAssignment(el)) {
            if (CompilerUtils.isIdentifier(Abstract.binaryLhs(el))) {
              String member = Abstract.getId(Abstract.binaryLhs(el));
              IType memType = new TypeVar();
              IType refType = TypeUtils.referenceType(memType);
              IContentExpression value = typeOfExp(Abstract.binaryRhs(el), memType, dict, outer);
              memberTypes.put(member, refType);
              els.put(member, new ConstructorTerm(loc, RefCell.cellLabel(refType), refType, value));
            } else
              errors.reportError(StringUtils.msg("invalid field name: ", Abstract.binaryLhs(el)), el.getLoc());
          } else if (CompilerUtils.isTypeEquality(el)) {
            String name = Abstract.getId(CompilerUtils.typeEqualField(el));
            IType type = TypeParser.parseType(CompilerUtils.typeEqualType(el), dict, errors, readWrite);

            try {
              Subsume.subsume(types.get(name), type, el.getLoc(), outer);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg("provided type ", type, "for ", name, " not consistent with ", types
                  .get(name), "\nbecause", e.getWords()), loc);
            }
          } else
            errors.reportError(StringUtils.msg("invalid member of aggregate value: ", el), el.getLoc());
        }

        checkContractImplementations(loc, constraintMap, dict, els);

        if (els.size() != memberTypes.size()) {
          for (Entry<String, IType> entry : memberTypes.entrySet()) {
            if (!els.containsKey(entry.getKey()))
              errors.reportError(StringUtils.msg("no value given for `", entry.getKey(), "'"), loc);
          }
        }

        return RecordTerm.anonRecord(loc, face, els, face.getAllTypes());
      } else
        errors.reportError(StringUtils.msg("anonymous record ", term, " not consistent with ", expectedType), loc);
    } else if (CompilerUtils.isFieldAccess(term)) {
      IAbstract rc = CompilerUtils.fieldRecord(term);
      IAbstract field = CompilerUtils.fieldField(term);

      if (CompilerUtils.isIdentifier(field)) {
        IType recordType = new TypeVar();
        IContentExpression record = typeOfExp(rc, recordType, dict, outer);
        String fieldName = Abstract.getId(field);

        return fieldOfRecord(loc, record, fieldName, expectedType, dict, errors);
      } else if (Abstract.isParenTerm(field)) {
        IAbstract reform = CompilerUtils.fieldExp(loc, rc, Abstract.deParen(field));
        return typeOfExp(reform, expectedType, dict, outer);
      } else if (CompilerUtils.isBraceTerm(field)) {
        IAbstract reform = CompilerUtils.braceTerm(loc, CompilerUtils
            .fieldExp(loc, rc, CompilerUtils.braceLabel(field)), CompilerUtils.braceArg(field));
        return typeOfExp(reform, expectedType, dict, outer);
      } else if (field instanceof Apply && CompilerUtils.isIdentifier(((Apply) field).getOperator())) {
        // R.m(A1,..,An) -> (R.m)(A1,..,An)
        IAbstract reform = new Apply(loc,
            Abstract.binary(loc, StandardNames.PERIOD, rc, ((Apply) field).getOperator()), ((Apply) field).getArgs());
        return typeOfExp(reform, expectedType, dict, outer);
      } else {
        errors.reportError(StringUtils.msg("invalid expression after period, got ", field), loc);
        return new VoidExp(loc);
      }
    } else if (Abstract.isBinary(term, StandardNames.SUBSTITUTE)) {
      IAbstract rcTerm = Abstract.getArg(term, 0);
      IAbstract repTerm = Abstract.getArg(term, 1);

      IContentExpression agg = typeOfExp(rcTerm, expectedType, dict, outer);
      IType subType = new TypeVar();
      IContentExpression sub = typeOfExp(repTerm, subType, dict, outer);

      if (TypeUtils.isTypeInterface(subType)) {
        TypeVar substType = new TypeVar();
        TypeInterfaceType face = (TypeInterfaceType) TypeUtils.interfaceOfType(loc, subType, dict);
        for (Entry<String, IType> e : face.getAllFields().entrySet()) {
          IType subFldType = e.getValue();
          if (TypeUtils.isRawType(subFldType))
            errors.reportError(StringUtils.msg("may not use substitute with raw type ", subFldType, " for field ", e
                .getKey()), sub.getLoc());
          else
            substType.setConstraint(new FieldConstraint(substType, e.getKey(), subFldType));
        }
        try {
          Subsume.subsume(expectedType, substType, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg(sub, " not consistent with ", agg, "\nbecause ", e.getWords()), loc);
        }
      } else
        errors.reportError(StringUtils.msg(sub, " should be a collection of bindings"), sub.getLoc());

      return new RecordSubstitute(loc, expectedType, agg, sub);
    } else if (CompilerUtils.isLetTerm(term)) {
      IAbstract defs = CompilerUtils.letDefs(term);
      final IAbstract inRhs = CompilerUtils.letBound(term);

      BoundChecker<IContentExpression> checker = new BoundChecker<IContentExpression>() {

        @Override
        public IContentExpression typeBound(List<IStatement> definitions, List<IContentAction> localActions,
            Over overloader, Dictionary thetaCxt, Dictionary dict)
        {
          IContentExpression bndExp = typeOfExp(inRhs, expectedType, thetaCxt, outer);

          if (!localActions.isEmpty()) {
            localActions.add(new ValisAction(loc, bndExp));
            return new LetTerm(loc, new ValofExp(loc, expectedType, new Sequence(loc, TypeUtils.typeExp(actionType,
                bndExp.getType()), localActions)), definitions);
          } else
            return new LetTerm(loc, bndExp, definitions);
        }
      };
      return checkTheta(defs, dict.fork(), dict, new TypeVar(), checker);
    } else if (CompilerUtils.isQuoted(term)) {
      try {
        Subsume.subsume(StandardTypes.astType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors
            .reportError(StringUtils.msg("expression not consistent with expected type\nbecause ", e.getWords()), loc);
      }
      Quoter quoter = new Quoter(dict, outer, this);

      return quoter.quoted(CompilerUtils.quotedExp(term));
    } else if (CompilerUtils.isUnQuoted(term)) {
      errors.reportError("not permitted to use unquote outside a quoted expression", loc);
      return new VoidExp(loc);
    }
    // Logical conditions
    else if (Abstract.isBinary(term, StandardNames.AND) || Abstract.isBinary(term, StandardNames.OR)
        || Abstract.isUnary(term, StandardNames.NOT) || Abstract.isBinary(term, StandardNames.OTHERWISE)
        || Abstract.isBinary(term, StandardNames.IN) || CompilerUtils.isBoundTo(term)
        || Abstract.isBinary(term, StandardNames.IMPLIES)) {
      try {
        Subsume.subsume(booleanType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(term, " not consistent with expected type\nbecause ", e.getWords()), loc);
      }
      Dictionary sCxt = dict.fork();
      Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(term, sCxt, outer);
      ICondition cond = condInfo.left();
      List<Variable> free = condInfo.middle();

      if (QueryPlanner.isTransformable(cond)) {
        IType resltType = StandardTypes.booleanType;
        IContentExpression reslt = CompilerUtils.trueLiteral(loc);
        IContentExpression deflt = CompilerUtils.falseLiteral(loc);

        return QueryPlanner.transformReferenceExpression(cond, free, sCxt, outer, reslt, deflt, resltType, loc, errors);
      } else if (cond instanceof Conjunction) {
        cond = FlowAnalysis.analyseFlow(cond, free, new DictionaryChecker(dict, condInfo.right));
        return new ContentCondition(loc, cond);
      } else
        return new ContentCondition(loc, cond);
    } else if (Abstract.isBinary(term, StandardNames.NOT_EQUAL))
      return typeOfExp(Abstract.unary(loc, StandardNames.NOT, Abstract.binary(loc, StandardNames.EQUAL, Abstract
          .binaryLhs(term), Abstract.binaryRhs(term))), expectedType, dict, outer);
    // Type Cast
    else if (CompilerUtils.isCast(term)) {
      IType cast = TypeParser.parseType(CompilerUtils.castType(term), dict, errors, readWrite);

      IType innerType = new TypeVar();
      IContentExpression castee = typeOfExp(CompilerUtils.castExp(term), innerType, dict, outer);

      try {
        Subsume.subsume(cast, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("cast type ", cast, " not consistent with expected type: " + expectedType
            + "\nbecause ", e.getWords()), loc);
      }

      return new CastExpression(loc, TypeAliaser.actualType(loc, errors, dict, cast), castee);
    }
    // Type conversion
    else if (CompilerUtils.isCoerce(term)) {
      IType type = TypeParser.parseType(CompilerUtils.coercedType(term), dict, errors, readWrite);

      try {
        Subsume.subsume(type, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors
            .reportError(StringUtils.msg("coerced type: ", type, " not consistent with expected type: ", expectedType),
                merge(loc, e.getLocs()));
      }

      return typeOfExp(Abstract.unary(loc, StandardNames.COERCE, CompilerUtils.coercedExp(term)), expectedType, dict,
          outer);
    } else if (CompilerUtils.isTypeAnnotation(term)) {
      IType type = TypeParser.parseType(CompilerUtils.typeAnnotation(term), dict, errors, readWrite);

      try {
        Subsume.subsume(type, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("target type ", type, " not consistent with expected type: " + expectedType
            + "\nbecause ", e.getWords()), loc);
        return new VoidExp(loc);
      }

      return typeOfExp(CompilerUtils.typeAnnotatedTerm(term), type, dict, outer);
    } else if (CompilerUtils.isValofExp(term)) {
      Dictionary valCxt = dict.fork();
      List<IContentAction> valofBody = checkAction(CompilerUtils.valofBody(term), actionType, expectedType, valCxt,
          outer);

      if (!hasValis(valofBody))
        errors.reportError(StringUtils.msg("valof expression: ", term, " not guaranteed to return a value"), loc);

      return new ValofExp(loc, expectedType, valofBody);
    } else if (CompilerUtils.isRunComputation(term)) {
      IType monadType = TypeVar.var(GenSym.genSym("m"), 1, readWrite);

      ((TypeVar) monadType).setConstraint(new ContractConstraint(Computations.EXECUTION, monadType));

      IType mType = TypeUtils.typeExp(monadType, expectedType);
      IType abortType = TypeUtils.functionType(StandardTypes.exceptionType, expectedType);

      IContentExpression action = typeOfExp(CompilerUtils.runComputation(term), mType, dict, outer);
      IContentExpression abort = typeOfExp(CompilerUtils.runCompAbort(term), abortType, dict, outer);
      if (!(action instanceof VoidExp))
        return Computations.perform(loc, monadType, action, abort, dict, errors);
      else
        return action;
    } else if (CompilerUtils.isComputationExpression(term)) {
      IType mType = TypeUtils.typeCon(Abstract.getId(CompilerUtils.computationType(term)), 1);

      Dictionary valCxt = dict.fork();
      List<IContentAction> body = new ArrayList<>();

      IType taskResltType = new TypeVar();

      try {
        Subsume.subsume(TypeUtils.typeExp(mType, taskResltType), expectedType, loc, valCxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(mType, " expression not consistent with ", expectedType, " because ", e
            .getWords()), merge(loc, e.getLocs()));
      }

      for (IAbstract act : CompilerUtils.unWrap(CompilerUtils.computationBody(term), StandardNames.TERM))
        body.addAll(checkAction(act, mType, taskResltType, valCxt, outer));

      IContentAction valofBody = body.size() != 1 ? new Sequence(loc, taskResltType, body) : body.get(0);
      if (!hasValis(valofBody) && !TypeUtils.unifyUnitType(taskResltType, loc, dict))
        errors.reportError(StringUtils.msg(mType, " expression: ", term, " not guaranteed to return a value"), loc);

      return Computations.monasticate(valofBody, mType, errors, valCxt, outer);
    } else if (CompilerUtils.isRaise(term)) {
      IContentExpression code = typeOfExp(CompilerUtils.raisedCode(term), stringType, dict, outer);
      IContentExpression raised = typeOfExp(CompilerUtils.raisedException(term), new TypeVar(), dict, outer);
      IContentExpression location = typeOfExp(new Name(loc, StandardNames.MACRO_LOCATION), StandardTypes.locationType,
          dict, outer);
      IContentExpression ex = new ConstructorTerm(loc, EvaluationException.name, StandardTypes.exceptionType, code,
          raised, location);
      return new RaiseExpression(loc, expectedType, ex);
    } else if (CompilerUtils.isSequenceTerm(term))
      return sequenceExpression(term, expectedType, dict, outer);
    else if (CompilerUtils.isSquareSequenceTerm(term) || CompilerUtils.isLabeledSequenceTerm(term))
      return squareSequenceExpression(term, expectedType, dict, outer);
    else if (CompilerUtils.isRecordLiteral(term))
      return checkRecordLiteral(term, expectedType, dict, outer);
    else if (CompilerUtils.isBraceTerm(term))
      return recordExpression(term, expectedType, dict, outer);
    else if (CompilerUtils.isConditional(term)) {
      Dictionary ifCxt = dict.fork();
      Triple<ICondition, List<Variable>, List<Variable>> tstInfo = typeOfCondition(CompilerUtils.conditionalTest(term),
          ifCxt, outer);
      ICondition tst = tstInfo.left();
      List<Variable> free = tstInfo.middle();

      IContentExpression then = typeOfExp(CompilerUtils.conditionalThen(term), expectedType, ifCxt, outer);
      IContentExpression els = typeOfExp(CompilerUtils.conditionalElse(term), expectedType, dict, outer);

      if (QueryPlanner.isTransformable(tst)) {
        return QueryPlanner.transformCondition(tst, free, then, els, ifCxt, outer, errors);
      } else
        return new ConditionalExp(loc, expectedType, tst, then, els);
    } else if (CompilerUtils.isCaseTerm(term)) {
      Pair<IContentPattern, IContentExpression> deflt = null;
      IAbstract selTerm = CompilerUtils.caseSel(term);
      IAbstract caseTerms = CompilerUtils.caseRules(term);

      IType selectorType = new TypeVar();
      IContentExpression selector = typeOfExp(selTerm, selectorType, dict, outer);
      List<Pair<IContentPattern, IContentExpression>> cases = new ArrayList<>();

      for (IAbstract el : CompilerUtils.unWrap(caseTerms)) {
        if (CompilerUtils.isDefaultRule(el)) {
          Wrapper<ICondition> cond = new Wrapper<>(CompilerUtils.truth);
          Dictionary caseCxt = dict.fork();
          IContentPattern ptn = typeOfPtn(CompilerUtils.defaultRulePtn(el), selectorType, cond, caseCxt, dict,
              new RuleVarHandler(dict, errors));
          IContentExpression value = typeOfExp(CompilerUtils.caseRuleValue(el), expectedType, caseCxt, dict);
          if (!CompilerUtils.isTrivial(cond.get()))
            ptn = new WherePattern(loc, ptn, cond.get());
          deflt = Pair.pair(ptn, value);
        } else if (CompilerUtils.isCaseRule(el)) {
          Wrapper<ICondition> cond = new Wrapper<>(CompilerUtils.truth);
          Dictionary caseCxt = dict.fork();
          IContentPattern ptn = typeOfPtn(CompilerUtils.caseRulePtn(el), selectorType, cond, caseCxt, dict,
              new RuleVarHandler(dict, errors));
          IContentExpression value = typeOfExp(CompilerUtils.caseRuleValue(el), expectedType, caseCxt, dict);
          if (!CompilerUtils.isTrivial(cond.get()))
            ptn = new WherePattern(loc, ptn, cond.get());
          cases.add(Pair.pair(ptn, value));
        } else
          errors.reportError(StringUtils.msg("expecting a case clause, not ", el), el.getLoc());
      }

      if (errors.noNewErrors(errCount)) {
        if (!CompilerUtils.isComputational(selector))
          return MatchCompiler.generateCaseExpression(loc, selector, cases, deflt, expectedType, dict, outer, errors);
        else {
          Variable tmp = Variable.create(selector.getLoc(), selector.getType(), GenSym.genSym("XX"));
          return new ValofExp(selector.getLoc(), expectedType, new VarDeclaration(selector.getLoc(), tmp, readOnly,
              selector), new ValisAction(selector.getLoc(), MatchCompiler.generateCaseExpression(selector.getLoc(),
              tmp, cases, deflt, expectedType, dict, outer, errors)));
        }
      } else
        return new VoidExp(loc);
    } else if (Abstract.isBinary(term, StandardNames.DEFAULT)
        && Abstract.isBinary(Abstract.getArg(term, 0), StandardNames.WHERE)
        && (Abstract.isUnary(Abstract.argPath(term, 0, 0), StandardNames.ANYOF) || Abstract.isUnary(Abstract.argPath(
            term, 0, 0), StandardNames.ANY_OF))) {
      IAbstract df = Abstract.getArg(term, 1);
      IAbstract lhs = Abstract.argPath(term, 0, 0);
      IAbstract rhs = Abstract.argPath(term, 0, 1);

      Dictionary queryCxt = dict.fork();

      Triple<ICondition, List<Variable>, List<Variable>> queryInfo = typeOfCondition(rhs, queryCxt, outer);
      List<Variable> free = queryInfo.middle();

      IContentExpression bound = typeOfExp(Abstract.getArg(lhs, 0), expectedType, queryCxt, dict);
      IContentExpression deflt = typeOfExp(df, expectedType, dict, outer);

      return QueryPlanner.transformReferenceExpression(queryInfo.left(), free, queryCxt, outer, bound, deflt,
          expectedType, loc, errors);
    } else if (Abstract.isBinary(term, StandardNames.WHERE)
        && (Abstract.isUnary(Abstract.binaryLhs(term), StandardNames.ANYOF) || Abstract.isUnary(Abstract
            .getArg(term, 0), StandardNames.ANY_OF))) {
      IAbstract lhs = Abstract.getArg(term, 0);
      IAbstract rhs = Abstract.getArg(term, 1);

      Dictionary queryCxt = dict.fork();

      Triple<ICondition, List<Variable>, List<Variable>> queryInfo = typeOfCondition(rhs, queryCxt, outer);
      List<Variable> free = queryInfo.middle();

      final IContentExpression bound = typeOfExp(Abstract.unaryArg(lhs), expectedType, queryCxt, dict);

      return QueryPlanner.transformReferenceExpression(loc, free, bound, expectedType, queryInfo.left(), queryCxt,
          outer, errors);
    } else if (CompilerUtils.isQueryTerm(term))
      return queryExpression(term, expectedType, dict, outer);
    else if (CompilerUtils.isDefaultExp(term)) {
      IContentExpression normal = typeOfExp(CompilerUtils.defaultExpNormal(term), expectedType, dict, outer);
      IContentExpression deflt = typeOfExp(CompilerUtils.defaultExpDefault(term), expectedType, dict, outer);

      IContentAction normValis = new ValisAction(normal.getLoc(), normal);
      IContentAction defltValis = new ValisAction(deflt.getLoc(), deflt);

      return new ValofExp(loc, expectedType, new ExceptionHandler(loc, normValis, defltValis));
    } else if (Abstract.isBinary(term, StandardNames.APPLY)) {
      TypeVar argType = new TypeVar();
      argType.setConstraint(new TupleConstraint(argType));
      IType funType = TypeUtils.funcType(argType, expectedType);

      IContentExpression lhs = typeOfExp(Abstract.binaryLhs(term), funType, dict, outer);
      IContentExpression rhs = typeOfExp(Abstract.binaryRhs(term), argType, dict, outer);

      return new Application(term.getLoc(), expectedType, lhs, rhs);
    } else if (term instanceof Apply) {
      Apply apply = ((Apply) term);
      IList args = apply.getArgs();
      int arity = args.size();

      final IAbstract operator = apply.getOperator();

      if (CompilerUtils.isIdentifier(operator)) {
        String conName = Abstract.getId(operator);
        IValueSpecifier cons = dict.getConstructor(conName);

        if (cons instanceof ConstructorSpecifier) {
          ConstructorSpecifier con = (ConstructorSpecifier) cons;
          TypeExp conType = (TypeExp) Freshen.freshenForUse(con.getConType());

          assert TypeUtils.isConstructorType(conType);

          if (TypeUtils.arityOfConstructorType(conType) == arity) {
            try {
              IType resultType = TypeUtils.getConstructorResultType(conType);
              TypeUtils.unify(expectedType, resultType, loc, dict);
              IType argType = TypeUtils.getConstructorArgType(conType);
              if (TypeUtils.isTupleType(argType)) {
                IContentExpression tArgs[] = argTuple(args, TypeUtils.tupleTypes(argType), dict, outer);

                return new ConstructorTerm(loc, conName, expectedType, tArgs);
              } else {
                errors.reportError(StringUtils.msg("constructor: ", conName, " of type ", resultType,
                    " is not a labeled tuple"), loc);
              }
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg("type of ", term, " not consistent with expected type\nbecause ", e
                  .getWords()), loc);
            }
          } else
            errors.reportError(StringUtils.msg(conName, " expects ", TypeUtils.arityOfConstructorType(conType),
                " arguments, got ", arity), loc);

          return new VoidExp(loc);
        }
      }

      if (StandardNames.isKeyword(operator))
        errors.reportError(StringUtils.msg("unexpected keyword: ", operator, " in expression"), apply.getLoc());

      IType argTypes[] = new IType[args.size()];

      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();

      IType funType = new TypeVar();

      final IContentExpression fun = typeOfExp(operator, funType, dict, outer);

      try {
        if (TypeUtils.isConstructorType(funType)) {
          Subsume.same(funType, TypeUtils.constructorType(argTypes, expectedType), loc, outer);
          IContentExpression argTpl[] = argTuple(args, argTypes, dict, outer);
          if (isLocallyDefinedConstructor(fun, expectedType, dict))
            return new ConstructorTerm(loc, ((Variable) fun).getName(), expectedType, argTpl);
          else
            return Application.apply(loc, expectedType, fun, argTpl);
        } else {
          Subsume.subsume(funType, TypeUtils.functionType(argTypes, expectedType), loc, dict);
          IContentExpression argTpl[] = argTuple(args, argTypes, dict, outer);

          return Application.apply(loc, expectedType, fun, argTpl);
        }
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("function ", operator, " of type ", DisplayType.toString(funType),
            "\nnot consistent with expected type ", TypeUtils.functionType(argTypes, expectedType), "\nbecause ", e
                .getWords()), loc);
      }
    } else
      errors.reportError(StringUtils.msg("invalid expression: ", term), term.getLoc());

    return new VoidExp(loc, expectedType);
  }

  private IContentExpression typeOfTuple(IAbstract term, IType expectedType, Dictionary dict, Dictionary outer)
  {
    Location loc = term.getLoc();
    IList tpl = Abstract.tupleArgs(term);
    int arity = tpl.size();
    IType argTypes[];

    if (TypeUtils.isTupleType(expectedType))
      argTypes = TypeUtils.tupleTypes(expectedType);
    else if (TypeUtils.isTypeVar(expectedType)) {
      argTypes = new IType[arity];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();

      IType tupleType = TypeUtils.tupleType(argTypes);

      try {
        Subsume.subsume(tupleType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(
            StringUtils.msg("cannot bind ", expectedType, " to ", tupleType, "\nbecause ", e.getWords()), merge(loc, e
                .getLocs()));
      }
    } else {
      errors.reportError(StringUtils.msg("not expecting tuple ", term, " here"), loc);
      return new VoidExp(loc);
    }
    if (arity == argTypes.length) {
      IContentExpression args[] = new IContentExpression[arity];

      for (int ix = 0; ix < arity; ix++)
        args[ix] = typeOfExp((IAbstract) tpl.getCell(ix), argTypes[ix], dict, outer);

      return new ConstructorTerm(loc, TypeUtils.deRef(expectedType), args);
    } else {
      errors.reportError(StringUtils.msg("expecting a tuple of ", argTypes.length, " elements"), loc);
      return new VoidExp(loc);
    }
  }

  private IContentExpression recordExpression(IAbstract term, IType expectedType, Dictionary dict, Dictionary outer)
  {
    assert CompilerUtils.isBraceTerm(term);

    Location loc = term.getLoc();
    IAbstract label = CompilerUtils.braceLabel(term);
    IAbstract arg = CompilerUtils.braceArg(term);
    IType funType = new TypeVar();
    IType argType = new TypeVar();

    IContentExpression fun = typeOfExp(label, funType, dict, outer);

    IType conType = Freshen.freshenForUse(funType);
    IType conArgType;

    try {
      if (TypeUtils.isConstructorType(conType)) {
        Subsume.subsume(conType, TypeUtils.constructorType(argType, expectedType), loc, dict);
        conArgType = TypeUtils.getConstructorArgType(conType);
      } else {
        Subsume.subsume(conType, TypeUtils.funcType(argType, expectedType), loc, dict);
        conArgType = TypeUtils.getFunArgType(conType);
      }
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg(fun, " is not consistent, \nbecause ", e.getWords()), loc);
      return new VoidExp(loc);
    }

    if (conArgType instanceof ExistentialType)
      conArgType = Freshen.freshenForEvidence(conArgType);

    SortedMap<String, IContentExpression> elements = new TreeMap<>();

    if (TypeUtils.isTypeInterface(conArgType)) {
      TypeInterfaceType face = (TypeInterfaceType) TypeUtils.deRef(conArgType);
      SortedMap<String, IType> fieldTypes = face.getAllFields();
      SortedMap<String, IType> types = face.getAllTypes();

      final Map<String, ContractConstraint> constraintMap = constraintMap(face);

      if (arg != null) {
        for (IAbstract el : CompilerUtils.unWrap(arg)) {
          Location loc1 = el.getLoc();
          if (Abstract.isBinary(el, StandardNames.EQUAL)) {
            IAbstract lhs = Abstract.deParen(Abstract.binaryLhs(el));
            if (CompilerUtils.isIdentifier(lhs)) {

              String member = Abstract.getId(lhs);

              if (fieldTypes.containsKey(member)) {
                IType fieldType = TypeUtils.deRef(fieldTypes.get(member));
                if (TypeUtils.isReferenceType(fieldType)) {
                  errors
                      .reportError(StringUtils.msg(member, " has incorrect form of field, should use := not ="), loc1);
                  fieldType = TypeUtils.referencedType(fieldType);
                }
                elements.put(member, typeOfExp(Abstract.binaryRhs(el), fieldType, dict, outer));
              } else
                errors.reportError(StringUtils.msg(member, " not a member of ", label), loc1);
            } else
              errors.reportError(StringUtils.msg("expecting an identifier on lhs of ="), loc1);
          } else if (Abstract.isBinary(el, StandardNames.ASSIGN)) {
            IAbstract lhs = Abstract.deParen(Abstract.binaryLhs(el));
            if (CompilerUtils.isIdentifier(lhs)) {
              String member = Abstract.getId(lhs);

              if (fieldTypes.containsKey(member)) {
                IType elType = fieldTypes.get(member);

                if (TypeUtils.isReferenceType(elType)) {
                  IType refType = TypeUtils.referencedType(elType);
                  IContentExpression value = typeOfExp(Abstract.binaryRhs(el), refType, dict, outer);
                  elements.put(member, new ConstructorTerm(loc, RefCell.cellLabel(elType), elType, value));
                } else {
                  IContentExpression value = typeOfExp(Abstract.binaryRhs(el), elType, dict, outer);

                  elements.put(member, value);
                }
              } else
                errors.reportError(StringUtils.msg("'", member, "' not a member of ", label), loc1);
            } else
              errors.reportError(StringUtils.msg("expecting an identifier on lhs of ="), loc1);
          } else if (CompilerUtils.isTypeEquality(el)) {
            String name = Abstract.getId(CompilerUtils.typeEqualField(el));
            IType type = TypeParser.parseType(CompilerUtils.typeEqualType(el), dict, errors, readWrite);

            try {
              Subsume.subsume(types.get(name), type, el.getLoc(), outer);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg("cannot unify provided type ", type, " with ", types.get(name),
                  "\nbecause", e.getWords()), loc);
            }
          } else
            errors.reportError(StringUtils.msg(el, " is not a valid element of a record"), loc1);
        }
      }

      checkContractImplementations(loc, constraintMap, dict, elements);

      if (fun instanceof Variable) {
        Variable rcFun = (Variable) fun;
        String rcLabel = rcFun.getName();
        String tpLabel = TypeUtils.deRef(expectedType).typeLabel();

        List<String> missingFields = new ArrayList<>();

        if (!checkDefaults(loc, tpLabel, rcLabel, fieldTypes, elements, dict, missingFields)) {
          return checkIntegrities(loc, rcLabel, expectedType, new RecordTerm(loc, expectedType, fun, elements, types),
              dict);
        } else {
          // We build a valof/valis expression along the lines of:
          //
          // <pre>
          // valof{
          // F1 is E1;
          // ...
          // Fn is En;
          // D1 is DF1(F1,..,Fn)
          // Dk is DFk(F1,..,Fn)
          // valis Rec{F1=F1;...,Dk=Dk}
          // }
          // </pre>

          // We are going to build a valof..valis expression...
          List<IContentAction> body = new ArrayList<>();

          Set<String> atts = notSuppliedArgs(elements, fieldTypes.keySet());
          Pair<IContentExpression[], IType[]> nonDefaultsVars = nonDefaultsVars(loc, dict, tpLabel, rcLabel, face);
          IContentExpression[] nonDefaults = nonDefaultsVars.left;
          SortedMap<String, IContentExpression> defEls = new TreeMap<>();

          for (Entry<String, IContentExpression> nonDef : elements.entrySet()) {
            IContentExpression supplied = nonDef.getValue();
            String name = nonDef.getKey();
            Variable el = Variable.create(loc, supplied.getType(), name);
            body.add(new VarDeclaration(supplied.getLoc(), el, readOnly, supplied));
            if (supplied instanceof Variable)
              defEls.put(name, supplied); // We build up name=name
            else
              defEls.put(name, el);
          }

          // Next we put in the defaulted expressions

          for (String att : atts) {
            final IType attType = face.getFieldType(att);

            VarInfo deflt = getDefaultFor(dict, TypeUtils.deRef(expectedType).typeLabel(), rcLabel, att);
            if (deflt != null) {
              Location defltLoc = deflt.getLoc();
              Variable def = Variable.create(defltLoc, attType, att);

              IContentExpression defFun = deflt.getVariable().verifyType(defltLoc, errors,
                  TypeUtils.functionType(nonDefaultsVars.right, attType), dict, false);

              IContentExpression defltValue = Application.apply(defltLoc, attType, defFun, nonDefaults);

              body.add(new VarDeclaration(defltLoc, def, readOnly, defltValue));
              defEls.put(att, def);
            } else
              errors.reportError(StringUtils.msg("no defaults supplied for ", att, " in term ", term), loc);
          }

          // We finish with the valis action
          return checkIntegrities(loc, rcLabel, expectedType, new RecordTerm(loc, expectedType, fun, defEls, types),
              dict, body);
        }
      } else {
        try {
          Subsume.same(conArgType, new TypeInterfaceType(types, fieldTypes), loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("arg ", arg, " not consistent with ", fun, "\nbecause ", e.getWords()),
              loc);
        }
        return new RecordTerm(loc, expectedType, fun, elements, types);
      }
    } else {
      errors.reportError(StringUtils.msg("expecting an argument of type ", conArgType,
          "\nwhich is not consistent with ", arg), arg.getLoc());
      return new VoidExp(loc);
    }
  }

  private IContentExpression sequenceExpression(IAbstract term, IType expectedType, Dictionary dict, Dictionary outer)
  {
    Location loc = term.getLoc();
    String label = CompilerUtils.sequenceLabel(term);

    sequenceType(label, expectedType, dict, loc, errors);

    IAbstract content = CompilerUtils.sequenceContent(term);
    IAbstract construct = braceSequence(loc, content, StandardNames.ADD_TO_FRONT, StandardNames.APND,
        StandardNames.NIL, errors);

    return typeOfExp(construct, expectedType, dict, outer);
  }

  private IContentPattern squareSequencePattern(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition,
      Dictionary dict, Dictionary outer, PtnVarHandler varHandler)
  {
    Location loc = ptn.getLoc();
    final IAbstract content;

    if (CompilerUtils.isLabeledSequenceTerm(ptn)) {
      String label = CompilerUtils.sequenceLabel(ptn);
      sequenceType(label, expectedType, dict, loc, errors);
      content = CompilerUtils.labeledContent(ptn);
    } else {
      sequenceType(StandardNames.SEQUENCE, expectedType, dict, loc, errors);
      content = CompilerUtils.squareContent(ptn);
    }

    IAbstract construct = squareSequence(loc, content, StandardNames.PAIR, StandardNames.BACK, StandardNames.EMPTY,
        errors);

    return typeOfPtn(construct, expectedType, condition, dict, outer, varHandler);
  }

  private IContentExpression squareSequenceExpression(IAbstract term, IType expectedType, Dictionary dict,
      Dictionary outer)
  {
    Location loc = term.getLoc();
    final IAbstract content;

    if (CompilerUtils.isLabeledSequenceTerm(term)) {
      String label = CompilerUtils.sequenceLabel(term);
      sequenceType(label, expectedType, dict, loc, errors);
      content = CompilerUtils.labeledContent(term);
    } else {
      sequenceType(StandardNames.SEQUENCE, expectedType, dict, loc, errors);
      content = CompilerUtils.squareContent(term);
    }

    IAbstract construct = squareSequence(loc, content, StandardNames.ADD_TO_FRONT, StandardNames.APND,
        StandardNames.NIL, errors);

    return typeOfExp(construct, expectedType, dict, outer);
  }

  private static IType sequenceType(String label, IType expectedType, Dictionary dict, Location loc, ErrorReport errors)
  {
    final IType collType = new TypeVar(GenSym.genSym("%s"));
    final IType elType = new TypeVar();

    ((TypeVar) collType).setConstraint(new ContractConstraint(StandardNames.SEQUENCE, collType, TypeUtils
        .determinedType(elType)));

    if (TypeUtils.isReferenceType(expectedType))
      expectedType = TypeUtils.referencedType(expectedType);

    try {
      Subsume.subsume(collType, expectedType, loc, dict);
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils
          .msg("cannot use ", expectedType, " in sequence expression\nbecause ", e.getWords()), loc);
    }

    if (!label.equals(StandardNames.SEQUENCE)) {
      ITypeDescription spec = dict.getTypeDescription(label);
      if (spec instanceof ITypeAlias) {
        try {
          TypeUtils.unify(collType, TypeUtils.checkAlias(new TypeExp(label, elType), dict, loc), loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("cannot use ", label, " in query\nbecause ", e.getWords()), loc);
        }
      } else if (spec != null) {
        IType type = Freshen.freshenForUse(spec.getType());

        try {
          TypeUtils.unify(collType, type, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("cannot use " + label + " in query\nbecause ", e.getWords()), loc);
        }
      } else {
        errors.reportError(StringUtils.msg("type ", label, " not known"), loc);
      }
    }
    return elType;
  }

  public static IContentExpression typeOfName(Location loc, String name, IType expectedType, Dictionary dict,
      ErrorReport errors)
  {
    DictInfo info = dict.varReference(name);

    if (info != null) {
      IType type = info.getType();
      if (TypeUtils.isConstructorType(type)) {
        Pair<IType, Map<String, Quantifier>> ref = Freshen.freshen(type, readOnly, readWrite);
        IType conType = ref.left;
        int arity = TypeUtils.arityOfConstructorType(conType);
        if (TypeUtils.isTupleConstructorType(conType) && arity == 0) {
          IContentExpression trm = new ConstructorTerm(loc, name, TypeUtils.getConstructorResultType(conType));

          return verifyType(trm.getType(), expectedType, loc, trm, dict, errors);
        } else
          return verifyType(conType, expectedType, loc, new Variable(loc, conType, name), dict, errors);
      } else if (info instanceof VarInfo) {
        VarInfo varInfo = ((VarInfo) info);

        IContentExpression var = varInfo.getVariable().verifyType(loc, errors, expectedType, dict, false);

        if (TypeUtils.hasContractDependencies(expectedType)) {
          if (var instanceof OverloadedVariable)
            var = new Variable(loc, var.getType(), ((Variable) var).getName());
        } else if (TypeUtils.hasContractDependencies(var.getType())) {
          IType overType = TypeUtils.refreshOverloaded(Over.computeDictionaryType(var.getType(), loc, readWrite));
          var = new Overloaded(loc, TypeUtils.getOverloadedType(overType), overType, var);
        }

        return var;
      }
    } else if (name.equals(StandardNames.BRACES)) {
      TypeInterfaceType type = new TypeInterfaceType();

      return verifyType(type, expectedType, loc, new RecordTerm(loc, type, new TreeMap<String, IContentExpression>(),
          new TreeMap<String, IType>()), dict, errors);
    } else if (name.equals(StandardNames.SQUARE)) {
      IContentExpression op = typeOfName(loc, StandardNames.NIL, TypeUtils.functionType(expectedType), dict, errors);
      return new Application(loc, expectedType, op);
    } else if (name.equals(StandardNames.MACRO_LOCATION))
      return verifyType(type, expectedType, loc, Quoter.generateLocation(loc), dict, errors);
    else if (StandardNames.isKeyword(name))
      errors.reportError(StringUtils.msg("unexpected keyword: ", name), loc);
    else {
      Variable var = Variable.create(loc, expectedType, name);

      dict.declareVar(name, var, readWrite, Visibility.priVate, true);

      errors.reportError(StringUtils.msg("'", var, "' does not seem to be declared"), loc);

      return var;
    }
    return new VoidExp(loc);
  }

  // Handle query expressions

  private IContentExpression queryExpression(IAbstract term, IType expectedType, Dictionary dict, Dictionary outer)
  {
    Location loc = term.getLoc();

    // look for the <type> of {<query>} form ...
    IType elType = new TypeVar();
    final IType collType = new TypeVar(GenSym.genSym("%s"));
    final IAbstract query;
    IAbstract reducer = null;
    boolean isSorted = false;
    boolean ascending = true;
    boolean isCollection = true;
    boolean isReduction = false;
    final boolean eliminateDuplicates;

    if (Abstract.isBinary(term, StandardNames.OF) && CompilerUtils.isBlockTerm(Abstract.binaryRhs(term))) {
      IAbstract leftOf = Abstract.binaryLhs(term);

      if (Abstract.isIdentifier(leftOf)) {
        elType = sequenceType(Abstract.getId(leftOf), collType, outer, loc, errors);
      } else if (Abstract.isUnary(leftOf, StandardNames.REDUCTION)) {
        reducer = Abstract.unaryArg(leftOf);
        isCollection = false;
        isReduction = true;
      }

      query = CompilerUtils.blockContent(Abstract.binaryRhs(term));
    } else {
      query = term;
      try {
        TypeUtils.unify(collType, TypeUtils.arrayType(elType), loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError("(internal) could not unify collection type with array", loc);
      }
    }

    IAbstract condTrm, sortbyTrm, cntTrm, compWithTrm;

    if ((Abstract.isBinary(query, StandardNames.ORDERBY) || Abstract.isBinary(query, StandardNames.ORDERDESCENDINBY) || Abstract
        .isBinary(query, StandardNames.DESCENDINGBY))) {
      isSorted = true;
      if (Abstract.isBinary(query, StandardNames.ORDERDESCENDINBY)
          || Abstract.isBinary(query, StandardNames.DESCENDINGBY))
        ascending = false;

      if (Abstract.isBinary(Abstract.binaryRhs(query), StandardNames.USING)) {
        compWithTrm = Abstract.binaryRhs(Abstract.binaryRhs(query));
        sortbyTrm = Abstract.binaryLhs(Abstract.binaryRhs(query));
      } else {
        compWithTrm = new Name(loc, StandardNames.LESS);
        sortbyTrm = Abstract.binaryRhs(query);
      }
      IAbstract lQ = Abstract.binaryLhs(query);
      if (Abstract.isBinary(lQ, StandardNames.WHERE)) {
        cntTrm = Abstract.binaryLhs(lQ);
        condTrm = Abstract.binaryRhs(lQ);
      } else {
        errors.reportError("invalid query form, expecting a 'where' clause", lQ.getLoc());
        return new VoidExp(loc);
      }
    } else if (Abstract.isBinary(query, StandardNames.WHERE)) {
      cntTrm = Abstract.binaryLhs(query);
      condTrm = Abstract.binaryRhs(query);
      sortbyTrm = null;
      compWithTrm = null;
    } else {
      errors.reportError("invalid query form, expecting a 'where' clause", query.getLoc());
      return new VoidExp(loc);
    }

    final IContentExpression countExp;
    final IContentExpression bound;
    Dictionary queryCxt = dict.fork();

    Triple<ICondition, List<Variable>, List<Variable>> queryInfo = typeOfCondition(condTrm, queryCxt, outer);
    List<Variable> free = queryInfo.middle();

    if (Abstract.isUnary(cntTrm, StandardNames.ALL)) {
      countExp = null;
      bound = typeOfExp(Abstract.unaryArg(cntTrm), elType, queryCxt, dict);
      eliminateDuplicates = false;
    } else if (Abstract.isUnary(cntTrm, StandardNames.UNIQUE)) {
      countExp = null;
      bound = typeOfExp(Abstract.unaryArg(cntTrm), elType, queryCxt, dict);
      eliminateDuplicates = true;
      if (compWithTrm == null)
        compWithTrm = Abstract.name(loc, StandardNames.LESS);
      sortbyTrm = Abstract.unaryArg(cntTrm);
    } else if (Abstract.isBinary(cntTrm, StandardNames.OF)) {
      if (Abstract.isUnary(Abstract.getArg(cntTrm, 0), StandardNames.UNIQUE)) {
        countExp = typeOfExp(Abstract.argPath(cntTrm, 0, 0), integerType, dict, outer);
        eliminateDuplicates = true;
        if (compWithTrm == null)
          compWithTrm = Abstract.name(loc, StandardNames.LESS);
        sortbyTrm = Abstract.binaryRhs(cntTrm);
      } else {
        countExp = typeOfExp(Abstract.getArg(cntTrm, 0), integerType, dict, outer);
        eliminateDuplicates = false;
      }
      bound = typeOfExp(Abstract.getArg(cntTrm, 1), elType, queryCxt, dict);
    } else if (Abstract.isUnary(cntTrm, StandardNames.ANYOF) || Abstract.isUnary(cntTrm, StandardNames.ANY_OF)) {
      bound = typeOfExp(Abstract.unaryArg(cntTrm), elType, queryCxt, dict);
      isCollection = false;
      countExp = null;
      eliminateDuplicates = false;
    } else {
      countExp = null;
      bound = typeOfExp(cntTrm, elType, queryCxt, dict);
      eliminateDuplicates = false;
    }

    if (isCollection) {
      try {
        Subsume.subsume(collType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("expected type: ", expectedType, " not consistent with query type: ",
            collType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
    } else {
      try {
        Subsume.subsume(elType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("expected type: ", expectedType, " not consistent with query type: ",
            elType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
    }

    if (isSorted) {
      // The expression we will be comparing against
      IType selType = new TypeVar();
      ((TypeVar) selType).setConstraint(new ContractConstraint((TypeExp) TypeUtils.typeExp(StandardNames.COMPARABLE,
          selType)));
      IContentExpression selector = typeOfExp(sortbyTrm, selType, queryCxt, dict);

      IType boundType = bound.getType();

      IContentExpression wrapBound = new ConstructorTerm(loc, bound, selector);

      IType intSequenceType = (isCollection ? rewrapSequenceType(collType, wrapBound.getType()) : TypeUtils
          .arrayType(wrapBound.getType()));
      IContentExpression transformed = QueryPlanner.transformQuery(loc, free, wrapBound, intSequenceType, queryInfo
          .left(), dict, outer, errors);

      // Define a lambda to do the comparison
      IContentExpression compLambda = compLambda(loc, boundType, selType, compWithTrm, ascending, dict, outer);

      // apply the compare to sort the result of the query
      IType sortResultType = transformed.getType();
      IContentExpression sortFun = typeOfExp(new Name(loc, StandardNames.SORT), TypeUtils.functionType(sortResultType,
          compLambda.getType(), sortResultType), dict, outer);
      IContentExpression sorted = Application.apply(loc, sortResultType, sortFun, transformed, compLambda);
      IType projectedType = new TypeVar();
      IContentExpression project = typeOfExp(new Name(loc, StandardNames.PROJECT_0), TypeUtils.functionType(
          sortResultType, projectedType), dict, outer);
      IContentExpression result = Application.apply(loc, projectedType, project, sorted);

      if (isCollection) {
        if (eliminateDuplicates) {
          IType cmpType = TypeUtils.functionType(bound.getType(), bound.getType(), booleanType);
          IType makeType = TypeUtils.functionType(projectedType, cmpType, expectedType);

          IContentExpression equals = typeOfExp(new Name(loc, StandardNames.EQUAL), cmpType, dict, outer);
          IContentExpression unique = typeOfExp(new Name(loc, StandardNames.UNIQUE_F), makeType, dict, outer);
          result = Application.apply(loc, expectedType, unique, result, equals);
        }
        if (countExp != null) {
          IType sliceFunType = TypeUtils.functionType(expectedType, integerType, integerType, expectedType);
          IContentExpression sliceFun = typeOfExp(new Name(loc, StandardNames.SLICE), sliceFunType, dict, outer);

          result = Application.apply(loc, expectedType, sliceFun, result, CompilerUtils.integerLiteral(loc, 0),
              countExp);
        }
        return result;
      } else if (isReduction) {
        if (countExp != null) {
          IType sliceFunType = TypeUtils.functionType(projectedType, integerType, integerType, projectedType);
          IContentExpression sliceFun = typeOfExp(new Name(loc, StandardNames.SLICE), sliceFunType, dict, outer);

          result = Application.apply(loc, projectedType, sliceFun, result, CompilerUtils.integerLiteral(loc, 0),
              countExp);
        }

        IType reducerType = TypeUtils.functionType(expectedType, expectedType, expectedType);
        IContentExpression reducerFun = typeOfExp(reducer, reducerType, dict, outer);
        IType foldType = TypeUtils.functionType(reducerType, result.getType(), expectedType);
        IContentExpression fold1 = typeOfExp(new Name(loc, StandardNames.LEFTFOLD1), foldType, dict, outer);
        return Application.apply(loc, expectedType, fold1, reducerFun, result);
      } else
        return firstEl(loc, result, expectedType, dict, outer);
    } else if (eliminateDuplicates && isCollection) {
      // The expression we will be comparing against
      IContentExpression transformed = QueryPlanner.transformQuery(loc, free, bound, rewrapSequenceType(collType, bound
          .getType()), queryInfo.left(), dict, outer, errors);
      IType eqType = TypeUtils.functionType(bound.getType(), bound.getType(), booleanType);
      IType uniqueType = new TypeVar();
      IType makeType = TypeUtils.functionType(uniqueType, eqType, expectedType);

      IContentExpression equals = typeOfExp(new Name(loc, StandardNames.EQUAL), eqType, dict, outer);
      IContentExpression unique = typeOfExp(new Name(loc, StandardNames.UNIQUE_F), makeType, dict, outer);
      IContentExpression result = Application.apply(loc, expectedType, unique, transformed, equals);
      if (countExp != null) {
        IType sliceFunType = TypeUtils.functionType(expectedType, integerType, integerType, expectedType);
        IContentExpression sliceFun = typeOfExp(new Name(loc, StandardNames.SLICE), sliceFunType, dict, outer);

        return Application.apply(loc, expectedType, sliceFun, result, CompilerUtils.integerLiteral(loc, 0), countExp);
      } else
        return result;
    } else if (reducer != null) {
      if (eliminateDuplicates) {
        // The expression we will be comparing against
        IType collectionType = TypeUtils.arrayType(expectedType);
        IContentExpression transformed = QueryPlanner.transformQuery(loc, free, bound, collectionType,
            queryInfo.left(), dict, outer, errors);
        IType eqType = TypeUtils.functionType(bound.getType(), bound.getType(), booleanType);
        IType makeType = TypeUtils.functionType(collectionType, eqType, collectionType);

        IContentExpression equals = typeOfExp(new Name(loc, StandardNames.EQUAL), eqType, dict, outer);
        IContentExpression unique = typeOfExp(new Name(loc, StandardNames.UNIQUE_F), makeType, dict, outer);
        IContentExpression result = Application.apply(loc, collectionType, unique, transformed, equals);

        if (countExp != null) {
          IType sliceFunType = TypeUtils.functionType(collectionType, integerType, integerType, collectionType);
          IContentExpression sliceFun = typeOfExp(new Name(loc, StandardNames.SLICE), sliceFunType, dict, outer);

          result = Application.apply(loc, collectionType, sliceFun, result, CompilerUtils.integerLiteral(loc, 0),
              countExp);
        }

        IType reducerType = TypeUtils.functionType(expectedType, expectedType, expectedType);
        IContentExpression reducerFun = typeOfExp(reducer, reducerType, dict, outer);
        IType foldType = TypeUtils.functionType(reducerType, collectionType, expectedType);
        IContentExpression fold1 = typeOfExp(new Name(loc, StandardNames.LEFTFOLD1), foldType, dict, outer);
        return Application.apply(loc, expectedType, fold1, reducerFun, result);
      } else {
        IType reducerType = TypeUtils.functionType(expectedType, expectedType, expectedType);
        IContentExpression reducerFun = typeOfExp(reducer, reducerType, dict, outer);

        IContentExpression code = CompilerUtils.stringLiteral(loc, "error");
        IContentExpression raised = CompilerUtils.stringLiteral(loc, "empty reduction");
        IContentExpression location = Quoter.generateLocation(loc);
        IContentExpression ex = new ConstructorTerm(loc, EvaluationException.name, StandardTypes.exceptionType, code,
            raised, location);
        RaiseExpression deflt = new RaiseExpression(loc, expectedType, ex);

        return QueryPlanner.transformReduction(reducerFun, bound, queryInfo.left(), deflt, free, queryCxt, outer,
            expectedType, loc, errors);
      }
    } else {
      IContentExpression result = QueryPlanner.transformQuery(loc, free, bound, collType, queryInfo.left(), dict,
          outer, errors);
      if (isCollection) {
        if (countExp != null) {
          IType sliceFunType = TypeUtils.functionType(expectedType, integerType, integerType, expectedType);
          IContentExpression sliceFun = typeOfExp(new Name(loc, StandardNames.SLICE), sliceFunType, dict, outer);

          return Application.apply(loc, expectedType, sliceFun, result, CompilerUtils.integerLiteral(loc, 0), countExp);
        } else
          return result;
      } else
        return firstEl(loc, result, expectedType, dict, outer);
    }
  }

  private IContentExpression firstEl(Location loc, IContentExpression result, IType expectedType, Dictionary dict,
      Dictionary outer)
  {
    IType someExpType = TypeUtils.optionType(expectedType);
    IType indexType = TypeUtils.functionType(result.getType(), integerType, someExpType);
    IContentExpression indexFun = typeOfExp(new Name(loc, StandardNames.INDEX), indexType, dict, outer);

    IContentExpression index = CompilerUtils.integerLiteral(loc, 0);
    IContentExpression some = Application.apply(loc, someExpType, indexFun, result, index);

    IType someValueFunType = TypeUtils.functionType(someExpType, expectedType);
    IContentExpression someFun = typeOfExp(new Name(loc, StandardNames.SOMEVALUE), someValueFunType, dict, outer);

    return Application.apply(loc, expectedType, someFun, some);
  }

  private IType rewrapSequenceType(IType orig, IType elType)
  {
    orig = TypeUtils.deRef(orig);
    if (orig instanceof TypeExp)
      return new TypeExp(((TypeExp) orig).getTypeCon(), elType);
    else {
      TypeVar newType = new TypeVar();
      newType.setConstraint(new ContractConstraint(StandardNames.SEQUENCE, newType, TypeUtils.determinedType(elType)));
      return newType;
    }
  }

  private IContentExpression compLambda(Location loc, IType boundType, IType selType, IAbstract compTrm,
      boolean ascending, Dictionary dict, Dictionary outer)
  {
    IType compareType = TypeUtils.functionType(selType, selType, booleanType);
    IContentExpression comp = typeOfExp(compTrm, compareType, dict, outer);

    Variable X = Variable.create(loc, selType, GenSym.genSym("__X"));
    Variable Y = Variable.create(loc, selType, GenSym.genSym("__Y"));
    String compFunName = GenSym.genSym("__compare");

    comp = ascending ? Application.apply(loc, booleanType, comp, X, Y) : Application
        .apply(loc, booleanType, comp, Y, X);

    IContentPattern tpl1 = new ConstructorPtn(loc, Variable.anonymous(loc, boundType), X);
    IContentPattern tpl2 = new ConstructorPtn(loc, Variable.anonymous(loc, boundType), Y);

    selType = TypeUtils.tupleType(boundType, selType);
    IType pairCompType = TypeUtils.functionType(selType, selType, booleanType);

    Triple<IContentPattern[], ICondition, IContentExpression> eqn = Triple.create(new IContentPattern[] { tpl1, tpl2 },
        CompilerUtils.truth, comp);

    Variable[] compFree = FreeVariables.findFreeVars(comp, dict);

    return MatchCompiler.generateFunction(new ArrayList<Triple<IContentPattern[], ICondition, IContentExpression>>(),
        eqn, pairCompType, compFree, compFunName, loc, dict, outer, errors);
  }

  private IContentExpression typeOfMemo(IAbstract term, IType expectedType, Dictionary cxt, Dictionary outer)
  {
    Location loc = term.getLoc();
    IType memoType = new TypeVar();
    try {
      TypeUtils.unify(expectedType, TypeUtils.functionType(memoType), loc, cxt);
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg("type of memo expression: ", memoType, " not consistent with expected type: ",
          expectedType, "\nbecause: ", e.getWords()), merge(loc, e.getLocs()));
    }
    IContentExpression memo = typeOfExp(CompilerUtils.memoedTerm(term), memoType, cxt, outer);
    Variable[] freeVars = FreeVariables.findFreeVars(memo, cxt);
    return new MemoExp(term.getLoc(), memo, freeVars);
  }

  private IContentExpression checkIntegrities(Location loc, String recordLabel, IType type, IContentExpression record,
      Dictionary dict, List<IContentAction> acts)
  {
    String integrityLabel = CompilerUtils.integrityLabel(type.typeLabel(), recordLabel);
    DictInfo info = dict.varReference(integrityLabel);

    if (info != null) {
      Variable vr = Variable.create(loc, type, GenSym.genSym("__"));

      acts.add(new VarDeclaration(loc, vr, readOnly, record));
      IContentExpression checkPr = info.getVariable();
      acts.add(new Ignore(loc, new Application(loc, unitType, checkPr, new ConstructorTerm(loc, vr))));

      acts.add(new ValisAction(loc, vr));
    } else
      acts.add(new ValisAction(loc, record));
    return new ValofExp(loc, type, acts);
  }

  private IContentExpression checkIntegrities(Location loc, String recordLabel, IType type, IContentExpression record,
      Dictionary dict)
  {
    String integrityLabel = CompilerUtils.integrityLabel(type.typeLabel(), recordLabel);
    DictInfo info = dict.varReference(integrityLabel);

    if (info != null) {
      List<IContentAction> acts = new ArrayList<>();
      Variable vr = Variable.create(loc, type, GenSym.genSym("__"));

      acts.add(new VarDeclaration(loc, vr, readOnly, record));
      IContentExpression checkPr = info.getVariable();
      acts.add(new Ignore(loc, new Application(loc, unitType, checkPr, new ConstructorTerm(loc, vr))));

      acts.add(new ValisAction(loc, vr));

      return new ValofExp(loc, type, acts);
    } else
      return record;
  }

  private IContentExpression checkRecordLiteral(IAbstract term, final IType expectedType, Dictionary dict,
      Dictionary outer)
  {
    assert CompilerUtils.isBraceTerm(term);

    final Location loc = term.getLoc();
    IAbstract label = CompilerUtils.braceLabel(term);
    IType funType = new TypeVar();
    IType argType = new TypeVar();

    final IContentExpression fun = typeOfExp(label, funType, dict, outer);

    IType conType = Freshen.freshenForUse(funType);

    IType conArgType;

    try {
      if (TypeUtils.isConstructorType(conType)) {
        Subsume.subsume(conType, TypeUtils.constructorType(argType, expectedType), loc, dict);
        conArgType = TypeUtils.getConstructorArgType(conType);
      } else {
        Subsume.subsume(conType, TypeUtils.funcType(argType, expectedType), loc, dict);
        conArgType = TypeUtils.getFunArgType(conType);
      }
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg(fun, " is not consistent, \nbecause ", e.getWords()), loc);
      return new VoidExp(loc);
    }

    if (conArgType instanceof ExistentialType)
      conArgType = Freshen.freshenForEvidence(conArgType);

    if (!(conArgType instanceof TypeInterfaceType)) {
      errors.reportError(StringUtils.msg("ill-formed record expression ", term), loc);
      return new VoidExp(loc);
    } else {
      final TypeInterfaceType face = (TypeInterfaceType) conArgType;

      final Map<String, ContractConstraint> constraintMap = constraintMap(face);

      Dictionary thetaCxt = dict.fork();

      BoundChecker<IContentExpression> checker = new BoundChecker<IContentExpression>() {

        @Override
        public IContentExpression typeBound(List<IStatement> definitions, List<IContentAction> localActions,
            Over overloader, Dictionary thetaCxt, Dictionary dict)
        {
          SortedMap<String, IContentExpression> elements = new TreeMap<>();

          checkContractImplementations(loc, constraintMap, thetaCxt, elements);

          // We need to look for every element of the record
          checkLoop: for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
            String field = entry.getKey();
            IType fieldType = entry.getValue();

            if (!elements.containsKey(field)) {
              for (IStatement def : definitions) {
                if (def.defines(field)) {
                  elements.put(field, new Variable(loc, entry.getValue(), field));
                  continue checkLoop;
                }
              }

              // Look for default associated with missing field
              if (fun instanceof Variable) {
                Variable recFun = (Variable) fun;
                String rcLabel = recFun.getName();
                String tpLabel = TypeUtils.deRef(expectedType).typeLabel();

                VarInfo deflt = getDefaultFor(dict, tpLabel, rcLabel, field);
                if (deflt != null) {
                  Pair<IContentExpression[], IType[]> nonDefaultsVars = nonDefaultsVars(loc, dict, tpLabel, rcLabel,
                      face);
                  IContentExpression[] nonDefaults = nonDefaultsVars.left;

                  IContentExpression dfltFun = deflt.getVariable().verifyType(loc, errors,
                      TypeUtils.functionType(nonDefaultsVars.right, fieldType), dict, false);

                  IContentExpression defltVal = Application.apply(loc, fieldType, dfltFun, nonDefaults);
                  definitions.add(new VarEntry(loc, Variable.create(loc, fieldType, field), defltVal, readOnly,
                      Visibility.pUblic));
                  elements.put(field, new Variable(loc, entry.getValue(), field));
                } else
                  errors.reportError(StringUtils.msg(field, " not defined within record"), loc);
              }
            }
          }

          // Verify that public definitions are safe
          for (IStatement stmt : definitions) {
            if (stmt.getVisibility() == Visibility.pUblic) {
              if (stmt instanceof VarEntry) {
                VarEntry var = (VarEntry) stmt;
                if (TypeUtils.isRawType(var.getType())) {
                  if (!TypeUtils.isRawType(face.getFieldType(var.getVariable().getName())))
                    isRawThetaType(var, errors);
                }
              }
            }
          }

          RecordTerm record = new RecordTerm(loc, expectedType, fun, elements, face.getAllTypes());

          if (!localActions.isEmpty()) {
            localActions.add(new ValisAction(loc, record));
            return new LetTerm(loc, new ValofExp(loc, expectedType, new Sequence(loc, TypeUtils.typeExp(actionType,
                expectedType), localActions)), definitions);
          } else
            return new LetTerm(loc, record, definitions);
        }
      };

      return checkTheta(CompilerUtils.recordContent(term), thetaCxt, dict, face, checker);
    }
  }

  private static Map<String, ContractConstraint> constraintMap(TypeInterfaceType face)
  {
    Map<String, ContractConstraint> conMap = new HashMap<>();
    Map<IType, IType> txMap = new HashMap<>();

    for (Entry<String, IType> e : face.getAllTypes().entrySet())
      txMap.put(e.getValue(), new Type(e.getKey(), e.getValue().kind()));

    TypeSubstitute txTrans = new TypeSubstitute(txMap);

    for (ITypeConstraint c : ConstraintFinder.findConstraints(face)) {
      if (c instanceof ContractConstraint) {
        ContractConstraint con = (ContractConstraint) c;
        TypeExp contract = con.getContract();

        IType exConType = contract.transform(txTrans, null);
        if (!exConType.equals(contract)) {
          String instanceName = Over.instanceFunName(exConType);

          conMap.put(instanceName, con);
        }
      }
    }

    return conMap;
  }

  private IContentExpression checkThetaLiteral(IAbstract term, final Dictionary dict, final IType expectedType)
  {
    Dictionary thetaCxt = dict.fork();
    final Location loc = term.getLoc();

    final IType evidenceType = Freshen.freshenForEvidence(expectedType);

    TypeInterfaceType face = (TypeInterfaceType) TypeUtils.interfaceOfType(loc, evidenceType, dict);
    final Map<String, ContractConstraint> constraintMap = constraintMap(face);

    BoundChecker<IContentExpression> checker = new BoundChecker<IContentExpression>() {

      @Override
      public IContentExpression typeBound(List<IStatement> definitions, List<IContentAction> localActions,
          Over overloader, Dictionary thetaCxt, Dictionary dict)
      {
        SortedMap<String, IContentExpression> elements = new TreeMap<>();
        checkContractImplementations(loc, constraintMap, thetaCxt, elements);
        sealInterface(evidenceType, elements, definitions, thetaCxt, errors, loc);

        TypeInterfaceType face = (TypeInterfaceType) TypeUtils.unwrap(TypeUtils
            .interfaceOfType(loc, evidenceType, dict));

        // We need to look for every element of the record
        SortedMap<String, IType> fieldTypes = face.getAllFields();

        checkLoop: for (Entry<String, IType> entry : fieldTypes.entrySet()) {
          String field = entry.getKey();
          if (!elements.containsKey(field)) {
            elements.put(field, new Variable(loc, entry.getValue(), field));

            for (IStatement def : definitions) {
              if (def.defines(field))
                continue checkLoop;
            }
            errors.reportError(StringUtils.msg("no definition found for ", field, " within record"), loc);
          }
        }

        // Verify that definitions are private or exported
        for (IStatement stmt : definitions) {
          if (stmt.getVisibility() == Visibility.pUblic) {
            for (String def : stmt.definedFields())
              if (face.getFieldType(def) == null)
                errors.reportWarning(StringUtils.msg(def, " not part of record type: ", face,
                    " should be marked private"), stmt.getLoc());
            if (stmt instanceof VarEntry)
              isRawThetaType((VarEntry) stmt, errors);
          }
        }

        IType sealed = Freshen.existentializeType(face, thetaCxt);

        try {
          Subsume.subsume(expectedType, sealed, loc, thetaCxt);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("cannot reconcile ", face, " with expected type ", expectedType), loc);
          return new VoidExp(loc, expectedType);
        }

        IContentExpression record = RecordTerm.anonRecord(loc, expectedType, elements, face.getAllTypes());

        if (!localActions.isEmpty()) {
          localActions.add(new ValisAction(loc, record));
          return new LetTerm(loc, new ValofExp(loc, sealed, new Sequence(loc, TypeUtils.typeExp(actionType, sealed),
              localActions)), definitions);
        } else
          return new LetTerm(loc, record, definitions);
      }
    };

    return checkTheta(CompilerUtils.blockContent(term), thetaCxt, dict, evidenceType, checker);
  }

  private static IType fieldType(Location loc, IType rType, String field, IType expectedType, Dictionary dict,
      ErrorReport errors)
  {
    rType = TypeUtils.deRef(rType);

    if (rType instanceof TypeInterfaceType) {
      TypeInterfaceType face = (TypeInterfaceType) rType;
      IType elType = face.getFieldType(field);
      if (elType == null) {
        errors.reportError(StringUtils.msg(rType, " not known to have field ", field), loc);
        return expectedType;
      }
      try {
        Subsume.same(elType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("type of ", field, ":", elType,
            " not consistent with expected type,\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return elType;
    } else if (rType instanceof TypeVar) {
      try {
        TypeUtils.addFieldConstraint((TypeVar) rType, loc, field, expectedType, dict, false);
        return expectedType;
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("cannot verify consistency of ", rType, "\nbecause ", e.getWords()), merge(
            loc, e.getLocs()));
        return null;
      }
    } else {
      IType attType = TypeUtils.getAttributeType(dict, rType, field, false);

      if (attType == null) {
        errors.reportError(StringUtils.msg("'", rType, "' not known to have field '", field, "'"), loc);
        return new TypeVar();
      }

      attType = Freshen.freshenForUse(attType);

      try {
        TypeUtils.unify(attType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("type of ", field, ":", attType,
            " not consistent with expected type,\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return attType;
    }
  }

  private static IType memberType(Location loc, IType rType, String member, IType expectedType, Dictionary dict,
      ErrorReport errors)
  {
    rType = TypeUtils.deRef(rType);

    if (rType instanceof TypeInterfaceType) {
      TypeInterfaceType face = (TypeInterfaceType) rType;
      IType elType = face.getType(member);
      if (elType == null) {
        errors.reportError(StringUtils.msg(rType, " not known to have type ", member), loc);
        return expectedType;
      }
      try {
        Subsume.same(elType, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("type of ", member, ":", elType,
            " not consistent with expected type,\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return elType;
    } else if (rType instanceof TypeVar) {
      try {
        TypeUtils.addTypeConstraint((TypeVar) rType, loc, member, expectedType, dict, false);
        return expectedType;
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("cannot verify consistency of ", rType, "\nbecause ", e.getWords()), merge(
            loc, e.getLocs()));
        return null;
      }
    } else {
      IType type = TypeUtils.getFieldTypeMember(dict, rType, member, false);

      if (type == null) {
        errors.reportError(StringUtils.msg("'", rType, "' not known to have type '", member, "'"), loc);
        return new TypeVar();
      }

      type = Freshen.freshenForUse(type);

      try {
        TypeUtils.unify(type, expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("type of ", member, ":", type,
            " not consistent with expected type,\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return type;
    }
  }

  private static IContentExpression fieldOfRecord(Location loc, IContentExpression record, String field,
      IType expectedType, Dictionary dict, ErrorReport errors)
  {
    IType rType = TypeUtils.deRef(record.getType());
    IType fieldType;

    if (TypeUtils.isTypeInterface(rType)) {
      fieldType = TypeUtils.getInterfaceField(rType, field);

      if (fieldType == null) {
        errors.reportError(StringUtils.msg(record, " not known to have field ", field), loc);
        return new Variable(loc, expectedType, field);
      }
    } else if (record instanceof Variable) {
      DictInfo info = dict.getVar(((Variable) record).getName());

      if (info instanceof VarInfo) {
        fieldType = ((VarInfo) info).typeOfField(loc, field, dict, errors);
        if (fieldType == null) {
          errors.reportError(StringUtils.msg(record, " not known to have field '", field, "'"), loc);
          return new Variable(loc, expectedType, field);
        }
      } else {
        errors.reportError(StringUtils.msg(record, " not known to have field ", field), loc);
        return new Variable(loc, expectedType, field);
      }
    } else if (rType instanceof TypeVar) {
      try {
        fieldType = expectedType;
        TypeUtils.addFieldConstraint((TypeVar) rType, loc, field, fieldType, dict, false);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("cannot verify consistency of ", rType, "\nbecause ", e.getWords()), merge(
            loc, e.getLocs()));
        return new Variable(loc, expectedType, field);
      }
    } else {
      fieldType = TypeUtils.getAttributeType(dict, rType, field, false);

      if (fieldType == null) {
        errors.reportError(StringUtils.msg("'", rType, "' not known to have field '", field, "'"), loc);
        return new Variable(loc, expectedType, field);
      }
      fieldType = Freshen.freshen(fieldType, readOnly, readWrite).left();
    }

    final IContentExpression access;
    if (TypeUtils.hasContractDependencies(expectedType)) {
      access = FieldAccess.create(loc, fieldType, record, field);
    } else if (TypeUtils.hasContractDependencies(fieldType)) {
      IType overType = TypeUtils.refreshOverloaded(Over.computeDictionaryType(fieldType, loc, readWrite));

      access = new OverloadedFieldAccess(loc, TypeUtils.getOverloadedType(overType), overType, record, field);
    } else if (TypeUtils.isUniversalType(fieldType))
      access = FieldAccess.create(loc, Freshen.freshenForUse(fieldType), record, field);
    else if (TypeUtils.isReferenceType(fieldType) && !TypeUtils.isReferenceType(expectedType))
      access = new Shriek(loc, FieldAccess.create(loc, fieldType, record, field));
    else
      access = FieldAccess.create(loc, fieldType, record, field);

    return verifyType(access.getType(), expectedType, loc, access, dict, errors);
  }

  private IContentExpression typeOfProcedure(String name, IAbstract proc, IType programType, Dictionary cxt)
  {
    List<Triple<IContentPattern[], ICondition, IContentExpression>> rules = new ArrayList<>();
    Triple<IContentPattern[], ICondition, IContentExpression> deflt = null;
    Location loc = proc.getLoc();
    Dictionary prcCxt = cxt.fork();
    final IType argTypes[];
    int arity = arityOfProcedure(proc);

    // We cannot just use freshenForEvidence because we have to be careful about keeping type
    // information intact

    if (TypeUtils.isProcedureType(programType = TypeUtils.deRef(programType))) {
      Map<String, TypeVar> funTypeVars = new HashMap<>();
      TypeExp funCon = (TypeExp) TypeUtils.unwrap(programType, funTypeVars);
      argTypes = TypeUtils.getFunArgTypes(funCon);
      for (Entry<String, TypeVar> entry : funTypeVars.entrySet()) {
        TypeVar tv = entry.getValue();
        prcCxt.defineType(new TypeExists(loc, tv.getVarName(), tv));
      }
    } else {
      argTypes = new IType[arity];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();
      try {
        Subsume.subsume(programType, TypeUtils.procedureType(argTypes), loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("expected type ", programType, " of ", name, " is not a procedure type"),
            loc);
      }
    }

    for (IAbstract rule : CompilerUtils.unWrap(proc, StandardNames.FATBAR)) {
      Location eqnLoc = rule.getLoc();

      if (Abstract.isBinary(rule, StandardNames.DO)) {
        Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
        Dictionary eqCxt = prcCxt.fork();

        IAbstract lhs = Abstract.binaryLhs(rule);
        IAbstract rhs = Abstract.binaryRhs(rule);
        IAbstract cond = null;

        boolean isDefault = false;
        if (Abstract.isUnary(lhs, StandardNames.DEFAULT)) {
          isDefault = true;
          lhs = Abstract.unaryArg(lhs);
        }

        if (Abstract.isBinary(lhs, StandardNames.WHERE)) {
          cond = Abstract.getArg(lhs, 1);
          lhs = Abstract.getArg(lhs, 0);
        }

        if (lhs instanceof Apply) {
          IList argTuple = ((Apply) lhs).getArgs();
          IContentPattern args[] = new IContentPattern[argTuple.size()];

          if (argTuple.size() != argTypes.length)
            errors.reportError(StringUtils.msg("arity of actual rule: ", argTuple.size(),
                " not consistent with expected arity: ", argTypes.length), eqnLoc);
          else {
            ptnTypeTpl(argTuple, argTypes, args, condition, eqCxt, cxt, new RuleVarHandler(cxt, errors));
            if (cond != null) {
              Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(cond, eqCxt, cxt);
              CompilerUtils.extendCondition(condition, condInfo.left());
            }

            List<IContentAction> bodyActions = new ArrayList<>();
            bodyActions.addAll(checkAction(rhs, actionType, unitType, eqCxt, cxt));
            bodyActions.add(new ValisAction(eqnLoc, new VoidExp(eqnLoc)));
            IContentExpression body = new ValofExp(eqnLoc, unitType, bodyActions);

            if (isDefault) {
              if (deflt == null)
                deflt = Triple.create(args, condition.get(), body);
              else
                errors.reportError(StringUtils.msg("already have a default action rule"), eqnLoc);
            } else
              rules.add(Triple.create(args, condition.get(), body));
          }
        } else
          errors.reportError(StringUtils.msg(lhs, " is not a valid head of an action rule"), eqnLoc);
      } else
        errors.reportError(StringUtils.msg(rule, " is not a valid action rule"), eqnLoc);
    }

    if (deflt == null) {
      IContentPattern[] args = new IContentPattern[argTypes.length];
      for (int ix = 0; ix < args.length; ix++)
        args[ix] = Variable.anonymous(loc, new TypeVar());
      deflt = Triple.create(args, CompilerUtils.truth, (IContentExpression) new VoidExp(loc));
    }

    return MatchCompiler.generateFunction(rules, deflt, programType, prcCxt.getFreeVars(), name, loc, cxt, cxt, errors);
  }

  private IContentExpression typeOfFunction(IAbstract fun, IType programType, Dictionary cxt)
  {
    String name = Abstract.getId(CompilerUtils.functionName(fun));
    List<Triple<IContentPattern[], ICondition, IContentExpression>> equations = new ArrayList<>();
    Triple<IContentPattern[], ICondition, IContentExpression> deflt = null;
    Location loc = fun.getLoc();
    Dictionary funCxt = cxt.fork();
    IType argTypes[];
    IType funResType;
    Map<String, TypeVar> funTypeVars = new HashMap<>();

    if (TypeUtils.isOverloadedType(programType)) {
      programType = TypeUtils.refreshOverloaded(programType);
    }

    if (TypeUtils.isFunType(programType = TypeUtils.deRef(programType))) {
      TypeExp funCon = (TypeExp) TypeUtils.unwrap(programType, funTypeVars);
      argTypes = TypeUtils.getFunArgTypes(funCon);
      funResType = TypeUtils.getFunResultType(funCon);
      for (Entry<String, TypeVar> entry : funTypeVars.entrySet()) {
        TypeVar tv = entry.getValue();
        funCxt.defineType(new TypeExists(loc, tv.getVarName(), tv));
      }
    } else if (programType instanceof TypeVar) {
      int funArity = arityOfFunction(fun);
      argTypes = new IType[funArity];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();
      funResType = new TypeVar();
      try {
        TypeUtils.unify(programType, TypeUtils.functionType(argTypes, funResType), loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("(internal) problem in computing type"), loc);
      }
    } else {
      errors.reportError(StringUtils.msg("expected type ", programType, " of ", name, " is not a function type"), loc);
      return new VoidExp(loc);
    }

    int errorCount = errors.errorCount();
    int eqnCount = CompilerUtils.count(fun, StandardNames.FATBAR);

    for (IAbstract eqn : CompilerUtils.unWrap(fun, StandardNames.FATBAR)) {
      Location eqnLoc = eqn.getLoc();

      if (Abstract.isBinary(eqn, StandardNames.IS)) {
        Wrapper<ICondition> condition = new Wrapper<>(CompilerUtils.truth);
        Dictionary eqCxt = funCxt.fork();

        IAbstract lhs = Abstract.binaryLhs(eqn);
        IAbstract rhs = Abstract.binaryRhs(eqn);
        IAbstract cond = null;

        boolean isDefault = eqnCount == 1;
        if (Abstract.isUnary(lhs, StandardNames.DEFAULT)) {
          isDefault = true;
          lhs = Abstract.unaryArg(lhs);
        }

        if (Abstract.isBinary(lhs, StandardNames.WHERE)) {
          cond = Abstract.getArg(lhs, 1);
          lhs = Abstract.getArg(lhs, 0);
        }

        IList argTuple = ((Apply) lhs).getArgs();

        IContentPattern args[] = new IContentPattern[argTuple.size()];

        if (argTuple.size() != argTypes.length)
          errors.reportError(StringUtils.msg("arity of actual equation: ", argTuple.size(),
              " not consistent with expected arity: ", argTypes.length), eqnLoc);
        else {
          ptnTypeTpl(argTuple, argTypes, args, condition, eqCxt, cxt, new RuleVarHandler(cxt, errors));
          if (cond != null) {
            Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(cond, eqCxt, cxt);
            CompilerUtils.extendCondition(condition, condInfo.left());
          }

          IContentExpression result = typeOfExp(rhs, funResType, eqCxt, cxt);
          ICondition predicate = condition.get();

          if (isDefault) {
            if (deflt == null)
              deflt = Triple.create(args, predicate, result);
            else
              errors.reportError(StringUtils.msg("already have a default equation"), eqnLoc);
          } else
            equations.add(Triple.create(args, predicate, result));
        }
      } else
        errors.reportError(StringUtils.msg("looking for an equation"), eqnLoc);
    }

    if (errors.noNewErrors(errorCount))
      return MatchCompiler.generateFunction(equations, deflt, programType, funCxt.getFreeVars(), name, loc, cxt, cxt,
          errors);
    else
      return new VoidExp(loc);
  }

  private IContentExpression typeOfLambda(IAbstract fun, IType programType, Dictionary cxt)
  {
    assert CompilerUtils.isLambdaExp(fun);

    List<Triple<IContentPattern[], ICondition, IContentExpression>> equations = new ArrayList<>();
    Triple<IContentPattern[], ICondition, IContentExpression> deflt = null;
    Location loc = fun.getLoc();
    Dictionary funCxt = cxt.fork();
    IType argTypes[];
    IType funResType;
    Map<String, TypeVar> funTypeVars = new HashMap<>();

    IAbstract lhs = CompilerUtils.lambdaPtn(fun);
    IAbstract rhs = CompilerUtils.lambdaExp(fun);
    int arity = CompilerUtils.lambdaArity(fun);

    if (TypeUtils.isOverloadedType(programType)) {
      programType = TypeUtils.refreshOverloaded(programType);
    }

    if (TypeUtils.isFunType(programType = TypeUtils.deRef(programType))) {
      TypeExp funCon = (TypeExp) TypeUtils.unwrap(programType, funTypeVars);
      argTypes = TypeUtils.getFunArgTypes(funCon);
      funResType = TypeUtils.getFunResultType(funCon);
      for (Entry<String, TypeVar> entry : funTypeVars.entrySet()) {
        TypeVar tv = entry.getValue();
        funCxt.defineType(new TypeExists(loc, tv.getVarName(), tv));
      }
    } else if (programType instanceof TypeVar) {
      argTypes = new IType[arity];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();
      funResType = new TypeVar();
      try {
        TypeUtils.unify(programType, TypeUtils.functionType(argTypes, funResType), loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("invalid function type: ", programType, "\nbecause ", e.getWords()), loc);
        programType = TypeUtils.functionType(argTypes, funResType);
      }
    } else {
      errors.reportError(StringUtils.msg("expected type ", programType, " of ", fun, " is not a function type"), loc);
      return new VoidExp(loc);
    }

    int errorCount = errors.errorCount();

    Wrapper<ICondition> condition = new Wrapper<>(CompilerUtils.truth);

    IAbstract cond = null;

    if (Abstract.isBinary(lhs, StandardNames.WHERE)) {
      cond = Abstract.getArg(lhs, 1);
      lhs = Abstract.getArg(lhs, 0);
    }

    IContentPattern args[] = new IContentPattern[arity];

    if (arity != argTypes.length)
      errors.reportError(StringUtils.msg("arity of actual function: ", arity, " not consistent with expected arity: ",
          argTypes.length), loc);
    else {
      RuleVarHandler varHandler = new RuleVarHandler(cxt, errors);
      if (Abstract.isTupleTerm(lhs))
        ptnTypeTpl(Abstract.tupleArgs(lhs), argTypes, args, condition, funCxt, cxt, varHandler);
      else {
        assert arity == 1;
        args[0] = typeOfPtn(lhs, argTypes[0], condition, funCxt, cxt, varHandler);
      }
      if (cond != null) {
        Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(cond, funCxt, cxt);
        CompilerUtils.extendCondition(condition, condInfo.left());
      }

      IContentExpression result = typeOfExp(rhs, funResType, funCxt, cxt);
      ICondition predicate = condition.get();

      equations.add(Triple.create(args, predicate, result));
    }

    if (errors.noNewErrors(errorCount))
      return MatchCompiler.generateFunction(equations, deflt, programType, funCxt.getFreeVars(), GenSym
          .genSym(StandardNames.LAMBDA), loc, cxt, cxt, errors);
    else
      return new VoidExp(loc);
  }

  private IContentExpression typeOfPtnAbstraction(String name, IAbstract ptn, IType programType, Dictionary cxt)
  {
    List<Triple<IContentPattern[], ICondition, IContentExpression>> rules = new ArrayList<>();
    Location loc = ptn.getLoc();
    Dictionary funCxt = cxt.fork();
    final IType resultType, matchType;
    int arity = arityOfPattern(ptn);

    if (TypeUtils.isPatternType(programType)) {
      List<TypeVar> funTypeVars = new ArrayList<>();
      TypeExp funCon = (TypeExp) TypeUtils.unwrap(programType, funTypeVars);
      for (TypeVar tv : funTypeVars)
        funCxt.defineType(new TypeExists(loc, tv.getVarName(), tv));
      resultType = TypeUtils.deRef(TypeUtils.getTypeArg(funCon,0));
      matchType = TypeUtils.getTypeArg(funCon,1);
    } else if (programType instanceof TypeVar) {
      IType[] argTypes = new IType[arity];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();

      resultType = TypeUtils.tupleType(argTypes);
      matchType = new TypeVar();
      IType patternType = TypeUtils.patternType(resultType, matchType);
      try {
        TypeUtils.unify(patternType, programType, loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(patternType, " not consistent with expected type: ", programType), loc);
      }
    } else {
      errors.reportError(StringUtils.msg("expected type ", programType, " of ", name, " is not a pattern type"), loc);
      resultType = new TypeVar();
      matchType = new TypeVar();
    }

    if (errors.isErrorFree()) {
      for (IAbstract rl : CompilerUtils.unWrap(ptn, StandardNames.FATBAR)) {
        Location ruleLoc = rl.getLoc();

        IAbstract head = CompilerUtils.patternRuleHead(rl);
        IAbstract body = CompilerUtils.patternRuleBody(rl);

        Wrapper<ICondition> condition = new Wrapper<>((ICondition) new TrueCondition(loc));
        Dictionary eqCxt = funCxt.fork();

        IContentPattern match = typeOfPtn(body, matchType, condition, eqCxt, cxt, new RuleVarHandler(cxt, errors));

        IList argTuple = ((Apply) head).getArgs();
        List<IType> resTypes = new ArrayList<>();
        List<IContentExpression> results = new ArrayList<>();

        for (IValue arg : argTuple) {
          IType argType = new TypeVar();
          resTypes.add(argType);
          results.add(typeOfExp((IAbstract) arg, argType, eqCxt, cxt));
        }

        IType resType = TypeUtils.tupleType(resTypes);

        try {
          TypeUtils.unify(resultType, resType, ruleLoc, eqCxt);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("result type: ", resultType, " not consistent with types ", resType,
              "\nbecause ", e.getWords()), merge(ruleLoc, e.getLocs()));
        }

        rules.add(Triple.create(new IContentPattern[] { match }, condition.get(),
            (IContentExpression) new ConstructorTerm(ruleLoc, results)));
      }
    }

    if (errors.isErrorFree()) {
      return MatchCompiler.compileMatch(loc, name, rules, programType, funCxt.getFreeVars(), cxt, cxt, errors);
    }
    return new VoidExp(loc);
  }

  private static int arityOfFunction(IAbstract fun)
  {
    if (CompilerUtils.isPrivate(fun))
      return arityOfFunction(CompilerUtils.privateTerm(fun));
    else if (Abstract.isBinary(fun, StandardNames.FATBAR))
      return arityOfFunction(Abstract.getArg(fun, 0));
    else if (Abstract.isBinary(fun, StandardNames.IS)) {
      IAbstract lhs = Abstract.getArg(fun, 0);
      if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.unaryArg(lhs);

      if (Abstract.isBinary(lhs, StandardNames.WHERE))
        lhs = Abstract.getArg(lhs, 0);
      if (lhs instanceof Apply) {
        return (((Apply) lhs).getArgs()).size();
      }
    }
    throw new IllegalArgumentException("invalid form of function to compute arity of");
  }

  private int arityOfProcedure(IAbstract proc)
  {
    if (CompilerUtils.isPrivate(proc))
      return arityOfProcedure(CompilerUtils.privateTerm(proc));
    else if (Abstract.isBinary(proc, StandardNames.FATBAR))
      return arityOfProcedure(Abstract.getArg(proc, 0));
    else if (Abstract.isUnary(proc, StandardNames.FATBAR))
      return arityOfProcedure(Abstract.unaryArg(proc));
    else if (Abstract.isBinary(proc, StandardNames.DO)) {
      IAbstract lhs = Abstract.getArg(proc, 0);
      if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.unaryArg(lhs);

      if (Abstract.isBinary(lhs, StandardNames.WHERE))

        lhs = Abstract.getArg(lhs, 0);
      if (lhs instanceof Apply)
        return ((Apply) lhs).getArgs().size();
    } else if (CompilerUtils.isBraceTerm(proc) && CompilerUtils.braceLabel(proc) instanceof Apply)
      return Abstract.arity(CompilerUtils.braceLabel(proc));

    errors.reportError(StringUtils.msg("cannot determine arity of invalid procedure: ", proc), proc.getLoc());
    return 0;
  }

  private int arityOfPattern(IAbstract stmt)
  {
    if (CompilerUtils.isPrivate(stmt))
      return arityOfPattern(CompilerUtils.privateTerm(stmt));
    else if (Abstract.isBinary(stmt, StandardNames.FATBAR))
      return arityOfPattern(Abstract.getArg(stmt, 0));
    else if (Abstract.isUnary(stmt, StandardNames.FATBAR))
      return arityOfPattern(Abstract.unaryArg(stmt));
    else {
      IAbstract lhs = CompilerUtils.patternRuleHead(stmt);
      if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.unaryArg(lhs);

      if (Abstract.isBinary(lhs, StandardNames.WHERE))
        lhs = Abstract.getArg(lhs, 0);
      if (lhs instanceof Apply)
        return ((Apply) lhs).getArgs().size();
    }

    errors.reportError(StringUtils.msg("cannot determine arity of invalid pattern: ", stmt), stmt.getLoc());
    return 0;
  }

  /**
   * Check a condition
   * 
   * @param condition
   *          the checked condition
   * @param cxt
   *          the dictionary
   * @param outer
   *          the outer scope dictionary
   * @return a triple of the condition, the free variables and the variables defined by this
   *         condition
   */
  private Triple<ICondition, List<Variable>, List<Variable>> typeOfCondition(IAbstract condition, Dictionary cxt,
      Dictionary outer)
  {
    final List<Variable> vars = new ArrayList<>();

    CheckVar handler = new VarCheck(vars);
    findVarsInCondition(condition, cxt, outer, handler);
    ICondition cond = typeOfCond(condition, cxt, outer, new VarHandler(readOnly, Visibility.priVate));

    List<Variable> definedVars = new ArrayList<>();
    for (Variable v : vars) {
      DictInfo vInfo = cxt.getVar(v.getName());
      if (vInfo instanceof VarInfo) {
        Variable access = vInfo.getVariable();
        if (access != null)
          definedVars.add(access);
      }
    }

    List<Variable> free = FreeVariables.freeVars(cond, cxt);
    free.removeAll(definedVars);
    cond = FlowAnalysis.analyseFlow(cond, free, new DictionaryChecker(cxt, handler.getVars()));

    return Triple.create(cond, free, definedVars);
  }

  private interface CheckVar
  {
    void checkVariable(Variable var, Dictionary cxt, Dictionary outer);

    List<Variable> getVars();

    CheckVar fork();
  }

  private class VarCheck implements CheckVar
  {
    final List<Variable> vars;

    VarCheck(List<Variable> vars)
    {
      this.vars = vars;
    }

    @Override
    public void checkVariable(Variable var, Dictionary cxt, Dictionary outer)
    {
      String name = var.getName();
      for (Variable v : vars) {
        if (v.getName().equals(name))
          return;
      }
      if (!cxt.isDefinedVar(name) && !outer.isDefinedVar(name)) {
        vars.add(var);
      }
    }

    @Override
    public CheckVar fork()
    {
      return new VarCheck(new ArrayList<>(vars));
    }

    @Override
    public List<Variable> getVars()
    {
      return vars;
    }
  }

  private void findVarsInCondition(IAbstract condition, Dictionary cxt, Dictionary outer, CheckVar varFinder)
  {
    if (Abstract.isParenTerm(condition))
      findVarsInCondition(Abstract.deParen(condition), cxt, outer, varFinder);
    else if (Abstract.isBinary(condition, StandardNames.AND)) {
      findVarsInCondition(Abstract.binaryLhs(condition), cxt, outer, varFinder);
      findVarsInCondition(Abstract.binaryRhs(condition), cxt, outer, varFinder);
    } else if (Abstract.isBinary(condition, StandardNames.OTHERWISE) || Abstract.isBinary(condition, StandardNames.OR)) {
      List<Variable> vars = varFinder.getVars();
      List<Variable> lVars = new ArrayList<>(vars);
      List<Variable> rVars = new ArrayList<>(vars);

      VarCheck lCheck = new VarCheck(lVars);
      IAbstract lhs = Abstract.binaryLhs(condition);
      findVarsInCondition(lhs, cxt, outer, lCheck);

      IAbstract rhs = Abstract.binaryRhs(condition);
      VarCheck rCheck = new VarCheck(rVars);
      findVarsInCondition(rhs, cxt, outer, rCheck);

      for (Variable v : lVars) {
        if (rVars.contains(v))
          varFinder.checkVariable(v, cxt, outer);
      }
    } else if (CompilerUtils.isConditional(condition)) {
      IAbstract then = CompilerUtils.conditionalThen(condition);
      IAbstract els = CompilerUtils.conditionalElse(condition);

      List<Variable> vars = varFinder.getVars();
      List<Variable> lVars = new ArrayList<>(vars);
      List<Variable> rVars = new ArrayList<>(vars);

      VarCheck lCheck = new VarCheck(lVars);
      findVarsInCondition(then, cxt, outer, lCheck);

      VarCheck rCheck = new VarCheck(rVars);
      findVarsInCondition(els, cxt, outer, rCheck);

      for (Variable v : lVars) {
        if (rVars.contains(v))
          varFinder.checkVariable(v, cxt, outer);
      }
    } else if (Abstract.isBinary(condition, StandardNames.IN)
        && Abstract.isBinary(Abstract.getArg(condition, 0), StandardNames.MAP_ARROW)) {
      findVarsInPtn(Abstract.argPath(condition, 0, 0), cxt, outer, varFinder);
      findVarsInPtn(Abstract.argPath(condition, 0, 1), cxt, outer, varFinder);
    } else if (Abstract.isBinary(condition, StandardNames.IN)
        && CompilerUtils.isIndexPattern(Abstract.binaryLhs(condition))) {
      IAbstract lhs = Abstract.binaryLhs(condition);
      findVarsInPtn(CompilerUtils.indexPttrnPtn(lhs), cxt, outer, varFinder);
      findVarsInPtn(CompilerUtils.indexPttrnIx(lhs), cxt, outer, varFinder);
    } else if (Abstract.isBinary(condition, StandardNames.DOWN)
        && CompilerUtils.isIndexPattern(Abstract.binaryLhs(condition))) {
      IAbstract lhs = Abstract.binaryLhs(condition);
      findVarsInPtn(CompilerUtils.indexPttrnPtn(lhs), cxt, outer, varFinder);
      findVarsInPtn(CompilerUtils.indexPttrnIx(lhs), cxt, outer, varFinder);
    } else if (Abstract.isBinary(condition, StandardNames.IN)) {
      findVarsInPtn(Abstract.getArg(condition, 0), cxt, outer, varFinder);
    } else if (CompilerUtils.isBoundTo(condition)) {
      findVarsInPtn(CompilerUtils.boundToPtn(condition), cxt, outer, varFinder);
    }
  }

  private void findVarsInPtn(IAbstract ptn, Dictionary cxt, Dictionary outer, CheckVar varFinder)
  {
    final Location loc = ptn.getLoc();

    if (CompilerUtils.isIdentifier(ptn)) {
      final String vrName = Abstract.getId(ptn);

      if (!(cxt.isConstructor(vrName) || vrName.equals(StandardNames.ANONYMOUS) || vrName.equals(StandardNames.TRUE)
          || vrName.equals(StandardNames.FALSE) || vrName.equals(StandardNames.BRACES) || cxt.isDeclaredVar(vrName))) {
        if (StandardNames.isKeyword(ptn))
          errors.reportError(StringUtils.msg("unexpected keyword"), loc);
        else
          varFinder.checkVariable(new Variable(loc, new TypeVar(), vrName), cxt, outer);
      }
    } else if (CompilerUtils.isRegexp(ptn)) {
      String text = CompilerUtils.regexpExp(ptn);

      // Look for regexp meta characters
      if (StringUtils.containsAny(text, "[]*+?.:()|{}\\\"$")) {
        for (Sequencer<Integer> it = new StringSequence(text); it.hasNext();) {
          int ch = it.next();
          switch (ch) {
          case ':': {
            StringBuilder var = new StringBuilder();
            while (it.hasNext() && Tokenizer.isIdentifierChar(ch = it.next()))
              var.appendCodePoint(ch);
            String vrName = var.toString();
            varFinder.checkVariable(new Variable(loc, StandardTypes.stringType, vrName), cxt, outer);
            continue;
          }
          case Tokenizer.QUOTE:
            it.next();
            continue;
          default:
          }
        }
      }
    } else if (CompilerUtils.isAnonAggConLiteral(ptn)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.anonAggEls(ptn), StandardNames.TERM)) {
        if (Abstract.isBinary(el, StandardNames.EQUAL) || Abstract.isBinary(el, StandardNames.ASSIGN)) {
          if (CompilerUtils.isIdentifier(Abstract.getArg(el, 0)))
            findVarsInPtn(Abstract.getArg(el, 1), cxt, outer, varFinder);
        } else
          errors.reportError(StringUtils.msg("invalid member of aggregate value: ", el), el.getLoc());
      }
    } else if (Abstract.isBinary(ptn, StandardNames.OF) && Abstract.isName(Abstract.binaryLhs(ptn))
        && CompilerUtils.isBlockTerm(Abstract.binaryRhs(ptn))) {

      IAbstract braceArg = CompilerUtils.blockContent(Abstract.binaryRhs(ptn));

      for (IAbstract el : CompilerUtils.unWrap(braceArg)) {
        if (Abstract.isBinary(el, StandardNames.CONS)) {
          findVarsInPtn(Abstract.getArg(el, 0), cxt, outer, varFinder);
          findVarsInPtn(Abstract.getArg(el, 1), cxt, outer, varFinder);
        } else
          findVarsInPtn(el, cxt, outer, varFinder);
      }
    } else if (CompilerUtils.isBraceTerm(ptn)) {
      IAbstract arg = CompilerUtils.braceArg(ptn);

      if (arg != null) {
        for (IAbstract agEl : CompilerUtils.unWrap(arg)) {
          if (Abstract.isBinary(agEl, StandardNames.EQUAL))
            findVarsInPtn(Abstract.getArg(agEl, 1), cxt, outer, varFinder);
          else
            errors.reportError(StringUtils.msg(agEl, " is not a valid element of an aggregate"), agEl.getLoc());
        }
      }
    } else if (Abstract.isParenTerm(ptn))
      findVarsInPtn(Abstract.deParen(ptn), cxt, outer, varFinder);
    else if (Abstract.isBinary(ptn, StandardNames.MATCHING)) {
      findVarsInPtn(Abstract.getArg(ptn, 0), cxt, outer, varFinder);
      findVarsInPtn(Abstract.getArg(ptn, 1), cxt, outer, varFinder);
    } else if (Abstract.isBinary(ptn, StandardNames.CAST)) {
      findVarsInPtn(Abstract.getArg(ptn, 0), cxt, outer, varFinder);
    } else if (Abstract.isBinary(ptn, StandardNames.WHERE)) {
      findVarsInPtn(Abstract.getArg(ptn, 0), cxt, outer, varFinder);
      findVarsInCondition(Abstract.getArg(ptn, 1), cxt, outer, varFinder);
    } else if (Abstract.isBinary(ptn, StandardNames.APPLY)) {
      findVarsInPtn(Abstract.getArg(ptn, 0), cxt, outer, varFinder);
      findVarsInPtn(Abstract.getArg(ptn, 1), cxt, outer, varFinder);
    } else if (CompilerUtils.isApply(ptn)) {
      Apply apply = (Apply) ptn;
      for (IValue arg : apply.getArgs())
        findVarsInPtn((IAbstract) arg, cxt, outer, varFinder);
    }
  }

  private ICondition typeOfCond(IAbstract condition, Dictionary cxt, Dictionary outer, PtnVarHandler varHandler)
  {
    Location loc = condition.getLoc();

    if (Abstract.isParenTerm(condition))
      return typeOfCond(Abstract.deParen(condition), cxt, outer, varHandler);
    else if (Abstract.isBinary(condition, StandardNames.AND) || Abstract.isBinary(condition, StandardNames.WHERE)) {
      ICondition lhs = typeOfCond(Abstract.binaryLhs(condition), cxt, outer, varHandler);
      ICondition rhs = typeOfCond(Abstract.binaryRhs(condition), cxt, outer, varHandler);
      return new Conjunction(loc, lhs, rhs);
    } else if (Abstract.isBinary(condition, StandardNames.OR)) {
      Dictionary leftCxt = cxt.fork();
      ICondition lhs = typeOfCond(Abstract.binaryLhs(condition), leftCxt, outer, varHandler);
      Dictionary rightCxt = cxt.fork();
      ICondition rhs = typeOfCond(Abstract.binaryRhs(condition), rightCxt, outer, varHandler);
      if (!reconcile(cxt, leftCxt, rightCxt, loc))
        errors.reportError(StringUtils.msg("cannot reconcile variables defined in ", lhs,
            ", with variables defined in ", rhs), loc);
      return new Disjunction(loc, lhs, rhs);
    } else if (Abstract.isBinary(condition, StandardNames.IMPLIES)) {
      Dictionary leftCxt = cxt.fork();
      ICondition gen = typeOfCond(Abstract.getArg(condition, 0), leftCxt, outer, varHandler);
      ICondition test = typeOfCond(Abstract.getArg(condition, 1), leftCxt, outer, varHandler);

      return new Implies(loc, test, gen);
    } else if (Abstract.isBinary(condition, StandardNames.OTHERWISE)) {
      Dictionary leftCxt = cxt.fork();
      ICondition lhs = typeOfCond(Abstract.getArg(condition, 0), leftCxt, outer, varHandler);
      Dictionary rightCxt = cxt.fork();
      ICondition rhs = typeOfCond(Abstract.getArg(condition, 1), rightCxt, outer, varHandler);
      if (!reconcile(cxt, leftCxt, rightCxt, loc))
        errors.reportError(StringUtils.msg("cannot reconcile variables defined in ", lhs,
            ", with variables defined in ", rhs), loc);
      return new Otherwise(lhs.getLoc(), lhs, rhs);
    } else if (Abstract.isUnary(condition, StandardNames.NOT)) {
      ICondition rhs = typeOfCond(Abstract.unaryArg(condition), cxt.fork(), outer, varHandler);
      return new Negation(loc, rhs);
    } else if (CompilerUtils.isKeyValCond(condition)) {

      IType kyType = new TypeVar();
      IType vlType = new TypeVar();
      TypeVar rlType = new TypeVar();
      ContractConstraint constraint = new ContractConstraint(StandardNames.IXITERABLE, rlType, TypeUtils
          .determinedType(kyType, vlType));
      rlType.setConstraint(constraint);

      Wrapper<ICondition> cond = new Wrapper<>(null);

      IContentExpression rhs = typeOfExp(CompilerUtils.keyValCondColl(condition), rlType, cxt, outer);

      IContentPattern kyPtn = typeOfPtn(CompilerUtils.keyValCondKey(condition), kyType, cond, cxt, outer, varHandler);
      IContentPattern vlPtn = typeOfPtn(CompilerUtils.keyValCondVal(condition), vlType, cond, cxt, outer, varHandler);

      ICondition mapTest = new ListSearch(loc, vlPtn, kyPtn, rhs);

      if (!cond.isEmpty())
        return new Conjunction(loc, mapTest, cond.get());
      else
        return mapTest;
    } else if (Abstract.isBinary(condition, StandardNames.IN)
        && CompilerUtils.isIndexPattern(Abstract.binaryLhs(condition))) {
      TypeVar kyType = new TypeVar();
      TypeVar elType = new TypeVar();
      TypeVar rlType = new TypeVar();

      ContractConstraint constraint = new ContractConstraint(StandardNames.IXITERABLE, rlType, TypeUtils
          .determinedType(kyType, elType));
      rlType.setConstraint(constraint);

      Wrapper<ICondition> cond = new Wrapper<ICondition>(CompilerUtils.truth);

      IContentExpression rhs = typeOfExp(Abstract.binaryRhs(condition), rlType, cxt, outer);
      IAbstract ptn = Abstract.binaryLhs(condition);

      errors.reportWarning(StringUtils.msg(CompilerUtils.indexPttrnPtn(ptn), "[", CompilerUtils.indexPttrnIx(ptn), "]",
          " in ", Abstract.binaryRhs(condition), " deprecated, use\n", CompilerUtils.indexPttrnIx(ptn), " -> ",
          CompilerUtils.indexPttrnPtn(ptn), " in ", Abstract.binaryRhs(condition)), loc);

      IContentPattern elPtn = typeOfPtn(CompilerUtils.indexPttrnPtn(ptn), elType, cond, cxt, outer, varHandler);
      IContentPattern ixPtn = typeOfPtn(CompilerUtils.indexPttrnIx(ptn), kyType, cond, cxt, outer, varHandler);

      ICondition search = new ListSearch(loc, elPtn, ixPtn, rhs);

      if (!cond.isEmpty())
        return new Conjunction(loc, search, cond.get());
      else
        return search;
    } else if (Abstract.isBinary(condition, StandardNames.IN)) {
      TypeVar elType = new TypeVar();
      TypeVar rlType = new TypeVar();

      rlType.setConstraint(new ContractConstraint(StandardNames.ITERABLE, rlType, TypeUtils.determinedType(elType)));

      Wrapper<ICondition> cond = new Wrapper<>(CompilerUtils.truth);

      IContentExpression rhs = typeOfExp(Abstract.binaryRhs(condition), rlType, cxt, outer);

      IContentPattern lhs = typeOfPtn(Abstract.binaryLhs(condition), elType, cond, cxt, outer, varHandler);
      ICondition relQ = new Search(loc, lhs, rhs);

      if (!CompilerUtils.isTrivial(cond.get()))
        return new Conjunction(loc, relQ, cond.get());
      else
        return relQ;
    } else if (CompilerUtils.isConditional(condition)) {
      Dictionary leftCxt = cxt.fork();
      ICondition tst = typeOfCond(CompilerUtils.conditionalTest(condition), cxt, outer, varHandler);
      ICondition lhs = typeOfCond(CompilerUtils.conditionalThen(condition), leftCxt, outer, varHandler);
      Dictionary rightCxt = cxt.fork();
      ICondition rhs = typeOfCond(CompilerUtils.conditionalElse(condition), rightCxt, outer, varHandler);
      if (!reconcile(cxt, leftCxt, rightCxt, loc))
        errors.reportError(StringUtils.msg("cannot reconcile variables defined in ", lhs,
            ", with variabls defined in ", rhs), loc);
      return new ConditionCondition(loc, tst, lhs, rhs);
    } else if (CompilerUtils.isBoundTo(condition)) {
      Wrapper<ICondition> cond = new Wrapper<>((ICondition) new TrueCondition(loc));
      IContentExpression lhs = typeOfExp(CompilerUtils.boundToExp(condition), new TypeVar(), cxt, outer);
      IContentPattern rhs = typeOfPtn(CompilerUtils.boundToPtn(condition), lhs.getType(), cond, cxt, outer, varHandler);

      if (cond.get() instanceof TrueCondition)
        return new Matches(loc, lhs, rhs);
      else
        return new Conjunction(loc, new Matches(loc, lhs, rhs), cond.get());
    } else if (Abstract.isBinary(condition, StandardNames.NOT_EQUAL))
      return typeOfCond(Abstract.unary(loc, StandardNames.NOT, Abstract.binary(loc, StandardNames.EQUAL, Abstract
          .binaryLhs(condition), Abstract.binaryRhs(condition))), cxt, outer, varHandler);
    else
      return new IsTrue(loc, typeOfExp(condition, booleanType, cxt, outer));
  }

  /**
   * Bring definitions from other to this
   * 
   * @param cxt
   *          parent dictionary
   * @param left
   *          dict corresponding to lhs of condition
   * @param right
   *          dict corresponding to rhs of condition
   * @param loc
   *          where to record any bindings
   * @return true if reconciliation possible
   */
  private static boolean reconcile(Dictionary cxt, Dictionary left, Dictionary right, Location loc)
  {
    Map<String, DictInfo> varsToDeclare = new HashMap<>();

    for (Iterator<DictInfo> leftIt = left.iterator(); leftIt.hasNext();) {
      DictInfo var = leftIt.next();
      String vrName = var.getName();
      if (!cxt.isDefinedVar(vrName)) {
        DictInfo rgtVar = right.getVar(vrName);
        if (rgtVar != null) {
          try {
            TypeUtils.unify(rgtVar.getType(), var.getType(), loc, cxt);
            varsToDeclare.put(vrName, var);
          } catch (TypeConstraintException e) {
            return false;
          }
        }
      } else if (var instanceof VarInfo && cxt.isFreeVar(var.getVariable()) && !cxt.isDeclaredVar(vrName))
        varsToDeclare.put(vrName, var);
    }

    for (Iterator<DictInfo> rightIt = right.iterator(); rightIt.hasNext();) {
      DictInfo var = rightIt.next();
      String vrName = var.getName();
      if (!cxt.isDefinedVar(vrName) && var instanceof VarInfo && cxt.isFreeVar(var.getVariable())
          && !cxt.isDeclaredVar(vrName))
        varsToDeclare.put(vrName, var);
    }

    for (DictInfo var : varsToDeclare.values())
      cxt.declareVar(var.getName(), var);
    return true;
  }

  private static IContentExpression verifyType(IType rType, IType eType, Location loc, IContentExpression orig,
      Dictionary cxt, ErrorReport errors)
  {
    try {
      Subsume.subsume(eType, rType, loc, cxt);
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg(orig, " has type ", rType, "\nwhich is not consistent with ", eType,
          "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
    }

    return orig;
  }

  static IContentExpression verifyType(IType rType, Map<String, Quantifier> bounds, IType eType,
      IContentExpression orig, Dictionary cxt, ErrorReport errors)
  {
    Location loc = orig.getLoc();
    try {
      Subsume.subsume(eType, rType, loc, cxt);
      checkForRawBindings(loc, bounds);
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg(orig, " has type ", rType, "\nwhich is not consistent with ", eType,
          "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
    }

    return orig;
  }

  static void checkForRawBindings(Location loc, Map<String, Quantifier> bound) throws TypeConstraintException
  {
    for (Entry<String, Quantifier> e : bound.entrySet()) {
      IType tp = TypeUtils.deRef(e.getValue().getVar());

      if (TypeUtils.isRawType(tp))
        throw new TypeConstraintException(StringUtils.msg("not permitted to constrain ", e.getKey(), " with raw type ",
            tp), loc);
    }
  }

  private static IContentPattern verifyType(IType expectedType, Location loc, IContentPattern orig, Dictionary cxt,
      ErrorReport errors)
  {
    try {
      Subsume.same(orig.getType(), expectedType, loc, cxt);
      return orig;
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg("`", orig, ":", orig.getType(), "' not consistent with expected type ",
          expectedType, "\nbecause ", e.getWords()), loc);
      return orig;
    }
  }

  private boolean checkDefaults(Location loc, String typeLabel, String recordLabel, Map<String, IType> fields,
      Map<String, IContentExpression> els, Dictionary cxt, List<String> missingFields)
  {
    boolean defaultRequired = false;

    for (String member : fields.keySet()) {
      if (!els.containsKey(member)) {
        missingFields.add(member);

        if (getDefaultFor(cxt, typeLabel, recordLabel, member) == null)
          errors.reportError(StringUtils.msg("attribute '", member, "' missing, and no default"), loc);
        else
          defaultRequired = true;
      }
    }
    return defaultRequired;
  }

  /**
   * Some fields in some records may have defaults associated with them. This function will return
   * information about such a default if it exists.
   * <p/>
   * If the default exists, then it takes the form of a function from the record itself to the value
   * of the field being set within the record. For example, in the type definition:
   * <p/>
   * <p/>
   * 
   * <pre>
   * type Person is someone{
   *   name has type string;
   *   age has type integer;
   *   age default is anyof A where (name,A) in ages
   * }
   * </pre>
   * <p/>
   * assuming that {@code ages} is a relation that is in scope, then an expression:
   * <p/>
   * <p/>
   * 
   * <pre>
   * john is someone{name is "peter"}
   * </pre>
   * <p/>
   * is compiled into the equivalent of:
   * <p/>
   * <p/>
   * 
   * <pre>
   * john is valof{
   *   var J := someone{name is "peter"}
   *   J.age := someone#age(J)
   *   valis J
   * }
   * </pre>
   * <p/>
   * where the {@code someone#age} function itself looks like:
   * <p/>
   * <p/>
   * 
   * <pre>
   * someone#age(S) is anyof A where (A,S.name) in ages
   * </pre>
   * 
   * @param member
   *          within the constructor
   * @return data about the default function. The {@code VarInfo} record simply returns the name of
   *         the function to use in computing the default value. If there is no default for the
   *         field, then {@code null} is returned.
   */
  private static VarInfo getDefaultFor(Dictionary cxt, String typeLabel, String recordLabel, String member)
  {
    DictInfo deflt = cxt.varReference(CompilerUtils.defaultLabel(typeLabel, recordLabel, member));
    if (deflt instanceof VarInfo)
      return ((VarInfo) deflt);
    else
      return null;
  }

  // We compute this in a sub-terranean way because defaults do not apply to the
  // Cafe level, and so we do not
  // wish to pollute the record description.

  private static Pair<IContentExpression[], IType[]> nonDefaultsVars(Location loc, Dictionary dict, String tpLabel,
      String rcLabel, TypeInterface face)
  {
    Set<String> names = new TreeSet<>();
    Map<String, IType> index = face.getAllFields();
    for (Entry<String, IType> entry : index.entrySet()) {
      DictInfo deflt = dict.varReference(CompilerUtils.defaultLabel(tpLabel, rcLabel, entry.getKey()));
      if (deflt == null)
        names.add(entry.getKey());
    }
    List<IContentExpression> vars = new ArrayList<>();
    List<IType> varTypes = new ArrayList<>();
    for (String name : names) {
      vars.add(Variable.create(loc, face.getFieldType(name), name));
      varTypes.add(face.getFieldType(name));
    }
    return Pair.pair(vars.toArray(new IContentExpression[vars.size()]), varTypes.toArray(new IType[varTypes.size()]));
  }

  IContentExpression[] argTuple(IList tpl, IType expectedTypes[], Dictionary cxt, Dictionary outer)
  {
    IContentExpression els[] = new IContentExpression[tpl.size()];
    for (int ix = 0; ix < tpl.size(); ix++) {
      boolean rawAllowed = TypeUtils.isRawType(expectedTypes[ix]);
      els[ix] = typeOfExp((IAbstract) tpl.getCell(ix), expectedTypes[ix], cxt, outer);
      if (!rawAllowed && TypeUtils.isRawType(expectedTypes[ix]))
        errors.reportError(StringUtils.msg("cannot use raw type ", expectedTypes[ix]), ((IAbstract) tpl.getCell(ix))
            .getLoc());
    }
    return els;
  }

  private IContentExpression lvalueType(IAbstract term, IType expectedType, Dictionary dict, Dictionary outer)
  {
    final Location loc = term.getLoc();
    term = Abstract.deParen(term);

    if (CompilerUtils.isIdentifier(term)) {
      final String identifier = Abstract.getId(term);
      if (StandardNames.isKeyword(identifier)) {
        errors.reportError(StringUtils.msg("unexpected keyword: ", term), loc);
        return new VoidExp(loc);
      } else {
        DictInfo info = dict.varReference(identifier);

        if (info instanceof VarInfo) {
          IContentExpression var = info.getVariable().verifyType(loc, errors, expectedType, dict, true);

          if (info.getAccess() == readOnly && !TypeUtils.isReferenceType(expectedType))
            errors.reportError(StringUtils.msg("cannot assign or modify variable ", identifier), loc);

          return var;
        } else {
          errors.reportError(StringUtils.msg(identifier, " not a variable"), loc);
          return new VoidExp(loc);
        }
      }
    } else if (CompilerUtils.isFieldAccess(term)) {
      IAbstract rc = CompilerUtils.fieldRecord(term);
      IAbstract field = CompilerUtils.fieldField(term);

      if (CompilerUtils.isIdentifier(field)) {
        IType recordType = new TypeVar();
        IContentExpression record = typeOfExp(rc, recordType, dict, outer);

        String fieldName = Abstract.getId(field);
        return fieldOfRecord(loc, record, fieldName, expectedType, dict, errors);
      } else if (Abstract.isParenTerm(field)) {
        IAbstract reform = Abstract.binary(loc, StandardNames.PERIOD, rc, Abstract.deParen(field));
        return lvalueType(reform, expectedType, dict, outer);
      } else if (field instanceof Apply && CompilerUtils.isIdentifier(((Apply) field).getOperator())) {
        // R.m(A1,..,An) -> (R.m)(A1,..,An)
        IAbstract reform = new Apply(loc,
            Abstract.binary(loc, StandardNames.PERIOD, rc, ((Apply) field).getOperator()), ((Apply) field).getArgs());
        return lvalueType(reform, expectedType, dict, outer);
      } else {
        errors.reportError(StringUtils.msg("invalid expression after period, got ", field), loc);
        return new VoidExp(loc);
      }
    } else if (Abstract.isTupleTerm(term)) {
      IList tpl = Abstract.tupleArgs(term);
      int arity = tpl.size();

      IType refType = new TypeVar();
      IType argTypes[];

      try {
        Subsume.subsume(TypeUtils.referenceType(refType), expectedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(expectedType, " should be a ref type"), loc);
        return new VoidExp(loc);
      }

      if (TypeUtils.isTupleType(refType))
        argTypes = TypeUtils.tupleTypes(refType);
      else if (TypeUtils.isTypeVar(refType)) {
        argTypes = new IType[arity];
        for (int ix = 0; ix < argTypes.length; ix++)
          argTypes[ix] = new TypeVar();

        IType tupleType = TypeUtils.tupleType(argTypes);

        try {
          Subsume.subsume(tupleType, refType, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("cannot bind ", expectedType, " to ", tupleType, "\nbecause ", e
              .getWords()), merge(loc, e.getLocs()));
        }
      } else {
        errors.reportError(StringUtils.msg("not expecting tuple ", term, " here"), loc);
        return new VoidExp(loc);
      }
      if (arity == argTypes.length) {
        IContentExpression args[] = new IContentExpression[arity];

        for (int ix = 0; ix < arity; ix++)
          args[ix] = lvalueType((IAbstract) tpl.getCell(ix), TypeUtils.referenceType(argTypes[ix]), dict, outer);

        return new ConstructorTerm(loc, args);
      } else {
        errors.reportError(StringUtils.msg("expecting a tuple of ", argTypes.length, " elements"), loc);
        return new VoidExp(loc);
      }
    } else
      return typeOfExp(term, expectedType, dict, outer);
  }

  private void ptnTypeTpl(IList args, IType types[], IContentPattern elements[], Wrapper<ICondition> cond,
      Dictionary cxt, Dictionary outer, PtnVarHandler varHandler)
  {
    for (int ix = 0; ix < args.size(); ix++) {
      elements[ix] = typeOfPtn((IAbstract) args.getCell(ix), types[ix], cond, cxt, outer, varHandler);
    }
  }

  interface PtnVarHandler
  {
    Variable typeOfVariable(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition, Permission duplicates,
        Dictionary cxt);
  }

  private class VarHandler implements PtnVarHandler
  {
    private final AccessMode access;
    private final Visibility visibility;

    public VarHandler(AccessMode access, Visibility visibility)
    {
      this.access = access;
      this.visibility = visibility;
    }

    @Override
    public Variable typeOfVariable(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition,
        Permission duplicates, Dictionary dict)
    {
      String vrName = Abstract.getId(ptn);
      Location loc = ptn.getLoc();

      if (dict.isDefinedVar(vrName)) {
        // Duplicate occurrence of a variable
        VarInfo info = (VarInfo) dict.getVar(vrName);
        IContentExpression var = info.getVariable();

        IType varType = info.getType();
        if (TypeUtils.isOverloadedType(varType))
          varType = TypeUtils.getOverloadedType(varType);

        if (TypeUtils.isReferenceType(varType)) {
          IType refType = TypeUtils.referencedType(varType);
          try {
            Subsume.same(expectedType, refType, loc, dict);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("variable ", vrName, ":", refType,
                " not consistent with expected type: ", expectedType), merge(loc, e.getLocs()));
          }

          Variable nVar = Variable.create(loc, refType, GenSym.genSym("__"));

          try {
            IType trial = new TypeVar();
            ((TypeVar) trial).addContractRequirement((TypeExp) TypeUtils.typeExp(StandardNames.EQUALITY, trial), loc,
                dict);
            Subsume.same(refType, trial, loc, dict);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg(
                "cannot have multiple occurrences of variables whose type does not support equality\nbecause ", e
                    .getWords()), merge(loc, e.getLocs()));
          }

          CompilerUtils.extendCondition(condition, CompilerUtils.equals(loc, new Shriek(loc, var), nVar));
          return nVar;
        } else {
          try {
            TypeUtils.unify(expectedType, varType, loc, dict);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("type of variable ", vrName, ":", varType,
                " not consistent with expected type\nbecause ", e.getWords()), merge(loc, e.getLocs()));
          }

          if (!info.isInitialized()) {
            info.setInitialized(true);
            info.setAccess(access);
            return info.getVariable();
          } else {
            Variable nVar = Variable.create(loc, varType, GenSym.genSym("__"));

            try {
              IType trial = new TypeVar();
              ((TypeVar) trial).addContractRequirement((TypeExp) TypeUtils.typeExp(StandardNames.EQUALITY, trial), loc,
                  dict);
              TypeUtils.unify(varType, trial, loc, dict);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg(
                  "cannot have multiple occurrences of variables whose type does not support equality\nbecause ", e
                      .getWords()), merge(loc, e.getLocs()));
            }

            CompilerUtils.extendCondition(condition, CompilerUtils.equals(loc, var, nVar));
            return nVar;
          }
        }
      } else
        dict.declareVar(vrName, Variable.create(loc, expectedType, vrName), access, visibility, true);

      return Variable.create(loc, expectedType, vrName);
    }
  }

  IContentPattern typeOfPtn(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition, Dictionary cxt,
      Dictionary outer, PtnVarHandler varHandler)
  {
    final Location loc = ptn.getLoc();

    if (CompilerUtils.isIdentifier(ptn)) {
      final String vrName = Abstract.getId(ptn);

      if (cxt.isConstructor(vrName)) {
        IValueSpecifier cons = cxt.getConstructor(vrName);

        if (cons instanceof ConstructorSpecifier) {
          ConstructorSpecifier posCon = (ConstructorSpecifier) cons;
          if (posCon.arity() == 0) {
            final IType posType = Freshen.freshenForUse(posCon.getConType());
            try {
              TypeUtils.unify(posType, TypeUtils.constructorType(expectedType), loc, cxt);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg(cons, " not consistent with expected type\nbecause ", e.getWords()),
                  loc);
            }
            return new ConstructorPtn(loc, vrName, expectedType);
          } else
            errors.reportError(StringUtils.msg(vrName, " is not legal here"), loc);
        } else
          errors.reportError(StringUtils.msg("invalid pattern: ", ptn), loc);
        return Variable.create(loc, new TypeVar(), vrName);
      } else if (vrName.equals(StandardNames.ANONYMOUS))
        return Variable.anonymous(loc, expectedType);
      else if (vrName.equals(StandardNames.BRACES)) {
        Map<String, Integer> index = new HashMap<>();
        Map<String, IContentPattern> els = new HashMap<>();
        IType type = TypeUtils.typeInterface(new TreeMap<String, IType>());
        return verifyType(expectedType, loc, new RecordPtn(loc, type, els, index), cxt, errors);
      } else if (vrName.equals(StandardNames.SQUARE)) {
        return typeOfPtn(Abstract.zeroary(loc, StandardNames.EMPTY), expectedType, condition, cxt, outer, varHandler);
      } else if (StandardNames.isKeyword(ptn))
        errors.reportError("unexpected keyword", loc);
      else
        return varHandler.typeOfVariable(ptn, expectedType, condition, Permission.notAllowed, cxt);
      return Variable.anonymous(loc, expectedType);
    } else if (ptn instanceof IntegerLiteral) {
      IContentPattern scalar = TypeCheckerUtils.integerPtn(loc, ((IntegerLiteral) ptn).getLit());
      return verifyType(expectedType, loc, scalar, cxt, errors);
    } else if (ptn instanceof LongLiteral) {
      IContentPattern scalar = TypeCheckerUtils.longPtn(loc, ((LongLiteral) ptn).getLit());
      verifyType(expectedType, loc, scalar, cxt, errors);
      return scalar;
    } else if (ptn instanceof FloatLiteral) {
      IContentPattern scalar = TypeCheckerUtils.floatPtn(loc, ((FloatLiteral) ptn).getLit());
      verifyType(expectedType, loc, scalar, cxt, errors);
      return scalar;
    } else if (ptn instanceof BigDecimalLiteral) {
      IContentPattern scalar = TypeCheckerUtils.decimalPtn(loc, ((BigDecimalLiteral) ptn).getLit());
      verifyType(expectedType, loc, scalar, cxt, errors);
      return scalar;
    } else if (ptn instanceof CharLiteral) {
      IContentPattern scalar = TypeCheckerUtils.charPtn(loc, ((CharLiteral) ptn).getLit());
      verifyType(expectedType, loc, scalar, cxt, errors);
      return scalar;
    } else if (ptn instanceof StringLiteral) {
      IContentPattern scalar = TypeCheckerUtils.stringPtn(loc, ((StringLiteral) ptn).getLit());
      verifyType(expectedType, loc, scalar, cxt, errors);
      return scalar;
    } else if (Abstract.isUnary(ptn, StandardNames.RAW)) {
      ptn = Abstract.unaryArg(ptn);
      final IContentPattern raw;
      if (ptn instanceof IntegerLiteral)
        raw = new ScalarPtn(loc, rawIntegerType, Factory.newInt(((IntegerLiteral) ptn).getLit()));
      else if (ptn instanceof LongLiteral)
        raw = new ScalarPtn(loc, rawLongType, Factory.newLong(((LongLiteral) ptn).getLit()));
      else if (ptn instanceof FloatLiteral)
        raw = new ScalarPtn(loc, rawFloatType, Factory.newFloat(((FloatLiteral) ptn).getLit()));
      else if (ptn instanceof BigDecimalLiteral)
        raw = new ScalarPtn(loc, rawDecimalType, Factory.newDecimal(((BigDecimalLiteral) ptn).getLit()));
      else if (ptn instanceof CharLiteral)
        raw = new ScalarPtn(loc, rawCharType, Factory.newChar(((CharLiteral) ptn).getLit()));
      else if (ptn instanceof StringLiteral)
        raw = new ScalarPtn(loc, rawStringType, Factory.newString(((StringLiteral) ptn).getLit()));
      else {
        errors.reportError(StringUtils.msg("not expecting ", ptn), loc);
        return Variable.anonymous(loc, expectedType);
      }
      return verifyType(expectedType, loc, raw, cxt, errors);
    } else if (CompilerUtils.isRegexp(ptn)) {
      try {
        TypeUtils.unify(expectedType, stringType, loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("type of ", ptn, " not consistent with expected type `", expectedType,
            "'\nbecause ", e.getWords()), loc);
      }
      String text = CompilerUtils.regexpExp(ptn);
      Location loc1 = CompilerUtils.regexpLoc(ptn);

      // Look for regexp meta characters
      if (StringUtils.containsAny(text, "[]*+?.:()|\\\"$")) {
        List<IContentPattern> subVars = new ArrayList<>();
        StringBuilder blder = new StringBuilder();
        findGroups(loc1, new StringSequence(text), subVars, blder, condition, cxt, varHandler, errors);
        Set<Variable> vars = new TreeSet<>();
        NFA regexp = RegexpParse.regexpNFA(loc1.offset(1, text.length()), text, errors, vars, cxt);

        // We add in a string pattern
        Variable nVar = new Variable(loc, StandardTypes.rawStringType, GenSym.genSym("re"));
        ConstructorPtn conPtn = new ConstructorPtn(loc, StandardTypes.STRING, StandardTypes.stringType, nVar);
        CompilerUtils.extendCondition(condition, new Matches(loc, nVar, new RegExpPattern(loc1, blder.toString(),
            regexp, subVars.toArray(new IContentPattern[subVars.size()]))));

        return conPtn;
      } else
        return CompilerUtils.stringPattern(loc1, text);
    } else if (CompilerUtils.isAnonAggConLiteral(ptn)) {
      IAbstract arg = Abstract.getArg(ptn, 0);
      expectedType = TypeUtils.deRef(expectedType);

      Variable recordVar = Variable.create(loc, expectedType, GenSym.genSym("_"));

      for (IAbstract el : CompilerUtils.unWrap(arg)) {
        if (CompilerUtils.isEquals(el)) {
          if (CompilerUtils.isIdentifier(Abstract.getArg(el, 0))) {
            String member = Abstract.getId(Abstract.getArg(el, 0));
            IType memType = fieldType(loc, expectedType, member, new TypeVar(), cxt, errors);

            IContentPattern elPtn = typeOfPtn(Abstract.getArg(el, 1), memType, condition, cxt, outer, varHandler);
            CompilerUtils.extendCondition(condition, new Matches(loc, FieldAccess.create(loc, memType, recordVar,
                member), elPtn));
          }
        } else if (CompilerUtils.isAssignment(el)) {
          if (CompilerUtils.isIdentifier(CompilerUtils.assignedVar(el))) {
            String member = Abstract.getId(CompilerUtils.assignedVar(el));
            IType memType = fieldType(loc, expectedType, member, TypeUtils.referenceType(new TypeVar()), cxt, errors);

            IContentPattern elPtn = typeOfPtn(CompilerUtils.assignedValue(el), memType, condition, cxt, outer,
                varHandler);
            CompilerUtils.extendCondition(condition, new Matches(loc, FieldAccess.create(loc, memType, recordVar,
                member), elPtn));
          }
        } else if (CompilerUtils.isTypeEquality(el)) {
          String name = Abstract.getId(CompilerUtils.typeEqualField(el));
          IType type = TypeParser.parseType(CompilerUtils.typeEqualType(el), cxt, errors, readWrite);

          memberType(loc, expectedType, name, type, cxt, errors);
        } else
          errors.reportError(StringUtils.msg("invalid member of record: ", el), el.getLoc());
      }

      return recordVar;
    } else if (Abstract.isUnary(ptn, StandardNames.BRACES)) {
      errors.reportError(StringUtils.msg("not permitted as a pattern: ", ptn), loc);
      return Variable.anonymous(loc, expectedType);
    } else if (CompilerUtils.isQuoted(ptn)) {
      try {
        TypeUtils.unify(expectedType, StandardTypes.astType, loc, cxt);
      } catch (TypeConstraintException e) {
        errors
            .reportError(StringUtils.msg("expression not consistent with expected type\nbecause ", e.getWords()), loc);
      }
      PtnQuoter quoter = new PtnQuoter(cxt, outer, condition, varHandler, this, errors);

      return quoter.quoted(CompilerUtils.quotedExp(ptn));
    } else if (CompilerUtils.isBraceTerm(ptn))
      return recordPattern(ptn, expectedType, condition, cxt, outer, varHandler);
    // Special <Lbl> of { <el>; .. ; <el>} or <Lbl> of { <el>; .. ;.. <Ptn> }
    // pattern.
    else if (CompilerUtils.isSequenceTerm(ptn)) {
      String label = CompilerUtils.sequenceLabel(ptn);

      sequenceType(label, expectedType, cxt, loc, errors);

      IAbstract braceArg = CompilerUtils.sequenceContent(ptn);
      IAbstract construct = braceSequence(loc, braceArg, StandardNames.PAIR, StandardNames.BACK, StandardNames.EMPTY,
          errors);

      return typeOfPtn(construct, expectedType, condition, cxt, outer, varHandler);
    } else if (CompilerUtils.isSquareSequenceTerm(ptn) || CompilerUtils.isLabeledSequenceTerm(ptn))
      return squareSequencePattern(ptn, expectedType, condition, cxt, outer, varHandler);
    else if (Abstract.isParenTerm(ptn)) {
      ptn = Abstract.unaryArg(ptn);
      if (Abstract.isTupleTerm(ptn))
        return typeOfTuplePtn(ptn, expectedType, condition, cxt, outer, varHandler);
      else
        return typeOfPtn(ptn, expectedType, condition, cxt, outer, varHandler);
    } else if (Abstract.isTupleTerm(ptn)) {
      return typeOfTuplePtn(ptn, expectedType, condition, cxt, outer, varHandler);
    } else if (Abstract.isBinary(ptn, StandardNames.MATCHING)) {
      IContentPattern lhs = typeOfPtn(Abstract.binaryLhs(ptn), expectedType, condition, cxt, outer, varHandler);
      IContentPattern rhs = typeOfPtn(Abstract.binaryRhs(ptn), expectedType, condition, cxt, outer, varHandler);

      if (lhs instanceof Variable)
        return new MatchingPattern(loc, (Variable) lhs, rhs);
      else if (rhs instanceof Variable)
        return new MatchingPattern(loc, (Variable) rhs, lhs);
      else {
        Variable var = Variable.create(loc, lhs.getType(), GenSym.genSym("_"));
        ICondition matches = new Matches(loc, var, lhs);

        CompilerUtils.extendCondition(condition, matches);
        CompilerUtils.extendCondition(condition, new Matches(loc, var, rhs));
        return Variable.create(loc, lhs.getType(), var.getName());
      }
    } else if (CompilerUtils.isCast(ptn)) {
      IType innerType = TypeParser.parseType(CompilerUtils.castType(ptn), cxt, errors, readWrite);

      IContentPattern inner = typeOfPtn(CompilerUtils.castExp(ptn), innerType, condition, cxt, outer, varHandler);

      try {
        TypeUtils.unify(expectedType, innerType, loc, cxt);

        return inner; // Nothing to do
      } catch (TypeConstraintException e) {
        if (TypeUtils.deRef(expectedType).equals(StandardTypes.anyType)
            || TypeUtils.deRef(innerType).equals(StandardTypes.anyType))
          return new CastPtn(loc, expectedType, inner);
        else {
          errors.reportError(StringUtils.msg("cannot cast ", inner, ":", innerType, " to ", expectedType), loc);
          return new CastPtn(loc, expectedType, inner);
        }
      }
    } else if (CompilerUtils.isTypeAnnotation(ptn)) {
      IType type = TypeParser.parseType(CompilerUtils.typeAnnotation(ptn), cxt, errors, readWrite);

      try {
        Subsume.same(type, expectedType, loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("target type ", type, " not consistent with expected type: ", expectedType,
            "\nbecause ", e.getWords()), loc);
      }

      return typeOfPtn(CompilerUtils.typeAnnotatedTerm(ptn), type, condition, cxt, outer, varHandler);
    } else if (CompilerUtils.isFieldAccess(ptn)) {
      IContentExpression dot = typeOfExp(ptn, expectedType, cxt, outer);
      TypeVar varType = new TypeVar();
      varType.setConstraint(new ContractConstraint((TypeExp) TypeUtils.typeExp(StandardNames.EQUALITY, varType)));
      Variable nV = Variable.anonymous(loc, varType);

      try {
        Subsume.subsume(expectedType, varType, loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(e.getWords()), merge(loc, e.getLocs()));
      }

      CompilerUtils.extendCondition(condition, CompilerUtils.equals(loc, nV, dot));
      return nV;
    } else if (Abstract.isBinary(ptn, StandardNames.WHERE)) {
      IContentPattern pttrn = typeOfPtn(Abstract.getArg(ptn, 0), expectedType, condition, cxt, outer, varHandler);
      IAbstract guard = Abstract.getArg(ptn, 1);
      ICondition cond = typeOfCond(guard, cxt, outer, varHandler);

      CompilerUtils.extendCondition(condition, cond);

      return pttrn;
    } else if (CompilerUtils.isReference(ptn)) {
      IAbstract refTerm = CompilerUtils.referencedTerm(ptn);
      if (Abstract.isIdentifier(refTerm)) {
        try {
          Subsume.subsume(TypeUtils.referenceType(new TypeVar()), expectedType, loc, cxt);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("type of ", refTerm, " not consistent with expected type: `",
              expectedType, "'\nbecause ", e.getWords()), loc);
        }

        return varHandler.typeOfVariable(refTerm, expectedType, condition, Permission.notAllowed, cxt);
      } else {
        errors.reportError(StringUtils.msg("expecting an identifier, not ", refTerm), refTerm.getLoc());
        return Variable.anonymous(loc, expectedType);
      }
    } else if (CompilerUtils.isVarPtn(ptn)) {
      IAbstract v = CompilerUtils.varPtnVar(ptn);
      return varHandler.typeOfVariable(v, expectedType, condition, Permission.allowed, cxt);
    } else if (Abstract.isBinary(ptn, StandardNames.APPLY)) {
      String conName = ((Name) Abstract.getArg(ptn, 0)).getId();

      IValueSpecifier cons = cxt.getConstructor(conName);

      if (cons instanceof RecordSpecifier) {
        RecordSpecifier record = (RecordSpecifier) cons;
        IType conType = record.getConType();

        if (TypeUtils.isConstructorType(conType)) {
          try {
            TypeUtils.unify(expectedType, TypeUtils.getConstructorResultType(conType), loc, cxt);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("type of ", conName, " not consistent with expected type: `",
                expectedType, "'\nbecause ", e.getWords()), loc);
          }

          IType applyArgType = applyBodyType(cons);
          IContentPattern appArg = typeOfPtn(Abstract.getArg(ptn, 1), applyArgType, condition, cxt, outer, varHandler);

          Map<String, IContentPattern> ptnEls = new TreeMap<>();
          SortedMap<String, IContentExpression> elements = new TreeMap<>();

          TypeInterface face = (TypeInterface) applyArgType;
          for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
            String att = entry.getKey();
            Variable var = Variable.create(loc, entry.getValue(), GenSym.genSym("@@"));

            ptnEls.put(att, var);
            elements.put(att, var);
          }

          CompilerUtils.extendCondition(condition, new Matches(loc, RecordTerm.anonRecord(loc, applyArgType, elements,
              face.getAllTypes()), appArg));

          IContentExpression recVar = new Variable(loc, conType, conName);
          return new RecordPtn(loc, expectedType, recVar, ptnEls, record.getIndex());
        } else
          errors.reportError(StringUtils.msg("invalid record constructor: ", cons), loc);
        return Variable.anonymous(loc, expectedType);
      } else if (cons instanceof ConstructorSpecifier) {
        IType conType = cons.getConType();

        if (TypeUtils.isConstructorType(conType)) {
          try {
            TypeUtils.unify(expectedType, TypeUtils.getConstructorResultType(conType), loc, cxt);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("type of ", conName, " not consistent with expected type: `",
                expectedType, "'\nbecause ", e.getWords()), loc);
          }

          IType ptnType = applyBodyType(cons);
          IContentPattern appArg = typeOfPtn(Abstract.getArg(ptn, 1), ptnType, condition, cxt, outer, varHandler);
          IType elTypes[] = ((TypeExp) ptnType).getTypeArgs();
          IContentPattern tplArgs[] = new IContentPattern[elTypes.length];
          IContentExpression varArgs[] = new IContentExpression[elTypes.length];

          for (int ix = 0; ix < elTypes.length; ix++) {
            Variable var = Variable.create(loc, elTypes[ix], GenSym.genSym("@@"));
            tplArgs[ix] = var;
            varArgs[ix] = var;
          }
          IContentExpression tpl = new ConstructorTerm(loc, TypeUtils.tupleType(elTypes), varArgs);
          IContentPattern con = new ConstructorPtn(loc, conName, expectedType, tplArgs);

          CompilerUtils.extendCondition(condition, new Matches(loc, tpl, appArg));
          return con;
        }
      }

      errors.reportError(StringUtils.msg(conName, " not known"), loc);
      return Variable.anonymous(loc, expectedType);
    } else if (CompilerUtils.isApply(ptn)) {
      Apply apply = ((Apply) ptn);
      Location conLoc = apply.getOperator().getLoc();
      String conName = Abstract.getId(apply.getOperator());
      IList args = apply.getArgs();
      int arity = args.size();

      IValueSpecifier cons = cxt.getConstructor(conName);

      if (cons instanceof RecordSpecifier) {
        errors.reportError(StringUtils.msg(conName, " is a record specifier, not consistent with ", ptn), conLoc);
        return Variable.anonymous(loc, expectedType);
      } else if (cons instanceof ConstructorSpecifier) {
        ConstructorSpecifier con = (ConstructorSpecifier) cons;

        if (con.arity() == arity) {
          try {
            IType conType = Freshen.freshenForUse(con.getConType());

            assert TypeUtils.isConstructorType(conType);

            TypeUtils.unify(expectedType, TypeUtils.getConstructorResultType(conType), conLoc, cxt);

            IContentPattern conArgs[] = new IContentPattern[arity];
            IType conArgTypes[] = TypeUtils.getConstructorArgTypes(conType);

            for (int ix = 0; ix < conArgs.length; ix++)
              conArgs[ix] = typeOfPtn(Abstract.getArg(apply, ix), conArgTypes[ix], condition, cxt, outer, varHandler);
            return new ConstructorPtn(loc, conName, expectedType, conArgs);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("type of ", ptn, " not consistent with expected type\nbecause ", e
                .getWords()), loc);
          }
        } else
          errors.reportError(StringUtils.msg("expecting " + con.arity() + " arguments, got ", arity), conLoc);

        return Variable.anonymous(loc, expectedType);
      } else if (conName.equals(StandardNames.ALL)) {
        errors.reportError("missing where clause", loc);
        return Variable.anonymous(loc, expectedType);
      } else if (StandardNames.isKeyword(conName)) {
        errors.reportError(StringUtils.msg(conName, " is a keyword that is not permitted here"), loc);
        return Variable.anonymous(loc, expectedType);
      } else {
        IContentPattern resPtns[] = new IContentPattern[arity];
        List<IType> resTypes = new ArrayList<>();

        for (int ix = 0; ix < arity; ix++) {
          IType resType = new TypeVar();
          resPtns[ix] = typeOfPtn((IAbstract) args.getCell(ix), resType, condition, cxt, outer, varHandler);
          resTypes.add(resType);
        }

        IContentExpression fun = typeOfExp(apply.getOperator(), new TypeVar(), cxt, outer);

        try {
          IType type = fun.getType();

          if (TypeUtils.isConstructorType(type)) {
            Subsume.subsume(type, TypeUtils.constructorType(resTypes, expectedType), loc, outer);
            if (isLocallyDefinedConstructor(fun, expectedType, cxt))
              return new ConstructorPtn(loc, ((Variable) fun).getName(), expectedType, resPtns);
            else
              return new PatternApplication(loc, expectedType, fun, resPtns);
          } else {
            Subsume.subsume(type, TypeUtils.patternType(TypeUtils.tupleType(resTypes), expectedType), loc, outer);
            return new PatternApplication(loc, expectedType, fun, resPtns);
          }
        } catch (TypeConstraintException e) {
          errors.reportError(
              StringUtils.msg("pattern ", fun, " not consistent with arguments\nbecause ", e.getWords()), loc);
          return Variable.anonymous(loc, expectedType);
        }
      }
    } else if (ptn instanceof Apply) {
      Apply apply = (Apply) ptn;
      IList args = apply.getArgs();
      int arity = args.size();

      IType ptnType = new TypeVar();
      IContentExpression fun = typeOfExp(apply.getOperator(), ptnType, cxt, outer);
      IContentPattern resPtns[] = new IContentPattern[arity];
      List<IType> resTypes = new ArrayList<>();

      for (int ix = 0; ix < arity; ix++) {
        IType resType = new TypeVar();
        resPtns[ix] = typeOfPtn((IAbstract) args.getCell(ix), resType, condition, cxt, outer, varHandler);
        resTypes.add(resType);
      }

      try {
        TypeUtils.unify(ptnType, TypeUtils.patternType(TypeUtils.tupleType(resTypes), expectedType), loc, cxt);
      } catch (TypeConstraintException e) {
        errors
            .reportError(StringUtils.msg("problem with pattern application: ", e.getWords()), merge(loc, e.getLocs()));
      }

      if (fun instanceof Variable)
        return new PatternApplication(loc, expectedType, fun, new ConstructorPtn(loc, resPtns));
      else {
        Variable tmp = Variable.create(fun.getLoc(), fun.getType(), GenSym.genSym("__Ptn$"));
        CompilerUtils.extendCondition(condition, new Matches(loc, fun, tmp));
        return new PatternApplication(loc, expectedType, tmp, new ConstructorPtn(loc, resPtns));
      }
    } else {
      IContentExpression exp = typeOfExp(ptn, expectedType, cxt, outer);
      Variable nVar = Variable.create(loc, exp.getType(), GenSym.genSym("__"));

      CompilerUtils.extendCondition(condition, CompilerUtils.equals(loc, exp, nVar));
      return nVar;
    }
  }

  private IContentPattern recordPattern(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition,
      Dictionary cxt, Dictionary outer, PtnVarHandler varHandler)
  {
    Location loc = ptn.getLoc();
    IAbstract label = CompilerUtils.braceLabel(ptn);
    IAbstract arg = CompilerUtils.braceArg(ptn);

    if (CompilerUtils.isIdentifier(label)) {
      String consName = Abstract.getId(label);
      IValueSpecifier cons = cxt.getConstructor(consName);

      if (cons instanceof RecordSpecifier) {
        RecordSpecifier record = (RecordSpecifier) cons;
        IType conType = Freshen.freshenForUse(record.getConType());

        try {
          Subsume.same(expectedType, TypeUtils.getConstructorResultType(conType), loc, cxt);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("type of ", ptn, " not consistent with expected type\nbecause ", e
              .getWords()), loc);
        }

        Map<String, IContentPattern> els = new HashMap<>();
        IType conArgType = TypeUtils.getConstructorArgType(conType);
        Pair<IType, Map<String, Quantifier>> freshenedArg = Freshen.freshen(conArgType, readOnly, readWrite);

        TypeInterface elTypes = (TypeInterface) freshenedArg.left;

        if (arg != null) {
          for (IAbstract el : CompilerUtils.unWrap(arg)) {
            final Location elLoc = el.getLoc();
            if (CompilerUtils.isEquals(el)) {
              if (CompilerUtils.isIdentifier(CompilerUtils.equalityLhs(el))) {
                final String member = Abstract.getId(CompilerUtils.equalityLhs(el));

                if (els.containsKey(member))
                  errors.reportError("multiple patterns for the same member not permitted.\nPrevious occurence of '"
                      + member + "' at " + els.get(member).getLoc(), elLoc);
                else if (record.hasMember(member)) {
                  IType elType = elTypes.getFieldType(member);
                  IContentPattern value = typeOfPtn(CompilerUtils.equalityRhs(el), elType, condition, cxt, outer,
                      varHandler);

                  els.put(member, value);
                } else
                  errors.reportError(member + " not known to be a member of aggregate " + record.getLabel(), elLoc);
              } else
                errors.reportError("expecting an identifier on lhs of =", elLoc);
            } else if (CompilerUtils.isAssignment(el)) {
              if (CompilerUtils.isIdentifier(CompilerUtils.assignedVar(el))) {
                String member = Abstract.getId(CompilerUtils.assignedVar(el));
                IType memType = fieldType(loc, expectedType, member, TypeUtils.referenceType(new TypeVar()), cxt,
                    errors);

                IContentPattern elPtn = typeOfPtn(CompilerUtils.assignedValue(el), memType, condition, cxt, outer,
                    varHandler);
                els.put(member, elPtn);
              }
            } else if (CompilerUtils.isTypeEquality(el)) {
              String name = Abstract.getId(CompilerUtils.typeEqualField(el));
              IType type = TypeParser.parseType(CompilerUtils.typeEqualType(el), cxt, errors, readWrite);

              memberType(loc, freshenedArg.left, name, type, cxt, errors);
            } else
              errors.reportError(StringUtils.msg(el, " is not a valid element of a record"), elLoc);
          }
        }

        IContentExpression recVar = new Variable(loc, conType, consName);
        return new RecordPtn(loc, TypeUtils.getConstructorResultType(conType), recVar, els, record.getIndex());
      } else {
        errors.reportError(StringUtils.msg(consName, " is not an record constructor"), loc);
        return Variable.anonymous(loc, expectedType);
      }
    } else {
      errors.reportError(StringUtils.msg("expecting an identifier: ", label), label.getLoc());
      return Variable.anonymous(loc, expectedType);
    }
  }

  private IContentPattern typeOfTuplePtn(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition,
      Dictionary dict, Dictionary outer, PtnVarHandler varHandler)
  {
    Location loc = ptn.getLoc();
    IList tplArgs = ((Apply) ptn).getArgs();
    int arity = tplArgs.size();
    IType elTypes[];

    expectedType = TypeUtils.deRef(expectedType);
    if (TypeUtils.isTupleType(expectedType)) {
      elTypes = TypeUtils.typeArgs(expectedType);
      if (elTypes.length != arity)
        errors.reportError(StringUtils.msg("expecting a tuple of ", elTypes.length, " elements, got ", arity,
            " elements"), loc);
    } else if (expectedType instanceof TypeVar) {
      elTypes = new IType[arity];
      for (int ix = 0; ix < elTypes.length; ix++)
        elTypes[ix] = new TypeVar();
      try {
        TypeUtils.unify(expectedType, TypeUtils.tupleType(elTypes), loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("pattern ", ptn, " not consistent with type ", expectedType, "\nbecause ", e
            .getWords()), merge(loc, e.getLocs()));
      }
    } else {
      errors.reportError(StringUtils.msg("expecting ", expectedType, " not ", ptn, " here"), loc);
      return Variable.anonymous(loc, expectedType);
    }

    IContentPattern els[] = new IContentPattern[arity];

    for (int ix = 0; ix < elTypes.length; ix++)
      els[ix] = typeOfPtn(Abstract.getArg(ptn, ix), elTypes[ix], condition, dict, outer, varHandler);

    return new ConstructorPtn(loc, expectedType, els);
  }

  private boolean isLocallyDefinedConstructor(IContentExpression fun, IType type, Dictionary cxt)
  {
    if (fun instanceof Variable) {
      String name = ((Variable) fun).getName();
      ITypeDescription desc = cxt.getTypeDescription(type.typeLabel());

      if (desc instanceof IAlgebraicType)
        return ((IAlgebraicType) desc).getValueSpecifier(name) != null;
    }
    return false;
  }

  private static IAbstract braceSequence(Location loc, IAbstract term, String cons, String apnd, String empty,
      ErrorReport errors)
  {
    List<IAbstract> front = new ArrayList<>();
    List<IAbstract> tail = new ArrayList<>();
    IAbstract remainder = null;
    List<IAbstract> els = front;

    for (IAbstract el : CompilerUtils.unWrap(term)) {
      if (Abstract.isBinary(el, StandardNames.CONS)) {
        if (remainder != null)
          errors.reportError(StringUtils.msg("not expecting elements after ", remainder), remainder.getLoc());
        else {
          assert els != null;
          els.add(Abstract.binaryLhs(el));
          IAbstract rhs = Abstract.binaryRhs(el);
          if (Abstract.isBinary(rhs, StandardNames.ENDCONS)) {
            remainder = Abstract.binaryLhs(rhs);
            els = tail;
            els.add(Abstract.binaryRhs(rhs));
          } else {
            remainder = rhs;
            els = null;
          }
        }
      } else if (Abstract.isBinary(el, StandardNames.ENDCONS)) {
        if (remainder != null)
          errors.reportError(StringUtils.msg("not expecting ", StandardNames.ENDCONS, " here"), el.getLoc());
        else if (!front.isEmpty())
          errors.reportError("too many elements to left of ..;", el.getLoc());
        else {
          remainder = Abstract.binaryLhs(el);
          els = tail;
          els.add(Abstract.binaryRhs(el));
        }
      } else if (els != null) {
        if (Abstract.isBinary(el, StandardNames.MAP_ARROW))
          els.add(Abstract.tupleTerm(el.getLoc(), Abstract.binaryLhs(el), Abstract.binaryRhs(el)));
        else
          els.add(el);
      } else if (remainder != null)
        errors.reportError(StringUtils.msg("not expecting elements after ", remainder), remainder.getLoc());
    }

    IAbstract reslt = remainder == null ? Abstract.zeroary(loc, empty) : remainder;

    for (IAbstract el : tail)
      reslt = Abstract.binary(loc, apnd, reslt, el);
    for (int ix = front.size(); ix > 0; ix--)
      reslt = Abstract.binary(loc, cons, front.get(ix - 1), reslt);
    return reslt;
  }

  private static IAbstract squareSequence(Location loc, IAbstract term, String cons, String apnd, String empty,
      ErrorReport errors)
  {
    if (term == null)
      return Abstract.zeroary(loc, empty);
    if (Abstract.isBinary(term, StandardNames.SCONS)) {
      IAbstract result = Abstract.binaryRhs(term);

      IAbstract front = Abstract.binaryLhs(term);

      if (Abstract.isBinary(front, StandardNames.ENDSCONS)) {
        errors.reportError(StringUtils.msg("not expecting ", front), front.getLoc(), term.getLoc());
      } else {
        for (IAbstract el : CompilerUtils.reverseUnwrap(front, StandardNames.COMMA)) {
          if (Abstract.isBinary(el, StandardNames.MAP_ARROW))
            result = Abstract.binary(loc, cons, Abstract.tupleTerm(el.getLoc(), Abstract.binaryLhs(el), Abstract
                .binaryRhs(el)), result);
          else
            result = Abstract.binary(loc, cons, el, result);
        }
      }
      return result;
    } else if (Abstract.isBinary(term, StandardNames.ENDSCONS)) {
      IAbstract result = Abstract.binaryLhs(term);
      for (IAbstract el : CompilerUtils.unWrap(Abstract.binaryRhs(term), StandardNames.COMMA)) {
        if (Abstract.isBinary(el, StandardNames.MAP_ARROW))
          result = Abstract.binary(loc, apnd, result, Abstract.tupleTerm(el.getLoc(), Abstract.binaryLhs(el), Abstract
              .binaryRhs(el)));
        else
          result = Abstract.binary(loc, apnd, result, el);
      }
      return result;
    } else {
      IAbstract result = Abstract.zeroary(loc, empty);
      for (IAbstract el : CompilerUtils.reverseUnwrap(term, StandardNames.COMMA)) {
        if (Abstract.isBinary(el, StandardNames.MAP_ARROW))
          result = Abstract.binary(loc, cons, Abstract.tupleTerm(el.getLoc(), Abstract.binaryLhs(el), Abstract
              .binaryRhs(el)), result);
        else
          result = Abstract.binary(loc, cons, el, result);
      }
      return result;
    }
  }

  @SuppressWarnings("unused")
  private IContentPattern regexpPtn(Location loc, String text, Wrapper<ICondition> condition, Dictionary cxt,
      ErrorReport errors, PtnVarHandler varHandler)
  {
    List<IContentPattern> subVars = new ArrayList<>();
    StringBuilder blder = new StringBuilder();
    findGroups(loc, new StringSequence(text), subVars, blder, condition, cxt, varHandler, errors);
    Set<Variable> vars = new TreeSet<>();
    NFA regexp = RegexpParse.regexpNFA(loc.offset(1, text.length()), text, errors, vars, cxt);

    return new RegExpPattern(loc, blder.toString(), regexp, subVars.toArray(new IContentPattern[subVars.size()]));
  }

  private void findGroups(Location loc, Sequencer<Integer> it, List<IContentPattern> subVars, StringBuilder blder,
      Wrapper<ICondition> condition, Dictionary cxt, PtnVarHandler varHandler, ErrorReport errors)
  {
    while (it.hasNext()) {
      int ch = it.next();
      switch (ch) {
      case '(':
        blder.appendCodePoint(ch);
        parseGroup(loc, it, subVars, blder, condition, cxt, errors, varHandler);
        continue;
      case ':':
      case ')':
        errors.reportError(StringUtils.msg("unexpected meta character: [" + (char) ch, "] in regular expression"), loc);
        continue;
      case '\\':
        charReference(loc, it, blder, errors);
        continue;
      case Tokenizer.QUOTE:
        blder.appendCodePoint(it.next());
        continue;
      default:
        blder.appendCodePoint(ch);
      }
    }
  }

  private void parseGroup(Location loc, Sequencer<Integer> it, List<IContentPattern> subVars, StringBuilder blder,
      Wrapper<ICondition> condition, Dictionary cxt, ErrorReport errors, PtnVarHandler varHandler)
  {
    int vNo = subVars.size();
    subVars.add(null);

    while (it.hasNext()) {
      int ch = it.next();
      switch (ch) {
      case '(':
        blder.appendCodePoint(ch);
        parseGroup(loc, it, subVars, blder, condition, cxt, errors, varHandler);
        continue;
      case ':': {
        StringBuilder var = new StringBuilder();
        int pos = it.index();
        while (it.hasNext() && Tokenizer.isIdentifierChar(ch = it.next()))
          var.appendCodePoint(ch);
        Location varLoc = loc.offset(pos + 1, var.length());
        subVars.set(vNo, typeOfPtn(new Name(varLoc, var.toString()), stringType, condition, cxt, cxt, varHandler));
        blder.appendCodePoint(ch);
        if (ch != ')')
          errors.reportError(StringUtils.msg("expecting a ')' in regular expression after variable name: ", var), loc);
        return;
      }

      case ')': {
        subVars.set(vNo, Variable.anonymous(loc, stringType));
        blder.appendCodePoint(ch);
        return;
      }
      case '\\':
        charReference(loc, it, blder, errors);
        continue;
      case Tokenizer.QUOTE:
        blder.appendCodePoint(it.next());
        continue;
      default:
        blder.appendCodePoint(ch);
      }
    }
  }

  public static void charReference(Location loc, Sequencer<Integer> it, StringBuilder bldr, ErrorReport errors)
  {
    int ch = it.next();
    switch (ch) {
    case 'b':
      bldr.append('\b');
      return;
    case 'e': // The escape character
      bldr.append('\33');
      return;
    case 'f': // Form feed
      bldr.append('\f');
      return;
    case 'n': // New line
      bldr.append('\n');
      return;
    case 'r': // Carriage return
      bldr.append('\r');
      return;
    case 't': // Tab
      bldr.append('\t');
      return;
    case '"': // Quote
      bldr.append('\"');
      return;
    case '$':
      bldr.append("\\$");
      return;
    case '\\': // Backslash itself
      bldr.append('\\');
      return;
    case 'd':
      bldr.append("\\d");
      return;
    case 'D':
      bldr.append("\\A");
      return;
    case 'F':
      bldr.append(NFA.floatRegexp);
      return;
    case 's':
      bldr.append("\\s");
      return;
    case 'S':
      bldr.append("\\S");
      return;
    case 'w':
      bldr.append("\\w");
      return;
    case 'W':
      bldr.append("\\W");
      return;
    case 'u': { // Start a hex sequence
      int hex = grabUnicode(loc, it, errors);

      bldr.appendCodePoint(hex);
      return;
    }
    default:
      bldr.append('\\');
      bldr.appendCodePoint(ch);
    }
  }

  public static int grabUnicode(Location loc, Sequencer<Integer> it, ErrorReport errors)
  {
    int X = 0;
    int ch = it.next();
    while (Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER || (ch >= 'a' && ch <= 'f')
        || (ch >= 'A' && ch <= 'F')) {
      X = X * HEX_RADIX + Character.digit(ch, HEX_RADIX);
      ch = it.next();
    }
    if (ch != ';')
      errors.reportError("invalid Unicode sequence", loc);
    return X;
  }

  private IType applyBodyType(IValueSpecifier cons)
  {
    IType conType = Freshen.freshenForUse(cons.getConType());
    IType argTypes[] = TypeUtils.getConstructorArgTypes(conType);

    if (cons instanceof RecordSpecifier) {
      SortedMap<String, IType> memberTypes = new TreeMap<>();
      RecordSpecifier aggCon = (RecordSpecifier) cons;
      for (Entry<String, Integer> entry : aggCon.getIndex().entrySet())
        memberTypes.put(entry.getKey(), argTypes[entry.getValue()]);

      return new TypeInterfaceType(memberTypes);
    } else
      return TypeUtils.tupleType(argTypes);
  }

  private List<IContentAction> checkAction(final IAbstract action, final IType actionType, final IType resultType,
      Dictionary cxt, Dictionary outer)
  {
    final Location loc = action.getLoc();

    if (CompilerUtils.isBlockTerm(action)) {
      Dictionary subCxt = cxt.fork();

      List<IContentAction> acts = new ArrayList<>();
      for (IAbstract act : CompilerUtils.unWrap(CompilerUtils.blockContent(action)))
        acts.addAll(checkAction(act, actionType, resultType, subCxt, outer));
      return acts;
    } else if (Abstract.isBinary(action, StandardNames.TERM) || Abstract.isUnary(action, StandardNames.TERM)) {
      List<IContentAction> acts = new ArrayList<>();
      for (IAbstract act : CompilerUtils.unWrap(action))
        acts.addAll(checkAction(act, actionType, resultType, cxt, outer));
      return acts;
    } else if (Abstract.isName(action, StandardNames.NOTHING) || CompilerUtils.isEmptyBlock(action))
      return FixedList.create();
    else if (CompilerUtils.isAssert(action)) {
      Dictionary sCxt = cxt.fork();
      Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(CompilerUtils.asserted(action),
          sCxt, outer);
      ICondition cond = condInfo.left();

      if (QueryPlanner.isTransformable(cond)) {
        return FixedList.create((IContentAction) new AssertAction(loc, QueryPlanner.transformCondition(cond, condInfo
            .right(), CompilerUtils.trueLiteral(loc), CompilerUtils.falseLiteral(loc), cxt, outer, errors)));
      } else
        return FixedList.create((IContentAction) new AssertAction(loc, new ContentCondition(loc, cond)));
    } else if (CompilerUtils.isIgnore(action)) {
      IContentExpression ignored = typeOfExp(CompilerUtils.ignored(action), new TypeVar(), cxt, outer);

      return FixedList.create((IContentAction) new Ignore(loc, ignored));
    } else if (CompilerUtils.isPerformAction(action)) {
      TypeVar monadType = TypeVar.var("%%m", 1, readWrite);
      IType actType = TypeUtils.typeExp(monadType, unitType);
      IType exType = StandardTypes.exceptionType;
      monadType.setConstraint(new ContractConstraint(Computations.EXECUTION, monadType));

      IContentExpression performed = typeOfExp(CompilerUtils.performedAction(action), actType, cxt, outer);

      if (!CompilerUtils.isBasicPerform(action)) {
        IType abortType = TypeUtils.functionType(exType, unitType);

        List<Pair<IContentPattern, IContentAction>> cases = checkCaseBranches(loc,
            CompilerUtils.performedAbort(action), exType, resultType, cxt);
        Variable exceptionVar = new Variable(loc, exType, StandardNames.EXCEPTION);
        IContentAction handler = MatchCompiler.generateCaseAction(loc, exceptionVar, cases, cxt, outer, errors);

        FunctionLiteral handlerFun = new FunctionLiteral(loc, GenSym.genSym("perform_"), abortType,
            new IContentPattern[] { exceptionVar }, new ValofExp(loc, unitType, handler, new ValisAction(loc,
                new VoidExp(loc))), FreeVariables.findFreeVars(handler, cxt));
        IContentAction body = new Ignore(loc, Computations.perform(loc, monadType, performed, handlerFun, cxt, errors));

        return FixedList.create((IContentAction) new ExceptionHandler(loc, body, handler));
      } else
        return FixedList.create((IContentAction) new Ignore(loc, Computations.perform(loc, monadType, performed, cxt,
            errors)));
    } else if (CompilerUtils.isYield(action)) {
      List<IContentAction> yield = checkAction(CompilerUtils.yielded(action), actionType, resultType, cxt, outer);

      if (yield.size() == 1)
        return FixedList.create((IContentAction) new Yield(loc, yield.get(0)));
      else
        return FixedList.create((IContentAction) new Yield(loc, new Sequence(loc, resultType, yield)));
    } else if (CompilerUtils.isTypeAnnotation(action)) {
      IType type = TypeParser.parseType(CompilerUtils.typeAnnotation(action), cxt, errors, readWrite);

      if (type != null) {
        IAbstract id = CompilerUtils.typeAnnotatedTerm(action);
        if (Abstract.isIdentifier(id)) {
          String vrName = Abstract.getId(id);
          cxt.declareVar(vrName, Variable.create(loc, type, vrName), AccessMode.unknown, Visibility.priVate, false);
        } else
          errors.reportError(StringUtils.msg("expecting an identifier, not ", id), loc);
      } else
        errors.reportError(StringUtils.msg("type annotation ", action, " does not parse"), loc);
      return FixedList.create();
    } else if (CompilerUtils.isVarDeclaration(action)) {
      IAbstract varPtn = CompilerUtils.varDeclarationPattern(action);
      IAbstract varValue = CompilerUtils.varDeclarationExpression(action);

      if (CompilerUtils.isVarPtn(varPtn)) {
        String vrName = Abstract.getId(CompilerUtils.varPtnVar(varPtn));

        DictInfo oVar = cxt.getVar(vrName);
        IType declType = new TypeVar();

        if (oVar != null && oVar.getAccess() != AccessMode.unknown && cxt.isLocallyDeclared(vrName, outer)) {
          errors.reportError(StringUtils.msg(vrName + " already defined at ", oVar.getLoc()), loc);
        } else if (oVar != null)
          declType = oVar.getType();

        if (TypeUtils.isReferenceType(declType))
          declType = TypeUtils.referencedType(declType);
        else if (!TypeUtils.isTypeVar(declType))
          errors.reportError(StringUtils.msg("type should be a reference type, not ", declType), loc);

        IContentExpression value = typeOfExp(varValue, declType, cxt.fork(), outer);

        Variable var = Variable.create(loc, TypeUtils.referenceType(declType), vrName);
        cxt.declareVar(vrName, var, readWrite, Visibility.priVate, true);
        return FixedList.create((IContentAction) VarDeclaration.varDecl(loc, var, value));
      } else {
        errors.reportError(StringUtils.msg("lhs of declaration should be an identifier: ", action), loc);
        return FixedList.create();
      }
    } else if (CompilerUtils.isIsStatement(action)) {
      IAbstract varPtn = CompilerUtils.isStmtPattern(action);
      IAbstract varValue = CompilerUtils.isStmtValue(action);

      IType declType = new TypeVar();

      Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
      IContentPattern var = typeOfPtn(varPtn, declType, condition, cxt, outer, new RuleVarHandler(readOnly,
          Visibility.priVate, outer, errors));

      if (!CompilerUtils.isTrivial(condition.get()))
        errors.reportError(StringUtils.msg("not permitted to have semantic condition ", condition.get(), " here"), loc);

      IContentExpression value = typeOfExp(varValue, declType, cxt.fork(), outer);

      VarDeclaration decl = new VarDeclaration(loc, var, readOnly, value);

      ConsList<VarDeclaration> extra = ConsList.nil();

      extra = establishInterfaceType(var, cxt, extra, errors);

      if (extra.isNil())
        return FixedList.create((IContentAction) decl);
      else {
        IContentAction[] decls = new IContentAction[extra.length() + 1];
        decls[0] = decl;
        int ix = 1;
        for (VarDeclaration d : extra) {
          decls[ix++] = d;
          Variable v = (Variable) d.getPattern();
          cxt.declareVar(v.getName(), v, AccessMode.readOnly, Visibility.priVate, true);
        }
        return FixedList.create(decls);
      }
    } else if (CompilerUtils.isLetTerm(action)) {
      Dictionary thetaCxt = cxt.fork();

      BoundChecker<IContentAction> checker = new BoundChecker<IContentAction>() {

        @Override
        public IContentAction typeBound(List<IStatement> definitions, List<IContentAction> localActions,
            Over overloader, Dictionary thetaCxt, Dictionary dict)
        {
          IContentAction bndAction = Over.resolve(thetaCxt, errors, pickAction(loc, resultType, checkAction(
              CompilerUtils.letBound(action), actionType, resultType, thetaCxt, thetaCxt)));

          if (!localActions.isEmpty()) {
            localActions.add(bndAction);
            return new LetAction(loc, definitions, new Sequence(loc, bndAction.getType(), localActions));
          } else
            return new LetAction(loc, definitions, bndAction);
        }
      };
      return FixedList.create(checkTheta(CompilerUtils.letDefs(action), thetaCxt, cxt, new TypeVar(), checker));
    } else if (Abstract.isUnary(action, StandardNames.VALIS)) {
      IContentExpression value = typeOfExp(Abstract.unaryArg(action), resultType, cxt, outer);

      return FixedList.create((IContentAction) new ValisAction(loc, value));
    } else if (CompilerUtils.isAssignment(action)) {
      IType vType = new TypeVar();
      IContentExpression lvalue = lvalueType(CompilerUtils.assignedVar(action), TypeUtils.referenceType(vType), cxt,
          outer);
      IContentExpression value = typeOfExp(CompilerUtils.assignedValue(action), vType, cxt, outer);
      return FixedList.create((IContentAction) new Assignment(loc, lvalue, value));
    } else if (CompilerUtils.isSyncAction(action)) {
      IContentExpression sel = typeOfExp(CompilerUtils.syncActionSel(action), new TypeVar(), cxt, outer);

      IAbstract body = CompilerUtils.syncActionBody(action);
      if (CompilerUtils.isConditionalSync(action)) {
        Map<ICondition, IContentAction> entries = new HashMap<>();
        for (IAbstract stmt : CompilerUtils.unWrap(body)) {
          if (CompilerUtils.isSyncCondition(stmt)) {
            Dictionary caseCxt = cxt.fork();

            Triple<ICondition, List<Variable>, List<Variable>> predInfo = typeOfCondition(CompilerUtils
                .syncConditionCondition(stmt), caseCxt, outer);
            ICondition predicate = predInfo.left();
            List<IContentAction> acts = checkAction(CompilerUtils.syncConditionAction(stmt), actionType, resultType,
                caseCxt, outer);
            entries.put(predicate, pickAction(loc, resultType, acts));
          } else
            errors.reportError(StringUtils.msg("expecting a conditional sync action, not ", stmt), stmt.getLoc());
        }
        return FixedList.create((IContentAction) new SyncAction(loc, resultType, sel, entries));
      } else
        return FixedList.create((IContentAction) new SyncAction(loc, resultType, sel, pickAction(loc, resultType,
            checkAction(body, actionType, resultType, cxt, outer))));
    } else if (CompilerUtils.isAbortHandler(action)) {
      Dictionary subCxt = cxt.fork();
      IContentAction body = pickAction(loc, resultType, checkAction(CompilerUtils.abortHandlerBody(action), actionType,
          resultType, subCxt, outer));
      IType exceptionType = StandardTypes.exceptionType;

      TypeVar monadType = TypeVar.var("%%m", 1, readWrite);
      IType actType = TypeUtils.typeExp(monadType, resultType);
      monadType.setConstraint(new ContractConstraint(Computations.COMPUTATION, monadType));

      try {
        Subsume.subsume(actType, TypeUtils.typeExp(actionType, resultType), loc, cxt);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("computation type: ", actionType, " is not consistent\nbecause ", e
            .getWords()), merge(loc, e.getLocs()));
      }

      List<Pair<IContentPattern, IContentAction>> cases = checkCaseBranches(loc, CompilerUtils
          .abortHandlerHandler(action), exceptionType, resultType, cxt);
      Variable exceptionVar = new Variable(loc, exceptionType, StandardNames.EXCEPTION);
      IContentAction handler = MatchCompiler.generateCaseAction(loc, exceptionVar, cases, cxt, outer, errors);
      return FixedList.create((IContentAction) new ExceptionHandler(loc, body, handler));
    } else if (CompilerUtils.isRaise(action)) {
      IContentExpression code = typeOfExp(CompilerUtils.raisedCode(action), stringType, cxt, outer);
      IContentExpression raised = typeOfExp(CompilerUtils.raisedException(action), new TypeVar(), cxt, outer);

      IContentExpression location = typeOfExp(new Name(loc, StandardNames.MACRO_LOCATION), StandardTypes.locationType,
          cxt, outer);
      IContentExpression ex = new ConstructorTerm(loc, EvaluationException.name, StandardTypes.exceptionType, code,
          raised, location);
      return FixedList.create((IContentAction) new RaiseAction(loc, ex));
    } else if (CompilerUtils.isForLoop(action)) {
      Dictionary loopCxt = cxt.fork();
      Triple<ICondition, List<Variable>, List<Variable>> cond = typeOfCondition(CompilerUtils.forLoopCond(action),
          loopCxt, outer);
      IContentAction body = pickAction(loc, resultType, checkAction(CompilerUtils.forLoopBody(action), actionType,
          resultType, loopCxt, outer));

      if (!TypeUtils.isType(actionType, StandardNames.ACTION))
        return FixedList
            .create((IContentAction) new ForLoopAction(loc, cond.left(), cond.right(), cond.middle(), body));
      else {
        IContentExpression loopExp = QueryPlanner.transformForLoop(action.getLoc(), cond.right(), cond.left(), body,
            resultType, resultType, actionType, cxt, outer, errors);

        List<Pair<IContentPattern, IContentAction>> cases = new ArrayList<>();
        if (!resultType.equals(Computations.unitType)) {
          Variable resltVar = new Variable(loc, resultType, GenSym.genSym());
          cases.add(Pair.pair((IContentPattern) CompilerUtils.noMorePtn(loc, resltVar),
              (IContentAction) new ValisAction(loc, resltVar)));
        }

        if (!cases.isEmpty())
          return FixedList.create((IContentAction) MatchCompiler.generateCaseAction(loc, loopExp, cases, cxt, outer,
              errors));
        else
          return FixedList.create((IContentAction) new Ignore(loc, loopExp));
      }
    } else if (Abstract.isBinary(action, StandardNames.DO)
        && Abstract.isUnary(Abstract.getArg(action, 0), StandardNames.WHILE)) {
      IAbstract cndTerm = Abstract.argPath(action, 0, 0);
      IAbstract bdyTerm = Abstract.getArg(action, 1);

      Dictionary whileCxt = cxt.fork();
      Triple<ICondition, List<Variable>, List<Variable>> cond = typeOfCondition(cndTerm, whileCxt, outer);
      List<Variable> free = cond.middle();
      List<Variable> definedVars = cond.right();

      IContentAction body = pickAction(loc, resultType, checkAction(bdyTerm, actionType, resultType, whileCxt, outer));

      if (QueryPlanner.isTransformable(cond.left())) {
        /**
         * Change to:
         * 
         * <pre>
         * while (any of Some(F[]) where <cond> default None) matches Some(F[]) do <body>
         * </pre>
         * 
         * where F[] are the free variables in <then> that may be bound by <cond>
         */

        if (!definedVars.isEmpty()) {
          List<IContentExpression> freeExps = new ArrayList<IContentExpression>(definedVars);
          IContentExpression freeTpl = new ConstructorTerm(loc, freeExps);

          IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, freeTpl.getType());
          IContentExpression reslt = CompilerUtils.possible(loc, freeTpl);
          IContentExpression deflt = CompilerUtils.impossible(loc, freeTpl.getType());

          IContentExpression test = QueryPlanner.transformReferenceExpression(cond.left(), free, whileCxt, outer,
              reslt, deflt, resltType, loc, errors);

          ICondition matches = new Matches(loc, test, CompilerUtils.possiblePtn(loc, resltType, ConstructorPtn
              .tuplePtn(loc, definedVars.toArray(new IContentPattern[definedVars.size()]))));
          return FixedList.create((IContentAction) new WhileAction(loc, matches, body));
        } else {
          IContentExpression ok = CompilerUtils.trueLiteral(loc);
          IContentExpression notOk = CompilerUtils.falseLiteral(loc);
          IContentExpression test = QueryPlanner.transformReferenceExpression(cond.left(), free, whileCxt, outer, ok,
              notOk, StandardTypes.booleanType, loc, errors);
          return FixedList.create((IContentAction) new WhileAction(loc, new IsTrue(loc, test), body));
        }
      } else
        return FixedList.create((IContentAction) new WhileAction(loc, cond.left(), body));
    } else if (Abstract.isBinary(action, StandardNames.ELSE)) {
      IAbstract ifPart = Abstract.binaryLhs(action);
      IAbstract thPart = null;
      IAbstract elPart = Abstract.binaryRhs(action);

      if (Abstract.isBinary(ifPart, StandardNames.THEN)) {
        thPart = Abstract.getArg(ifPart, 1);
        ifPart = Abstract.getArg(ifPart, 0);
      } else
        errors.reportError(StringUtils.msg("missing then part of conditional"), loc);

      if (Abstract.isUnary(ifPart, StandardNames.IF))
        ifPart = Abstract.unaryArg(ifPart);
      else
        errors.reportError(StringUtils.msg("missing if part of conditional"), loc);

      Dictionary ifCxt = cxt.fork();
      Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(ifPart, ifCxt, outer);
      ICondition cond = condInfo.left();
      List<Variable> free = condInfo.middle();
      List<Variable> definedVars = condInfo.right();

      IContentAction thAct = pickAction(loc, resultType, checkAction(thPart, actionType, resultType, ifCxt, outer));
      IContentAction elAct = (elPart != null ? pickAction(loc, resultType, checkAction(elPart, actionType, resultType,
          cxt, outer)) : new NullAction(loc, resultType));

      if (QueryPlanner.isTransformable(cond)) {
        /**
         * Change to:
         * 
         * <pre>
         * if (any of Some(F[]) where <cond> default None) matches Some(F[]) then <then> else <else>
         * </pre>
         * 
         * where F[] are the free variables in <then> that may be bound by <cond>
         */

        if (!definedVars.isEmpty()) {
          List<IContentExpression> freeExps = new ArrayList<IContentExpression>(definedVars);
          IContentExpression freeTpl = new ConstructorTerm(loc, freeExps);

          IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, freeTpl.getType());
          IContentExpression reslt = CompilerUtils.possible(loc, freeTpl);
          IContentExpression deflt = CompilerUtils.impossible(loc, freeTpl.getType());

          IContentExpression test = QueryPlanner.transformReferenceExpression(cond, free, ifCxt, outer, reslt, deflt,
              resltType, loc, errors);

          ICondition matches = new Matches(loc, test, CompilerUtils.possiblePtn(loc, resltType, ConstructorPtn
              .tuplePtn(loc, definedVars.toArray(new IContentPattern[definedVars.size()]))));
          return FixedList.create((IContentAction) new ConditionalAction(loc, matches, thAct, elAct));
        } else {
          IContentExpression ok = CompilerUtils.trueLiteral(loc);
          IContentExpression notOk = CompilerUtils.falseLiteral(loc);
          IContentExpression test = QueryPlanner.transformReferenceExpression(cond, free, ifCxt, outer, ok, notOk,
              StandardTypes.booleanType, loc, errors);
          return FixedList.create((IContentAction) new ConditionalAction(loc, new IsTrue(loc, test), thAct, elAct));
        }
      } else
        return FixedList.create((IContentAction) new ConditionalAction(loc, cond, thAct, elAct));
    } else if (Abstract.isBinary(action, StandardNames.THEN)) {
      IAbstract ifPart = Abstract.binaryLhs(action);
      IAbstract thPart = Abstract.binaryRhs(action);
      IContentAction nothing = new NullAction(loc, unitType);

      if (Abstract.isUnary(ifPart, StandardNames.IF))
        ifPart = Abstract.unaryArg(ifPart);

      Dictionary ifCxt = cxt.fork();
      Triple<ICondition, List<Variable>, List<Variable>> condInfo = typeOfCondition(ifPart, ifCxt, outer);
      ICondition cond = condInfo.left();
      List<Variable> free = condInfo.middle();
      List<Variable> definedVars = condInfo.right();

      IContentAction thAct = pickAction(loc, resultType, checkAction(thPart, actionType, resultType, ifCxt, outer));

      if (QueryPlanner.isTransformable(cond)) {
        if (definedVars.size() > 1) {
          List<IContentExpression> freeExps = new ArrayList<IContentExpression>(definedVars);
          IContentExpression freeTpl = new ConstructorTerm(loc, freeExps);
          IContentPattern freeMtch = new ConstructorPtn(loc, definedVars
              .toArray(new IContentPattern[definedVars.size()]));

          IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, freeTpl.getType());
          IContentExpression reslt = CompilerUtils.possible(loc, freeTpl);
          IContentExpression deflt = CompilerUtils.impossible(loc, freeTpl.getType());

          IContentExpression test = QueryPlanner.transformReferenceExpression(cond, free, ifCxt, outer, reslt, deflt,
              resltType, loc, errors);

          ICondition matches = new Matches(loc, test, CompilerUtils.possiblePtn(loc, resltType, freeMtch));
          return FixedList.create((IContentAction) new ConditionalAction(loc, matches, thAct, nothing));
        } else if (definedVars.size() == 1) {
          IContentExpression freeTpl = definedVars.get(0);
          IContentPattern freeMtch = definedVars.get(0);

          IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, freeTpl.getType());
          IContentExpression reslt = CompilerUtils.possible(loc, freeTpl);
          IContentExpression deflt = CompilerUtils.impossible(loc, freeTpl.getType());

          IContentExpression test = QueryPlanner.transformReferenceExpression(cond, free, ifCxt, outer, reslt, deflt,
              resltType, loc, errors);

          ICondition matches = new Matches(loc, test, CompilerUtils.possiblePtn(loc, resltType, freeMtch));
          return FixedList.create((IContentAction) new ConditionalAction(loc, matches, thAct, nothing));
        } else {
          IContentExpression ok = CompilerUtils.trueLiteral(loc);
          IContentExpression notOk = CompilerUtils.falseLiteral(loc);
          IContentExpression test = QueryPlanner.transformReferenceExpression(cond, free, ifCxt, outer, ok, notOk,
              StandardTypes.booleanType, loc, errors);
          return FixedList.create((IContentAction) new ConditionalAction(loc, new IsTrue(loc, test), thAct, nothing));
        }
      } else
        return FixedList.create((IContentAction) new ConditionalAction(loc, cond, thAct, nothing));
    }
    // refactor the form X.f(e1,..,en) to (X.f)(e1,..,en)
    else if (Abstract.isBinary(action, StandardNames.PERIOD) && Abstract.binaryRhs(action) instanceof Apply) {
      Apply act = (Apply) Abstract.binaryRhs(action);
      IAbstract reform = new Apply(loc, Abstract.binary(loc, StandardNames.PERIOD, Abstract.binaryLhs(action), act
          .getOperator()), act.getArgs());
      return checkAction(reform, actionType, resultType, cxt, outer);
    } else if (CompilerUtils.isCaseTerm(action)) {
      IAbstract selTerm = CompilerUtils.caseSel(action);
      IAbstract caseTerms = CompilerUtils.caseRules(action);

      IType selectorType = new TypeVar();
      IContentExpression selector = typeOfExp(selTerm, selectorType, cxt, outer);
      List<Pair<IContentPattern, IContentAction>> cases = checkCaseBranches(loc, caseTerms, selectorType, resultType,
          cxt);

      if (!CompilerUtils.isComputational(selector))
        return FixedList.create((IContentAction) MatchCompiler.generateCaseAction(loc, selector, cases, cxt, outer,
            errors));
      else {
        Variable tmp = Variable.create(selector.getLoc(), selector.getType(), GenSym.genSym("XX"));
        return FixedList.create(new VarDeclaration(selector.getLoc(), tmp, readOnly, selector), MatchCompiler
            .generateCaseAction(selector.getLoc(), tmp, cases, cxt, outer, errors));
      }
    } else if (action instanceof Apply) {
      Apply apply = (Apply) action;

      IList args = apply.getArgs();
      int arity = args.size();

      if (StandardNames.isKeyword(apply.getOperator()))
        errors.reportError(StringUtils.msg("unexpected keyword: ", apply.getOperator(), " in action"), loc);

      IType argTypes[] = new IType[arity];

      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = new TypeVar();

      IContentExpression proc = typeOfExp(apply.getOperator(), TypeUtils.procedureType(argTypes), cxt, outer);

      IContentExpression argTpl[] = argTuple(args, argTypes, cxt, outer);

      if (proc instanceof Variable && JavaImport.isJavaName(((Variable) proc).getName()))
        return FixedList.create((IContentAction) new ProcedureCallAction(loc, proc, argTpl));
      else
        return FixedList.create((IContentAction) new Ignore(loc, new Application(loc, unitType, proc, argTpl)));
    } else {
      errors.reportError(StringUtils.msg("action ", action, " not understood"), loc);
      return FixedList.create();
    }
  }

  private static IContentAction pickAction(Location loc, IType type, List<IContentAction> acts)
  {
    if (acts.size() == 1)
      return acts.get(0);
    else
      return new Sequence(loc, type, acts);
  }

  private ConsList<VarDeclaration> establishInterfaceType(IContentPattern lhs, Dictionary dict,
      ConsList<VarDeclaration> extra, ErrorReport errors)
  {
    if (lhs instanceof Variable) {
      Variable v = (Variable) lhs;
      DictInfo info = dict.getVar(v.getName());
      if (info instanceof VarInfo) {
        VarInfo var = (VarInfo) info;

        var.getFace(errors, dict);
      }
    } else if (lhs instanceof ConstructorPtn) {
      ConstructorPtn con = (ConstructorPtn) lhs;

      for (int ix = 0; ix < con.arity(); ix++) {
        IContentPattern arg = con.getArg(ix);

        extra = establishInterfaceType(arg, dict, extra, errors);
      }
      return extra;
    } else
      return extra;
    return extra;
  }

  private List<Pair<IContentPattern, IContentAction>> checkCaseBranches(Location loc, IAbstract caseTerms,
      IType selectorType, IType resultType, Dictionary dict)
  {
    Pair<IContentPattern, IContentAction> deflt = Pair.pair((IContentPattern) Variable.anonymous(loc, new TypeVar()),
        (IContentAction) new NullAction(loc, resultType));

    List<Pair<IContentPattern, IContentAction>> cases = new ArrayList<>();

    for (IAbstract el : CompilerUtils.unWrap(caseTerms)) {
      if (CompilerUtils.isDefaultRule(el)) {
        Wrapper<ICondition> cond = new Wrapper<>(CompilerUtils.truth);
        Dictionary caseCxt = dict.fork();
        IContentPattern ptn = typeOfPtn(CompilerUtils.defaultRulePtn(el), selectorType, cond, caseCxt, dict,
            new RuleVarHandler(dict, errors));

        IContentAction caseArm = pickAction(loc, resultType, checkAction(CompilerUtils.caseRuleValue(el), actionType,
            resultType, caseCxt, dict));
        if (!CompilerUtils.isTrivial(cond.get()))
          ptn = new WherePattern(loc, ptn, cond.get());
        deflt = Pair.pair(ptn, caseArm);
      } else if (CompilerUtils.isCaseRule(el)) {
        Wrapper<ICondition> cond = new Wrapper<>(CompilerUtils.truth);
        Dictionary caseCxt = dict.fork();
        IContentPattern ptn = typeOfPtn(CompilerUtils.caseRulePtn(el), selectorType, cond, caseCxt, dict,
            new RuleVarHandler(dict, errors));
        IContentAction caseArm = pickAction(loc, resultType, checkAction(CompilerUtils.caseRuleValue(el), actionType,
            resultType, caseCxt, dict));

        if (!CompilerUtils.isTrivial(cond.get()))
          ptn = new WherePattern(loc, ptn, cond.get());
        cases.add(Pair.pair(ptn, caseArm));
      } else
        errors.reportError(StringUtils.msg("expecting a case clause, not ", el), el.getLoc());
    }

    cases.add(deflt);
    return cases;
  }

  // Look for a valis action
  public static boolean hasValis(IContentAction act)
  {
    if (act instanceof ValisAction)
      return true;
    else if (act instanceof Sequence) {
      Sequence seq = (Sequence) act;
      for (IContentAction stmt : seq)
        if (hasValis(stmt))
          return true;
      return false;
    } else if (act instanceof ConditionalAction) {
      ConditionalAction cond = (ConditionalAction) act;
      return hasValis(cond.getThPart()) && hasValis(cond.getElPart());
    } else if (act instanceof CaseAction) {
      CaseAction cse = (CaseAction) act;
      for (Pair<IContentPattern, IContentAction> entry : cse.getCases()) {
        if (!hasValis(entry.getValue()))
          return false;
      }
      return cse.getDeflt() != null && hasValis(cse.getDeflt());
    } else if (act instanceof LetAction)
      return hasValis(((LetAction) act).getBoundAction());
    else if (act instanceof ExceptionHandler) {
      ExceptionHandler handler = (ExceptionHandler) act;
      return hasValis(handler.getBody());
    } else if (act instanceof RaiseAction)
      return true;
    else if (act instanceof Yield)
      return hasValis(((Yield) act).getYielded());
    else if (act instanceof WhileAction) {
      WhileAction loop = (WhileAction) act;
      return CompilerUtils.isTrivial(loop.getControl()) || hasValis(loop.getBody());
    } else if (act instanceof SyncAction) {
      SyncAction sync = (SyncAction) act;

      for (Entry<ICondition, IContentAction> entry : sync.getBody().entrySet()) {
        if (!hasValis(entry.getValue()))
          return false;
      }
      return true;
    }

    return false;
  }

  public static boolean hasValis(List<IContentAction> actions)
  {
    for (IContentAction act : actions)
      if (hasValis(act))
        return true;
    return false;
  }

  protected interface BoundChecker<T>
  {
    T typeBound(List<IStatement> definitions, List<IContentAction> localActions, Over overloader, Dictionary thetaCxt,
        Dictionary dict);
  }

  private <T> T checkTheta(IAbstract theta, final Dictionary thetaDict, Dictionary dict, IType face,
      BoundChecker<T> checker)
  {
    List<IStatement> thetaElements = new ArrayList<>();
    List<IContentAction> localActions = new ArrayList<>();

    if (theta != null) {
      final Location loc = theta.getLoc();

      IAbstract eqTheta = EqualityBuilder.checkForEqualities(theta, dict);
      IAbstract dispTheta = DisplayBuilder.checkForDisplay(eqTheta, dict);
      // IAbstract quoteTheta = QuoteBuilder.checkForQuoting(dispTheta, dict);

      Pair<List<IAbstract>, List<IAbstract>> parts = splitOffImports(dispTheta);

      DependencyResults dependencies = Dependencies.dependencies(errors, parts.right);

      List<IAbstract> others = dependencies.getOthers();
      if (others != null && !others.isEmpty()) {
        for (IAbstract el : others)
          if (!Abstract.isUnary(el, StandardNames.META_HASH))
            errors.reportError(StringUtils.msg("non-valid element ", el, " found"), el.getLoc());
      }

      List<List<Definition>> programGroups = dependencies.getDefinitions();
      Map<String, IType> declaredFields = new HashMap<>();
      TypeInterface methods = (TypeInterface) TypeUtils.interfaceOfType(loc, face, thetaDict);

      declaredFields.putAll(methods.getAllFields());

      for (Entry<String, IType> entry : methods.getAllTypes().entrySet())
        thetaDict.defineType(new TypeExists(loc, entry.getKey(), entry.getValue()));

      for (IAbstract stmt : parts.left) {
        if (CompilerUtils.isNamedImport(stmt))
          namedImportPkg(stmt, thetaDict, thetaElements, Visibility.pUblic);
        else if (CompilerUtils.isImport(stmt))
          openImportPkg(stmt, thetaDict, thetaElements, Visibility.pUblic);
        else if (CompilerUtils.isJavaStmt(stmt))
          importJava(stmt, thetaDict, thetaElements, Visibility.pUblic);
        else
          errors.reportError(StringUtils.msg("(internal) cannot understand ", stmt), stmt.getLoc());
      }

      for (List<Definition> group : programGroups) {
        switch (validateGroup(group)) {
        case type:
          declareTypes(group, thetaDict, dict);
          break;

        case contract:
          declareContract(group, thetaDict);
          break;
        default:
        }
      }

      lookupImplementations(programGroups, thetaDict, errors);

      for (List<Definition> group : programGroups) {
        DefinitionKind kind = validateGroup(group);

        switch (kind) {
        case variable:
          variableGroup(thetaDict, group, dependencies.getTypeAnnotations(), thetaElements, declaredFields);
          break;

        case type:
          typeGroup(group, thetaElements, thetaDict);
          break;

        case contract:
          defineContracts(group, thetaElements, thetaDict, dict);
          break;

        case implementation:
          implementationGroup(group, thetaElements, thetaDict, dict);
          break;

        case unknown:
          errors.reportError(StringUtils.msg("mutually recursive group ", group, " has incompatible forms of program"),
              group.get(0).getLoc());
          break;

        case imports:
          openRecord(group.get(0).get(), thetaDict, thetaElements, Visibility.pUblic);
          break;

        default:
          errors.reportError(StringUtils.msg("(internal): unexpected var kind: ", kind), group.get(0).getLoc());
          break;
        }
      }

      // Resolve method overloading
      Over overloader = new Over();
      Dictionary subTheta = thetaDict.fork();
      List<IStatement> overloadedTheta = overloader.overloadTheta(errors, thetaElements, thetaDict);
      OverContext overCxt = new OverContext(subTheta, errors, 0);

      for (IAbstract local : dependencies.getLocalActions()) {
        List<IContentAction> actions = checkAction(local, actionType, unitType, thetaDict, dict);
        for (IContentAction action : actions)
          localActions.add(action.transform(overloader, overCxt));
      }

      return checker.typeBound(overloadedTheta, localActions, overloader, thetaDict, dict);
    } else {
      return checker.typeBound(thetaElements, localActions, new Over(), thetaDict, dict);
    }
  }

  private Pair<List<IAbstract>, List<IAbstract>> splitOffImports(IAbstract theta)
  {
    List<IAbstract> imports = new ArrayList<>();
    List<IAbstract> others = new ArrayList<>();

    for (IAbstract stmt : CompilerUtils.unWrap(theta)) {
      if (CompilerUtils.isImport(stmt) || CompilerUtils.isNamedImport(stmt) || CompilerUtils.isJavaStmt(stmt))
        imports.add(stmt);
      else
        others.add(stmt);
    }

    return Pair.pair(imports, others);
  }

  private static void sealInterface(IType face, Map<String, IContentExpression> elements, List<IStatement> defs,
      Dictionary dict, ErrorReport errors, Location loc)
  {
    SortedMap<String, IType> fieldTypes = new TreeMap<>();
    SortedMap<String, IType> localTypes = new TreeMap<>();

    Exporter exporter = new Exporter(dict, fieldTypes, localTypes);

    for (IStatement stmt : defs) {
      if (stmt.getVisibility() == Visibility.pUblic)
        stmt.accept(exporter);
    }

    for (Entry<String, IContentExpression> entry : elements.entrySet()) {
      if (!fieldTypes.containsKey(entry.getKey()))
        fieldTypes.put(entry.getKey(), entry.getValue().getType());
    }

    TypeInterfaceType thetaFace = new TypeInterfaceType(localTypes, fieldTypes);

    try {
      Subsume.subsume(face, thetaFace, null, dict);
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg("public interface ", face, " not consistent with actual code\nbecause ", e
          .getWords()), loc);
    }
  }

  private static class Exporter extends DefaultVisitor
  {
    private final Dictionary dict;
    private final Map<String, IType> fieldTypes;
    private final Map<String, IType> localTypes;
    private final Map<String, Location[]> fieldLocs = new HashMap<>();
    private final Map<String, Location[]> typeLocs = new HashMap<>();

    protected Exporter(Dictionary dict, Map<String, IType> fieldTypes, Map<String, IType> localTypes)
    {
      super(false);
      this.dict = dict;
      this.fieldTypes = fieldTypes;
      this.localTypes = localTypes;
    }

    @Override
    public void visitContractEntry(ContractEntry contract)
    {
      IAlgebraicType contractType = contract.getContract().getContractType();

      addTypeEntry(contract.getLoc(), contract.getName(), new Type(contract.getName(), Kind.kind(contractType
          .typeArity())));
      Location loc = contract.getLoc();

      for (IValueSpecifier spec : contractType.getValueSpecifiers())
        addFieldEntry(loc, spec.getLabel(), spec.getConType());
    }

    @Override
    public void visitContractImplementation(ImplementationEntry impl)
    {
    }

    @Override
    public void visitImportEntry(ImportEntry entry)
    {
      TypeInterfaceType imported = (TypeInterfaceType) TypeUtils.interfaceOfType(entry.getLoc(), entry.getPkgType(),
          dict);
      Location loc = entry.getLoc();
      for (Entry<String, IType> field : imported.getAllFields().entrySet())
        addFieldEntry(loc, field.getKey(), field.getValue());
      for (Entry<String, IType> type : imported.getAllTypes().entrySet())
        addTypeEntry(loc, type.getKey(), type.getValue());
    }

    @Override
    public void visitTypeAliasEntry(TypeAliasEntry entry)
    {
    }

    @Override
    public void visitTypeEntry(TypeDefinition type)
    {
      if (!type.isFromContract()) {
        Location loc = type.getLoc();

        IAlgebraicType def = type.getTypeDescription();
        addTypeEntry(loc, type.getName(), new Type(def.getName(), Kind.kind(def.typeArity())));
        for (IValueSpecifier spec : type.getTypeDescription().getValueSpecifiers())
          addFieldEntry(loc, spec.getLabel(), spec.getConType());
      }
    }

    @Override
    public void visitTypeWitness(TypeWitness witness)
    {
      addTypeEntry(witness.getLoc(), witness.getType().typeLabel(), witness.getWitness());
    }

    @Override
    public void visitVarEntry(VarEntry var)
    {
      var.getVarPattern().accept(this);
    }

    @Override
    public void visitRecordPtn(RecordPtn record)
    {
      for (IContentPattern arg : record.getElements().values())
        arg.accept(this);
    }

    @Override
    public void visitTuplePtn(ConstructorPtn tpl)
    {
      for (IContentPattern arg : tpl.getElements())
        arg.accept(this);
    }

    @Override
    public void visitPatternApplication(PatternApplication apply)
    {
      apply.getArg().accept(this);
    }

    @Override
    public void visitVariable(Variable variable)
    {
      addFieldEntry(variable.getLoc(), variable.getName(), variable.getType());
    }

    @Override
    public void visitOverloadedVariable(OverloadedVariable over)
    {
      addFieldEntry(over.getLoc(), over.getName(), over.getType());
    }

    @Override
    public void visitWherePattern(WherePattern where)
    {
      where.getPtn().accept(this);
    }

    @Override
    public void visitOpenStatement(OpenStatement open)
    {
      IContentExpression record = open.getRecord();
      IType recordF = TypeUtils.interfaceOfType(open.getLoc(), record.getType(), dict);

      if (TypeUtils.isTypeInterface(recordF)) {
        Location loc = open.getLoc();
        TypeInterfaceType recordFace = (TypeInterfaceType) TypeUtils.unwrap(recordF);
        for (Entry<String, IType> fEntry : recordFace.getAllFields().entrySet())
          addFieldEntry(loc, fEntry.getKey(), fEntry.getValue());

        for (Entry<String, IType> tEntry : recordFace.getAllTypes().entrySet())
          addTypeEntry(loc, tEntry.getKey(), tEntry.getValue());
      }
    }

    private void addFieldEntry(Location loc, String name, IType type)
    {
      if (!fieldTypes.containsKey(name)) {
        fieldLocs.put(name, new Location[] { loc });
        fieldTypes.put(name, type);
      }
    }

    private void addTypeEntry(Location loc, String name, IType type)
    {
      if (!localTypes.containsKey(name)) {
        typeLocs.put(name, new Location[] { loc });
        localTypes.put(name, type);
      }
    }
  }

  private void lookupImplementations(List<List<Definition>> groups, Dictionary cxt, ErrorReport errors)
  {
    for (List<Definition> group : groups) {
      DefinitionKind kind = validateGroup(group);

      if (kind == DefinitionKind.implementation) {
        for (Definition def : group) {
          for (IAbstract stmt : CompilerUtils.unWrap(def.getDefinition(), StandardNames.TERM))
            if (CompilerUtils.isImplementationStmt(stmt)) {
              boolean isFallback = CompilerUtils.isFallbackImplementationStmt(stmt);

              IType implType = TypeParser.parseContractImplType(CompilerUtils.implementedContractSpec(stmt), cxt,
                  errors, true, isFallback);

              String instanceFunName = isFallback ? TypeContracts.contractFallbackName(implType) : Over
                  .instanceFunName(implType);

              IType dictType = Over.computeDictionaryType(Freshen.generalizeType(implType), stmt.getLoc(), readOnly);

              cxt.declareVar(instanceFunName, Variable.create(stmt.getLoc(), dictType, instanceFunName), readOnly, def
                  .getVisibility(), true);
            }
        }
      }
    }
  }

  private void namedImportPkg(IAbstract stmt, Dictionary cxt, List<IStatement> definitions, Visibility visibility)
  {
    if (CompilerUtils.isPrivate(stmt))
      namedImportPkg(CompilerUtils.privateTerm(stmt), cxt, definitions, Visibility.priVate);
    else {
      final Location loc = stmt.getLoc();

      assert CompilerUtils.isNamedImport(stmt);

      final IAbstract pkgVarName = CompilerUtils.namedImportName(stmt);
      final IAbstract pkgName = CompilerUtils.namedImportPkg(stmt);

      try {
        CodeCatalog imported = RepositoryManager.locatePackage(pkg.getRepository(), CompileDriver.uriOfPkgRef(pkgName,
            pkg.getSrcCatalog()));
        if (imported != null) {
          Manifest manifest = (Manifest) imported.resolve(StandardNames.MANIFEST, Manifest.EXTENSION);
          if (manifest != null) {
            final IType pkgType = Freshen.freshenForUse(manifest.getPkgType());

            Variable pkgVar = new Variable(loc, pkgType, Abstract.getId(pkgVarName));
            cxt.declareVar(pkgVar.getName(), pkgVar, readOnly, visibility, true);

            final String importedFunName = manifest.getPkgFunName();

            // set up the local version of the package
            definitions.add(new VarEntry(loc, pkgVar, Variable.create(loc, pkgType, importedFunName), readOnly,
                Visibility.priVate));

            // add in the import entry itself, but privately
            definitions.add(new ImportEntry(loc, manifest.getName(), pkgType, manifest.getUri(), Visibility.priVate));

            for (Entry<String, TypeContract> entry : manifest.getContracts().entrySet()) {
              String name = entry.getKey();
              TypeContract contract = entry.getValue();
              TypeUtils.defineTypeContract(cxt, contract);
              definitions.add(new ContractEntry(name, contract.getLoc(), contract, visibility));
            }
          } else
            errors.reportError(StringUtils.msg("imported package has invalid type structure"), loc);
        }
      } catch (IllegalArgumentException e) {
        errors.reportError(StringUtils.msg("cannot process import of ", pkgName, "\nsince '", pkgName,
            "' is not a valid identifier for import."), loc);
      } catch (ResourceException e) {
        errors.reportError(StringUtils.msg("cannot process import of " + pkgName + "\nbecause of resource problem: ", e
            .getMessage()), loc);
      } catch (CatalogException e) {
        errors.reportError(StringUtils.msg("cannot process import of " + pkgName + "\nbecause of catalog problem: ", e
            .getMessage()), loc);
      } catch (RepositoryException e) {
        errors.reportError(StringUtils.msg("cannot access: " + StandardNames.MANIFEST + "\nbecause ", e.getMessage()),
            loc);
      }
    }
  }

  // import package and open it
  private void openImportPkg(IAbstract stmt, Dictionary cxt, List<IStatement> definitions, Visibility visibility)
  {
    if (CompilerUtils.isPrivate(stmt))
      openImportPkg(CompilerUtils.privateTerm(stmt), cxt, definitions, Visibility.priVate);
    else {
      final Location loc = stmt.getLoc();

      assert CompilerUtils.isImport(stmt);

      final IAbstract pkgName = CompilerUtils.importPkg(stmt);
      final String pkgVarName = GenSym.genSym(pkgName.toString());

      try {
        ResourceURI importUri = CompileDriver.uriOfPkgRef(pkgName, pkg.getSrcCatalog());
        CodeCatalog imported = RepositoryManager.locatePackage(pkg.getRepository(), importUri);
        if (imported != null) {
          Manifest manifest = (Manifest) imported.resolve(StandardNames.MANIFEST, Manifest.EXTENSION);
          if (manifest != null) {
            TypeInterfaceType pkgType = (TypeInterfaceType) Freshen.openType(manifest.getPkgType());
            String importedFunName = manifest.getPkgFunName();

            // set up the local version of the package
            Variable pkgVar = new Variable(loc, pkgType, pkgVarName);
            cxt.declareVar(pkgVarName, pkgVar, readOnly, Visibility.priVate, true);

            definitions.add(new VarEntry(loc, pkgVar, Variable.create(loc, pkgType, importedFunName), readOnly,
                Visibility.priVate));

            // add in the import entry itself
            definitions.add(new ImportEntry(loc, manifest.getName(), pkgType, manifest.getUri(), visibility));

            // And this
            for (ITypeAlias alias : manifest.getAliases()) {
              cxt.defineTypeAlias(loc, alias);
              definitions.add(EnvironmentEntry.createTypeAliasEntry(alias.getName(), loc, alias, visibility));
            }

            for (Entry<String, IType> entry : pkgType.getAllFields().entrySet()) {
              String vName = entry.getKey();
              IType vType = entry.getValue();

              Variable var = Variable.create(loc, vType, vName);

              // if (visibility == Visibility.priVate && !TypeUtils.isConstructorType(vType))
              // cxt.declareVar(vName, new LazyVar(var, readOnly, true, definitions,
              // definitions.size(), new VarEntry(
              // loc, var, FieldAccess.create(loc, vType, pkgVar, vName), readOnly, visibility)));
              // else {
              cxt.declareVar(vName, var, readOnly, visibility, true);
              definitions.add(new VarEntry(loc, var, FieldAccess.create(loc, vType, pkgVar, vName), readOnly,
                  visibility));
              // }
            }

            for (ITypeDescription desc : recreateTypeDescs(loc, pkgType, pkgVar))
              cxt.defineType(desc);

            for (Entry<String, TypeContract> entry : manifest.getContracts().entrySet()) {
              String name = entry.getKey();
              TypeContract contract = entry.getValue();
              TypeUtils.defineTypeContract(cxt, contract);
              definitions.add(new ContractEntry(name, contract.getLoc(), contract, visibility));
            }
          } else
            errors.reportError(StringUtils.msg("imported package has invalid structure"), loc);
        }
      } catch (IllegalArgumentException e) {
        errors.reportError(StringUtils.msg("cannot process import of ", pkgName, "\nsince '", pkgName,
            "' is not a valid identifier for import."), loc);
      } catch (ResourceException e) {
        errors.reportError(StringUtils.msg("cannot process import of " + pkgName + "\nbecause of resource problem: ", e
            .getMessage()), loc);
      } catch (CatalogException e) {
        errors.reportError(StringUtils.msg("cannot process import of " + pkgName + "\nbecause of catalog problem: ", e
            .getMessage()), loc);
      } catch (RepositoryException e) {
        errors.reportError(StringUtils.msg("cannot access: " + StandardNames.MANIFEST + "\nbecause ", e.getMessage()),
            loc);
      }
    }
  }

  private Collection<ITypeDescription> recreateTypeDescs(Location loc, TypeInterface face, IContentExpression source)
  {
    Map<String, IType> types = face.getAllTypes();
    Map<String, IType> fields = face.getAllFields();

    Collection<ITypeDescription> descs = new ArrayList<>();
    for (Entry<String, IType> tpEntry : types.entrySet()) {
      String tpName = tpEntry.getKey();
      Collection<IValueSpecifier> specs = new ArrayList<>();
      IType genType = null;

      int conIx = 0;
      for (Entry<String, IType> fldEntry : fields.entrySet()) {
        IType fType = fldEntry.getValue();
        String conName = fldEntry.getKey();
        if (TypeUtils.isConstructorType(fType) && TypeUtils.getConstructorResultType(fType).typeLabel().equals(tpName)) {
          if (TypeUtils.isTypeInterface(TypeUtils.getConstructorArgType(fType)))
            specs.add(new RecordSpecifier(loc, conName, new FieldAccess(loc, fType, source, conName), conIx++, fType));
          else
            specs.add(new ConstructorSpecifier(loc, conName, conIx++, fType, source));
          if (genType == null) {
            Pair<IType, Map<String, Quantifier>> freshen = Freshen.freshen(fType, readOnly, readOnly);
            IType proto = TypeUtils.getConstructorResultType(freshen.left);
            List<Quantifier> quants = new ArrayList<>();
            for (Entry<String, Quantifier> q : freshen.right.entrySet()) {
              if (OccursCheck.occursIn(proto, q.getValue().getVar()))
                quants.add(q.getValue());
            }
            genType = TypeUtils.requant(quants, proto);
          }
        }
      }
      if (!specs.isEmpty())
        descs.add(new TypeDescription(loc, genType, specs));
      else
        descs.add(new TypeExists(loc, tpName, types.get(tpName)));
    }
    return descs;
  }

  private void importJava(IAbstract stmt, Dictionary dict, List<IStatement> stmts, Visibility visibility)
  {
    if (CompilerUtils.isPrivate(stmt))
      importJava(CompilerUtils.privateTerm(stmt), dict, stmts, Visibility.priVate);
    else {
      assert CompilerUtils.isJavaStmt(stmt);

      IAbstract term = Abstract.unaryArg(stmt);
      String className = CompilerUtils.findJavaClassName(term);
      Location loc = term.getLoc();
      try {
        JavaInfo javaInfo = pkg.getRepository().locateJava(className);

        if (javaInfo != null) {
          Map<String, ICafeBuiltin> javaMethods = javaInfo.getMethods();

          for (Entry<String, ICafeBuiltin> iEntry : javaMethods.entrySet()) {
            String jvName = iEntry.getKey();

            if (iEntry.getValue() instanceof NestedBuiltin)
              dict.declareVar(jvName, Variable.create(loc, iEntry.getValue().getType(), jvName), readOnly, visibility,
                  true);
            else {
              dict.declareVar(JavaImport.javaName(jvName), Variable.create(loc, iEntry.getValue().getType(), jvName),
                  readOnly, visibility, true);

              FunctionLiteral javaWrapper = JavaImport.javaWrapper(jvName, javaInfo, loc, errors);
              if (javaWrapper != null) {
                Variable wrapperVar = new Variable(loc, javaWrapper.getType(), javaWrapper.getName());
                stmts.add(VarEntry.createVarEntry(loc, wrapperVar, javaWrapper, readOnly, visibility));
                dict.declareVar(wrapperVar.getName(), wrapperVar, readOnly, visibility, true);
              }
            }
          }

          for (ITypeDescription spec : javaInfo.getTypes())
            dict.defineType(spec);

          stmts.add(new JavaEntry(className, loc, javaInfo, visibility));
        }
      } catch (Exception e) {
        errors.reportError(StringUtils.msg("could not access Java class ", className), loc);
      }
    }
  }

  // import package and open it
  private void openRecord(IAbstract stmt, Dictionary dict, List<IStatement> definitions, Visibility visibility)
  {
    if (CompilerUtils.isPrivate(stmt))
      openRecord(CompilerUtils.privateTerm(stmt), dict, definitions, Visibility.priVate);
    else {
      final Location loc = stmt.getLoc();

      assert CompilerUtils.isOpen(stmt);

      Dictionary rDict = dict.fork(); // be careful about existentials ...

      IContentExpression record = typeOfExp(CompilerUtils.openedRecord(stmt), new TypeVar(), rDict, dict);
      IType recordType = record.getType();
      Variable recordVar;
      TypeInterface face;

      if (record instanceof Variable) {
        recordVar = (Variable) record;
        DictInfo info = dict.getVar(recordVar.getName());

        recordType = TypeUtils.deRef(((VarInfo) info).getFace(errors, rDict));
        if (!(recordType instanceof TypeInterfaceType)) {
          errors.reportError(StringUtils.msg("no interface known for ", recordType, ", type of ", record), loc);
          return;
        } else
          face = (TypeInterface) recordType;
      } else {
        final String recordName = GenSym.genSym();
        IType opened = Freshen.openType(recordType);
        if (!(opened instanceof TypeInterfaceType)) {
          errors.reportError(StringUtils.msg("no interface known for ", recordType, ", type of ", record), loc);
          return;
        } else
          face = (TypeInterface) opened;
        recordVar = new Variable(loc, opened, recordName);
        dict.declareVar(recordName, recordVar, readOnly, Visibility.priVate, true);

        // set up the local version of the record variable
        definitions.add(new VarEntry(loc, recordVar, record, readOnly, Visibility.priVate));
      }

      for (Entry<String, IType> entry : face.getAllTypes().entrySet()) {
        String name = entry.getKey();
        IType type = entry.getValue();

        if (dict.typeExists(name)) {
          ITypeDescription desc = dict.getTypeDescription(name);
          if (!desc.kind().checkKind(type.kind()))
            errors.reportError(StringUtils.msg(name, " not consistent with expected type\nbecause ", entry.getValue()
                .kind(), " is not consistent with ", desc.kind()), loc);
        } else
          dict.defineType(TypeAlias.typeAlias(loc, name, type));

        TypeWitness witness = new TypeWitness(loc, new Type(name, type.kind()), type, visibility);
        definitions.add(witness);

      }

      for (ITypeDescription d : recreateTypeDescs(loc, face, recordVar))
        dict.defineType(d);

      for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
        final String vName = entry.getKey();
        final IType vType = entry.getValue();

        Variable var = Variable.create(loc, vType, vName);
        dict.declareVar(vName, var, readOnly, visibility, true);
        definitions.add(new VarEntry(loc, var, FieldAccess.create(loc, vType, recordVar, vName), readOnly, visibility));

        if (Over.isInstanceFunName(vName)) {
          for (Entry<String, IType> tpEntry : face.getAllTypes().entrySet()) {
            String name = tpEntry.getKey();

            // Stitch together a local implementation from the imported one that matches the local
            // type
            if (Over.isInstanceFunName(vName, name)) {
              IType type = tpEntry.getValue();
              String implName = StringUtils.replaceAfter(vName, "#", name, type.typeLabel());
              Variable localImpl = Variable.create(loc, vType, implName);
              dict.declareVar(implName, localImpl, readOnly, Visibility.priVate, true);
              definitions.add(new VarEntry(loc, localImpl, var, readOnly, Visibility.priVate));
            }
          }

        }
      }
    }
  }

  private void declareTypes(List<Definition> group, Dictionary dict, Dictionary outer)
  {
    boolean hasRegularTypes = false;
    List<String> aliases = new ArrayList<>();

    for (Definition def : group) {
      // Process the statement(s) looking for a type to declare
      for (IAbstract tpStmt : CompilerUtils.unWrap(def.getDefinition(), StandardNames.FATBAR)) {
        Location loc = tpStmt.getLoc();
        if (CompilerUtils.isTypeAlias(tpStmt)) {
          TypeAlias alias = TypeParser.parseTypeAlias(tpStmt, dict, errors);

          dict.defineTypeAlias(loc, alias);
          aliases.add(alias.getName());
        } else if (CompilerUtils.isTypeDefn(tpStmt)) {
          hasRegularTypes = true;
          TypeParser.declareType(dict, outer, tpStmt, errors);
        } else if (CompilerUtils.isTypeWitness(tpStmt)) {
          IType witness = TypeParser.parseType(CompilerUtils.typeWitness(tpStmt), dict, errors, readWrite);
          String witnessedType = CompilerUtils.typeLabel(CompilerUtils.witnessedType(tpStmt));
          ITypeDescription existing = dict.getTypeDescription(witnessedType);
          if (existing != null) {
            try {
              Subsume.same(witness, existing.getType(), loc, dict);
            } catch (TypeConstraintException e) {
              errors.reportError(StringUtils.msg("actual type witness ", tpStmt,
                  " is not consistent with required type ", existing.getType(), "\nbecause ", e.getWords()), loc);
            }
          } else
            dict.defineType(new TypeExists(loc, witnessedType, witness));
        } else
          errors.reportError(StringUtils.msg("bad type definition statement"), loc);
      }
    }
    if (!hasRegularTypes && aliases.size() > 1)
      errors.reportError(StringUtils.msg(PrettyPrintDisplay.msg(aliases),
          " are mutually recursive type aliases, which is not permitted"), groupLocations(group));
  }

  private void typeGroup(List<Definition> group, List<IStatement> thetaElements, Dictionary dict)
  {
    Dictionary tmpCxt = dict.fork();

    for (Definition def : group) {
      Visibility visibility = def.getVisibility();

      assert !Abstract.isBinary(def.getDefinition(), StandardNames.FATBAR);

      IAbstract tpStmt = def.getDefinition();
      Location loc = tpStmt.getLoc();

      if (CompilerUtils.isTypeAlias(tpStmt)) {
        String name = CompilerUtils.typeName(CompilerUtils.typeAliasType(tpStmt));
        if (name != null) {
          ITypeAlias alias = (ITypeAlias) dict.getTypeDescription(name);
          thetaElements.add(EnvironmentEntry.createTypeAliasEntry(alias.getName(), tpStmt.getLoc(), alias, visibility));
        } else
          errors.reportError(StringUtils.msg("invalid type alias ", tpStmt), loc);
      } else if (CompilerUtils.isTypeWitness(tpStmt)) {
        IAbstract wit = CompilerUtils.witnessedType(tpStmt);
        if (!Abstract.isIdentifier(wit))
          errors.reportError(StringUtils.msg("expecting a type name, not ", wit), loc);
        else {
          String id = Abstract.getId(wit);
          assert dict.typeExists(id) && dict.getTypeDescription(id) instanceof TypeExists;

          TypeWitness witnessStmt = new TypeWitness(loc, new Type(id), dict.getTypeDescription(id).getType(),
              visibility);
          thetaElements.add(witnessStmt); // more needed here
        }
      } else if (CompilerUtils.isTypeDefn(tpStmt)) {
        Map<String, IAbstract> integrities = new HashMap<>();
        Map<String, Pair<IAbstract, IType>> defaults = new HashMap<>();
        IAlgebraicType desc = TypeParser
            .parseTypeDefinition(tpStmt, defaults, integrities, tmpCxt, dict, errors, false);

        if (desc != null) {
          for (IValueSpecifier spec : desc.getValueSpecifiers())
            tmpCxt.declareConstructor((ConstructorSpecifier) spec);

          for (Entry<String, IAbstract> intPair : integrities.entrySet()) {
            String vrName = intPair.getKey();
            IAbstract fun = intPair.getValue();
            ProgramLiteral intFun = (ProgramLiteral) typeOfExp(fun, TypeUtils.procedureType(desc.getType()), tmpCxt,
                dict);
            VarEntry integrityVar = VarEntry.createVarEntry(vrName, intFun.getType(), fun.getLoc(), intFun, visibility);
            dict.declareVar(vrName, integrityVar.getVariable(), readOnly, visibility, true);
            thetaElements.add(integrityVar);
          }

          for (Entry<String, Pair<IAbstract, IType>> entry : defaults.entrySet()) {
            String defName = entry.getKey();
            Dictionary funCxt = tmpCxt.fork();
            IAbstract defFun = entry.getValue().left();
            IType defType = entry.getValue().right();

            IContentExpression defltExp = typeOfExp(defFun, defType, funCxt, dict);
            if (defltExp instanceof ProgramLiteral) // can happen if there is a syntax error
            {
              ProgramLiteral dFun = (ProgramLiteral) defltExp;

              VarEntry defltVar = VarEntry.createVarEntry(defName, defType, defFun.getLoc(), dFun, visibility);

              thetaElements.add(Over.overload(errors, defltVar, dict));
              dict.declareVar(defltVar.getVariable().getName(), defltVar.getVariable(), readOnly, visibility, true);
            }
          }

          thetaElements.add(EnvironmentEntry.createTypeEntry(desc.getName(), def.getLoc(), desc.getType(), desc,
              visibility, false, false));
          dict.defineType(desc); // May have been temporarily declared as a type witness
        }
      }
    }
  }

  private DefinitionKind validateGroup(List<Definition> group)
  {
    assert !group.isEmpty();

    DefinitionKind groupKind = null;

    for (Definition def : group) {
      DefinitionKind kind = statementKind(def.get());

      if (groupKind == null)
        groupKind = kind;
      else if (groupKind != kind) {
        errors.reportError(StringUtils.msg("cannot mix ", def, " which is a ", kind, " with ", groupKind), merge(def
            .getLoc(), groupLocations(group, def)));
        return groupKind;
      }
    }

    return groupKind;
  }

  private static Location[] groupLocations(List<Definition> group, Definition except)
  {
    List<Location> locs = new ArrayList<>();
    for (Definition def : group)
      if (def != except)
        locs.add(def.getLoc());
    return locs.toArray(new Location[locs.size()]);
  }

  private static Location[] groupLocations(List<Definition> group)
  {
    return groupLocations(group, null);
  }

  public static DefinitionKind statementKind(IAbstract stmt)
  {
    if (CompilerUtils.isTypeStmt(stmt))
      return DefinitionKind.type;
    else if (CompilerUtils.isContractStmt(stmt))
      return DefinitionKind.contract;
    else if (CompilerUtils.isImplementationStmt(stmt))
      return DefinitionKind.implementation;
    else if (CompilerUtils.isImport(stmt))
      return DefinitionKind.imports;
    else if (CompilerUtils.isJavaStmt(stmt))
      return DefinitionKind.java;
    else if (CompilerUtils.isOpen(stmt))
      return DefinitionKind.imports;
    else if (CompilerUtils.isProgramStmt(stmt))
      return DefinitionKind.variable;
    else
      return DefinitionKind.unknown;
  }

  private void declareContract(List<Definition> group, Dictionary dict)
  {
    Dictionary tmpCxt = dict.fork();

    for (Definition def : group) {
      if (CompilerUtils.isContractStmt(def.get())) {
        TypeContract contract = TypeParser.parseTypeContractHead(def.get(), tmpCxt, errors);

        if (contract != null)
          dict.defineTypeContract(contract);
      }
    }
  }

  private void defineContracts(List<Definition> group, List<IStatement> thetaElements, Dictionary dict, Dictionary outer)
  {
    Dictionary tmpCxt = dict.fork();

    for (Definition def : group) {
      if (CompilerUtils.isContractStmt(def.get())) {
        IAbstract conId = CompilerUtils.contractName(def.get());
        if (Abstract.isIdentifier(conId)) {
          TypeContract contract = dict.getContract(Abstract.getId(conId));

          assert contract != null;

          Map<String, Pair<IAbstract, IType>> defaults = new HashMap<>();
          Map<String, IAbstract> integrities = new HashMap<>();

          TypeParser.fleshoutTypeContract(def.get(), contract, tmpCxt, errors, defaults, integrities);
          Visibility visibility = def.getVisibility();

          String contractName = contract.getName();
          Location loc = def.getLoc();

          thetaElements.add(EnvironmentEntry.createContractEntry(loc, contractName, contract, def.getVisibility()));
          IAlgebraicType desc = contract.getContractType();
          thetaElements.add(EnvironmentEntry.createTypeEntry(desc.getName(), loc, desc.getType(), desc, def
              .getVisibility(), false, true));

          for (Entry<String, Pair<IAbstract, IType>> entry : defaults.entrySet()) {
            String defName = entry.getKey();
            IAbstract defFun = entry.getValue().left();
            IType defType = entry.getValue().right();

            IContentExpression defltExp = typeOfExp(defFun, defType, dict, outer);
            if (defltExp instanceof ProgramLiteral) // may not happen if syntax error in default
            {
              ProgramLiteral dFun = (ProgramLiteral) defltExp;

              VarEntry defltVar = VarEntry.createVarEntry(defFun.getLoc(), Variable.create(defFun.getLoc(), defType,
                  defName), dFun, readOnly, visibility);
              defltVar = (VarEntry) Over.overload(errors, defltVar, dict);

              thetaElements.add(defltVar);
              dict.declareVar(defltVar.getVariable().getName(), defltVar.getVariable(), readOnly, visibility, true);
            }
          }
          TypeUtils.defineTypeContract(dict, contract);
        }
      }
    }
  }

  private void implementationGroup(List<Definition> group, List<IStatement> thetaElements, Dictionary cxt,
      Dictionary outer)
  {
    Dictionary tmpCxt = cxt.fork();

    for (Definition def : group) {
      IAbstract impl = def.get();
      final Location loc = def.getLoc();

      if (CompilerUtils.isImplementationStmt(impl)) {
        String conName = CompilerUtils.implementedContractName(impl);
        IAbstract implTerm = CompilerUtils.implementationBody(impl);
        boolean isDefault = CompilerUtils.isFallbackImplementationStmt(impl);

        IType implType = TypeParser.parseContractImplType(CompilerUtils.implementedContractSpec(impl), tmpCxt, errors,
            false, isDefault);
        String instanceName = isDefault ? TypeContracts.contractFallbackName(implType) : Over.instanceFunName(implType);

        TypeContract contract = tmpCxt.getContract(conName);

        if (contract != null) {
          IAlgebraicType contractType = contract.getContractType();
          RecordSpecifier contractRecord = (RecordSpecifier) contractType.getValueSpecifier(conName);
          IType conType = Freshen.freshenForUse(contractRecord.getConType());

          assert TypeUtils.isConstructorType(conType);

          try {
            Subsume.same(TypeUtils.getConstructorResultType(conType), implType, loc, tmpCxt);
          } catch (TypeConstraintException e) {
            errors.reportError(StringUtils.msg("implementation not consistent with contract because\n", e.getWords()),
                merge(loc, e.getLocs()));
          }

          // implType = Freshen.generalizeType(implType, tmpCxt);
          Pair<IType, Map<String, Quantifier>> evidence = Freshen.freshen(implType, readWrite, readOnly);

          for (Entry<String, Quantifier> entry : evidence.right.entrySet()) {
            Quantifier q = entry.getValue();
            tmpCxt.defineType(new TypeExists(loc, entry.getKey(), q.getVar()));
          }

          IContentExpression implementation = typeOfExp(adjustContractImplementationRecord(implTerm, conName),
              evidence.left, tmpCxt, outer);

          VarEntry instanceFun = Over.instanceFunction(loc, instanceName, evidence.left, implementation, outer, errors,
              def.getVisibility());

          cxt.declareVar(instanceName, instanceFun.getVariable(), instanceFun.isReadOnly(),
              instanceFun.getVisibility(), true);

          cxt.declareImplementation(instanceFun.getVariable(), contract.getName(), isDefault);

          thetaElements.add(instanceFun);
          thetaElements.add(new ImplementationEntry(loc, new ContractImplementation(conName, instanceFun.getVariable(),
              isDefault), instanceFun.getVisibility()));
        } else
          errors.reportError(StringUtils.msg(conName, " not a known contract"), loc);
      } else {
        errors.reportError(StringUtils.msg("contract instance not valid"), impl.getLoc());
      }
    }
  }

  private IAbstract adjustContractImplementationRecord(IAbstract term, String label)
  {
    Location loc = term.getLoc();
    if (CompilerUtils.isEmptyBlock(term))
      return CompilerUtils.emptyBrace(loc, label);
    else if (CompilerUtils.isBlockTerm(term)) {
      return CompilerUtils.braceTerm(loc, Abstract.name(loc, label), CompilerUtils.blockContent(term));
    } else if (CompilerUtils.isLetTerm(term)) {
      IAbstract inLhs = CompilerUtils.letDefs(term);
      IAbstract inRhs = CompilerUtils.letBound(term);
      return CompilerUtils.letExp(loc, inLhs, adjustContractImplementationRecord(inRhs, label));
    } else if (Abstract.isBinary(term, StandardNames.USING) && CompilerUtils.isBlockTerm(Abstract.getArg(term, 1))) {
      IAbstract body = Abstract.argPath(term, 1);
      IAbstract boundTerm = Abstract.getArg(term, 0);
      return Abstract.binary(loc, StandardNames.USING, adjustContractImplementationRecord(boundTerm, label), body);
    } else {
      errors.reportError(StringUtils.msg("cannot understand implementation term: ", term), loc);
      return term;
    }
  }

  private void variableGroup(Dictionary thetaCxt, List<Definition> group, Map<String, IAbstract> thetaTypes,
      List<IStatement> definitions, Map<String, IType> declaredTypes)
  {
    Dictionary tmpCxt = thetaCxt.fork();

    for (Definition def : group) {
      String[] defines = def.getDefines(DefinitionKind.variable);
      for (String name : defines) {
        IAbstract typeAnnotation = thetaTypes.get(name);
        if (typeAnnotation != null) {
          LayeredMap<String, TypeVar> typeVars = new LayeredHash<>();

          if (CompilerUtils.isTypeAnnotation(typeAnnotation)) {

            IType parsedType = TypeParser.parseType(CompilerUtils.typeAnnotation(typeAnnotation), typeVars, thetaCxt
                .fork(), errors, readOnly);
            if (!TypeUtils.isReferenceType(parsedType))
              parsedType = Freshen.generalizeType(parsedType, thetaCxt);
            declaredTypes.put(name, parsedType);
          } else {
            errors.reportError("invalid type annotation", typeAnnotation.getLoc());
          }
        }
      }
    }

    List<IContentPattern> lhsPtns = new ArrayList<>();
    List<AccessMode> policies = new ArrayList<>();

    // Declare all the variables in the group
    for (Definition def : group) {
      IAbstract stmt = def.getDefinition();

      AccessMode policy = variablePolicy(stmt, null);
      AccessMode access = CompilerUtils.defaultFor(policy, readOnly);
      ThetaVarHandler varHandler = new ThetaVarHandler(declaredTypes, tmpCxt, access, def.getVisibility());
      Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);

      IType ptnType = new TypeVar();

      if (isFunctionStmt(stmt))
        lhsPtns.add(typeOfPtn(CompilerUtils.nameOfFunction(stmt), ptnType, condition, tmpCxt, thetaCxt, varHandler));
      else if (isProcedureStmt(stmt))
        lhsPtns.add(typeOfPtn(CompilerUtils.nameOfProcedure(stmt), ptnType, condition, tmpCxt, thetaCxt, varHandler));
      else if (isPatternStmt(stmt))
        lhsPtns.add(typeOfPtn(CompilerUtils.nameOfPattern(stmt), ptnType, condition, tmpCxt, thetaCxt, varHandler));
      else if (CompilerUtils.isVarDeclaration(stmt))
        lhsPtns.add(typeOfPtn(CompilerUtils.varDeclarationPattern(stmt), TypeUtils.referenceType(ptnType), condition,
            tmpCxt, thetaCxt, varHandler));
      else if (CompilerUtils.isIsStatement(stmt))
        lhsPtns.add(typeOfPtn(CompilerUtils.isStmtPattern(stmt), ptnType, condition, tmpCxt, thetaCxt, varHandler));

      policies.add(access);
      if (!CompilerUtils.isTrivial(condition.get()))
        errors.reportError(StringUtils.msg("definition is too complex, because it requires the guard: ", condition
            .get()), def.getLoc());
    }

    // Check the definitions themselves
    List<VarEntry> vars = new ArrayList<>();

    if (group.size() == lhsPtns.size() && group.size() == policies.size()) {
      for (int ix = 0; ix < group.size(); ix++) {
        Definition def = group.get(ix);
        checkDefinition(def.getDefinition(), lhsPtns.get(ix), policies.get(ix), tmpCxt, thetaCxt, def.getVisibility(),
            vars);
      }
    }

    // Generalize types
    for (VarEntry defn : vars) {
      IContentPattern defnVar = defn.getVarPattern();
      IContentExpression binding = defn.getValue();
      Visibility visibility = defn.getVisibility();
      AccessMode access = defn.isReadOnly();
      Location loc = defn.getLoc();

      if (access == readOnly) {
        IContentPattern defPtn = generalizeTypes(defnVar, binding, declaredTypes, thetaCxt, thetaCxt, true);

        defn = VarEntry.createVarEntry(loc, defPtn, binding, access, visibility);
      }
      definitions.add(defn);
      declareVar(defn.getVarPattern(), thetaCxt, access, visibility);

      ConsList<VarDeclaration> extra = ConsList.nil();

      extra = establishInterfaceType(defn.getVarPattern(), thetaCxt, extra, errors);

      for (VarDeclaration decl : extra) {
        definitions.add(VarEntry.createVarEntry(decl.getLoc(), decl.getPattern(), decl.getValue(), access, visibility));
        declareVar(decl.getPattern(), thetaCxt, access, visibility);
      }
    }
  }

  private class ThetaVarHandler implements PtnVarHandler
  {
    private final Map<String, IType> declared;
    private final Dictionary thetaCxt;
    private final AccessMode access;
    private final Visibility visibility;

    public ThetaVarHandler(Map<String, IType> declared, Dictionary thetaCxt, AccessMode access, Visibility visibility)
    {
      this.declared = declared;
      this.thetaCxt = thetaCxt;
      this.access = access;
      this.visibility = visibility;
    }

    @Override
    public Variable typeOfVariable(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition,
        Permission duplicates, Dictionary cxt)
    {
      String vName = Abstract.getId(ptn);
      Location loc = ptn.getLoc();

      if (thetaCxt.isDeclaredVar(vName) && duplicates != Permission.allowed) {
        DictInfo other = thetaCxt.getVar(vName);
        if (!(other.getVariable() instanceof MethodVariable))
          errors.reportError(StringUtils.msg("`", vName, "' is already declared at ", other.getLoc()), loc, other
              .getLoc());
      }

      IType type = declared.get(vName);
      if (type != null) {
        try {
          Subsume.subsume(type, expectedType, loc, cxt);
        } catch (TypeConstraintException e) {
          errors.reportError(StringUtils.msg("variable " + vName + " not consistent with declared type: " + type,
              "\nbecause ", e.getWords()), Location.merge(loc, e.getLocs()));
        }

        Variable ref = Variable.create(loc, expectedType, vName);

        cxt.declareVar(vName, ref, access, visibility, false);

        return ref;
      } else {
        Variable ref = Variable.create(loc, expectedType, vName);
        cxt.declareVar(vName, ref, access, visibility, false);

        return ref;
      }
    }
  }

  private void checkDefinition(IAbstract stmt, IContentPattern defPtn, AccessMode access, Dictionary cxt,
      Dictionary outer, Visibility visibility, List<VarEntry> defs)
  {

    int mark = errors.errorCount();

    if (isFunctionStmt(stmt)) {
      IContentExpression fun = typeOfFunction(stmt, defPtn.getType(), cxt);

      if (errors.noNewErrors(mark)) {
        defs.add(VarEntry.createVarEntry(fun.getLoc(), defPtn, Over.resolve(cxt, errors, fun), access, visibility));
      }
    } else if (isProcedureStmt(stmt)) {
      IContentExpression proc = typeOfProcedure(Abstract.getId(CompilerUtils.procedureName(stmt)), stmt, defPtn
          .getType(), cxt);

      if (errors.noNewErrors(mark)) {
        defs.add(VarEntry.createVarEntry(proc.getLoc(), defPtn, Over.resolve(cxt, errors, proc), access, visibility));
      }
    } else if (isPatternStmt(stmt)) {
      IContentExpression pttrn = typeOfPtnAbstraction(Abstract.getId(CompilerUtils.patternName(stmt)), stmt, defPtn
          .getType(), cxt);

      if (errors.noNewErrors(mark)) {
        defs.add(VarEntry.createVarEntry(pttrn.getLoc(), defPtn, Over.resolve(cxt, errors, pttrn), access, visibility));
      }
    } else if (CompilerUtils.isVarDeclaration(stmt)) {
      IContentExpression val = typeOfExp(CompilerUtils.varDeclarationExpression(stmt), TypeUtils.referencedType(defPtn
          .getType()), cxt.fork(), outer);

      if (errors.noNewErrors(mark)) {
        Variable[] free = FreeVariables.findFreeVars(val, cxt);

        checkFreeRefs(defPtn, val, free);

        defs.add(VarEntry.createReassignableVarEntry(val.getLoc(), defPtn, Over.resolve(cxt, errors, val), free,
            visibility));
      }
    } else if (CompilerUtils.isPrivate(stmt))
      checkDefinition(CompilerUtils.privateTerm(stmt), defPtn, access, cxt, outer, visibility, defs);
    else if (CompilerUtils.isIsStatement(stmt)) {
      IContentExpression val = typeOfExp(CompilerUtils.isStmtValue(stmt), defPtn.getType(), cxt.fork(), outer);

      if (errors.noNewErrors(mark)) {
        Variable[] free = FreeVariables.findFreeVars(val, cxt);

        checkFreeRefs(defPtn, val, free);

        defs.add(VarEntry.createVarEntry(val.getLoc(), defPtn, Over.resolve(cxt, errors, val), access, visibility));
      }
    }
  }

  private static void isRawThetaType(VarEntry var, ErrorReport errors)
  {
    if (TypeUtils.isRawType(var.getType()))
      errors.reportError(StringUtils.msg("theta-level variable", var, " may not have raw type ", var.getType(),
          " unless variable is marked private"), var.getLoc());
  }

  private void checkFreeRefs(IContentPattern ptn, IContentExpression fun, Variable[] free)
  {
    if (ptn instanceof ConstructorPtn && fun instanceof ConstructorTerm) {
      List<IContentPattern> ptnArgs = ((ConstructorPtn) ptn).getElements();
      List<IContentExpression> funArgs = ((ConstructorTerm) fun).getElements();
      for (int ix = 0; ix < ptnArgs.size(); ix++)
        checkFreeRefs(ptnArgs.get(ix), funArgs.get(ix), free);
    } else if (ptn instanceof Variable && !(fun instanceof FunctionLiteral || fun instanceof PatternAbstraction)) {
      String vName = ((Variable) ptn).getName();
      for (Variable fr : free)
        if (fr.getName().equals(vName)) {
          errors.reportError(StringUtils.msg(vName, " is free within its own definition, which is not a function"), fr
              .getLoc());
          break;
        }
    }
  }

  private void declareVar(IContentPattern lhs, Dictionary thetaCxt, AccessMode access, Visibility visibility)
  {
    if (lhs instanceof ConstructorPtn) {
      ConstructorPtn tuple = (ConstructorPtn) lhs;
      for (IContentPattern arg : tuple.getElements())
        declareVar(arg, thetaCxt, access, visibility);
    } else if (lhs instanceof Variable) {
      Variable v = (Variable) lhs;
      thetaCxt.declareVar(v.getName(), v, access, visibility, true);
    } else if (lhs instanceof RecordPtn) {
      RecordPtn record = (RecordPtn) lhs;
      for (IContentPattern ptn : record.getElements().values())
        declareVar(ptn, thetaCxt, access, visibility);
    } else
      errors.reportError(StringUtils.msg("cannot declare variables in ", lhs), lhs.getLoc());
  }

  private IContentPattern generalizeTypes(IContentPattern lhs, IContentExpression rhs, Map<String, IType> declared,
      Dictionary tmpCxt, Dictionary thetaCxt, boolean isReadOnly)
  {
    if (lhs instanceof ConstructorPtn) {
      ConstructorPtn con = (ConstructorPtn) lhs;

      if (rhs instanceof ConstructorTerm) {
        IContentPattern args[] = new IContentPattern[con.arity()];
        ConstructorTerm valTpl = (ConstructorTerm) rhs;
        for (int ix = 0; ix < con.arity(); ix++)
          args[ix] = generalizeTypes(con.getArg(ix), valTpl.getArg(ix), declared, tmpCxt, thetaCxt, isReadOnly);
        return new ConstructorPtn(con.getLoc(), con.getLabel(), con.getType(), args);
      } else {
        for (int ix = 0; ix < con.arity(); ix++) {
          IContentPattern arg = con.getArg(ix);
          if (arg instanceof Variable && errors.isErrorFree()) {
            Variable var = (Variable) arg;
            String varName = var.getName();
            IType varType = var.getType();

            if (declared.containsKey(varName)) {
              try {
                TypeUtils.unify(varType, declared.get(varName), arg.getLoc(), thetaCxt);
              } catch (TypeConstraintException e) {
                errors.reportError(StringUtils.msg("type of: ", var, " does not match declared type: ", rhs.getType(),
                    "\n typically because declared is too generic"), lhs.getLoc());
              }
            }
          }
        }
        return lhs;
      }
    } else if (lhs instanceof Variable) {
      Variable var = (Variable) lhs;
      String varName = var.getName();

      if (errors.isErrorFree()) {
        IType varType = var.getType();

        if (declared.containsKey(varName)) {
          IType declType = Freshen.unsealType(declared.get(varName));

          return Variable.create(var.getLoc(), declType, varName);
        } else if (isReadOnly && !isMemo(rhs)) {
          IType unsealed = Freshen.unsealType(varType);
          varType = Freshen.generalizeType(unsealed, tmpCxt);
        }

        return Variable.create(var.getLoc(), varType, varName);
      } else
        return var;
    } else {
      errors.reportError(StringUtils.msg("(internal) unexpected form of lhs of statement: ", lhs), lhs.getLoc());
      return lhs;
    }
  }

  private static boolean isMemo(IContentExpression exp)
  {
    return exp instanceof MemoExp;
  }

  private void addToThetaInterface(Location loc, String name, IType type, IType face, Dictionary dict)
  {
    try {
      TypeVar tv = new TypeVar();
      TypeUtils.setFieldConstraint(tv, loc, name, type);

      Subsume.subsume(face, tv, loc, dict);
    } catch (TypeConstraintException e) {
      if (TypeUtils.hasAttributeType(dict, face, name))
        errors.reportError(StringUtils.msg("definition for ", name, " not permitted because ", e.getWords()), merge(
            loc, e.getLocs()));
    }
  }

  private AccessMode accessPolicy(Location loc, AccessMode access, AccessMode deflt)
  {
    if (deflt != null && deflt != access)
      errors.reportError(StringUtils.msg("conflicting access mode, previously ", deflt), loc);
    return access;
  }

  // determine variable policy for a variable declaration
  private AccessMode variablePolicy(IAbstract stmt, AccessMode accessMode)
  {
    Location loc = stmt.getLoc();

    if (isFunctionStmt(stmt) || isProcedureStmt(stmt) || isPatternStmt(stmt)) {
      accessMode = accessPolicy(loc, readOnly, accessMode);
    } else if (Abstract.isBinary(stmt, StandardNames.IS)) {
      accessMode = accessPolicy(loc, readOnly, accessMode);
    } else if (Abstract.isUnary(stmt, StandardNames.VAR)
        && Abstract.isBinary(Abstract.unaryArg(stmt), StandardNames.IS)) {
      accessMode = accessPolicy(loc, readOnly, accessMode);
    } else if (Abstract.isUnary(stmt, StandardNames.VAR)
        && Abstract.isBinary(Abstract.unaryArg(stmt), StandardNames.ASSIGN)) {
      accessMode = accessPolicy(loc, readWrite, accessMode);
    } else if (CompilerUtils.isPrivate(stmt))
      return variablePolicy(CompilerUtils.privateTerm(stmt), accessMode);
    else
      errors.reportError(StringUtils.msg("cannot understand program statement: ", stmt), loc);

    return accessMode;
  }

  private boolean isFunctionStmt(IAbstract stmt)
  {
    if (Abstract.isBinary(stmt, StandardNames.IS))
      return CompilerUtils.isProgramHeadPtn(Abstract.getArg(stmt, 0));
    else if (CompilerUtils.isPrivate(stmt))
      return isFunctionStmt(CompilerUtils.privateTerm(stmt));
    else if (Abstract.isBinary(stmt, StandardNames.FATBAR)) {
      boolean left = isFunctionStmt(Abstract.binaryLhs(stmt));
      boolean right = isFunctionStmt(Abstract.binaryRhs(stmt));
      if (left != right)
        errors.reportError(StringUtils.msg("cannot mix equations with action rules with same name"), stmt.getLoc());
      return left;
    } else
      return false;
  }

  private boolean isProcedureStmt(IAbstract stmt)
  {
    if (Abstract.isBinary(stmt, StandardNames.DO))
      return CompilerUtils.isProgramHeadPtn(Abstract.getArg(stmt, 0));
    else if (Abstract.isBinary(stmt, StandardNames.FATBAR)) {
      boolean left = isProcedureStmt(Abstract.getArg(stmt, 0));
      boolean right = isProcedureStmt(Abstract.getArg(stmt, 1));
      if (left != right)
        errors.reportError(StringUtils.msg("cannot mix action rules with equations with same name"), stmt.getLoc());
      return left;
    } else
      return false;
  }

  private boolean isPatternStmt(IAbstract stmt)
  {
    if (CompilerUtils.isPatternRule(stmt))
      return true;
    else if (Abstract.isBinary(stmt, StandardNames.FATBAR)) {
      boolean left = isPatternStmt(Abstract.getArg(stmt, 0));
      boolean right = isPatternStmt(Abstract.getArg(stmt, 1));
      if (left != right)
        errors.reportError(StringUtils.msg("cannot different types of definitions of the same name"), stmt.getLoc());
      return left;
    } else
      return false;
  }

  private boolean isPatternLambda(IAbstract stmt)
  {
    if (CompilerUtils.isPatternRule(stmt))
      return CompilerUtils.patternRuleName(stmt).equals(StandardNames.PATTERN);
    else if (Abstract.isBinary(stmt, StandardNames.FATBAR)) {
      boolean left = isPatternLambda(Abstract.binaryLhs(stmt));
      boolean right = isPatternLambda(Abstract.binaryRhs(stmt));
      if (left != right)
        errors.reportError(StringUtils.msg("cannot different types of definitions of the same name"), stmt.getLoc());
      return left;
    } else
      return false;
  }

  private void checkContractImplementations(Location loc, Map<String, ContractConstraint> constraintMap,
      Dictionary thetaCxt, SortedMap<String, IContentExpression> elements)
  {
    if (!constraintMap.isEmpty()) {
      OverContext overCxt = new OverContext(thetaCxt, errors, 0);
      Over over = new Over();
      for (Entry<String, ContractConstraint> cEntry : constraintMap.entrySet()) {
        if (!thetaCxt.isDeclaredVar(cEntry.getKey())) {
          TypeExp contract = cEntry.getValue().getContract();

          IContentExpression res = over.resolve(loc, contract, overCxt);

          if (res instanceof Resolved) {
            Resolved resolved = (Resolved) res;
            IContentExpression[] dicts = resolved.getDicts();
            if (dicts.length == 0)
              elements.put(cEntry.getKey(), resolved.getOver());
            else
              elements.put(cEntry.getKey(), new MemoExp(loc, resolved, dicts));
          } else
            errors.reportError(StringUtils.msg("cannot resolve ", cEntry.getValue()), loc);
        }
      }
    }
  }

  private static Set<String> notSuppliedArgs(Map<String, IContentExpression> elements, Collection<String> members)
  {
    Set<String> notSupplied = new TreeSet<>();
    for (String entry : members) {
      if (!elements.containsKey(entry))
        notSupplied.add(entry);
    }
    return notSupplied;
  }

  private static void recordContractImplementation(Map<String, Set<ContractImplementation>> contractImplementations,
      ImplementationEntry implementationEntry)
  {
    ContractImplementation implementation = implementationEntry.getImplementation();
    String conName = implementation.getImplementedContract();
    Set<ContractImplementation> implementations = contractImplementations.get(conName);
    if (implementations == null) {
      implementations = new HashSet<>();
      contractImplementations.put(conName, implementations);
    }
    implementations.add(implementation);
  }
}
