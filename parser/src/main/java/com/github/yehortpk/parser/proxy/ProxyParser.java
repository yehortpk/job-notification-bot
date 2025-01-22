package com.github.yehortpk.parser.proxy;

import java.net.Proxy;
import java.util.List;

public interface ProxyParser {
    List<Proxy> parseProxies();
}
