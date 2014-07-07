package org.star_lang.star.operators.resource.runtime;

import java.io.IOException;
import java.io.OutputStream;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

public class ResourceOps
{
  public static class GetResource implements IFunction
  {
    public static final String name = "__getResource";

    @CafeEnter
    public static String enter(ResourceURI uri) throws EvaluationException
    {
      try {
        return Resources.getUriContent(uri);
      } catch (ResourceException e) {
        throw new EvaluationException("could not access " + uri, e);
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(enter((ResourceURI) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(ResourceURI.type, StandardTypes.rawStringType);
    }
  }

  public static class PutResource implements IFunction
  {
    public static final String name = "__putResource";

    @CafeEnter
    public static IValue enter(ResourceURI uri, String text) throws EvaluationException
    {
      try {
        Resources.putResource(uri, text);
        OutputStream out = Resources.getOutputStream(uri);
        FileUtil.writeContent(out, text);
      } catch (ResourceException e) {
        throw new EvaluationException("could not access " + uri, e);
      } catch (IOException e) {
        throw new EvaluationException("io problem when writing to " + uri, e);
      }
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((ResourceURI) args[0], Factory.stringValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.procedureType(ResourceURI.type, StandardTypes.rawStringType);
    }
  }
}
