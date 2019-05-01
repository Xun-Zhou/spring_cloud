# spring cloud config service

## 添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

## 修改配置文件

    server:
      port: 8771
    spring:
      application:
        name: config-service
      cloud:
        config:
          server:
            git:
              uri: https://github.com/Xun-Zhou/spring_cloud_config.git
              search-paths: config
          label: master
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    
    #使用本地配置
    #spring:
    #  cloud:
    #    config:
    #      server:
    #        git:
    #          native:
    #          searchLocations: classpath:/shared
    #  profiles:
    #    active: native