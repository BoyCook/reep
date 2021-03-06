### About

“repo” is a hosted platform for data storing and information collaboration.

### Overview

Data can be stored at the lowest level as an `entity`. Entities can then be bundled into `domains` and `subdomains`.
Loosely coupled data structures can be formed to add a semantic layer on top of the entities.

##### Main Components

* Adaptive multi content REST API. At its core is an API that can store and serve objects of any content type
* Transform layer. This can transform data into a format different to how it's stored i.e. markdown to HTML
* Data store. This is the main object store. Exposed via an interface it can have different implementations depending on requirements. e.g. MongoDB, FS, in-memory 


##### Key Concepts

* Data and meta data (model) will be stored as entities
* Extensible, loosely coupled entities
* Entities can be of different `types`, which directly relates to it’s default HTTP `Content Type`
* `Content Negotiation` will be used to choose `Content Type`
* Default UIs are stored as data entities
* All types can inherit, with lowest level item overriding duplicates (see types)
* Pricing model options:
   * Free for low storage / bandwidth, pay for higher usages
   * Free for public data, pay for private
* `domain` maps to URL subdomain: `http://{domain}.repo.io`
* PaaS / BaaS
* Whole platform can run locally and sync to hosted service
* Implicit model generation
* Powerful query API / language (traversals, associations etc)
* Default use cases for platform
* http://en.wikipedia.org/wiki/Entity%E2%80%93attribute%E2%80%93value_model
* http://en.wikipedia.org/wiki/Sparse_matrix

### Default Types / Reserved

* `Entity`
* `Model`
* `Association`
* `Restriction`
* `User`
* `Domain`
* `Subdomain`

### REST API

##### Basic Entity

* `http://{domain}.repo.io/{entity}`
* `http://{domain}.repo.io/{entity}/{version}`
* `http://{domain}.repo.io/{subdomain}/{entity}`
* `http://{domain}.repo.io/{subdomain}/{entity}/{version}`

##### Model

* `http://{domain}.repo.io/types`
* `http://{domain}.repo.io/types/{name}`

##### Queries

* `http://{domain}.repo.io/<data>/(<query>)`
* `http://{domain}.repo.io/<data>/<id>/<association>/(<query>)`
* `http://{domain}.repo.io/<data>/(<query>)/<association>`
* `http://{domain}.repo.io/<data>/<id>|(<query>)/<association>/(<query>)/<association>/...`

Where queries apply to the left hand block. Also sort/filter parameters can be applied via querystrings.

### Logical Model

```
“model:” {
   “name”: “text”,
   “description”: “text”,
   “short”: “text”,
   “attributes”: [],
   “associations”: []
}
“attribute”: {
   “name”: “text”,
   “description”: “text”,
   “short”: “text”,
   “type”: “text”
}
“association”: {
   “name”: “text”,
   “description”: “text”,
   “short”: “text”,
   “forwardName”: “text”,
   “reverseName”: “text”
}
```

### Build 
[![Build Status](https://travis-ci.org/BoyCook/ExampleWebapp.png?branch=master)](https://travis-ci.org/BoyCook/ExampleWebapp)
[![Coverage Status](https://coveralls.io/repos/BoyCook/ExampleWebapp/badge.png)](https://coveralls.io/r/BoyCook/ExampleWebapp)

### DESCRIPTION

An example webapp

### Building

Tests

    mvn clean test

Coverage

    mvn clean cobertura:cobertura


#### Running

    mvn clean jetty:run
