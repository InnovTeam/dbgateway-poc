package com.sample.gateway.poc.dbgatewaypoc;

import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

/**
   * Custom routing information stored in mongodb
 *
 * @author lkf
 * @date 2018-11-29 10-51
 */
@Data
public class GatewayRouteDefinition {

    /**
           * route id
     */
    @Field("route_id")
    private String routeId;
    /**
           * Routing predicates
     */
    private List<PredicateDefinition> predicates = new ArrayList<>();
    /**
           * filter 
     */
    private List<FilterDefinition> filters = new ArrayList<>();
    /**
           * Jump address uri
     */
    private String uri;
    /**
           * Routing order
     */
    private int order;

}

