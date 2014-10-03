package org.star_lang.star.data.value;

import java.math.BigDecimal;

import org.star_lang.star.data.IScalar;

/**
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
