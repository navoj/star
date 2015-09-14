package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public abstract class BaseExpression implements IContentExpression
{
  private final Location loc;
  private final IType type;

  protected BaseExpression(Location loc, IType type)
  {
    this.loc = loc;
    this.type = TypeUtils.deRef(type);
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
