private import macrosupport;
private import base;
private import arrays;
private import strings;
private import sequences;
private import folding;
  
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
  # while ?L do ?A :: workStatement :- while L do A :: action;
  # for ?C do ?A :: workStatement :- for C do A :: action;
  # { ?B } :: workStatement :- B;* action;
  # try ?B on abort ?A :: workStatement :- B::action;
  # ?S :: workStatement :- S::statement;

  # worksheet{ ?Dfs } ==> genMain(Dfs) ## {
    fun collectActions(Rl matching <|show ?E|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|perform ?A|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|ignore ?A|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|assert ?A|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|?L := ?R|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|while ?L do ?R|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|for ?L do ?R|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|{ ?L }|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(Rl matching <|try ?L on abort ?R|>, (Theta, Actions)) is (Theta,list of [Actions..,Rl])
     |  collectActions(<|?L;?R|>,Coll) is collectActions(R,collectActions(L,Coll))
     |  collectActions(Stmt,(Theta,Actions)) is (list of [Theta..,Stmt],Actions);
    
    fun convertAction(<|show ?E |>) is <|showExpression(?E,?__display_macro(E),?getLineNumber(__macro_location(E)))|>
     |  convertAction(<|perform ?A |>) is <|performAction((() do ?A),?__display_macro(A),?getLineNumber(__macro_location(A)))|>
     |  convertAction(<|ignore ?A |>) is <|performAction((() do ignore ?A),?__display_macro(A),?getLineNumber(__macro_location(A)))|>
     |  convertAction(<|assert ?A |>) is <|assertCondition(() => ?A,?__display_macro(A),?getLineNumber(__macro_location(A)))|>
     |  convertAction(<|?L := ?R |>) is <|performAction((() do { ?L := ?R}),"#(?__display_macro(L)) := #(?__display_macro(R))",?getLineNumber(__macro_location(L)))|>
     |  convertAction(<|?L do ?R |>) is <| ?L do ?convertAction(R) |>
     |  convertAction(<| { ?B } |>) is  <| { ?__mapSemi(B,convertAction) } |>
     |  convertAction(<| try ?B on abort {?A} |>) is  <| try ?convertAction(B) on abort {?__mapSemi(A,convertCase)} |>
     |  convertAction(A) is A;
    
    fun convertCase(<| ?P do ?A|>) is <| ?P do ?convertAction(A) |>;
    
    fun getLineNumber(noWhere) is <|nonInteger|>
     |  getLineNumber(Lc) is integerAst(Lc,Lc.lineCount);
    
    fun genWkSht(D) is valof{
      def (Defs,Actions) is __foldSemi(D,collectActions,(list of [],list of []));
      def Body is __wrapSemi(map(convertAction,Actions),<|nothing|>);
      if Defs=list of [] then
        valis <| ?Body |>
      else
        valis <| let { ?__wrapSemi(Defs, <|{}|>) } in ?Body |>
    }
    
    #fun genMain(D) is <| prc main() do ?genWkSht(D) |> 
  }
  
  prc showExpression(Val,Msg,Line) do logMsg(info,"",Msg++" -> $Val at $Line");
  prc performAction(Prc,Msg,Line) do { Prc(); logMsg(info,"",Msg++" at $Line"); }
  prc assertCondition(A,Msg,Line) do { if A() then logMsg(info,"",Msg++" ok at $Line") else logMsg(info,Msg++" failed at $Line") }