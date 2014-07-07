package org.star_lang.star.compiler.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.FieldConstraint;
import org.star_lang.star.data.type.FieldTypeConstraint;
import org.star_lang.star.data.type.HasKind;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.InstanceOf;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.QuantifiedType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/**
 * An implementation of the {@link ITypeVisitor} that renames type variables in a type expression.
 * It can be used both to 'standardize apart' type expressions (replacing quantified types with type
 * expressions with new type variables) and to 'generalize' type expression (the converse: to
 * construct an explicitly quantified type expression).
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

public class Refresher implements ITypeVisitor<Void>
{
  private final Map<String, TypeVar> bound;
  private final Stack<String> exclusions = new Stack<String>();
  protected final Stack<IType> stack = new Stack<IType>();

  private Refresher(Map<String, TypeVar> bound)
  {
    this.bound = bound;
  }

  private IType top()
  {
    return stack.pop();
  }

  private int stackSize()
  {
    return stack.size();
  }

  /**
   * Refresh a type while updating a supplied map with the variables that were found and their
   * replacements.
   * 
   * @param type
   *          the type to refresh
   * @param bound
   *          a map, which should be empty on entry, which will be updated with the names of the
   *          variables that were refreshed.
   * @return the refreshed type expression.
   */
  public static IType refresh(IType type, Map<String, TypeVar> bound)
  {
    return refresh(type, AccessMode.readWrite, bound);
  }

  /**
   * Refresh a type while updating a supplied map with the variables that were found and their
   * replacements.
   * 
   * @param type
   *          the type to refresh
   * @param bound
   *          a map, which should be empty on entry, which will be updated with the names of the
   *          variables that were refreshed.
   * @return the refreshed type expression.
   */
  public static IType refresh(IType type, AccessMode mode, Map<String, TypeVar> bound)
  {
    type = TypeUtils.deRef(type);

    if (type instanceof UniversalType) {
      while (type instanceof UniversalType) {
        UniversalType univ = (UniversalType) type;

        TypeVar tv = univ.getBoundVar();
        String varName = tv.getVarName();
        TypeVar newVar = new TypeVar(newVarName(varName), tv.getOriginalName(), mode);
        bound.put(varName, newVar);

        type = univ.getBoundType();
      }

      Refresher refresher = new Refresher(bound);
      type.accept(refresher, null);
      assert refresher.stackSize() == 1;
      return refresher.top();
    } else if (type instanceof ExistentialType) {

      while (type instanceof ExistentialType) {
        QuantifiedType univ = (QuantifiedType) type;

        TypeVar tv = univ.getBoundVar();
        String varName = tv.getVarName();
        TypeVar newVar = new TypeVar(newVarName(varName), tv.getOriginalName(), mode);
        bound.put(varName, newVar);

        type = univ.getBoundType();
      }

      Refresher refresher = new Refresher(bound);
      type.accept(refresher, null);
      assert refresher.stackSize() == 1;
      return refresher.top();
    } else if (!bound.isEmpty()) {
      Refresher refresher = new Refresher(bound);
      type.accept(refresher, null);
      return refresher.top();
    } else
      return type;
  }

  private static String newVarName(String vName)
  {
    if (vName.contains("_"))
      vName = vName.substring(0, vName.indexOf("_"));
    return vName + "_" + GenSym.counter("__");
  }

  public static IType generalize(IType type, Map<String, TypeVar> vars)
  {
    Refresher refresher = new Refresher(vars);
    type.accept(refresher, null);
    assert refresher.stackSize() == 1;

    IType genType = refresher.top();
    return UniversalType.univ(vars.values(), genType);
  }

  /**
   * The rewrite function can be used to apply a binding to a type expression. The binding takes the
   * form of a map of variable names to types. {@code rewrite} creates a copy of the type expression
   * with all type variables within the type expression that appear in the {code binding} map
   * replaced.
   * 
   * This function honors quantified types: if the type contains (even at the top-level) a
   * quantified type that happens to have the same variable name as one in the {@code binding} map,
   * then that variable will not be rewritten.
   * 
   * @param type
   *          to rewrite
   * @param binding
   *          a map of variable names to type expressions
   * @return the rewritten type expression.
   */
  public static IType rewrite(IType type, final Map<String, TypeVar> binding)
  {
    Refresher rewriter = new Refresher(binding) {
      Stack<String> exclusions = new Stack<String>();

      @Override
      public void visitTypeVar(TypeVar var, Void cxt)
      {
        String typeName = var.getVarName();

        IType refreshed = binding.get(typeName);

        if (!exclusions.contains(typeName) && refreshed != null)
          stack.push(refreshed);
        else
          stack.push(var);
      }
    };

    type.accept(rewriter, null);
    assert rewriter.stackSize() == 1;
    return rewriter.top();
  }

  @Override
  public void visitSimpleType(Type t, Void cxt)
  {
    stack.push(t);
  }

  @Override
  public void visitTypeExp(TypeExp t, Void cxt)
  {
    t.getTypeCon().accept(this, cxt);
    IType tyCon = stack.pop();

    IType typeArgs[] = t.getTypeArgs();

    for (int ix = 0; ix < typeArgs.length; ix++)
      typeArgs[ix].accept(this, cxt);
    assert stack.size() >= typeArgs.length;
    IType newArgs[] = new IType[typeArgs.length];
    for (int ix = typeArgs.length - 1; ix >= 0; ix--)
      newArgs[ix] = stack.pop();

    stack.push(TypeUtils.typeExp(tyCon, newArgs));
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Void cxt)
  {
    SortedMap<String, IType> nF = new TreeMap<String, IType>();
    SortedMap<String, IType> nT = new TreeMap<String, IType>();

    for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
      entry.getValue().accept(this, cxt);
      nF.put(entry.getKey(), stack.pop());
    }
    for (Entry<String, IType> entry : t.getAllTypes().entrySet()) {
      entry.getValue().accept(this, cxt);
      nT.put(entry.getKey(), stack.pop());
    }

    stack.push(new TypeInterfaceType(nT, nF));
  }

  @Override
  public void visitTypeVar(TypeVar var, Void cxt)
  {
    IType type = var.deRef();

    if (type instanceof TypeVar) {
      var = (TypeVar) type;
      String typeName = var.getVarName();

      IType refreshed = bound.get(typeName);

      if (!exclusions.contains(typeName) && refreshed != null) {
        if (var.hasConstraints() && refreshed instanceof TypeVar && !((TypeVar) refreshed).hasConstraints()
            && !stack.contains(refreshed)) {
          stack.push(refreshed);
          TypeVar refreshedVar = (TypeVar) refreshed;

          for (ITypeConstraint con : var) {
            if (con instanceof FieldConstraint) {
              FieldConstraint fieldCon = (FieldConstraint) con;
              fieldCon.getType().accept(this, cxt);
              IType fieldType = stack.pop();
              TypeUtils.setFieldConstraint(refreshedVar, Location.nullLoc, fieldCon.getField(), fieldType);
            } else if (con instanceof FieldTypeConstraint) {
              FieldTypeConstraint fieldCon = (FieldTypeConstraint) con;
              fieldCon.getType().accept(this, cxt);
              IType fieldType = stack.pop();
              TypeUtils.setTypeConstraint(refreshedVar, fieldCon.getName(), fieldType);
            } else if (con instanceof ContractConstraint) {
              ContractConstraint contract = (ContractConstraint) con;
              contract.getContract().accept(this, cxt);
              ContractConstraint nContract = new ContractConstraint((TypeExp) stack.pop());
              refreshedVar.setConstraint(nContract);
            } else if (con instanceof HasKind)
              refreshedVar.setConstraint(new HasKind(refreshedVar, ((HasKind) con).getKind()));
            else if (con instanceof InstanceOf) {
              ((InstanceOf) con).getType().accept(this, cxt);
              IType instType = stack.pop();
              refreshedVar.setConstraint(new InstanceOf(refreshedVar, instType));
            } else
              assert false : "invalid type constraint";
          }
        } else
          stack.push(refreshed);
      } else
        stack.push(var);
    } else
      type.accept(this, cxt);
  }

  @Override
  public void visitExistentialType(ExistentialType t, Void cxt)
  {
    int mark = exclusions.size();
    TypeVar bndVar = t.getBoundVar();
    exclusions.push(bndVar.getVarName());

    t.getBoundType().accept(this, cxt);
    IType refreshed = stack.pop();
    exclusions.setSize(mark);
    stack.push(new ExistentialType(bndVar, refreshed));
  }

  @Override
  public void visitUniversalType(UniversalType t, Void cxt)
  {
    int mark = exclusions.size();
    TypeVar bndVar = t.getBoundVar();
    exclusions.push(bndVar.getVarName());

    t.getBoundType().accept(this, cxt);
    IType refreshed = stack.pop();
    exclusions.setSize(mark);
    stack.push(new UniversalType(bndVar, refreshed));
  }
}
