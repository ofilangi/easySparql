import { SWDiscoveryConfiguration , SWDiscovery } from "@p2m2/discovery"

describe("SWDiscovery", () => {
    
          
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
  
  test("progression", async () => {
    const transaction = SWDiscovery(localConf).something("hello").isObjectOf("http://test").select("hello")
    
    transaction.progression( (percent : Number) => {
    expect(percent).not.toBeNull();
    })

    const results = await transaction.commit().raw()
    expect(results.head.vars).toStrictEqual(["hello"])
  })

  test("progression", async () => {
    const transaction = SWDiscovery(localConf).something("hello").isObjectOf("http://test").select("hello")
    
    transaction.requestEvent( (event : string) => {
        expect(event).not.toBeNull();
    })

    const results = await transaction.commit().raw()
    expect(results.head.vars).toStrictEqual(["hello"])
  })
    
});