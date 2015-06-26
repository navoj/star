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

-- Test conjunctive event rules
-- This actor looks for a ping event and a pong event and raises a pingpong event when it sees 
eventProcessor is package{
  monitoredStockSymbols is ["COOV" , "BHKG" , "JJQA" , "ZBVK" , "DIBZ" , "LBZT" , "ZJFS" , "JKXP" , "CRXL" , "CVOG" , "ZUJA" , "DUSA" , "UMLH" , "OJUT" , "BTAL" , "KEUA" , "XENB" , "ZADI" , "ALTL" , "PQBI" , "QOZQ" , "RRJM" , "CEIN" , "MGNV" , "XDGV" , "GZRR" , "VYVL" , "NLUJ" , "PIMA" , "BLCW" , "BFYC" , "GZDW" , "NRAC" , "LBNU" , "JQAC" , "RHKB" , "UELO" , "IRUQ" , "SPHS" , "CFBA" , "YJLA" , "ZILL" , "TVKQ" , "UIZF" , "IAGK" , "MZHF" , "KDCD" , "YPWC" , "OSKY" , "HIJY" , "ZOSY" , "WKKF" , "HLBZ" , "OEFF" , "NWJE" , "HCJL" , "OQIR" , "OVQP" , "MKRX" , "VREM" , "HJCC" , "JLOC" , "YLQF" , "KCJG" , "BWKS" , "JFIE" , "XWFB" , "ASOW" , "SREP" , "SSXU" , "YEAQ" , "DZFU" , "TGLH" , "FPEE" , "CWCO" , "WXYJ" , "ENXE" , "TSPV" , "QNZU" , "UHVX" , "AXNM" , "AGPU" , "DRMP" , "DRTI" , "LRJL" , "RZUU" , "ASYN" , "IGIF" , "HWAN" , "GCZT" , "KAZR" , "UGOD" , "PFJH" , "EIIX" , "XCIS" , "RAWZ" , "BIOK" , "RKNZ" , "EFHS" , "MWUI" , "LKOK" , "BRUT" , "WGNT" , "TTSS" , "FJQZ" , "HFKS" , "WKZD" , "LHUS" , "HCRQ" , "PVDA" , "IQDK" , "BYCR" , "KWSJ" , "FPZV" , "LCGP" , "JTNT" , "ZIZU" , "CSKE" , "TKGB" , "SDYL" , "TQOM" , "VFBL" , "OCUB" , "TDLX" , "LYRS" , "QTPC" , "RYJW" , "SPYK" , "ILFW" , "YBRE" , "SPBJ" , "HUNB" , "YLKP" , "ONSU" , "DVPW" , "FJUX" , "FXVK" , "MQWL" , "AXFD" , "FFUP" , "RDGW" , "ZMQI" , "IWXN" , "JNMQ" , "YRJL" , "QUTX" , "GVDY" , "BURM" , "GBUZ" , "PCHS" , "WCXL" , "CRZX" , "ZKJK" , "UNZV" , "VIFN" , "UWJU" , "FECE" , "LVZJ" , "YFJX" , "QKAC" , "OOUF" , "QOYX" , "HFLL" , "DYUK" , "GXWK" , "PEQK" , "OVLB" , "XFRF" , "OBHH" , "FLTC" , "XWCZ" , "RDPX" , "TAJZ" , "RJZW" , "FWKF" , "EJVI" , "VVFB" , "BVDX" , "NHBO" , "BDUS" , "RXXX" , "KJTE" , "MFND" , "UIWC" , "DGSR" , "HFMA" , "EMZO" , "NSYW" , "WCBX" , "LWPU" , "JUZB" , "GBZQ" , "WCNE" , "YJLR" , "RFUV" , "JQZV" , "DLOW" , "XLSO" , "GUEQ" , "HDGD" ];
  printAgent is printConditionAgent();
  type timestamp is alias of long;
  type stockAgentType is alias of actor{		
    inflow has type port of (StockEvent, long);
   	outflow has type occurrence of (OutputStockEvent, long);
  };
  type printConditionAgentType is alias of actor{
  	inflow has type port of (OutputStockEvent, long);
  };
  type StockEvent is stockEvent{
    stockSymbol has type string;
	price has type float;
	initTimestamp has type long;
	loadGenTimestamp has type long; -- load Generator Timestamp
  };
  type OutputStockEvent is 
	outputStockEvent{
		stockSymbol has type string;
		lastPrice has type float;
		price has type float;
		initTimestamp has type long;
		loadGenTimestamp has type long;
		countRate has type float;
    };
  mainAgent has type actor{
    STOCK_EVENT has type port of (StockEvent, long);
	OUTPUT_STOCK_EVENT has type occurrence of (OutputStockEvent, long);
	TO_BROADCAST has type port of (OutputStockEvent, long);
  }; 	
  mainAgent is actor{
  	stockAgent has type (string) =>stockAgentType;
    stockAgent(s) is actor{
  	  stockSymbol is s;
  	  var lastPrice := -1.0;
      var incCount := 0.0;
      on stockEvent{stockSymbol=N; price=P; initTimestamp=Time; loadGenTimestamp=T2} at T from inflow do { 
    	def b is s=N;    			
     	if(lastPrice = -1) then{
    	  lastPrice = P;
    	  -- logMsg(info, "setting initial price of last Price");
    	};
    	float var incRate := 1000000;
    	if (lastPrice != 0) then 
     	  incRate := abs((P-lastPrice)/lastPrice);
     	if ( incRate > 0.02) then{
     	  post outputStockEvent{stockSymbol=N; lastPrice= lastPrice; price=P; initTimestamp=Time; loadGenTimestamp=T2; countRate=incRate} at T to inflow of printAgent;
     	  post outputStockEvent{stockSymbol=N; lastPrice= lastPrice; price=P; initTimestamp=Time; loadGenTimestamp=T2; countRate=incRate} at T to OUTPUT_STOCK_EVENT;
       	};
     	if( P > lastPrice) then
     	  incCount := incCount + 1
     	else 
     	  incCount := 0;
     	if incCount > 2 then {
     	  post outputStockEvent{stockSymbol=N; lastPrice=lastPrice; price=P; initTimestamp=Time; loadGenTimestamp=T2;countRate=incCount} at T to inflow of printAgent;	
     	  post outputStockEvent{stockSymbol=N; lastPrice=lastPrice; price=P; initTimestamp=Time; loadGenTimestamp=T2;countRate=incCount} at T to OUTPUT_STOCK_EVENT;	
     	};
     	lastPrice := P;
     	-- logMsg(info, "actor $s NEW LAST PRICE IS $lastPrice");
      };
  	};
  	var agentsMap := valof{
      var hMap := dictionary of [];
      for stock in monitoredStockSymbols do{
  		a is stockAgent(stock);
  		mupdate(hMap, stock, a);
	  };
      valis hMap;
    };
  	NOT_FOUND is stockAgent("NOT_FOUND");
	on stockEvent{stockSymbol=S; price=P; initTimestamp=Ti; loadGenTimestamp=Tg} at T from STOCK_EVENT do {
	  -- logMsg(info, " ********** MAIN AGENT ************ ");
	  if(mcontains(agentsMap,S)) then {		
		-- logMsg(info, "**************************** In ");
		a is mget(agentsMap, S, NOT_FOUND);
		post stockEvent{stockSymbol=S; price=P; initTimestamp=Ti; loadGenTimestamp=Tg} at T to inflow of a;			
	  }else{
		-- logMsg(info, "**************************** Out ");
		nothing;
	  };
	}
  }
  printConditionAgent has type () => printConditionAgentType;
  printConditionAgent() is actor{
	on outputStockEvent{stockSymbol=N; lastPrice = LP; price=P; initTimestamp=Time; loadGenTimestamp=T2; countRate=R} at T from inflow do{
	  -- logMsg(info, "********* PRINTAGENT *********");
	  logMsg(info, "OUTPUT EVENT, symbol: $N, countRate: $R");
	};
  };
  -- why cannot use abs has type[%t] => %t where %t requires arithmetic;
  abs has type (float) => float;
  abs(N) where N>= 0 is N;
		abs(N) where N<0 is -N;
		mod has type [float, float] => float;
		mod(A,B) where A >= B is mod(A-B, B);
		mod(A,B) where A < B is A;  
  main() do {
    long var i := 0;
    float var newPrice := 4;
	-- TEST RATE
    while i < 10 do{
		if mod(i,2) = 0 then
    	  newPrice := newPrice + newPrice * 0.010001
    	else
    	  newPrice := newPrice - newPrice * 0.020001;
    	-- logMsg(info, "new Price: $newPrice");
    	 post stockEvent{stockSymbol="COOVw"; price= newPrice; initTimestamp=i; loadGenTimestamp=i} at i to STOCK_EVENT of mainAgent;
    	i := i + 1;
    };
	-- TEST COUNT
	i := 1;
	while i < 10 do{
		if mod(i,8) = 0 then
    	  newPrice := newPrice - newPrice * 0.011
    	else
    	  newPrice := newPrice + newPrice * 0.011;
    	-- logMsg(info, "new Price: $newPrice");
    	 post stockEvent{stockSymbol="COOV"; price= newPrice; initTimestamp=i; loadGenTimestamp=i} at i to STOCK_EVENT of mainAgent;
    	i := i + 1;
    }
    sleep(10000);
  };
}