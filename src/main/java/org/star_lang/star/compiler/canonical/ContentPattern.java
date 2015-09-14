package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public abstract class ContentPattern implements IContentPattern
{
  private final Location loc;
  private final IType type;

  protected ContentPattern(Location loc, IType type)
  {
    this.loc = loc;
    this.type = type;
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
