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

import star
buffer is package {
  type word8 is _word8(integer);
  word8 has type (integer) => word8;
  word8(o) where o >= 0 and o =< 255 is _word8(o);
  word8(o) is raise "word8 argument is not a word8";

  type word16 is _word16(integer);
  word16 has type (integer) => word16;
  word16(s) where s >= 0 and s =< 0xffff is _word16(s);
  word16(s) is raise "word16 argument is not a word16";

  type word32 is _word32(integer);
  word32(i) default is _word32(i);
  word32(nonInteger) is raise "nonInteger not a word32";

  type word64 is _word64(long);
  word64(l) default is _word64(l);
  word64(nonLong) is raise "nonLong not a word64";
}
