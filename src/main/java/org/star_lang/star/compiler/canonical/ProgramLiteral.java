package org.star_lang.star.compiler.canonical;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public abstract class ProgramLiteral extends BaseExpression
{
  protected final Variable freeVars[];
  protected final String name;

  protected ProgramLiteral(Location loc, IType type, String name, Variable[] freeVars)
  {
    super(loc, type);
    assert freeVars != null;
    this.freeVars = freeVars;
    this.name = name;
  }

  public Variable[] getFreeVars()
  {
    return freeVars;
  }

  public String getName()
  {
    return name;
  }
}
