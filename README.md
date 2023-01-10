# 💪🏽 About ProgressPro

📈 Fitness trainers can keep track of their clients' progress with ProgressPro. The app is designed specifically for fitness instructors and allows them to easily monitor and track their clients' performance over time.

🏋🏽 Instructors can input and track their clients' weight and sizes, as well as set fitness goals and monitor progress towards achieving them.  

⏰ This app is perfect for busy fitness instructors who want to stay organized and on top of their clients' progress without spending hours on paperwork and manual tracking.

# 🚀 ProgressPro API (Ktor)

ProgressPro (also known as ProgressP) is a lightweight app that empowers fitness instructors to change lives.

This is a real-world example of implementing a web server using [Kotlin](https://kotlinlang.org/). The codebase uses the popular [Ktor](https://ktor.io/) framework, and it demonstrates the simplicity and efficiency of Kotlin when it comes to building web applications.

The API server exposes a set of endpoints for CRUD operations on a resource, and it also includes examples of handling HTTP requests, parsing JSON, and handling exceptions.

## 💻 Development

We recommend using IntelliJ for developing, running and testing the app functionalities.

The application was built with:

- [Ktor 2.2.1](https://github.com/ktorio/ktor) as web framework
- [Koin 3.2.2](https://github.com/InsertKoinIO/koin) as dependency injection framework
- [Jackson 2.14](https://github.com/FasterXML/jackson-module-kotlin) for data serialization/deserialization
- [HikariCP 5.0](https://github.com/brettwooldridge/HikariCP) as datasource abstraction of driver implementation
- [Postgres 42.5](https://github.com/postgres/postgres) as database
- [Exposed 0.40](https://github.com/JetBrains/Exposed) as SQL framework to persistence layer

### 📁 Project Structure

      + gradle/
          Gradle wrapper properties
      + resources/
          Configuration files for logback and the API server
      + src/
        + api
            Defining accessibile routes
        + config
            All app setups.
        + database
            Database setup
        + models
            Persistence layer and tables definition
        + service
            Logic layer for transforming data
        + util
            Jwt, Handlers, Exception classes (& others)
        - Main.kt <- The main class
      + test/
          Tests for the implemented functionalities


## 🔨 Building

    ./gradlew clean build

## ▶️ Running

    ./gradlew run
