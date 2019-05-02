# sleuth client服务链路跟踪

## zipkin

    spring cloud D版本之前zipkin通过添加依赖的方式进行部署 E版本之后变为从官网下载jar包直接启动方式部署
    
    下载地址
    
    https://zipkin.io/pages/quickstart.html
    
    启动
    
    java -jar zipkin-server-2.12.9-exec.jar
    
    访问
    
    http://localhost:9411/zipkin/

## 添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zipkin</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.amqp</groupId>
        <artifactId>spring-rabbit</artifactId>
    </dependency>

sleuth默认使用http方式传输数据到zipkin spring-rabbit将传输方式变为rabbitmq也可使用kafka进行传输

## 修改配置文件

    server:
      port: 8801
    spring:
      application:
        name: sleuth-client
      sleuth:
        sampler:
          probability: 1.0
        web:
          client:
            enabled: true
    #  zipkin:
    #    base-url: http://localhost:9411
      rabbitmq:
        host: localhost
        username: guest
        password: guest
        port: 5672
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/

使用rabbitmq方式传输需要将zipkin.base-url删除 添加rabbitmq配置

spring.sleuth.sampler.probability   默认0.1---10%概收集 1.0---100%收集

## 使用rabbitmq方式传输

添加依赖
    
        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit</artifactId>
        </dependency>

添加rabbitmq配置
        
        spring:
            rabbitmq:
                host: localhost
                username: guest
                password: guest
                port: 5672

将zipkin.base-url删除

启动zipkin

    java -jar .\zipkin-server-2.12.9-exec.jar --zipkin.collector.rabbitmq.addresses=localhost:5672
    
    zipkin.collector.rabbitmq.addresses 用逗号分隔的 RabbitMQ 地址列表，例如localhost:5672,localhost:5673
    
    zipkin.collector.rabbitmq.username  连接到 RabbitMQ 时使用的用户名，默认为guest
    
    zipkin.collector.rabbitmq.password  连接到 RabbitMQ 时使用的密码，默认为 guest

## zipkin将数据存入mysql

    zipkin存入mysql需要初始化sql脚本 默认数据库为zipkin
    
    https://github.com/apache/incubator-zipkin/blob/master/zipkin-storage/mysql-v1/src/main/resources/mysql.sql
    
启动zipkin

    java -jar .\zipkin-server-2.12.9-exec.jar --zipkin.collector.rabbitmq.addresses=localhost:5672 --zipkin.stora
    ge.type=mysql --zipkin.storage.mysql.username=root --zipkin.storage.mysql.password=root
    
    zipkin.storage.mysql.host       数据库的host，默认localhost
        
    zipkin.storage.type             默认的为mem，即为内存，其他可支持的为cassandra、cassandra3、elasticsearch、mysql
    
    zipkin.storage.mysql.username   连接数据库的用户名，默认为空
    
    zipkin.storage.mysql.password   连接数据库的密码，默认为空
    
    zipkin.storage.mysql.db         zipkin使用的数据库名，默认是zipkin
    
    zipkin.storage.mysql.max-active 最大连接数，默认是10
    
## zipkin将数据存入Elasticsearch

先行安装ElasticSearch和Kibana 注意版本选择ElasticSearch 6.0以上会报错Setting index.mapper.dynamic was removed after version 6.0.0 大神们求解

ElasticSearch   5.6.15

Kibana          5.6.15

启动zipkin

    java -jar .\zipkin-server-2.12.9-exec.jar --zipkin.collector.rabbitmq.addresses=localhost:5672 --zipkin.stora
    ge.type=elasticsearch --zipkin.storage.elasticsearch.hosts=localhost:9200 --zipkin.storage.elasticsearch.index=zipkin
    
    zipkin.storage.elasticsearch.hosts          ES_HOSTS，默认为空
    
    zipkin.storage.elasticsearch.index          ES_INDEX，默认是zipkin
    
    zipkin.storage.elasticsearch.username       ES的用户名，默认为空
    
    zipkin.storage.elasticsearch.password       ES的密码，默认是为空