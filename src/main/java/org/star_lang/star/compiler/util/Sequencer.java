package org.star_lang.star.compiler.util;

/**
 * A Sequencer is similar to an Iterator, except that there is a known index and can move in either
 * direction
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
public interface Sequencer<T>
{
  int index();

  T peek();

  T next();

  T prev();

  boolean hasNext();

  boolean hasPrev();

  @SuppressWarnings("serial")
  public static class SequenceException extends Error
  {
    public SequenceException(String msg)
    {
      super(msg);
    }
  }
}
