package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class NullAction extends Action
{
  public NullAction(Location loc, IType type)
  {
    super(loc, type);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.NOTHING);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitNullAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitNullAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformNullAction(this, context);
  }
}
