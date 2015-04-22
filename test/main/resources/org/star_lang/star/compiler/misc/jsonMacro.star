import worksheet;
import json

worksheet{
  -- Test with an example json value
	 
  J is json{
    "firstName": "John";
    "lastName": "Smith",
    "age": 25,
    "address": {
        "streetAddress": "21 2nd Street"
        "city": "New York"
        "state": "NY",
        "postalCode": 10021
    },
    "phoneNumbers": [
        {
            "type": "home",
            "number": "212 555-1234"
        },
        {
            "type": "fax"
            "number": "646 555-4567"
        }
    ]
  }
		
  fnames          is J[list of {kString("firstName")}];
  lnames          is J[list of {kString("lastName")}];
  ages            is J[list of {kString("age")}];		
  addresses       is J[list of {kString("address")}];
  streetAddresses is J[list of {kString("address");kString("streetAddress")}];
  cities          is J[list of {kString("address");kString("city")}];
  states          is J[list of {kString("address");kString("state")}];
  postalCodes     is J[list of {kString("address");kString("postalCode")}];
  phoneNumbers    is J[list of {kString("phoneNumbers")}];
				
  phoneNums    is J[list of {kString("phoneNumbers");kInt(0);kString("number")}];
	    
  assert phoneNums has value iText("212 555-1234")
  
  assert fnames has value iText("John")
  
  show J[list of {kString("phoneNumbers");kInt(1)}] 
   		
  assert someValue(J[list of {kString("phoneNumbers");kInt(1)}]) = json{"number":"646 555-4567"; "type":"fax"}
}