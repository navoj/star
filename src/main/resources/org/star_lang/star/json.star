/*
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
 */
private import base;
private import strings;
private import sequences;
private import option;
private import arrays;
private import cons;
private import maps;
private import iterable;
private import compute;
private import arithmetic;
private import casting;
private import folding;

  -- implement an json type
  -- intended to represent flexible datasets such as those found in JSON and XML

  type json is 
      iFalse or
      iTrue or
      iNull or
      iColl(dictionary of (string,json)) or
      iSeq(list of json) or
      iText(string) or
      iNum(long) or
      iFlt(float);

  type infoPathKey is kString(string) or kInt(integer);
  type infoPath is alias of list of infoPathKey;

  implementation indexable over json determines (infoPath,json) is {
    fun _index(I,K) is find(K,I);
    fun _set_indexed(I,K,V) is updte(K,I,V);
    fun _delete_indexed(I,K) is remve(K,I);
  } using {
    find has type (infoPath,json)=>option of json;
    fun find(list of [],I) is some(I)
     |  find(list of [Ky,..Keys],I) is 
	      switch Ky in {
            case kString(K) where I matches iColl(M) and M[K] has value S is find(Keys,S);
            case kInt(Ix) where I matches iSeq(L) and L[Ix] has value S is find(Keys,S);
            case _ default is none
	      };

    updte has type (infoPath,json,json)=>json;
    fun updte(list of [],I,_) is raise "path not valid"
     |  updte(list of [Ky],I,V) is switch Ky in {
          case kString(K) where I matches iColl(M) is iColl(M[K->V]);
          case kInt(Ix) where I matches iSeq(L) is iSeq(L[Ix->V]);
          case _ default is raise "illegal key $Ky"
        }
     |  updte(list of [Ky,..Keys],I,V) is switch Ky in {
          case kString(K) where I matches iColl(M) is iColl(M[K->updte(Keys,someValue(M[K]),V)]);
          case kInt(Ix) where I matches iSeq(L) is iSeq(L[Ix->updte(Keys,someValue(L[Ix]),V)]);
          case _ default is raise "illegal key $Ky";
        }

    remve has type (infoPath,json)=>json;
    fun remve(list of [],I) is I
     |  remve(list of [Ky],I) is switch Ky in {
          case kString(K) where I matches iColl(M) is iColl(_delete_indexed(M,K));
          case kInt(Ix) where I matches iSeq(L) is iSeq(_delete_indexed(L,Ix));
          case _ default is raise "illegal key $Ky";
        }
     |  remve(list of [Ky,..Keys],I) is switch Ky in {
          case kString(K) where I matches iColl(M) is iColl(_set_indexed(M,K,remve(Keys,someValue(M[K]))));
          case kInt(Ix) where I matches iSeq(L) is iSeq(_set_indexed(L,Ix,remve(Keys,someValue(L[Ix]))));
          case _ default is raise "illegal key $Ky";
        }
  }

  implementation iterable over json determines json is {
    fun _iterate(I,F,S) is infoIterate(I,F,S);
  } using {
    fun infoIterate(I,F,S) is let{
      fun IterateInfo(_,NoMore(X)) is NoMore(X)
       |  IterateInfo(iColl(M),St) is _iterate(M,IterateInfo,St)
       |  IterateInfo(iSeq(L),St) is _iterate(L,IterateInfo,St)
       |  IterateInfo(Info,St) default is F(Info,St)
    } in IterateInfo(I,S);
  }

  implementation indexed_iterable over json determines (infoPath,json) is {
    fun _ixiterate(I,F,St) is indexInfoIterate(I,F,St,list of [])
  } using {
    fun indexInfoIterate(_,_,NoMore(X),_) is NoMore(X)
     |  indexInfoIterate(iColl(M),F,S,P) is _ixiterate(M,pathFun,S) using {
          fun pathFun(Ky,El,St) is indexInfoIterate(El,F,St,list of [P..,kString(Ky)]);
        }
     |  indexInfoIterate(iSeq(L),F,S,P) is _ixiterate(L,pathFun,S) using {
          fun pathFun(Ix,El,St) is indexInfoIterate(El,F,St,list of [P..,kInt(Ix)]);
        }
     |  indexInfoIterate(I,F,St,P) default is F(P,I,St);
  };

  implementation pPrint over json is {
    fun ppDisp(iColl(M)) is ppSequence(2, cons of [ppStr("{"), ppSequence(0,dispContent(M)), ppStr("}")])
     |  ppDisp(iSeq(L)) is ppSequence(0, cons of [ppStr("["), ppSequence(0,dispSeq(L)), ppStr("]")])
     |  ppDisp(iText(S)) is ppStr(display(S))
     |  ppDisp(iNum(I)) is ppStr(display(I))
     |  ppDisp(iFlt(F)) is ppStr(display(F))
     |  ppDisp(iFalse) is ppStr("false")
     |  ppDisp(iTrue) is ppStr("true")
     |  ppDisp(iNull) is ppStr("null")

    private
    fun dispContent(M) is interleave(cons of { ppSequence(0,cons of [ppStr(display(K)),ppStr(":"),ppDisp(V)]) where K->V in M },ppStr(","));
    
    private
    fun dispSeq(L) is interleave(cons of { ppDisp(E) where E in L},ppStr(","));
  }
  
  implementation coercion over (string,json) is {
    fun coerce(S) is valof{
      def (J,_) is jParse(explode(S))
      valis J
    }
  }
  
  implementation coercion over (json,string) is {
    fun coerce(I) is display(I);
  }
  
  private fun jParse(L) is jP(skipBlanks(L));
  
  private
  fun skipBlanks(list of [' ',..L]) is skipBlanks(L)
   |  skipBlanks(list of ['\t',..L]) is skipBlanks(L)
   |  skipBlanks(list of ['\n',..L]) is skipBlanks(L)
   |  skipBlanks(list of ['\r',..L]) is skipBlanks(L)
   |  skipBlanks(L) default is L

  private
  fun jP(sequence of ['t','r','u','e',..L]) is (iTrue,L)
   |  jP(list of ['f','a','l','s','e',..L]) is (iFalse,L)
   |  jP(list of ['n','u','l','l',..L]) is (iNull,L)
   |  jP(L matching (list of ['-',.._])) is parseNumber(L)
   |  jP(L matching (list of [D,.._])) where isDigit(D) is parseNumber(L)
   |  jP(list of ['"',..L]) is parseStr(L,nil)
   |  jP(list of ['[',..L]) is parseSeq(L)
   |  jP(list of ['{',..L]) is parseMap(L)
   |  jP(L) default is raise "cannot parse "++implode(L)++" as json"
  
  private
  fun parseSeq(list of [']',..L]) is (iSeq(list of []),L)
   |  parseSeq(L) is valof{
        def (El,L0) is jParse(L);
        var SoFar := list of [El];
        var LL := skipBlanks(L0);
    
        while LL matches list of [',',..Lx] do {
          def (Elx,LLx) is jParse(Lx);
          SoFar := list of [SoFar..,Elx];
          LL := skipBlanks(LLx);
        };
    
        if LL matches list of [']',..Lx] then
          valis (iSeq(SoFar),Lx)
        else
          raise "missing ']'";
      }
  
  private 
  fun parseMap(list of ['}',..L]) is (iColl(dictionary of []),L)
   |  parseMap(L) is valof{
        def (K1,V1,L0) is parsePair(L);
        var SoFar := dictionary of [K1->V1];
        var LL := skipBlanks(L0);
    
        while LL matches list of [',',..Lx] do {
          def (Ky,Vl,LLx) is parsePair(Lx);
          SoFar[Ky] := Vl;
          LL := skipBlanks(LLx);
        }
    
        if LL matches list of ['}',..Lx] then
          valis (iColl(SoFar),Lx)
        else
          raise "missing '}'";
      }
  
  private
  fun parsePair(L0) is valof{
    def (iText(K),L1) is jParse(L0);
    if skipBlanks(L1) matches list of [':',..L2] then{
      def (V,L3) is jParse(L2);
      valis (K,V,L3);
    }
    else raise "expecting a ':'";
  }
    
  private
  fun parseStr(list of ['"',..L],SoFar) is (iText(revImplode(SoFar)),L)
   |  parseStr(list of ['\\','u',H1,H2,H3,H4,..L],SoFar) is parseStr(L,cons of [grabHex(list of [H1,H2,H3,H4],0),..SoFar])
   |  parseStr(list of ['\\','\\',..L],SoFar) is parseStr(L,cons of ['\\',..SoFar])
   |  parseStr(list of ['\\','b',..L],SoFar) is parseStr(L,cons of ['\b',..SoFar])
   |  parseStr(list of ['\\','f',..L],SoFar) is parseStr(L,cons of ['\f',..SoFar])
   |  parseStr(list of ['\\','n',..L],SoFar) is parseStr(L,cons of ['\n',..SoFar])
   |  parseStr(list of ['\\','r',..L],SoFar) is parseStr(L,cons of ['\r',..SoFar])
   |  parseStr(list of ['\\','t',..L],SoFar) is parseStr(L,cons of ['\t',..SoFar])
   |  parseStr(list of ['\\',X,..L],SoFar) is parseStr(L,cons of [X,..SoFar])
   |  parseStr(list of [X,..L],SoFar) is parseStr(L,cons of [X,..SoFar])
     
  private
  fun grabHex(list of [],Hx) is Hx as char
   |  grabHex(list of [X,..L],Hx) where isHexDigit(X) is 
        grabHex(L,Hx*16+hexDigitVal(X))

  private 
  fun isHexDigit(X) is ('0'<=X and X<='9') or ('a'<=X and X<='f') or ('A'<=X and X<='F')
  
  private 
  fun revImplode(X) is string(__string_rev_implode(X));
     
  private 
  fun hexDigitVal(X) where '0'<=X and X<='9' is X as integer-'0' as integer
   |  hexDigitVal(X) where 'a'<=X and X<='f' is X as integer-'a' as integer+10
   |  hexDigitVal(X) where 'A'<=X and X<='F' is X as integer-'A' as integer+10
     
  fun parseNumber(Str) is let{
    fun parseInt(list of [D,..L],Nm) where isDigit(D) is parseInt(L,Nm*10l+digitVal(D))
     |  parseInt(L,Nm) default is (Nm,L)

    fun parseFrac(list of [D,..L],Nm,F) where isDigit(D) is 
          parseFrac(L,Nm+(digitVal(D)as float)*F,F/10.0)
     |  parseFrac(L,Nm,_) default is (Nm,L)

    fun parseNum(list of ['-',..L]) is valof{
          def (I,Rest) is parseNum(L);
          valis (negate(I),Rest)
        }
     |  parseNum(L) is parseMore@parseInt(L,0l)

    fun parseMore(Nm,list of ['.',..L]) is 
	      parseExp@parseFrac(L,Nm as float,0.1)
     |  parseMore(Nm,L matching (list of ['e',.._])) is
	      parseExp(Nm as float,L)
     |  parseMore(Nm,L) is (iNum(Nm),L)

    fun parseExp(Nm,list of [E,..L]) where E='e' or E='E' is parseX(Nm,L)
     |  parseExp(Nm,L) is (iFlt(Nm),L)

    fun parseX(Nm,list of ['+',..L]) is parseX(Nm,L)
     |  parseX(Nm,L) is valof{
          def (Ex,LL) is parseInt(L,0l);
          valis (iFlt(Nm*10.0**(Ex as float)),LL)
        }

    fun negate(iNum(I)) is iNum(-I)
     |  negate(iFlt(F)) is iFlt(-F)

    private
    fun digitVal('0') is 0l
     |  digitVal('1') is 1l
     |  digitVal('2') is 2l
     |  digitVal('3') is 3l
     |  digitVal('4') is 4l
     |  digitVal('5') is 5l
     |  digitVal('6') is 6l
     |  digitVal('7') is 7l
     |  digitVal('8') is 8l
     |  digitVal('9') is 9l
  } in parseNum(Str);

  private
  fun isDigit('0') is true
   |  isDigit('1') is true
   |  isDigit('2') is true
   |  isDigit('3') is true
   |  isDigit('4') is true
   |  isDigit('5') is true
   |  isDigit('6') is true
   |  isDigit('7') is true
   |  isDigit('8') is true
   |  isDigit('9') is true
   |  isDigit(_) default is false

  private fun razer(E) is raise E;
  
  -- Implement the json notation as a first class value, except that we allow semi-colons between elements in a record
  
#json{ ?B } :: expression :- B ;* jsonEntry ## {
  ?L , ?R :: jsonEntry :- L :: jsonEntry :& R :: jsonEntry;
  
  string : ?V :: jsonEntry :- V :: jsonValue;
  
  false :: jsonValue;
  true :: jsonValue;
  null :: jsonValue;
  
  { ?E } :: jsonValue :- E;*jsonEntry;
  [ ?E ] :: jsonValue :- E :: jsonSeq ## {
    ?L , ?R :: jsonSeq :- L::jsonSeq :& R::jsonSeq;
    ?L :: jsonSeq :- L::jsonValue;
  }
  
  string :: jsonValue;
  number :: jsonValue;
}

#json{?B} ==> jsonColl(B) ## {
  #fun jsonColl(E) is <|iColl(dictionary of [?jsonMapElements(E)])|>
  
  fun jsonMapElements(<|?L;?R|>) is <|?jsonMapElements(L),?jsonMapElements(R)|>
   |  jsonMapElements(<|?L,?R|>) is <|?jsonMapElements(L),?jsonMapElements(R)|>
   |  jsonMapElements(<|?S:?E|>) is <|?S->?jsonValue(E)|>
  
  fun jsonValue(<|true|>) is <|iTrue|>
   |  jsonValue(<|false|>) is <|iFalse|>
   |  jsonValue(<|null|>) is <|iNull|>
   |  jsonValue(<|{}|>) is <|iColl(dictionary of [])|>
   |  jsonValue(<|{?E}|>) is jsonColl(E)
   |  jsonValue(<|[]|>) is <|iSeq(list of [])|>
   |  jsonValue(<|[?E]|>) is <|iSeq(list of [ ?jsonSeq(E) ])|>
   |  jsonValue(<|string?T|>) is <|iText(?T)|>
   |  jsonValue(<|integer?I|>) is <|iNum(?I as long)|>
   |  jsonValue(<|long?L|>) is <|iNum(?L)|>
   |  jsonValue(<|float?F|>) is <|iFlt(?F)|>
  
  fun jsonSeq(<|?L;?R|>) is <|?jsonSeq(L),?jsonSeq(R)|>
   |  jsonSeq(<|?L,?R|>) is <|?jsonSeq(L),?jsonSeq(R)|>
   |  jsonSeq(L) is jsonValue(L)
}

