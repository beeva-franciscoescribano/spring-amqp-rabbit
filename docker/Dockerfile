FROM rabbitmq:3.5.7-management

# Define environment variables.
ENV RABBITMQ_USER rabbitmq

ADD init.sh /init.sh

RUN chmod 755 init.sh

ADD rabbitmqadmin /usr/local/bin/rabbitmqadmin

RUN chmod 755 /usr/local/bin/rabbitmqadmin

ADD rabbitmqadmin.conf /etc/rabbitmq/rabbitmqadmin.conf

WORKDIR /etc/rabbitmq

RUN mkdir ssl

WORKDIR /etc/rabbitmq/ssl

RUN mkdir certs private server client
RUN chmod 700 private
RUN echo 01 > serial && touch index.txt

COPY openssl.cnf /etc/rabbitmq/ssl/openssl.cnf

RUN openssl req -x509 -config openssl.cnf -newkey rsa:2048 -days 365 -out cacert.pem -outform PEM -subj /CN=MyTestCA/ -nodes
RUN openssl x509 -in cacert.pem -out cacert.cer -outform DER

WORKDIR /etc/rabbitmq/ssl/server

RUN openssl genrsa -out key.pem 2048
RUN openssl req -new -key key.pem -out req.pem -outform PEM -subj /CN=rabbitmq/O=server/ -nodes

WORKDIR /etc/rabbitmq/ssl

RUN openssl ca -config openssl.cnf -in ./server/req.pem -out ./server/cert.pem -notext -batch -extensions server_ca_extensions

WORKDIR /etc/rabbitmq/ssl/server

RUN openssl pkcs12 -export -out keycert.p12 -in cert.pem -inkey key.pem -passout pass:rabbitmqpass

WORKDIR /etc/rabbitmq/ssl/client

RUN openssl genrsa -out key.pem 2048
RUN openssl req -new -key key.pem -out req.pem -outform PEM -subj /CN=rabbitmq/O=client/ -nodes

WORKDIR /etc/rabbitmq/ssl

RUN openssl ca -config openssl.cnf -in ./client/req.pem -out ./client/cert.pem -notext -batch -extensions client_ca_extensions

WORKDIR /etc/rabbitmq/ssl/client

RUN openssl pkcs12 -export -out keycert.p12 -in cert.pem -inkey key.pem -passout pass:rabbitmqpass

WORKDIR /etc/rabbitmq

COPY rabbitmq.config /etc/rabbitmq/rabbitmq.config

EXPOSE 15672

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
RUN apt-get update && apt-get install -qy oracle-java8-installer \
	python3

WORKDIR /etc/rabbitmq/ssl

RUN keytool -importkeystore -srckeystore server/keycert.p12 -srcstoretype PKCS12 -destkeystore keycert.jks -storepass rabbitmqpass -srcstorepass rabbitmqpass

WORKDIR /etc/rabbitmq

# Define default command
CMD ["/init.sh"]

