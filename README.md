# MDC Project Resolver

Scala tool for identifying MDC project codes within a text and resolving the codes to a project name via a single POST and a single GET endpoint.
Reads a source file into memory and generates a map of project code to project names which is then used to resolve codes matching a specific regex format (see below).  The source file can be either:
* an `.xlsx` file where the first two columns represent `projectCode` and `projectName`.  Data must be in the first tab of the worksheet, and the first row of the spreadsheet is ignored on the basis that these will usually be column headings. 
* a `.txt` file in the format `projectCode=projectName`

Examples of both file types can be found in the `src/test/resources` directory.

### Running locally

The service can be run locally with `sbt run`, or by running the main `ProjectResolverWebService` class in Intellij.

 The service runs on `localhost:8081`, but this can be altered with the `PROJECT_RESOLVER_HOST` and `PROJECT_RESOLVER_PORT` environment variables.

### Containerisation

Build the Docker image with the following command in the root folder:

``` docker build -t resolver-service .```

This requires a compiled .jar file in the /target file structure, which must first be generated with `sbt assembly`

To run the container:

`docker run --publish 8081:8081 resolver-service`


### Environment Variables

Although currently configured to identify and resolve MDC project codes, by overriding the default environment variables  a custom regular expression and filepath the tool can be used as a generic regex service to resolve one value to another.  

* `PROJECT_RESOLVER_HOST`: default value is 8081
* `PROJECT_RESOLVER_PORT`: default value is 0.0.0.0
* `PROJECT_RESOLVER_FILEPATH`: path to `.xlsx` or `.txt` file used to resolve possible matches in a text.  Defaults to the spreadsheet in this repo (`src/main/resources/informatics_projects.xlsx`) 
* `PROJECT_RESOLVER_REGEX`: regular expression used to identify possible matches in a text. Defaults to `\bMDCP-\d{4}\b`
* `PROJECT_RESOLVER_ENDPOINT`: determines the url path of the web service.  Defaults to "projects"
* `RESOLVER_MATCH_FROM_REGEX`: whether the resolved entity is matched from a regex itself. Boolean and default is `false`.
* `RESOLVER_MATCH_REGEX`: the regex to determine the resolved entity match (see `RESOLVER_MATCH_FROM_REGEX`)

If using a regex to match the resolved entity then the file referenced by `PROJECT_RESOLVER_FILEPATH` should match with the `RESOLVER_MATCH_REGEX`.  
For example, the initial regex (`PROJECT_RESOLVER_REGEX`) to match something could be:
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
[{"code":"ABC-123","name":"I am the first entity"}]
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
curl --location --request POST 'http://localhost:8081/projects' --header 'Content-Type: text/plain'--data-raw 'Your string...'
```


Results are returned in JSON format:

```json
[
  {
    "code": "MDCP-0199",
    "name": "Aphrodite"
  },
  {
    "code": "MDCP-0008",
    "name": "Apollo"
  }
]
```

Both endpoints will return an empty array with a 200 response if no matches are found in the passed text.
