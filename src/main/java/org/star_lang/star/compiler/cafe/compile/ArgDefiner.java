package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
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
public class ArgDefiner implements Definer
{
  protected final MethodNode mtd;
  protected final HWM hwm;
  protected final LabelNode endLabel;
  protected final CodeCatalog bldCat;
  protected final CodeContext ccxt;

  public ArgDefiner(MethodNode mtd, HWM hwm, LabelNode endLabel, CodeCatalog bldCat, CodeContext ccxt)
  {
    this.mtd = mtd;
    this.hwm = hwm;
    this.endLabel = endLabel;
    this.bldCat = bldCat;
    this.ccxt = ccxt;
  }

  @Override
  public VarInfo declareArg(Location loc, String name, int varOffset, IType varType, CafeDictionary dict,
      AccessMode access, boolean isInited, ErrorReport errors)
  {
    ISpec vrSpec = SrcSpec.generic(loc, varType, dict, ccxt.getRepository(), errors);

    VarInfo var = dict.declareLocal(name, vrSpec, isInited, access);
    if (isInited) {
      LabelNode startLabel = new LabelNode();
      mtd.localVariables.add(new LocalVariableNode(var.getJavaSafeName(), var.getJavaSig(), null, startLabel, endLabel,
          var.getOffset()));
      InsnList ins = mtd.instructions;
      ins.add(startLabel);
      if (!TypeUtils.isRawType(varType)) {
        hwm.probe(1);
        ins.add(new VarInsnNode(Opcodes.ALOAD, var.getOffset()));
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, var.getJavaType()));
        ins.add(new VarInsnNode(Opcodes.ASTORE, var.getOffset()));
      }
    }
    return var;
  }
}