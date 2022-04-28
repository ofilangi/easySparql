import { SWDiscoveryConfiguration , SWDiscovery } from '../../js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt'

describe('SWDiscovery', () => {
    
          
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

  beforeEach(() => {});

  afterEach(() => {});
  
  afterAll(() => {});
  
  test('progression', async () => {
    const transaction = SWDiscovery(localConf).something("hello").isObjectOf("http://test").select("hello")
    
    transaction.progression( (percent : Number) => {
        console.log("percent:"+percent)
    })

    const results = await transaction.commit().raw()
    expect(results.head.vars).toStrictEqual(["hello"])
  })

  test('progression', async () => {
    const transaction = SWDiscovery(localConf).something("hello").isObjectOf("http://test").select("hello")
    
    transaction.requestEvent( (event : string) => {
        console.log("event:"+event)
    })

    const results = await transaction.commit().raw()
    expect(results.head.vars).toStrictEqual(["hello"])
  })
    
});