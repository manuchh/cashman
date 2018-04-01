# CashMan

## Cash Dispensing Machine

## Running CashMan locally
```
	git clone https://github.com/manuchh/cashman.git or Download ZIP
	cd cashman
```


## 1. Running from an IDE
```
	You can run a Spring Boot application from your IDE as a simple Java application. Eclipse users can select
	File menu -> Import -> Existing Maven Projects
	
	If you are using Spring STS, then just right click on project and select Run As -> Spring Boot App
```


## 2. Running as a Packaged Application
```
	If you use the Spring Boot Maven plugin to create an executable jar, you can run your application using
	$ cd cashman
	$ java -jar target/cashman-0.0.1-SNAPSHOT.jar
```


## 3. Using the Maven Plugin
```
	$ cd cashman
	$ mvn spring-boot:run
```


You can then access CashMan here: http://localhost:8081/


## Working with CashMan in Eclipse/STS

### prerequisites
The following items should be installed in your system:
* Apache Maven (https://maven.apache.org/install.html)
* git command line tool (https://help.github.com/articles/set-up-git)
* Eclipse with the m2e plugin or STS (http://www.springsource.org/sts)


### Steps:

1) In the command line
```
git clone https://github.com/manuchh/cashman.git
```
2) Inside Eclipse/STS
```
File -> Import -> Maven -> Existing Maven project
```


## CashMan supports all the below features:
```
* Cash dispensing machine is initialized only once.
* All the currency loaded while initialization is saved in in-memory storage.
* Machine has the supply of cash available.
* Machine knows about how many of each type of bank note it has. And user can check the balance of a particular bank note from UI also. 
* After initialisation, it is only possible to add or withdraw notes. 
* It support $20 and $50 Australian denominations. 
* It is able to dispense legal combinations of notes. For example, a request for $100 can be satisfied by either five $20 notes or 2 $50 notes.
* If a request can not be satisfied due to failure to find a suitable combination of notes, it reports an error on the screen. 
* Dispensing money reduces the amount of available cash in the machine. 
* Failure to dispense money due to an error does not reduce the amount of available cash in the machine.
* It is able to dispense combinations of cash that leave options open. For example, if it could serve up either 5 $20 notes or 2 $50 notes to satisfy a request for $100, but it only has 5 $20 notes left, it should serve the 2 $50 notes. 
* It is able to inform admin if there is low balance in machine.
```


## Technology Stack includes below:
```
* Sprig Boot (Web, Data-JPA, Thymeleaf, Mail, Actuator )
* Java 8
* JUnit and Mockito
* H2
* Maven
* H2
* Bootstrap, JQuery
* Jacoco
```


## Notes
```
* User interface is basic and can be enhanced
* All UI labels are coming from messages, can be enhanced for Internationalization
* Currently supports only $20 and $50 notes, can be enhanced for other Australian denominations and coinage
* I have tried to achieve maximum code coverage for test, still can be enhanced
```


Thanks,

Sunil Chopra
