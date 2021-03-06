package org.star_lang.star.compiler.ast;


/*
 * Copyright (c) 2015. Francis G. McCabe
 *                            
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public interface IAbstractVisitor
{
  void visitApply(AApply app);
  
  void visitTuple(AsTuple tpl);

  void visitBooleanLiteral(BooleanLiteral lit);

  void visitFloatLiteral(FloatLiteral flt);

  void visitStringLiteral(StringLiteral str);

  void visitIntegerLiteral(IntegerLiteral lit);

  void visitLongLiteral(LongLiteral lit);

  void visitName(Name name);
}
