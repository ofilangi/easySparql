## Library generation

```
sbt discoveryJS/fullOptJS
sbt discoveryJVM/package
sbt publishLocal 
```

### Library generation html/nodejs

```bash
./update_cdn_libjs.sh
```

## test

```
sbt discoveryJVM/test  
```

## coverage

```
sbt clean coverage discoveryJVM/test discoveryJVM/coverageReport
chromium ./jvm/target/scala-2.13/scoverage-report/index.html
```

### dependencies

```bash
npm install axios --save-dev
npm install qs --save-dev
npm install browserify
```

### memo

```bash

sbt compile
sbt discoveryJS/test
sbt discoveryJVM/test
sbt discoveryJS/fastOptJS 
sbt discoveryJS/fullOptJS

export NODE_OPTIONS=--openssl-legacy-provider
sbt discoveryJS/fastOptJS/webpack
sbt discoveryJS/fullOptJS/webpack


sbt discoveryJVM/testOnly inrae.semantic_web.QueryPlannerTest
```

#### d.ts generation test

```bash
npx -p typescript tsc ./js/target/scala-2.13/scalajs-bundler/main/discovery-opt.js --declaration --allowJs --emitDeclarationOnly --outDir types
```

## local publication -> .ivy2

```bash
sbt publishLocal
``` 

## oss.sonatype maven central repository publication
https://oss.sonatype.org/

```bash 
sbt publish
```

### NPM publication

```bash
sbt discoveryJS/fullOptJS/webpack
sbt npmPackageJson => genere package.json
npm view @p2m2/discovery version -> list published version
npm unpublish @p2m2/discovery@X.X.X -> unpublished lib
npm publish --access public
```

### generate-changelog / Usage

```
type(category): description [flags]
Where type is one of the following:
```
  * breaking
  * build
  * ci
  * chore
  * docs
  * feat
  * fix
  * other
  * perf
  * refactor
  * revert
  * style
  * test

### FORUM


```html 
<script type="text/javascript" src="/media/olivier/hdd-local/workspace/INRAE/P2M2/DISCOVERY/discovery/dist/discovery-web-dev.js"> </script> 
<script>
      var config = SWDiscoveryConfiguration
                    .proxy("http://localhost:8082")
                    .sparqlEndpoint("https://forum.semantic-metabolomics.fr/sparql/")
                    .sparqlEndpoint("https://query.wikidata.org/");

      SWDiscovery(config)
          .prefix("cito","http://purl.org/spar/cito/")
          .prefix("compound","http://rdf.ncbi.nlm.nih.gov/pubchem/compound/")
          .prefix("rdfs","http://www.w3.org/2000/01/rdf-schema#")
          .something("compound")
                 .set("compound:CID60823")
                  .isSubjectOf(URI("skos:closeMatch"),"supp")
                      .isSubjectOf(URI("http://www.wikidata.org/prop/P2175"),"medical_condition_treated")
                  
          .select("compound","supp","medical_condition_treated")
             .commit()
             .raw()
             .then((response) => {
		  console.log(JSON.stringify(response))
                  for (let i=0;i<response.results.bindings.length;i++) {
                    let study=response.results.bindings[i]["study"].value;
                   // let label=response.results.datatypes["label"][study][0].value; 
                     let label="*";
                     console.log(study+"-->"+label);
                  }
            }).catch( (error) => {
              console.error(" -- catch exception --")
              console.error(error)
            } );
 </script>
 ```
