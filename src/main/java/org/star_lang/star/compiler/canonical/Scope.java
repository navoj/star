package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.VarInfo;

public interface Scope
{
  /**
   * Return the variable description of a variable, if it is defined in this scope
   * 
   * @param name
   * @return null if not defined here.
   */
  VarInfo inScope(String name);
}
