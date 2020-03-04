package com.satya.SCGatewaysample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(UriConfiguration.class)
public class ScGatewaySampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScGatewaySampleApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
		String httpBin = uriConfiguration.getHttpbin();

		return builder.routes()
				.route(p -> p.path("/get").filters(f -> f.addRequestHeader("Hello", "World"))
				.uri(httpBin))
				.route(p -> p
					.host("*.hystrix.com")
						.filters(f -> f.hystrix(config ->
								config.setName("myCmd").setFallbackUri("forward:/fallback")))
						.uri(httpBin))
				.build();
	}

	@RequestMapping("/fallback")
	public Mono<String> fallback() {
		return Mono.just("fallback");
	}

}
