# LangChain4j Tool Calling Demo

A collection of minimal Java examples showing how an LLM can call real application code
("tool calling" / function calling) using [LangChain4j](https://docs.langchain4j.dev/).
The model doesn't actually know course prices, course durations, or the current time
anywhere in the world — it asks a Java method for the real answer, gets it back, and
turns that into a natural-language reply.

## What this demonstrates

- **Tool calling / function calling** with the `@Tool` annotation — marking plain Java
  methods as functions an LLM can invoke.
- **Parameter hints** with `@P(...)` — telling the LLM what a tool's parameter actually
  means, so it extracts the right value from free-form user text.
- **LangChain4j AI Services** (`AiServices.builder(...)`) — declarative interfaces
  (`CourseAssistant`, `DateTimeAssistant`) instead of manually building prompts and
  parsing responses.
- **System/user prompt templating** via `@SystemMessage` and `@UserMessage`.
- A clean separation between the *agent* (LLM + prompts) and the *tools* (deterministic,
  testable Java methods) it can call.
- Two independent tool-calling demos in the same project: a course Q&A assistant and a
  real-time date/time assistant.

## How it works

```
User question
     │
     ▼
AI Service interface (e.g. CourseAssistant / DateTimeAssistant)  <-- backed by OpenAI chat model
     │
     ▼
LLM decides it needs real data and calls a @Tool method, extracting
the arguments (course name, location, ...) straight from the user's text
     │
     ▼
Your real Java method runs (e.g. CourseTools.getCoursePrice, DateTimeTools.getCurrentDateTime)
     │
     ▼
Tool result is fed back to the LLM, which composes the final natural-language answer
```

Note: `AiServices.builder(...).build()` returns a dynamic proxy — you never call the
`@Tool` methods directly yourself. The proxy calls them via reflection whenever the LLM's
response asks for a specific tool + arguments.

## Demo 1 — Course price & duration assistant

| Class | Role |
|---|---|
| `Main` | Wires up the `OpenAiChatModel` (reads API key from env) and the `CourseAssistant`, sends a sample question. |
| `CourseAssistant` | AI Service interface. `@SystemMessage` sets the assistant's behavior/guardrails; `@UserMessage("{{it}}")` forwards the caller's input. |
| `CourseTools` | Plain Java class with two `@Tool`-annotated methods (`getCoursePrice`, `getCourseDuration`) that the LLM can call. Backed by in-memory data for `Java`, `Python`, and `AI` courses. |

**Example** — input (see `Main.java`):

```
What is the duration of Java Course and Price of Java course
```

The LLM calls `getCourseDuration("Java")` and `getCoursePrice("Java")` behind the scenes,
then replies with something like:

```
The Java course lasts 3 months and costs 3000 INR.
```

## Demo 2 — Real-time date & time assistant

| Class | Role |
|---|---|
| `LaunchMainTool2` | Wires up the `OpenAiChatModel` and the `DateTimeAssistant`, sends a sample question. |
| `DateTimeAssistant` | AI Service interface instructing the model to always use the tool and never guess the date/time. |
| `DateTimeTools` | `@Tool`-annotated `getCurrentDateTime(location)` that maps a city/postal code to a real `ZoneId` and returns the actual current time via `ZonedDateTime.now(...)` — not something the model made up. |

**Example** — input (see `LaunchMainTool2.java`):

```
Tell me the current date and time in Bengaluru
```

Runtime log trail (proves the real Java clock was used, not an LLM guess):

```
Tool called: getCurrentDateTime
Location received: Bengaluru
The current date and time in Bengaluru is Monday, 16 November 2026, 12:15:41 AM IST.
```

Supported locations are defined in `DateTimeTools.ZONES` (city names and postal codes for
Bengaluru/Bangalore, Mumbai, New York, Los Angeles, London, Tokyo, and Dubai).

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

2. Run the course assistant demo:

   ```bash
   mvn compile exec:java
   ```

   Run the date/time assistant demo:

   ```bash
   mvn compile exec:java -Dexec.mainClass=com.demo.LaunchMainTool2
   ```

   (or run `Main.java` / `LaunchMainTool2.java` directly from your IDE, once
   `OPENAI_API_KEY` is set in its run configuration.)

## Project structure

```
src/main/java/com/demo/
├── Main.java                   # entry point for Demo 1 (course assistant)
├── CourseAssistant.java        # AI Service interface for Demo 1
├── LaunchMainTool2.java        # entry point for Demo 2 (date/time assistant)
├── aiservice/
│   └── DateTimeAssistant.java  # AI Service interface for Demo 2
└── tools/
    ├── CourseTools.java        # @Tool methods for price/duration lookups
    └── DateTimeTools.java      # @Tool method for real-time date/time lookups
```

## Notes

- This is a learning/demo project, not a production service — the course "database" and
  the city-to-timezone map are both just in-memory lookups. Swapping either for a real
  data source wouldn't change the tool-calling pattern.
- Model used: `gpt-4o-mini` (configurable in `Main.java` / `LaunchMainTool2.java`).
