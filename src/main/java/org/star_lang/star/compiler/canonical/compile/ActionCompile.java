package org.star_lang.star.compiler.canonical.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.Sense;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.canonical.AssertAction;
import org.star_lang.star.compiler.canonical.Assignment;
import org.star_lang.star.compiler.canonical.CaseAction;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.ExceptionHandler;
import org.star_lang.star.compiler.canonical.ForLoopAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.Ignore;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.ProcedureCallAction;
import org.star_lang.star.compiler.canonical.RaiseAction;
import org.star_lang.star.compiler.canonical.Sequence;
import org.star_lang.star.compiler.canonical.SyncAction;
import org.star_lang.star.compiler.canonical.TransformAction;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.WhileAction;
import org.star_lang.star.compiler.canonical.Yield;
import org.star_lang.star.compiler.canonical.compile.cont.PttrnCont;

import com.starview.platform.data.type.Location;
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

public class ActionCompile implements
    TransformAction<FrameState, FrameState, FrameState, FrameState, FrameState, CompileContext>
{

  @Override
  public FrameState transformAssertAction(AssertAction act, CompileContext context)
  {
    InsnList ins = context.getIns();
    Location loc = act.getLoc();

    LabelNode nxLabel = new LabelNode();

    doLineNumber(loc, context);

    CafeDictionary asDict = context.getDict();

    LabelNode elLabel = new LabelNode();

    CompileContext forked = context.fork(asDict);
    CompileContext ccxt = ConditionContext.fork(forked, Sense.jmpOnOk, nxLabel, elLabel);

    FrameState cond = act.getAssertion().transform(new ExpressionCompile(), ccxt);

    String exceptionType = Type.getInternalName(AssertionError.class);

    ins.add(elLabel);
    ins.add(new TypeInsnNode(Opcodes.NEW, exceptionType));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode("assert failed at " + loc.toString()));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionType, Types.INIT, "(" + Types.OBJECT_SIG + ")V"));
    ins.add(new InsnNode(Opcodes.ATHROW));

    Utils.jumpTarget(ins, nxLabel);
    context.cont(cond, loc);
    context.getDict().migrateFreeVars(asDict);

    return context.cont(context.getFrame(), loc);
  }

  @Override
  public FrameState transformAssignment(Assignment act, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformCaseAction(CaseAction exp, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformConditionalAction(ConditionalAction act, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformExceptionHandler(ExceptionHandler handler, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformForLoop(ForLoopAction loop, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformIgnored(Ignore ignore, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformLetAction(LetAction let, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformWhileLoop(WhileAction act, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformNullAction(NullAction act, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformProcedureCallAction(ProcedureCallAction call, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformRaiseAction(RaiseAction raise, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformSequence(Sequence sequence, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformSyncAction(SyncAction sync, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformValisAction(ValisAction act, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformVarDeclaration(VarDeclaration decl, CompileContext context)
  {
    IContentPattern ptn = decl.getPattern();
    IContentExpression val = decl.getValue();

    CompileContext ptnContext = context.fork(new PttrnCont(ptn, context));

    return val.transform(new ExpressionCompile(), ptnContext);
  }

  @Override
  public FrameState transformYield(Yield act, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public static void doLineNumber(Location loc, CompileContext cxt)
  {
    if (!loc.equals(Location.nullLoc)) {
      LabelNode lnLbl = new LabelNode();
      MethodNode mtd = cxt.getMtd();
      mtd.instructions.add(lnLbl);
      mtd.instructions.add(new LineNumberNode(loc.getLineCnt(), lnLbl));
    }
  }
}
