indexErr is package {

/* from Prelude */
type Either of (%a, %b) is Left(%a) or Right(%b);
iterateState has type (%a, (%a) => Either of (%b, %a)) => %b;
iterateState(theST, step) is valof {
    var st := theST;
    while true do {
      switch step(st) in {
        case Left(final) do valis final;
        case Right(next) do st := next;
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