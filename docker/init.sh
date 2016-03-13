#!/bin/sh

( sleep 5 ; \

rabbitmq-plugins enable rabbitmq_federation
rabbitmq-plugins enable rabbitmq_federation_management
rabbitmq-plugins enable rabbitmq_auth_mechanism_ssl ; \
echo "*** Plugins enabled. ***" ; \

rabbitmqctl add_user $RABBITMQ_USER rabbit 2>/dev/null ; \
rabbitmqctl clear_password $RABBITMQ_USER  ; \
rabbitmqctl set_permissions -p / $RABBITMQ_USER  ".*" ".*" ".*" ; \
echo "*** User '$RABBITMQ_USER' completed. ***" ; \

rabbitmqadmin -c /etc/rabbitmq/rabbitmqadmin.conf -N host_ssl declare exchange name=myexchange type=direct ; \
rabbitmqadmin -c /etc/rabbitmq/rabbitmqadmin.conf -N host_ssl declare queue name=myqueue durable=true ; \
rabbitmqadmin -c /etc/rabbitmq/rabbitmqadmin.conf -N host_ssl declare binding source="myexchange" destination="myqueue" routing_key="mykey" ; \
echo "*** Exchange, queue and binding created. ***" ; \

rabbitmqctl set_parameter federation-upstream other-rabbit-upstream '{"uri":"amqps://rabbit-host:5673?cacertfile=/etc/rabbitmq/ssl/cacert.pem&certfile=/etc/rabbitmq/ssl/client/cert.pem&keyfile=/etc/rabbitmq/ssl/client/key.pem&verify=verify_peer&fail_if_no_peer_cert=true&auth_mechanism=external","max-hops":1}'
rabbitmqctl set_policy federate-me "^myqueue" '{"federation-upstream-set":"all"}' --apply-to queues
echo "*** Federation upstream created and policy declared. ***" ; \

echo "*** Log in the WebUI at port 15672 ***") &

rabbitmq-server $@
