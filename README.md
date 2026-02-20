# 🚀 GoogleAi

> Spring Boot project with Google AI integration and dynamic README auto-generation

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F)
![Version](https://img.shields.io/badge/version-0.0.1-SNAPSHOT-blue)

---

## 📦 Project Metadata

| Property | Value |
|----------|-------|
| **Artifact ID** | `google-ai` |
| **Group ID** | `com.googleai` |
| **Version** | `0.0.1-SNAPSHOT` |
| **Spring Boot** | `3.2.3` |

---

## 🔨 Build Information

| Property | Value |
|----------|-------|
| **Build Time** | `2026-02-21 01:15:17` |
| **Java Version** | `21.0.9` |
| **Java Home** | `C:\Program Files\Java\jdk-21` |
| **OS** | `Windows 11 (amd64)` |
| **Built By** | `skart` |

---

## 🌿 Git Information

| Property | Value |
|----------|-------|
| **Branch** | `fatal: not a git repository (or any of the parent directories): .git` |
| **Commit** | `fatal: not a git repository (or any of the parent directories): .git` |
| **Last Commit Message** | fatal: not a git repository (or any of the parent directories): .git |

---

## 🌐 REST API Endpoints

| Method | Endpoint | Handler |
|--------|----------|---------|
| `GET` | `/api/v1/health` | `HealthController.java::health()` |
| `GET` | `/api/v1/info` | `HealthController.java::info()` |
| `POST` | `/api/v1/echo` | `HealthController.java::echo()` |
| `GET` | `/api/v1/greet/{name}` | `HealthController.java::greet()` |
| `GET` | `/hello` | `HelloWorld.java::hello()` |


> ℹ️  Actuator endpoints are also available at `/actuator/health`, `/actuator/info`, `/actuator/metrics`

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application

```bash
# Run with Maven dev server
mvn spring-boot:run

# Or run the packaged jar
java -jar target/google-ai-0.0.1-SNAPSHOT.jar
```

### Build (also auto-regenerates this README!)

```bash
mvn package
```

---

## 📁 Project Structure

```
google-ai/
├── src/
│   ├── main/
│   │   ├── java/com/googleai/
│   │   │   ├── GoogleAiApplication.java
│   │   │   └── controller/
│   │   │       └── HealthController.java
│   │   └── resources/
│   │       └── application.properties
│   └── build/
│       └── java/
│           └── ReadmeGenerator.java     # 🔄 Runs on every mvn package
├── readme-template.md                   # ✏️  Edit this to customize the layout
├── pom.xml
└── README.md                            # ✅ Auto-generated — do not edit manually
```

---

## ⚙️ How README Auto-Generation Works

This README is **automatically regenerated** on every `mvn package` via `exec-maven-plugin`.

### Available Template Placeholders

| Placeholder | Description |
|-------------|-------------|
| `GoogleAi` | Project display name from pom.xml |
| `google-ai` | Maven artifact ID |
| `0.0.1-SNAPSHOT` | Current version |
| `Spring Boot project with Google AI integration and dynamic README auto-generation` | POM description |
| `com.googleai` | Maven group ID |
| `3.2.3` | Spring Boot parent version |
| `2026-02-21 01:15:17` | Build timestamp |
| `21.0.9` | JVM runtime version |
| `C:\Program Files\Java\jdk-21` | JAVA_HOME path |
| `Windows 11` | Operating system |
| `amd64` | OS architecture |
| `skart` | System username |
| `fatal: not a git repository (or any of the parent directories): .git` | Current git branch |
| `fatal: not a git repository (or any of the parent directories): .git` | Short commit hash |
| `fatal: not a git repository (or any of the parent directories): .git` | Last commit message |
| `| Method | Endpoint | Handler |
|--------|----------|---------|
| `GET` | `/api/v1/health` | `HealthController.java::health()` |
| `GET` | `/api/v1/info` | `HealthController.java::info()` |
| `POST` | `/api/v1/echo` | `HealthController.java::echo()` |
| `GET` | `/api/v1/greet/{name}` | `HealthController.java::greet()` |
| `GET` | `/hello` | `HelloWorld.java::hello()` |
` | Auto-scanned REST endpoints table |

---

> 📝 **Auto-generated on `2026-02-21 01:15:17` — edit `readme-template.md` to customise, not this file.**
