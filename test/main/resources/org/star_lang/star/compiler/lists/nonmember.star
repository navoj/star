nonmember is package{
  member has type ((%a, %a) => boolean, %a, cons of %a) => boolean;
  fun member(eq,y,lis) is valof{
    var r := lis;
    while r matches cons(x,xs) do{
      if eq(x,y) then
        valis true;
      r := xs;
    };
    valis false
  };
  
  def vanillaList is cons(1, cons(2, cons(3, nil)));
  
  #AssertBool(?s, ?expr) ==> assertBool( () => ?expr);
  
  assertBool has type (()=>boolean)=>boolean;
  fun assertBool(F) is F();
  
  prc main() do {
    assert AssertBool("member4", not member((=), 4, vanillaList));
  }
}
    