export class SWDiscoveryConfiguration {
    setConfigString(confjson:string) : SWDiscoveryConfiguration
    static init() : SWDiscoveryConfiguration
    proxy(urlProxy : string , method? : string) : SWDiscoveryConfiguration
    urlFile(
        filename:string,
        mimetype?:string,
        method?:string,
        auth?:string,
        login?:string,
        password?:string,
        token?:string) : SWDiscoveryConfiguration

    sparqlEndpoint(
        url : string,
        method?:string,
        auth?:string,
        login?:string,
        password?:string,
        token?:string)

    localFile(filename : string ,mimetype? : string) : SWDiscoveryConfiguration

    rdfContent(content : string ,mimetype? : string) : SWDiscoveryConfiguration
}

export class FilterIncrement {
    isLiteral : SWDiscovery
    isUri : SWDiscovery
    isBlank : SWDiscovery
    regex(pattern:string, flags?:string) : SWDiscovery
    contains(pattern:string) : SWDiscovery
    strStarts(pattern:string) : SWDiscovery
    strEnds(pattern:string) : SWDiscovery
}

export class SWTransaction {
    progression(callBack : (percent: number) => any ) : SWTransaction
    requestEvent(callBack : (event: string) => any ) : SWTransaction
    abort() : SWTransaction
    commit() : SWTransaction
    raw() : Promise<any>
    distinct() : SWTransaction
    limit(l:number) : SWTransaction
    offset(l:number) : SWTransaction
    orderByAsc(...ref) : SWTransaction
    orderByDesc(...ref) : SWTransaction
    getSerializedString() : string
    setSerializedString(serialized_transaction:string) : SWTransaction
    console() : SWTransaction
}

export class SWDiscovery {

    constructor(config: SWDiscoveryConfiguration)
    filter : FilterIncrement
    root() : SWDiscovery

    prefix(short: string,long : URI | IRI | string) : SWDiscovery
    directive(diretive: string) : SWDiscovery
    prefix(directive: URI | IRI | string) : SWDiscovery

    something(ref: string): SWDiscovery
    isSubjectOf(uri: URI | IRI | string, ref?: string): SWDiscovery
    isObjectOf(uri: URI | IRI | string, ref?: string): SWDiscovery
    isLinkTo(uri: URI | IRI | string, ref?: string): SWDiscovery
    isLinkFrom(uri: URI | IRI | string, ref?: string): SWDiscovery
    isA(uri: URI | IRI | string): SWDiscovery


    set(uri: URI | IRI | string) : SWDiscovery
    setList(uris: (URI | IRI | string)[]) : SWDiscovery
    datatype(uri: URI | IRI | string, ref: string): SWDiscovery

    bind(ref: string) : SWDiscovery
    helper() : SWDiscovery
    focus() : string
    focus(ref: string) : SWDiscovery

    selectDistinctByPage( ...lRef ) : Promise<number,(SWTransaction[])>

}

/**
 * Type definition
 */
export class SparqlDefinition {}
export class IRI extends SparqlDefinition { constructor(iri : String) }
export class URI extends SparqlDefinition { constructor(uri : String) }
export class Anonymous extends SparqlDefinition { constructor(iri : String) }
export class PropertyPath extends SparqlDefinition { constructor(value : String) }
export class PropertyPath extends SparqlDefinition { constructor(value : String) }
export class Literal extends SparqlDefinition {
    constructor(value : String, datatype?: URI|string)
    constructor(value : number)
    constructor(value : boolean)
}
export class QueryVariable extends SparqlDefinition { constructor(name : String) }


export function Anonymous(a: any): any

export function BindIncrement(a: any, b: any): any

export function IRI(a: any): any

export function LinkFrom(a: any, b: any, c: any): any

export function LinkTo(a: any, b: any, c: any): any