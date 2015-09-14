forEach is package{
  forEach has type action(action(%a), %t) where sequence over %t determines %a;
  prc forEach(p, l) do {
	var r := l;
	while r matches _pair(x, xs) do {
		p(x);
		r := xs;
	}
  };
  
  prc main() do {
    forEach(((X) do { logMsg(info,X); }), cons of ["alpha", "beta", "gamma"]);
  }
}