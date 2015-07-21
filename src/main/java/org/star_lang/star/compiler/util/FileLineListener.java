package org.star_lang.star.compiler.util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

/**
 * Simple utility listener for character file processing.
 * This interface should be implemented to handle processing each character
 * line of an ascii file.
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
public interface FileLineListener
{
  /**
   * This method is call from the FileUtil method that takes a listener as
   * a parameter. This method should be implemented to process each line
   * of an ascii file in any way desired by the implementing class.
   */
  void onLine(String line);
}
