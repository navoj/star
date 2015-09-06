package org.star_lang.star.data.type;

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
public abstract class QuantifiedType extends AbstractType
{
  protected final TypeVar boundVar;
  protected final IType boundType;

  public QuantifiedType(String label, TypeVar boundVar, IType boundType)
  {
    super(boundType.typeLabel(), boundType.kind());
    this.boundVar = boundVar;
    this.boundType = boundType;
  }

  public TypeVar getBoundVar()
  {
    return boundVar;
  }

  public IType getBoundType()
  {
    return boundType;
  }
}