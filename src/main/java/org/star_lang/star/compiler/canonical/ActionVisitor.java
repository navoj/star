package org.star_lang.star.compiler.canonical;

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

  void visitValisAction(ValisAction act);

  void visitVarDeclaration(VarDeclaration var);
}
