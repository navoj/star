import worksheet

worksheet{
	type Org is Org{
		depts has type list of {dpt has type string};
		employees has type list of {dpt has type string; emp has type string; }
		tasks has type list of {emp has type string; tsk has type string}
    }
    
    O is Org{
      depts is list of [
      	  {dpt = “Product”},
      	  {dpt = “Quality”},
      	  {dpt = “Research”},
      	  {dpt = “Sales”}
      	 ]; 
      employees is list of [
        {dpt = “Product”; emp = “Alex”},
        {dpt = “Product”; emp = “Bert”},
        {dpt = “Research”; emp = “Cora”},
        {dpt = “Research”; emp = “Drew”},
        {dpt = “Research”; emp = “Edna”},
        {dpt = “Sales”; emp = “Fred”}
      ];
      tasks is list of [
        {emp = “Alex”; tsk = “build”},
        {emp = “Bert”; tsk = “build”},
        {emp = “Cora”; tsk = “abstract”},
        {emp = “Cora”; tsk = “build”},
        {emp = “Cora”; tsk = “design”},
        {emp = “Drew”; tsk = “abstract”},
        {emp = “Drew”; tsk = “design”},
        {emp = “Edna”; tsk = “abstract”}, 
        {emp = “Edna”; tsk = “call”},
        {emp = “Edna”; tsk = “design”},
        {emp = “Fred”; tsk = “call”}
      ]
    }
    
    U is "abstract"
    
    Q is list of { all D.dpt where D in O.depts and (E in O.employees and E.dpt=D.dpt) implies (T in O.tasks and T.emp=E.emp and T.tsk=U) }
    
    assert Q = list of [ "Quality", "Research" ] 
}
    