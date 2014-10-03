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
quoting is package{
  main() do
  {
    P is quote("Alpha");
    Q is quote((query A with all X where (X,unquote(P)) in M));
    
    logMsg(info,"$Q");
    
    assert quote("alpha")=quote("alpha");
    assert quote(A)=quote(A);
    assert quote(f(A))=quote(f(A));
    
    assert Q=quote((query A with all X where (X,unquote(P)) in M));
    
    QS is <| "An interpolated $String" |>;
    logMsg(info,"QS=$QS");
    
    assert Q=quote((query A with all X where (X,"Alpha") in M));
    
    logMsg(info,"$(quote(#(var X is found())#))");
  }
}