/**
 * 
 * Copyright (C) 2013 Starview Inc
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
jsonTest is package{
  import json;

  var I1 := iColl( map of { "eventVersion"-> iText("1.0");
     "sourceString"->iText("::1 - - [29/Aug/2010:06:12:57 +0700] \"OPTIONS * HTTP/1.0\" 200 152 \"-\" \"Apache/2.2.12 (Ubuntu) (internal dummy connection)\"");
     "sourceName"->iText("access log");
     "Status"->iText("200");
     "Useragent"->iText("Apache/2.2.12 (Ubuntu) (internal dummy connection)");
     "Date"->iText("29/Aug/2010:06:12:57 +0700");
     "Host"->iText("::1");
     "Ident"->iText("-");
     "Referer"->iText("-");
     "Request"->iText("OPTIONS * HTTP/1.0");
     "Bytes"->iText("152");
     "Authuser"->iText("-");
     "messageDate"->iNum(1368743816990L);
     "processedDate"->iNum(1368743816990L);
     "hostBytes"->iNum(3040L);
     "numUniqueUsers"->iNum(19L);
     "bigDownloadUser"->iText("false");
     "avgHostBytes"->iFlt(61668.47916666669);
     "imageBytes"->iNum(110408L);
     "pictureBytes"->iNum(396883L);
     "statusFrequency"->iSeq(array of {
	iColl(map of {"200"->iNum(75L)});
	iColl(map of {"304"->iNum(60L)});
	iColl(map of {"404"->iNum(5L)});
	iColl(map of {"206"->iNum(2L)});
	iColl(map of {"301"->iNum(2L)})});
     "totalBytes"->iNum(2425928L);
     "referFrequency"->iSeq(array of {
	iColl(map of {"-"->iNum(85L)});
	iColl(map of {"http://www.vallop.in.th/"->iNum(36L)});
	iColl(map of {"http://www.tb1nkp.com/webboard/view.php?No=1"->iNum(6L)});
	iColl(map of {"http://www.kpsw.ac.th/teacher/piyaporn/page3.htm"->iNum(5L)});
	iColl(map of {"http://whois.domaintools.com/ntsdc.go.th"->iNum(4L)})})});

  main() do {
    logMsg(info,"Init $I1");

    logMsg(info,"Look=$(I1[list of {kString("statusFrequency");kInt(0);kString("200")}])");

    assert I1[list of {kString("statusFrequency");kInt(0);kString("200")}]=iNum(75L);

    I1[list of {kString("statusFrequency");kInt(0);kString("404")}] := iNum(7L);
    logMsg(info,"After update $I1");
 
    assert I1[list of {kString("statusFrequency");kInt(0);kString("200")}]=iNum(75L);
    assert I1[list of {kString("statusFrequency");kInt(0);kString("404")}]=iNum(7L);

    remove I1[list of {kString("statusFrequency");kInt(0)}]
    logMsg(info,"After remove $I1");

    assert not present I1[list of {kString("statusFrequency");kInt(0);kString("404")}];

    for E in I1 do
	logMsg(info,display(E));

    for P->E in I1 do 
	logMsg(info,"Path=$P, E=$E");
	
	assert "true" as json = iTrue;
	assert "34" as json = iNum(34L);
    
    logMsg(info,"float parse = $("-45.45e23" as json)");
    assert "-45.45e23" as json matches F and approx(F,iFlt(-45.45e23));
    
    assert "\"fred\"" as json matches iText("fred");
    assert "\"fr\\ned\"" as json matches iText("fr\ned");
    assert "\"fr\\\\ed\"" as json matches iText("fr\\ed");
    assert "\"fr\\u1234ed\"" as json matches iText("fr\u1234;ed");
    
    S0 is "[116, 943, 234, 38793]";
  
    logMsg(info,"parse S0=$(S0 as json)");
    assert S0 as json = iSeq(list of { iNum(116l); iNum(943l); iNum(234l); iNum(38793l)});
    
    S1 is "{ \"alpha\" : 1, \"beta\" : [116, 943, 234, 38793] }";
    logMsg(info,"parse S1=$(S1 as json)");
    
    attr is "{\"Image\": {\"Width\": 800,\"Height\": 600,\"Title\": \"View from 15th Floor\" }}";
    logMsg(info,"parse attr = $(attr as json)");
    
  }

  approx has type (json,json)=>boolean;
  approx(iFlt(X),iFlt(Y)) is abs(X-Y)/abs(X+Y)<1.0e-10;
}