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
private import base;

connect(string(H),integer(P)) is __connect(H,P);

openReadfile(string(F)) is __openInFile(F);

openWriteFile(string(F)) is __openOutFile(F);

closeFile(IO) do __closeIO(IO);

readLn(IO) is string(__readLn(IO)) default nonString;

readCh(IO) is char(__readChar(IO)) default nonChar;

readAll(string(F)) is valof{
  IO is __openInFile(F);
  Txt is __readAll(IO);
  __closeIO(IO);
  valis string(Txt)
}

writeLn(IO,string(L)) do __writeLn(IO,L);

writeStr(IO,string(L)) do __writeStr(IO,L);

atEof(IO) is __atEof(IO);