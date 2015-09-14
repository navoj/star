monad is package {

  contract monad over %%m is {
    return has type for all a such that (a) => %%m of a;
    bind has type for all a,b such that (%%m of a, (a) => %%m of b) => %%m of b
    fail has type for all a such that () => %%m of a;
  };

  implementation monad over option is {
    fun return(x) is some(x);
    fun bind(m, f) is
			switch m in {
			  case none is none;
			  case some(v) is f(v);
			 };
    fun fail() is none;
  };
}
