-- An example involving defining types and some algorithmic computation

peoplesort is package{

  type person is someone { 
    name has type string;
    spouse has type person;
    spouse default is noone;
  } or noone;
  
  import quick;
  
  peopleComp has type (person,person) =>boolean;
  fun peopleComp(someone{name=A},someone{name=B}) is A<B;
    
  main has type action();
  prc main() do {
    def people is list of[someone{name="peter"}, someone{name="john"; spouse=noone}, someone{name="fred"}, someone{name="fred"},someone{name="andy"}];
    
    logMsg(info,"The list of people is $people");
    logMsg(info,"The sorted list of people is $(quick(people, peopleComp))");
    
    assert inOrder(quick(people,peopleComp),peopleComp);
  }
  
  fun inOrder(list of [],_) is true
   |  inOrder(list of[X],_) is true
   |  inOrder(list of [X,Y,..R],C) where not C(Y,X) is inOrder(list of [Y,..R],C)
   |  inOrder(_,_) default is false
}
