package com.sigure.config;

import com.sigure.controller.filterFactory.OneGatewayFilterFactory;
import com.sigure.controller.filterFactory.TokenFilter;
import com.sigure.controller.filterFactory.TwoGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class InitConfig {

    /*@Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("name", "zx"))
                        .uri("http://httpbin.org:80")
                )
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
    }*/

    /*@Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.filter(new RequestTimeFilter()))
                        .uri("http://httpbin.org:80")
                )
                .build();
    }*/

    @Bean
    public OneGatewayFilterFactory oneGatewayFilterFactory(){
        return new OneGatewayFilterFactory();
    }

    @Bean
    public TwoGatewayFilterFactory twoGatewayFilterFactory(){
        return new TwoGatewayFilterFactory();
    }

    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter();
    }

    @Bean
    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }

    @Bean
    @Primary
    public UriKeyResolver uriKeyResolver() {
        return new UriKeyResolver();
    }
}
