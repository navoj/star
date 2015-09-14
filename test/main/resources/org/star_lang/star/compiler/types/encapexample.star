encapexample is package {
  type E1 is e1a {
    x has kind type;
    y has kind type;
    z has kind type;
    f has type (x, y) => z;
  } or e1b {
    a has kind type;
    b has kind type;
    c has kind type;
    f1 has type E1;
    f2 has type E1;
    g has type (a, b) => c;
  };

 /* makeE1B has type (E1,E1,(E1.z,E1.z)=>E1.c) => E1;
  fun makeE1B(n1, n2, G) is e1b {
    type c is alias of 
    def f1 is n1;
    def f2 is n2;
    def g is G;
  };
*/
  fun runE1B(x, y, e) is e.g(e.f1.f(x, y), e.f2.f(x, y));

  prc main() do {
    def e11 is e1a {
      type x is alias of integer;
      type y is alias of integer;
      type z is alias of integer;
      def f is (+);
    };
    def e12 is e1a {
      type x is alias of integer;
      type y is alias of integer;
      type z is alias of integer;
      def f is (*);
    };
--     eb1 is makeE1B(e11, e12, (-));
    def eb1 is e1b{
      type a is alias of e11.z;
      type b is alias of e12.z;
      type c is alias of integer;
      def f1 is e11;
      def f2 is e12;
      def g is (-);
    }
    assert(-1 = runE1B(2, 3, eb1));
  }
}
