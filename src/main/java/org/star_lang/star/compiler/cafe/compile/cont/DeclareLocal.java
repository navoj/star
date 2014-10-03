package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.Expressions;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.compiler.cafe.compile.VarInfo;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.Location;

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
public class DeclareLocal implements IContinuation
{
  private final ISpec desc;
  private final CafeDictionary dict;
  private final LabelNode endLabel;
  private final VarInfo v;

  public DeclareLocal(Location loc, String name, ISpec desc, AccessMode access, CafeDictionary dict, LabelNode endLabel)
  {
    this.desc = desc;
    this.dict = dict;
    this.endLabel = endLabel;
    this.v = dict.declareLocal(loc, name, false, desc.getType(), desc.getJavaType(), desc.getJavaSig(), desc
        .getJavaInvokeSig(), desc.getJavaInvokeName(), access);
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();
    
    InsnList ins = mtd.instructions;

    if (!v.isInited()) {
      LabelNode start = new LabelNode();
      ins.add(start);
      mtd.localVariables.add(new LocalVariableNode(v.getJavaSafeName(), v.getJavaSig(), null, start, endLabel, v
          .getOffset()));
      v.setInited(true);
    }

    Expressions.checkType(src, desc, mtd, dict, hwm, loc, errors, bldCat);
    int offset = v.getOffset();

    switch (v.getKind()) {
    case rawBool:
    case rawChar:
      ins.add(new VarInsnNode(Opcodes.ISTORE, offset));
      break;
    case rawInt:
      ins.add(new VarInsnNode(Opcodes.ISTORE, offset));
      break;
    case rawLong:
      ins.add(new VarInsnNode(Opcodes.LSTORE, offset));
      break;
    case rawFloat:
      ins.add(new VarInsnNode(Opcodes.DSTORE, offset));
      break;
    case rawBinary:
    case rawString:
    case rawDecimal:
    case general:
      ins.add(new VarInsnNode(Opcodes.ASTORE, offset));
      break;
    default:
      assert false : "invalid kind of variable";
    }

    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return false;
  }
}
