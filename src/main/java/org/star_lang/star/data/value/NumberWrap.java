package org.star_lang.star.data.value;

import java.math.BigDecimal;

import org.star_lang.star.data.IScalar;
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
public abstract class NumberWrap<T> implements IScalar<T>
{
  public abstract int intValue();

  public abstract long longValue();

  public abstract double floatValue();

  public abstract BigDecimal bigNumValue();

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof NumberWrap) {
      NumberWrap<?> other = (NumberWrap<?>) obj;
      return this.getValue().equals(other.getValue());
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return this.getValue().hashCode();
  }
}
