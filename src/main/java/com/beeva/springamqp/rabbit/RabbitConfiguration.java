package com.beeva.springamqp.rabbit;

import com.rabbitmq.client.SaslConfig;
import com.rabbitmq.client.SaslMechanism;
import com.rabbitmq.client.impl.ExternalMechanism;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

@Configuration
@EnableRabbit
@ComponentScan(basePackages = "com.beeva.springamqp.rabbit")
public class RabbitConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() throws Exception {

        final Resource resource = new FileSystemResource("/var/properties/spring-amqp-rabbit/ssl.properties");

        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        factoryBean.setUseSSL(true);
        factoryBean.setSslPropertiesLocation(resource);

        SaslConfig saslConfig = new SaslConfig() {
            public SaslMechanism getSaslMechanism(String[] strings) {
                SaslMechanism mechanism = new ExternalMechanism();
                return mechanism;
            }
        };
        factoryBean.setSaslConfig(saslConfig);

        factoryBean.afterPropertiesSet();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factoryBean.getObject());

        final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        final String addresses = properties.getProperty("connection.addresses");
        connectionFactory.setAddresses(addresses);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws Exception {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean(name = "exchange")
    public String exchange() {
        return ("myexchange");
    }

    @Bean(name = "routingKey")
    public String routingKey() {
        return ("mykey");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() throws Exception {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
