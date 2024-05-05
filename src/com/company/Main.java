package com.company;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            CodeAnalyzer analyzer = new CodeAnalyzer("src/com/company/test.java");
            analyzer.analyzeCode();
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
