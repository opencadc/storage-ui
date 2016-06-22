By default, this uses the CANFAR (CADC) VOSpace located at:

<a rel="external" href="http://www.canfar.phys.uvic.ca/vospace">http://www.canfar.phys.uvic.ca/vospace</a>

<a href="https://travis-ci.org/canfar/beacon"><img src="https://travis-ci.org/canfar/beacon.svg?branch=master" /></a>


### Building

Running:

`gradle clean build`

Will produce a `war` file in the `build/libs` directory that can be deployed into a Java container such as Tomcat or Jetty.


### Running

For an embedded Jetty container, you can just run:

`gradle run`

To produce a running embedded Jetty container running on port `8080`, with a debug port on `5555`.

Or deploy the `war` file in `build/libs` into a Java container such as Tomcat.

#### Running with Docker

See the Docker repo here:

<a rel="external" href="https://hub.docker.com/r/canfar/beacon/">https://hub.docker.com/r/canfar/beacon/</a>

Or build your own with the provided Dockerfile (from the same directory as the Dockerfile):

`docker build -t beacon .`

It uses the lightweight Tomcat java container that was built using Alpine Linux found here:

<a href="https://hub.docker.com/r/canfar/tomcat/" rel="external">https://hub.docker.com/r/canfar/tomcat/</a>
