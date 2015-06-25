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
import execution;

stepping is package{
  -- implement single-stepping mode
  
  type eventually of %t is done(%t) or notYet(()=>eventually of %t);
  
  implementation execution over eventually is {
    _zero() is result(unit);
    _bind(X,F) is bind(X,F);
    _combine(X,Y) is combine(X,Y);
    _return(X) is result(X);
    _delay(F) is notYet((function() is F()));
    _run(done(X)) is X;
    _run(notYet(F)) is _run(F());
  } using {
    bind(done(X),F) is notYet((function() is F(X)));
    bind(notYet(W),F) is notYet((function() is bind(W(),F)));
    
    combine(X,Y) is notYet((function() is Y(X)));
    
    result(X) is done(X);
  }
  
  step(X) is switch X in {
    case done(_) is X;
    case notYet(F) is F();
  }
  
  main() do {
    C is eventually build {
      def A is 1;
      def B is 2;
      return A+B;
    };
    
    logMsg(info,"C0=$C");
    
    logMsg(info,"C1=$(step(C))");
    
    logMsg(info,"C2=$(step(step(C)))");
    
    logMsg(info,"C3=$(step(step(step(C))))");
    
    logMsg(info,"C4=$(step(step(step(step(C)))))");
    
    assert step(step(step(C)))=done(3);
  }
}
    
    
    
    