package com.demo;

import com.demo.aiservice.DateTimeAssistant;
import com.demo.tools.DateTimeTools;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class LaunchMainTool2 {
    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .build();

        DateTimeTools tools = new DateTimeTools();

        //Registering Tool
        DateTimeAssistant assistant = AiServices.builder(DateTimeAssistant.class)
                .chatModel(model)
                .tools(tools)
                .build();

       // String resonse = model.chat("Tell me the current date and time in Bengaluru");
        String resonse = assistant.chat("Tell me the current date and time in Bengaluru");
        System.out.println(resonse);
    }
}
