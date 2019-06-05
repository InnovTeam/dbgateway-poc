package com.sample.gateway.poc.dbgatewaypoc;

import static java.util.Collections.synchronizedMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GatewayRouteDefinitionRepository implements RouteDefinitionRepository {
	private final Map<String, RouteDefinition> routes = synchronizedMap(new LinkedHashMap<String, RouteDefinition>());
	
	private final MongoTemplate mongoTemplate;
	private final String mongoCollection="gateway_route";
	@Autowired
	public GatewayRouteDefinitionRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		//this.mongoCollection = mongoCollection;
	}

	/**
	 * Get custom routing information
	 *
	 */
	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		// Determine whether the local cache is empty, not empty to return directly
		if (Objects.nonNull(routes) && routes.size() > 0) {
			return getRoutes();
		}
		// Get custom routing information from mongodb
		List<GatewayRouteDefinition> mongoRouteDefinitionList = mongoTemplate.findAll(GatewayRouteDefinition.class,
				mongoCollection);
		List<RouteDefinition> routeDefinitions = new ArrayList<>();
		// Convert to a route definition object and store it in the cache
		mongoRouteDefinitionList.forEach(mongoRouteDefinition -> {
			RouteDefinition routeDefinition = new RouteDefinition();
			routeDefinition.setId(mongoRouteDefinition.getRouteId());
			routeDefinition.setFilters(mongoRouteDefinition.getFilters());
			routeDefinition.setPredicates(mongoRouteDefinition.getPredicates());
			routeDefinition.setUri(URI.create(mongoRouteDefinition.getUri()));
			routeDefinition.setOrder(mongoRouteDefinition.getOrder());
			routes.put(routeDefinition.getId(), routeDefinition);
		});
		return Flux.fromIterable(routeDefinitions);
	}

	/**
	 * Add routing information
	 *
	 * @param route route definition object
	 * @return reactor.core.publisher.Mono<java.lang.Void>
	 */
	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return route.flatMap(routeDefinition -> {
			GatewayRouteDefinition mongoRouteDefinition = new GatewayRouteDefinition();
			mongoRouteDefinition.setRouteId(routeDefinition.getId());
			mongoRouteDefinition.setPredicates(routeDefinition.getPredicates());
			mongoRouteDefinition.setFilters(routeDefinition.getFilters());
			mongoRouteDefinition.setUri(routeDefinition.getUri().toString());
			mongoRouteDefinition.setOrder(routeDefinition.getOrder());
			mongoTemplate.save(mongoRouteDefinition, mongoCollection);
			routes.put(routeDefinition.getId(), routeDefinition);
			return Mono.empty();
		});

	}

	/**
	 * Delete routing information
	 *
	 * @param routeId route id
	 * @return reactor.core.publisher.Mono<java.lang.Void>
	 */
	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.flatMap(id -> {
			if (routes.containsKey(id)) {
				routes.remove(id);
				return Mono.empty();
			}
			return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
		});
	}

	private Flux<RouteDefinition> getRoutes() {
		return Flux.fromIterable(routes.values());
	}

}
