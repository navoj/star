package org.star_lang.star.compiler.codegen;

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

import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.canonical.*;

/**
 * Created by fgm on 9/14/15.
 */
public class StatementCompile implements TransformStatement<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {

  @Override
  public ISpec transformContractDefn(ContractEntry con, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformContractImplementation(ImplementationEntry entry, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformImportEntry(ImportEntry entry, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformJavaEntry(JavaEntry entry, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformTypeAliasEntry(TypeAliasEntry entry, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformTypeEntry(TypeDefinition entry, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformVarEntry(VarEntry entry, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformOpenStatement(OpenStatement open, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformWitness(TypeWitness stmt, IContinuation cont) {
    return null;
  }
}
