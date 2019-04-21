# eureka client ribbon(eureka ribbon客户端)

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
 
## 修改配置文件

    server:
      port: 8766
    spring:
      application:
        name: eureka-client-ribbon
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/

defaultZone不能写成default-zone

## 配置RestTemplate

    @Configuration
    public class RestTemplateConfig {
    
        @Bean
        @LoadBalanced
        RestTemplate restTemplate(){
            return new RestTemplate();
        }
    }
@LoadBalanced将RestTemplate开启负载 实现原理是为RestTemplate添加拦截器 将远程请求交于ribbon LoadBalanceClient处理 实现负载

## 编写controller

    @RestController
    public class HelloController {
    
        @Autowired
        private RestTemplate restTemplate;
    
        @RequestMapping("/hello")
        public String hello(String name){
            return restTemplate.getForObject("http://eureka-client/hello?name=" + name, String.class);
        }
    }
这里直接使用服务名称eureka-client 注意服务名称不能使用下划线 否则无法识别

## 不使用eureka注册表

ribbon默认使用eureka client从eureka service获取的服务注册表 并将其缓存 每10s更新一次

可以不使用eureka注册表 自己定义服务也可以实现负载

在配置文件application.yml中添加

    ribbon:
      eureka:
        enabled: false
    hello:
      ribbon:
        listOfServers: localhost:8764,localhost:8765
hello为自定义服务名 替换掉服务名eureka-client即可