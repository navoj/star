plus is package{
  contract pluss over (l,r) determines s is {
    plus has type (l,r)=>s;
  };
  
  implementation pluss over (integer,integer) determines integer is {
    fun plus(integer(L),integer(R)) is integer(__integer_plus(L,R));
  }
  
  implementation pluss over (integer,long) determines long is {
    fun plus(integer(L),long(R)) is long(__long_plus(__integer_long(L),R));
  }
  
  implementation pluss over (long,integer) determines long is {
    fun plus(long(L),integer(R)) is long(__long_plus(L,__integer_long(R)));
  }
  
  implementation pluss over (integer,float) determines float is {
    fun plus(integer(L),float(R)) is float(__float_plus(__integer_float(L),R));
  }
  
  implementation pluss over (float,float) determines float is {
    fun plus(float(L),float(R)) is float(__float_plus(L,R));
  }
}