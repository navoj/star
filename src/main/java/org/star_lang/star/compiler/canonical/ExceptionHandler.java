package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ExceptionHandler extends Action
{
  private final IContentAction body;
  private final IContentAction handler;

  public ExceptionHandler(Location loc, IContentAction body, IContentAction handler)
  {
    super(loc, body.getType());
    this.body = body;
    this.handler = handler;
  }

  public IContentAction getBody()
  {
    return body;
  }

  public IContentAction getHandler()
  {
    return handler;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.TRY);
    body.prettyPrint(disp);
    disp.appendWord(StandardNames.ON_ABORT);
    handler.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitExceptionHandler(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitExceptionHandler(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformExceptionHandler(this, context);
  }
}
