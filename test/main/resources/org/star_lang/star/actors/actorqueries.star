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
actorqueries is package{
  import person;
  
   fred is someone{
    name = "fred";
    gender = male
  };
  
  john is someone{
    name = "john";
    gender = male
  };
  
  jim is someone{
    name="jim";
    gender = male
  };
  
  peter is someone{
    name="peter";
    gender = male;
  };
  
  jane is someone{
    name="jane";
    gender = female;
  };
  
  sue is someone{
    name="sue";
    gender = female;
  };
  
  sally is someone{
    name="sally";
    gender = female;
  };
  
  repo has type repoActorType;
  var repo is actor{
    var children := list of[
       { parent = fred; child = sue},
       { parent = fred; child = john},
       { parent = jane; child = john}
     ];
     var married := list of [
       { husband = fred; wife = jane},
       { husband = john; wife = sally}
     ];
     
     recordBirth(P,C) do {
       extend children with {parent=P;child=C};
     }
   };
   
 type repoActorType is alias of actor of{
    children has type ref list of { parent has type person; child has type person};
    married has type ref list of {husband has type person; wife has type person};
    recordBirth has type action(person,person);
  };
  
  queryActor has type (repoActorType)=>actor of {
    findUnmarried has type action(person);
    findSiblings has type action(person);
    recordBirth has type action(person,person,person);
  };
  queryActor(R) is actor{
    findUnmarried(P) do {
      logMsg(info,"finding unmarried children of $P");
      QQ is query R's children'n married with 
         list of {
           all S where {parent=PP;child=S} in children and PP=P and not ({husband=S} in married or {wife=S} in married)
         };
      logMsg(info,"QQ is $QQ");
    };
    
    findSiblings(W) do {
      logMsg(info,"siblings of $W");
      logMsg(info,"siblings of $W are $(query R's children with all S where {parent=P;child=S} in children and {parent=P;child=W} in children and W!=S)");
    };
    
    recordBirth(F,M,C) do{
      request R's recordBirth to {
        recordBirth(F,C);
        recordBirth(M,C);
      }
    }
  };
   
  main() do {    
    QQ is query repo's children 'n married with
      all C where { parent=S; child=C} in children and S.name="fred" and not ({ husband = C} in married or { wife=C} in married);
    
    logMsg(info,"fred has unmarried children $QQ");
    
    qA is queryActor(repo);
    
    request qA's findUnmarried to findUnmarried(fred);
    
    request qA's recordBirth to  { recordBirth(john,sally,jim); recordBirth(john,sally,peter); }; 
    
    request qA's findSiblings to { findSiblings(john); findSiblings(jim); }
  }
}
    