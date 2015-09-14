catalog is package{
	type catalog of %t is manifest{
		entries has type dictionary of (string,catalogReference of %t);
	}
	
	type catalogReference of %t is local(catalogEntry of %t)
	                            or remote{uri has type uri of %t};
	
	type catalogEntry of %t is entry(%t)
	                        or catalog(catalog of %t);
	
	type uri of %t is uri{
		scheme has type string;
		ref has type string;
		resolve has type ()=>%t;
	}
}