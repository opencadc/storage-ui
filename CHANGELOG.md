# Storage User Interface

## Apr 15, 2021
### version 1022 (unchanged)
* Gradle updates and added CI

## Jan 7, 2021
### version 1022
* Requires cadc-vosui v 1.2.1: supports POSIX-backed VOSpace services, VOSpace web service
implementation configuration required, error messages passed more effectively from 
VOSpace web service implementation

#### Browser integration test changes 
* Structured to test individual actions separately. (ie 'move')
* Can be configured to run against specific VOSpace implementations
* Clean up test files after successful run


### version 1021 & lower
* VOSpace implementation defaults to 'vault'

#### Browser integration tests
* run against CADC `vault` VOSpace implementation
* require known/dependable folders in the test user's home directory
* test user & password is configurable


