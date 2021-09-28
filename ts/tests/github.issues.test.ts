import { SWDiscoveryConfiguration , SWDiscovery } from '../../js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt'

describe('-- GITHUB ISSUES -- ', () => {
          
  const json : string  =  `{
         "sources" : [{
              "id"  : "local_endpoint",
              "url" : "http://localhost:8890/sparql"
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
    
});