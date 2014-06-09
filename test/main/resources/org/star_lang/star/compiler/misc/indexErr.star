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
indexErr is package {

/* from Prelude */
type Either of (%a, %b) is Left(%a) or Right(%b);
iterateState has type (%a, (%a) => Either of (%b, %a)) => %b;
iterateState(theST, step) is valof {
    var st := theST;
    while true do {
      case step(st) in {
        Left(final) do valis final;
        Right(next) do st := next;
      };
    };
};

type HashTrieEntry of (%k, %v) is
     EmptyHashTrieEntry
  or HashTrieKeyValue(%k, %v)
  or HashCollision(cons of ((%k, %v)));

hashTrieEntryRemoveByPattern has type (HashTrieEntry of (%k, %v), ()<=((%k, %v))) => (HashTrieEntry of (%k, %v), integer) where equality over %k and equality over %v; -- ';
hashTrieEntryRemoveByPattern(entry matching HashCollision(entries), P) is
  iterateState((entries, 0, nil), next) using {
    next((nil, removed, nil)) is Left((EmptyHashTrieEntry, removed));
    next((nil, removed, cons((k, v), nil))) is -- only one k,v pair left
      Left((HashTrieKeyValue(k,v), removed));
    next((nil, removed, res matching cons(_, cons(_, _)))) is
      Left((HashCollision(res), removed));  /* no need to reverse res */
    next((cons(entry matching (k,v), rest), removed, res)) is
     ((k,v) matches P()
     ? Right((rest, removed+ 0, res))
     | Right((rest, removed, cons(entry, rest))));
  };
}