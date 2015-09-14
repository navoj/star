package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class AssertAction extends Action
{
  private final IContentExpression assertion;

  public AssertAction(Location loc, IContentExpression assertion)
  {
    super(loc, StandardTypes.unitType);
    this.assertion = assertion;
  }

  public IContentExpression getAssertion()
  {
    return assertion;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.ASSERT);
    assertion.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitAssertAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitAssertAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformAssertAction(this, context);
  }

}
