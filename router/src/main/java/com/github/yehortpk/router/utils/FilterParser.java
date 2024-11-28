package com.github.yehortpk.router.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
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
    private String companyMatch;
    private final List<String> multiChoiceMatches = new ArrayList<>();
    private final List<String> negativeMatches = new ArrayList<>();
    private final List<String> mandatoryMatches = new ArrayList<>();
    private final String FILTER_REGEX = "^(.*,)?\\s*(\\([^)]+\\))?\\s*([^\\-\\s,][^\\-]*)?\\s*((?:\\s*-[^\\-\\s]+)+)?\\s*$";

    public FilterParser(String filter) {

        Pattern pattern = Pattern.compile(FILTER_REGEX);
        Matcher matcher = pattern.matcher(filter);

        if (matcher.matches()) {
            companyMatch = matcher.group(1);
            String multiChoice = matcher.group(2);
            String mandatory = matcher.group(3);
            String negative = matcher.group(4);

            if (companyMatch != null) {
                companyMatch = companyMatch.split(",")[0];
            }

            if (mandatory != null) {
                mandatoryMatches.addAll(Arrays.stream(mandatory.split(" ")).map(String::strip).toList());
            }

            if (negative != null) {
                negativeMatches.addAll(Arrays.stream(negative.replace("-", "").split(" "))
                        .map(String::strip).toList());
            }

            if (multiChoice != null) {
                String[] binaryParts = multiChoice
                        .replace("(", "")
                        .replace(")", "")
                        .split("\\|");
                multiChoiceMatches.addAll(Arrays.stream(binaryParts).map(String::strip).toList());
            }
        } else {
            throw  new RuntimeException("Parser couldn't match this filter: " + filter);
        }
    }

    public boolean isVacancyApplicable(String vacancyTitle) {
        if(companyMatch != null && !vacancyTitle.startsWith(companyMatch)) {
            return false;
        }

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
        for (String multiChoiceMatch : multiChoiceMatches) {
            if (vacancyTitle.contains(multiChoiceMatch)) {
                return true;
            }
            result = false;
        }

        return result;
    }
}
