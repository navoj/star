package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class WhileAction extends Action
{
  private final ICondition control;
  private final IContentAction body;

  public WhileAction(Location loc, ICondition control, IContentAction body)
  {
    super(loc, body.getType());
    this.control = control;
    this.body = body;
  }

  public ICondition getControl()
  {
    return control;
  }

  public IContentAction getBody()
  {
    return body;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.WHILE);
    control.prettyPrint(disp);
    disp.appendWord(StandardNames.DO);
    disp.append("\n");
    body.prettyPrint(disp);
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitWhileAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitWhileAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformWhileLoop(this, context);
  }
}
