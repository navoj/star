import worksheet;
import json

worksheet{
	
	
	/*  ***************************************************************************************************************
	 *  *******      Here an example json string:
	 * ***************************************************************************************************************** 
	 
	{
    	"firstName": "John",
    	"lastName": "Smith",
    	"age": 25,
    	"address": {
        	"streetAddress": "21 2nd Street",
        	"city": "New York",
        	"state": "NY",
        	"postalCode": 10021
    	},
    	"phoneNumbers": [
        	{
            	"type": "home",
            	"number": "212 555-1234"
        	},
        	{
            	"type": "fax",
            	"number": "646 555-4567"
        	}
    	]
	}
	
	***************************************************************************************************************** 
	*/
	
	
	someJsonStr is "{\"firstName\": \"John\",\"lastName\": \"Smith\",\"age\": 25,\"address\": {\"streetAddress\": \"21 2nd Street\",\"city\": \"New York\",\"state\": \"NY\",\"postalCode\": 10021},\"phoneNumbers\": [{\"type\": \"home\",\"number\": \"212 555-1234\"},{\"type\": \"fax\",\"number\": \"646 555-4567\"}]}";
	 
    show "parse attr=$(someJsonStr as json)";
		
	JJ is someJsonStr as json;
		
	fnames          is JJ[list of {kString("firstName")}];
	lnames          is JJ[list of {kString("lastName")}];
	ages            is JJ[list of {kString("age")}];		
	addresses       is JJ[list of {kString("address")}];
	streetAddresses is JJ[list of {kString("address");kString("streetAddress")}];
	cities          is JJ[list of {kString("address");kString("city")}];
	states          is JJ[list of {kString("address");kString("state")}];
	postalCodes     is JJ[list of {kString("address");kString("postalCode")}];
	phoneNumbers    is JJ[list of {kString("phoneNumbers")}];
		
	-- [{"number":"212 555-1234","type":"home"},{"number":"646 555-4567","type":"fax"}]
	-- phoneTypes      is JJ[list of {kString("phoneNumbers");[{kString("type")}]}];
		
	phoneNums    is JJ[list of {kString("phoneNumbers");kInt(0);kString("number")}];
	    
	show phoneNums
	   		
	show fnames;
	show lnames;
	show ages;
	show addresses
	show streetAddresses;
	show cities;
	show states;
	show postalCodes;
	show phoneNumbers;
}