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
tokens is package{
  type token is idTok(string,astLocation) 
             or integerTok(integer,astLocation)
             or longTok(long,astLocation)
             or floatTok(float,astLocation)
             or decimalTok(decimal,astLocation)
             or charTok(char,astLocation)
             or stringTok(string,astLocation)
             or terminal;

  fun tokenize(SrcTxt,Uri) is let{
    fun allTokens(sequence of {},Lc) is cons of {}
     |  allTokens(Text,Lc) is valof{
          def (Tok,Rest,nLc) is nextToken(Text,Lc);
          logMsg(info,"got token: $Tok");
          valis cons of {Tok;.. allTokens(Rest,nLc)};
        };

    fun nextToken(Text,Lc) is valof{
      def (nText,nLc) is skipComments(Text,Lc);
      valis nxtToken(nText,nLc);
    };

    fun locOf((ChrCount,Ln,Off),C) is _somewhere{
        uri=Uri;charCount=ChrCount;
        lineCount=Ln;lineOffset=Off;
        length=C};

    fun nxtLoc((Count,Ln,Off),C) is (Count+C,Ln,Off+C);
    fun nxtLne((Count,Ln,Off)) is (Count+1,Ln+1,0);

    fun reportId(Id,Cnt,L,Lc) is (idTok(Id,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
    
    fun reportChr(ch,Cnt,L,Lc) is (charTok(ch[0], locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
    
    fun reportString(S,Cnt,L,Lc) is (stringTok(S,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
    
    fun reportChrInteger(ch,Cnt,L,Lc) is (integerTok(ch[0] as integer,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
    
	fun reportInteger(I,Cnt,L,Lc) is  (integerTok(I as integer,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
		
	fun reportLong(Ln,Cnt,L,Lc) is  (longTok(Ln as long,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));

	fun reportFloat(F,Cnt,L,Lc) is  (floatTok(F as float,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
	
	fun reportDecimal(F,Cnt,L,Lc) is  (decimalTok(F as decimal,locOf(Lc,Cnt)),L,nxtLoc(Lc,Cnt));
	
	fun reportTerminal(Lc) is (terminal,"",Lc);
	
    fun skipComments(sequence of {' ';..L}, Lc) is skipComments(L,nxtLoc(Lc,1))
     |  skipComments(sequence of {'\t';..L}, Lc) is skipComments(L,nxtLoc(Lc,1))
     |  skipComments(sequence of {'\n';..L}, Lc) is skipComments(L,nxtLne(Lc))
     |  skipComments(sequence of {'-';'-';' ';..L}, Lc) is lineComment(L,nxtLoc(Lc,3))
     |  skipComments(sequence of {'-';'-';'\t';..L}, Lc) is lineComment(L,nxtLoc(Lc,3))
     |  skipComments(sequence of {'/';'*';..L}, Lc) is blockComment(L,nxtLoc(Lc,2))
     |  skipComments(L,Lc) default is (L,Lc)

    fun lineComment(sequence of {'\n';..L},Lc) is skipComments(L,nxtLne(Lc))
     |  lineComment(sequence of {_;..L},Lc) is lineComment(L,nxtLoc(Lc,1))
     |  lineComment(L,Lc) default is (L,Lc)

    fun blockComment(sequence of {'*';'/';..L},Lc) is skipComments(L,nxtLoc(Lc,2))
     |  blockComment(sequence of {'\n';..L},Lc) is blockComment(L,nxtLne(Lc))
     |  blockComment(sequence of {_;..L},Lc) is blockComment(L,nxtLoc(Lc,1))
     |  blockComment(L,Lc) default is (L,Lc)

    fun nxtToken(T,Lc) is case T in {
      `\$\:(.*:L)` is reportId("\$:",2,L,Lc);
      `\$\$(.*:L)` is reportId("\$\$",2,L,Lc);
      `\$=>(.*:L)` is reportId("\$=>",3,L,Lc);
      `\$(.*:L)` is reportId("\$",1,L,Lc);
      `//(.*:L)` is reportId("//",2,L,Lc);
      `/(.*:L)` is reportId("/",1,L,Lc);
      `-->(.*:L)` is reportId("-->",3,L,Lc);
      `->(.*:L)` is reportId("->",2,L,Lc);
      `-(.*:L)` is reportId("-",1,L,Lc);
      `\#(.*:L)` is reportId("\#",1,L,Lc);
      `\#\((.*:L)` is reportId("\#\(",2,L,Lc);
      `\)\#(.*:L)` is reportId("\)\#",2,L,Lc);
      `\((.*:L)` is reportId("\(",1,L,Lc);
      `\)(.*:L)` is reportId("\)",1,L,Lc);
      `\[(.*:L)` is reportId("\[",1,L,Lc);
      `\](.*:L)` is reportId("\]",1,L,Lc);
      `\{(.*:L)` is reportId("\{",1,L,Lc);
      `\}(.*:L)` is reportId("\}",1,L,Lc);
      `;\.\.(.*:L)` is reportId(";..",3,L,Lc);
      `;\*(.*:L)` is reportId(";*",2,L,Lc);
      `;(.*:L)` is reportId(";",1,L,Lc);
      `\+\+(.*:L)` is reportId("++",2,L,Lc);
      `\+(.*:L)` is reportId("+",1,L,Lc);
      `\^(.*:L)` is reportId("^",1,L,Lc);
      `%%(.*:L)` is reportId("%%",2,L,Lc);
      `%(.*:L)` is reportId("%",1,L,Lc); 
      `@(.*:L)` is reportId("@",1,L,Lc);
      `\?(.*:L)` is reportId("?",1,L,Lc);
      `\*\*(.*:L)` is reportId("**",2,L,Lc);
      `\*(.*:L)` is reportId("*",1,L,Lc);
      `=>(.*:L)` is reportId("=>",2,L,Lc);
      `==>(.*:L)` is reportId("=>",2,L,Lc);
      `\.\.;(.*:L)` is reportId("..;",3,L,Lc);
      `\.\.(.*:L)` is reportId("..",2,L,Lc);
      `\.(.*:L)` is reportId(".",1,L,Lc);
      `0c(.:C)(.*:L)` is reportChrInteger(C,3,L,Lc);
      `([0-9]+:N)(.*:L)` is reportInteger(N,size(N),L,Lc);
      `([0-9]+[lL]:N)(.*:L)` is reportLong(N,size(N),L,Lc);
      `([0-9]*\.[0-9]+[aA]:N)(.*:L)` is reportDecimal(N,size(N),L,Lc);
      `([0-9]*\.[0-9]+[eE][0-9]+:N)(.*:L)` is reportFloat(N,size(N),L,Lc);
	  `([A-Za-z_][A-Za-z0-9_]*:I)(.*:L)` is reportId(I,size(I),L,Lc);
	  `'(.:ch)'(.*:L)` is reportChr(ch,3,L,Lc);
	  `(.:O)(.*:L)` is valof{
	    logMsg(info,"Bad char: $O");
	    valis reportId(O,1,L,Lc)
	  }
	  "" is reportTerminal(Lc);
     }
  } in allTokens(SrcTxt,(0,1,0));
  
  prc main() do {
    def Toks is tokenize(
    """ /* A comment */
  type token -- A line comment 
  -- A line comment /* with a block comment */ in it

  /* Punctuation: */()[]{}#()###=>==>$=>$;..;+*
  /* Identifiers: */ alpha _omega _0345 one_two 
  /* Integers */12 0c0 0x56
  /* Decimal: */123.45a
  /* Floats: */123.45 12.34e10 345.12e-10
  /* Longs: */123l 23L 0x45l
  /* Characters: */'a' 'b' '\n' '@' '\u34;' 
  /* Strings: */"a simple string" "a string with a \n in it" 0'6"a blob "stri\u56;ng"
  /* Interpolated */"A $var string" "an $(expression()+34) string"
  /* Some special symbols */ 'n 's """
    ,"test.star" as uri);
    logMsg(info,"$Toks")
  } 
}