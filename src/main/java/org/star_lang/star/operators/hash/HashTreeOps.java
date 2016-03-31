package org.star_lang.star.operators.hash;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.*;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.*;

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
public class HashTreeOps
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(HashCreate.name, HashCreate.type(), HashCreate.class));
    cxt.declareBuiltin(new Builtin(HashContains.name, HashContains.type(), HashContains.class));
    cxt.declareBuiltin(new Builtin(HashGet.name, HashGet.funType(), HashGet.class));
    cxt.declareBuiltin(new Builtin(HashUpdate.name, HashUpdate.type(), HashUpdate.class));
    cxt.declareBuiltin(new Builtin(HashMerge.name, HashMerge.type(), HashMerge.class));
    cxt.declareBuiltin(new Builtin(DeleteFromHash.name, DeleteFromHash.type(), DeleteFromHash.class));
    cxt.declareBuiltin(new Builtin(UpdateIntoHash.name, UpdateIntoHash.type(), UpdateIntoHash.class));
    cxt.declareBuiltin(new Builtin(HashDelete.mapDelete, HashDelete.funType(), HashDelete.class));
    cxt.declareBuiltin(new Builtin(HashEmpty.mapEmpty, HashEmpty.type(), HashEmpty.class));
    cxt.declareBuiltin(new Builtin(HashSize.name, HashSize.type(), HashSize.class));

    cxt.declareBuiltin(new Builtin(HashIterate.name, HashIterate.type(), HashIterate.class));
    cxt.declareBuiltin(new Builtin(HashIxIterate.name, HashIxIterate.type(), HashIxIterate.class));
    cxt.declareBuiltin(new Builtin(HashEqual.name, HashEqual.type(), HashEqual.class));

    cxt.declareBuiltin(new Builtin(HashLeftFold.name, HashLeftFold.type(), HashLeftFold.class));
    cxt.declareBuiltin(new Builtin(HashLeftFold1.name, HashLeftFold1.type(), HashLeftFold1.class));
    cxt.declareBuiltin(new Builtin(HashRightFold.name, HashRightFold.type(), HashRightFold.class));
    cxt.declareBuiltin(new Builtin(HashRightFold1.name, HashRightFold1.type(), HashRightFold1.class));

    cxt.declareBuiltin(new Builtin(HashPick.name, HashPick.type(), HashPick.class));
    cxt.declareBuiltin(new Builtin(HashRemaining.name, HashRemaining.type(), HashRemaining.class));
  }
}
