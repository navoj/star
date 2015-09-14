complexorder is package {
  
  type EventItem is event{
    createdTimestamp has type long;
  };
  
--  AA is actor{
    LocalizedActualEventItems has type list of {containerId has type string;
      event has type EventItem;
      locale has type string;
    };
    
    def LocalizedActualEventItems is list of [
      { containerId = "first"; event=event{createdTimestamp=0L}; locale="somewhere"},
      { containerId = "second"; event=event{createdTimestamp=1L}; locale="nowhere"}
    ];
--  };
    
  prc main() do {
   def  QQ is -- query AA's LocalizedActualEventItems with
      list of { all x.event where x in LocalizedActualEventItems order by x.event.createdTimestamp};
      
    logMsg(info,"$QQ");
  }
}