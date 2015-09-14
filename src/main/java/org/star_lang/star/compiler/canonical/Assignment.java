package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class Assignment extends Action
{
  private final IContentExpression lValue;
  private final IContentExpression value;

  public Assignment(Location loc, IContentExpression lValue, IContentExpression value)
  {
    super(loc, StandardTypes.unitType);
    this.lValue = lValue;
    this.value = value;
  }

  public IContentExpression getLValue()
  {
    return lValue;
  }

  public IContentExpression getValue()
  {
    return value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    lValue.prettyPrint(disp);
    disp.append(StandardNames.ASSIGN);
    value.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitAssignment(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitAssignment(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformAssignment(this, context);
  }
}
