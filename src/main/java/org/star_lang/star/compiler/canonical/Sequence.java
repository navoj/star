package org.star_lang.star.compiler.canonical;

import java.util.Iterator;
import java.util.List;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Sequence extends Action implements Iterable<IContentAction>
{
  private final List<IContentAction> actions;

  public Sequence(Location loc, IType type, List<IContentAction> actions)
  {
    super(loc, type);
    this.actions = actions;
  }

  public List<IContentAction> getActions()
  {
    return actions;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("{\n");
    disp.prettyPrint(actions, ";\n");
    disp.append("}");
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitSequence(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitSequence(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformSequence(this, context);
  }

  @Override
  public Iterator<IContentAction> iterator()
  {
    return actions.iterator();
  }

}
