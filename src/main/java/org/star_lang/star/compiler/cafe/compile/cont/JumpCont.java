package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.data.type.Location;

/*  * Copyright (c) 2015. Francis G. McCabe  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language governing  * permissions and limitations under the License.  */
public class JumpCont implements IContinuation
{
  private final LabelNode jmp;

  public JumpCont(LabelNode lbl)
  {
    this.jmp = lbl;
  }

  @Override
  public boolean isJump()
  {
    return true;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    InsnList ins = mtd.instructions;

    if (!isUnconditionalJump(ins.getLast().getOpcode()))
      ins.add(new JumpInsnNode(Opcodes.GOTO, jmp));
    return src;
  }

  public LabelNode getJmp()
  {
    return jmp;
  }

  public static boolean isUnconditionalJump(int opcode)
  {
    switch (opcode) {
    case Opcodes.GOTO:
    case Opcodes.RETURN:
    case Opcodes.RET:
    case Opcodes.ARETURN:
    case Opcodes.LRETURN:
    case Opcodes.FRETURN:
    case Opcodes.DRETURN:
      return true;
    default:
      return false;
    }
  }
}
