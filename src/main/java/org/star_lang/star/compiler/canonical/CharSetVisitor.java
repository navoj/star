package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.canonical.CharSet.AnyChar;
import org.star_lang.star.compiler.canonical.CharSet.CharClass;
import org.star_lang.star.compiler.canonical.CharSet.CharUnion;

public interface CharSetVisitor
{
  void visitAnyChar(AnyChar any);

  void visitCharClass(CharClass set);

  void visitUnion(CharUnion union);
}