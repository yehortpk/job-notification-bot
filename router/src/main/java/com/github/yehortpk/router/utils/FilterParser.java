package com.github.yehortpk.router.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class FilterParser {
    private final List<String[]> binaryMatches = new ArrayList<>();
    private final List<String> negativeMatches = new ArrayList<>();
    private final List<String> defaultMatches = new ArrayList<>();

    public FilterParser(String filter) {
        String regex = "\\(([^)]+)\\)|-([\\w-]+)|(\\w+)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filter.toLowerCase());

        while (matcher.find()) {
            String binary = matcher.group(1);
            String negative = matcher.group(2);
            String defaultElement = matcher.group(3);

            if (defaultElement != null) {
                defaultMatches.add(defaultElement);
            }

            if (negative != null) {
                negativeMatches.add(negative);
            }

            if (binary != null) {
                String[] binaryParts = binary
                        .replace("(", "")
                        .replace(")", "")
                        .split("\\|");
                binaryMatches.add(binaryParts);
            }
        }
    }
}
