## Signito Java client - usage guide

This repository contains HowTos and usage examples for [Signito](https://www.signito.sk/en/home/) signing solution.

### Basic usage

To start using signito-client Java API just include these Maven dependencies into your project:

```
<dependency>
	<groupId>sk.annotation.projects.signito</groupId>
    <artifactId>signito-client</artifactId>
    <classifier>minimal</classifier>
    <version>LATEST VERSION FROM MAVEN CENTRAL</version>
</dependency>

<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.2.2</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.13.3</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jdk8</artifactId>
    <version>2.13.3</version>
</dependency>
```

Examples of how to use Java API you just importend, please refer to [sample Maven project](java-sample-client/) in this repository that contains several examples for: 
- creating new document to sign (basic scenario with one signature and one document, to more complex examples)
- checking whether document is signed (either by webhook or by pull-method)
- downloading signed documents & signature protocol
