package com.sample.gateway.poc.dbgatewaypoc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Custom Gateway Attribute Entity
*
* @author lkf
* @date 2018-11-29 13-13
*/
@Data
@ConfigurationProperties(prefix = "cmp.gateway.route")
public class CmpGatewayProperties {
  private String collection;
}
