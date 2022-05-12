# Discovery

[![p2m2](https://circleci.com/gh/p2m2/discovery.svg?style=shield)](https://app.circleci.com/pipelines/github/p2m2)
[![codecov](https://codecov.io/gh/p2m2/discovery/branch/develop/graph/badge.svg)](https://codecov.io/gh/p2m2/discovery)
[![CodeFactor](https://www.codefactor.io/repository/github/p2m2/discovery/badge)](https://www.codefactor.io/repository/github/p2m2/discovery)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8d8ecb66f9ff4963a22efab3c693b629)](https://www.codacy.com/gh/p2m2/discovery/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=p2m2/discovery&amp;utm_campaign=Badge_Grade)
[![javadoc](https://javadoc.io/badge2/com.github.p2m2/discovery_2.13/javadoc.svg)](https://javadoc.io/doc/com.github.p2m2/discovery_2.13)
[![Npm package version](https://shields.io/npm/v/@p2m2/discovery)](https://www.npmjs.com/package/@p2m2/discovery)
[![Docker Image Version (tag latest semver)](https://img.shields.io/docker/automated/inraep2m2/service-discovery-proxy)](https://hub.docker.com/repository/docker/inraep2m2/service-discovery-proxy)
![Maven Central](https://img.shields.io/maven-central/v/com.github.p2m2/discovery_2.13)

## What is discovery

discovery is a software library which aims to ease the development of decision support tools
exploiting [omics](https://en.wikipedia.org/wiki/Multiomics) RDF databases.
The library offers a dedicated query language that can be used in several runtime environments (Browser/JS, Node/JS, JVM/Scala).

discovery is developed as part of the work package "Creating FAIR e-resources for knowledge mining" for [the
national infrastructure for metabolomics and fluxomics - MetaboHUB](https://www.metabohub.fr/home.html)

further information and documentation, visit https://p2m2.github.io/discovery/

### Html/Js example

#### Html import 

```html 
<script type="text/javascript" src="https://cdn.jsdelivr.net/gh/p2m2/discovery@develop/dist/discovery-web.min.js"> </script> 
<script>
      var config = SWDiscoveryConfiguration
                    .init()
                    .sparqlEndpoint("https://metabolights.semantic-metabolomics.fr/sparql");

      SWDiscovery(config)
          .prefix("obo","http://purl.obolibrary.org/obo/")
          .prefix("metabolights","https://www.ebi.ac.uk/metabolights/property#")
          .prefix("rdfs","http://www.w3.org/2000/01/rdf-schema#")
          .something()
            .set(URI("obo:CHEBI_4167"))
              .isObjectOf(URI("metabolights:Xref"),"study")
                .datatype(URI("rdfs:label"),"label")
          .select("study","label")
             .commit()
             .raw()
             .then((response) => {
                  for (let i=0;i<response.results.bindings.length;i++) {
                    let study=response.results.bindings[i]["study"].value;
                    let label=response.results.datatypes["label"][study][0].value; 
                    console.log(study+"-->"+label);
                  }
            }).catch( (error) => {
              console.error(" -- catch exception --")
              console.error(error)
            } );
 </script>
 ```

[js fiddle example](https://jsfiddle.net/xv3d4Lte/1/)


## Import discovery with SBT

```sbt
libraryDependencies += "com.github.p2m2" %%% "discovery" % "0.4.0"
```

## Running docker proxy image

### docker command

```bash
docker run -d --network host -t service-discovery-proxy:latest
```

### docker-compose file

```yaml
version: '3.9'
services:
  service-discovery-proxy:
    image: inraep2m2/service-discovery-proxy:latest
    command: ./mill -w app.runBackground --port 8085 --verbose
    network_mode: "host"
    restart: on-failure
```

