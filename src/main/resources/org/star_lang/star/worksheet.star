private import macrosupport;
private import base;
private import arrays;
private import strings;
private import sequences;
private import folding;

  -- test out ideas for a Star work sheet.
  
  -- worksheet would be used instead of a package:
  
  /*
  
  import worksheet;
  worksheet{
    import Pkg; ...
    
    definitions ....
    
    perform <action>
    
    show <expression>
  }
  
  which would be the equivalent of
  
  perform
    let{
      import Pkg;
    
      definitions ...
    
    } in action {
      perform <action> @ line
      ...
      show <expression> @ line
    } 
    
  except that this perform would be allied to printing the result in-line rather than on a log file
  
*/
  #prefix("show",1200);
  -- #infix("~",950);
  
  -- First the validation rules

  # worksheet { ?D } :: statement :- D;*workStatement;

  # perform ?A :: workStatement :- A::action;
  # ignore ?E :: workStatement :- E::expression;
  # show ?E :: workStatement :- E::expression;
  # ?N := ?E :: workStatement :- ?N :: lvalue :& ?E :: expression;
  # ?L do ?A :: workStatement :- L do A :: action;
  # { ?B } :: workStatement :- B;* action;
  # try ?B on abort ?A :: workStatement :- B::action;
  # ?S :: workStatement :- S::statement;

  # worksheet{ ?Dfs } ==> genMain(Dfs) ## {
    collectActions(Rl matching <|show ?E|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|perform ?A|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|ignore ?A|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|assert ?A|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|?L := ?R|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|?L do ?R|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|{ ?L }|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(Rl matching <|try ?L on abort ?R|>, (Theta, Actions)) is (Theta,list of {Actions..;Rl})
    collectActions(<|?L;?R|>,Coll) is collectActions(R,collectActions(L,Coll));
    collectActions(Stmt,(Theta,Actions)) is (list of {Theta..;Stmt},Actions);
    
    convertAction(<|show ?E |>) is <|showExpression(?E,?__display_macro(E),?getLineNumber(__macro_location(E)))|>
    convertAction(<|perform ?A |>) is <|performAction((procedure() do ?A),?__display_macro(A),?getLineNumber(__macro_location(A)))|>
    convertAction(<|ignore ?A |>) is <|performAction((procedure() do ignore ?A),?__display_macro(A),?getLineNumber(__macro_location(A)))|>
    convertAction(<|assert ?A |>) is <|assertCondition((function() is ?A),?__display_macro(A),?getLineNumber(__macro_location(A)))|>
    convertAction(<|?L := ?R |>) is <|performAction((procedure() do { ?L := ?R}),"#(?__display_macro(L)) := #(?__display_macro(R))",?getLineNumber(__macro_location(L)))|>
    convertAction(<|?L do ?R |>) is <| ?L do ?convertAction(R) |>
    convertAction(<| { ?B } |>) is  <| { ?__mapSemi(B,convertAction) } |>
    convertAction(<| try ?B on abort {?A} |>) is  <| try ?convertAction(B) on abort {?__mapSemi(A,convertCase)} |>
    convertAction(A) is A;
    
    convertCase(<| ?P do ?A|>) is <| ?P do ?convertAction(A) |>;
    
    getLineNumber(noWhere) is <|nonInteger|>;
    getLineNumber(Lc) is integerAst(Lc,Lc.lineCount);
    
    #genWkSht(D) is valof{
      (Defs,Actions) is __foldSemi(D,collectActions,(list of {},list of {}));
      Body is __wrapSemi(map(Actions,convertAction),<|nothing|>);
      if Defs=list of {} then
        valis <| ?Body |>
      else
        valis <| let { ?__wrapSemi(Defs, <|{}|>) } in ?Body |>
    }
    
    #genMain(D) is <| main() do ?genWkSht(D) |> 
  }
  
  showExpression(Val,Msg,Line) do logMsg(info,"",Msg++" -> $Val at $Line");
  performAction(Prc,Msg,Line) do { Prc(); logMsg(info,"",Msg++" at $Line"); }
  assertCondition(A,Msg,Line) do { if A() then logMsg(info,"",Msg++" ok at $Line") else logMsg(info,Msg++" failed at $Line") }