package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtVar implements FmtBuildOp
{
  private final int offset;
  private final String vName;

  public FmtVar(int offset, String vName)
  {
    this.offset = offset;
    this.vName = vName;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc)
  {
    return env[offset];
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord("?");
    disp.append(offset);
  }

  public String getName()
  {
    return vName;
  }
}
