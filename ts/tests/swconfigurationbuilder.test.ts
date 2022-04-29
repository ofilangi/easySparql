import { SWDiscoveryConfiguration , SWDiscovery } from "../../js/target/scala-2.13/scalajs-bundler/main/discovery-fastopt";

describe("SWDiscovery", () => {

  const config = SWDiscoveryConfiguration
                 .init()
                 .sparqlEndpoint("http://localhost:8890/sparql")
                 .setPageSize(5)
                 .setSizeBatchProcessing(10)
                 .setLogLevel("debug")
                 .setCache(false);

  var turtleContent = `@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                                            @prefix ns0: <http://www.some-ficticious-zoo.com/rdf#> .

                                            ns0:lion ns0:name "Lion" ;
                                                     ns0:species "Panthera leo" ;
                                                     ns0:class "Mammal" .
                                            ns0:tarantula
                                                ns0:name "Tarantula" ;
                                                ns0:species "Avicularia avicularia" ;
                                                ns0:class "Arachnid" .

                                            ns0:hippopotamus
                                                ns0:name "Hippopotamus" ;
                                                ns0:species "Hippopotamus amphibius" ;
                                                ns0:class "Mammal" .`;

  const configTurtleContent = SWDiscoveryConfiguration
                 .init()
                 .rdfContent(turtleContent)
                 .setPageSize(5)
                 .setSizeBatchProcessing(10)
                 .setLogLevel("debug")
                 .setCache(false);

  beforeEach(() => {});

  afterEach(() => {});

  afterAll(() => {});

  test("accessors", async () => {
    expect(config).not.toBeNull();
    expect(config.sourcesSize).not.toBeNull();
    expect(config.pageSize).not.toBeNull();
    expect(config.sizeBatchProcessing).not.toBeNull();
    expect(config.logLevel).not.toBeNull();
    expect(config.cache).not.toBeNull();
  })

  test("something", async () => {
      const results = await SWDiscovery(config).something("h1").select("h1").commit().raw();
      expect(results.head.vars).toStrictEqual(["h1"]);
  })

  test("configTurtleContent", async () => {
        const results = await SWDiscovery(configTurtleContent)
                               .prefix("ns0","http://www.some-ficticious-zoo.com/rdf#")
                               .something("h1")
                               .isSubjectOf("ns0:name")
                                .select("h1").commit().raw();

        expect(results.results.bindings.length).toEqual(3)
    })

    /**
  const localFileConfig = SWDiscoveryConfiguration
                   .init()
                   .localFile(__dirname+"/../shared/src/test/resources/animals_basic.ttl")
                   .setPageSize(5)
                   .setSizeBatchProcessing(10)
                   .setLogLevel("debug")
                   .setCache(false)

    test("localFile", async () => {
        const results = await SWDiscovery(localFileConfig)
                               .prefix("ns0","http://www.some-ficticious-zoo.com/rdf#")
                               .something("h1")
                               .isSubjectOf("ns0:name")
                                .select("h1").commit().raw();

        expect(results.results.bindings.length).toEqual(3)

        console.log(__dirname+"******************************************************************")
    }) */
});