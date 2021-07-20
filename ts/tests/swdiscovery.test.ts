import { SWDiscoveryConfiguration , SWDiscovery } from '../../js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt'

describe('SWDiscovery', () => {
    
          
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

  beforeEach(() => {});

  afterEach(() => {});
  
  afterAll(() => {});
  
  test("something", async () => {
      const results = await SWDiscovery(localConf).something("h1").select("h1").commit().raw();
      expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("selectByPage", async () => {
    const args = 
    await SWDiscovery(localConf)
    .something("h1").selectByPage("h1")
    

    let numberOfPages : Number = Object.values(args)[0] as Number ;
    let lazyPage : Array<any> = Object.values(args)[1] as Array<any> ;

		console.log("number of pages:"+numberOfPages)
		console.log(" -- deuxieme page -- ")

		const results = await lazyPage[0].commit().raw()
      
		console.log(JSON.stringify(results,null,2));

    expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("selectByPage", async () => {
    const args =
      await SWDiscovery(localConf)
      .something("h1").selectByPage(["h1"])

    let numberOfPages : Number = Object.values(args)[0] as Number ;
    let lazyPage : Array<any> = Object.values(args)[1] as Array<any> ;
  })

  test("select *", async () => {
    const results = await SWDiscovery(localConf).something("h1").select("*").commit().raw();
    expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("getSerializedString/setSerializedString", async () => {
    const s : string = SWDiscovery(localConf).something("h1").getSerializedString();
    const results = await SWDiscovery().setSerializedString(s).select("h1").commit().raw();
    expect(results.head.vars).toStrictEqual(["h1"]);
  })


  test("browse", () => {
    const results = SWDiscovery(localConf)
                      .something("h1")
                       .isObjectOf("http://test11")
                         .browse( ( n: any, p : Number) => {
                          return n.$type + " : " + p;
                         });
    expect(results).toStrictEqual([
      "inrae.semantic_web.node.Root : 0",
      "inrae.semantic_web.node.Something : 1",
      "inrae.semantic_web.node.ObjectOf : 2"
    ]);
})
    
});