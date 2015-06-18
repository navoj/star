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
  fun tt(X) is task{
    valis valof task { valis X+2 };
  }
  
  fun pos(X) is X>0;
  
  def alpha is "alpha";
  def beta is "beta";
  
  cc has type (integer)=>task of string;
  fun cc(X) is task{
    valis "$X"
  }
  
  fun S0(X) is task{ valis (pos(X) ? alpha : valof cc(X)) }

  fun S1(X) is task{ valis (pos(X) ? valof task{ valis "$X"}  : beta) }
  
  def L is list of {1;2;3};

  fun S2(X) is task{ valis X in L ? valof task { valis X} : nonInteger };
  
  prc main() do{
    def R0a is valof S0(1);
    def R0b is valof S0(-1);
   
    def R1a is valof S1(2);
    def R1b is valof S1(-2);
   
    def R2a is valof S2(1);
    def R2b is valof S2(4);
   
    assert R0a=alpha;
    assert R0b="-1";
   
    assert R1a="2";
    assert R1b=beta;
   
    assert R2a=1;
    assert R2b=nonInteger;
  }
}