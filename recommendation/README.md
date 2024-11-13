# Yas AI
Yas AI is a project that explores the power of **[Generative AI](https://en.wikipedia.org/wiki/Generative_artificial_intelligence)** through **[Spring AI](https://spring.io/projects/spring-ai)**. This project demonstrates various use cases for applying Generative AI to enhance application functionality.

## Features

### Product Recommendation
#### Similar Search
Implements vector-based search to identify and recommend similar products, leveraging the capabilities of Generative AI and Spring AI. This feature can help users discover products related to their preferences based on semantic similarity.

### AI Assistant
#### Intelligent Assistant Capabilities
Yas AI includes an intelligent assistant with several advanced AI functionalities:

- **Chat Completion**: Supports contextual chat completions chat.
- **Embedding**: Uses embeddings to process user's request and similar product search.
- **Function Calling**: Enables dynamic function calling based on AI-powered prompts.
- **Prompting**: Supports complex prompt designs for more refined AI interactions.

## Tech Stack

Yas AI utilizes the following technologies:

- **Java**
- **Spring Boot**
- **Spring AI**
- **Apache Kafka**
- **PostgreSQL with pgvector**
- **Debezium**
- **Azure OpenAI API**

---
# How to start

**1. Start needed instances:**
Run below comment to start needed instances.
```bash
docker-compose -f docker-compose.yml up postgres kafka kafka-connect product media kafka-ui -d
```

**Start recommendation:**

- Start along with docker-compose:

You can also include **recommendation** to above command, ensure that you have provided your **OpenAI** correctly 

**_.env_**
```properties
SPRING_AI_AZURE_OPENAI_API_KEY=${SPRING_AI_AZURE_OPENAI_API_KEY}
SPRING_AI_AZURE_OPENAI_ENDPOINT=${SPRING_AI_AZURE_OPENAI_ENDPOINT}
SPRING_AI_AZURE_OPENAI_EMBEDDING_OPTIONS_MODEL=${SPRING_AI_AZURE_OPENAI_EMBEDDING_OPTIONS_MODEL}
```
- Start locally:

Ensure that you have provided below configuration correctly:

_**recommendation/src/main/resources/application.properties**_
```properties
spring.ai.azure.openai.api-key=${SPRING_AI_AZURE_OPENAI_API_KEY}
spring.ai.azure.openai.endpoint=${SPRING_AI_AZURE_OPENAI_ENDPOINT}
spring.ai.azure.openai.embedding.options.model=${SPRING_AI_AZURE_OPENAI_EMBEDDING_OPTIONS_MODEL}
```

**3. Start create kafka connector:**
```bash
./start-source-connectors.sh
```

**4. Init data:**
Run these 2 sql script to init some data:
```text
sampledata/src/main/resources/db/media/media.sql
sampledata/src/main/resources/db/product/product.sql
```

**5. Verify:**
After finishing above steps, let's verify following things
- Check kafka-ui (default run on localhost:8089), if CDC events are created.
- Check recommendation database, if vector data are saved.





