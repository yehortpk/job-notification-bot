package com.github.yehortpk.parser.models;

import lombok.*;

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
