freeInArg is package{
  -- test case where free variable is argument
  
  fun outer(X) is let{
    fun inner((U,V)) is switch X in {
        case (A,B) is ((U+A),(V+B))
      }
  } in inner;

  
  prc main() do {
    assert outer((1,2))((3,2))=(4,4);
  }
}