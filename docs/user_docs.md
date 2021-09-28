# Philosophy

discovery allows you to build and execute a request to an RDF resource by simply defining 
a configuration, and building blocks sequence of atomic element .

```html 
/* ---------------------------------------*/
/* Import javascript library the          */
/* ---------------------------------------*/
<script 
        type="text/javascript" 
        src="https://cdn.jsdelivr.net/gh/p2m2/discovery@0.2.0/dist/discovery-web.min.js"> 
</script>

/* ---------------------------------------*/
/* Request your favorite RDF resource     */
/* ---------------------------------------*/

<script>
/* -------------------------------*/
/* Part 1. Setting Configuration  */
/* -------------------------------*/
let config = SWDiscoveryConfiguration.setConfigString(`
          {
          "sources" : [{
          "id"  : "dbpedia",
          "url" : "https://dbpedia.org/sparql"
           }]}
          `) ;
          
/* --------------------------------------------------*/
/* Part 2. Building Request  "Something is an Album" */
/* --------------------------------------------------*/
 var req = SWDiscovery(config)
             .something("some1")
               .isA("http://dbpedia.org/ontology/Album")

/* -----------------------------------------------*/
/* Part 3. Executing request to get list of Album */
/* -----------------------------------------------*/
   req.select("some1")
               .commit()
               .raw().then((response) => {
                  for (let i=0;i<response.results.bindings.length;i++) {
                    console.log(response.results.bindings[i]["some1"].value);
                  }
               })
/* ------------------------------------------------------------------------------------*/
/* Part 3. Debugging or Incrementing request thanks to the discovery console proposals */
/* ------------------------------------------------------------------------------------*/
   
   req
     .console() // display information on the console
     .helper("Tribute") // display information on the web page and propose new building block.
               
</script>
```

To use discovery, user have some basics of RDF graph databases and SPARQL query language.

- [Configuration](user_docs_configuration.md)
- [Building query](user_docs_building_block.md)
- [Executing query](user_docs_transaction.md)
- [Debugging query](user_docs_debug.md)
- [API documentation](./api/index.html)

