package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.FreeVariables;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.canonical.DefaultTransformer;
import org.star_lang.star.compiler.canonical.FieldAccess;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.Overloaded;
import org.star_lang.star.compiler.canonical.OverloadedFieldAccess;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.RecordTerm;
import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.ContractDependencies;
import org.star_lang.star.compiler.type.DictInfo;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Refresher;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeContracts;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.type.TypeUtils.CompareTypes;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.UndoableHash;
import org.star_lang.star.compiler.util.UndoableMap;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/**
 * Refactored overloader using the transformation pattern
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
public class Over extends DefaultTransformer<OverContext>
{
  private final UndoableMap<String, IContentExpression> substitutions = new UndoableHash<String, IContentExpression>();

  @Override
  public IContentExpression transformFunctionLiteral(FunctionLiteral fun, OverContext context)
  {
    int mark = context.markDict();

    IType funType = fun.getType();
    if (TypeUtils.isOverloadedType(funType)) {
      IContentPattern args[] = fun.getArgs();
      for (IContentPattern arg : args) {
        if (arg instanceof Variable)
          context.define(arg.getType(), (IContentExpression) arg);
      }
    }

    IContentPattern trArgs[] = transformPatterns(fun.getArgs(), context);
    IContentExpression trRes = fun.getBody().transform(this, context);

    Variable[] freeVars = FreeVariables.freeFreeVars(trArgs, trRes, context.getLocalCxt());

    String funName = fun.getName();
    IContentExpression newName = substitute(funName);
    if (newName != null) {
      funName = ((Variable) newName).getName();
      funType = newName.getType();
    }
    context.resetDict(mark);

    return new FunctionLiteral(fun.getLoc(), funName, funType, trArgs, trRes, freeVars);
  }

  @Override
  public IContentExpression transformLetTerm(LetTerm let, OverContext context)
  {
    OverContext subCxt = context.fork();

    for (IStatement stmt : let.getEnvironment()) {
      if (stmt instanceof VarEntry) {
        VarEntry vEntry = (VarEntry) stmt;
        if (vEntry.getVarPattern() instanceof Variable) {
          Variable var = vEntry.getVariable();
          subCxt.getLocalCxt().declareVar(var.getName(), var, vEntry.isReadOnly(), vEntry.getVisibility(), true);
        }
      }
    }

    List<IStatement> newEnv = new ArrayList<IStatement>();
    for (IStatement entry : let.getEnvironment()) {
      newEnv.add(entry.transform(this, subCxt));
    }

    return new LetTerm(let.getLoc(), let.getBoundExp().transform(this, subCxt), newEnv);
  }

  @Override
  public IContentAction transformLetAction(LetAction let, OverContext context)
  {
    OverContext subCxt = context.fork();

    for (IStatement stmt : let.getEnvironment()) {
      if (stmt instanceof VarEntry) {
        VarEntry vEntry = (VarEntry) stmt;
        if (vEntry.getVarPattern() instanceof Variable) {
          Variable var = vEntry.getVariable();
          subCxt.getLocalCxt().declareVar(var.getName(), var, vEntry.isReadOnly(), vEntry.getVisibility(), true);
        }
      }
    }

    List<IStatement> newEnv = new ArrayList<IStatement>();
    for (IStatement entry : let.getEnvironment()) {
      newEnv.add(entry.transform(this, subCxt));
    }

    return new LetAction(let.getLoc(), newEnv, let.getBoundAction().transform(this, subCxt));
  }

  @Override
  public IContentExpression transformMemo(MemoExp memo, OverContext context)
  {
    IContentExpression trRes = memo.getMemo().transform(this, context);

    Variable[] freeVars = FreeVariables.freeFreeVars(new Variable[] {}, trRes, context.getLocalCxt());

    return new MemoExp(memo.getLoc(), memo.getMemo().transform(this, context), freeVars);
  }

  @Override
  public IContentExpression transformPatternAbstraction(PatternAbstraction ptn, OverContext context)
  {
    int mark = context.markDict();

    if (TypeUtils.isOverloadedType(ptn.getType())) {
      IContentPattern args[] = ptn.getArgs();
      for (IContentPattern arg : args) {
        if (arg instanceof Variable)
          context.define(arg.getType(), (IContentExpression) arg);
      }
    }

    String ptnName = ptn.getName();
    IContentExpression newName = substitute(ptnName);
    if (newName != null)
      ptnName = ((Variable) newName).getName();

    IContentPattern trMatch = ptn.getMatch().transformPattern(this, context);
    IContentExpression trRes = ptn.getResult().transform(this, context);

    Variable[] freeVars = FreeVariables.freeFreeVars(new IContentPattern[] { trMatch }, trRes, context.getLocalCxt());
    context.resetDict(mark);
    return new PatternAbstraction(ptn.getLoc(), ptnName, ptn.getType(), trMatch, trRes, freeVars);
  }

  @Override
  public IContentExpression transformMethodVariable(MethodVariable method, OverContext context)
  {
    TypeExp contract = (TypeExp) method.getContract();
    Location loc = method.getLoc();
    IType varType = method.getType();

    // First look for primitive implementations
    String name = method.getName();
    String escape = PrimitiveOverloader.getPrimitive((TypeExp) method.getContract(), name);
    if (escape != null)
      return new Variable(method.getLoc(), method.getType(), escape);

    IContentExpression cVar = resolve(loc, contract, context);
    if (cVar == null)
      cVar = context.locateDictVar(contract);
    if (cVar != null && !TypeUtils.isTypeVar(cVar.getType()))
      return FieldAccess.create(loc, varType, cVar, name);
    else
      return method;// May end up being converted at another level
  }

  @Override
  public IContentExpression transformOverloaded(Overloaded over, OverContext context)
  {
    IContentExpression inner = over.getInner().transform(this, context);

    Location loc = over.getLoc();

    IType overType = over.getDictType();

    IType reqs[] = TypeUtils.getOverloadRequirements(overType);
    IContentExpression args[] = new IContentExpression[reqs.length];

    for (int ix = 0; ix < args.length; ix++) {
      IType reqType = reqs[ix];

      assert reqType instanceof TypeExp;

      args[ix] = resolve(loc, reqType, context);

      if (args[ix] == null)
        return over; // not ready to overload, signal for overloading later
    }

    if (inner instanceof Variable)
      inner = ((Variable) inner).underLoad(); // convert an overloaded variable to a regular one.

    return new Resolved(loc, TypeUtils.getOverloadedType(overType), overType, inner, args);
  }

  @Override
  public IContentExpression transformOverloadedFieldAccess(OverloadedFieldAccess dot, OverContext context)
  {
    IContentExpression overRoute = dot.getRecord().transform(this, context);

    Location loc = dot.getLoc();

    IType overType = dot.getDictType();

    IType reqs[] = TypeUtils.getOverloadRequirements(overType);
    IContentExpression args[] = new IContentExpression[reqs.length];

    for (int ix = 0; ix < args.length; ix++) {
      IType reqType = reqs[ix];

      assert reqType instanceof TypeExp;

      args[ix] = resolve(loc, reqType, context);

      if (args[ix] == null)
        return dot; // not ready to overload, signal for overloading later
    }

    return new Resolved(loc, TypeUtils.getOverloadedType(overType), overType, new FieldAccess(loc, overType, overRoute,
        dot.getField()), args);
  }

  @Override
  public IContentExpression transformOverloadVariable(OverloadedVariable var, OverContext context)
  {
    IContentExpression subst = substitute(var.getName());
    if (subst != null && isConsistent(subst.getType(), var.getType(), var.getLoc(), context)) {
      if (subst instanceof OverloadedVariable)
        var = (OverloadedVariable) subst;
      else
        return subst;
    }

    Location loc = var.getLoc();
    String varName = var.getName();

    IType overType = var.getDictType();

    IType reqs[] = TypeUtils.getOverloadRequirements(overType);
    IContentExpression args[] = new IContentExpression[reqs.length];

    for (int ix = 0; ix < args.length; ix++) {
      IType reqType = reqs[ix];

      assert reqType instanceof TypeExp;

      args[ix] = resolve(loc, reqType, context);

      if (args[ix] == null)
        return var; // not ready to overload, signal for overloading later
    }

    return new Resolved(loc, TypeUtils.getOverloadedType(overType), overType, new Variable(loc, overType, varName),
        args);
  }

  @Override
  public IContentExpression transformRecord(RecordTerm record, OverContext context)
  {
    OverContext subCxt = context.fork();
    SortedMap<String, IContentExpression> nEls = new TreeMap<>();

    IContentExpression fun = record.getFun().transform(this, subCxt);

    for (Entry<String, IContentExpression> entry : record.getArguments().entrySet())
      nEls.put(entry.getKey(), entry.getValue().transform(this, subCxt));

    return new RecordTerm(record.getLoc(), record.getType(), fun, nEls, record.getTypes());
  }

  @Override
  public IContentExpression transformResolved(Resolved res, OverContext context)
  {
    IContentExpression over = res.getOver();
    IType dictType = res.getDictType();
    Location loc = res.getLoc();

    IType[] reqTypes = TypeUtils.getOverloadRequirements(dictType);
    if (over instanceof Variable && TypeContracts.isContractFallbackName(((Variable) over).getName())) {
      assert reqTypes.length == 0;
      IContentExpression resolved = resolve(loc, res.getType(), context);

      if (resolved != null && resolved != res)
        return resolved;
      else
        return res;
    } else {
      IContentExpression args[] = res.getDicts();
      IContentExpression nArgs[] = null;
      argLoop: for (int ix = 0; ix < args.length; ix++) {
        IContentExpression arg = args[ix];
        if (arg instanceof Resolved)
          arg = transformResolved((Resolved) arg, context);
        else if (arg == null) // special case for initial resolved case
          arg = resolve(loc, reqTypes[ix], context);

        if (arg != args[ix]) {
          if (nArgs == null) {
            nArgs = new IContentExpression[args.length];
            for (int jx = 0; jx < ix; jx++)
              nArgs[jx] = args[jx];
          }
          nArgs[ix] = arg;
          continue argLoop;
        }

        if (nArgs != null)
          nArgs[ix] = arg;
      }

      if (nArgs != null)
        return new Resolved(loc, TypeUtils.getOverloadedType(dictType), dictType, over, nArgs);
      else
        return res;
    }
  }

  @Override
  public IContentExpression transformVariable(Variable var, OverContext context)
  {
    IContentExpression subst = substitute(var.getName());
    if (subst != null /* && isConsistent( var.getType(), subst.getType(), exp.getLoc()) */)
      return subst;
    else
      return var;
  }

  @Override
  public IContentAction transformVarDeclaration(VarDeclaration var, OverContext context)
  {
    return new VarDeclaration(var.getLoc(), var.getPattern().transformPattern(this, context), var.isReadOnly(), var
        .getValue().transform(this, context));
  }

  @Override
  public IStatement transformVarEntry(VarEntry entry, OverContext context)
  {
    final IContentExpression transformedValue = entry.getValue().transform(this, context);

    return new VarEntry(entry.getDefined(), entry.getLoc(), entry.getVarPattern().transformPattern(this, context),
        transformedValue, entry.isReadOnly(), entry.getVisibility());
  }

  public IContentPattern[] transformPatterns(IContentPattern[] args, OverContext cxt)
  {
    IContentPattern nArgs[] = new IContentPattern[args.length];
    for (int ix = 0; ix < args.length; ix++)
      nArgs[ix] = args[ix].transformPattern(this, cxt);
    return nArgs;
  }

  public IContentExpression resolve(Location loc, IType type, OverContext context)
  {
    type = TypeUtils.deRef(type);

    IContentExpression dictVar = context.locateDictVar(type);
    if (dictVar != null)
      return dictVar;
    else if ((type instanceof TypeExp || type instanceof Type)) {
      String instanceName = instanceFunName(type);
      if (context.isDefinedVar(instanceName))
        return checkInstance(loc, type, instanceName, context);
      else if (TypeUtils.isGroundSurface(type) || (TypeUtils.isVarSurface(type) && !context.varsInScope(type))) {
        instanceName = TypeContracts.contractFallbackName(type);
        if (context.isDefinedVar(instanceName))
          return checkInstance(loc, type, instanceName, context);
      }
    }
    return null;
  }

  private IContentExpression checkInstance(Location loc, IType type, String instanceName, OverContext cxt)
  {
    Dictionary localCxt = cxt.getLocalCxt();
    DictInfo instance = localCxt.varReference(instanceName);
    // If the instance is not null but has a variable type, then it is a
    // marker
    // declaration; not the final one. So we do not resolve for it yet.
    if (instance != null && !TypeUtils.isTypeVar(instance.getType())) {
      IType instanceType = instance.getType();
      IType instType = TypeUtils.isOverloadedType(instanceType) ? TypeUtils.refreshOverloaded(instanceType) : Freshen
          .freshenForUse(instanceType);

      if (TypeUtils.isOverloadedType(instType)) {
        IType instArgs[] = TypeUtils.getOverloadRequirements(instType);
        IType conBoundType = TypeUtils.getOverloadedType(instType);

        try {
          Subsume.subsume(type, conBoundType, loc, localCxt);
        } catch (TypeConstraintException e) {
          cxt.getErrors().reportError("cannot resolve: " + type + "\nbecause " + e.getMessage(),
              Location.merge(loc, e.getLocs()));
          return null;
        }

        IContentExpression args[] = new IContentExpression[instArgs.length];
        for (int ix = 0; ix < args.length; ix++) {
          assert TypeUtils.deRef(instArgs[ix]) instanceof TypeExp;
          TypeExp argType = (TypeExp) instArgs[ix];
          IContentExpression cVar = resolve(loc, argType, cxt);

          if (cVar == null)
            cVar = cxt.locateDictVar(argType);
          if (cVar == null)
            return null;

          args[ix] = cVar;
        }

        IContentExpression over = instance.getVariable();
        if (over instanceof OverloadedVariable)
          over = new Variable(over.getLoc(), instType, ((OverloadedVariable) over).getName());
        return new Resolved(loc, type, instType, over, args);
      } else
        return instance.getVariable();
    }
    return null;
  }

  public static VarEntry instanceFunction(Location loc, String funName, IType implType,
      IContentExpression implementation, Dictionary dict, ErrorReport errors, Visibility visibility)
  {
    IType genImplType = Freshen.generalizeType(implType, dict);
    IType dictType = computeDictionaryType(genImplType, loc, AccessMode.readOnly);

    Dictionary instDict = dict.fork();
    OverContext context = new OverContext(dict, errors, 0);
    Over over = new Over();

    int varNo = 0;

    IType[] reqTypes = TypeUtils.getOverloadRequirements(dictType);
    Variable reqVars[] = new Variable[reqTypes.length];

    IType instArgTypes[] = new IType[reqTypes.length];

    for (int ix = 0; ix < reqTypes.length; ix++) {
      IType reqType = instArgTypes[ix] = reqTypes[ix];

      Variable dictVar = reqVars[ix] = new Variable(loc, reqType, reqType.typeLabel() + varNo++);
      instDict.declareVar(dictVar.getName(), dictVar, AccessMode.readOnly, Visibility.priVate, true);
      context.define(reqType, (IContentExpression) dictVar);
    }

    implementation = implementation.transform(over, context);

    if (reqTypes.length > 0) {
      Variable[] iFree = FreeVariables.freeFreeVars(reqVars, implementation, instDict);

      implementation = new FunctionLiteral(loc, funName, dictType, reqVars, implementation, iFree);
      return new VarEntry(loc, new Variable(loc, dictType, funName), implementation, AccessMode.readOnly, visibility);
    } else {
      Variable[] freeVars = FreeVariables.findFreeVars(implementation, dict);
      implementation = new MemoExp(loc, implementation, freeVars);
      return new VarEntry(loc, new Variable(loc, dictType, funName), implementation, AccessMode.readOnly, visibility);
    }
  }

  public static String instanceFunName(IType type)
  {
    StringBuilder bldr = new StringBuilder();
    String sep = "";
    for (IType arg : TypeUtils.typeArgs(type)) {
      arg = TypeUtils.deRef(arg);
      if (!TypeUtils.isDetermines(arg)) {
        bldr.append(sep);
        IType arg1 = arg;
        arg1 = TypeUtils.deRef(arg1);
        if (TypeUtils.isTypeVar(arg1)) {
          if (((TypeVar) arg1).isReadOnly())
            bldr.append(((TypeVar) arg1).getVarName());
          else
            bldr.append("_");
        } else
          bldr.append(arg1.typeLabel());
        sep = "_";
      }
    }
    return instanceFunName(type.typeLabel(), bldr.toString());
  }

  public static String originalInstanceFunName(IType type)
  {
    StringBuilder bldr = new StringBuilder();
    String sep = "";
    for (IType arg : TypeUtils.typeArgs(type)) {
      arg = TypeUtils.deRef(arg);
      if (!TypeUtils.isDetermines(arg)) {
        bldr.append(sep);

        if (TypeUtils.isTypeVar(arg))
          bldr.append(((TypeVar) arg).getOriginalName());
        else
          bldr.append(arg.typeLabel());
        sep = "_";
      }
    }
    return instanceFunName(type.typeLabel(), bldr.toString());
  }

  public static String instanceFunName(String abTypeName, String typeLabel)
  {
    return abTypeName + "#" + typeLabel;
  }

  public static boolean isInstanceFunName(String name)
  {
    return name.indexOf('#') > 0;
  }

  public static boolean isInstanceFunName(String name, String tp)
  {
    int pos = name.indexOf('#');
    return pos > 0 && StringUtils.lookingAt(name, pos + 1, tp);
  }

  public static String instanceFunName(IAbstract tp)
  {
    tp = CompilerUtils.unwrapQuants(tp);

    if (Abstract.isBinary(tp, StandardNames.WHERE))
      return instanceFunName(Abstract.binaryLhs(tp));
    else if (Abstract.isParenTerm(tp))
      return instanceFunName(Abstract.deParen(tp));
    else {
      StringBuilder bldr = new StringBuilder();

      if (Abstract.isBinary(tp, StandardNames.OVER) || Abstract.isBinary(tp, StandardNames.OF)) {
        IAbstract con = Abstract.deParen(Abstract.binaryLhs(tp));
        IAbstract args = Abstract.binaryRhs(tp);

        if (con instanceof Name)
          bldr.append(TypeContracts.contractImplTypeName(Abstract.getId(con)));
        bldr.append("#");

        if (Abstract.isBinary(args, StandardNames.DETERMINES))
          args = Abstract.binaryLhs(args);

        if (Abstract.isTupleTerm(args) && !Abstract.isParenTerm(args)) {
          String sep = "";
          for (IValue a : Abstract.tupleArgs(args)) {
            bldr.append(sep);
            sep = "_";
            instFunArgName((IAbstract) a, bldr);
          }
        } else
          instFunArgName(args, bldr);
      }

      return bldr.toString();
    }
  }

  private static void instFunArgName(IAbstract arg, StringBuilder bldr)
  {
    arg = Abstract.deParen(arg);
    if (Abstract.isBinary(arg, StandardNames.WHERE))
      instFunArgName(Abstract.binaryLhs(arg), bldr);
    else if (Abstract.isIdentifier(arg))
      bldr.append(Abstract.getId(arg));
    else if (Abstract.isBinary(arg, StandardNames.OF) && Abstract.isIdentifier(Abstract.binaryLhs(arg)))
      bldr.append(Abstract.getId(Abstract.binaryLhs(arg)));
    else if (Abstract.isTupleTerm(arg))
      bldr.append(TypeUtils.tupleLabel(Abstract.tupleArity(arg)));
    else if (CompilerUtils.isAnonAggConLiteral(arg))
      bldr.append(CompilerUtils.anonRecordTypeLabel(arg));
    else
      bldr.append("_");
  }

  public static IContentExpression resolve(Dictionary cxt, ErrorReport errors, IContentExpression exp)
  {
    Over overloader = new Over();
    return exp.transform(overloader, new OverContext(cxt, errors, 0));
  }

  public static IContentAction resolve(Dictionary cxt, ErrorReport errors, IContentAction act)
  {
    Over overloader = new Over();
    return act.transform(overloader, new OverContext(cxt, errors, 0));
  }

  public static IStatement overload(ErrorReport errors, VarEntry defn, Dictionary cxt)
  {
    Over overloader = new Over();
    return defn.transform(overloader, new OverContext(cxt, errors, 0));
  }

  private boolean testTheta(List<IStatement> stmts)
  {
    for (IStatement stmt : stmts) {
      if (stmt instanceof VarEntry) {
        VarEntry var = (VarEntry) stmt;
        IType varType = var.getType();
        if (TypeUtils.hasContractDependencies(varType) && !isOverloadedDefn(var))
          return true;
      }
    }
    return false;
  }

  public List<IStatement> overloadTheta(ErrorReport errors, List<IStatement> stmts, Dictionary cxt)
  {
    if (testTheta(stmts)) {
      errors.addToCount("overload theta", stmts.size());
      List<IStatement> result = new ArrayList<IStatement>();
      List<List<IStatement>> groups = ContractDependencies.dependencySort(stmts);

      OverContext subCxt = new OverContext(cxt.fork(), errors, 0);

      for (List<IStatement> group : groups) {
        int mark = substitutionState();

        for (IStatement stmt : group) {

          if (group.size() > 1) {
            // phase I, declare other overloads.
            for (IStatement st : group)
              if (st instanceof VarEntry && st != stmt) {
                VarEntry defn = (VarEntry) st;
                IType varType = defn.getType();

                // Set up a substitution so that we can overload this variable
                if (TypeUtils.hasContractDependencies(varType) && !isOverloadedDefn(defn)) {
                  OverloadedVariable var = (OverloadedVariable) defn.getVariable();

                  defineSubstitution(var.getName(), var);
                }
              }
          }

          if (stmt instanceof VarEntry) {
            VarEntry defn = (VarEntry) stmt;

            IType varType = defn.getType();

            if (TypeUtils.hasContractDependencies(varType) && !TypeUtils.isConstructorType(TypeUtils.unwrap(varType))
                && !isOverloadedDefn(defn)) {
              // We have a generic function that relies on the implementation of a
              // type contract
              // F(P1,..,Pn) is Exp
              //
              // We rewrite this to:
              // F(D) is let{ F'(P1,..,Pn) is Exp } in F'
              // where F is replaced by F' in Exp and F is replaced by F(D) in other expressions

              Location loc = defn.getLoc();
              Variable var = defn.getVariable();

              assert var instanceof OverloadedVariable;

              String vrName = var.getName();

              IContentExpression expression = defn.getValue();

              IType dictType = ((OverloadedVariable) var).getDictType();

              Variable innerVar = new Variable(loc, TypeUtils.getOverloadedType(dictType), vrName + "'");
              IContentExpression old = defineSubstitution(vrName, innerVar);

              IType[] reqTypes = TypeUtils.getOverloadRequirements(dictType);
              Variable reqVars[] = new Variable[reqTypes.length];

              int currDictState = subCxt.markDict();

              for (int ix = 0; ix < reqTypes.length; ix++) {
                IType reqType = reqTypes[ix];

                Variable dictVar = reqVars[ix] = new Variable(loc, reqType, reqType.typeLabel() + subCxt.nextVarNo());
                subCxt.getLocalCxt().declareVar(dictVar.getName(), dictVar, AccessMode.readOnly, Visibility.priVate,
                    true);
                subCxt.define(reqType, (IContentExpression) dictVar);
              }

              IContentExpression resolvedExp = expression.transform(this, subCxt);

              List<IStatement> inner = new ArrayList<IStatement>();

              inner.add(new VarEntry(loc, innerVar, resolvedExp, AccessMode.readOnly, Visibility.priVate));

              resolvedExp = new LetTerm(loc, innerVar, inner);

              Variable[] iFree = FreeVariables.freeFreeVars(reqVars, resolvedExp, cxt);

              FunctionLiteral instFun = new FunctionLiteral(loc, vrName, dictType, reqVars, resolvedExp, iFree);

              // This is carefully done to avoid overloading a definition twice.
              Visibility visibility = defn.getVisibility();
              VarEntry resolved = new VarEntry(loc, Variable.create(loc, varType, vrName), instFun,
                  AccessMode.readOnly, visibility);
              subCxt.getLocalCxt().declareVar(vrName, Variable.create(loc, dictType, vrName), AccessMode.readOnly,
                  visibility, true);

              defineSubstitution(vrName, old); // undo substitution
              subCxt.resetDict(currDictState);
              result.add(resolved);
            } else
              result.add(stmt);
          } else
            result.add(stmt);
        }
        undoSubstitutionState(mark);
      }

      return result;
    } else
      return stmts;
  }

  private static boolean isOverloadedDefn(VarEntry varEntry)
  {
    return TypeUtils.hasContractDependencies(varEntry.getType())
        && TypeUtils.isOverloadedType(varEntry.getValue().getType());
  }

  public static IType computeDictionaryType(IType conType, Location loc, AccessMode access)
  {
    List<TypeVar> uniVars = new ArrayList<TypeVar>();
    IType unwrapped = TypeUtils.unwrap(conType, uniVars);
    Set<IType> reqVars = new TreeSet<>(new CompareTypes());

    findTypeContracts(reqVars, uniVars);

    Map<String, TypeVar> boundMap = new HashMap<String, TypeVar>();

    for (TypeVar tVar : uniVars)
      boundMap.put(tVar.typeLabel(), new TypeVar(tVar.getVarName(), tVar.getOriginalName(), access));

    List<IType> requires = new ArrayList<IType>();

    for (TypeVar tVar : uniVars) {
      TypeVar repl = (TypeVar) boundMap.get(tVar.typeLabel());

      for (ITypeConstraint con : (TypeVar) tVar) {
        if (con instanceof FieldConstraint) {
          FieldConstraint recCon = (FieldConstraint) con;
          TypeUtils.setFieldConstraint(repl, loc, recCon.getField(), Refresher.rewrite(recCon.getType(), boundMap));
        } else if (con instanceof FieldTypeConstraint) {
          FieldTypeConstraint recCon = (FieldTypeConstraint) con;
          TypeUtils.setTypeConstraint(repl, recCon.getName(), Refresher.rewrite(recCon.getType(), boundMap));
        }
      }
    }

    for (IType req : reqVars)
      requires.add(Refresher.rewrite(req, boundMap));

    return UniversalType.universal(boundMap.values(), TypeUtils.overloadedType(requires, Refresher.rewrite(unwrapped,
        boundMap)));
  }

  public static IType computeConstrainedType(IType type)
  {
    if (TypeUtils.isOverloadedType(type)) {
      Map<String, TypeVar> tVars = new HashMap<String, TypeVar>();
      IType tp = TypeUtils.deRef(type);

      while (tp instanceof UniversalType) {
        UniversalType univ = (UniversalType) tp;
        TypeVar bound = univ.getBoundVar();
        String name = bound.getVarName();
        tVars.put(name, new TypeVar(name, bound.getOriginalName(), AccessMode.readOnly));
        tp = univ.getBoundType();
      }

      IType rTp = Refresher.refresh(tp, tVars);

      IType[] requirements = TypeUtils.getOverloadRequirements(rTp);
      for (int ix = 0; ix < requirements.length; ix++) {
        assert requirements[ix] instanceof TypeExp;
        TypeExp contract = (TypeExp) requirements[ix];

        IType argTypes[] = contract.getTypeArgs();
        for (IType aType : argTypes) {
          aType = TypeUtils.deRef(aType);
          if (TypeUtils.isTypeVar(aType))
            ((TypeVar) aType).setConstraint(new ContractConstraint(contract));
        }
      }
      rTp = TypeUtils.getOverloadedType(rTp);

      return UniversalType.universal(tVars.values(), rTp);
    } else
      return type;
  }

  private static void findTypeContracts(Set<IType> reqVars, Collection<TypeVar> typeVars)
  {
    for (TypeVar tv : typeVars) {
      reqLoop: for (ITypeConstraint con : tv) {
        if (con instanceof ContractConstraint) {
          TypeExp req = ((ContractConstraint) con).getContract();
          for (IType conType : reqVars) {
            if (conType.equals(req))
              continue reqLoop;
          }

          reqVars.add(req);
        }
      }
    }
  }

  private boolean isConsistent(IType lhsType, IType rhsType, Location loc, OverContext cxt)
  {
    return Subsume.test(lhsType, rhsType, loc, cxt.getLocalCxt());
  }

  public int substitutionState()
  {
    return substitutions.undoState();
  }

  public void undoSubstitutionState(int mark)
  {
    substitutions.undo(mark);
  }

  protected IContentExpression defineSubstitution(String orig, IContentExpression substitute)
  {
    if (substitute == null)
      return substitutions.remove(orig);
    else
      return substitutions.put(orig, substitute);
  }

  protected IContentExpression substitute(String orig)
  {
    return substitutions.get(orig);
  }

  protected int markSubstitutions()
  {
    return substitutions.size();
  }

}
