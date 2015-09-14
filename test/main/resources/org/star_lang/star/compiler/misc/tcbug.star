tcbug is package {
-- copied verbatim from Prelude:
type Either of (%a, %b) is Left(%a) or Right(%b)
type CollEnumerator of (%val, %seed) is alias of
    ((Iteratee of (%val, %seed), %seed) => %seed);
type Iteratee of (%val, %seed) is alias of
    ((%seed, %val) => Either of (%seed, %seed));

-- bug triggering code
enumerateEnumerator has type (CollEnumerator of (%val, %seed)) => CollEnumerator of ((integer, %val), %seed);
fun enumerateEnumerator(enum) is
  ((it, seed) =>
    enum((((count, seed1), val1) =>
      (switch it(seed1, (count, val1)) in {
        case Right(v) is Right((count+1, v));
        case Left(v) is Left(v)
      })),
        (0, seed)));
  }