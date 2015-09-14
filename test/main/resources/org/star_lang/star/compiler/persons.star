persons is package{
  type Person is noone 
	          or someone{
		        name has type string;
		        dob has type date;
		        age has type ()=>long;
		        fun age() default is timeDiff(today(),dob);
		      };
	
  def john is someone{
    def name is "john";
	def dob is today();
  };
	
  fun sho(P) is
	"$name:$(age())" using P()'s name 'n age;
	
  prc disp(P) do
	  logMsg(info,"$name:$(age())") using P's name 'n age;
	
  prc main() do
  {
	logMsg(info,sho(() => john));
	disp(john);
	assert john.name="john";
  }
}
