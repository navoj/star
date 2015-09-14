package org.star_lang.star.operators.string;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.string.runtime.Regexp;

public class SpecificRegExp extends Builtin
{
  private final String regexp;

  public SpecificRegExp(String localName, String regexp)
  {
    super(localName, TypeUtils.patternType(TypeUtils.tupleType(), StandardTypes.stringType), Regexp.class);
    this.regexp = regexp;
  }

  public String getRegexp()
  {
    return regexp;
  }

  @Override
  public Class<?> getImplClass()
  {
    return Regexp.class;
  }
}