package org.star_lang.star.compiler.type;

import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

public abstract class AbstractTypeVisitor<C> implements ITypeVisitor<C>
{
  private final Stack<String> exclusions = new Stack<>();

  @Override
  public void visitSimpleType(Type t, C cxt)
  {
  }

  @Override
  public void visitTypeVar(TypeVar v, C cxt)
  {
    IType type = v.getBoundValue();
    if (type != null)
      type.accept(this, cxt);
  }

  @Override
  public void visitTypeExp(TypeExp t, C cxt)
  {
    t.getTypeCon().accept(this, cxt);
    for (IType arg : t.getTypeArgs())
      arg.accept(this, cxt);
  }

  @Override
  public void visitTupleType(TupleType t, C cxt)
  {
    for (IType arg : t.getElTypes())
      arg.accept(this, cxt);
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, C cxt)
  {
    for (Entry<String, IType> entry : t.getAllFields().entrySet())
      entry.getValue().accept(this, cxt);
  }

  @Override
  public void visitExistentialType(ExistentialType t, C cxt)
  {
    exclusions.push(t.getBoundVar().getVarName());
    t.getBoundType().accept(this, cxt);
    exclusions.pop();
  }

  @Override
  public void visitUniversalType(UniversalType t, C cxt)
  {
    exclusions.push(t.getBoundVar().getVarName());
    t.getBoundType().accept(this, cxt);
    exclusions.pop();
  }

  protected boolean isNotExcluded(String name)
  {
    return !exclusions.contains(name);
  }
}
