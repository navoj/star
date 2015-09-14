helloMorning is package {
	prc hello(who,morning) default do logMsg(info, "Good Morning, $who")
	 |  hello(who,morning) where not morning do logMsg(info, "Hello $who")
	prc main() do {
		hello("Debu",true);
		hello("Tim",false);
	}
}