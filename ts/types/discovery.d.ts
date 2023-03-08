export class SWDiscoveryConfiguration {
    static setConfigString(confjson:string) : SWDiscoveryConfiguration
    static init() : SWDiscoveryConfiguration
    
    setPageSize(n: number) : SWDiscoveryConfiguration

    setSizeBatchProcessing(n: number) : SWDiscoveryConfiguration

    setLogLevel(level: string) : SWDiscoveryConfiguration
    
    setCache(flag: boolean)  : SWDiscoveryConfiguration

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
        token?:string) : SWDiscoveryConfiguration

    localFile(filename : string ,mimetype? : string) : SWDiscoveryConfiguration

    rdfContent(content : string ,mimetype? : string) : SWDiscoveryConfiguration

    sourcesSize : number 
    pageSize    : number 
    sizeBatchProcessing : number 
    logLevel : string 
    cache : boolean

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
    orderByAsc(...ref:string[]) : SWTransaction
    orderByDesc(...ref:string[]) : SWTransaction
    getSerializedString() : string
    setSerializedString(serialized_transaction:string) : SWTransaction
    console() : SWTransaction
}

export function SWDiscovery(config?:SWDiscoveryConfiguration) : SWDiscovery

export class SWDiscovery {

    setConfig(config:SWDiscoveryConfiguration) : SWDiscovery
    getConfig() : SWDiscoveryConfiguration

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
    console(): SWDiscovery
    focus() : string
    focus(ref: string) : SWDiscovery
    remove(focus: string) : SWDiscovery
    sparql() : string

    select( ...lRef:string[] ) : SWTransaction
    selectByPage( ...lRef:string[] ) : Promise<[number,(SWTransaction[])]>
    selectDistinctByPage( ...lRef:string[] ) : Promise<[number,(SWTransaction[])]>

    setSerializedString(serializedDiscovery:string) : SWDiscovery
    getSerializedString() : string

    setDecoration(key : string, value : string) : SWDiscovery
    getDecoration(key : string) : string  
    browse<Type>( fun : (n: any, p : Number) => Type ) : Type[]


}

/**
 * RDF type definition
 */

export interface IRI {} 
export function IRI(iri : String) : IRI
export interface URI {} 
export function URI(iri : String) : URI
export interface Anonymous {} 
export function Anonymous(iri : String) : Anonymous
export interface PropertyPath {} 
export function PropertyPath(value : String) : PropertyPath
export interface Literal {} 
export function Literal(value : String, datatype?: URI|string) : Literal
export function Literal(value : number) : Literal
export function Literal(value : boolean) : Literal
export interface QueryVariable {} 
export function QueryVariable(name : string) : QueryVariable

