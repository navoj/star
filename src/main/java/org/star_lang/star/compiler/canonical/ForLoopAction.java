package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ForLoopAction extends Action
{
  private final ICondition control;
  private final IContentAction body;
  private final List<Variable> free;
  private final List<Variable> defined;

  public ForLoopAction(Location loc, ICondition control, List<Variable> free, List<Variable> defined, IContentAction body)
  {
    super(loc, body.getType());
    this.control = control;
    this.body = body;
    this.free = free;
    this.defined = defined;
  }

  public ICondition getControl()
  {
    return control;
  }

  public IContentAction getBody()
  {
    return body;
  }

  public List<Variable> getFree()
  {
    return free;
  }

  public List<Variable> getDefined()
  {
    return defined;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.FOR);
    control.prettyPrint(disp);
    disp.appendWord(StandardNames.DO);
    disp.append("\n");
    body.prettyPrint(disp);
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitForLoopAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitForLoopAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformForLoop(this, context);
  }
}
