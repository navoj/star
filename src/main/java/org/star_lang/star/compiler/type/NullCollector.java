package org.star_lang.star.compiler.type;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

public class NullCollector implements ITypeCollector
{

  @Override
  public void fieldAnnotation(Location loc, String name, IType type)
  {
  }

  @Override
  public void kindAnnotation(Location loc, String name, IType type)
  {
  }

  @Override
  public void completeInterface(Location loc)
  {
  }

}
