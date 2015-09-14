package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class LetAction extends Action
{
  final private List<IStatement> environment;
  final private IContentAction bound;

  public LetAction(Location loc, List<IStatement> environment, IContentAction bound)
  {
    super(loc, bound.getType());
    this.bound = bound;
    this.environment = environment;
  }

  public List<IStatement> getEnvironment()
  {
    return environment;
  }

  public IContentAction getBoundAction()
  {
    return bound;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int outer = disp.markIndent();
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.LET);
    disp.append("{");

    for (IStatement entry : environment) {
      disp.append("\n");
      entry.prettyPrint(disp);
    }

    disp.popIndent(mark);
    disp.append("\n");
    disp.appendWord("}");
    disp.appendWord(StandardNames.IN);
    bound.prettyPrint(disp);
    disp.popIndent(outer);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitLetAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitLetAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformLetAction(this, context);
  }
}
