[![GitHub license](https://img.shields.io/github/license/p2m2/discovery.svg)](https://github.com/p2m2/discovery/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/p2m2/discovery.svg)](https://github.com/p2m2/discovery/releases/)

# What is discovery

discovery is a software library which aims to ease the development of decision support tools
exploiting RDF databases.
The library offers a dedicated query language that can be used in several runtime environments (Browser/JS, Node/JS, JVM/Scala).

discovery is developed as part of the work package "Creating FAIR e-resources for knowledge mining" for [the 
national infrastructure for metabolomics and fluxomics - MetaboHUB](https://www.metabohub.fr/home.html) 



## Metabolomic example

This example shows how to retrieve "Metabolights Studies" 
linking to the CHEBI:4167-D-glucopyranose compound. This example uses the following SPARQL endpoint https://metabolights.semantic-metabolomics.fr/sparql


```html
<script 
        type="text/javascript" 
        src="https://cdn.jsdelivr.net/gh/p2m2/discovery@0.2.0/dist/discovery-web.min.js"> 
</script>
<script>
      var config = SWDiscoveryConfiguration.setConfigString(`
          {
          "sources" : [{
          "id"  : "metabolights",
          "url" : "https://metabolights.semantic-metabolomics.fr/sparql"
           }]}
          `);

      SWDiscovery(config)
          .something()
            .set(URI("http://purl.obolibrary.org/obo/CHEBI_4167"))
              .isObjectOf(URI("https://www.ebi.ac.uk/metabolights/property#Xref"),"study")
                .datatype(URI("http://www.w3.org/2000/01/rdf-schema#label"),"label")
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

[js fiddle example](https://jsfiddle.net/uoecqath/5/)


# Documentation

- [User documentation](user_docs.md)

## Tutorials / Template

- [discovery-tutorial-html-js](https://github.com/p2m2/discovery-tutorial-html-js/tree/0.2.0)
- [discovery-tutorial-nodejs](https://github.com/p2m2/discovery-tutorial-nodejs/tree/0.2.0)
- [scalajs project template](https://github.com/p2m2/discovery-scalajs-template)
- [scala project template](https://github.com/p2m2/discovery-scala-template)

## Metabolomics Applications 

- [Example using the FORUM DiseasesChem endpoint with discovery](user_docs_forum_example.md)
- Full example with [table view implementation](https://github.com/p2m2/discovery-table-view) using [scalatags](https://github.com/lihaoyi/scalatags)
- [ChEBI-discovery](https://github.com/eMetaboHUB/ChEBI-discovery). ScalaJs implementation of *"Improving lipid mapping in Genome Scale Metabolic Networks using ontologies."* [1].
  This methodology uses the Chemical Entities of Biological Interest (ChEBI)

## Dependencies - RDF Software libraries 

this software development is based on [RDFJS (RDF JavaScript Libraries)](https://rdf.js.org/) and [RDF4J](https://rdf4j.org/)

### JavaScript library facades to develop ScalaJS app

- [N3.js](https://github.com/p2m2/N3.js-facade) - [source](https://github.com/rdfjs/N3.js/) 
- [data-model-spec](https://github.com/p2m2/data-model-rdfjs) - [source](https://github.com/rdfjs/data-model-spec) 
- [RDF/XML Streaming Parser](https://github.com/p2m2/rdfxml-streaming-parser-facade) - [source](https://github.com/rdfjs/rdfxml-streaming-parser.js)
- [Comunica SPARQL RDFJS Init Actor](https://github.com/p2m2/comunica-actor-init-sparql-rdfjs-facade) [source](https://github.com/comunica/comunica/tree/master/packages/actor-init-sparql-rdfjs)

## Contact/Issues

Add [GitHub issue](https://github.com/p2m2/discovery/issues/new) to request bugs, new features or suggest modifications to existing features. 

## Licence
The software is licensed under MIT and under development.

## Authors

- O. Filangi -  IGEPP's Metabolic Profiling and Metabolomic Platform (P2M2, Rennes)
- F. Giacomoni, N. Paulhe - The Platform "Exploration du Métabolisme" (PFEM, Clermont-Ferrand)
   
## References

[1] Poupin, N., Vinson, F., Moreau, A. et al. Improving lipid mapping in Genome Scale Metabolic Networks using ontologies. Metabolomics 16, 44 (2020). https://doi.org/10.1007/s11306-020-01663-5