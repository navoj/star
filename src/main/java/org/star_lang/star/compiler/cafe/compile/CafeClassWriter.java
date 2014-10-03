package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.ClassWriter;

/**
 * Version of {@link ClassWriter} appropriate for use with the Star compiler.
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
class CafeClassWriter extends ClassWriter
{
  // static {
  // ClassWriter.MAX_CODE_LENGTH = 16484;
  // }

  public CafeClassWriter()
  {
    super(ClassWriter.COMPUTE_FRAMES,
        new org.objectweb.asm.commons.splitlarge.SplitMethodWriterDelegate());
  }

  /**
   * This override keeps ASM from trying to classload the auto-generated function types.
   */
  @Override
  protected String getCommonSuperClass(final String type1, final String type2)
  {
    if (type1.equals("com/starview/platform/data/IValue") || type2.equals("com/starview/platform/data/IValue"))
      return "com/starview/platform/data/IValue";
    else
      return "java/lang/Object";
  }
}