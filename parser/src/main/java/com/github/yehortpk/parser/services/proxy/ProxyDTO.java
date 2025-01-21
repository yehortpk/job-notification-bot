package com.github.yehortpk.parser.services.proxy;

import lombok.*;

/**
 * DTO representing an information about the proxy
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class ProxyDTO {
    @ToString.Include
    @EqualsAndHashCode.Include
    private String proxyHost;
    @EqualsAndHashCode.Include
    @ToString.Include
    private int proxyPort;
    private String countryCode;
    private String countryTitle;
    private String anonymity;
    private boolean isGoogle;
    private boolean isHTTPS;
    private String lastChecked;
}
