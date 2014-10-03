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
altArrayTest is package{
  import altarrays;

  main() do {
    A0 is emptyRlist;

    assert isEmpty(A0);
    assert size(A0)=0;

    logMsg(info,"A0=$A0");

    A1 is rCons("alpha",A0);
    logMsg(info,"A1=$A1");
    A2 is rCons("beta", A1);
    logMsg(info,"A2=$A2");
    A3 is rCons("gamma",A2);
    logMsg(info,"A3=$A3");
    A4 is rCons("delta", A3);
    logMsg(info,"A4=$A4");
  }


}