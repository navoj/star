tupleTypes is package{
  type tpl of t is alias of ((t));
  
  T has type ( ( ( ( ( ( string ) ) ) ) ) );
  T is (((((("alpha"))))));
  
  U has type tpl of tpl of tpl of string;
  U is (((((("alpha"))))));
  
  main() do {
    assert T=U
  }
}