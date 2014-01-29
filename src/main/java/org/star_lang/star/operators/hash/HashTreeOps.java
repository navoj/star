package org.star_lang.star.operators.hash;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.HashIterate;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.HashIxIterate;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.HashLeftFold;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.HashLeftFold1;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.HashRightFold;
import org.star_lang.star.operators.hash.runtime.HashTreeIterable.HashRightFold1;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.DeleteFromHash;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashContains;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashCreate;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashDelete;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashEmpty;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashEqual;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashGet;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashMerge;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashSize;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.HashUpdate;
import org.star_lang.star.operators.hash.runtime.HashTreeOps.UpdateIntoHash;

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
  }
}
