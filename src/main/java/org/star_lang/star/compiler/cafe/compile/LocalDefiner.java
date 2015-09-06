package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.ErrorReport;
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

public class LocalDefiner implements Definer {
  protected final CodeContext ccxt;

  public LocalDefiner(CodeContext ccxt) {
    this.ccxt = ccxt;
  }

  @Override
  public VarInfo declareArg(Location loc, String name, int varOffset, IType varType, CafeDictionary dict,
                            AccessMode access, boolean isInited, ErrorReport errors) {
    MethodNode mtd = ccxt.getMtd();

    ISpec vrSpec = SrcSpec.generic(loc, varType, dict, ccxt.getRepository(), errors);

    VarInfo var = dict.declareLocal(name, vrSpec, isInited, access);

    if (isInited) {
      LabelNode startLabel = new LabelNode();
      mtd.localVariables.add(new LocalVariableNode(var.getJavaSafeName(), var.getJavaSig(), null, startLabel, ccxt.getEndLabel(),
          var.getOffset()));
      InsnList ins = mtd.instructions;
      ins.add(startLabel);
    }
    return var;
  }
}