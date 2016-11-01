/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

private import base;

public fun connect(string(H),integer(P)) is __connect(H,P);

public fun openReadfile(string(F)) is __openInFile(F);

public fun openWriteFile(string(F)) is __openOutFile(F);

public prc closeFile(IO) do __closeIO(IO);

public fun readLn(IO) is string(__readLn(IO)) default nonString;

public fun readCh(IO) is integer(__readChar(IO));

public fun readAll(string(F)) is valof{
  def IO is __openInFile(F);
  def Txt is __readAll(IO);
  __closeIO(IO);
  valis string(Txt)
}

public prc writeLn(IO,string(L)) do __writeLn(IO,L);

public prc writeStr(IO,string(L)) do __writeStr(IO,L);

public fun atEof(IO) is __atEof(IO);