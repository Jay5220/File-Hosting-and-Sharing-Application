# Cloud Based Storage Application
We have implemented cloud based application similar to dropbox where user can download, upload, delete and edit the files.

## Features

- User can upload multiple files to the server using UDP Protocol, creating multiple threads and buffers
- Data is  Synchronized and stored on a cloud storage and its status is sent to the server.
- File is transmitted using blocks because if error occurs while transmission than only those - blocks which are not transmitted will be send again instead of sending whole file again.
- Multiple users can works together on one server.

## Use Case
![](https://ase-project.s3.amazonaws.com/1.jpeg)


## High Level Design 
![](https://ase-project.s3.amazonaws.com/2.jpeg)
## Technologies
- [JAVA] 
- [AWS] 
- [Spring Boot] 


   [JAVA]: <https://www.oracle.com/java/>
   [AWS]: <https://aws.amazon.com/>
   [Spring Boot]: <https://spring.io/projects/spring-boot/>
   
## How to run this project
Open server folder and run following command.

```sh
mvn clean
mvn install
mvn spring-boot:run
```

Open client folder and run following command.

```sh
mvn clean
mvn install
java -jar Client-1.0-SNAPSHOT-jar-withependencies.jar <clientFileHolder Path>
```
Open download folder and run following command.

```sh
mvn clean
mvn install
mvn spring-boot:run
```

## Output

To get the all file list from server hit below url

http://127.0.0.1:8081/getFileList

To download the file from server hit below url

http://127.0.0.1:8081/fileDownload/<fileName>




