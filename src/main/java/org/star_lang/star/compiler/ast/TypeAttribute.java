package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;

@SuppressWarnings("serial")
public class TypeAttribute extends BaseAttribute<IType>
{
  private final IType type;

  public TypeAttribute(IType type)
  {
    super(false, 0);
    this.type = type;
  }

  public IType getType()
  {
    return type;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    DisplayType.display(disp, type);
  }

  @Override
  public IType attribute(IType original)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
