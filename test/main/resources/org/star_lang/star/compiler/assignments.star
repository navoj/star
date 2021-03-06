assignments is package{

  type person is noone or someone{
    name has type ref string;
    spouse has type ref person;
    spouse default := noone;
  }
  
  type family is family{
    parents has type list of person;
    children has type ref list of person;
    children default := list of [];
  };
  
  var LL := list of [1, 2, 3, 4];
  
  var PP := list of [ someone{name:="a"}, someone{name:="b"}, someone{name:="c"}];
  
  prc main() do {
    var F := family{ parents=list of [someone{name:="p"}, someone{name:="m"}] };
    
    var X := 0;
    
    assert X=0;
    X := X+1;
    assert X=1;
    
    if PP[1] has value Px then
      Px.spouse:=someone{name:="d"};
    
    assert someValue(PP[1]).spouse.name = "d";
    assert PP[1]?.spouse?.name has value "d"
    
    logMsg(info,"PP[1]'s spouse's name $(PP[1]?.spouse?.name)")
    
    logMsg(info,"PP is now $PP");
    
    if PP[1] has value Px then
      Px.spouse.name:="e";
    
    logMsg(info,"PP[1]?.spouse?.name = $(someValue(PP[1]).spouse.name)");
    assert someValue(PP[1]).spouse.name = "e";
    
    assert PP[1]?.spouse?.name has value "e"
    
    F.children:=list of [someone{name:="cc"}];
    logMsg(info,"F=$F");
  }
}