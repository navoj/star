package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Patterns;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.compile.VarInfo;
import org.star_lang.star.compiler.cafe.compile.VarPattern;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.assignment.runtime.RefCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.BoolCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.Cell;
import org.star_lang.star.operators.assignment.runtime.RefCell.IntegerCell;

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

public class AssignmentCont implements IContinuation
{
  private final IAbstract ptn;
  private final CafeDictionary dict, outer;
  private final AccessMode access;
  private final LabelNode endLabel;
  private final IContinuation succ, fail;

  public AssignmentCont(IAbstract ptn, CafeDictionary dict, CafeDictionary outer, AccessMode access,
                        LabelNode endLabel, IContinuation succ, IContinuation fail)
  {
    this.ptn = ptn;
    this.dict = dict;
    this.outer = outer;
    this.access = access;
    this.endLabel = endLabel;
    this.succ = succ;
    this.fail = fail;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    Patterns.compilePtn(ptn, access, src, dict, outer, endLabel, new AssignName(), succ, fail, ccxt);
    return src;
  }

  public static class AssignName implements VarPattern
  {
    @Override
    public void varPttrn(ISpec src, Location loc, String name, CafeDictionary dict, IContinuation succ,
                         IContinuation fail, CodeContext ccxt)
    {
      VarInfo var = dict.find(name);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      ErrorReport errors = ccxt.getErrors();
      InsnList ins = mtd.instructions;
      LabelNode okLabel = new LabelNode();
      int mark = hwm.getDepth();

      if (var != null) // Already declared?
      {
        if (!TypeUtils.isReferenceType(var.getType()))
          errors.reportError(StringUtils.msg("expecting reference type, not ", var.getType()), loc);
        IType refType = TypeUtils.referencedType(var.getType());

        if (TypeUtils.isRawBoolType(refType)) {
          var.loadValue(mtd, hwm, dict);
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Utils.javaInternalClassName(BoolCell.class)));
          ins.add(new InsnNode(Opcodes.SWAP));
          ins.add(new FieldInsnNode(Opcodes.PUTFIELD, Utils.javaInternalClassName(BoolCell.class), RefCell.VALUEFIELD,
              Types.JAVA_BOOL_SIG));
        } else if (TypeUtils.isRawIntType(refType)) {
          var.loadValue(mtd, hwm, dict);
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Utils.javaInternalClassName(IntegerCell.class)));
          ins.add(new InsnNode(Opcodes.SWAP));
          ins.add(new FieldInsnNode(Opcodes.PUTFIELD, Utils.javaInternalClassName(IntegerCell.class),
              RefCell.VALUEFIELD, Types.JAVA_INT_SIG));
        } else if (TypeUtils.isRawLongType(refType)) {
          errors.reportError(StringUtils.msg("raw long not handled "), loc);
        } else if (TypeUtils.isRawFloatType(refType))
          errors.reportError(StringUtils.msg("raw float not handled "), loc);
        else {
          var.loadValue(mtd, hwm, dict);
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Utils.javaInternalClassName(Cell.class)));
          ins.add(new InsnNode(Opcodes.SWAP));
          ins.add(new FieldInsnNode(Opcodes.PUTFIELD, Utils.javaInternalClassName(Cell.class), RefCell.VALUEFIELD,
              Types.IVALUE_SIG));
        }
      } else if (Utils.isAnonymous(name))
        ins.add(new InsnNode(Opcodes.POP));
      else
        errors.reportError(name + " not declared", loc);

      ins.add(okLabel);
      succ.cont(SrcSpec.prcSrc, dict, loc, ccxt);
      hwm.reset(mark);
    }
  }

  @Override
  public boolean isJump()
  {
    return succ.isJump() && fail.isJump();
  }

}
