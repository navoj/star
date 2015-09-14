package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.ClassWriter;

/**
 * Version of {@link ClassWriter} appropriate for use with the Star compiler.
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