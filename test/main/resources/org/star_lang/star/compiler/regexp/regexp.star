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
regexp is package{
  -- Test some of the regexp matching stuff
    
  main has type action();
  main() do {
    testStr is "alphabeta";
    
    assert testStr matches "alphabeta";

    assert testStr matches `alpha(.*:A)` and A="beta";
    
    assert testStr matches `alpha(.*:Alpha)ta` and Alpha="be";
    
    assert not ( testStr matches `alpha(.*:Alpha)ta` and Alpha="beta");
    
    assert testStr matches `(...(.:A).*:B)` and A="h" and B="alphabeta";
    
    assert testStr matches `(a.*b:A)(.*:B)` and A="alphab" and B="eta";
    
    assert "foooBar|P" matches `(.*:A)\|P` and A="foooBar";
    
    assert lengthString("eta")=3;

    logMsg(info,"MINUTES=$MINUTES");
    logMsg(info,"SECONDS=$SECONDS");
     
    assert MINUTES = 13;
    assert SECONDS = 14;
    
    assert "-35.56e100" matches `(\F:F)` and F as float = -35.56e100;

  }

  -- DO NOT use this as a template for string length :)
  lengthString(Str) where Str matches `.(.*:X)` is lengthString(X)+1;
  lengthString("") is 0;

   DateStr is "12-21-2009 12:13:14";
   
   MINUTES is (DateStr matches `.*\:([0-9][0-9]:X)\:.*` ? X as integer | -1);
   SECONDS is (DateStr matches `.*\d[0-9][\u003A;][0-9][0-9][\u003A;]([0-9][0-9]:X)` ? X as integer | -1);
}
      