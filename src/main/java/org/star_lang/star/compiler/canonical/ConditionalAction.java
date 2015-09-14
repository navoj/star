package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class ConditionalAction extends Action
{
  private final ICondition cond;
  private final IContentAction thPart;
  private final IContentAction elPart;

  public ConditionalAction(Location loc, ICondition cond, IContentAction thPart, IContentAction elPart)
  {
    super(loc, computeResultType(thPart.getType(), elPart.getType()));
    this.cond = cond;
    this.thPart = thPart;
    this.elPart = elPart;
  }

  private static IType computeResultType(IType lhs, IType rhs)
  {
    lhs = TypeUtils.deRef(lhs);
    rhs = TypeUtils.deRef(rhs);
    if (lhs.equals(StandardTypes.unitType))
      return rhs;
    else
      return lhs;
  }

  public ICondition getCond()
  {
    return cond;
  }

  public IContentAction getThPart()
  {
    return thPart;
  }

  public IContentAction getElPart()
  {
    return elPart;
  }

  @Override
  public IType getType()
  {
    return super.getType();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.IF);
    cond.prettyPrint(disp);
    disp.appendWord(StandardNames.THEN);
    disp.append("\n  ");
    int mark = disp.markIndent();
    thPart.prettyPrint(disp);
    disp.popIndent(mark);
    if (!CompilerUtils.isTrivial(elPart)) {
      disp.append("\n");
      disp.appendWord(StandardNames.ELSE);
      disp.append("\n  ");
      mark = disp.markIndent();
      elPart.prettyPrint(disp);
      disp.popIndent(mark);
    }
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitConditionalAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitConditionalAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConditionalAction(this, context);
  }
}
