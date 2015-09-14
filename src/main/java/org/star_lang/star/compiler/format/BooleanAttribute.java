package org.star_lang.star.compiler.format;

import org.star_lang.star.compiler.ast.BaseAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

@SuppressWarnings("serial")
public class BooleanAttribute extends BaseAttribute<Boolean>
{
  private final boolean att;

  public BooleanAttribute(boolean flag, int specificity)
  {
    this(flag, specificity, true);
  }

  public BooleanAttribute(boolean att, int specificity, boolean inheritable)
  {
    super(inheritable, specificity);
    this.att = att;
  }

  @Override
  public Boolean attribute(Boolean original)
  {
    return att;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(att ? "true" : "false");
  }

}
