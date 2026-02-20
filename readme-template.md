# 🚀 {{PROJECT_NAME}}

> {{PROJECT_DESCRIPTION}}

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-{{JAVA_SOURCE_VERSION}}-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-{{SPRING_BOOT_VERSION}}-6DB33F)
![Version](https://img.shields.io/badge/version-{{PROJECT_VERSION}}-blue)

---

## 📦 Project Metadata

| Property | Value |
|----------|-------|
| **Artifact ID** | `{{PROJECT_ARTIFACT_ID}}` |
| **Group ID** | `{{PROJECT_GROUP_ID}}` |
| **Version** | `{{PROJECT_VERSION}}` |
| **Spring Boot** | `{{SPRING_BOOT_VERSION}}` |

---

## 🔨 Build Information

| Property | Value |
|----------|-------|
| **Build Time** | `{{BUILD_TIME}}` |
| **Java Version** | `{{JAVA_VERSION}}` |
| **Java Home** | `{{JAVA_HOME}}` |
| **OS** | `{{OS_NAME}} ({{OS_ARCH}})` |
| **Built By** | `{{USER_NAME}}` |

---

## 🌿 Git Information

| Property | Value |
|----------|-------|
| **Branch** | `{{GIT_BRANCH}}` |
| **Commit** | `{{GIT_COMMIT}}` |
| **Last Commit Message** | {{GIT_MESSAGE}} |

---

## 🌐 REST API Endpoints

{{ENDPOINTS_TABLE}}

> ℹ️  Actuator endpoints are also available at `/actuator/health`, `/actuator/info`, `/actuator/metrics`

---

## 🚀 Getting Started

### Prerequisites
- Java {{JAVA_SOURCE_VERSION}}+
- Maven 3.8+

### Run the Application

```bash
# Run with Maven dev server
mvn spring-boot:run

# Or run the packaged jar
java -jar target/{{PROJECT_ARTIFACT_ID}}-{{PROJECT_VERSION}}.jar
```

### Build (also auto-regenerates this README!)

```bash
mvn package
```

---

## 📁 Project Structure

```
{{PROJECT_ARTIFACT_ID}}/
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
| `{{PROJECT_NAME}}` | Project display name from pom.xml |
| `{{PROJECT_ARTIFACT_ID}}` | Maven artifact ID |
| `{{PROJECT_VERSION}}` | Current version |
| `{{PROJECT_DESCRIPTION}}` | POM description |
| `{{PROJECT_GROUP_ID}}` | Maven group ID |
| `{{SPRING_BOOT_VERSION}}` | Spring Boot parent version |
| `{{BUILD_TIME}}` | Build timestamp |
| `{{JAVA_VERSION}}` | JVM runtime version |
| `{{JAVA_HOME}}` | JAVA_HOME path |
| `{{OS_NAME}}` | Operating system |
| `{{OS_ARCH}}` | OS architecture |
| `{{USER_NAME}}` | System username |
| `{{GIT_BRANCH}}` | Current git branch |
| `{{GIT_COMMIT}}` | Short commit hash |
| `{{GIT_MESSAGE}}` | Last commit message |
| `{{ENDPOINTS_TABLE}}` | Auto-scanned REST endpoints table |

---

> 📝 **Auto-generated on `{{BUILD_TIME}}` — edit `readme-template.md` to customise, not this file.**
