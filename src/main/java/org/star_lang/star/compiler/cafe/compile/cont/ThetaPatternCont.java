package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Patterns;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.Location;

public class ThetaPatternCont implements IContinuation
{
  private final IAbstract ptn;
  private final CafeDictionary dict, outer;
  private final AccessMode access;
  private final LabelNode endLabel;
  private final IContinuation succ, fail;

  public ThetaPatternCont(IAbstract ptn, CafeDictionary dict, CafeDictionary outer, AccessMode access, MethodNode mtd,
      LabelNode endLabel, ErrorReport errors, IContinuation succ, IContinuation fail)
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
    Patterns.compilePttrn(ptn, access, src, dict, outer, endLabel, succ, fail, ccxt);
    return src;
  }

  @Override
  public boolean isJump()
  {
    return succ.isJump() && fail.isJump();
  }

}
