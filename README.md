## {APP} Application

This is a sample of a very basic application generated using declarative vert.x code generator tool.

It implements all the needed operations in order to handle various resource-specific operations and verify the integrity of the server response.
 
## Requirements

Java 17 or higher and Maven 3.x are required.

## Table of contents

* [Getting started](#getting-started)
* [Review Swagger Doc](#review-swagger-doc)
* [Test the service](#test-the-service)
* [What's included](#whats-included)
* [Security concerns](#security-concerns)
* [Configuration](#configuration)

## Getting started 

This sample application provides a REST API using [Declarative Vert](https://github.com/sudiptasish/declarative-vertx), so it is very easy to make it work as standalone server.  

### Local deployment 

* Run `mvn clean install`, to build the executable jar
* Run `java -jar target/{LOWER{APP}}-0.0.1-SNAPSHOT.jar`

The application should start and have an output similar to this: 

    [...]
    [vert.x-eventloop-thread-1] INFO org.javalabs.decl.vertx.container.VertxHttpServer - Started Http Server. Listening to port: 8080
    [main] INFO org.javalabs.decl.vertx.container.VertxContainer - Deployment of verticle app.http.server is successful. Deployment Id: 3abd2b35-4cf7-426f-83a9-7b394710df08
    [vert.x-worker-thread-0] INFO com.folks.app.core.AppProcessor - Scheduled default timer. Initial Delay: 0. Pause Time (ms): 1800000
    [vert.x-worker-thread-0] INFO com.folks.app.core.AppProcessor - Started Verticle: AppProcessor
    [main] INFO org.javalabs.decl.vertx.container.VertxContainer - Deployment of verticle app.processor is successful. Deployment Id: 04595511-d20a-43e2-abda-52c931c2d531


By default the application run on 8080 port. Modify the port in the `server.xml` if you want to change this value.

## Review swagger doc

The openapi.yaml file will be generated in

```
docs/openapi.yaml
```

To view the swagger doc, navigate to `/docs` directory and run the below command to start a simple http server
```
python3 -m http.server -b 127.0.0.1
```

The server will be started, and you will see the below log:
```
Serving HTTP on 127.0.0.1 port 8000 (http://127.0.0.1:8000/) ...
```

Open the url `http://127.0.0.1:8000/` in your favourite browser and you will see the api documentation.

## Keystore Handling

### Generate Keystore

```
keytool -genkeypair \
    -alias RS256 \
    -keyalg RSA \
    -sigalg SHA384withRSA \
    -keysize 2048 \
    -validity 365 \
    -keystore folks.pkcs \
    -storetype PKCS12 \
    -storepass secret \
    -keypass secret \
    -dname "CN=Folks App, OU=Development, O=Zetachron Technologies LLP, L=Kolkata, S=West Bengal, C=IN"

```

**Note:** The alias name must be one of "RS256", "RS384", "RS512", "ES256K", "ES256", "ES384", "ES512".

### View the Keystore

```
keytool  -list -v -keystore folks.pkcs -storepass secret

```

### Extract the Public Key to .pem File

```
keytool -exportcert -rfc \
    -alias fks_dev \
    -keystore folks.pkcs \
    -file folks_pub.pem \
    -storepass secret 

```

### Extract the Private Key to .pem File

```
openssl pkcs12 -in folks.pkcs -nodes -nocerts -out folks_prv.pem 
Enter Import Password:


```

## Test the service

The service is just a simple REST service. It uses an in-memory map to store the data.
You can also do with a relational database like MySQL or PostgreSQL.

#### Create an element

```
curl -X POST\
     -d '<payload>'\
     -H "Content-Type:application/json"\
     http://localhost:8080/api/v1/<resource_name>s
```


#### Update an element

```
curl -X PUT\
     -d '<payload>'\
     -H "Content-Type:application/json"\
     http://localhost:8080/api/v1/<resource_name>/{id}
```

#### View all elements

```
curl -X GET\
     -H "Content-Type:application/json"\
     http://localhost:8080/api/v1/<resource_name>
```

#### View specific element

```
curl -X GET\
     -H "Content-Type:application/json"\
     http://localhost:8080/api/v1/<resource_name>/{id}
```

#### Delete specific element

```
curl -X DELETE\
     -H "Content-Type:application/json"\
     http://localhost:8080/api/v1/<resource_name>/{id}
```



## What's included

```
|---com.lyra.sdk.server
|   |-- ServerApplication.java
|   |-- resource 
|        |-- CreateResource.java
|        |-- HealthResource.java
|        |-- VerifyResultResource.java   
|   |-- util
|        |-- ServerConfiguration.java
|---resources
|   |-- application.properties
```

**ServerApplication.java**: is the entry point of the Spring Boot application.

**Resource package**: contains all the Rest controllers that handle the Rest operations.

**Util package**: contains a configuration implementation that reads data from environment variables if provided.  

## Security concerns

This sample uses a [basic auth](https://developer.mozilla.org/fr/docs/Web/HTTP/Authentication#Sch%C3%A9ma_d'authentification_basique_(Basic)) implementation in order to provide a simple way to secure the Rest API.

See [Configuration](#security-configuration) section if you want to how to configure credentials.  

Please note that this authentication requires an HTTP**S** connection in order to be really effective. Otherwise the credentials will 
be sent in plain text.
 

## Configuration

There are 3 configuration files used by this service. These files are placed under `src/main/resources` directory.
 
<ul>
<li><b>vertx-web.xml</b> - This file is the core configuration file that provides configuration and deployment information for Vert.x. It's the standard name used by decl-vertx-container module as a deployment descriptor in Vert.x applications. Apart from standard vert.x configuration, this file also defines the Verticles that will be deployed.</li>
<li><b>server.xml</b> - If any of your verticles is starting an http server, then you need to create the second file server.xml, which is a configuration file for the embedded http server. It dictates how the server behaves during startup and operation. It also defines various elements like the server, services, connectors, and containers, which handle requests and manage web applications.</li>
<li><b>routing-config.xml</b> - This file defines how HTTP requests are handled based on their paths and methods. Configuration typically involves setting up routes with corresponding handlers, potentially including path parameters and request body processing</li>
</ul>
