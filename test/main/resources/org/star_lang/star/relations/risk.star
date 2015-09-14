risk is package{

  def countries is list of [
    { name="Western Australia";
      borders = list of ["Eastern Australia", "Indonesia", "Papua New Guinea"];
      continent="Australia"
    },
    { name="Eastern Australia";
      borders = list of ["Western Australia", "Papua New Guinea"];
      continent="Australia"
    },
    { name="Papua New Guinea";
      borders = list of ["Western Australia", "Eastern Australia", "Indonesia"];
      continent="Australia"
    },
    { name= "Indonesia";
      borders = list of ["Western Australia","Thailand","Papua New Guinea"];
      continent="Australia"
    },
    { name= "Argentina";
      borders = list of ["Peru","Brazil"];
      continent="South America"
    },
    { name= "Brazil";
      borders = list of ["Peru","Argentina", "Venezuela","West Africa"];
      continent="South America"
    },
    { name= "Peru";
      borders = list of ["Argentina","Brazil","Venezuela"];
      continent="South America"
    },
    { name= "Venezuela";
      borders = list of ["Peru","Brazil","Mexico"];
      continent="South America"
    },
    { name= "Alaska";
      borders = list of ["Alberta","North West Territories","Kamchatka"];
      continent="North America"
    },
    { name= "Alberta";
      borders = list of ["Alaska","North West Territories","Ontario","California"];
      continent="North America"
    },
    { name= "California";
      borders = list of ["Mexico","Alberta","Eastern United States","Ontario"];
      continent="North America"
    },
    { name= "Eastern United States";
      borders = list of ["Mexico","Ontario","California","Quebec"]
      continent="North America"
    },
    { name= "Greenland";
      borders = list of ["North West Territories","Ontario","Quebec","Iceland"];
      continent="North America"
    },
    { name= "Mexico";
      borders = list of ["Venezuela","California","Eastern United States"];
      continent="North America"
    },
    { name= "North West Territories";
      borders = list of ["Alaska","Alberta","Greenland","Ontario"];
      continent="North America"
    },
    { name= "Ontario";
      borders = list of ["North West Territories","Alberta", "California","Eastern United States","Quebec","Greenland"];
      continent="North America"
    },
    { name= "Quebec";
      borders = list of ["Greenland","Ontario","Eastern United States"];
      continent="North America"
    },
    { name= "Germany";
      borders = list of ["Russia","Scandinavia","Southern Europe","Western Europe"];
      continent="Europe"
    },
    { name= "Iceland";
      borders = list of ["Greenland","Scandinavia","United Kingdom"];
      continent="Europe"
    },
    { name= "Russia";
      borders = list of ["Germany","Scandinavia","Southern Europe","Kazakstan","Middle East","Ural"];
      continent="Europe"
    },
    { name= "Scandinavia";
      borders = list of ["Germany","Iceland","Russia","United Kingdom"];
      continent="Europe"
    },
    { name= "Southern Europe";
      borders = list of ["Germany","Russia","Western Europe","Egypt","West Africa","Middle East"];
      continent="Europe"
    },
    { name= "United Kingdom";
      borders = list of ["Iceland","Scandinavia","Western Europe"];
      continent="Europe"
    },
    { name= "Western Europe";
      borders = list of ["Germany","Southern Europe","United Kingdom","West Africa"];
      continent="Europe"
    },
    
    { name= "Congo";
      borders = list of ["South Africa","West Africa","Ethiopia"];
      continent="Africa"
    },
    { name= "Egypt";
      borders = list of ["Southern Europe","West Africa","Ethiopia","Middle East"];
      continent="Africa"
    },
    { name= "Ethiopia";
      borders = list of ["Congo","Egypt","Madagascar","South Africa","West Africa","Middle East"];
      continent="Africa"
    },
    { name= "Madagascar";
      borders = list of ["South Africa","Ethiopia"];
      continent="Africa"
    },
    { name= "South Africa";
      borders = list of ["Congo","Madagascar","Ethiopia"];
      continent="Africa"
    },
    { name= "West Africa";
      borders = list of ["Southern Europe","Western Europe","Congo","Egypt","Ethiopia","Brazil"];
      continent="Africa"
    },
    
    { name= "Baikal";
      borders = list of ["Kamchatka","Mongolia","Siberia","Yukatia"];
      continent="Asia"
    },
    { name= "China";
      borders = list of ["India","Kazakstan","Mongolia","Siberia","Thailand","Ural"];
      continent="Asia"
    },
    { name= "India";
      borders = list of ["China","Kazakstan","Middle East","Thailand"];
      continent="Asia"
    },
    { name= "Japan";
      borders = list of ["Kamchatka","Mongolia"];
      continent="Asia"
    },
    { name= "Kamchatka";
      borders = list of ["Alaska","Baikal","Japan","Mongolia","Yukatia"];
      continent="Asia"
    },
    { name= "Kazakstan";
      borders = list of ["Russia","China","India","Middle East","Ural"];
      continent="Asia"
    },
    { name= "Middle East";
      borders = list of ["Russia","Southern Europe","Egypt","Ethiopia","India","Kazakstan"];
      continent="Asia"
    },
    { name= "Mongolia";
      borders = list of ["Kamchatka","Baikal","China","Japan","Siberia"];
      continent="Asia"
    },
    { name= "Siberia";
      borders = list of ["Baikal","China","Mongolia","Ural","Yukatia"];
      continent="Asia"
    },
    { name= "Thailand";
      borders = list of ["China","India","Indonesia"];
      continent="Asia"
    },
    { name= "Ural";
      borders = list of ["Russia","China","Kazakstan","Siberia"];
      continent="Asia"
    },
    { name= "Yukatia";
      borders = list of ["Kamchatka","Baikal","Siberia"];
      continent="Asia"
    }
   ];
   
   
   prc main() do {
     for {name=Cntry; borders=B} in countries do{
       for C in B do{
         if {name=C;borders=BC} in countries and not Cntry in BC then
           logMsg(info,"$Cntry missing from borders of $C");
       }
     }
     logMsg(info,"$countries");
     logMsg(info,"End of test");
   }
 }