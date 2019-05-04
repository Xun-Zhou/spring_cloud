# spring cloud gateway

Spring Cloud Gateway是Spring Cloud官方推出的第二代网关框架，取代Zuul网关。

## 添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>

## 修改配置文件

    server:
      port: 8901
    spring:
      application:
        name: spring-cloud-gateway
      profiles:
        active: my_route
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    
    ---
    spring:
      cloud:
        gateway:
          routes:
            - id: after_route
              uri: http://httpbin.org:80
              filters:
                - AddRequestHeader=X-Request-Foo, Bar
              predicates:
                - After=2017-01-20T17:42:47.789-07:00[America/Denver]
      profiles: after_route
    
    ---
    spring:
      cloud:
        gateway:
          discovery:
            locator:
              enabled: false
              lower-case-service-id: true
          routes:
            #- id: one_route
            #  uri: http://httpbin.org:80
            #  filters:
            #    - One=true
            #    - Two=X-Request-Foo, Bar
            #  predicates:
            #    - Path=/get
            - id: two_route
              uri: lb://EUREKA-CLIENT
              filters:
                - One=true
                - Two=X-Request-Foo, Bar
                - name: RequestRateLimiter
                  args:
                    key-resolver: '#{@uriKeyResolver}'
                    redis-rate-limiter.replenishRate: 1
                    redis-rate-limiter.burstCapacity: 3
              predicates:
                - Path=/hello
      profiles: my_route
      redis:
        host: localhost
        port: 6379

## 路由predicates

添加路由有两种方式

1.通过java bean方式添加
    
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .uri("http://httpbin.org:80")
                )
                .build();
    } 

将/get/**方法路由到http://httpbin.org:80  相当于请求http://httpbin.org:80/get/**

2.通过配置文件

    spring:
      cloud:
        gateway:
          routes:
            - id: after_route
              uri: http://httpbin.org:80
              predicates:
                - Path=/get
            - id: after_route
              uri: http://httpbin.org:80
              predicates:
                - Path=/hello

spring cloud gateway提供很多默认的predicates方式

https://cloud.spring.io/spring-cloud-gateway/spring-cloud-gateway.html#gateway-request-predicates-factories

## 过滤器filter

spring cloud gateway filter有两种类型pre(路有前执行)和post(路由后执行) 同zuul类似

spring cloud gateway filter作用范围有两种gateway filter(单个路由)global gateway filer(全局路由)

    spring:
      cloud:
        gateway:
          routes:
            - id: after_route
              uri: http://httpbin.org:80
              filters:
                - AddRequestHeader=X-Request-Foo, Bar
              predicates:
                - After=2017-01-20T17:42:47.789-07:00[America/Denver]
      profiles: after_route

AddRequestHeader用于添加请求头参数 完整类名AddRequestHeaderGatewayFilterFactory 遵循约定大于配置原则 

自定义filter时类名格式 XXXXXXHeaderGatewayFilterFactory 配置文件引用填写 XXXXXX

spring cloud gateway提供很多默认实现

https://cloud.spring.io/spring-cloud-gateway/spring-cloud-gateway.html#_addrequestheader_gatewayfilter_factory

## 自定义gateway filter

    public class RequestTimeFilter implements GatewayFilter, Ordered {
    
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            exchange.getAttributes().put("requestTime", System.currentTimeMillis());
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        Long startTime = exchange.getAttribute("requestTime");
                        if (startTime != null) {
                            System.out.println(exchange.getRequest().getURI().getRawPath() + ": " + (System.currentTimeMillis() - startTime) + "ms");
                        }
                    })
            );
        }
    
        @Override
        public int getOrder() {
            return 0;
        }
    }
 
实现GatewayFilter, Ordered接口

chain.filter(exchange).then()相当于post路由后执行方法

getOrder排序 数字越小优先级越高

使用

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.filter(new RequestTimeFilter()))
                        .uri("http://httpbin.org:80")
                )
                .build();
    }

## 自定义gateway filter factory

过滤器工厂可以再配置文件中使用

两种方式

1.实现AbstractGatewayFilterFactory只有一个参数时使用

    public class OneGatewayFilterFactory extends AbstractGatewayFilterFactory<OneGatewayFilterFactory.Config> {
    
        @Override
        public GatewayFilter apply(Config config) {
            return (exchange, chain) -> {
                exchange.getAttributes().put("requestTime", System.currentTimeMillis());
                return chain.filter(exchange).then(
                        Mono.fromRunnable(() -> {
                            Long startTime = exchange.getAttribute("requestTime");
                            if (startTime != null) {
                                StringBuilder sb = new StringBuilder(exchange.getRequest().getURI().getRawPath())
                                        .append(": ")
                                        .append(System.currentTimeMillis() - startTime)
                                        .append("ms");
                                if (config.isWithParams()) {
                                    sb.append(" params:").append(exchange.getRequest().getQueryParams());
                                }
                                System.out.println(sb.toString());
                            }
                        })
                );
            };
        }
    
        @Override
        public List<String> shortcutFieldOrder() {
            return Arrays.asList("withParams");
        }
    
        public OneGatewayFilterFactory() {
            super(Config.class);
        }
    
        public static class Config {
    
            private boolean withParams;
    
            public boolean isWithParams() {
                return withParams;
            }
    
            public void setWithParams(boolean withParams) {
                this.withParams = withParams;
            }
        }
    }

需要将其交于spring ioc管理
    
    @Bean
    public OneGatewayFilterFactory oneGatewayFilterFactory(){
        return new OneGatewayFilterFactory();
    }

配置文件 注意名称

    spring:
      cloud:
        gateway:
          routes:
            - id: after_route
              uri: http://httpbin.org:80
              filters:
                - One=true
                - AddRequestHeader=X-Request-Foo, Bar
              predicates:
                - After=2017-01-20T17:42:47.789-07:00[America/Denver]

2.实现AbstractNameValueGatewayFilterFactory多个参数时使用

    public class TwoGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    
        public GatewayFilter apply(NameValueConfig config) {
            return (exchange, chain) -> {
                ServerHttpRequest request = exchange.getRequest().mutate().header(config.getName(), config.getValue()).build();
                return chain.filter(exchange.mutate().request(request).build());
            };
        }
    
        public TwoGatewayFilterFactory(){
    
        }
    }

需要将其交于spring ioc管理

    @Bean
    public TwoGatewayFilterFactory twoGatewayFilterFactory(){
        return new TwoGatewayFilterFactory();
    }

配置文件 注意名称

    spring:
      cloud:
        gateway:
          routes:
            - id: after_route
              uri: http://httpbin.org:80
              filters:
                - Two=X-Request-Foo, Bar
              predicates:
                - After=2017-01-20T17:42:47.789-07:00[America/Denver]

## 自定义global gateway filer

无需再配置文件中配置 初始化后作用于所有路由

    public class TokenFilter implements GlobalFilter, Ordered {
    
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            String token = exchange.getRequest().getQueryParams().getFirst("token");
            if (token == null || token.isEmpty()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        }
    
        @Override
        public int getOrder() {
            return 0;
        }
    }

需要将其交于spring ioc管理

    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter();
    }
    
## spring cloud gateway自带限流

spring cloud gateway自带限流用redis+lua脚本实现令牌桶算法

添加依赖

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>

配置

    spring:
      cloud:
        gateway:
          routes:
            - id: two_route
              uri: http://localhost:8764
              filters:
                - One=true
                - Two=X-Request-Foo, Bar
                - name: RequestRateLimiter
                  args:
                    key-resolver: '#{@uriKeyResolver}'
                    redis-rate-limiter.replenishRate: 1
                    redis-rate-limiter.burstCapacity: 3
              predicates:
                - Path=/hello
      redis:
        host: localhost
        port: 6379

redis-rate-limiter.replenishRate    令牌桶每秒填充平均速率

redis-rate-limiter.burstCapacity    令牌桶总容量

key-resolver                        限流key

限流key需要自己定义 实现KeyResolver接口 将类交于spring ioc管理

    public class UriKeyResolver implements KeyResolver {
    
        @Override
        public Mono<String> resolve(ServerWebExchange exchange) {
            return Mono.just(exchange.getRequest().getURI().getPath());
        }
    }
    
    @Bean
    public UriKeyResolver uriKeyResolver() {
        return new UriKeyResolver();
    }
    
## spring cloud gateway熔断器

spring cloud gateway熔断器通过filter的形式使用

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/hello")
                        .filters(f -> f
                            .hystrix(c -> c
                                .setFallbackUri("forward:/fallback")
                            )
                        )
                        .uri("http://localhost:8764")
                )
                .build();
    }

这里熔断器开启时重定向到/fallback

    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("fallback");
    }

