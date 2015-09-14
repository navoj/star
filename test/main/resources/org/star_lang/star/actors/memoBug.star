memoBug is package {
    def actorA is memo actor{
       prc replan() do request actorB()'s replan to replan();

       prc restock() do nothing;
    } using {
        prc dummy() do nothing;
    };
    
    def actorB is memo actor{
        prc removeLot(L) do request actorA()'s restock to restock();
        
        prc replan() do nothing; 
    };
}
