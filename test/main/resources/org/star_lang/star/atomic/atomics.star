atomics is package{
  contract assignment over %t determines %v is {
    _atomic has type (%v)=>%t;
    _get has type (%t)=>%v;
    _assign has type action(%t,%v);
  }
  
  implementation assignment over atomic of %t determines %t is {
    fun _atomic(V) is atomic(V);
    fun _get(A) is __atomic_reference(A);
    prc _assign(A,V) do __atomic_assign(A,V);
  }
  
  implementation assignment over atomic_int determines integer is {
    fun _atomic(integer(I)) is atomic_int(I);
    fun _get(A) is integer(__atomic_int_reference(A));
    prc _assign(A,integer(I)) do __atomic_int_assign(A,I);
  }
}