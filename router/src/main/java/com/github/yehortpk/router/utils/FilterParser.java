package com.github.yehortpk.router.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for parsing filter into matches categories.
 * <p>Mandatory matches - word has to be presented in the input</p>
 * <p>Negative matches - word must not be presented in the input</p>
 * <p>Binary matches - one of the words from (word1|word2...) has to be presented in the input</p>
 */
@Getter
public class FilterParser {
    private final List<String[]> binaryMatches = new ArrayList<>();
    private final List<String> negativeMatches = new ArrayList<>();
    private final List<String> mandatoryMatches = new ArrayList<>();
    private final String FILTER_REGEX = "\\(([^)]+)\\)|-([\\w-]+)|(\\w+)";

    public FilterParser(String filter) {

        Pattern pattern = Pattern.compile(FILTER_REGEX);
        Matcher matcher = pattern.matcher(filter.toLowerCase());

        while (matcher.find()) {
            String binary = matcher.group(1);
            String negative = matcher.group(2);
            String defaultElement = matcher.group(3);

            if (defaultElement != null) {
                mandatoryMatches.add(defaultElement);
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

    public boolean isVacancyApplicable(String vacancyTitle) {
        vacancyTitle = vacancyTitle.toLowerCase();

        for (String negativeMatch : negativeMatches) {
            if (vacancyTitle.contains(negativeMatch)) {
                return false;
            }
        }

        for (String defaultMatch : mandatoryMatches) {
            if (!vacancyTitle.contains(defaultMatch)) {
                return false;
            }
        }

        boolean result = true;
        for (String[] binaryMatch : binaryMatches) {
            for (String match : binaryMatch) {
                if (vacancyTitle.contains(match)) {
                    return true;
                }
            }
            result = false;
        }

        return result;
    }
}
