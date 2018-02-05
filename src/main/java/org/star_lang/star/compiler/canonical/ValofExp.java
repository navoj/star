package org.star_lang.star.compiler.canonical;

import java.util.Iterator;
import java.util.List;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.SingleIterator;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ValofExp extends BaseExpression implements Iterable<IContentAction>
{
  private final IContentAction action;

  public ValofExp(Location loc, IType type, IContentAction... action)
  {
    super(loc, type);
    if (action.length != 1)
      this.action = new Sequence(loc, type, FixedList.create(action));
    else
      this.action = action[0];
  }
  
  public ValofExp(Location loc, IType type, List<IContentAction> action)
  {
    super(loc, type);
    if (action.size() != 1)
      this.action = new Sequence(loc, type, action);
    else
      this.action = action.get(0);
  }

  public IContentAction getAction()
  {
    return action;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.VALOF);
    action.prettyPrint(disp);
  }

  @Override
  public Iterator<IContentAction> iterator()
  {
    if (action instanceof Sequence)
      return ((Sequence) action).iterator();
    else
      return new SingleIterator<>(action);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitValofExp(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformValofExp(this, context);
  }
}
