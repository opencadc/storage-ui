#### User Storage User Interface 0.7.1

By default, this uses the CANFAR (CADC) VOSpace located at:

<a rel="external" href="http://www.canfar.phys.uvic.ca/vospace">http://www.canfar.phys.uvic.ca/vospace</a>

<a href="https://travis-ci.org/opencadc/vosui"><img src="https://travis-ci.org/opencadc/vosui.svg?branch=master" /></a>


### Building

Running:

`gradle clean build`

Will produce a `war` file in the `build/libs` directory that can be deployed into a Java container such as Tomcat or Jetty.


### Running

For an embedded Jetty container, you can just run:

`gradle run`

To produce a running embedded Jetty container running on port `8080`, with a debug port on `5555`.

Pass your own Registry settings into the `JAVA_OPTS` environment variable to use your own VOSpace service:

`gradle -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555 -Djava.security.egd=file:/dev/./urandom -Djsse.enableSNIExtension=false -Dca.nrc.cadc.auth.BasicX509TrustManager.trust=true -Dca.nrc.cadc.reg.client.RegistryClient.host=<your host for IVOA Registry lookup> run`

Or deploy the `war` file in `build/libs` into a Java container such as Tomcat.

Then, in your browser, look at <a href="http://localhost:8080/storage/list">http://localhost:8080/storage/list</a>.

#### Running with Docker

See the Docker repo here:

<a rel="external" href="https://hub.docker.com/r/opencadc/storage/">https://hub.docker.com/r/opencadc/storage/</a>

It uses the lightweight Tomcat java container that was built using Alpine Linux found here:

<a href="https://hub.docker.com/r/opencadc/tomcat/" rel="external">https://hub.docker.com/r/opencadc/tomcat/</a>

To run it as-is and use the CANFAR VOSpace Service, use:

`docker run --name storage -d -p 8080:8080 -p 5555:5555 opencadc/storage`

Then, in your browser, look at <a href="http://localhost:8080/storage/list">http://localhost:8080/storage/list</a>.

##### Running with Docker for your environment

To run in your environment, create your own Dockerfile:

```
# This is the Docker hub location for the User Storage User Interface (Project Beacon)
FROM opencadc/storage

# The JAVA_OPTS variable to pass to Tomcat.  Note the -Dca.nrc.cadc.reg.client.RegistryClient.host property.
ENV JAVA_OPTS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555 -Djava.security.egd=file:/dev/./urandom -Djsse.enableSNIExtension=false -Dca.nrc.cadc.auth.BasicX509TrustManager.trust=true -Dca.nrc.cadc.reg.client.RegistryClient.host=<your host for IVOA Registry lookup>"
```

Then run:

`docker build -t user_storage_ui .`

Then run it:

`docker run --name storage -d -p 8080:8080 -p 5555:5555 user_storage_ui`

Or mount your own built `war`:

`docker run --name storage -d -p 8080:8080 -p 5555:5555 -v $(pwd)/build/libs:/usr/local/tomcat/webapps user_storage_ui`

Then, in your browser, look at <a href="http://localhost:8080/storage/list">http://localhost:8080/storage/list</a>.
