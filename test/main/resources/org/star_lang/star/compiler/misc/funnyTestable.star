funnyTestable is package {

  contract Testable over %s is {
    toTest has type for all %t such that (%s,%t) => integer where sizeable over %t;
  };

  implementation Testable over string is {
    toTest = ( (a,b) => (size(a)+size(b)));    
  };

  prc main() do { 
    def t1 is toTest("Hello",list of [1,2,3]);
    logMsg(info, "t1 is $t1"); 

    -- the next line got a type error saying 
    -- "dictionary literal not valid here 
    -- because list of integer not equal to dictionary of(%__47213, %__47214)" 

    def t2 is toTest("Good Morning",dictionary of [1->1,2->2]); -- now commented out to pass type checking
    logMsg(info, "t2 is $t2");
  }
}