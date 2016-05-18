package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.star_lang.star.compiler.cafe.compile.*;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.Location;

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
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    InsnList ins = mtd.instructions;

    if (!v.isInited()) {
      LabelNode start = new LabelNode();
      ins.add(start);
      mtd.localVariables.add(new LocalVariableNode(v.getJavaSafeName(), v.getJavaSig(), null, start, endLabel, v
          .getOffset()));
      v.setInited(true);
    }

    Expressions.checkType(src, desc, mtd, dict, hwm);
    int offset = v.getOffset();

    switch (v.getKind()) {
    case rawBool:
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
