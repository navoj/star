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
mapcomp is package {
    main has type action();
    prc main() do {
        def M1 is dictionary of ["a"->0,"b"->1];
        def M2 is dictionary of ["b"->0,"c"->1];
        for K1->V1 in M1 do {
            if(not (K1->V2 in M2)) then logMsg(info, "$K1 is not in $M2");
        }
        for K2->V2 in M2 do {
            if(not (K2->V1 in M1)) then logMsg(info, "$K2 is not in $M1");
        }
    }
}