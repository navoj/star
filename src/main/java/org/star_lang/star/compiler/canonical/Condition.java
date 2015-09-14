package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public abstract class Condition implements ICondition
{
  private final Location loc;

  protected Condition(Location loc)
  {
    this.loc = loc;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
