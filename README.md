#### VOSpace Browser 0.6

By default, this uses the CANFAR (CADC) VOSpace located at:

<a rel="external" href="http://www.canfar.phys.uvic.ca/vospace">http://www.canfar.phys.uvic.ca/vospace</a>

<a href="https://travis-ci.org/opencadc/storage"><img src="https://travis-ci.org/opencadc/storage.svg?branch=0.6" /></a>


### Building

Running:

`gradle clean build`

Will produce a `war` file in the `build/libs` directory that can be deployed into a Java container such as Tomcat or Jetty.


### Running

For an embedded Jetty container, you can just run:

`gradle run`

To produce a running embedded Jetty container running on port `8080`, with a debug port on `5555`.

Although untested, one should be able to pass one's own Registry settings into the `JAVA_OPTS` environment variable:

`gradle -Dca.nrc.cadc.reg.client.Registry.host=<YOUR HOST> run`

Or deploy the `war` file in `build/libs` into a Java container such as Tomcat.

#### Running with Docker

See the Docker repo here:

<a rel="external" href="https://hub.docker.com/r/opencadc/storage/">https://hub.docker.com/r/opencadc/storage/</a>

Or build your own with the provided Dockerfile (from the same directory as the Dockerfile):

`docker build -t storage .`

It uses the lightweight Tomcat java container that was built using Alpine Linux found here:

<a href="https://hub.docker.com/r/canfar/tomcat/" rel="external">https://hub.docker.com/r/canfar/tomcat/</a>

Then run it:

`docker run --name storage -d -p 8080:8080 -p 5555:5555 storage`

Or mount your own built `war`:

`docker run --name storage -d -p 8080:8080 -p 5555:5555 -v $(pwd)/build/libs:/usr/local/tomcat/webapps storage`

