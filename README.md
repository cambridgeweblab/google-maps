Google Maps Module
============

The google maps module provides the google api ready to use (through Spring), along
with some classes that perform requests against it. 

It was originally necessary because the exampay project was deployed on bluemix
 using IBM java which did not work with the built in request handler (using OkHttp).

### Setup with Maven and Spring
Just add to your pom file: 
```xml
<dependency>
    <groupId>ucles.weblab</groupId>
    <artifactId>weblab-google-maps</artifactId>
    <version>${common-java.version}</version>
</dependency>
```
and import the config file in your spring config file:
```java
@Configuration
@Import(ProvideApiContextConfig.class)
public class Foo {}
```
this will provide the geoApiContext bean (required for every requeset) and some 
querying classes.