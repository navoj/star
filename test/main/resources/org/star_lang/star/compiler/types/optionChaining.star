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
    phone has type option of list of string
  }
  
  main() do {
    A is address{
      line1 = "1 Main St"
      line2 = none
      city = "Market"
      state = "MyState"
      phone = some(["alpha","beta"])
    }
    
    P is person{
      name = "fred"
      address = some(A)
      call = some(fn ()=>"fred")
    }
    
    logMsg(info,display(P))
    
    assert P.address?.line1 = some("1 Main St")
    assert A.phone??[0] = some("alpha")
    assert P.call??() = some("fred")
    
    assert L unwraps P.address?.line1 and L="1 Main St"
  }
} 
    
    