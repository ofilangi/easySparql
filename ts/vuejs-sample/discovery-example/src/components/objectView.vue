<script setup lang="ts">
import { defineAsyncComponent } from 'vue';
import Loading from './Loading.vue'
import { SWDiscoveryConfiguration , SWDiscovery, URI, QueryVariable } from "@p2m2/discovery";

const props = defineProps<{
    sparqlEndpoint : String,
    subjectUri : String
}>()

const config = SWDiscoveryConfiguration
                    .init()
                    .sparqlEndpoint(props.sparqlEndpoint);

const discoveryBase = SWDiscovery(config)
            .prefix("CHEBI","http://purl.obolibrary.org/obo/CHEBI_")
            .something("chebiId")
            .set(URI(props.subjectUri)) ;

const results_datatypejson = await discoveryBase.finder.datatypeProperties() ;

const results_dp = results_datatypejson.map(
                 (obj : Object) => {
                    let o = Object.values(obj)
                    let property = o[0];
                    return property
                });

const results_objectjson = await discoveryBase.finder.objectProperties() ;

const results_op = results_objectjson.map(
                 (obj : Object) => {
                    let o = Object.values(obj)
                    let property = o[0];
                    return property
                });

const AsyncDatatypePropertyComponent = defineAsyncComponent({
  loader: () => import('./DatatypeProperty.vue'),
  loadingComponent: Loading /* shows while loading */,
  //errorComponent: ErrorComponent /* shows if there's an error */,
  delay: 1000 /* delay in ms before showing loading component */,
  timeout: 3000 /* timeout after this many ms */,
});             

const AsyncObjectPropertyComponent = defineAsyncComponent({
  loader: () => import('./ObjectProperty.vue'),
  loadingComponent: Loading /* shows while loading */,
  //errorComponent: ErrorComponent /* shows if there's an error */,
  delay: 1000 /* delay in ms before showing loading component */,
  timeout: 3000 /* timeout after this many ms */,
});        

</script>
<template>
    <h2>{{ props.subjectUri }}</h2>
        <small>
        <div v-for="property in results_op">
        <suspense>
                    <template #default>
                        <AsyncObjectPropertyComponent :discovery=discoveryBase.getSerializedString() :propertyUri = property />
                    </template>
        </suspense>           
        </div>
    </small>

    <h3>Datatype Properties</h3>


    <ul id="property-uri" v-for="property in results_dp">
        <li >
            <suspense>
                <template #default>
                    <AsyncDatatypePropertyComponent :discovery=discoveryBase.getSerializedString() :propertyUri = property />
                </template>
                <template #fallback>
                    <Loading />
                </template>
            </suspense>
           
        </li>
</ul>
</template>