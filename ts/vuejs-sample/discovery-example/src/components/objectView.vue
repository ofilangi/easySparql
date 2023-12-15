<script setup lang="ts">
import { defineAsyncComponent } from 'vue';
import Loading from './Loading.vue'
import { SWDiscoveryConfiguration , SWDiscovery, URI, QueryVariable } from "@p2m2/discovery";

const subjectUri = "CHEBI:106246" ;

const config = SWDiscoveryConfiguration
                    .init()
                    .sparqlEndpoint("https://forum.semantic-metabolomics.fr/sparql/");

const discoveryBase = SWDiscovery(config)
            .prefix("CHEBI","http://purl.obolibrary.org/obo/CHEBI_")
            .something("chebiId")
            .set(URI(subjectUri)) ;

const results_datatypejson = await discoveryBase.finder.datatypeProperties() ;

const results = results_datatypejson.map(
                 (obj : Object) => {
                    let o = Object.values(obj)
                    let property = o[0];
                    return property
                });

const results_objectjson = await discoveryBase.finder.objectProperties() ;

const AsyncComponent = defineAsyncComponent({
  loader: () => import('./Property.vue'),
  loadingComponent: Loading /* shows while loading */,
  //errorComponent: ErrorComponent /* shows if there's an error */,
  delay: 1000 /* delay in ms before showing loading component */,
  timeout: 3000 /* timeout after this many ms */,
});             

</script>
<template>
    <h2>{{ subjectUri }}</h2>
    <ul id="property-uri" v-for="property in results">
        <li >
            <suspense>
                <template #default>
                    <AsyncComponent :discovery=discoveryBase.getSerializedString() :propertyUri = property />
                </template>
                <template #fallback>
                    <Loading />
                </template>
            </suspense>
           
        </li>
</ul>
</template>