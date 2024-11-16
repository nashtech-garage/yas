# Yas AI
Yas AI is a project that explores the power of **[Generative AI](https://en.wikipedia.org/wiki/Generative_artificial_intelligence)** through **[Spring AI](https://spring.io/projects/spring-ai)**. This project demonstrates various use cases for applying Generative AI to enhance application functionality.

## Features

### Product Recommendation
#### Similar Search
Implements vector-based search to identify and recommend similar products, leveraging the capabilities of Generative AI and Spring AI. This feature can help users discover products related to their preferences based on semantic similarity.
##### How data come to vector database?
Let's say we have a product and recommendation database. 
Our purpose is to make sure they are consistent, if product have any changes, 
recommendation must sync them. 

We are using Kafka Connect, Debezium to do that, below is the flow how product data come to 
recommendation.

<p style="text-align: center"><img src="docs/vector-search.svg"></p>

1. There are changes on product data.
2. Debezium, Kafka Connect sent changes event to kafka topic (configured from kafka connect).
3. The **recommendation** service consumes message from the kafka topic.
4. The **recommendation** service send product data (in text) to 
OpenAI provider (Azure OpenAI, Ollama, etc) to get product embedding value.
5. OpenAI provider responses the embedding value.
6. The **recommendation** service saved product and its embedding value to database.

The utilization of Spring AI is in step **No4**, and **No5**. 
It's help us to integrate with OpenAI provider

##### How recommendation service handle data
We assume that you have known the data structure which SpringAI define. 

Generally, by default data will be saved as below table.
<p style="text-align: center"><img src="docs/img.png"></p>


For simple functionality, using the guide from SpringAI we can create simple implementation as we need.

But the idea is the **vector_store** table is too generic, so we need to create our own **layer** 
to support our needs:

- Custom Content Formatter.
- Custom Id Generator.
- Make data entity oriented.

![img_1.png](docs/img_1.png)

---

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





