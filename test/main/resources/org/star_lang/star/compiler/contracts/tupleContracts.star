tupleContracts is package {
  type redBlackTree of %a is RBT(redBlackTree of %a, %a, redBlackTree of %a);

  rbInsert has type (redBlackTree of %a, %a) => redBlackTree of %a where comparable over %a;
  fun rbInsert(s1, x) is s1

  type set of %s is Set(redBlackTree of ((%s, ())))

  setInsert has type (set of %i, %i) => set of %i where comparable over %i and equality over %i;
  fun setInsert(Set(t),a) is Set(rbInsert(t,(a,())));
}
