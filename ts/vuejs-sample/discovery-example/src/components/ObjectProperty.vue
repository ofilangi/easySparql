<script setup lang="ts">
import { SWDiscovery, URI } from "@p2m2/discovery";

const props = defineProps<{
discovery : String, 
propertyUri: string
}>()

const discoveryBase = SWDiscovery().setSerializedString(props.discovery)
let json = await discoveryBase.isSubjectOf(URI(props.propertyUri),"value")
                   //     .console()
                        .select("value")
                        .commit()
                        .raw();

let value =  json.results.bindings[0].value.value
//let datatype =  json.results.bindings.value[0].datatype

</script>


<template>
    <div class="p-5">
     
        {{ propertyUri.split("/").pop().split("#").pop() }} 
     
     &rarr; 
     
     <router-link to="{ name: 'subject', params: { subject: 123 , endpoint :'' }}">
        {{ value.split("/").pop().split("#").pop() }}
    </router-link>
    </div>
</template>