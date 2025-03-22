package org.bbsk.mysender.api.alphavantage.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alpha-vantage")
public record AlphaVantageRecord(String key) {}
