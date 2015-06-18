badConTypes is package{
  private import badTypes;

  type carrierAgentType is carrier{
    carrierId has type carrierIdType;
 	contains has type ref(list of integer);
 	location has type ref(integer);
  };
     
  makeCarrierAgent has type (carrierIdType, list of integer, integer) => carrierAgentType;
  fun makeCarrierAgent(C, Ls, P) is carrier{
    carrierId = C;
    contains := Ls;
    location := P;
  };
}