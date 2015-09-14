package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.data.type.Location;

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
public class ThrowContinuation implements IContinuation
{
  private final String msg;

  public ThrowContinuation(String msg)
  {
    this.msg = msg;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    InsnList ins = mtd.instructions;

    ins.add(new TypeInsnNode(Opcodes.NEW, Types.EVALUATION_EXCEPTION));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode(msg));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Types.EVALUATION_EXCEPTION, Types.INIT, "("
        + Types.JAVA_STRING_SIG + ")V"));
    ins.add(new InsnNode(Opcodes.ATHROW));
    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return true;
  }

}
