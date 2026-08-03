## Folks Application

Folks backend server.

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

## Getting Started 

This sample application provides a REST API using [Declarative Vert](https://github.com/javalabs-eng/declarative-vertx), so it is very easy to make it work as standalone server.  

### Checkout the Code.

```
git clone https://github.com/folks-eng/folks-app

```

### Compile the Code

`folks-app` uses `maven` as build tool. Use the below command to compile the codebase. You need `JDK 17` or higher version to compile the code.

```
mvn clean install

```

### Version Upgrade

To upgrade the module version in parent pom file as well as all child modules, issue the below command:

```
mvn versions:set -DnewVersion=YOUR_NEW_VERSION

```

### Local Deployment

#### Pre-Requisite

Folks backend requires `PostgreSQL` to be setup. Follow the [Folks DB](https://github.com/folks-eng/folks-db) to install `PostgreSQL` and setup folks schema.

#### Start Folks Server

Once database setup is complete, Run the `start.sh` file to bring up the folks backend server.

```
sh start.sh

```

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

`folks-app` maintain two different key store options:
1. folks.pkcs - For jwt signing
2. .pem keys and certs - For mTLS

### Setup mTLS

This is how the end-to-end communication would look like.

```
Browser
    │ HTTPS
    ▼
Node.js        https://localhost:3000
    │ mTLS
    ▼
Vert.x         https://localhost:8443

```

For local development, create:

```
certs/
├── ca.key
├── ca.crt
├── folks-app.key
├── folks-app.csr
├── folks-app.crt
├── folks-ui.key
├── folks-ui.csr
└── folks-ui.crt
```

The flow is:

1. Create your own Certificate Authority (CA).
1. Use the CA to sign the server certificate.
1. Use the CA to sign the client certificate.
1. Configure:
    1. Vert.x with folks-app.key, folks-app.crt, and ca.crt
    1. Node.js with folks-ui.key, folks-ui.crt, and ca.crt

#### Step 1 - Create a CA

```
openssl genrsa -out ca_javalabs.key 4096
```

```
openssl req -x509 \
    -new \
    -nodes \
    -key ca_javalabs.key \
    -sha256 \
    -days 3650 \
    -out ca_javalabs.crt \
    -subj "/C=IN/ST=West Bengal/L=Kolkata/O=Javalabs/CN=Javalabs CA"
```

#### Step 2 - Create the Server Certificate

**Generate private Key:**

```
openssl genrsa -out folks-app.key 2048
```

**Generate a certificate signing request (CSR):**

```
openssl req \
    -new \
    -key folks-app.key \
    -out folks-app.csr \
    -subj "/C=IN/ST=West Bengal/L=Kolkata/O=Folks/CN=Folks App"
```

**Create a file named server.ext:**

```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage=digitalSignature,keyEncipherment
extendedKeyUsage=serverAuth
subjectAltName=DNS:localhost,IP:127.0.0.1
```

While an `.ext` file may not be not mandatory, but they are highly recommended, especially for TLS and mTLS.
The `.ext` files tell `OpenSSL` what type of certificate you are creating and what extensions should be embedded in the `X.509` certificate.

Without an .ext file OpenSSL creates a certificate, but it may not contain important extensions such as:

* Subject Alternative Name (SAN)
* Extended Key Usage
* Key Usage
* Basic Constraints

Modern TLS implementations rely on these extensions.

Here are the attributes of server.ext file:

* basicConstraints - `CA:FALSE`

This says: This certificate cannot act as a Certificate Authority.
Only the `ca_javalabs.crt` should have: `CA:FALSE`

* keyUsage - `digitalSignature,keyEncipherment`

This specifies what the key may be used for.
For an HTTPS server, these usages are appropriate. Without them, some clients will reject the certificate.

* extendedKeyUsage - `serverAuth`

This is very important. It tells clients:

This certificate is intended to authenticate a TLS server. If you accidentally use a client certificate as the server certificate, 
many TLS stacks will reject it.

* subjectAltName (SAN) - `DNS:localhost,IP:127.0.0.1`

This is probably the most important extension.

Older browsers used the Common Name (CN): `CN=localhost`
Modern TLS clients ignore the CN and verify the hostname against the SAN extension instead.

For example: 
1. https://localhost:8443 requires: DNS:`localhost`

1. https://127.0.0.1:8443 requires: `IP:127.0.0.1`

If the SAN is missing, you'll typically see hostname verification failures.

**Sign it:**

```
openssl x509 \
    -req \
    -in folks-app.csr \
    -CA ca_javalabs.crt \
    -CAkey ca_javalabs.key \
    -CAcreateserial \
    -out folks-app.crt \
    -days 365 \
    -sha256 \
    -extfile server.ext
```

#### Step 3 - Create the Client Certificate

**Generate the key:**

```
openssl genrsa -out folks-ui.key 2048
```

**Generate the CSR:**

```
openssl req \
    -new \
    -key folks-ui.key \
    -out folks-ui.csr \
    -subj "/C=IN/ST=West Bengal/L=Kolkata/O=Folks/CN=Folks UI"
```

**Create client.ext:**

```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage=digitalSignature,keyEncipherment
extendedKeyUsage=clientAuth
```

**Sign it:**

```
openssl x509 \
    -req \
    -in folks-ui.csr \
    -CA ca_javalabs.crt \
    -CAkey ca_javalabs.key \
    -CAcreateserial \
    -out folks-ui.crt \
    -days 365 \
    -sha256 \
    -extfile client.ext
```

#### Final Set of Files

```
certs/
│
├── ca.crt
├── ca.key
│
├── server.key
├── server.crt
│
├── client.key
└── client.crt
```

There are other files that will be created.

1. `.csr` — Certificate Signing Request

This file contains:

1. The public key.
1. Information about the subject (Common Name, Organization, etc.).
1. A digital signature created with the corresponding private key.

It does **not** contain the private key.

For example:
`client.csr` contains a request like:

```
CN=folks-ui
O=Folks
C=IN
Public Key=...
```

You send a CSR to a Certificate Authority (CA), and the CA verifies it and issues a certificate.

The flow is:

```
client.key
      │
      ▼
Generate CSR
      │
      ▼
folks-ui.csr
      │
      ▼
CA signs it
      │
      ▼
folks-ui.crt
```

After you receive `folks-ui.crt`, you usually don't need the `.csr` anymore unless you want to reissue the certificate.

2. `.srl` — Serial Number File

This file stores the next certificate serial number that your CA will issue.




### Generate Keystore for JWT Signing and Validation

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
    -dname "CN=Folks App, OU=Development, O=Javalabs, L=Kolkata, S=West Bengal, C=IN"

```

**Note:** The alias name must be one of "RS256", "RS384", "RS512", "ES256K", "ES256", "ES384", "ES512".

### View the Keystore

```
keytool  -list -v -keystore folks.pkcs -storepass secret

```

### Additional Options

#### Extract the Public Key to .pem File

```
keytool -exportcert -rfc \
    -alias fks_dev \
    -keystore folks.pkcs \
    -file folks_pub.pem \
    -storepass secret 

```

#### Extract the Private Key to .pem File

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
 

## Configuration

There are 3 configuration files used by this service. These files are placed under `src/main/resources` directory.
 
<ul>
<li><b>vertx-web.xml</b> - This file is the core configuration file that provides configuration and deployment information for Vert.x. It's the standard name used by decl-vertx-container module as a deployment descriptor in Vert.x applications. Apart from standard vert.x configuration, this file also defines the Verticles that will be deployed.</li>
<li><b>server.xml</b> - If any of your verticles is starting an http server, then you need to create the second file server.xml, which is a configuration file for the embedded http server. It dictates how the server behaves during startup and operation. It also defines various elements like the server, services, connectors, and containers, which handle requests and manage web applications.</li>
<li><b>routing-config.xml</b> - This file defines how HTTP requests are handled based on their paths and methods. Configuration typically involves setting up routes with corresponding handlers, potentially including path parameters and request body processing</li>
</ul>
