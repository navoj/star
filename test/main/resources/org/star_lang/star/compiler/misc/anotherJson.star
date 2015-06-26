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
	
	
	def someJsonStr is "{\"firstName\": \"John\",\"lastName\": \"Smith\",\"age\": 25,\"address\": {\"streetAddress\": \"21 2nd Street\",\"city\": \"New York\",\"state\": \"NY\",\"postalCode\": 10021},\"phoneNumbers\": [{\"type\": \"home\",\"number\": \"212 555-1234\"},{\"type\": \"fax\",\"number\": \"646 555-4567\"}]}";
	 
    show "parse attr=$(someJsonStr as json)";
		
	def JJ is someJsonStr as json;
		
	def fnames          is JJ[list of [kString("firstName")]];
	def lnames          is JJ[list of [kString("lastName")]];
	def ages            is JJ[list of [kString("age")]];		
	def addresses       is JJ[list of [kString("address")]];
	def streetAddresses is JJ[list of [kString("address"),kString("streetAddress")]];
	def cities          is JJ[list of [kString("address"),kString("city")]];
	def states          is JJ[list of [kString("address"),kString("state")]];
	def postalCodes     is JJ[list of [kString("address"),kString("postalCode")]];
	def phoneNumbers    is JJ[list of [kString("phoneNumbers")]];
		
	-- [{"number":"212 555-1234","type":"home"},{"number":"646 555-4567","type":"fax"}]
	-- phoneTypes      is JJ[list of [kString("phoneNumbers"),[{kString("type")}]]];
		
	def phoneNums    is JJ[list of [kString("phoneNumbers"),kInt(0),kString("number")]];
	    
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