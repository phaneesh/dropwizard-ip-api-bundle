# Dropwizard IP-API Bundle [![Travis build status](https://travis-ci.org/phaneesh/dropwizard-ip-api-bundle.svg?branch=master)](https://travis-ci.org/phaneesh/dropwizard-ip-api-bundle)

This bundle adds IP-API support for dropwizard.
This bundle compiles only on Java 17.

## Dependencies
* Dropwizard 2.1.0
* [IP-API]https://ip-api.com/docs/api:json)  

## Usage
The bundle adds IP-API support which makes it easier for geo ip information that is required by location aware services.


### Build instructions
  - Clone the source:

        git clone github.com/phaneesh/dropwizard-ip-api-bundle

  - Build

        mvn install

### Maven Dependency
Use the following repository:
```xml
<repository>
    <id>clojars</id>
    <name>Clojars repository</name>
    <url>https://clojars.org/repo</url>
</repository>
```
Use the following maven dependency:
```xml
<dependency>
    <groupId>io.github.phaneesh</groupId>
    <artifactId>dropwizard-ip-api</artifactId>
    <version>2.1.0-1</version>
</dependency>
```

### Using MaxMind bundle

#### Configuration
```yaml
ip-api:
  remoteIpHeader: "CLIENT-IP" #default is X-FORWARDED-FOR (when used behind a loadbalancer)
  cacheTTL: 120 #In seconds, Default is 300 seconds
  cacheMaxEntries: 102400 #Default is 10240
```

#### Bootstrap
```java
    @Override
    public void initialize(final Bootstrap...) {
        bootstrap.addBundle(new IpApiBundle() {
            
            public IpApiConfig getIpApiConfig(T configuration) {
                ...
            }
        });
    }
```

#### Headers stamped
* X-REQUEST-COUNTRY
* X-REQUEST-COUNTRY-ISO
* X-REQUEST-STATE
* X-REQUEST-STATE-ISO
* X-REQUEST-CITY
* X-REQUEST-POSTAL-CODE
* X-REQUEST-LATITUDE
* X-REQUEST-LONGITUDE
* X-REQUEST-ISP


LICENSE
-------

Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
