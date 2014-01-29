package org.star_lang.star.operators.uri.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.URIUtils;

/**
 * Runtime parts of the functions that parse strings into uris
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
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
