# LangChain4j Tool Calling Demo

A minimal Java example showing how an LLM can call real application code ("tool calling" /
function calling) using [LangChain4j](https://docs.langchain4j.dev/). The model doesn't know
course prices or durations — it asks a Java method for them, gets a real answer back, and
turns that into a natural-language reply.

## What this demonstrates

- **Tool calling / function calling** with the `@Tool` annotation — marking plain Java methods
  as functions an LLM can invoke.
- **LangChain4j AI Services** (`AiServices.builder(...)`) — a declarative interface
  (`CourseAssistant`) instead of manually building prompts and parsing responses.
- **System/user prompt templating** via `@SystemMessage` and `@UserMessage`.
- A clean separation between the *agent* (LLM + prompts) and the *tools* (deterministic,
  testable Java methods) it can call.

## How it works

```
User question
     │
     ▼
CourseAssistant.toolChat(message)   <-- AI Service interface, backed by OpenAI chat model
     │
     ▼
LLM decides it needs course data and calls a @Tool method
     │
     ▼
CourseTools.getCoursePrice(courseName) / getCourseDuration(courseName)
     │
     ▼
Tool result is fed back to the LLM, which composes the final natural-language answer
```

| Class | Role |
|---|---|
| `Main` | Wires up the `OpenAiChatModel` (reads API key from env) and the `CourseAssistant`, sends a sample question. |
| `CourseAssistant` | AI Service interface. `@SystemMessage` sets the assistant's behavior/guardrails; `@UserMessage("{{it}}")` forwards the caller's input. |
| `CourseTools` | Plain Java class with two `@Tool`-annotated methods (`getCoursePrice`, `getCourseDuration`) that the LLM can call. Currently backed by in-memory data for `Java`, `Python`, and `AI` courses. |

## Example

Input (see `Main.java`):

```
What is the duration of Java Course and Price of Java course
```

The LLM calls `getCourseDuration("Java")` and `getCoursePrice("Java")` behind the scenes, then
replies with something like:

```
The Java course lasts 3 months and costs 3000 INR.
```

## Prerequisites

- JDK 21+
- Maven 3.8+
- An [OpenAI API key](https://platform.openai.com/api-keys)

## Setup & run

1. Set your API key as an environment variable (never hardcode it in source):

   ```bash
   # macOS / Linux
   export OPENAI_API_KEY=your-key-here

   # Windows PowerShell
   $env:OPENAI_API_KEY = "your-key-here"
   ```

2. Run it:

   ```bash
   mvn compile exec:java
   ```

   (or run `Main.java` directly from your IDE, once `OPENAI_API_KEY` is set in its run
   configuration.)

## Project structure

```
src/main/java/com/demo/
├── Main.java              # entry point: builds the chat model & assistant, asks a question
├── CourseAssistant.java   # AI Service interface (system prompt + user message template)
└── tools/
    └── CourseTools.java   # @Tool methods the LLM can call for price/duration lookups
```

## Notes

- This is a learning/demo project, not a production service — the course "database" is just
  if/else logic in `CourseTools`. Swapping it for a real data source wouldn't change the
  tool-calling pattern.
- Model used: `gpt-4o-mini` (configurable in `Main.java`).
