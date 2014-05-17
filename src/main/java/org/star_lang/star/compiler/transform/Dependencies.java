package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DefinitionKind;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.StringUtils;

import com.starview.platform.data.IList;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.Location;

/**
 * Dependency analysis module. Sort out functions and other kinds of rules into groups of mutually
 * recursive groups.
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
public class Dependencies
{
  private final Map<String, IAbstract> typeAnnotations = new HashMap<>();
  private final Map<DefinitionKind, Set<String>> privateNames = new HashMap<>();
  private final List<IAbstract> others = new ArrayList<>();
  private final List<Definition> definitions = new ArrayList<>();
  private final List<IAbstract> localActions = new ArrayList<>();
  private final ErrorReport errors;

  private final List<List<Definition>> groups = new ArrayList<>();
  private final Stack<StackEntry> stack = new Stack<>();

  private Dependencies(ErrorReport errors)
  {
    this.errors = errors;
  }

  public static DependencyResults dependencies(ErrorReport errors, List<IAbstract> defs)
  {
    Dependencies deps = new Dependencies(errors);
    deps.thDepend(defs);

    while (!deps.definitions.isEmpty()) {
      deps.analyseDef(deps.definitions.get(0));
    }

    return new DependencyResults(deps.groups, deps.getTypes(), deps.getOthers(), deps.getLocalActions());
  }

  public static class DependencyResults
  {
    private final List<List<Definition>> groups;
    private final Map<String, IAbstract> types;
    private final List<IAbstract> others;
    private final List<IAbstract> localActions;

    public DependencyResults(List<List<Definition>> groups, Map<String, IAbstract> types, List<IAbstract> others,
        List<IAbstract> localActions)
    {
      this.groups = groups;
      this.types = types;
      this.others = others;
      this.localActions = localActions;
    }

    public List<List<Definition>> getDefinitions()
    {
      return groups;
    }

    public Map<String, IAbstract> getTypeAnnotations()
    {
      return types;
    }

    public List<IAbstract> getOthers()
    {
      return others;
    }

    public List<IAbstract> getLocalActions()
    {
      return localActions;
    }
  }

  private int analyseDef(Definition def)
  {
    int low = stack.size();
    int point = low;
    Stack<String> exclusion = new Stack<String>();

    definitions.remove(def);
    stack.push(new StackEntry(low, def));

    IAbstract definition = def.getDefinition();
    DefinitionKind defKind = kindOfStmt(definition);
    point = minPoint(point, analyse(definition, defKind, point, exclusion));
    if (defKind == DefinitionKind.variable) {
      for (String id : def.getDefines(DefinitionKind.variable)) {
        IAbstract annotation = typeAnnotations.get(id);
        if (annotation != null)
          point = minPoint(point, analyse(annotation, DefinitionKind.type, point, exclusion));
      }
    }

    if (low == point) { // We have a new group
      List<Definition> group = new ArrayList<Definition>();

      while (!stack.isEmpty()) {
        StackEntry entry = stack.peek();

        if (entry.depth >= point) {
          group.add(entry.def);
          stack.pop();
        } else
          break;
      }

      if (!group.isEmpty()) {
        groups.add(group);
      }
    }

    return point;
  }

  private int analyse(IAbstract term, DefinitionKind kind, int low, Stack<String> exclusion)
  {
    if (term instanceof Name && !StandardNames.isKeyword(term)) {
      String name = ((Name) term).getId();
      if (exclusion.contains(name))
        return low;
      else {
        for (StackEntry entry : stack) {
          if (entry.def.defines(name, kind))
            return minPoint(entry.depth, low);
        }

        final Definition defn = findDefn(name, kind);

        if (defn != null)
          low = minPoint(low, analyseDef(defn));

        return low;
      }
    } else if (CompilerUtils.isBraceTerm(term)) {
      int mark = exclusion.size();
      addToExclusions(CompilerUtils.braceArg(term), exclusion);
      low = minPoint(analyse(CompilerUtils.braceLabel(term), DefinitionKind.constructor, low, exclusion), analyse(
          CompilerUtils.braceArg(term), kind, low, exclusion));

      while (exclusion.size() > mark)
        exclusion.pop();
      return low;
    } else if (CompilerUtils.isBlockTerm(term)) {
      int mark = exclusion.size();
      addToExclusions(CompilerUtils.blockContent(term), exclusion);
      low = analyse(CompilerUtils.blockContent(term), kind, low, exclusion);

      while (exclusion.size() > mark)
        exclusion.pop();
      return low;
    } else if (Abstract.isUnary(term, StandardNames.PRIVATE))
      return analyse(Abstract.unaryArg(term), kind, low, exclusion);
    else {
      // Try special rules for the different kinds of definition
      switch (kind) {
      case variable:
        if (Abstract.isBinary(term, StandardNames.CAST))
          return minPoint(analyse(Abstract.binaryLhs(term), kind, low, exclusion), analyse(Abstract.binaryRhs(term),
              DefinitionKind.type, low, exclusion));
        else if (CompilerUtils.isTypeAlias(term))
          return analyse(CompilerUtils.typeAliasAlias(term), DefinitionKind.type, low, exclusion);
        else if (CompilerUtils.isTypeDefn(term)) {
          for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(term), StandardNames.OR)) {
            low = minPoint(analyseConstructor(con, low, exclusion), low);
          }
          return low;
        } else if (CompilerUtils.isTypeWitness(term))
          return analyse(CompilerUtils.typeWitness(term), DefinitionKind.type, low, exclusion);
        else if (Abstract.isBinary(term, StandardNames.IS)
            && Abstract.isUnary(Abstract.binaryLhs(term), StandardNames.DEFAULT))
          return analyse(Abstract.binaryRhs(term), DefinitionKind.variable, low, exclusion);
        else if (Abstract.isUnary(term, StandardNames.ASSERT))
          return analyse(Abstract.unaryArg(term), DefinitionKind.variable, low, exclusion);
        else if (CompilerUtils.isTypeVar(term))
          return low;
        else
          break;
      case type:
        if (Abstract.isBinary(term, StandardNames.WHERE))
          return minPoint(analyse(Abstract.binaryLhs(term), kind, low, exclusion), analyse(Abstract.binaryRhs(term),
              DefinitionKind.contract, low, exclusion));
        else if (CompilerUtils.isTypeAlias(term))
          return analyse(CompilerUtils.typeAliasAlias(term), DefinitionKind.type, low, exclusion);
        else if (CompilerUtils.isTypeDefn(term)) {
          low = analyse(CompilerUtils.typeDefnType(term), DefinitionKind.type, low, exclusion);
          for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(term), StandardNames.OR)) {
            low = minPoint(analyseConstructor(con, low, exclusion), low);
          }
          return low;
        } else if (CompilerUtils.isTypeWitness(term))
          return analyse(CompilerUtils.typeWitness(term), DefinitionKind.type, low, exclusion);
        else if (Abstract.isBinary(term, StandardNames.IS)
            && Abstract.isUnary(Abstract.binaryLhs(term), StandardNames.DEFAULT))
          return analyse(Abstract.binaryRhs(term), DefinitionKind.variable, low, exclusion);
        else if (Abstract.isUnary(term, StandardNames.ASSERT))
          return analyse(Abstract.unaryArg(term), DefinitionKind.variable, low, exclusion);
        else if (CompilerUtils.isTypeVar(term))
          return low;
        else
          break;
      case contract:
        if (CompilerUtils.isContractStmt(term))
          return minPoint(analyse(CompilerUtils.contractForm(term), DefinitionKind.contract, low, exclusion), analyse(
              CompilerUtils.contractSpec(term), DefinitionKind.type, low, exclusion));
        else if (Abstract.isBinary(term, StandardNames.IMPLEMENTS))
          return minPoint(analyse(Abstract.binaryLhs(term), DefinitionKind.type, low, exclusion), analyse(Abstract
              .binaryRhs(term), DefinitionKind.type, low, exclusion));
        else
          break;
      case implementation:
        if (CompilerUtils.isImplementationStmt(term))
          return minPoint(analyse(CompilerUtils.implementationBody(term), DefinitionKind.variable, low, exclusion),
              minPoint(analyse(CompilerUtils.implementationContractType(term), DefinitionKind.type, low, exclusion),
                  minPoint(analyse(CompilerUtils.implementedContract(term), DefinitionKind.contract, low, exclusion),
                      analyse(CompilerUtils.implementationBody(term), DefinitionKind.type, low, exclusion))));
        else
          break;
      case imports:
        if (CompilerUtils.isImport(term)) {
          IAbstract imported = Abstract.unaryArg(term);

          if (imported instanceof Apply) {
            Apply apply = (Apply) imported;
            for (IValue arg : apply.getArgs()) {
              low = minPoint(analyse((IAbstract) arg, DefinitionKind.variable, low, exclusion), low);
            }
            return low;
          }
          return low;
        } else if (CompilerUtils.isOpen(term)) {
          IAbstract opened = CompilerUtils.openedRecord(term);
          if (opened instanceof Apply) {
            Apply apply = (Apply) opened;
            for (IValue arg : apply.getArgs()) {
              low = minPoint(analyse((IAbstract) arg, DefinitionKind.variable, low, exclusion), low);
            }
          }
          return low;
        }
      case java:
      case unknown:
      case constructor:
        break;
      }
      if (term instanceof Apply) {
        Apply apply = (Apply) term;
        low = minPoint(low, analyse(apply.getOperator(), kind, low, exclusion));

        for (IValue arg : apply.getArgs()) {
          low = minPoint(analyse((IAbstract) arg, kind, low, exclusion), low);
        }
        return low;
      } else
        return low;
    }
  }

  private int analyseConstructor(IAbstract con, int low, Stack<String> exclusion)
  {
    if (con instanceof Name)
      return low;
    else if (CompilerUtils.isBraceTerm(con)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(con), StandardNames.TERM)) {
        if (Abstract.isBinary(el, StandardNames.IS) && Abstract.isUnary(Abstract.binaryLhs(el), StandardNames.DEFAULT))
          low = analyse(Abstract.binaryRhs(el), DefinitionKind.variable, low, exclusion);
        else if (Abstract.isUnary(el, StandardNames.ASSERT))
          low = analyse(Abstract.unaryArg(el), DefinitionKind.variable, low, exclusion);
        else if (CompilerUtils.isTypeAnnotation(el))
          low = analyse(CompilerUtils.typeAnnotation(el), DefinitionKind.type, low, exclusion);
      }
      return low;
    } else if (Abstract.isRoundTerm(con)) {
      for (IValue el : Abstract.roundTermArgs(con))
        low = analyse((IAbstract) el, DefinitionKind.type, low, exclusion);
      return low;
    } else
      return low;
  }

  private static void addToExclusions(IAbstract term, Stack<String> exclusion)
  {
    if (term != null) {
      for (IAbstract el : CompilerUtils.unWrap(term)) {
        if (Abstract.isBinary(el, StandardNames.EQUAL) && CompilerUtils.isIdentifier(Abstract.binaryLhs(term)))
          exclusion.push(Abstract.getId(Abstract.binaryLhs(el)));
        else if (CompilerUtils.isProgramStmt(el))
          addPatternToExclusions(CompilerUtils.programStmtPattern(el), exclusion);
      }
    }
  }

  private static void addPatternToExclusions(IAbstract term, Stack<String> exclusion)
  {
    if (Abstract.isIdentifier(term))
      exclusion.push(Abstract.getId(term));
    else if (Abstract.isBinary(term, StandardNames.WHERE))
      addPatternToExclusions(Abstract.binaryLhs(term), exclusion);
    else if (Abstract.isRoundTerm(term))
      addPatternToExclusions(Abstract.roundTermArgs(term), exclusion);
    else if (CompilerUtils.isBraceTerm(term)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(term))) {
        if (Abstract.isBinary(el, StandardNames.EQUAL))
          addPatternToExclusions(Abstract.binaryLhs(el), exclusion);
      }
    }
  }

  private static void addPatternToExclusions(IList roundTermArgs, Stack<String> exclusion)
  {
    for (IValue term : roundTermArgs)
      addPatternToExclusions((IAbstract) term, exclusion);
  }

  private static class StackEntry
  {
    final int depth;
    final Definition def;

    StackEntry(int depth, Definition def)
    {
      this.depth = depth;
      this.def = def;
    }

    @Override
    public String toString()
    {
      return def.toString();
    }
  }

  private static int minPoint(int X, int Y)
  {
    if (X <= Y)
      return X;
    else
      return Y;
  }

  private Definition findDefn(String name, DefinitionKind kind)
  {
    for (Definition def : definitions)
      if (def.defines(name, kind))
        return def;
    kind = kind.degenerate();
    if (kind != null)
      return findDefn(name, kind);
    else
      return null;
  }

  private Definition findLocalDefn(String name, DefinitionKind kind)
  {
    for (Definition def : definitions)
      if (def.defines(name, kind) && (kind == DefinitionKind.imports || !def.isKind(DefinitionKind.imports)))
        return def;
    return null;
  }

  public void thDepend(List<IAbstract> theta)
  {
    for (IAbstract stmt : theta) {
      IAbstract term = stmt;
      Visibility visibility = Visibility.pUblic;
      Location loc = term.getLoc();

      if (Abstract.isParenTerm(term))
        term = Abstract.deParen(term);

      if (CompilerUtils.isPrivate(term)) {
        visibility = Visibility.priVate;
        term = CompilerUtils.privateTerm(term);
      }

      if (CompilerUtils.isImplementationStmt(term)) {
        IAbstract conSpec = CompilerUtils.implementedContractSpec(term);
        String implName = Over.instanceFunName(conSpec);

        if (implName != null) {
          Definition def = new Definition(loc, term, implName, DefinitionKind.implementation, visibility);
          definitions.add(def);
        }
      } else if (CompilerUtils.isContractStmt(term)) {
        IAbstract content = CompilerUtils.contractSpec(term);

        IAbstract contractName = CompilerUtils.contractName(term);

        if (Abstract.isIdentifier(contractName)) {
          String contractId = Abstract.getId(contractName);
          Definition conDef = findDefn(contractId, DefinitionKind.contract);
          if (conDef == null) {
            List<String> defines = findContractDefinitions(content);

            Map<DefinitionKind, String[]> kindMap = new HashMap<DefinitionKind, String[]>();
            kindMap.put(DefinitionKind.contract, new String[] { contractId });
            kindMap.put(DefinitionKind.variable, defines.toArray(new String[defines.size()]));
            conDef = new Definition(loc, term, kindMap, visibility);

            definitions.add(conDef);
          } else
            errors.reportError("multiple definitions for " + contractName + " defined, earlier definition at "
                + conDef.getLoc(), loc, conDef.getLoc());
        } else
          errors.reportError("invalid contract spec: " + term, loc);
      } else if (CompilerUtils.isTypeAnnotation(term)) {
        IAbstract lhs = CompilerUtils.typeAnnotatedTerm(term);

        if (Abstract.isIdentifier(lhs))
          typeAnnotations.put(Abstract.getId(lhs), stmt);
        else
          errors.reportError("type annotations apply to identifiers", term.getLoc());

        if (visibility == Visibility.priVate)
          markPrivate(loc, Abstract.getId(lhs), DefinitionKind.variable);
      } else if (Abstract.isBinary(term, StandardNames.IS)) {
        IAbstract lhs = Abstract.binaryLhs(term);

        if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
          lhs = Abstract.unaryArg(lhs);

        if (Abstract.isBinary(lhs, StandardNames.WHERE))
          lhs = Abstract.binaryLhs(lhs);

        lhs = Abstract.deParen(lhs);

        if (lhs instanceof Name) {
          String name = ((Name) lhs).getId();
          addDefinition(loc, name, term, visibility(name, visibility, DefinitionKind.variable), DefinitionKind.variable);
        } else if (Abstract.isTupleTerm(lhs)) {
          List<String> defines = findDefinedNames(lhs, new ArrayList<String>());

          Definition def = new Definition(lhs.getLoc(), term, defines.toArray(new String[defines.size()]),
              DefinitionKind.variable, visibility(defines, visibility, DefinitionKind.variable));
          definitions.add(def);
        } else if (lhs instanceof Apply) { // Must be a function definition
          IAbstract fun = Abstract.deParen(((Apply) lhs).getOperator());

          if (fun instanceof Name) {
            String name = ((Name) fun).getId();
            addDefinition(loc, name, term, visibility(name, visibility, DefinitionKind.variable),
                DefinitionKind.variable);
          } else
            errors.reportError("invalid form of function", term.getLoc());
        } else
          errors.reportError("invalid form of definition statement", term.getLoc());
      } else if (CompilerUtils.isVarDeclaration(term))
        extractDefines(CompilerUtils.varPtnVar(CompilerUtils.varDeclarationPattern(term)), term, visibility);
      else if (CompilerUtils.isIsStatement(term))
        extractDefines(CompilerUtils.isStmtPattern(term), term, visibility);
      else if (Abstract.isUnary(term, StandardNames.VAR)
          && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.ASSIGN)) {
        IAbstract varDec = Abstract.getArg(term, 0);
        IAbstract var = Abstract.getArg(varDec, 0);

        if (var instanceof Name)
          makeDefinition(varDec.getLoc(), Abstract.getId(var), term, visibility, DefinitionKind.variable);
        else
          errors.reportError("invalid form of variable declaration", term.getLoc());
      } else if (CompilerUtils.isOpen(term)) {
        definitions.add(new Definition(loc, term, new String[] {}, DefinitionKind.imports, visibility));
      } else if (CompilerUtils.isBraceTerm(term)) { // Procedure
        IAbstract lhs = Abstract.deParen(CompilerUtils.braceLabel(term));

        if (lhs instanceof Apply) { // Must be a procedure definition
          Apply fPtn = (Apply) lhs;
          IAbstract fun = fPtn.getOperator();

          if (fun instanceof Name)
            makeDefinition(loc, ((Name) fun).getId(), term, visibility, DefinitionKind.variable);
          else
            errors.reportError("invalid form of procedure", loc);
        } else if (lhs instanceof Name)
          addDefinition(loc, ((Name) lhs).getId(), term, visibility, DefinitionKind.variable);
        else
          errors.reportError("invalid form of definition statement", term.getLoc());
      } else if (Abstract.isBinary(term, StandardNames.DO)) {
        IAbstract lhs = Abstract.binaryLhs(term);

        if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
          lhs = Abstract.unaryArg(lhs);

        if (Abstract.isBinary(lhs, StandardNames.WHERE))
          lhs = Abstract.binaryLhs(lhs);

        if (lhs instanceof Apply) { // Must be a procedure definition
          IAbstract prc = ((Apply) lhs).getOperator();

          if (prc instanceof Name) {
            String name = ((Name) prc).getId();
            if (StandardNames.isKeyword(name))
              errors.reportError("keyword " + name + " cannot be used as the name of a procedure", prc.getLoc());

            addDefinition(loc, name, term, visibility, DefinitionKind.variable);
          } else
            errors.reportError("invalid form of action rule", term.getLoc());
        } else
          errors.reportError("invalid form of procedure statement", term.getLoc());
      } else if (CompilerUtils.isTypeAlias(term)) {
        IAbstract definedType = CompilerUtils.typeAliasType(term);
        String tpName = CompilerUtils.typeLabel(definedType);

        makeDefinition(loc, tpName, term, visibility, DefinitionKind.type);
      } else if (CompilerUtils.isTypeDefn(term)) {
        IAbstract definedType = CompilerUtils.typeDefnType(term);
        IAbstract spec = CompilerUtils.typeDefnConstructors(term);
        String tpName = CompilerUtils.typeLabel(definedType);

        Definition tpDef = findDefn(tpName, DefinitionKind.type);
        if (tpDef == null) {
          final ArrayList<String> definedNames = new ArrayList<String>();

          pickupDefinedConstructors(spec, definedNames);

          Map<DefinitionKind, String[]> kindMap = new HashMap<DefinitionKind, String[]>();
          kindMap.put(DefinitionKind.type, new String[] { tpName });
          kindMap.put(DefinitionKind.constructor, definedNames.toArray(new String[definedNames.size()]));
          tpDef = new Definition(loc, term, kindMap, visibility);

          definitions.add(tpDef);
        } else
          errors.reportError("multiple definitions of type: " + tpName + " earlier definition at " + tpDef.getLoc(),
              loc, tpDef.getLoc());
      } else if (CompilerUtils.isTypeWitness(term)) {
        String tpName = CompilerUtils.typeLabel(CompilerUtils.witnessedType(term));
        makeDefinition(loc, tpName, term, visibility, DefinitionKind.type);
      } else if (CompilerUtils.isPatternRule(term)) {
        IAbstract lhs = Abstract.deParen(CompilerUtils.patternRuleHead(term));

        if (lhs instanceof Apply) { // Must be a function definition
          IAbstract fun = Abstract.deParen(((Apply) lhs).getOperator());

          if (fun instanceof Name)
            addDefinition(loc, ((Name) fun).getId(), term, visibility, DefinitionKind.variable);
          else
            errors.reportError("invalid form of pattern abstraction", term.getLoc());
        } else
          errors.reportError("invalid form of definition statement", term.getLoc());
      } else if (CompilerUtils.isEmptyBlock(term))
        ;
      else if (CompilerUtils.isBlockTerm(term))
        localActions.add(Abstract.getArg(term, 0));
      else if (Abstract.isUnary(term, StandardNames.ASSERT))
        localActions.add(term);
      else
        others.add(term);
    }
  }

  private void markPrivate(Location loc, String name, DefinitionKind kind)
  {
    Definition defs = findLocalDefn(name, kind);

    if (defs != null)
      defs.setVisibility(Visibility.priVate);
    else {
      Set<String> prvNames = privateNames.get(kind);
      if (prvNames == null) {
        prvNames = new HashSet<>();
        privateNames.put(kind, prvNames);
      }
      prvNames.add(name);
    }
  }

  private Visibility visibility(String name, Visibility deflt, DefinitionKind kind)
  {
    Set<String> names = privateNames.get(kind);
    if (names != null && names.contains(name))
      return Visibility.priVate;
    else
      return deflt;
  }

  private Visibility visibility(Collection<String> names, Visibility deflt, DefinitionKind kind)
  {
    for (String name : names) {
      Set<String> prNames = privateNames.get(kind);
      if (prNames != null && prNames.contains(name))
        return Visibility.priVate;
    }
    return deflt;
  }

  private void makeDefinition(Location loc, String name, IAbstract stmt, Visibility visibility, DefinitionKind kind)
  {
    Definition defs = findLocalDefn(name, kind);

    visibility = visibility(name, visibility, kind);

    if (defs == null) {
      defs = new Definition(loc, stmt, name, kind, visibility);
      definitions.add(defs);
    } else if (defs.getDefinition() == null)
      defs.addRule(stmt);
    else if (CompilerUtils.isImport(defs.getDefinition())) {
      errors.reportWarning("overriding imported definition of " + name, loc);
      defs = new Definition(loc, stmt, name, kind, visibility);
      definitions.add(defs);
    } else
      errors.reportError("multiple definitions of " + name + " not permitted, other definition at "
          + defs.get().getLoc(), loc, defs.get().getLoc());
  }

  private void addDefinition(Location loc, String name, IAbstract stmt, Visibility visibility, DefinitionKind kind)
  {
    Definition defs = findLocalDefn(name, kind);
    visibility = visibility(name, visibility, kind);

    if (defs == null) {
      defs = new Definition(loc, stmt, name, kind, visibility);
      definitions.add(defs);
    } else if (defs.getDefinition() == null)
      defs.addRule(stmt);
    else if (CompilerUtils.isImport(defs.getDefinition())) {
      errors.reportWarning("overriding imported definition of " + name, loc);
      defs = new Definition(loc, stmt, name, kind, visibility);
      definitions.add(defs);
    } else
      defs.addRule(stmt);
  }

  private void extractDefines(IAbstract lhs, IAbstract term, Visibility visibility)
  {
    Location loc = lhs.getLoc();
    List<String> defined = findDefinedNames(lhs, new ArrayList<String>());
    if (!defined.isEmpty()) {
      visibility = visibility(defined, visibility, DefinitionKind.variable);
      Definition def = new Definition(loc, term, defined.toArray(new String[defined.size()]), DefinitionKind.variable,
          visibility);
      definitions.add(def);
    }
  }

  private List<String> findDefinedNames(IAbstract lhs, List<String> names)
  {
    Location loc = lhs.getLoc();
    if (lhs instanceof Name) {
      String name = ((Name) lhs).getId();
      Definition defs = findLocalDefn(name, DefinitionKind.variable);

      if (defs == null)
        names.add(name);
      else
        errors.reportError(StringUtils.msg("multiple definitions of ", name, " not permitted, other definition at ",
            defs.getLoc()), loc);
    } else if (Abstract.isRoundTerm(lhs)) {
      for (IValue arg : Abstract.roundTermArgs(lhs))
        findDefinedNames((IAbstract) arg, names);
    } else
      errors.reportError("invalid form of variable definition lhs: " + lhs, loc);
    return names;
  }

  private List<String> findContractDefinitions(IAbstract arg)
  {
    List<String> definedByContract = new ArrayList<String>();

    for (IAbstract el : CompilerUtils.unWrap(arg)) {
      if (CompilerUtils.isTypeAnnotation(el)) {
        String id = Abstract.getId(CompilerUtils.typeAnnotatedTerm(el));
        if (!definedByContract.contains(id))
          definedByContract.add(id);
      }
    }

    return definedByContract;
  }

  private void pickupDefinedConstructors(IAbstract arg, ArrayList<String> definedNames)
  {
    if (Abstract.isBinary(arg, StandardNames.OR)) {
      pickupDefinedConstructors(Abstract.getArg(arg, 0), definedNames);
      pickupDefinedConstructors(Abstract.getArg(arg, 1), definedNames);
    } else if (Abstract.isBinary(arg, StandardNames.WHERE))
      pickupDefinedConstructors(Abstract.getArg(arg, 0), definedNames);
    else if (arg instanceof Name)
      definedNames.add(((Name) arg).getId());
    else if (Abstract.isBinary(arg, StandardNames.AGGREGATE) && Abstract.getArg(arg, 0) instanceof Name)
      definedNames.add(((Name) Abstract.getArg(arg, 0)).getId());
    else if (arg instanceof Apply && !(StandardNames.isKeyword(((Apply) arg).getOperator())))
      definedNames.add(((Name) ((Apply) arg).getOperator()).getId());
  }

  private DefinitionKind kindOfStmt(IAbstract stmt)
  {
    if (CompilerUtils.isTypeAlias(stmt))
      return DefinitionKind.type;
    else if (CompilerUtils.isTypeDefn(stmt))
      return DefinitionKind.type;
    else if (CompilerUtils.isTypeWitness(stmt))
      return DefinitionKind.type;
    else if (CompilerUtils.isContractStmt(stmt))
      return DefinitionKind.contract;
    else if (CompilerUtils.isImplementationStmt(stmt))
      return DefinitionKind.implementation;
    else if (CompilerUtils.isJavaStmt(stmt))
      return DefinitionKind.java;
    else if (CompilerUtils.isOpen(stmt))
      return DefinitionKind.imports;
    else
      return DefinitionKind.variable;
  }

  public Map<String, IAbstract> getTypes()
  {
    return typeAnnotations;
  }

  public List<IAbstract> getOthers()
  {
    return others;
  }

  public List<IAbstract> getLocalActions()
  {
    return localActions;
  }
}
