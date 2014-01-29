badTypes is package{
  type carrierIdType is carrier(integer) 
	  or onTool        -- Note: Could add the toolId as an argument
	  or nonCarrierId;
}