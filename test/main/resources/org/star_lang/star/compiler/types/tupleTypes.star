tupleTypes is package{
  type tpl of t is alias of ((t));
  
  T has type ( ( ( ( ( ( string ) ) ) ) ) );
  def T is (((((("alpha"))))));
  
  U has type tpl of tpl of tpl of string;
  def U is (((((("alpha"))))));
  
  prc main() do {
    assert T=U
  }
}