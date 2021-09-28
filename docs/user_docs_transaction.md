# Executing query

## methods

- select(`[var1,var2,var3,..]`,`limit`,`offset`) : get results as a promise (Promise[SWTransaction])
- selectByPage(`var1,var2,var3,..`) : get results as a promise (count,Array[Promise[SWTransaction]])

### patterns getting results

```html
    SWDiscovery(config)
        .something("some")
        ...
        .select( "some" )
           .commit()
           .raw()
             .then((response) => {
                  for (let i=0;i<response.results.bindings.length;i++) {
                    ....
                  }
               }); // end promise select
```

```html
    SWDiscovery(config)
        .something("some")
        ...
        .selectByPage( "some" )
          .then( (args) => {
             let numberOfPages = Object.values(args)[0] ;
             let lazyPages = Object.values(args)[1] ;
            
             if ( numberOfPages>=1 )
                 lazyPages[0]
                   .commit()
                   .raw()
                     .then((response) => {
                          for (let i=0;i<response.results.bindings.length;i++) {
                            ....
                          }
                       }); // end promise lazy page 1
         }); // end promise selectByPage
```

### unit block results

```html
.select( "some" )
           .commit()
           .raw()
             .then((response) => {
                  for (let i=0;i<response.results.bindings.length;i++) {
                        console.log(response.results.bindings[i]["some"].value) ;
                  }
               }); // end promise select
```

### datatype results

```html
.datatype("datatypeSome")
.select( "some" )
           .commit()
           .raw()
             .then((response) => {
                  for (let i=0;i<response.results.bindings.length;i++) {
                        let uri = response.results.bindings[i]["some"].value ;
                        /* none or several results could be retrieve */
                        for (let i=0;i<esponse.results.datatypes["datatypeSome"][uri].length;i++) {
                            console.log(response.results.datatypes["datatypeSome"][uri][0].value);
                        }
                  }
               }); // end promise select
```

### transport results

- getSerializedString
- setSerializedString