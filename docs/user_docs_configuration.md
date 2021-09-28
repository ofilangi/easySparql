# Configuration

The configuration have to be defined in a json format. It should a contains a list of "source" definition and a general settings.

**example**

```json 
{
     "sources" : [{
       "id"  : "dbpedia",
       "url" : "https://dbpedia.org/sparql",
       "mimetype" : "application/sparql-query",
       "method" : "POST"
     }],
     "settings" : {
       "cache" : true,
       "logLevel" : "info",
       "sizeBatchProcessing" : 10,
       "pageSize" : 10
     }
}
```

## Sources definition

each source defined un identifier.

- `id`       : `<identifyTheSourceEndpoint>`

Three types of RDF sources can be exploited : SPARQL endpoint, RDF Files, RDF inlining content

### SPARQL endpoint

- `mimetype  = "application/sparql-query"`
- `url`      =  `<URL : String>`

**optional argument**

- `method`    = `"POST" | "POST_ENCODED" | "GET"`
- `auth`            = `"basic" | "digest" | "bearer" | "proxy"`
- `login`           = `<login>`
- `password`        = `<password>`

**example**

source definition to query the DBpedia SPARQL endpoint.

```json
{
   "id"       : "dbpedia",
   "url"      : "https://dbpedia.org/sparql",
   "mimetype" : "application/sparql-query",
   "method"   : "POST"
 }
```

### RDF files

- `mimetype`  = `"text/turtle" | "text/n3" | "text/rdf-xml"`
- `file`   = `<URL file : String>`

**example**

```json
{
   "id"       : "local_content",
   "mimetype" : "text/turtle",
   "file"     : "https://raw.githubusercontent.com/p2m2/discovery/develop/shared/src/test/resources/metabo.ttl"
 } 
```

### RDF inline content

- `mimetype`  = `"text/turtle" | "text/n3" | "text/rdf-xml"`
- `content`   = `<String>`

**example**

```json
{
   "id"       : "local_content",
   "mimetype" : "text/turtle",
   "content"  : "<http://example.org/#subject1> <http://example.org/#predicate1> <http://example.org/#object1> ."
 } 
```

## General settings

**disable/unable cache query results**

- `cache`           = `"true" | "false"`

**control the logging output with the log level**
- `logLevel`        = `"trace" | "debug" | "info" | "warn" | "error" | "all" | "off"`

`"warn"` is the default value.

**customize pool batch processing (see datatype block)**

- `sizeBatchProcessing`  = `<Int>`

150 is the default value.

**customize selectByPage**

- `pageSize`             = `<Int>`

10 is the default value.