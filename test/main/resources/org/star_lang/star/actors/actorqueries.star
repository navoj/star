actorqueries is package{
  import person;
  
  def fred is someone{
    name = "fred";
    gender = male
  };
  
  def john is someone{
    name = "john";
    gender = male
  };
  
  def jim is someone{
    name="jim";
    gender = male
  };
  
  def peter is someone{
    name="peter";
    gender = male;
  };
  
  def jane is someone{
    name="jane";
    gender = female;
  };
  
  def sue is someone{
    name="sue";
    gender = female;
  };
  
  def sally is someone{
    name="sally";
    gender = female;
  };
  
  repo has type repoActorType;
  def repo is actor{
    var children := list of[
       { parent = fred; child = sue},
       { parent = fred; child = john},
       { parent = jane; child = john}
     ];
     var married := list of [
       { husband = fred; wife = jane},
       { husband = john; wife = sally}
     ];
     
     prc recordBirth(P,C) do {
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
  fun queryActor(R) is actor{
    prc findUnmarried(P) do {
      logMsg(info,"finding unmarried children of $P");
      def QQ is query R's children'n married with 
         list of {
           all S where {parent=PP;child=S} in children and PP=P and not ({husband=S} in married or {wife=S} in married)
         };
      logMsg(info,"QQ is $QQ");
    };
    
    prc findSiblings(W) do {
      logMsg(info,"siblings of $W");
      logMsg(info,"siblings of $W are $(query R's children with all S where {parent=P;child=S} in children and {parent=P;child=W} in children and W!=S)");
    };
    
    prc recordBirth(F,M,C) do{
      request R's recordBirth to {
        recordBirth(F,C);
        recordBirth(M,C);
      }
    }
  };
   
  prc main() do {    
    def QQ is query repo's children 'n married with
      all C where { parent=S; child=C} in children and S.name="fred" and not ({ husband = C} in married or { wife=C} in married);
    
    logMsg(info,"fred has unmarried children $QQ");
    
    def qA is queryActor(repo);
    
    request qA's findUnmarried to findUnmarried(fred);
    
    request qA's recordBirth to  { recordBirth(john,sally,jim); recordBirth(john,sally,peter); }; 
    
    request qA's findSiblings to { findSiblings(john); findSiblings(jim); }
  }
}
    