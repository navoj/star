package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.TypeUtils;

/*
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
@SuppressWarnings("serial")
public class Type extends AbstractType
{
  public Type(String name, Kind kind)
  {
    super(name, kind);
  }

  public Type(String name, int arity)
  {
    this(name, Kind.kind(arity));
  }

  public Type(String name)
  {
    this(name, Kind.type);
  }

  public int getArity()
  {
    return kind().arity();
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    visitor.visitSimpleType(this, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformSimpleType(this, cxt);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof IType) {
      IType itype = TypeUtils.deRef((IType) obj);
      if (itype instanceof Type) {
        Type type = (Type) itype;

        return type.typeLabel().equals(typeLabel()) && type.kind().equals(kind());
      }
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return typeLabel().hashCode();
  }
}
