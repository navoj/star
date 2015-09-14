package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

public class CallCont implements IContinuation
{
  private final InsnList ins;
  private final IContinuation cont;

  public CallCont(InsnList ins, IContinuation cont)
  {
    this.ins = ins;
    this.cont = cont;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    IType resType = src.getType();
    switch (Types.varType(resType)) {
    default:
      // if (!TypeUtils.isProcedureReturnType(resType))
      ins.add(new InsnNode(Opcodes.POP));
      break;
    case rawLong:
    case rawFloat:
      ins.add(new InsnNode(Opcodes.POP2));
      break;
    }
    return cont.cont(src, cxt, loc, ccxt);
  }

  @Override
  public boolean isJump()
  {
    return cont.isJump();
  }
}