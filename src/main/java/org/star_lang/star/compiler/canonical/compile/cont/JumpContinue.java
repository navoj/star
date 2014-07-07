package org.star_lang.star.compiler.canonical.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.canonical.compile.CompileContext;
import org.star_lang.star.compiler.canonical.compile.Continue;
import org.star_lang.star.compiler.canonical.compile.FrameState;
import org.star_lang.star.data.type.Location;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class JumpContinue implements Continue
{
  private final LabelNode lbl;
  private int triggerCount = 0;
  @SuppressWarnings("unused")
  private final FrameState orig;
  private final CompileContext cxt;

  public JumpContinue(LabelNode lbl, FrameState frame, CompileContext cxt)
  {
    this.lbl = lbl;
    this.orig = frame;
    this.cxt = cxt;
  }

  @Override
  public boolean isJump()
  {
    return true;
  }

  @Override
  public FrameState cont(FrameState src, Location loc)
  {
    MethodNode mtd = cxt.getMtd();
    InsnList ins = mtd.instructions;

    if (!isUnconditionalJump(ins.getLast().getOpcode())) {
      ins.add(new JumpInsnNode(Opcodes.GOTO, lbl));
      triggerCount++;
    }
    return src;
  }

  public boolean isTriggered()
  {
    return triggerCount > 0;
  }

  public LabelNode getJmp()
  {
    return lbl;
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

  public void jumpTarget(InsnList ins, FrameState frame, CompileContext cxt)
  {
    if (triggerCount > 0) {
      AbstractInsnNode in = ins.getLast();
      while (in != null) {
        if (in instanceof JumpInsnNode) {
          JumpInsnNode jump = (JumpInsnNode) in;
          if (jump.label == lbl) {
            in = in.getPrevious();
            ins.remove(jump); // remove redundant jump
            triggerCount--;
            continue;
          }

          break;
        } else if (in instanceof LabelNode)
          in = in.getPrevious();
        else
          break;
      }
      // if (triggerCount > 0)
      // computeFrameIns();
      ins.add(lbl);
    }
  }

}
