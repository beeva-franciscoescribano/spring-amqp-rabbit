# RabbitMq Producer and Consumer Example using Spring Amqp Framework

Producer to a RabbitMq Broker (Federated) SSL External configurated, 3 messages and wait for a minute.

Consume with listeners the 3 messages produced in order to verify connection with the broker/s

### Docker

Previously it's necessary to have a RabbitMq broker composed of two containers from an image created with the Dockerfile included in __/docker__ directory

In the first place, from docker directory:

```
docker build -t rabbitmq:3.5.7-management-ssl
```

After, run the docker containers 

```
docker run -d --hostname rabbit1 --name my-rabbit1 rabbitmq:3.5.7-management-ssl
```

```
docker run -d --hostname rabbit2 --name my-rabbit2 rabbitmq:3.5.7-management-ssl
```

Update the federation upstream uri in each broker, log in the web administration console (user guest, pass guest) at

https://rabbit1:15672/#/federation-upstreams

https://rabbit2:15672/#/federation-upstreams

and replacing rabbit-host by rabbit2 in rabbit1 broker and rabbit-host by rabbit1.

Check the federation status in 

https://rabbit1:15672/#/federation

https://rabbit2:15672/#/federation

is running

### Installing jar with dependencies

From pom.xml directory

```
mvn clean install
```

Under ./target directory there is the SpringAmqpRabbit-1.0-SNAPSHOT.jar with all dependencies included in it.


### Configuration 

A configuration by example must be created inside the machine where jar will be executed.
Create by convention a directory called __/var/properties/spring-amqp-rabbit/__ and locate in: 

* __keycert.p12__ client certificate .p12 file. 
* __keycert.jks__ trusted certificates store in jks. 
* __ssl.properties__ copied from /src/main/resources

Keycert files can be get from one of the two docker containers running under the __/etc/rabbitmq/ssl/client__ directory

```
# client certificate store
keyStore=file:/var/properties/spring-amqp-rabbit/keycert.p12

# java trust store with all the trusted server certificates
trustStore=file:/var/properties/spring-amqp-rabbit/keycert.jks

# client certificate password
keyStore.passPhrase=rabbitmqpass

# java trust store with all the trusted server certificates password
trustStore.passPhrase=rabbitmqpass

# connection addresses hostname:port separated with commas
connection.addresses=172.17.0.2:5673,172.17.0.3:5673
```
To test with more than one RabbitMQ Federated broker in __connection.addresses__ include all __hostname:port__ brokers separated with commas

### Execution

```
java -jar SpringAmqpRabbit-1.0-SNAPSHOT.jar 
```

Output
```
MESSAGE CONSUMED: Message 1 queued with key
MESSAGE CONSUMED: Message 2 queued with key
MESSAGE CONSUMED: Message 3 queued with key
```
After 60 secs program exits(0)
