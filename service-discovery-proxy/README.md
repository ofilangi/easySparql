# service-discovery-proxy

## test

mill app.test

## assembly

mill app.assembly

java -jar ./out/app/assembly.dest/out.jar

## run proxy 

mill -w app.runBackground

## mill app --help

```mill app --help```
```
Usage: SWDiscoveryProxy [options]

  --port <port>  listening port. default [8080].
  --host <host>  hostname. default [localhost].
  --verbose      verbose flag.
  --background   background flag.
  --help         prints this usage text
some notes.

```

## test

http://localhost:8082/get?transaction=%7B%22sw%22%3A%7B%22config%22%3A%7B%22sources%22%3A%5B%7B%22id%22%3A%22https%3A%2F%2Fmetabolights.semantic-metabolomics.fr%2Fsparql%22%2C%22path%22%3A%22https%3A%2F%2Fmetabolights.semantic-metabolomics.fr%2Fsparql%22%2C%22mimetype%22%3A%22application%2Fsparql-query%22%2C%22method%22%3A%22POST%22%7D%5D%7D%2C%22rootNode%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.Root%22%2C%22idRef%22%3A%227e2ca433-9a72-4184-bbfe-763d869692a0%22%2C%22prefixes%22%3A%7B%22rdf%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.IRI%22%2C%22iri%22%3A%22http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%22%7D%2C%22owl%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.IRI%22%2C%22iri%22%3A%22http%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%22%7D%2C%22rdfs%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.IRI%22%2C%22iri%22%3A%22http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%22%7D%2C%22xsd%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.IRI%22%2C%22iri%22%3A%22http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%22%7D%2C%22metabolights%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.IRI%22%2C%22iri%22%3A%22https%3A%2F%2Fwww.ebi.ac.uk%2Fmetabolights%2Fproperty%23%22%7D%2C%22obo%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.IRI%22%2C%22iri%22%3A%22http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2F%22%7D%7D%2C%22lDatatypeNode%22%3A%5B%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.DatatypeNode%22%2C%22refNode%22%3A%22study%22%2C%22property%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.SubjectOf%22%2C%22idRef%22%3A%22label%22%2C%22term%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.URI%22%2C%22localNameUser%22%3A%22rdfs%3Alabel%22%7D%7D%2C%22idRef%22%3A%22label%22%7D%5D%2C%22lSolutionSequenceModifierNode%22%3A%5B%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.Limit%22%2C%22value%22%3A0%2C%22idRef%22%3A%221b4f68ac-386a-47f4-b829-b0d899e7bf09%22%7D%2C%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.Offset%22%2C%22value%22%3A0%2C%22idRef%22%3A%2226530d86-b652-4670-ae9e-5c6359436ff6%22%7D%2C%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.Projection%22%2C%22variables%22%3A%5B%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.QueryVariable%22%2C%22name%22%3A%22study%22%7D%2C%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.QueryVariable%22%2C%22name%22%3A%22label%22%7D%5D%2C%22idRef%22%3A%229fc46952-cdea-4bdf-884b-b741badc04eb%22%7D%5D%2C%22children%22%3A%5B%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.Something%22%2C%22idRef%22%3A%22something0%22%2C%22children%22%3A%5B%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.Value%22%2C%22term%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.URI%22%2C%22localNameUser%22%3A%22obo%3ACHEBI_4167%22%7D%2C%22idRef%22%3A%2226ecd278-0094-41d9-9bcb-c9cdbe38b256%22%7D%2C%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.node.ObjectOf%22%2C%22idRef%22%3A%22study%22%2C%22term%22%3A%7B%22%24type%22%3A%22fr.inrae.metabohub.semantic_web.rdf.URI%22%2C%22localNameUser%22%3A%22metabolights%3AXref%22%7D%7D%5D%7D%5D%7D%2C%22fn%22%3A%229fc46952-cdea-4bdf-884b-b741badc04eb%22%7D%7D
https://www.url-encode-decode.com/