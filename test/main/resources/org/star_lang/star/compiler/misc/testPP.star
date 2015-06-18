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
testPP is package{  
  import person;
  
  type fooPair is foo((string,integer));
  
  implementation pPrint over ((%a,%b)) where pPrint over %a and pPrint over %b is {
    def ppDisp is dispPair;
  } using {
    fun dispPair((L,R)) is ppSequence(2,cons of { ppStr("!("); ppDisp(L); ppStr(", "); ppDisp(R); ppStr(")")});
  }
  
  type tree of %t is empty or node(tree of %t,%t,tree of %t);
  
  implementation pPrint over tree of %t where pPrint over %t is {
    fun ppDisp(T) is ppSequence(2,cons of {ppStr("{"); treeDisplay(T); ppStr("}")});
  } using {
    fun treeDisplay(empty) is ppSpace
     |  treeDisplay(node(L,Lb,R)) is ppSequence(0,cons of {treeDisplay(L); ppDisp(Lb); treeDisplay(R) })
  } 
  
  prc main() do {
    def Jack is someone{name="Jack"; gender=male};
    
    def DD is ppDisp(Jack);
    logMsg(info,"pp = $DD");
    logMsg(info,"flat = #(display(Jack))");
    
    logMsg(info,"foo = $(foo(("alpha",56)))");
    
    def TT is node(node(empty,"alpha",empty),"beta",node(empty,"gamma",empty));
    logMsg(info,"TT = $(ppDisp(TT))");
    logMsg(info,"TT = $TT");
  }
}

    