package com.demo.tools;

import dev.langchain4j.agent.tool.Tool;

import java.util.Locale;

public class CourseTools {

    //@Tool is LangChain4j's annotation that
    // marks a Java method as a function the LLM can call — this is "tool calling"/function calling

    @Tool("Get the price of a training course")
    public String getCoursePrice(String courseName){

        System.out.println("Tool called: getCoursePrice, courseName=" + courseName);

        switch (normalize(courseName)) {
            case "java": return "3000 INR";
            case "python": return "2500 INR";
            case "ai": return "4500 INR";
            default: return "Course price not available";
        }
    }

    @Tool("Get the duration of a training course")
    public String getCourseDuration(String courseName){

        System.out.println("Tool called: getCourseDuration, courseName=" + courseName);

        switch (normalize(courseName)) {
            case "java": return "3 months";
            case "python": return "2 months";
            case "ai": return "4 months";
            default: return "Course duration not available";
        }
    }

    // Strips an optional trailing "course" so "Java", "Java Course" and "java course" all match the same key.
    private String normalize(String courseName) {
        return courseName.trim().toLowerCase(Locale.ROOT).replace(" course", "");
    }
}
