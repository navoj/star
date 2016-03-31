package org.star_lang.star.operators.sets;

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

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.sets.runtime.SetIterableOps;
import org.star_lang.star.operators.sets.runtime.SetOps;

/**
 * Created by fgm on 7/14/15.
 */
public class SetOpsDecl {
  public static void declare(Intrinsics cxt) {
    cxt.declareBuiltin(new Builtin(SetOps.SetEqual.name, SetOps.SetEqual.type(), SetOps.SetEqual.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetCreate.name, SetOps.SetCreate.type(), SetOps.SetCreate.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetContains.name, SetOps.SetContains.type(), SetOps.SetContains.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetIntersect.name, SetOps.SetIntersect.type(), SetOps.SetIntersect.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetUnion.name, SetOps.SetUnion.type(), SetOps.SetUnion.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetDifference.name, SetOps.SetDifference.type(), SetOps.SetDifference.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetFilter.name, SetOps.SetFilter.type(), SetOps.SetFilter.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetMap.name, SetOps.SetMap.type(), SetOps.SetMap.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetInsert.name, SetOps.SetInsert.type(), SetOps.SetInsert.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetDelete.name, SetOps.SetDelete.type(), SetOps.SetDelete.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetEmpty.name, SetOps.SetEmpty.type(), SetOps.SetEmpty.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetSize.name, SetOps.SetSize.type(), SetOps.SetSize.class));

    cxt.declareBuiltin(new Builtin(SetOps.SetPick.name, SetOps.SetPick.type(), SetOps.SetPick.class));
    cxt.declareBuiltin(new Builtin(SetOps.SetRemaining.name, SetOps.SetRemaining.type(), SetOps.SetRemaining.class));

    cxt.declareBuiltin(new Builtin(SetIterableOps.SetIterate.name, SetIterableOps.SetIterate.type(), SetIterableOps.SetIterate.class));
    cxt.declareBuiltin(new Builtin(SetIterableOps.SetUpdate.name, SetIterableOps.SetUpdate.type(), SetIterableOps.SetUpdate.class));

    cxt.declareBuiltin(new Builtin(SetIterableOps.SetLeftFold.name, SetIterableOps.SetLeftFold.type(), SetIterableOps.SetLeftFold.class));
    cxt.declareBuiltin(new Builtin(SetIterableOps.SetLeftFold1.name, SetIterableOps.SetLeftFold1.type(), SetIterableOps.SetLeftFold1.class));
    cxt.declareBuiltin(new Builtin(SetIterableOps.SetRightFold.name, SetIterableOps.SetRightFold.type(), SetIterableOps.SetRightFold.class));
    cxt.declareBuiltin(new Builtin(SetIterableOps.SetRightFold1.name, SetIterableOps.SetRightFold1.type(), SetIterableOps.SetRightFold1.class));
  }
}
