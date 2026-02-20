# How to Auto-Generate a README.md in Spring Boot (Student Guide)

This guide shows you how to add automatic `README.md` generation to any Spring Boot (Maven) project.

By following this guide, your project will automatically create or update its `README.md` file every time you build the project with `mvn package`. This guarantees your build time, version number, and REST API endpoints are always accurate and up-to-date!

---

## Step 1: Create the ReadmeGenerator Tool

The generator is a **plain Java program** (`public static void main`) that runs during the Maven build string. It does *not* use Spring, so it starts up and finishes in milliseconds.

1. Create a folder inside your project named `src/build/java` (this keeps it separate from your main application code).
2. Create a file named `ReadmeGenerator.java` inside that folder.
3. Paste the following Java code into it:

```java
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ReadmeGenerator {
    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Starting README generation...");
        
        Path projectRoot = Path.of(System.getProperty("user.dir"));
        Path pomPath = projectRoot.resolve("pom.xml");
        Path templatePath = projectRoot.resolve("readme-template.md");
        
        // 1. Gather Metadata
        Map<String, String> meta = new LinkedHashMap<>();
        
        // Fallbacks from Maven POM arguments
        meta.put("PROJECT_NAME", args.length > 0 ? args[0] : "Unknown");
        meta.put("PROJECT_ARTIFACT_ID", args.length > 1 ? args[1] : "unknown");
        meta.put("PROJECT_VERSION", args.length > 2 ? args[2] : "0.0.1");
        meta.put("PROJECT_DESCRIPTION", args.length > 3 ? args[3] : "");
        
        // Read runtime environment info
        meta.put("BUILD_TIME", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        meta.put("JAVA_VERSION", System.getProperty("java.version"));
        
        // Scan for REST Endpoints
        meta.put("ENDPOINTS_TABLE", buildEndpointsTable(projectRoot));
        
        // 2. Read Template
        String content;
        if (Files.exists(templatePath)) {
            content = Files.readString(templatePath);
        } else {
            content = "# {{PROJECT_NAME}}\\n> {{PROJECT_DESCRIPTION}}";
        }
        
        // 3. Replace Placeholders
        for (Map.Entry<String, String> e : meta.entrySet()) {
            content = content.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        
        // 4. Write README.md
        Path readmePath = projectRoot.resolve("README.md");
        Files.writeString(readmePath, content, StandardCharsets.UTF_8);
        
        System.out.println("✅ README.md generated successfully!");
    }

    // Very basic endpoint scanner using Regex
    private static String buildEndpointsTable(Path projectRoot) throws Exception {
        Path srcDir = projectRoot.resolve("src/main/java");
        if (!Files.exists(srcDir)) return "_No source directory found._";

        Pattern methodMapping = Pattern.compile(
                "@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\\\s*\\\\(?\\\\s*[\\"']?(/[^\\"')]*)[\\"']?");

        StringBuilder table = new StringBuilder();
        table.append("| Method | Endpoint |\\n");
        table.append("|--------|----------|\\n");

        List<Path> javaFiles = Files.walk(srcDir)
                .filter(p -> p.toString().endsWith(".java"))
                .sorted()
                .collect(Collectors.toList());

        for (Path file : javaFiles) {
            String src = Files.readString(file);
            Matcher mm = methodMapping.matcher(src);
            while (mm.find()) {
                String httpMethod = mm.group(1).replace("Mapping", "").toUpperCase();
                String path = mm.group(2).trim();
                if (path.isEmpty()) path = "/";
                table.append("| `").append(httpMethod).append("` | `").append(path).append("` |\\n");
            }
        }
        return table.length() > 40 ? table.toString() : "_No mapped endpoints found._";
    }
}
```

---

## Step 2: Configure Maven (`pom.xml`)

Now we need to tell Maven to compile and run our `ReadmeGenerator` tool *after* the `.jar` file is built.

Add this `<plugin>` section exactly inside your `<build><plugins> ... </plugins></build>` block in your project's `pom.xml`:

```xml
<!-- 
    README AUTO-GENERATOR
    Compiles ReadmeGenerator.java and runs it during the 'package' phase.
-->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <!-- Step 1: Compile the generator tool -->
        <execution>
            <id>compile-readme-generator</id>
            <phase>package</phase>
            <goals><goal>exec</goal></goals>
            <configuration>
                <executable>javac</executable>
                <arguments>
                    <argument>-d</argument>
                    <argument>${project.build.directory}/readme-gen-classes</argument>
                    <!-- Adjust to match where you saved the generator -->
                    <argument>${project.basedir}/src/build/java/ReadmeGenerator.java</argument>
                </arguments>
            </configuration>
        </execution>

        <!-- Step 2: Run the generator tool -->
        <execution>
            <id>run-readme-generator</id>
            <phase>package</phase>
            <goals><goal>exec</goal></goals>
            <configuration>
                <executable>java</executable>
                <arguments>
                    <argument>-cp</argument>
                    <argument>${project.build.directory}/readme-gen-classes</argument>
                    <argument>ReadmeGenerator</argument>
                    <!-- Variables passed into public static void main() -->
                    <argument>${project.name}</argument>
                    <argument>${project.artifactId}</argument>
                    <argument>${project.version}</argument>
                    <argument>${project.description}</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## Step 3: Create the Template (`readme-template.md`)

Create a file named **`readme-template.md`** exactly in your project's root folder (next to `pom.xml`). This is the blueprint for your generated README.

```markdown
# 🚀 {{PROJECT_NAME}}

> {{PROJECT_DESCRIPTION}}

**Current Version:** `{{PROJECT_VERSION}}`
**Built On:** `{{BUILD_TIME}}` using Java `{{JAVA_VERSION}}`

---

## 🌐 API Overview
The following endpoints were automatically detected in the project:

{{ENDPOINTS_TABLE}}

---
*Note: This README is automatically generated. Please edit `readme-template.md`, not `README.md`.*
```

---

## Step 4: Add `.gitignore` Rules

We need to tell Git to **ignore** the `README.md` we generate (since it changes on every build) and the compiled output folder for the generator.

Open `.gitignore` and add these two lines at the bottom:
```gitignore
# Auto-generated README files
README.md
target/readme-gen-classes/
```

---

## Step 5: Run the Build!

In your terminal, navigate to your project folder and run:
**(Make sure to use the exact `mvn` path if it is not in your environment variables)**:

```bash
mvn package
```
*(Tip: Use `mvn package -DskipTests` to build it faster).*

Open your project folder, and you will now see a beautifully filled-out `README.md` file! Try adding a new `@GetMapping` in a controller and re-run `mvn package` — watch the `README.md` automatically update with the new endpoint!
