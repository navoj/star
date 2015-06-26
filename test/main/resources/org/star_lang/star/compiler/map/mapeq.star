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
mapeq is package{
  prc main() do
  {
    M1 has type dictionary of (string,integer);
    def M1 is dictionary of ["alpha"->1, "beta"->2, "gamma"->3];
    
    def L1 is list of ["alpha", "beta", "gamma"];
    
    def M2 is valof{
      var M := dictionary of [];
      for Ix in iota(1,3,1) do
        M[someValue(L1[Ix-1])]:= Ix;
      valis M
    };
    
    logMsg(info,"M1=$M1");
    logMsg(info,"M2=$M2");
    assert M1=M2;
    
    assert M1!=dictionary of ["alpha"->1];
    assert M1!=dictionary of ["alpha"->1, "beta"->2, "gamma"->3, "delta"->4];
  }
}