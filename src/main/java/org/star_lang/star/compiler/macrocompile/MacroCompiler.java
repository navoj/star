package org.star_lang.star.compiler.macrocompile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.star_lang.star.CompileDriver;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.code.Manifest;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.BooleanLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.DefaultAbstractVisitor;
import org.star_lang.star.compiler.ast.DisplayAst;
import org.star_lang.star.compiler.ast.DisplayLocation;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.MacroDisplay;
import org.star_lang.star.compiler.ast.MacroError;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.macrocompile.MacroDescriptor.MacroRuleType;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayEl;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayConcatenate;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayHasSize;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayMap;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayNil;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.BinaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.TernaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.UnaryArray;
import org.star_lang.star.operators.ast.runtime.AstCategory;
import org.star_lang.star.operators.ast.runtime.AstLocation;
import org.star_lang.star.operators.ast.runtime.AstMacroKey;
import org.star_lang.star.operators.ast.runtime.AstReplace;
import org.star_lang.star.operators.ast.runtime.AstWithCategory;
import org.star_lang.star.operators.general.runtime.GeneralEq;
import org.star_lang.star.operators.string.DisplayValue;
import org.star_lang.star.operators.string.runtime.StringOps.GenerateSym;
import org.star_lang.star.operators.string.runtime.StringOps.StringConcat;
import org.star_lang.star.operators.system.runtime.SimpleLog;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;

/**
 * Transform a set of macro rules into a star program which is then compiled and executed.
 * <p/>
 * A macro rule of the form:
 * <p/>
 * 
 * <pre>
 * # logMsg(?L,?M) ==> logMsg(L, #(#__location__)#,M);
 * </pre>
 * <p/>
 * is transformed to
 * <p/>
 * 
 * <pre>
 * %_logMsg(astApply(_,astName(_,"logMsg"),cons(L,cons(M,nil))),locVar,Replacer,Outer) is
 *     Replacer(astApply(locVar,astName(locVar,"logMsg"), cons(L, cons(AstString(locVar,__display(locVar),cons(M,
 *     nil))))));
 * %_logMsg(L,_,_,Outer) is Outer(L);
 * </pre>
 * <p/>
 * for each <ruleKey> found in the rule set.
 * <p/>
 * A macro rule environment looks like:
 * <p/>
 * 
 * <pre>
 * .. repl ## { Rules }
 * </pre>
 * <p/>
 * which is mapped to:
 * <p/>
 * 
 * <pre>
 * let {
 *   ... -- compiled rules, as above
 * 
 *   <local_replace>(Term,Outer) is case astMacroKey(Term) in {
 *     "key1" is <ruleKey1>(Term,__location(Term),<local_replace>,Outer);
 *     ...
 *   _ default is Outer(Term)
 *   }
 * </pre>
 * <p/>
 * The entire macro program becomes a collection of star programs for each macro + a standard entry
 * point. Imported macros are incorporated into the standard replacer.
 * <p/>
 * 
 * <pre>
 * pkg%macro is package{
 *   import "pkg?%macro";  -- note special uri for imported macros
 * 
 *   %_logMsg(...) ...
 * 
 *   -- The main macro entry point for any package
 *   pkg%macro(T) is let{
 *     _replacer(Term,Outer) is case astMacroKey(Term) in {
 *       "logMsg" is %_logMsg(Term,_replacer,Outer);
 *       _ default is Outer(Term)
 *     }
 * 
 *     pkg%walk(astApply(Op,Args)) is astApply(pkg%macro(Op),Args//pkg%macro);
 *     pkg%walk(X) default is X
 *   } in _replacer(T,pkg%walk)
 * </pre>
 */
/*
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
public class MacroCompiler
{
  private static final String PATH_DOT = "_dot";
  public static final String MACRO_QUERY = "%macro";
  public static final String MACRO_WALK = "%walk";

  private static final MacroDict intrinsicMacroFuns = intrinsicMap();

  private static void compileRule(IAbstract rule, ErrorReport errors, MacroDict dict, Set<String> vars,
      Map<String, Pair<String, IAbstract>> macros, List<IAbstract> macroRules)
  {
    assert CompilerUtils.isMacroDef(rule);

    Location loc = rule.getLoc();

    // Three standard arguments: location, replacer, outer-replacer
    IAbstract locationVar = new Name(loc, GenSym.genSym("__location_"));
    IAbstract outerVar = new Name(loc, GenSym.genSym("__outer"));
    IAbstract replaceVar = new Name(loc, GenSym.genSym("__replace"));
    IAbstract matchVar = new Name(loc, GenSym.genSym("__term"));

    IAbstract ptnArg = CompilerUtils.macroRulePtn(rule);

    String key = patternKey(ptnArg, errors);

    int errorState = errors.errorCount();

    Wrapper<IAbstract> cond = Wrapper.create(null);
    Set<String> ruleVars = new HashSet<>(vars);
    IAbstract ptn = compilePtn(ptnArg, cond, errors, ruleVars, locationVar);

    if (StarCompiler.TRACEMACRO) {
      if (Abstract.isBinary(ptn, StandardNames.MATCHING)) {
        IAbstract lhs = Abstract.binaryLhs(ptn);
        IAbstract rhs = Abstract.binaryRhs(ptn);
        if (Abstract.isIdentifier(lhs))
          matchVar = lhs;
        else if (Abstract.isIdentifier(rhs))
          matchVar = rhs;
        else {
          errors.reportWarning(StringUtils.msg("Cannot trace complex pattern ", ptn), loc);
        }
      } else {
        ptn = Abstract.binary(loc, StandardNames.MATCHING, matchVar, ptn);
        vars.add(Abstract.getId(matchVar));
      }
    }

    Wrapper<IAbstract> counterVar = Wrapper.create(null);

    IAbstract repl = compileReplacement(CompilerUtils.macroRuleRepl(rule), dict, errors, ruleVars, counterVar,
        locationVar, replaceVar, outerVar);

    if (!counterVar.isEmpty()) {
      IAbstract defn = CompilerUtils.defStatement(loc, counterVar.get(), Abstract.unary(loc, GenerateSym.name,
          new StringLiteral(loc, "")));
      repl = CompilerUtils.valofValis(loc, repl, defn);
    }

    if (StarCompiler.TRACEMACRO) {
      IAbstract tmpVar = Abstract.name(loc, GenSym.genSym("__macV"));
      vars.add(Abstract.getId(tmpVar));

      IAbstract fireAction = Abstract.unary(loc, SimpleLog.name, Abstract.binary(loc, StringConcat.name,
          new StringLiteral(loc, "rule at " + loc + " fired on --> "), Abstract.unary(loc, DisplayValue.displayQuoted,
              matchVar)));

      IAbstract dispTerm = Abstract.binary(loc, StringConcat.name, new StringLiteral(loc, "rule at " + loc
          + " result --> "), Abstract.unary(loc, DisplayValue.displayQuoted, tmpVar));

      IAbstract traceAction = Abstract.unary(loc, SimpleLog.name, dispTerm);
      IAbstract decl = CompilerUtils.defStatement(loc, tmpVar, repl);
      repl = CompilerUtils.valofValis(loc, genReplace(loc, replaceVar, tmpVar, vars, dict), fireAction, decl,
          traceAction);
    } else
      repl = genReplace(loc, replaceVar, repl, vars, dict);

    if (errors.noNewErrors(errorState))
      addRuleEquation(loc, key, ptn, cond, repl, locationVar, replaceVar, outerVar, macros, macroRules);
  }

  private static void addRuleEquation(Location loc, String key, IAbstract ptn, Wrapper<IAbstract> cond, IAbstract repl,
      IAbstract locationVar, IAbstract replaceVar, IAbstract outerVar, Map<String, Pair<String, IAbstract>> macros,
      List<IAbstract> macroRules)
  {
    List<IAbstract> args = stdMacroArgs(ptn, locationVar, replaceVar, outerVar);

    Pair<String, IAbstract> ruleset = macros.get(key);
    if (ruleset == null) {
      String macroName = macroRuleName(key);
      IAbstract macroRule = CompilerUtils.equation(loc, macroName, args, cond.get(), repl);

      macros.put(key, Pair.pair(macroName, macroRule));
    } else {
      String nextName = GenSym.genSym(key);

      macroRules.add(CompilerUtils.function(loc, ruleset.right, fallbackRule(loc, ruleset.left, new Name(loc, GenSym
          .genSym("_")), locationVar, replaceVar, outerVar, new Name(loc, nextName))));

      macros.put(key, Pair.pair(nextName, CompilerUtils.equation(loc, nextName, args, cond.get(), repl)));
    }
  }

  private static void compileMacroVar(IAbstract rule, IAbstract replaceVar, IAbstract outerVar, ErrorReport errors,
      MacroDict dict, Set<String> vars, Map<String, Pair<String, IAbstract>> macros)
  {
    assert CompilerUtils.isMacroVar(rule);

    int errorState = errors.errorCount();

    Location loc = rule.getLoc();

    IAbstract ptnArg = CompilerUtils.macroRulePtn(rule);

    assert ptnArg instanceof Name;

    String key = patternKey(ptnArg, errors);

    Set<String> ruleVars = new HashSet<>(vars);
    Wrapper<IAbstract> counterVar = Wrapper.create(null);

    IAbstract repl = compileReplacement(CompilerUtils.macroRuleRepl(rule), dict, errors, ruleVars, counterVar,
        new Name(loc, Location.nowhere), replaceVar, outerVar);

    if (!counterVar.isEmpty()) {
      IAbstract defn = CompilerUtils.defStatement(loc, counterVar.get(), Abstract.unary(loc, GenerateSym.name,
          new StringLiteral(loc, "")));
      repl = CompilerUtils.valofValis(loc, repl, defn);
    }

    repl = CompilerUtils.memoTerm(loc, repl);

    if (errors.noNewErrors(errorState)) {

      Pair<String, IAbstract> ruleset = macros.get(key);
      if (ruleset == null) {
        String macroName = macroRuleName(key);
        IAbstract macroRule = CompilerUtils.varIsDeclaration(loc, new Name(loc, macroName), repl);
        macros.put(key, Pair.pair(macroName, macroRule));
      } else {
        errors.reportError(StringUtils.msg("cannot have multiple defs for macro ", key), loc);
      }
    }
  }

  private static IAbstract defaultRule(Location loc, String key, String ruleName, IAbstract outer, MacroDict dict)
  {
    Name var = new Name(loc, GenSym.genSym("V"));
    MacroDescriptor outerDesc = dict.find(key);
    if (outerDesc != null && outerDesc.getImportVar() != null) {
      List<IAbstract> stdArgs = stdMacroArgs(var, new Name(loc, GenSym.genSym("__location__")), new Name(loc, GenSym
          .genSym("__replace__")), outer);

      return CompilerUtils.defaultEquation(loc, ruleName, stdArgs, Abstract.apply(loc, outerDesc.getInvokeName(loc),
          stdArgs));
    } else {
      IAbstract invokeOuter = Abstract.unary(loc, outer, var);
      IAbstract anon = Abstract.anon(loc);
      List<IAbstract> stdArgs = stdMacroArgs(var, anon, anon, outer);

      if (StarCompiler.TRACEMACRO) {
        IAbstract fireAction = Abstract.unary(loc, SimpleLog.name, Abstract
            .binary(loc, StringConcat.name, new StringLiteral(loc, "rules for " + ruleName + " failed for "), Abstract
                .unary(loc, DisplayAst.name, var)));

        invokeOuter = CompilerUtils.valofValis(loc, invokeOuter, fireAction);
      }

      return CompilerUtils.defaultEquation(loc, ruleName, stdArgs, invokeOuter);
    }
  }

  private static IAbstract fallbackRule(Location loc, String key, IAbstract arg, IAbstract locationVar,
      IAbstract replaceVar, IAbstract outer, IAbstract fallback)
  {
    List<IAbstract> args = stdMacroArgs(arg, locationVar, replaceVar, outer);
    return CompilerUtils.equation(loc, key, args, Abstract.apply(loc, fallback, args));
  }

  private static List<IAbstract> stdMacroArgs(IAbstract arg, IAbstract locationVar, IAbstract replaceVar,
      IAbstract outer)
  {
    return FixedList.create(arg, locationVar, replaceVar, outer);
  }

  public static IAbstract compileMacroRules(final Location loc, List<IAbstract> macroStmts, final String pkgName,
      final Catalog catalog, final CodeRepository repository, final ErrorReport errors)
  {
    final MacroDict dict = intrinsicMacroFuns.fork();
    final List<IAbstract> pkgStmts = new ArrayList<>();
    final IAbstract walker = Abstract.name(loc, GenSym.genSym(MACRO_WALK));

    MacroRulesHandler pkgMacroHandler = new MacroRulesHandler() {

      @Override
      public void handleImport(IAbstract stmt, MacroDict dict)
      {
        IAbstract imported = CompilerUtils.importPkg(stmt);
        try {
          ResourceURI uri = CompileDriver.uriOfPkgRef(imported, catalog);
          ResourceURI macroUri = macroUri(uri);
          Location loc = stmt.getLoc();

          Manifest manifest = RepositoryManager.locateStarManifest(repository, macroUri);

          if (manifest != null) {
            IAbstract importName = new Name(loc, GenSym.genSym(manifest.getName()));

            IType macroFunType = manifest.getPkgType();
            if (!TypeUtils.isTypeInterface(macroFunType))
              errors.reportError(StringUtils.msg("invalid package type: ", macroFunType), loc);
            else {
              TypeInterface face = (TypeInterface) TypeUtils.unwrap(TypeUtils.deRef(macroFunType));
              for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
                String macroName = entry.getKey();
                if (isMacroRuleName(macroName)) {
                  String key = macroKeyOfRuleName(macroName);
                  dict.define(key, new MacroDescriptor(key, importName, macroName, MacroRuleType.macroRule, 1));
                }
              }
            }

            // Import the macro package
            IAbstract macroImport = CompilerUtils.namedImportStmt(loc, importName, new StringLiteral(loc, macroUri
                .toString()));
            if (CompilerUtils.isPrivate(stmt))
              macroImport = CompilerUtils.privateStmt(loc, macroImport);
            else
              pkgStmts.add(0, CompilerUtils.openStmt(loc, importName));
            pkgStmts.add(0, macroImport);
            pkgStmts.add(0, stmt);
          } else
            errors.reportError(StringUtils.msg("could not find compiled macro package ", macroUri), loc);
        } catch (ResourceException e) {
          errors.reportError("could not access imported package: " + imported, loc);
        } catch (CatalogException e) {
          errors.reportError("could not access catalog for imported package: " + imported, loc);
        } catch (RepositoryException e) {
          errors.reportError("could not access code repository for imported package: " + imported, loc);
        }
      }

      @Override
      public IAbstract generateResult(List<IAbstract> macroRules, List<IAbstract> others, IAbstract replacer,
          IAbstract replaceVar, MacroDict dict)
      {
        IAbstract macroDriver = new Name(loc, pkgName);
        pkgStmts.addAll(macroRules);

        IAbstract termArg = Abstract.name(loc, GenSym.genSym("T"));

        if (CompilerUtils.isTrivialFunction(replacer))
          pkgStmts.add(CompilerUtils.function(loc, CompilerUtils.equation(loc, Abstract
              .unary(loc, macroDriver, termArg), termArg)));
        else {
          List<IAbstract> pkgRules = genWalker(loc, replacer, replaceVar, walker);

          IAbstract driver = CompilerUtils.letExp(loc, CompilerUtils.blockTerm(loc, pkgRules), Abstract.unary(loc,
              walker, termArg));

          IAbstract topLevel = CompilerUtils.equation(loc, Abstract.unary(loc, macroDriver, termArg), driver);

          pkgStmts.add(CompilerUtils.function(loc, topLevel));
        }

        return CompilerUtils.tupleUp(loc, StandardNames.TERM, pkgStmts);
      }
    };

    return compileRules(loc, macroStmts, errors, dict, new HashSet<>(), new HashSet<>(), walker,
        pkgMacroHandler, true);
  }

  private static List<IAbstract> genWalker(Location loc, IAbstract replacer, IAbstract replaceVar, IAbstract driver)
  {
    List<IAbstract> pkgRules = new ArrayList<>();
    pkgRules.add(replacer);

    if (!CompilerUtils.isTrivialFunction(replacer)) {

      // Construct the walker boilerplate:
      // fun pkg%walk(astApply(Loc,Op,Args)) is astApply(Loc,replacer(Op,pkg%walk),Args//replacer);
      //
      IAbstract argsArg = Abstract.name(loc, GenSym.genSym("A"));
      IAbstract opArg = Abstract.name(loc, GenSym.genSym("op"));
      IAbstract locArg = Abstract.name(loc, GenSym.genSym("Loc"));

      // Define the walker itself
      IAbstract head = Abstract.unary(loc, driver, Abstract.ternary(loc, Apply.name, locArg, opArg, argsArg));
      IAbstract opRepl = Abstract.unary(loc, replaceVar, opArg);

      IAbstract walkerRl1 = CompilerUtils.equation(loc, head, Abstract.ternary(loc, Apply.name, locArg, opRepl,
          Abstract.binary(loc, ArrayMap.name, argsArg, replaceVar)));
      IAbstract walkerRl2 = CompilerUtils.equation(loc, Abstract.unary(loc, driver, argsArg), argsArg);

      pkgRules.add(CompilerUtils.function(loc, walkerRl1, walkerRl2));
    }

    return pkgRules;
  }

  private static MacroDict intrinsicMap()
  {
    MacroDict map = new MacroDict(null);

    for (ICafeBuiltin builtin : Intrinsics.allBuiltins()) {
      if (isAstFunctionType(builtin.getType())) {
        String funName = builtin.getName();
        String key = funName + "()";
        MacroDescriptor desc = new MacroDescriptor(key, funName, MacroRuleType.builtin, 1);

        map.define(key, desc);
      }
    }

    return map;
  }

  private static boolean isAstFunctionType(IType type)
  {
    if (TypeUtils.isFunctionType(type)) {
      for (IType arg : TypeUtils.getFunArgTypes(type))
        if (!arg.equals(ASyntax.type))
          return false;
      return TypeUtils.getFunResultType(type).equals(ASyntax.type);
    }
    return false;
  }

  private interface MacroRulesHandler
  {
    IAbstract generateResult(List<IAbstract> macroRules, List<IAbstract> otherRules, IAbstract replacer,
        IAbstract replaceVar, MacroDict dict);

    void handleImport(IAbstract stmt, MacroDict dict);
  }

  private static IAbstract compileRules(Location loc, Iterable<IAbstract> defs, ErrorReport errors, MacroDict dict,
      Set<String> vars, Set<String> keyRefs, IAbstract driver, MacroRulesHandler handler, boolean topLevel)
  {
    List<IAbstract> rules = new ArrayList<>();
    List<IAbstract> otherRules = new ArrayList<>();

    // The name of the local macro replacement function
    Name replaceVar = new Name(loc, GenSym.genSym("__replace"));
    MacroDict subDict = dict.fork(replaceVar);

    for (IAbstract rl : defs) {
      if (CompilerUtils.isMacroVar(rl) && !topLevel) {
        String name = Abstract.getId(CompilerUtils.macroRulePtn(rl));
        String macroName = macroRuleName(name);
        MacroDescriptor desc = new MacroDescriptor(name, macroName, MacroRuleType.macroVar, 1);
        subDict.define(name, desc);
      } else if (CompilerUtils.isMacroDef(rl)) {
        final IAbstract ptnArg = CompilerUtils.macroRulePtn(rl);
        String key = patternKey(ptnArg, errors);
        if (!subDict.defines(key)) {
          String macroName = macroRuleName(key);
          subDict.define(key, new MacroDescriptor(key, macroName, MacroRuleType.macroRule, 1));
        }
      } else if (!topLevel && CompilerUtils.isCodeMacro(rl)) {
        rl = CompilerUtils.codeMacroEqn(rl);
        final String funName;
        final String key;
        final int arity;
        if (CompilerUtils.isFunctionStatement(rl)) {
          IAbstract lhs = CompilerUtils.functionHead(rl);
          funName = Abstract.getOp(lhs);
          key = patternKey(lhs, errors);
          arity = Abstract.arity(lhs);
        } else {
          IAbstract lhs = CompilerUtils.isStmtPattern(rl);
          funName = Abstract.getId(lhs);
          key = patternKey(lhs, errors);
          arity = 0;
        }

        if (!subDict.defines(key)) {
          MacroDescriptor desc = new MacroDescriptor(key, funName, MacroRuleType.quotedFun, arity);
          subDict.define(key, desc);
        }
      } else if (CompilerUtils.isImport(rl))
        handler.handleImport(rl, subDict);
    }

    Map<String, Pair<String, IAbstract>> macros = new HashMap<>();

    for (IAbstract rl : defs) {
      if (CompilerUtils.isMacroVar(rl) && !topLevel)
        compileMacroVar(rl, replaceVar, driver, errors, subDict, vars, macros);
      else if (CompilerUtils.isMacroDef(rl))
        compileRule(rl, errors, subDict, vars, macros, rules);
      else if (CompilerUtils.isCodeMacro(rl))
        otherRules.add(CompilerUtils.codeMacroEqn(rl));
      else
        otherRules.add(rl);
    }

    if (subDict.size() > 0) {
      IAbstract termArg = new Name(loc, GenSym.genSym("T"));

      Map<String, IAbstract> caseMap = new HashMap<>();
      IAbstract localizer = Abstract.unary(loc, AstLocation.name, termArg);

      MacroDict d = subDict;
      while (d != null) {
        for (Entry<String, MacroDescriptor> entry : d) {
          MacroDescriptor desc = entry.getValue();

          switch (desc.type()) {
          case macroRule: {
            IAbstract cseBody = Abstract.apply(loc, desc.getInvokeName(loc), termArg, localizer, replaceVar, driver);
            caseMap.put(desc.getKey(), CompilerUtils.caseRule(loc, new StringLiteral(loc, desc.getKey()), cseBody));
            break;
          }
          case macroVar: {
            IAbstract macroCall = Abstract.zeroary(loc, desc.getInvokeName(loc));
            caseMap.put(desc.getKey(), CompilerUtils.caseRule(loc, new StringLiteral(loc, desc.getKey()), macroCall));
            break;
          }
          case quotedFun: {
            if (desc.getArity() == 1)
              caseMap.put(desc.getKey(), CompilerUtils.caseRule(loc, new StringLiteral(loc, desc.getKey()), Abstract
                  .unary(loc, desc.getInvokeName(loc), termArg)));
            break;
          }
          default:
          }
        }
        d = (MacroDict) d.getOuter();
      }

      for (Entry<String, Pair<String, IAbstract>> entry : macros.entrySet()) {
        Pair<String, IAbstract> e = entry.getValue();

        IAbstract last = e.right;
        if (!CompilerUtils.isVarDeclaration(last) && !CompilerUtils.isIsStatement(last))
          rules.add(CompilerUtils.function(loc, last, defaultRule(loc, entry.getKey(), e.left, new Name(loc, GenSym
              .genSym("outer")), subDict)));
        else
          rules.add(last);
      }

      // Add in special rule for meta-rules
      caseMap.put("#()", CompilerUtils.caseRule(loc, new StringLiteral(loc, "#()"), termArg));
      // Add in special rule for quoted terms
      String quoteKey = StandardNames.QUOTE + "()";
      caseMap.put(quoteKey, CompilerUtils.caseRule(loc, new StringLiteral(loc, quoteKey), termArg));

      List<IAbstract> cases = new ArrayList<>(caseMap.values());

      // If there is no macro rule then invoke the outer scope macros.
      cases.add(CompilerUtils.defaultCaseRule(loc, Abstract.name(loc, StandardNames.ANONYMOUS), Abstract.unary(loc,
          driver, termArg)));

      // build the replacer
      IAbstract replacer = CompilerUtils.caseTerm(loc, Abstract.unary(loc, StandardNames.MAC_KEY, termArg), cases);

      // if (StarCompiler.TRACEMACRO) {
      // IAbstract dispTerm = Abstract.binary(loc, StringConcat.name, new StringLiteral(loc,
      // "macro replace ("
      // + replaceVar + ")"), Abstract.unary(loc, DisplayValue.displayQuoted, termArg));
      //
      // IAbstract traceAction = Abstract.unary(loc, SimpleLog.name, dispTerm);
      //
      // replacer = CompilerUtils.valofValis(loc, replacer, traceAction);
      // }

      List<IAbstract> localArgs = new ArrayList<>();
      localArgs.add(termArg);
      return handler.generateResult(rules, otherRules, CompilerUtils.function(loc, CompilerUtils.equation(loc,
          replaceVar.getId(), localArgs, replacer)), replaceVar, subDict);
    } else {
      // If no local macros then simply return the term
      IAbstract termArg = new Name(loc, GenSym.genSym("T"));
      List<IAbstract> localArgs = new ArrayList<>();
      localArgs.add(termArg);
      return handler.generateResult(rules, otherRules, CompilerUtils.function(loc, CompilerUtils.equation(loc,
          replaceVar.getId(), localArgs, termArg)), replaceVar, subDict);
    }
  }

  private static String patternKey(IAbstract term, ErrorReport errors)
  {
    try {
      return AstMacroKey.astMacroKey(term);
    } catch (EvaluationException e) {
      errors.reportError(e.getLocalizedMessage(), e.getLoc());
      return "";
    }
  }

  private static String macroRuleName(String key)
  {
    return StandardNames.MAC_KEY_PREFIX + key;
  }

  private static boolean isMacroRuleName(String name)
  {
    return name.startsWith(StandardNames.MAC_KEY_PREFIX) && name.length() > StandardNames.MAC_KEY_PREFIX.length();
  }

  private static String macroKeyOfRuleName(String name)
  {
    assert isMacroRuleName(name);
    return name.substring(StandardNames.MAC_KEY_PREFIX.length());
  }

  private static IAbstract compilePtn(IAbstract ptn, Wrapper<IAbstract> cond, ErrorReport errors, Set<String> vars,
      IAbstract locationVar)
  {
    Location loc = ptn.getLoc();
    IAbstract anon = Abstract.anon(loc);

    if (Abstract.isBinary(ptn, StandardNames.MACRO_APPLY)) {
      Wrapper<IAbstract> subCond = Wrapper.create(null);

      IAbstract opPtn = compilePtn(Abstract.binaryLhs(ptn), subCond, errors, vars, locationVar);
      IAbstract argPtn = compilePtn(Abstract.binaryRhs(ptn), subCond, errors, vars, locationVar);
      IAbstract argVr = new Name(loc, GenSym.genSym("P$"));
      vars.add(Abstract.getId(argVr));

      CompilerUtils.extendCondition(cond, CompilerUtils.boundTo(loc, argPtn, Abstract.binary(loc, "__macro_tuple",
          locationVar, argVr)));
      CompilerUtils.appendCondition(cond, subCond);

      return astApply(loc, anon, opPtn, argVr);
    } else if (Abstract.isUnary(ptn, StandardNames.QUESTION) && Abstract.isName(Abstract.unaryArg(ptn))) {
      String var = Abstract.getId(Abstract.unaryArg(ptn));
      if (var.equals(StandardNames.ANONYMOUS))
        return new Name(loc, var);
      else if (vars.contains(var)) {
        String nvar = GenSym.genSym(var);
        // vars.add(nvar);
        CompilerUtils.extendCondition(cond, Abstract.binary(loc, GeneralEq.name, new Name(loc, nvar),
            new Name(loc, var)));
        return new Name(loc, nvar);
      } else {
        vars.add(var);
        return new Name(loc, var);
      }
    } else if (Abstract.isBinary(ptn, StandardNames.QUESTION)) {
      String var = Abstract.getId(Abstract.binaryRhs(ptn));
      if (var == null) {
        errors.reportError("expecting a variable after ?, not " + Abstract.binaryRhs(ptn), loc);
        return new Name(loc, GenSym.genSym("_"));
      } else {
        vars.add(var);
        return Abstract.binary(loc, StandardNames.MATCHING, new Name(loc, var), compilePtn(Abstract.binaryLhs(ptn),
            cond, errors, vars, locationVar));
      }
    } else if (Abstract.isBinary(ptn, StandardNames.DOTSLASH))
      return dotSlashPttrn(loc, ptn, cond, errors, vars, locationVar);
    else if (Abstract.isBinary(ptn, StandardNames.WFF_DEFINES)) {
      IAbstract lhs = compilePtn(Abstract.binaryLhs(ptn), cond, errors, vars, locationVar);
      IAbstract lvar = lhs;
      IAbstract rhs = Abstract.binaryRhs(ptn);
      if (Abstract.isIdentifier(rhs)) {
        if (!Abstract.isIdentifier(lhs)) {
          IAbstract nv = new Name(loc, GenSym.genSym("_"));
          lhs = Abstract.binary(loc, StandardNames.MATCHING, lhs, nv);
          lvar = nv;
        } else
          lvar = lhs;
        CompilerUtils.extendCondition(cond, Abstract.binary(loc, AstCategory.name, lvar, new StringLiteral(loc,
            Abstract.getId(rhs))));
      } else
        errors.reportWarning(StringUtils.msg(ptn, " not supported by macro compiler"), loc);
      return lhs;
    } else if (ptn instanceof Apply) {
      Apply apply = (Apply) ptn;
      IAbstract opPtn = compilePtn(apply.getOperator(), cond, errors, vars, locationVar);
      Name argsVar = new Name(loc, GenSym.genSym("_args"));
      vars.add(Abstract.getId(argsVar));

      IList argArray = apply.getArgs();
      int arity = argArray.size();

      CompilerUtils.extendCondition(cond, Abstract.binary(loc, ArrayHasSize.name, argsVar, CompilerUtils.rawLiteral(
          loc, new IntegerLiteral(loc, arity))));
      for (int ix = 0; ix < arity; ix++) {
        Wrapper<IAbstract> subCond = Wrapper.create(null);
        IAbstract argPtn = compilePtn((IAbstract) argArray.getCell(ix), subCond, errors, vars, locationVar);
        IAbstract arg = Abstract.binary(loc, ArrayEl.name, argsVar, CompilerUtils.rawLiteral(loc, new IntegerLiteral(
            loc, ix)));
        CompilerUtils.extendCondition(cond, CompilerUtils.boundTo(loc, argPtn, arg));
        CompilerUtils.appendCondition(cond, subCond);
      }

      return Abstract.ternary(loc, Apply.name, anon, opPtn, argsVar);
    } else if (ptn instanceof Name) {
      String sym = ((Name) ptn).getId();

      if (sym.equals(StandardTypes.INTEGER))
        return Abstract.binary(loc, IntegerLiteral.name, anon, anon);
      else if (sym.equals(StandardTypes.LONG))
        return Abstract.binary(loc, LongLiteral.name, anon, anon);
      else if (sym.equals(StandardTypes.FLOAT))
        return Abstract.binary(loc, FloatLiteral.name, anon, anon);
      else if (sym.equals(StandardNames.NUMBER)) {
        Name nvar = new Name(loc, GenSym.genSym(sym));
        CompilerUtils.extendCondition(cond, Abstract.unary(loc, "__macro_isNumber", nvar));
        return nvar;
      } else if (sym.equals(StandardTypes.DECIMAL))
        return Abstract.binary(loc, BigDecimalLiteral.name, anon, anon);
      else if (sym.equals(StandardNames.IDENTIFIER))
        return Abstract.binary(loc, Name.name, anon, anon);
      else if (sym.equals(StandardTypes.CHAR))
        return Abstract.binary(loc, CharLiteral.name, anon, anon);
      else if (sym.equals(StandardTypes.STRING))
        return Abstract.binary(loc, StringLiteral.name, anon, anon);
      else if (sym.equals(StandardNames.TUPLE)) {
        Name nvar = new Name(loc, GenSym.genSym(sym));
        CompilerUtils.extendCondition(cond, Abstract.unary(loc, "__macro_isTuple", nvar));
        return nvar;
      } else if (vars.contains(sym)) {
        Name nvar = new Name(loc, GenSym.genSym(sym));
        CompilerUtils.extendCondition(cond, Abstract.binary(loc, GeneralEq.name, nvar, ptn));
        return nvar;
      } else
        return astName(loc, anon, sym);
    } else if (ptn instanceof StringLiteral)
      return Abstract.binary(loc, StringLiteral.name, anon, ptn);
    else if (ptn instanceof IntegerLiteral)
      return Abstract.binary(loc, IntegerLiteral.name, anon, ptn);
    else if (ptn instanceof LongLiteral)
      return Abstract.binary(loc, LongLiteral.name, anon, ptn);
    else if (ptn instanceof FloatLiteral)
      return Abstract.binary(loc, FloatLiteral.name, anon, ptn);
    else if (ptn instanceof BigDecimalLiteral)
      return Abstract.binary(loc, BigDecimalLiteral.name, anon, ptn);
    else if (ptn instanceof BooleanLiteral)
      return Abstract.binary(loc, BooleanLiteral.name, anon, ptn);
    else if (ptn instanceof CharLiteral)
      return Abstract.binary(loc, CharLiteral.name, anon, ptn);
    else {
      errors.reportError("(internal) cannot handle macro pattern: " + ptn, loc);
      return ptn;
    }
  }

  // A ./ pattern of the form: ?X ./ <ptn>
  // is mapped to a call to a pattern expression: X matching astMacroSearch(pattern(A,..,A) from
  // <ptn>)(Pth,(A,...,A))

  private static IAbstract dotSlashPttrn(Location loc, IAbstract ptn, Wrapper<IAbstract> cond, ErrorReport errors,
      Set<String> vars, IAbstract locationVar)
  {
    IAbstract lhs = Abstract.binaryLhs(ptn);
    if (Abstract.isUnary(lhs, StandardNames.QUESTION) && Abstract.isIdentifier(Abstract.unaryArg(lhs))) {
      IAbstract ptnVar = Abstract.unaryArg(lhs);
      IAbstract pthVar = Abstract.name(lhs.getLoc(), Abstract.getId(ptnVar) + PATH_DOT);

      Pair<IAbstract, IAbstract> subPttrn = genSubPttrn(Abstract.binaryRhs(ptn), errors, vars, locationVar);
      IAbstract pttrn = subPttrn.left();
      IAbstract reslt = subPttrn.right();

      assert Abstract.isTupleTerm(reslt) || Abstract.isIdentifier(reslt);
      if (Abstract.isTupleTerm(reslt))
        for (IValue arg : Abstract.tupleArgs(reslt))
          vars.add(Abstract.getId((IAbstract) arg));
      else
        vars.add(Abstract.getId(reslt));

      CompilerUtils.extendCondition(cond, CompilerUtils.boundTo(loc, Abstract.binary(loc, Abstract.unary(loc,
          "_dotSlashSearch", pttrn), pthVar, reslt), ptnVar));
      vars.add(Abstract.getId(ptnVar));
      vars.add(Abstract.getId(pthVar));
      return ptnVar;
    } else {
      errors.reportError("lhs of ./ should be a macro variable, not: " + lhs, lhs.getLoc());
      return new Name(loc, GenSym.genSym("_"));
    }
  }

  private static Pair<IAbstract, IAbstract> genSubPttrn(IAbstract ptn, ErrorReport errors, Set<String> vars,
      IAbstract locationVar)
  {
    Location loc = ptn.getLoc();

    // We build a search 'engine' along the lines of:
    //
    // (var...var) from ptn$(ptn) -- where vars are the macro variables in ptn
    Set<String> subVars = new HashSet<>(vars);
    Wrapper<IAbstract> cond = Wrapper.create(null);
    IAbstract match = compilePtn(ptn, cond, errors, subVars, locationVar);
    List<IAbstract> extracted = new ArrayList<>();
    for (String vr : subVars) {
      if (!vars.contains(vr))
        extracted.add(Abstract.name(loc, vr));
    }
    IAbstract reslt = Abstract.tupleTerm(loc, extracted);
    if (!CompilerUtils.isTrivial(cond.get()))
      match = Abstract.binary(loc, StandardNames.WHERE, match, cond.get());

    IAbstract pttrnName = new Name(loc, GenSym.genSym(StandardNames.PATTERN));

    IAbstract let = CompilerUtils.letExp(loc, CompilerUtils.blockTerm(loc, CompilerUtils.pattern(loc, CompilerUtils
        .patternRule(loc, Abstract.unary(loc, pttrnName, reslt), match))), pttrnName);

    return Pair.pair(let, reslt);
  }

  private static IAbstract compileReplacement(final IAbstract repl, MacroDict dict, final ErrorReport errors,
      final Set<String> vars, final Wrapper<IAbstract> counterVar, final IAbstract locationVar, IAbstract replaceVar,
      final IAbstract outerVar)
  {
    final Location loc = repl.getLoc();

    if (Abstract.isUnary(repl, StandardNames.QUESTION) && Abstract.isIdentifier(Abstract.unaryArg(repl)))
      return Abstract.unaryArg(repl);
    else if (Abstract.isBinary(repl, StandardNames.MACRO_APPLY)) {
      IAbstract replOp = compileReplacement(Abstract.binaryLhs(repl), dict, errors, vars, counterVar, locationVar,
          replaceVar, outerVar);
      IAbstract replArgs = compileReplacement(Abstract.binaryRhs(repl), dict, errors, vars, counterVar, locationVar,
          replaceVar, outerVar);
      return Abstract.ternary(loc, "__macro_apply", locationVar, replOp, replArgs);
    } else if (Abstract.isUnary(repl, StandardNames.ERROR))
      return Abstract.binary(loc, MacroError.name, locationVar, compileMsg(locationVar, Abstract.unaryArg(repl), dict,
          vars, errors, counterVar, locationVar, replaceVar, outerVar));
    else if (Abstract.isBinary(repl, StandardNames.ERROR))
      return Abstract.binary(loc, MacroError.name, locationVar, compileMsg(locationVar, Abstract.binaryLhs(repl), dict,
          vars, errors, counterVar, locationVar, replaceVar, outerVar));
    else if (Abstract.isUnary(repl, StandardNames.WARNING))
      return Abstract.unary(loc, "__macro_warning", compileMsg(locationVar, Abstract.unaryArg(repl), dict, vars,
          errors, counterVar, locationVar, replaceVar, outerVar));
    else if (Abstract.isUnary(repl, StandardNames.INFO))
      return Abstract.unary(loc, "__macro_info", compileMsg(locationVar, Abstract.unaryArg(repl), dict, vars, errors,
          counterVar, locationVar, replaceVar, outerVar));
    else if (Abstract.isUnary(repl, StandardNames.MACRO_GEN)) {
      IAbstract pr = Abstract.unaryArg(repl);
      IAbstract counter = counterVar.get();
      if (counter == null) {
        counter = new Name(loc, GenSym.genSym("__counter"));
        counterVar.set(counter);
      }
      if (pr instanceof Name) {
        String prefix = ((Name) pr).getId();
        return astName(loc, locationVar, Abstract.binary(loc, StringConcat.name, new StringLiteral(loc, prefix),
            counter));
      } else if (pr instanceof StringLiteral)
        return astName(loc, locationVar, Abstract.binary(loc, StringConcat.name, pr, counter));
      else {
        errors.reportError("expecting an identifier or string", repl.getLoc());
        return null;
      }
    } else if (Abstract.isUnary(repl, StandardNames.MACRO_FORCE))
      return genReplace(loc, replaceVar, compileReplacement(Abstract.unaryArg(repl), dict, errors, vars, counterVar,
          locationVar, replaceVar, outerVar), vars, dict);
    else if (Abstract.isBinary(repl, StandardNames.WFF_DEFINES) && Abstract.isIdentifier(Abstract.binaryRhs(repl))) {
      IAbstract term = compileReplacement(Abstract.binaryLhs(repl), dict, errors, vars, counterVar, locationVar,
          replaceVar, outerVar);
      return Abstract.binary(loc, AstWithCategory.name, term, Abstract.newString(loc, Abstract.getId(Abstract
          .binaryRhs(repl))));
    } else if (Abstract.isUnary(repl, StandardNames.META_HASH)
        && Abstract.isName(Abstract.unaryArg(repl), StandardNames.MACRO_LOCATION))
      return astString(loc, locationVar, Abstract.unary(loc, StandardTypes.STRING, Abstract.unary(loc,
          DisplayLocation.name, locationVar)));
    else if (Abstract.isBinary(repl, StandardNames.MACRO_CATENATE)) {
      IAbstract lhs = compileReplacement(Abstract.binaryLhs(repl), dict, errors, vars, counterVar, locationVar,
          replaceVar, outerVar);
      IAbstract rhs = compileReplacement(Abstract.binaryRhs(repl), dict, errors, vars, counterVar, locationVar,
          replaceVar, outerVar);
      return astName(loc, locationVar, Abstract.binary(loc, "_macro_catenate", genReplace(loc, replaceVar, lhs, vars,
          dict), genReplace(loc, replaceVar, rhs, vars, dict)));
    } else if (Abstract.isBinary(repl, StandardNames.DOTSLASH))
      return dotSlashReplace(loc, repl, dict, errors, vars, counterVar, locationVar, replaceVar, outerVar);
    else if (Abstract.isBinary(repl, StandardNames.MACRO_LOG)) {
      IAbstract traceAction = Abstract.unary(loc, SimpleLog.name, compMsg(Abstract.binaryLhs(repl), dict, errors, vars,
          counterVar, locationVar, replaceVar, outerVar));

      return CompilerUtils.valofValis(loc, compileReplacement(Abstract.binaryRhs(repl), dict, errors, vars, counterVar,
          locationVar, replaceVar, outerVar), traceAction);
    } else if (Abstract.isUnary(repl, StandardNames.MACRO_IDENT))
      return astString(loc, locationVar, compMsg(Abstract.unaryArg(repl), dict, errors, vars, counterVar, locationVar,
          replaceVar, outerVar));
    else if (Abstract.isUnary(repl, StandardNames.MACRO_INTERN))
      return astName(loc, locationVar, Abstract.unary(loc, MacroDisplay.name,
          genReplace(loc, replaceVar, compileReplacement(Abstract.unaryArg(repl), dict, errors, vars, counterVar,
              locationVar, replaceVar, outerVar), vars, dict)));
    else if (Abstract.isUnary(repl, StandardNames.MACRO_DETUPLE))
      return Abstract.unary(loc, "__macro_detupleize", genReplace(loc, replaceVar, compileReplacement(Abstract
          .unaryArg(repl), dict, errors, vars, counterVar, locationVar, replaceVar, outerVar), vars, dict));
    else if (Abstract.isUnary(repl, StandardNames.MACRO_TUPLE))
      return Abstract.binary(loc, "__macro_tupleize", locationVar, genReplace(loc, replaceVar, compileReplacement(
          Abstract.unaryArg(repl), dict, errors, vars, counterVar, locationVar, replaceVar, outerVar), vars, dict));
    else if (repl instanceof Name) {
      String key = Abstract.getId(repl);
      if (vars.contains(key))
        return repl;
      else if (dict.defines(key)) {
        MacroDescriptor desc = dict.get(key);
        switch (desc.type()) {
        case macroRule:
          return new Apply(loc, desc.getInvokeName(loc), stdMacroArgs(astName(loc, locationVar, key), locationVar,
              replaceVar, outerVar));
        case macroVar:
          return Abstract.zeroary(loc, desc.getInvokeName(loc));
        case quotedFun:
        case builtin:
        default:
          return astName(loc, locationVar, key);
        }
      } else
        return astName(loc, locationVar, key);
    } else if (Abstract.isBinary(repl, StandardNames.MACRO_WHERE)
        && CompilerUtils.isBlockTerm(Abstract.binaryRhs(repl))) {
      IAbstract subRules = CompilerUtils.blockContent(Abstract.binaryRhs(repl));
      if (subRules != null) {
        final IAbstract letDriver = Abstract.name(loc, GenSym.genSym("%let"));

        MacroRulesHandler handler = new MacroRulesHandler() {
          @Override
          public void handleImport(IAbstract stmt, MacroDict dict)
          {
            errors.reportError(StringUtils.msg("import ", stmt, " not support here"), stmt.getLoc());
          }

          @Override
          public IAbstract generateResult(List<IAbstract> macroRules, List<IAbstract> otherRules, IAbstract replacer,
              IAbstract replaceVar, MacroDict dict)
          {
            List<IAbstract> localRules = genWalker(loc, replacer, replaceVar, letDriver);

            IAbstract lRepl = genReplace(loc, replaceVar, compileReplacement(Abstract.binaryLhs(repl), dict, errors,
                vars, counterVar, locationVar, replaceVar, letDriver), vars, dict);

            localRules.addAll(macroRules);
            localRules.addAll(otherRules);

            return CompilerUtils.letExp(loc, CompilerUtils.blockTerm(loc, localRules), lRepl);
          }
        };

        return compileRules(loc, CompilerUtils.unWrap(subRules), errors, dict, vars, findKeys(Abstract.binaryLhs(repl),
            errors), letDriver, handler, false);
      } else
        return compileReplacement(Abstract.binaryLhs(repl), dict, errors, vars, counterVar, locationVar, replaceVar,
            outerVar);
    } else if (repl instanceof Apply) {
      Apply apply = (Apply) repl;
      List<IAbstract> replArgs = new ArrayList<>();
      for (IValue arg : apply.getArgs())
        replArgs.add(compileReplacement((IAbstract) arg, dict, errors, vars, counterVar, locationVar, replaceVar,
            outerVar));

      IAbstract op = apply.getOperator();

      if (Abstract.isIdentifier(op)) {
        String opName = Abstract.getId(op);
        IAbstract opRepl = vars.contains(opName) ? op : astName(loc, locationVar, opName);
        IAbstract replTerm = astApply(loc, locationVar, opRepl, replArgs);

        String key = patternKey(repl, errors);
        if (dict.defines(key)) {
          MacroDescriptor desc = dict.find(key);
          switch (desc.type()) {
          case macroRule:
            // return localGenReplace(replTerm, replaceVar, loc);
            return replTerm;
            // return genReplace(loc, replaceVar, replTerm, vars, dict);
          case macroVar:
            return Abstract.zeroary(loc, opRepl);
          case quotedFun:
          case builtin:
            return new Apply(loc, desc.getInvokeName(loc), replArgs);
          default:
            return replTerm;
          }
        } else
          return replTerm;
      } else {
        IAbstract replOp = compileReplacement(op, dict, errors, vars, counterVar, locationVar, replaceVar, outerVar);

        return astApply(loc, locationVar, replOp, replArgs);
      }
    } else if (repl instanceof StringLiteral)
      return Abstract.binary(loc, StringLiteral.name, locationVar, repl);
    else if (repl instanceof IntegerLiteral)
      return Abstract.binary(loc, IntegerLiteral.name, locationVar, repl);
    else if (repl instanceof LongLiteral)
      return Abstract.binary(loc, LongLiteral.name, locationVar, repl);
    else if (repl instanceof FloatLiteral)
      return Abstract.binary(loc, FloatLiteral.name, locationVar, repl);
    else if (repl instanceof BigDecimalLiteral)
      return Abstract.binary(loc, BigDecimalLiteral.name, locationVar, repl);
    else if (repl instanceof BooleanLiteral)
      return Abstract.binary(loc, BooleanLiteral.name, locationVar, repl);
    else if (repl instanceof CharLiteral)
      return Abstract.binary(loc, CharLiteral.name, locationVar, repl);
    else {
      errors.reportError("(internal) cannot handle macro replacement term: " + repl, loc);
      return repl;
    }
  }

  private static Set<String> findKeys(IAbstract term, final ErrorReport errors)
  {
    final Set<String> keys = new HashSet<>();

    IAbstractVisitor finder = new DefaultAbstractVisitor() {

      @Override
      public void visitName(Name name)
      {
        if (!keys.contains(name.getId()))
          keys.add(name.getId());
      }

      @Override
      public void visitApply(Apply app)
      {
        if (Abstract.isIdentifier(app.getOperator())) {
          String key = patternKey(app, errors);
          keys.add(key);
        }

        super.visitApply(app);
      }
    };

    term.accept(finder);
    return keys;
  }

  private static IAbstract genReplace(Location loc, IAbstract replVar, IAbstract repl, Set<String> vars, MacroDict dict)
  {
    while (Abstract.isUnary(repl) && dict.isReplaceVar(Abstract.getOperator(repl))) {
      if (Abstract.isUnary(repl, Abstract.getId(replVar)))
        return repl;
      else
        repl = Abstract.unaryArg(repl);
    }

    return localGenReplace(repl, replVar, loc);
  }

  private static IAbstract localGenReplace(IAbstract repl, IAbstract replVar, Location loc)
  {
    return Abstract.unary(loc, replVar, repl);
  }

  private static IAbstract dotSlashReplace(Location loc, IAbstract repl, MacroDict dict, ErrorReport errors,
      Set<String> vars, Wrapper<IAbstract> counterVar, IAbstract locationVar, IAbstract replace, IAbstract outer)
  {
    assert Abstract.isBinary(repl, StandardNames.DOTSLASH);
    IAbstract lhs = Abstract.binaryLhs(repl);

    if (Abstract.isUnary(lhs, StandardNames.QUESTION))
      lhs = Abstract.unaryArg(lhs);

    if (!Abstract.isIdentifier(lhs)) {
      errors.reportError("expecting a variable, not " + lhs, lhs.getLoc());
      return Abstract.name(loc, StandardNames.VOID);
    }

    if (Abstract.isBinary(Abstract.binaryRhs(repl), StandardNames.MAP_ARROW)) {
      IAbstract tgt = compileReplacement(Abstract.binaryLhs(Abstract.binaryRhs(repl)), dict, errors, vars, counterVar,
          locationVar, replace, outer);
      IAbstract rep = compileReplacement(Abstract.binaryRhs(Abstract.binaryRhs(repl)), dict, errors, vars, counterVar,
          locationVar, replace, outer);
      return Abstract.ternary(loc, "__macro_substitute", lhs, tgt, rep);
    } else {
      String pathVar = Abstract.getId(lhs) + PATH_DOT;
      if (!vars.contains(pathVar)) {
        errors.reportError("lhs of ./ must also be lhs of ./ in pattern", lhs.getLoc());
        return Abstract.name(loc, StandardNames.VOID);
      }

      IAbstract subRepl = compileReplacement(Abstract.binaryRhs(repl), dict, errors, vars, counterVar, locationVar,
          replace, outer);
      return Abstract.ternary(loc, AstReplace.name, lhs, Abstract.name(loc, pathVar), subRepl);
    }
  }

  private static IAbstract compileMsg(IAbstract locVar, IAbstract repl, MacroDict dict, Set<String> vars,
      ErrorReport errors, Wrapper<IAbstract> counterVar, IAbstract locationVar, IAbstract replace, IAbstract outer)
  {
    Location loc = repl.getLoc();

    return astString(loc, locVar, compMsg(repl, dict, errors, vars, counterVar, locationVar, replace, outer));
  }

  private static IAbstract compMsg(IAbstract repl, MacroDict dict, ErrorReport errors, Set<String> vars,
      Wrapper<IAbstract> counterVar, IAbstract locationVar, IAbstract replace, IAbstract outer)
  {
    Location loc = repl.getLoc();

    if (Abstract.isBinary(repl, StandardNames.STRING_CATENATE)) {
      IAbstract lhs = compMsg(Abstract.binaryLhs(repl), dict, errors, vars, counterVar, locationVar, replace, outer);
      IAbstract rhs = compMsg(Abstract.binaryRhs(repl), dict, errors, vars, counterVar, locationVar, replace, outer);
      return Abstract.binary(loc, StringConcat.name, lhs, rhs);
    } else if (Abstract.isUnary(repl, StandardNames.DISPLAY))
      return Abstract.unary(loc, DisplayAst.name, compileReplacement(Abstract.unaryArg(repl), dict, errors, vars,
          counterVar, locationVar, replace, outer));
    else if (Abstract.isUnary(repl, StandardNames.QUESTION))
      return compMsg(Abstract.unaryArg(repl), dict, errors, vars, counterVar, locationVar, replace, outer);
    else if (Abstract.isUnary(repl, StandardNames.MACRO_IDENT))
      return Abstract.unary(loc, MacroDisplay.name, genReplace(loc, replace, compileReplacement(
          Abstract.unaryArg(repl), dict, errors, vars, counterVar, locationVar, replace, outer), vars, dict));
    else if (repl instanceof StringLiteral)
      return repl;
    else
      return Abstract.unary(loc, MacroDisplay.name, compileReplacement(repl, dict, errors, vars, counterVar,
          locationVar, replace, outer));
  }

  public static ResourceURI macroUri(ResourceURI uri)
  {
    return URIUtils.uriWithQuery(uri, MACRO_QUERY);
  }

  public static IAbstract astApply(Location loc, IAbstract aloc, IAbstract op, IAbstract args)
  {
    return Abstract.ternary(loc, Apply.name, aloc, op, args);
  }

  public static IAbstract astApply(Location loc, IAbstract aloc, IAbstract op, List<IAbstract> args)
  {
    return astApply(loc, aloc, op, consTerm(loc, args));
  }

  public static IAbstract astName(Location loc, IAbstract aloc, String name)
  {
    return Abstract.binary(loc, Name.name, aloc, new StringLiteral(loc, name));
  }

  public static IAbstract astName(Location loc, IAbstract aloc, IAbstract name)
  {
    return Abstract.binary(loc, Name.name, aloc, name);
  }

  public static IAbstract astInteger(Location loc, IAbstract aloc, int ix)
  {
    return Abstract.binary(loc, IntegerLiteral.name, aloc, Abstract.newInteger(loc, ix));
  }

  public static IAbstract astLong(Location loc, IAbstract aloc, long ix)
  {
    return Abstract.binary(loc, LongLiteral.name, aloc, Abstract.newLong(loc, ix));
  }

  public static IAbstract astChar(Location loc, IAbstract aloc, int cx)
  {
    return Abstract.binary(loc, CharLiteral.name, aloc, Abstract.newChar(loc, cx));
  }

  public static IAbstract astString(Location loc, IAbstract aloc, IAbstract str)
  {
    return Abstract.binary(loc, StringLiteral.name, aloc, str);
  }

  public static IAbstract consTerm(Location loc, List<IAbstract> args)
  {
    return partitionArgs(loc, args, 0, args.size());
  }

  private static IAbstract partitionArgs(Location loc, List<IAbstract> args, int from, int to)
  {
    assert to >= from;

    if (to == from)
      return Abstract.zeroary(loc, ArrayNil.name);
    else if (to == from + 1)
      return Abstract.unary(loc, UnaryArray.name, args.get(from));
    else if (to == from + 2)
      return Abstract.binary(loc, BinaryArray.name, args.get(from), args.get(from + 1));
    else if (to == from + 3)
      return Abstract.ternary(loc, TernaryArray.name, args.get(from), args.get(from + 1), args.get(from + 2));
    else {
      int s = (to + from) / 2;
      IAbstract low = partitionArgs(loc, args, from, s);
      IAbstract high = partitionArgs(loc, args, s, to);
      return Abstract.binary(loc, ArrayConcatenate.name, low, high);
    }
  }
}
