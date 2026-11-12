package com.demo;

//In Langchain4j there two options when interacting with AI, 1st is creating everything manual and 2nd is AI Services which automate.
//Automate in the sense that if LLM is requesting for some tool to execute and the response LLM get automatically
// and the result will be converted to natural language and sent to user
//We are using this assistant of AI Service which enables the automate

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface CourseAssistant {

    @SystemMessage("""
            You are helpful course assistant.
            Use available tools to answer course
            price and duration questions.
            Do not invent course details.
            """)
    @UserMessage("{{it}}")      //here what ever message arguments comes, it will be added to "it" by default. it is builtin predefined placeholder in lanchain4j
    String toolChat(String message);
}
