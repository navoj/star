/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
-- An example involving defining types and some algorithmic computation

peoplesort is package{

  type person is someone { 
    name has type string;
    spouse has type person;
    spouse default is noone;
  } or noone;
  
  import quick;
  
  peopleComp has type (person,person) =>boolean;
  peopleComp(someone{name=A},someone{name=B}) is A<B;
    
  main has type action();
  main() do {
    people is list of[someone{name="peter"}, someone{name="john"; spouse=noone}, someone{name="fred"}, someone{name="fred"},someone{name="andy"}];
    
    logMsg(info,"The list of people is $people");
    logMsg(info,"The sorted list of people is $(quick(people, peopleComp))");
    
    assert inOrder(quick(people,peopleComp),peopleComp);
  }
  
  inOrder(list of [],_) is true;
  inOrder(list of[X],_) is true;
  inOrder(list of [X,Y,..R],C) where not C(Y,X) is inOrder(list of [Y,..R],C);
  inOrder(_,_) default is false;
}
