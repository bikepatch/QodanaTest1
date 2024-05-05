package com.company;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer {
    private final String filePath;

    public CodeAnalyzer(String filePath) {
        this.filePath = filePath;
    }

    public void analyzeCode() throws IOException {
        Map<String, Integer> complexityMap = new LinkedHashMap<>();
        List<String> methodNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentMethod = null;
            int curlyBraceCount = 0;
            boolean insideClass = false;

            Pattern classPattern = Pattern.compile("\\s*class\\s+\\w+\\s*\\{");
            Pattern methodPattern = Pattern.compile("(public|protected|private)?\\s+static\\s+(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{");
            Pattern bracePattern = Pattern.compile("\\{");
            Pattern endBracePattern = Pattern.compile("\\}");

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!insideClass) {
                    Matcher classMatcher = classPattern.matcher(line);
                    if (classMatcher.find()) {
                        insideClass = true;
                        curlyBraceCount++;
                        continue;
                    }
                }

                Matcher methodMatcher = methodPattern.matcher(line);
                if (methodMatcher.find() && curlyBraceCount == 1) {
                    methodNames.add(methodMatcher.group(3));
                    currentMethod = methodMatcher.group(0);
                    complexityMap.put(currentMethod, 0);
                    curlyBraceCount++;
                }

                if (currentMethod != null) {
                    if (line.contains("for") || line.contains("while")) {
                        complexityMap.put(currentMethod, complexityMap.get(currentMethod) + 1);
                    }
                }

                Matcher braceMatcher = bracePattern.matcher(line);
                while (braceMatcher.find()) {
                    curlyBraceCount++;
                }

                Matcher endBraceMatcher = endBracePattern.matcher(line);
                while (endBraceMatcher.find()) {
                    curlyBraceCount--;
                    if (curlyBraceCount == 2 && currentMethod != null) {
                        Integer complexityScore = complexityMap.get(currentMethod);
                        curlyBraceCount--;
                        String complexity = complexityScore > 1 ? "O(n^2)" : "O(n)";
                        System.out.println(currentMethod.split("\\s*\\(")[0] + " - Complexity: " + complexity);
                        currentMethod = null;
                    }
                }

                if (curlyBraceCount == 0 && insideClass) {
                    insideClass = false;
                }
            }

            long nonCamelCaseCount = methodNames.stream()
                    .filter(name -> Character.isLowerCase(name.charAt(0)) && !name.contains("_"))
                    .count();
            double nonCamelCasePercentage = 100.0 * nonCamelCaseCount / methodNames.size();
            System.out.println("Percentage of methods in non camelCase: " + nonCamelCasePercentage + "%");
        }
    }
}