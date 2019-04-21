# eureka client ribbon hystrix(eureka ribbon hystrix客户端)

包含RestTemplate使用hystrix熔断器 以及hystrix熔断器监控hystrix dashboard

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
 
## 修改配置文件

    server:
      port: 8768
    spring:
      application:
        name: eureka-client-ribbon-hystrix
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    management:
      endpoints:
        web:
          exposure:
            include: hystrix.stream

spring boot 2.0后需要添加management.endpoints

defaultZone不能写成default-zone

## 配置启动类

    启动类添加注解
    @EnableHystrix
    @EnableHystrixDashboard
    
## 配置RestTemplate

    @Configuration
    public class RestTemplateConfig {
    
        @Bean
        @LoadBalanced
        RestTemplate restTemplate(){
            return new RestTemplate();
        }
    }

## 编写controller

    @RestController
    public class HelloController {
    
        @Autowired
        private RestTemplate restTemplate;
    
        @RequestMapping("/hello")
        @HystrixCommand(fallbackMethod = "helloError")
        public String hello(String name){
            return restTemplate.getForObject("http://eureka-client/hello?name=" + name, String.class);
        }
    
        public String helloError(String name){
            return "hi " + name + " hello api is error";
        }
    }
这里直接使用服务名称eureka-client 注意服务名称不能使用下划线 否则无法识别

方法上添加@HystrixCommand注解 表示使用熔断器 fallbackMethod为失败快速返回方法名 参数和返回值与熔断方法相同

## hystrix dashboard监控页面

    访问http://localhost:8768/actuator/hystrix.stream可以看到熔断监控数据
    
    访问http://localhost:8768/hystrix输入http://localhost:8768/actuator/hystrix.stream查看监控仪表 未产生数据为loading...
    
spring boot 2.0后访问额路径为actuator/hystrix.stream 也可通过配置形式直接访问/hystrix.stream

    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }