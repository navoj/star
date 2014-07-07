package org.star_lang.star.compiler.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.compiler.util.HistoricalMap;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/**
 * This looks for type variables in a type expression. It also defines the 'official' order of
 * occurrence of type variables in a type expression. This is important for the meaning of a
 * quantified type.
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
public class TypeVarFinder implements ITypeVisitor<Void>
{
  private final Map<String, TypeVar> foundTypes;
  private final Stack<String> exclusions;
  private final VarGenerator gen;

  public TypeVarFinder(Map<String, TypeVar> foundTypes, Stack<String> exclusions, VarGenerator gen)
  {
    this.foundTypes = foundTypes;
    this.exclusions = exclusions;
    this.gen = gen;
  }

  public static HistoricalMap<String, TypeVar> findTypeVars(IType type, VarGenerator gen)
  {
    HistoricalMap<String, TypeVar> found = new HistoricalMap<String, TypeVar>();

    TypeVarFinder finder = new TypeVarFinder(found, new Stack<String>(), gen);
    type.accept(finder, null);
    return found;
  }

  public static HistoricalMap<String, TypeVar> findTypeVars(IType type)
  {
    return findTypeVars(type, new VarGenerator() {
      @Override
      public boolean isThisTheOne(TypeVar var)
      {
        return true;
      }

      @Override
      public TypeVar generate(TypeVar var)
      {
        return var;
      }
    });
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Void cxt)
  {
    for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
      entry.getValue().accept(this, cxt);
    }
    for (Entry<String, IType> entry : t.getAllTypes().entrySet())
      entry.getValue().accept(this, cxt);
  }

  @Override
  public void visitSimpleType(Type t, Void cxt)
  {
  }

  @Override
  public void visitTypeExp(TypeExp t, Void cxt)
  {
    for (IType argType : t.getTypeArgs())
      argType.accept(this, cxt);
    t.getTypeCon().accept(this, cxt);
  }

  @Override
  public void visitTypeVar(TypeVar v, Void cxt)
  {
    String varName = v.getVarName();
    if (!exclusions.contains(varName)) {
      if (gen.isThisTheOne(v) && !foundTypes.containsKey(varName)) {
        foundTypes.put(varName, gen.generate(v));
        for (ITypeConstraint con : v)
          con.accept(this, cxt);
      }
    }
  }

  @Override
  public void visitExistentialType(ExistentialType t, Void cxt)
  {
    int mark = exclusions.size();
    exclusions.push(t.getBoundVar().getVarName());

    t.getBoundType().accept(this, cxt);
    exclusions.setSize(mark);
  }

  @Override
  public void visitUniversalType(UniversalType t, Void cxt)
  {
    int mark = exclusions.size();
    exclusions.push(t.getBoundVar().getVarName());

    t.getBoundType().accept(this, cxt);
    exclusions.setSize(mark);
  }

  public interface VarGenerator
  {
    TypeVar generate(TypeVar var);

    boolean isThisTheOne(TypeVar var);
  }
}
