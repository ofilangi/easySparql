---
title: 'Discovery: Empowering Access and Reusability of RDF Graphs with a Programming Query Builder'
tags:
  - sematic web
  - RDF
  - linked data
  - metabolomics
  - bioinformatics
  - scala
  - scala.js
authors:
  - name: Adrian M. Price-Whelan
    orcid: 0000-0000-0000-0000
    equal-contrib: true
    affiliation: "1, 2" # (Multiple affiliations must be quoted)
  - name: Author Without ORCID
    equal-contrib: true # (This is how you can denote equal contributions between multiple authors)
    affiliation: 2
  - name: Author with no affiliation
    corresponding: true # (This is how to denote the corresponding author)
    affiliation: 3
  - given-names: Ludwig
    dropping-particle: van
    surname: Beethoven
    affiliation: 3
affiliations:
 - name: Lyman Spitzer, Jr. Fellow, Princeton University, USA
   index: 1
 - name: Institution Name, Country
   index: 2
 - name: Independent Researcher, Country
   index: 3
date: 13 August 2017
bibliography: paper.bib

# Optional fields if submitting to a AAS journal too, see this blog post:
# https://blog.joss.theoj.org/2018/12/a-new-collaboration-with-aas-publishing
aas-doi: 10.3847/xxxxx <- update this with the DOI from AAS once you know it.
aas-journal: Astrophysical Journal <- The name of the AAS journal.
---

# Summary

Linked data is increasingly available on the web and has been widely adopted by the bioinformatics community. 
However, it is not common to find APIs that enable the direct use of semantic information in web interfaces. 
This often leads web application designers to incorporate this information into relational databases, 
as they can benefit from the query builder and object-relational mapping features that are widely used in this community.

We have developed Discovery, a free software library designed to easily build intuitive and interactive user interfaces 
to exploit RDF data in graphical form. The API provides a dedicated query language to create and maintain complex queries 
to be used in a client or server side web development environment. We used Discovery to implement functionality in web decision 
support applications within the MetaboHUB consortium (French national Metabolomics and Fluxomics infrastructure) : 
FORUM[@delmas_forum_2021] (Metabolism Knowledge Network Portal) and PeakForest[@paulhe_peakforest_2022] (The Metabolomics spectral database web portal).

# Statement of need

Nowaday, the use of semantic web technologies into bioinformatics has become ubiquitous across all domains of life sciences[@wu_semantic_2014]. 
Many bioinformatics resources is now organized according to the FAIR (Findable, Accessible, Interoperable, and Reusable) principles[@wilkinson_fair_2016], enabling efficient management and reuse of data in both research and industrial settings[@wu_semantic_2014]. This implementation was made possible by the standardized languages and protocols defined by the World Wide Web Consortium (W3C, https://www.w3.org/) such as the Resource Description Framework (RDF, https://www.w3.org/RDF/) which provides a versatile framework for representing data and knowledge in a machine-readable format and the SPARQL query language (https://www.w3.org/TR/sparql11-query/) to exploit these data known as knowledge graphs.

Bioinformatics communities are encouraged to develop ontologies that adhere to the principles of the Basic Formal Ontology[BFO:2022] and the Open Biological and Biomedical Ontology Foundry[@BFO:2022]. These ontologies aim to structure the modelling of knowledge in a common conceptual framework and allow the reuse of existing ontologies, favouring collaboration between different research communities [XREF].
The datasets, now structured, use controlled vocabularies and taxonomies to use unambiguous standard terms.

Effective tools exist to access ontologies (BioPortal, OLS and AgroPortal) and datasets (NCBI, EBI). In addition, these resources can be imported into RDF data store, also known as triplet store, to be exploited using the SPARQL query language. In conclusion, semantic web technologies have greatly facilitated the integration and exploitation of bioinformatics data, allowing the efficient management of large and complex datasets.

MetaboHUB is a French national infrastructure dedicated to research in metabolomics and fluxomics, with the aim of providing an integrated platform for the study of metabolic pathways and networks. This initiative brings together a wide range of academic and industrial partners, including experts in analytical chemistry and bioinformatics, to develop cutting-edge technologies and methodologies for metabolomics research. One of the key objectives of MetaboHUB is to ensure data and software interoperability within the consortium. In this context, our working group "Creating FAIR resources for knowledge mining" aims to organize data and metadata in RDF format, as well as to structure consortium software products into web components, allowing for better reuse and integration of resources within the scientific community.
Presently, this has resulted in the establishment of a dedicated infrastructure engineered for leveraging knowledge bases. Among these resources is the FORUM database[REF], which has been made accessible to the metabolic community

# Overview of the General Design


# 

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


# Acknowledgements

We acknowledge contributions from Brigitta Sipocz, Syrtis Major, and Semyeong
Oh, and support from Kathryn Johnston during the genesis of this project.

# References



