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
lexdigit is package{

  readNumber(Str) is let{
    readNum(sequence of{'0';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l,Count+1);
    readNum(sequence of{'1';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+1l,Count+1);
    readNum(sequence of{'2';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+2l,Count+1);
    readNum(sequence of{'3';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+3l,Count+1);
    readNum(sequence of{'4';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+4l,Count+1);
    readNum(sequence of{'5';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+5l,Count+1);
    readNum(sequence of{'6';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+6l,Count+1);
    readNum(sequence of{'7';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+7l,Count+1);
    readNum(sequence of{'8';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+8l,Count+1);
    readNum(sequence of{'9';..L},NumSoFar,Count) is readNum(L,NumSoFar*10l+9l,Count+1);
    readNum(L,Num,Count) default is Num;
  } in readNum(Str,0l,0);
  
  main() do {
    XX is readNumber(cons of {'0';'3';'4'});
    logMsg(info,"XX=$XX");
    assert XX=34l;
    assert readNumber("34")=34l;
  }
}
