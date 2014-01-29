package org.star_lang.star.compiler.canonical;

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
public interface TransformAction<A, E, P, C, D, T>
{
  A transformAssertAction(AssertAction act, T context);

  A transformAssignment(Assignment act, T context);

  A transformCaseAction(CaseAction exp, T context);

  A transformConditionalAction(ConditionalAction act, T context);

  A transformExceptionHandler(ExceptionHandler handler, T context);

  A transformForLoop(ForLoopAction loop, T context);

  A transformIgnored(Ignore ignore, T context);

  A transformLetAction(LetAction let, T context);

  A transformWhileLoop(WhileAction act, T context);

  A transformNullAction(NullAction act, T context);

  A transformProcedureCallAction(ProcedureCallAction call, T context);

  A transformRaiseAction(RaiseAction raise, T context);

  A transformSequence(Sequence sequence, T context);

  A transformSyncAction(SyncAction sync, T context);

  A transformValisAction(ValisAction act, T context);

  A transformVarDeclaration(VarDeclaration var, T context);

  A transformYield(Yield act, T context);
}
