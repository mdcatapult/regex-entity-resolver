# MDC Entity Resolver

A Scala web-service for identifying entities (ie terms) within text and resolving to a value. This was originally designed to find MDC project codes or drug company research codes within text and to resolve them to project or company names. It exposes a single POST and a single GET endpoint
The web service can be used for 2 different use cases. Both use a regex to identify the term in the provided text but have different methods of resolving those terms to a "thing".

1. Uses a regex to find the term and a key value file to resolve the exact term found. eg "MDCP-0123"="A project"
2. Uses a regex to find the term and a regex along with a key value file to resolve the term. This is used where only part of the term is used to resolve something. For example, where the terms ABC-123 & ABC-456 both resolve to "ABC"="Something".
This requires the environment variables `RESOLVER_MATCH_FROM_REGEX` and `RESOLVER_MATCH_REGEX` to be provided.

### How it works
The web-service reads a source file into memory and generates a map of terms to values which is then used to resolve terms matching a specific regex format (see below).  The source file can be either:
* an `.xlsx` file where the first two columns represent `term` and `resolved value`.  Data must be in the first tab of the worksheet, and the first row of the spreadsheet is ignored on the basis that these will usually be column headings. 
* a `.txt` file in the format `key=value`. Examples of company codes and the regexes for resolving them are provided in `company_codes_and_regexes.txt` and the key/value file `company_codes_resolver.txt`.

Examples of both file types can be found in the `src/test/resources` directory.

### Running locally

The service can be run locally with `sbt run`, or by running the main `EntityResolverWebService` class in Intellij.

 The service runs on `localhost:8081`, but this can be altered with the `PROJECT_RESOLVER_HOST` and `PROJECT_RESOLVER_PORT` environment variables.

### Containerisation

Build the Docker image with the following command in the root folder:

``` docker build -t resolver-service .```

This requires a compiled .jar file in the /target file structure, which must first be generated with `sbt assembly`

To run the container:

`docker run --publish 8081:8081 resolver-service`


### Environment Variables

Although currently configured to identify and resolve MDC project codes, by overriding the default environment variables  a custom regular expression and filepath the tool can be used as a generic regex service to resolve one value to another.  

* `ENTITY_RESOLVER_HOST`: default value is 8081
* `ENTITY_RESOLVER_PORT`: default value is 0.0.0.0
* `ENTITY_RESOLVER_FILEPATH`: path to `.xlsx` or `.txt` file used to resolve possible matches in a text.  Defaults to the spreadsheet in this repo (`src/main/resources/informatics_projects.xlsx`) 
* `ENTITY_RESOLVER_REGEX`: regular expression used to identify possible matches in a text. Defaults to `\bMDCP-\d{4}\b`
* `ENTITY_RESOLVER_ENDPOINT`: determines the url path of the web service.  Defaults to `projects` ie `http://localhost:8081/projects`
* `ENTITY_RESOLVER_MATCH_FROM_REGEX`: whether the resolved entity is matched from a regex itself. Boolean and default is `false`.
* `ENTITY_RESOLVER_MATCH_REGEX`: the regex to determine the resolved entity match (see `ENTITY_RESOLVER_MATCH_FROM_REGEX`)

### Regular Expression Resolving
If using a regex to match the resolved entity then the file referenced by `ENTITY_RESOLVER_FILEPATH` should match with the `ENTITY_RESOLVER_MATCH_REGEX`.  
For example, the initial regex (`ENTITY_RESOLVER_REGEX`) to match something could be:
```regexp
\b(ABC|DEF)\s{0,1}.{0,1}\d{1,20}\b
```
The file containing the resolvers could be:
```
ABC="I am the first entity"
DEF="I am the second entity"
```
and the regular expression to resolve the entity could be:
```regexp
\b\s{0,1}.{0,1}\d{1,20}\b
```
This would match `ABC-123` or `DEF-123` and respond with eg:

```json
[{"entity":"ABC-123","resolvedEntity":"I am the first entity"}]
```

In the code the `RESOLVER_MATCH_REGEX` is used to extract text from the match so that it will then match whatever is in the file. ie `-123` is matched 
and this is then extracted from `ABC-123` which then matches `ABC` in the resolver file.

### Endpoints

`GET /projects/{mdc-project-code}`
```curl
curl --location --request GET 'http://localhost:8081/projects/MDCP-0199
```
`POST /projects` - accepts raw text in the request body, e.g.
```curl
curl --location --request POST 'http://localhost:8081/projects' --header 'Content-Type: text/plain' --data-raw 'Your string...'
```


Results are returned in JSON format:

```json
[
  {
    "entity": "MDCP-0199",
    "resolvedEntity": "Aphrodite"
  },
  {
    "entity": "MDCP-0008",
    "resolvedEntity": "Apollo"
  }
]
```

Both endpoints will return an empty array with a 200 response if no matches are found in the passed text.
