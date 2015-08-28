package org.star_lang.star.compiler.canonical;

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
public interface ActionVisitor
{
  void visitAssertAction(AssertAction act);

  void visitAssignment(Assignment act);

  void visitCaseAction(CaseAction exp);

  void visitConditionalAction(ConditionalAction act);

  void visitExceptionHandler(ExceptionHandler handler);

  void visitIgnored(Ignore ignore);

  void visitForLoopAction(ForLoopAction loop);

  void visitLetAction(LetAction let);

  void visitRaiseAction(RaiseAction exp);

  void visitWhileAction(WhileAction act);

  void visitNullAction(NullAction act);

  void visitProcedureCallAction(ProcedureCallAction call);

  void visitSequence(Sequence sequence);

  void visitSyncAction(SyncAction sync);

  void visitValisAction(ValisAction act);

  void visitVarDeclaration(VarDeclaration var);
}
