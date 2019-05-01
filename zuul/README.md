# zuul

## 添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>
 
 spring-retry配合zuul开启重试
 
 ## 修改配置文件
 
    server:
      port: 8770
    spring:
      application:
        name: zuul
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    zuul:
      routes:
        hello:
          path: /hello/**
          serviceId: eureka-client
      retryable: true
    
    hystrix:
      command:
        default:
          execution:
            isolation:
              thread:
                timeoutInMilliseconds: 30000
    
    ribbon:
      ConnectTimeout: 1000
      ReadTimeout: 1000
      MaxAutoRetries: 1
      MaxAutoRetriesNextServer: 1
      OkToRetryOnAllOperations: true
    
    #直接使用url
    #zuul:
    #  routes:
    #    helloUrl:
    #      path: /helloUrl/**
    #      url: http://localhost:8764
    
    #不使用eureka配置 自定义服务
    #zuul:
    #  routes:
    #    hello:
    #      path: /hello/**
    #      serviceId: hello
    #  prefix: /api
    #ribbon:
    #  eureka:
    #    enabled: false
    #hello:
    #  ribbon:
    #    listOfServers: http://localhost:8764

## zuul自定义熔断器

    @Component
    public class MyFallbackProvider implements FallbackProvider {
        @Override
        public String getRoute() {
            return "*";
        }
    
        @Override
        public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
            return new ClientHttpResponse() {
    
                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    return headers;
                }
    
                @Override
                public InputStream getBody() throws IOException {
                    return new ByteArrayInputStream("error".getBytes());
                }
    
                @Override
                public HttpStatus getStatusCode() throws IOException {
                    return HttpStatus.OK;
                }
    
                @Override
                public int getRawStatusCode() throws IOException {
                    return 200;
                }
    
                @Override
                public String getStatusText() throws IOException {
                    return "ok";
                }
    
                @Override
                public void close() {
    
                }
            };
        }
    }
 
getRoute返回某个服务名或者*全部服务

hystrix.command.default.execution.isolation.thread:timeoutInMilliseconds=30000

熔断器超时时间推荐大于ribbon超时时间

可将default替换为具体服务名 为具体服务添加熔断超时时间

## 自定义filter

    @Component
    public class TokenFilter extends ZuulFilter {
        @Override
        public String filterType() {
            return "pre";
        }
    
        @Override
        public int filterOrder() {
            return 0;
        }
    
        @Override
        public boolean shouldFilter() {
            return true;
        }
    
        @Override
        public Object run() throws ZuulException {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            String token = request.getParameter("token");
            if (StringUtils.isBlank(token)) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(401);
                try {
                    ctx.getResponse().getWriter().write("token is empty");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }
    }

filterType过滤器类型pre-路有前 post-路由后 routing-路由动作 error-错误

## zuul重试

需添加spring.retry依赖

    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>
    
添加ribbon配置

    ribbon:
      ConnectTimeout: 1000
      ReadTimeout: 1000
      MaxAutoRetries: 1
      MaxAutoRetriesNextServer: 1
      OkToRetryOnAllOperations: true
     
ribbon超时计算(ReadTimeout + ConnectTimeout) * (MaxAutoRetries + 1) * (MaxAutoRetriesNextServer + 1)建议小于熔断器超时时间

可在ribbon前添加具体服务名 为服务单独设置重试

    eureka-client:
        ribbon:
          ConnectTimeout: 1000
          ReadTimeout: 1000
          MaxAutoRetries: 1
          MaxAutoRetriesNextServer: 1
          OkToRetryOnAllOperations: true