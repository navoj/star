import json

worksheet{
  -- Test with an example json value
	 
  def J is json{
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
		
  def fnames          is J[list of {kString("firstName")}];
  def lnames          is J[list of {kString("lastName")}];
  def ages            is J[list of {kString("age")}];		
  def addresses       is J[list of {kString("address")}];
  def streetAddresses is J[list of {kString("address");kString("streetAddress")}];
  def cities          is J[list of {kString("address");kString("city")}];
  def states          is J[list of {kString("address");kString("state")}];
  def postalCodes     is J[list of {kString("address");kString("postalCode")}];
  def phoneNumbers    is J[list of {kString("phoneNumbers")}];
				
  def phoneNums    is J[list of {kString("phoneNumbers");kInt(0);kString("number")}];
	    
  assert phoneNums has value iText("212 555-1234")
  
  assert fnames has value iText("John")
  
  show J[list of {kString("phoneNumbers");kInt(1)}] 
   		
  assert someValue(J[list of {kString("phoneNumbers");kInt(1)}]) = json{"number":"646 555-4567"; "type":"fax"}
}