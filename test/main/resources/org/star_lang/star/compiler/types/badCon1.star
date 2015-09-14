badCon1 is package {
  contract foo over %%c is {
    fool has type for all a,b such that ((a, b) => b, %%c of a, b) => b;
  };

  bar has type (%%c of %a) => cons of %a where foo over %%c;
  fun bar(s) is f1(s, nil) using {
    -- f1 has type (%c,%%c of %a) => cons of %a;
    fun f1(a0,b0) is fool((a,b) => cons(a, b), a0, b0);
  };
}