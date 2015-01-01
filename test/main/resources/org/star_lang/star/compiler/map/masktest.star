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
masktest is package{
    private commonMask(H1,H2) is valof{
    var M := 0;
    var H := 1_;
    var Lst := 0_;
    -- logMsg(info,"compute common mask of #(integer(H1)) and #(integer(H2))");
    while M<32 do{
      -- logMsg(info,"H=$(integer(H))");
      H1U is __integer_bit_and(H,H1);
      H2U is __integer_bit_and(H,H2);
      
      -- logMsg(info,"H1U=$(integer(H1U)), H2U=$(integer(H2U))");
      
      if __integer_eq(H1U,H2U) then {
        Lst := H1U;
        H := __integer_bit_or(__integer_bit_shl(H,1_),1_); --   (H.<<.1)+1;
        M := M+1;
      } else
        valis Lst;
    };
    valis Lst;
  };
  
  mask(integer(H1),integer(H2)) is integer(commonMask(H1,H2));
          
  main() do {
    for I in iota(0,15,1) do {
      for J in iota(0,15,1) do {
        logMsg(info,"common mask of $I,$J is $(mask(I,J))");
      }
    }
    
    assert mask(1,1)=1;
    assert mask(10,26)=10;
  }
}