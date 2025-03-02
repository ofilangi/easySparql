import { SWDiscoveryConfiguration , SWDiscovery } from "@p2m2/discovery";

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
  }}`;

  const json2 : string  =  `{
           "sources" : [{
                "id"  : "local_endpoint2",
                "path" : "http://somethingelse:8890/sparql",
                "mimetype" : "application/sparql-query"
             }]}`;
           
  const localConf = SWDiscoveryConfiguration.setConfigString(json);

  const localConf2 = SWDiscoveryConfiguration.setConfigString(json2);

  beforeEach(() => {});

  afterEach(() => {});
  
  afterAll(() => {});
  
  test("something", async () => {
      const results = await SWDiscovery(localConf).something("h1").select("h1").commit().raw();
      expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("selectByPage", async () => {
    const args = await SWDiscovery(localConf).something("h1").selectByPage("h1")

    let numberOfPages : Number = Object.values(args)[0] as Number ;
    let lazyPage : Array<any> = Object.values(args)[1] as Array<any> ;

	const results = await lazyPage[0].commit().raw()
    expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("selectByPage", async () => {
    const args =
      await SWDiscovery(localConf)
      .something("h1").selectByPage("h1");

    let numberOfPages : Number = Object.values(args)[0] as Number ;
    let lazyPage : Array<any> = Object.values(args)[1] as Array<any> ;
  })

  test("select *", async () => {
    const results = await SWDiscovery(localConf).something("h1").select("*").commit().raw();
    expect(results.head.vars).toContain("h1");
  })

  test("getSerializedString/setSerializedString", async () => {
    const s : string = SWDiscovery(localConf).something("h1").getSerializedString();
    const results = await SWDiscovery().setSerializedString(s).select("h1").commit().raw();
    expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("browse", () => {
    const results : string[] = SWDiscovery(localConf)
                      .something("h1")
                       .isObjectOf("http://test11")
                         .browse( ( n: any, p : Number) : string => {
                          return n.$type + " : " + p;
                         });
    expect(results).toStrictEqual([
      "fr.inrae.metabohub.semantic_web.node.Root : 0",
      "fr.inrae.metabohub.semantic_web.node.Something : 1",
      "fr.inrae.metabohub.semantic_web.node.ObjectOf : 2" ]);
    })

     test("setDecoration/getDecoration", () => {
        const results = SWDiscovery(localConf)
                          .something("h1")
                           .isObjectOf("http://test11")
                           .setDecoration("k1","v1")
                             .browse( ( n: any, p : Number) => {
                                if (n.decorations) return n.decorations["k1"];
                             });
        expect(results).toStrictEqual([
          undefined,
          undefined,
          "v1" ]);
          expect(SWDiscovery(localConf)
                          .something("h1")
                            .isObjectOf("http://test11")
                             .setDecoration("k1","v1")
                             .getDecoration("k1")).toStrictEqual("v1");
        })

       test("setConfig/getConfig", () => {
            let conf = SWDiscovery(localConf)
                                   .something("h1")
                                    .setConfig(localConf2)
                                     .isObjectOf("http://test11")
                                       .getConfig();
            for(let k in conf) {
                expect(k).not.toBeNull();
            }


       })
});