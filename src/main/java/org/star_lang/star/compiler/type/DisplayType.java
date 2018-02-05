package org.star_lang.star.compiler.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.AbstractType;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

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

public class DisplayType implements ITypeVisitor<Integer>
{
  private final PrettyPrintDisplay disp;
  private final LayeredMap<String, TypeVar> usedNames;
  private final LayeredMap<IType, String> quants;

  public DisplayType(PrettyPrintDisplay disp)
  {
    this.disp = disp;
    this.usedNames = new LayeredHash<>();
    this.quants = new LayeredHash<>();
  }

  private DisplayType(PrettyPrintDisplay disp, LayeredMap<String, TypeVar> used, LayeredMap<IType, String> quants)
  {
    this.disp = disp;
    this.usedNames = used;
    this.quants = quants;
  }

  public static void display(PrettyPrintDisplay disp, IType type)
  {
    display(disp, type, Operators.EXPRESSION_PRIORITY);
  }

  public static void display(PrettyPrintDisplay disp, IType type, int priority)
  {
    DisplayType display = new DisplayType(disp);

    display.show(type, priority);

    display.showConstraints(type);
  }

  public static void display(PrettyPrintDisplay disp, IType type, Map<String, TypeVar> vars,
      LayeredMap<IType, String> quants)
  {
    LayeredMap<String, TypeVar> used = new LayeredHash<>(vars);
    DisplayType dType = new DisplayType(disp, used, quants);
    dType.show(type, Operators.EXPRESSION_PRIORITY);
  }

  public void display(IType type)
  {
    show(type, Operators.EXPRESSION_PRIORITY);
    showConstraints(type);
  }

  public PrettyPrintDisplay getDisp()
  {
    return disp;
  }

  private void showConstraints(IType type)
  {
    Collection<ITypeConstraint> constraints = ConstraintFinder.findConstraints(type);

    if (!constraints.isEmpty()) {
      String sep = StandardNames.WHERE;
      for (ITypeConstraint constraint : constraints) {
        if (constraint instanceof HasKind) {
          HasKind hasKind = (HasKind) constraint;
          if (hasKind.getKind().equals(Kind.type) || quants.containsKey(hasKind.getVar()))
            continue;
        }
        disp.appendWord(sep);
        sep = StandardNames.AND;
        constraint.showConstraint(this);
      }
    }
  }

  private static void showConstraints(DisplayType display, Collection<IType> types)
  {
    Collection<ITypeConstraint> constraints = ConstraintFinder.findConstraints(types);
    if (!constraints.isEmpty()) {
      String sep = StandardNames.WHERE;
      for (ITypeConstraint constraint : constraints) {
        if (constraint instanceof HasKind && ((HasKind) constraint).getKind().equals(Kind.type))
          continue;
        display.disp.appendWord(sep);
        sep = StandardNames.AND;
        constraint.showConstraint(display);
      }
    }
  }

  public static void displayContract(PrettyPrintDisplay disp, IType type)
  {
    DisplayType display = new DisplayType(disp);

    display.displayContract((TypeExp) type);
    display.showConstraints(type);
  }

  public static String showContract(TypeExp tp)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    DisplayType display = new DisplayType(disp);

    display.displayContract(tp);
    display.showConstraints(tp);
    return disp.toString();
  }

  public static String toString(IType type)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    display(disp, type);
    return disp.toString();
  }

  public void show(IType type, Integer priority)
  {
    type = TypeUtils.deRef(type);

    if (quants.containsKey(type))
      disp.appendWord(quants.get(type));
    else {
      String tpLabel = typeLabel(type);
      if (Operators.operatorRoot().canDominate(tpLabel, priority)) {
        disp.append("#(");
        type.accept(this, priority);
        disp.append(")#");
      } else
        type.accept(this, priority);
    }
  }

  private String typeLabel(IType type)
  {
    if (type instanceof UniversalType)
      return StandardNames.FOR_ALL;
    else if (type instanceof ExistentialType)
      return StandardNames.EXISTS;
    else
      return type.typeLabel();
  }

  @Override
  public void visitSimpleType(Type t, Integer priority)
  {
    disp.appendId(t.typeLabel());
  }

  @Override
  public void visitTypeExp(TypeExp t, Integer priority)
  {
    IType typeArgs[] = t.getTypeArgs();

    if (TypeUtils.isFunType(t)) {
      show(TypeUtils.getFunArgType(t), Operators.FUN_TYPE_PRIORITY - 1);
      disp.appendWord(StandardNames.FUN_ARROW);
      show(TypeUtils.getFunResultType(t), Operators.FUN_TYPE_PRIORITY);
    } else if (TypeUtils.isConstructorType(t)) {
      show(TypeUtils.getConstructorArgType(t), Operators.FUN_TYPE_PRIORITY - 1);
      disp.append(" <=> ");
      show(TypeUtils.getConstructorResultType(t), Operators.FUN_TYPE_PRIORITY);
    } else if (TypeUtils.isOverloadedType(t)) {
      show(typeArgs[0], Operators.FUN_TYPE_PRIORITY - 1);
      disp.appendWord(AbstractType.OVERLOADED_TYPE);
      show(typeArgs[1], Operators.FUN_TYPE_PRIORITY);
    } else if (TypeUtils.isPatternType(t)) {
      show(TypeUtils.getPtnResultType(t), Operators.FUN_TYPE_PRIORITY - 1);
      disp.appendWord(AbstractType.PTN_TYPE);
      show(TypeUtils.getPtnMatchType(t), Operators.PTN_TYPE_PRIORITY);
    } else if (TypeUtils.isTupleType(t))
      assert false : "should not happen (tpl 3)";
    else if (TypeUtils.isReferenceType(t)) {
      disp.appendWord(StandardNames.REF);
      show(TypeUtils.referencedType(t), Operators.REF_PRIORITY);
    } else {
      IType tyCon = TypeUtils.deRef(t.getTypeCon());

      if (tyCon instanceof Type) {
        String label = tyCon.typeLabel();

        disp.appendId(label);
      } else
        show(tyCon, Operators.OF_PRIORITY - 1);

      if (typeArgs.length > 0) {
        disp.append(" of ");
        typeArgs(typeArgs);
      }
    }
  }

  @Override
  public void visitTupleType(TupleType t, Integer cxt)
  {
    typeArgs(t.getElTypes(), t.arity());
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Integer priority)
  {
    if (t.getAllFields().size() + t.getAllTypes().size() == 0)
      disp.append("{}");
    else {
      disp.append("{");
      int mark = disp.markIndent(2);
      String sep = "\n";
      for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
        disp.append(sep);
        sep = ";\n";
        String field = entry.getKey();
        disp.appendId(field);
        disp.appendWord(StandardNames.HAS_TYPE);
        show(entry.getValue(), Operators.HAS_TYPE_PRIORITY);
      }

      for (Entry<String, IType> entry : t.getAllTypes().entrySet()) {
        disp.append(sep);
        sep = ";\n";
        IType boundType = TypeUtils.deRef(entry.getValue());

        if (quants.containsKey(boundType)) {
          Kind kind = TypeUtils.typeKind(boundType);
          disp.appendId(entry.getKey());
          disp.appendWord("has kind ");
          if (kind != Kind.unknown)
            kind.prettyPrint(disp);
          else
            Kind.type.prettyPrint(disp);
        } else {
          disp.appendWord(StandardNames.TYPE);
          disp.appendId(entry.getKey());
          disp.append("=");
          show(entry.getValue(), Operators.EQUAL_PRIORITY - 1);
        }
      }
      disp.popIndent(mark);
      disp.append("\n}");
    }
  }

  public void typeArgs(IType[] typeArgs)
  {
    if (typeArgs.length > 0) {
      int limit = typeArgs.length - 1;
      IType det = TypeUtils.deRef(typeArgs[limit]);
      if (TypeUtils.isType(det, StandardNames.DETERMINES)) {
        if (limit == 1)
          show(typeArgs[0], Operators.DETERMINES_PRIORITY - 1);
        else
          typeArgs(typeArgs, limit);
        disp.appendWord(StandardNames.DETERMINES);
        typeArgs(((TypeExp) det).getTypeArgs());
        return;
      }
    }

    if (typeArgs.length == 1 && isRegularType(typeArgs[0]))
      show(typeArgs[0], Operators.EXPRESSION_PRIORITY);
    else
      typeArgs(typeArgs, typeArgs.length);
  }

  private void typeArgs(IType[] typeArgs, int count)
  {
    disp.append("(");
    String sep = "";
    for (int ix = 0; ix < count; ix++) {
      disp.append(sep);
      sep = ", ";
      show(typeArgs[ix], Operators.ARG_PRIORITY);
    }
    disp.append(")");
  }

  private boolean isRegularType(IType type)
  {
    type = TypeUtils.deRef(type);
    return !(TypeUtils.isFunctionType(type) || TypeUtils.isTupleType(type));
  }

  @Override
  public void visitTypeVar(TypeVar var, Integer priority)
  {
    IType type = var.deRef();

    if (type instanceof TypeVar) {
      var = (TypeVar) type;

      if (StarCompiler.TEST_TVAR_DISPLAY)
        disp.appendWord(typeVarName(var));
      else
        disp.appendId(var.getVarName());
      if (StarCompiler.TVAR_DISPLAY_READONLY && var.isReadOnly())
        disp.append("%");
    } else
      show(type, Operators.ARG_PRIORITY);
  }

  private String typeVarName(TypeVar var)
  {
    String vrName = var.getOriginalName();
    if (vrName == null)
      vrName = "t";
    String origName = vrName;
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    int counter = 0;

    do {
      TypeVar used = usedNames.get(vrName);

      if (used == var) {
        if (vrName.startsWith("%")) {
          disp.appendWord(vrName);
        } else
          disp.appendId(vrName);
        return disp.toString();
      } else if (used == null) {
        usedNames.put(vrName, var);
        if (vrName.startsWith("%")) {
          disp.appendWord(vrName);
        } else
          disp.appendId(vrName);
        return disp.toString();
      } else
        vrName = origName + counter++;
    } while (true);
  }

  public void displayContract(TypeExp contract)
  {
    IType[] contractArgs = contract.getTypeArgs();

    disp.appendId(contract.typeLabel());
    disp.appendWord(AbstractType.OVER);
    typeArgs(contractArgs);
  }

  @Override
  public void visitExistentialType(ExistentialType t, Integer priority)
  {
    LayeredMap<IType, String> forkedQuants = quants.fork();
    DisplayType fork = new DisplayType(disp, usedNames.fork(), forkedQuants);

    disp.appendWord(StandardNames.EXISTS);
    String sep = "";

    Collection<IType> bndVars = new HashSet<>();

    IType tp = t;
    while (tp instanceof ExistentialType) {
      ExistentialType e = (ExistentialType) tp;
      disp.appendWord(sep);
      sep = StandardNames.COMMA;
      forkedQuants.put(TypeUtils.deRef(e.getBoundVar()), typeVarName(e.getBoundVar()));
      bndVars.add(e.getBoundVar());
      fork.show(e.getBoundVar(), Operators.ARG_PRIORITY);
      tp = e.getBoundType();
    }

    disp.appendWord(StandardNames.S_T);
    disp.append(" ");
    fork.show(tp, Operators.S_T_PRIORITY);
    showConstraints(fork, bndVars);
  }

  @Override
  public void visitUniversalType(UniversalType t, Integer priority)
  {
    LayeredMap<IType, String> forkedQuants = quants.fork();
    DisplayType fork = new DisplayType(disp, usedNames.fork(), forkedQuants);

    disp.appendWord(StandardNames.FOR_ALL);
    String sep = "";

    Collection<IType> bndVars = new HashSet<>();

    IType tp = t;
    while (tp instanceof UniversalType) {
      UniversalType e = (UniversalType) tp;
      disp.appendWord(sep);
      sep = StandardNames.COMMA;
      forkedQuants.put(TypeUtils.deRef(e.getBoundVar()), fork.typeVarName(e.getBoundVar()));
      bndVars.add(e.getBoundVar());
      fork.show(e.getBoundVar(), Operators.ARG_PRIORITY);
      tp = e.getBoundType();
    }

    disp.appendWord(StandardNames.S_T);
    disp.append(" ");
    fork.show(tp, Operators.S_T_PRIORITY);
    showConstraints(fork, bndVars);
  }
}
