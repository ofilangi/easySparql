import { SWDiscoveryConfiguration , SWDiscovery, URI } from '../../js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt'

describe('-- GITHUB ISSUES -- ', () => {
          
  const json : string  =  `{
         "sources" : [{
              "id"  : "local_endpoint",
              "path" : "http://localhost:8890/sparql",
              "mimetype" : "application/sparql-query"
           }],
           "settings" : {
             "cache" : true,
             "logLevel" : "info",
             "sizeBatchProcessing" : 10,
             "pageSize" : 10
  }}` 
           
  const localConf = SWDiscoveryConfiguration.setConfigString(json)
  
  test('#101', async () => {
    const str : string = SWDiscovery(localConf).something("hello").getSerializedString()
    const t = SWDiscovery(localConf).setSerializedString(str)
  })

  test('#144', async () => {
      const conf = SWDiscoveryConfiguration.setConfigString(`{
               "sources" : [{
                    "id"  : "triplydb",
                    "path" : "https://api.triplydb.com/datasets/gr/gr/services/gr/sparql",
                    "mimetype" : "application/sparql-query"
                 }],
                 "settings" : {
                   "cache" : true,
                   "logLevel" : "info",
                   "sizeBatchProcessing" : 10,
                   "pageSize" : 10
        }}`)

      const results : string =
          await SWDiscovery(conf)
            .something("hello")
            .isSubjectOf(URI("a"),"type")
              .filter.contains("Business")
             .console()
         /*    .select("type")
             .commit()
             .raw();

      console.log("results:"+JSON.stringify(results))*/
    })
});