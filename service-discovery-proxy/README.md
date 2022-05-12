# service-discovery-proxy

## test

mill app.test

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

http://localhost:8082/get?transaction=%7B%22sw%22%3A%7B%22config%22%3A%7B%22sources%22%3A%5B%7B%22id%22%3A%22http%3A%2F%2Flocalhost%3A8890%2Fsparql%22%2C%22path%22%3A%22http%3A%2F%2Flocalhost%3A8890%2Fsparql%22%2C%22mimetype%22%3A%22application%2Fsparql-query%22%2C%22method%22%3A%22POST%22%7D%5D%7D%2C%22rootNode%22%3A%7B%22%24type%22%3A%22inrae.semantic_web.node.Root%22%2C%22idRef%22%3A%22234a05f7-f97c-496e-b0c0-507e1c5eda61%22%2C%22lSolutionSequenceModifierNode%22%3A%5B%7B%22%24type%22%3A%22inrae.semantic_web.node.Limit%22%2C%22value%22%3A0%2C%22idRef%22%3A%228de102aa-f5f7-4b1f-aaee-8902878b7048%22%7D%2C%7B%22%24type%22%3A%22inrae.semantic_web.node.Offset%22%2C%22value%22%3A0%2C%22idRef%22%3A%22eb9b8a25-d153-4923-91d9-bc83e56feb3d%22%7D%2C%7B%22%24type%22%3A%22inrae.semantic_web.node.Projection%22%2C%22variables%22%3A%5B%7B%22%24type%22%3A%22inrae.semantic_web.rdf.QueryVariable%22%2C%22name%22%3A%22h1%22%7D%5D%2C%22idRef%22%3A%22b19b1927-81b3-41f2-b69f-b4af68a5cab7%22%7D%2C%7B%22%24type%22%3A%22inrae.semantic_web.node.Distinct%22%2C%22idRef%22%3A%22f125f12e-f3ab-4c13-9d68-150ec9f44ff1%22%7D%5D%2C%22children%22%3A%5B%7B%22%24type%22%3A%22inrae.semantic_web.node.Something%22%2C%22idRef%22%3A%22h1%22%2C%22children%22%3A%5B%7B%22%24type%22%3A%22inrae.semantic_web.node.SubjectOf%22%2C%22idRef%22%3A%22object0%22%2C%22term%22%3A%7B%22%24type%22%3A%22inrae.semantic_web.rdf.URI%22%2C%22localNameUser%22%3A%22http%3A%2F%2Fbb%22%7D%7D%5D%7D%5D%7D%2C%22fn%22%3A%22234a05f7-f97c-496e-b0c0-507e1c5eda61%22%7D%7D



https://www.url-encode-decode.com/