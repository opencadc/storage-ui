# Storage User Interface

## January 30, 2024
* Support for IAM Group querying
* MD5 property checking fix

## January 12, 2024
* Feature flag to disable some features (Batch download/upload, ZIP Download, Create External Links, Supports Paginated downloads)

## December 8, 2023
* OpenID Connect compliant with Authorization Code flow

## March 29, 2023
* Add Shell Script download
* Merge library as forking is not practical.

## May 12, 2022
* Zip file generation for arc storage nodes
* requires cadc-vosui v 1.2.12

## March 1, 2022
* Zip file generation for vault storage nodes
* storage.war file is unversioned
* requires cadc-vosui v 1.2.8

## June 4, 2021
### version 1023
* Support for multiple VOSpace Services
* Retains support for existing URLs

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


