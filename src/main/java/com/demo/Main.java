package com.demo;

import com.demo.tools.CourseTools;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Main {

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .build();

        CourseTools tools = new CourseTools();

        CourseAssistant assistant = AiServices.builder(CourseAssistant.class)
                .chatModel(chatModel)
                .tools(tools)
                .build();

        String reply = assistant.toolChat("What is the duration of Java Course and Price of Java course");
        System.out.println(reply);
    }
}
