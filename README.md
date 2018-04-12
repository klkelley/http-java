# HTTP-Server[![Build Status](https://travis-ci.org/klkelley/http-java.svg?branch=master)](https://travis-ci.org/klkelley/http-java)


## Visit on the Web

[karakelley.rocks](http://karakelley.rocks:8080/)


## Using Docker 
### Get latest docker image 

```
$ docker pull klkelley/http-server:master.latest
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
Running the below command will automatically pull and run the image specified

```
$ docker run -it --rm -p 8080:8080 -v <DIRECTORY YOU WANT TO SERVE>:<PATH WHERE DIRECTORY WILL BE MOUNTED> klkelley/http-server:master.latest --port 8080 --directory <MOUNTED PATH>
```

An example: `ex. docker run -it --rm -p 8080:8080 -v /Users/karakelley/:/var/public klkelley/http-server:master.latest --port 8080 --directory /var/public`

## Using Gradle 
 
### Build 
```
$ gradle build 
```

### Run project 

The default port is set to 0 and can be set to specific port by passing a port number as an 
argument.

```
$ java -jar build/libs/http-server.jar -p <OPTIONAL PORT NUMBER> -d <OPTIONAL DIRECTORY PATH>
```

### Run tests
```
$ gradle test
```