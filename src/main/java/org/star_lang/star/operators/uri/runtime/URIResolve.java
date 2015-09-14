package org.star_lang.star.operators.uri.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.resource.ResourceException;

public class URIResolve implements IFunction
{
  @CafeEnter
  public static ResourceURI enter(ResourceURI base, ResourceURI rel) throws EvaluationException
  {
    try {
      return base.resolve(rel);
    } catch (ResourceException e) {
      throw new EvaluationException(e.getMessage());
    }
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((ResourceURI) args[0], (ResourceURI) args[1]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(ResourceURI.type, ResourceURI.type, ResourceURI.type);
  }

}
