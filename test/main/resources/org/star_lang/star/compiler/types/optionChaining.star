optionChaining is package{
  type person is person{
    name has type string;
    address has type option of address;
    call has type option of (()=>string);
  }
  
  type address is address{
    line1 has type string;
    line2 has type option of string;
    city has type string;
    state has type string;
    phone has type list of string
  }
  
  prc main() do {
    def A is address{
      line1 = "1 Main St"
      line2 = none
      city = "Market"
      state = "MyState"
      phone = ["alpha","beta"]
    }
    
    def P is person{
      name = "fred"
      address = some(A)
      call = some(()=>"fred")
    }
    
    logMsg(info,display(P))
    
    assert P.address?.line1 = some("1 Main St")
    assert A.phone[0] has value "alpha"
    assert someValue(P.call)() = "fred"
    
    assert P.address?.line1 has value "1 Main St"
  }
} 

    