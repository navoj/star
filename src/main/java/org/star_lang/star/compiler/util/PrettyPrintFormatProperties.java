package org.star_lang.star.compiler.util;

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
public class PrettyPrintFormatProperties
{
  private static final int MAXLINE = 80; // Maximum line width

  int maxLine = MAXLINE;
  private boolean relativeTabs = false;

  public int getMaxLine()
  {
    return maxLine;
  }

  public void setMaxLine(int maxLine)
  {
    this.maxLine = maxLine;
  }

  public void setRelativeTabs(boolean relativeTabs)
  {
    this.relativeTabs = relativeTabs;
  }

  public boolean isRelativeTabs()
  {
    return relativeTabs;
  }
}
