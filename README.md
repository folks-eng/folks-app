## Folks Application

Folks is a backend server application.

It provides the operations required to manage application resources and verify the integrity of server responses.
 
## Requirements

Java 17 or higher and Maven 3.x are required.

## Table of Contents

* [Getting started](#getting-started)
* [Review Swagger Doc](#review-swagger-doc)
* [Test the service](#test-the-service)
* [What's included](#whats-included)
* [Security concerns](#security-concerns)
* [Configuration](#configuration)

## Getting Started

This sample application provides a REST API using [Declarative Vert](https://github.com/javalabs-eng/declarative-vertx), so it is very easy to make it work as standalone server.  

### Check Out the Code

```
<prompt> git clone https://github.com/folks-eng/folks-app

```

### Compile the Code

`folks-app` uses Maven as its build tool. Use the following command to compile the codebase. JDK 17 or later is required.

```
<prompt> mvn clean install

```

### Version Upgrade

To update the module version in the parent POM and all child modules, run the following command:

```
<prompt> mvn versions:set -DnewVersion=YOUR_NEW_VERSION

```

### Local Deployment

#### Prerequisites

The Folks backend requires PostgreSQL. Follow the [Folks DB](https://github.com/folks-eng/folks-db) instructions to install PostgreSQL and set up the Folks schema.

#### Start Folks Server

Once the database setup is complete, run `start.sh` to start the Folks backend server.

```
<prompt> sh start.sh

```

The application should start and have an output similar to this: 

    [...]
    [vert.x-eventloop-thread-1] INFO org.javalabs.decl.vertx.container.VertxHttpServer - Started Http Server. Listening to port: 8080
    [main] INFO org.javalabs.decl.vertx.container.VertxContainer - Deployment of verticle app.http.server is successful. Deployment Id: 3abd2b35-4cf7-426f-83a9-7b394710df08
    [vert.x-worker-thread-0] INFO com.folks.app.core.AppProcessor - Scheduled default timer. Initial Delay: 0. Pause Time (ms): 1800000
    [vert.x-worker-thread-0] INFO com.folks.app.core.AppProcessor - Started Verticle: AppProcessor
    [main] INFO org.javalabs.decl.vertx.container.VertxContainer - Deployment of verticle app.processor is successful. Deployment Id: 04595511-d20a-43e2-abda-52c931c2d531


The server port is configured in `server.xml`. Update that configuration if you need to use a different port.

## Review the Swagger Documentation

The `openapi.yaml` file is generated in:

```
docs/openapi.yaml
```

To view the Swagger documentation, navigate to the `docs` directory and run the following command to start a simple HTTP server:
```
<prompt> python3 -m http.server -b 127.0.0.1
```

The server starts and displays output similar to:
```
<prompt> Serving HTTP on 127.0.0.1 port 8000 (http://127.0.0.1:8000/) ...
```

Open `http://127.0.0.1:8000/` in your browser to view the API documentation.


## SSL Configuration

### Server Configuration

`folks-app` is configured to start with SSL enabled. Refer to the following snippet from `server.xml`:

```
<server-config>
    <server-opts>
    	<port>9443</port>
        <client-auth>REQUIRED</client-auth>
    </server-opts>
    <tcp-opts>
        <ssl>true</ssl>
        <expiry>60</expiry>
    </tcp-opts>
    ...
    ...
    ...

</server-config>

```

The `expiry` is in `minute`.

`<client-auth>REQUIRED</client-auth>` requires clients, such as cURL or Postman, to present a valid client certificate. This enables mTLS between the client and `folks-app`.

### Keys and Certificates

`folks-app` includes its server key, certificates, and CA certificate under `src/main/resources`.

```
src/main/resources
      |
       --- ca
      |     |
      |      --- ca_javalabs.key
      |     |
      |      --- ca_javalabs.crt
      |
       --- server_cert
      |     |
      |      --- folks-app.key
      |     |
      |      --- folks-app.crt
      |
       --- client_cert
            |
             --- folks-client.key
            |
             --- folks-client.crt

```

The `client_cert` directory is not used as the server identity. Its certificate and key are used by clients when establishing mTLS connections, including requests used to obtain authentication tokens.

## Testing Folks Application

Because `folks-app` requires SSL and mTLS, a client must present a valid client certificate when establishing a connection before making API calls.

### Step 1 - Obtain an Admin Token

An admin token is required for certain operations, for example:

1. Creating a User         [ scope -> user:create ]
2. Viewing All Users    [ scope -> user:query ]

We will create a token with the scope `user:create`. Likewise, create a token with scope `user:query` if you want to view all users.

```
curl -i \
    -u '9efbd3b3-a0a9-468a-8652-7f489adf6a45:7c6a180b36896a0a8c02787eeafb0e4c' \
    --cert /path/to/folks-app/src/main/resources/client_cert/folks-client.crt \
    --key /path/to/folks-app/src/main/resources/client_cert/folks-client.key \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d 'grant_type=client_credentials&scope=user%3Acreate' \
    https://localhost:9443/api/v1/mgmt/login

```

Response:

```
{
  "token_type" : "Bearer",
  "access_token" : "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI5ZWZiZDNiMy1hMGE5LTQ2OGEtODY1Mi03ZjQ4OWFkZjZhNDUiLCJhdWQiOm51bGwsInNjb3BlIjoidXNlcjpjcmVhdGUiLCJpc3MiOiJmb2xrcyIsInByaXYiOiJhZG1pbiIsImp0aSI6ImM5YmVhNDQzLTJlOGEtNDNiNi1hMzY1LTBmN2FiYjdiMmRjZCIsImlhdCI6MTc4Njc4NjAwOCwiZXhwIjoxNzg2Nzg5NjA4fQ.Tej28BCxo3GG6KsabNW_Q82Q_o6h5a9XSefjXe0WBLVI4hIuA2-_dA2zNh1ae3mEkXlu1TAuVchdsfXMzC0gS_YTeQzINVoDikaUtWmz1XpEPP2CD2U3tCTdQ2XmQoRnn7a5XlERb-vLX3sHgVyhWMqXh6cRGPfxK84Fgkj2qy27gLjRM4auM3MVC_lUvWrrg009TybvBhm4C-SqenfjrVVm1qXY1C89aefYmXV2uvkpdjiniNxa2W0yuc2mKmrJW6j-i5gjZLC96fIKi4an2GZy6CxSf1jWgqt2pa1BGdAV8OqTc1vfTVOXcPy4YV3tlxncdm-gSf_R1hWM1rMfog",
  "scope" : "user:create",
  "expires_in" : 3600,
  "refresh_token" : null
}
```

The `expires_in` indicates the token expiry time in `seconds`.


### Step 2 - Create a User

**Payload:** `user.json`

```
{
    "fullName": "Socretes",
    "phone1" : "1-029837467382",
    "email" : "socretes@javalabs.org"
}


```

**Command:**

```
curl -i \
    -X POST \
    --cert ~/Projects/folks-app/src/main/resources/client_cert/folks-client.crt \
    --key ~/Projects/folks-app/src/main/resources/client_cert/folks-client.key \
    -H "Authorization: Bearer {access_token}" \
    -H "Content-Type:application/json" \
    --data-binary @./user.json \
    https://localhost:9443/api/v1/users

```

**Response:**

```
{
  "externalId" : "234b8491-cc5e-4be1-82ac-ba8ac35ff6d8",
  "fullName" : "Socretes",
  "email" : "socretes@javalabs.org",
  "phone1" : "1-029837467382",
  "phone2" : null,
  "role" : "CUSTOMER",
  "status" : "ACTIVE",
  "createdAt" : 1786786731348,
  "updatedAt" : null
}

```

After the user is created, a user token can be generated for operations such as viewing the user profile and future bookings.

### Step 3 - Generate a User Token (Non-Admin)

> **TODO:** Add the documented flow for generating a non-admin user token.



## Keystore Handling

`folks-app` uses two types of key material:
1. `folks.pkcs` - Used for JWT signing and validation.
2. PEM keys and certificates - Used for TLS and mTLS.

### Set Up the Browser-Facing Certificate for Node.js

#### Step 1 - Create a CSR and private key for the Node browser-facing server

```
openssl req -new -newkey rsa:2048 -nodes \
  -keyout node-ext.key \
  -out node-ext.csr \
  -subj "/CN=www.folks.com"

```

#### Step 2 - Create SAN + serverAuth extensions

```
subjectAltName = DNS:node.example.internal,DNS:localhost,IP:127.0.0.1
extendedKeyUsage = serverAuth
keyUsage = digitalSignature, keyEncipherment

```

#### Step 3 - Sign with your CA

```
openssl x509 -req \
  -in node-ext.csr \
  -CA ca_javalabs.crt \
  -CAkey ca_javalabs.key \
  -CAcreateserial \
  -out node-ext.crt \
  -days 825 \
  -sha256 \
  -extfile node-ext.ext

```

#### Step 4 - Verify Certificate

```
openssl verify -CAfile ca_javalabs.crt node-ext.crt

```

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
2. Use the CA to sign the server certificate.
3. Use the CA to sign the client certificate.
4. Configure:
   - Vert.x with `folks-app.key`, `folks-app.crt`, and `ca.crt`.
   - Node.js with `folks-ui.key`, `folks-ui.crt`, and `ca.crt`.

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

Although an `.ext` file may not always be mandatory, it is highly recommended for TLS and mTLS certificates.
The `.ext` file tells OpenSSL which extensions should be embedded in the X.509 certificate.

Without an `.ext` file, OpenSSL can create a certificate that does not contain important extensions such as:

* Subject Alternative Name (SAN)
* Extended Key Usage
* Key Usage
* Basic Constraints

Modern TLS implementations rely on these extensions.

The following attributes are defined in `server.ext`:

* basicConstraints - `CA:FALSE`

This specifies that the server certificate cannot act as a Certificate Authority.
The CA certificate is the certificate that should be configured as a Certificate Authority; the server certificate remains `CA:FALSE`.

* keyUsage - `digitalSignature,keyEncipherment`

This specifies what the key may be used for.
For an HTTPS server, these usages are appropriate. Without them, some clients will reject the certificate.

* extendedKeyUsage - `serverAuth`

This extension tells clients that:

The certificate is intended to authenticate a TLS server. If a client-only certificate is used as the server certificate, 
many TLS stacks will reject it.

* subjectAltName (SAN) - `DNS:localhost,IP:127.0.0.1`

This extension is essential for hostname verification.

Older clients commonly used the Common Name (CN), such as `CN=localhost`.
Modern TLS clients verify the hostname against the Subject Alternative Name (SAN) extension.

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

The certificate-generation process also creates additional files:

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

A CSR is submitted to a Certificate Authority (CA), which signs it and issues a certificate.

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

After `folks-ui.crt` has been issued, the CSR is generally not required unless the certificate needs to be reissued.

2. `.srl` — Serial Number File

This file stores certificate serial-number information used by the CA.


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



The service exposes a REST API. Depending on the application configuration, data can be backed by a relational database such as PostgreSQL.

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

The service uses three configuration files located under `src/main/resources`.
 
<ul>
<li><b>vertx-web.xml</b> - This file is the core configuration file that provides configuration and deployment information for Vert.x. It's the standard name used by decl-vertx-container module as a deployment descriptor in Vert.x applications. Apart from standard vert.x configuration, this file also defines the Verticles that will be deployed.</li>
<li><b>server.xml</b> - If any of your verticles is starting an http server, then you need to create the second file server.xml, which is a configuration file for the embedded http server. It dictates how the server behaves during startup and operation. It also defines various elements like the server, services, connectors, and containers, which handle requests and manage web applications.</li>
<li><b>routing-config.xml</b> - This file defines how HTTP requests are handled based on their paths and methods. Configuration typically involves setting up routes with corresponding handlers, potentially including path parameters and request body processing</li>
</ul>
