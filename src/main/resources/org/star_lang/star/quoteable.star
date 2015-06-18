/**
 * Package to facilitate conversion between quoted form and actual values 
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
private import base;
private import sequences;
private import strings;
private import casting;
private import macrosupport;
private import folding;
private import iterable;
private import arrays;

private type possible of %t is impossible or exactly(%t);

implementation coercion over (quoted, list of %t) where coercion over (quoted,%t) is{
  fun coerce(R) is dequoteRel(R)
} using {
  dequoteRel has type (quoted) => list of %t where coercion over (quoted,%t);
  fun dequoteRel(<| list of { ?Q } |>) is let{
        fun dequoteSemi(<| ?L ; ?R |>) is dequoteSemi(L)++dequoteSemi(R)
         |  dequoteSemi(El) is list of {El as %t}
      } in dequoteSemi(Q)
   |  dequoteRel(Q) is valof{
        logMsg(info,"Cannot dequote $Q");
        valis list of {}
      }
}

implementation coercion over (quoted,()) is {
  fun coerce(<|()|>) is ()
}

implementation coercion over ((),quoted) is {
  fun coerce(()) is <|()|>
}

implementation coercion over (((%t)),quoted) where coercion over (%t,quoted) is {
  fun coerce(((X))) is <| (( ?xCoerce(X) )) |>
} using {
  fun xCoerce(X) is X as quoted;
}

implementation coercion over (quoted,((%t))) where coercion over (quoted,%t) is {
  fun coerce(<|((?X))|>) is (( xCoerce(X) ))
} using {
  fun xCoerce(X) is X as %t;
}

implementation coercion over ((%l,%r),quoted) where coercion over (%l,quoted) and coercion over (%r,quoted) is {
  fun coerce((L,R)) is <| (?xCoerce(L), ?xCoerce(R)) |>;
} using {
  fun xCoerce(X) is X as quoted;
}

implementation coercion over (quoted, (%l,%r)) where coercion over (quoted,%l) and coercion over (quoted, %r) is {
  fun coerce(<|(?L,?R)|>) is (lCoerce(L),rCoerce(R))
} using {
  fun lCoerce(X) is X as %l;
  fun rCoerce(X) is X as %r;
}

fun dequoteField(Q,Name) is let{
  fun findQF(_,E matching exactly(_)) is E
   |  findQF(<| ?L ; ?R |>,M) is findQF(R,findQF(L,M))
   |  findQF(<| ?F = ?V |>,_) where F matches nameAst(_,Nm) and Nm=Name is exactly(V)
   |  findQF(_,_) default is impossible;

  fun unpack(exactly(S)) is S
   |  unpack(impossible) is raise "$Name is not present";
} in unpack(findQF(Q,impossible));

implementation coercion over (list of %t,quoted) where coercion over (%t,quoted) is{
  fun coerce(R) is quoteList(R)
} using {
  quoteList has type (list of %t) => quoted where coercion over (t,%quoted);
  fun quoteList(R) where isEmpty(R) is <| list of [] |>
   |  quoteList(R) is <| list of [ ?quoteSeq(R) ] |>;
};

quoteSeq has type (%s)=>quoted where iterable over %s determines %t and coercion over (%t,quoted);
fun quoteSeq(S) is valof{
  var El := <| () |>;
  for E in S do{
    def QEl is E as quoted;
    if El = <| () |> then
      El := QEl
    else
      El := <| ?QEl , ?El |>
  }
  valis El;
};

#infix("implementing",1300);

# type ?N is ?Algebraic implementing ?Specs :: statement :- N::typeSpec :& Algebraic :: valueSpecifier :& Specs :: names;
# type ?Ptn is ?Algebraic implementing ?Specs ==> #(type Ptn is Algebraic; #*generate(Specs) )# ## {
	#generate(?L and ?R) ==> #(generate(L) ; generate(R) )#;
	#generate((?Id)) ==> generateSpecs(Id);
	#generate(identifier?Id) ==> #(implement_#+Id)#(Ptn,Algebraic)
}

#implement_quotable(?Ptn,?Spec) ==> #(
 implementation #*dequoteTemplate(Ptn) is {
   fun coerce(X) is dequote(X)
 } using {
   fun #*genDequote(Spec)
 };
 implementation #*quoteTemplate(Ptn) is {
   fun coerce(Q) is quoted(Q)
 } using {
   fun #*genQuote(Spec)
 }
 )# ## {
  # genQuote(?L or ?R) ==> genQuote(L) | genQuote(R);
  # genQuote(?ConSpec) ==> genQuoteCon(ConSpec);

  # genQuoteCon(#(?H)#{?A}) ==>
      #( quoted(H{ #*first(gendFields) }) is q(#* H{ #*second(gendFields) } ) )# ## {     
	#genFields( #( ?F has type ?Tp )# ) ==> (#(F = #$F)#, #(F = #(?)#(#$F as quoted) )#);
	#genFields( #( ?L ; ?R )# ) ==>
	    distribute( #*genFields(L) , #*genFields(R) );
	#genFields( #( ?F default is ?E )#) ==> (); -- cannot be the only entry
	#genFields( #( assert ?C )#) ==> ();
	#gendFields ==> #*genFields(A);
  };
  # genQuoteCon(identifier ? I) ==> #( quoted(I) is <|I|> )#;
  # genQuoteCon( #(?O #@ ?A)#) ==> #( quoted(Con) is q(Term))# ##{
    #gnArgs((?A1,?R1)) ==> ((#$A,first(Rgs)), (#(?)#(#$A as quoted),second(Rgs))) ## {
      #Rgs ==> #*gnArgs(R1);
    }
    #gnArgs(()) ==> ((),());
    #genArgs ==> #*gnArgs(#:A);
    #Con ==> O #@ #<first(genArgs) >#;
    #Term ==> O #@ #<second(genArgs) >#;
  };

  #q ==> #(quote)#;

  # genDequote(?L or ?R) ==> #( genDequote(L) | genDequote(R) )#;
  # genDequote(?ConSpec) ==> genDequoteCon(ConSpec);

  # genDequoteCon(#(?H)#{?A}) ==>
    #( dequote(<| H{ #(?)#(#$A) } |>) is H{ #*genFields(A,#$A) } )# ## {
      #genFields( #( ?F has type ?Tp )#,?Arg ) ==>
        #( F = dequoteField( Arg, $$F) as Tp )#;
      #genFields( #( ?L ; ?R )#,?Arg ) ==>
        glom( #*genFields(L,Arg) , #*genFields(R,Arg) );
      #genFields( #( ?F default is ?E)#, ?Arg ) ==> ();
      #genFields( #( assert ?C)# ,?Arg) ==> ();
    };
  # genDequoteCon(identifier ? I) ==> #( dequote(<| I |>) is I )#;
  # genDequoteCon( #(?O #@ ?A)#) ==> #( dequote(<| Con |>) is Term | dequote(XX) is raise "cannot dequote $XX" )# ##{
     #gnArgs((?A1,?R1)) ==> ((#(?)#(#$A),first(Rgs)), ((#$A as A1),second(Rgs))) ## {
       #Rgs ==> #*gnArgs(R1);
     }
     #gnArgs(()) ==> ((),());
     #genArgs ==> #*gnArgs(#:A);
     #Con ==> O #@ #<#*first(genArgs) >#;
     #Term ==> O #@ #<#*second(genArgs) >#;
    };

  # first((?F,?S)) ==> F;
  # second((?F,?S)) ==> S;

  # distribute( (), ?A) ==> A;
  # distribute( ?A, ()) ==> A;
  # distribute( (?F1,?S1), (?F2,?S2)) ==> ( #(F1;F2)#, #(S1;S2)# );
  
  # qtTemplate(identifier?Tp,?Fn) ==> coercion over Fn(Tp);
  # qtTemplate(?Tp of ?Args,?Fn) ==> #( coercion over Fn(Tp of Args) where findRequirements(Args) )# ## {
    #findRequirements(tuple?T) ==> composeConstraints(#*findReqs(#:T));
    #findRequirements(?T) ==> composeConstraints(#*findReqs((T,())));

    #findReqs(()) ==> ();
    #findReqs((?L,?R)) ==> (coercion over Fn(L),findReqs(R));

    #composeConstraints((?C,())) ==> C;
    #composeConstraints((?L,?R)) ==> #( L and composeConstraints(R) )#;
  }
  
  #quoteTemplate(?Tp) ==> #*qtTemplate(Tp,QQ) ## {
    # QQ(?T) ==> (T,quoted)
  }
  #dequoteTemplate(?Tp) ==> #*qtTemplate(Tp,DeQ) ## {
    # DeQ(?T) ==> (quoted,T)
  }
  
  #glom(?AA,?BB) ==> glue(AA,BB) ## {
    #fun glue(X,Y) is glm(X,Y);
    
    fun glm(A,<|()|>) is A
     |  glm(<|()|>,A) is A
     |  glm(<|?L;?R|>,A) is <|?L;?glm(R,A)|>
     |  glm(A,B) is <|?A;?B|>;
  };
}
