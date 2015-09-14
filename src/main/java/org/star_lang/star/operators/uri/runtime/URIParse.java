package org.star_lang.star.operators.uri.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

/**
 * Runtime parts of the functions that parse strings into uris
 * 
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
  */

public abstract class URIParse implements IFunction
{
  private final IType type;

  protected URIParse(IType type)
  {
    this.type = type;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  public static class String2URI extends URIParse
  {

    public String2URI()
    {
      super(type());
    }

    @CafeEnter
    public static IValue __string_uri(String txt) throws EvaluationException
    {
      try {
        return URIUtils.parseUri(txt);
      } catch (NumberFormatException e) {
        throw new EvaluationException(txt + " cannot be parsed as an integer");
      } catch (ResourceException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_uri(Factory.stringValue(args[0]));
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, ResourceURI.type);
    }
  }

  public static class URI2String implements IFunction
  {
    @CafeEnter
    public static String enter(ResourceURI uri) throws EvaluationException
    {
      return uri.toString();
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
}
