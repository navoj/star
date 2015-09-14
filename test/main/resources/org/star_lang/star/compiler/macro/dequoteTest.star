dequoteTest is package{
  import quoteable;

  type person is
    noone or
    someone{
      name has type string;
      age has type float;
      dob has type long;
    } implementing quotable;

   type tree of %t is
    empty or
    node(tree of %t,%t,tree of %t) implementing quotable;

  prc main() do {
    def N is <|noone|> as person;

    logMsg(info,"N is $N");

    def S is <|someone{ name="fred"; age=0.0; dob=12345l }|> as person;
    logMsg(info,"S is $S");

    assert S.name="fred";

    def T is <|node(node(empty,1,empty),2,node(empty,3,empty))|> as (tree of integer);

    logMsg(info,"T=$T");
    assert T matches node(_,2,_);
    
    def Q is S as quoted;
    logMsg(info,"Quoted S = $Q");
    
    assert Q as person = S;
    
    logMsg(info,"Quoted tuple: $( ((34)) as quoted )");
    logMsg(info,"Dequoted tuple: $( <| ((34)) |> as ((integer)))");
    
    
    logMsg(info,"Quoted pair: $( ("alpha",3) as quoted )");
    logMsg(info,"Dequoted: $( <| (3,5.6) |> as (integer,float) )");
  }
}