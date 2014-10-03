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
condexptask is package{
  import tasking;
  
  tt(X) is task{
    valis valof task { valis X+2 };
  }
  
  pos(X) is X>0;
  
  alpha is "alpha";
  beta is "beta";
  
  cc has type (integer)=>task of string;
  cc(X) is task{
    valis "$X"
  }
  
  S0(X) is task{ valis (pos(X) ? alpha | valof cc(X)) }

  S1(X) is task{ valis (pos(X) ? valof task{ valis "$X"}  | beta) }
  
  L is list of {1;2;3};

  S2(X) is task{ valis X in L ? valof task { valis X} | nonInteger };
  
  main() do{
    R0a is valof S0(1);
    R0b is valof S0(-1);
   
    R1a is valof S1(2);
    R1b is valof S1(-2);
   
    R2a is valof S2(1);
    R2b is valof S2(4);
   
    assert R0a=alpha;
    assert R0b="-1";
   
    assert R1a="2";
    assert R1b=beta;
   
    assert R2a=1;
    assert R2b=nonInteger;
  }
}