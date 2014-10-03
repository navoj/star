/**
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
monadtest is package{
  contract monad over %%c is {
    ret has type for all e such that (e)=>%%c of e;
    bind has type for all e, f such that (%%c of e,(e)=>%%c of f) => %%c of f;
    step has type for all e, f such that (%%c of e, %%c of f) => %%c of f;
    fail has type for all a such that (string) => %%c of a;
    
    step(M,N) default is bind(M, (function(_) is N));
  }
  
  implementation monad over cons is {
    ret(X) is cons of {X};
    bind(Ll,F) is let{
      apply(nil,A) is flat(A,nil);
      apply(cons(E,L), A) is apply(L,cons(F(E),A));
      
      private
      flat(nil,A) is A;
      flat(cons(E,L),A) is flat(L,concat(E,A));
      
      private concat(nil,A) is A;
      concat(cons(E,X),Y) is cons(E,concat(X,Y));
    } in apply(Ll,nil);
    
    fail(S) is nil;
  }
  
  main() do {
    X0 is cons of {"alpha"; "beta"};
    logMsg(info,"X0=$X0");
    X1 is bind(X0,(function(X) is cons of {X;X}));
    logMsg(info,"X1=$X1");
    assert X1=cons of {"alpha";"alpha";"beta";"beta"}
  }
  
}
    