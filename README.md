# HTTP-Server[![Build Status](https://travis-ci.org/klkelley/http-java.svg?branch=master)](https://travis-ci.org/klkelley/http-java)


## Using Docker 
### Get latest docker image 

```
$ docker pull klkelley/http-server:branch-master-latest
```

### Pull a Specific Image 

To pull a specific image: 
```
$ docker pull klkelley/http-sever:<IMAGE-NAME>
```

Visit the [HTTP-Server DockerHub Repo](https://hub.docker.com/r/klkelley/http-server/tags/) to pick a specific image.

There are three different types of tagged images: 

* `master.latest` which is considered to be the most up to date image associated with the master branch
* Images specific the latest build on a development branch, i.e. `feat-get-request.latest`. 
* Immutable images for each merge to master. These are designated as `master.<SHA>` and are the only group of images
that do not change.  

### Run project 
```
$ docker run klkelley/http-server:latest
```

## Using Gradle 
 
### Build 
```
$ gradle build 
```

### Run project 
```
$ java -jar build/libs/http-server.jar
```

### Run tests
```
$ gradle test
```