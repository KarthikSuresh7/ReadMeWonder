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

/**
 * ReadmeGenerator — Plain Java build tool (no Spring dependencies).
 *
 * Compiled and executed by exec-maven-plugin during the 'package' phase.
 * Reads project metadata and writes README.md to the project root.
 *
 * Usage (handled automatically by Maven):
 * javac -d target/readme-gen-classes src/build/java/ReadmeGenerator.java
 * java -cp target/readme-gen-classes ReadmeGenerator [name] [artifactId]
 * [version] [description]
 */
public class ReadmeGenerator {

    // ── ANSI colours for console output ────────────────────────────────────
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) throws Exception {
        System.out.println(CYAN + "\n╔══════════════════════════════════════════╗");
        System.out.println("║       README Auto-Generator v1.0         ║");
        System.out.println("╚══════════════════════════════════════════╝" + RESET);

        // ── 1. Collect metadata ────────────────────────────────────────────
        Path projectRoot = Path.of(System.getProperty("user.dir"));
        Path pomPath = projectRoot.resolve("pom.xml");
        Path templatePath = projectRoot.resolve("readme-template.md");

        Map<String, String> meta = new LinkedHashMap<>();

        // From pom.xml (authoritative source of truth)
        parsePom(pomPath, meta, args);

        // Runtime / environment
        meta.put("BUILD_TIME", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        meta.put("JAVA_VERSION", System.getProperty("java.version"));
        meta.put("JAVA_HOME", System.getProperty("java.home"));
        meta.put("OS_NAME", System.getProperty("os.name"));
        meta.put("OS_ARCH", System.getProperty("os.arch"));
        meta.put("USER_NAME", System.getProperty("user.name"));

        // Git metadata (best-effort)
        meta.put("GIT_BRANCH", gitOutput(projectRoot, "rev-parse", "--abbrev-ref", "HEAD"));
        meta.put("GIT_COMMIT", gitOutput(projectRoot, "rev-parse", "--short", "HEAD"));
        meta.put("GIT_MESSAGE", gitOutput(projectRoot, "log", "-1", "--pretty=%s"));

        // Scanned REST endpoints
        meta.put("ENDPOINTS_TABLE", buildEndpointsTable(projectRoot));

        // ── 2. Fill template ───────────────────────────────────────────────
        String content;
        if (Files.exists(templatePath)) {
            System.out.println(YELLOW + "  Using custom template: readme-template.md" + RESET);
            content = Files.readString(templatePath);
        } else {
            System.out.println(YELLOW + "  No custom template found — using built-in template." + RESET);
            content = builtInTemplate();
        }

        for (Map.Entry<String, String> e : meta.entrySet()) {
            content = content.replace("{{" + e.getKey() + "}}", e.getValue());
        }

        // ── 3. Write README.md ─────────────────────────────────────────────
        Path readmePath = projectRoot.resolve("README.md");
        Files.writeString(readmePath, content, StandardCharsets.UTF_8);

        System.out.println(GREEN + "\n  ✅ README.md generated successfully!");
        System.out.println("     Location : " + readmePath.toAbsolutePath());
        System.out.println("     Project  : " + meta.get("PROJECT_NAME") + " v" + meta.get("PROJECT_VERSION"));
        System.out.println("     Built on : " + meta.get("BUILD_TIME") + RESET + "\n");
    }

    // ── POM Parser ──────────────────────────────────────────────────────────
    private static void parsePom(Path pomPath, Map<String, String> meta, String[] args) {
        // Fallback from CLI args (passed by exec-maven-plugin)
        meta.put("PROJECT_NAME", args.length > 0 ? args[0] : "Unknown");
        meta.put("PROJECT_ARTIFACT_ID", args.length > 1 ? args[1] : "unknown");
        meta.put("PROJECT_VERSION", args.length > 2 ? args[2] : "0.0.1");
        meta.put("PROJECT_DESCRIPTION", args.length > 3 ? args[3] : "");

        if (!Files.exists(pomPath))
            return;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document doc = dbf.newDocumentBuilder().parse(pomPath.toFile());
            doc.getDocumentElement().normalize();

            meta.put("PROJECT_NAME", text(doc, "name", meta.get("PROJECT_NAME")));
            meta.put("PROJECT_ARTIFACT_ID", text(doc, "artifactId", meta.get("PROJECT_ARTIFACT_ID")));
            meta.put("PROJECT_VERSION", text(doc, "version", meta.get("PROJECT_VERSION")));
            meta.put("PROJECT_DESCRIPTION", text(doc, "description", meta.get("PROJECT_DESCRIPTION")));
            meta.put("PROJECT_GROUP_ID", text(doc, "groupId", "com.googleai"));
            meta.put("JAVA_SOURCE_VERSION", text(doc, "java.version", "17"));
            meta.put("SPRING_BOOT_VERSION", parentVersion(doc));
        } catch (Exception e) {
            System.err.println("  ⚠ Could not parse pom.xml: " + e.getMessage());
        }
    }

    private static String text(Document doc, String tag, String fallback) {
        NodeList list = doc.getElementsByTagName(tag);
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getParentNode() != null && "parent".equals(node.getParentNode().getNodeName())) {
                continue;
            }
            String val = node.getTextContent().trim();
            if (!val.isEmpty())
                return val;
        }
        return fallback;
    }

    private static String parentVersion(Document doc) {
        NodeList parents = doc.getElementsByTagName("parent");
        if (parents.getLength() == 0)
            return "N/A";
        Node parent = parents.item(0);
        if (parent.getNodeType() == Node.ELEMENT_NODE) {
            NodeList children = ((Element) parent).getElementsByTagName("version");
            if (children.getLength() > 0)
                return children.item(0).getTextContent().trim();
        }
        return "N/A";
    }

    // ── Git helper ─────────────────────────────────────────────────────────
    private static String gitOutput(Path cwd, String... cmd) {
        try {
            List<String> command = new ArrayList<>();
            command.add("git");
            command.addAll(Arrays.asList(cmd));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(cwd.toFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String out = new String(p.getInputStream().readAllBytes()).trim();
            return out.isEmpty() ? "N/A" : out;
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ── Endpoint Scanner ───────────────────────────────────────────────────
    private static String buildEndpointsTable(Path projectRoot) throws Exception {
        Path srcDir = projectRoot.resolve("src/main/java");
        if (!Files.exists(srcDir))
            return "_No source directory found._";

        // Patterns to match Spring mapping annotations
        Pattern classMapping = Pattern.compile("@RequestMapping\\s*\\(\\s*[\"']?(/[^\"')]+)[\"']?");
        Pattern methodMapping = Pattern.compile(
                "@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\s*\\(?\\s*[\"']?(/[^\"')]*)[\"']?");
        Pattern methodName = Pattern.compile("public\\s+[\\w<>,\\s]+\\s+(\\w+)\\s*\\(");

        StringBuilder table = new StringBuilder();
        table.append("| Method | Endpoint | Handler |\n");
        table.append("|--------|----------|---------|\n");

        List<Path> javaFiles = Files.walk(srcDir)
                .filter(p -> p.toString().endsWith(".java"))
                .sorted()
                .collect(Collectors.toList());

        for (Path file : javaFiles) {
            String src = Files.readString(file);
            String basePath = "";

            Matcher cm = classMapping.matcher(src);
            if (cm.find())
                basePath = cm.group(1).trim();

            Matcher mm = methodMapping.matcher(src);
            while (mm.find()) {
                String httpMethod = mm.group(1).replace("Mapping", "").toUpperCase();
                String path = mm.end() < src.length()
                        ? basePath + mm.group(2).trim()
                        : basePath;
                if (path.isEmpty())
                    path = basePath.isEmpty() ? "/" : basePath;

                // Try to grab the method name that follows
                String handler = "—";
                Matcher mn = methodName.matcher(src.substring(mm.end()));
                if (mn.find())
                    handler = mn.group(1) + "()";

                table.append("| `").append(httpMethod).append("` | `")
                        .append(path).append("` | `")
                        .append(file.getFileName()).append("::").append(handler).append("` |\n");
            }
        }

        return table.length() > 60 ? table.toString() : "_No mapped endpoints found._";
    }

    // ── Built-in Template ─────────────────────────────────────────────────
    private static String builtInTemplate() {
        return """
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

                > ℹ️  Actuator endpoints are available at `/actuator/health`, `/actuator/info`, `/actuator/metrics`

                ---

                ## 🚀 Getting Started

                ### Prerequisites
                - Java {{JAVA_SOURCE_VERSION}}+
                - Maven 3.8+

                ### Run the Application

                ```bash
                # Clone the repository
                git clone <repo-url>
                cd {{PROJECT_ARTIFACT_ID}}

                # Run with Maven
                mvn spring-boot:run

                # Or run the packaged jar
                java -jar target/{{PROJECT_ARTIFACT_ID}}-{{PROJECT_VERSION}}.jar
                ```

                ### Build (also regenerates this README!)

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
                │   │   │   ├── GoogleAiApplication.java      # Main class
                │   │   │   └── controller/
                │   │   │       └── HealthController.java     # REST endpoints
                │   │   └── resources/
                │   │       └── application.properties
                │   └── build/
                │       └── java/
                │           └── ReadmeGenerator.java          # 🔄 This auto-generates README.md
                ├── readme-template.md                        # ✏️  Edit this to customize README layout
                ├── pom.xml
                └── README.md                                 # ✅ Auto-generated — do not edit manually
                ```

                ---

                ## ⚙️ How README Auto-Generation Works

                This README is **automatically regenerated** on every `mvn package` run via:

                1. `exec-maven-plugin` compiles `src/build/java/ReadmeGenerator.java`
                2. Runs `ReadmeGenerator` which:
                   - Parses `pom.xml` for project metadata
                   - Reads runtime info (Java version, OS, build time)
                   - Queries `git` for branch/commit info
                   - Scans `src/main/java` for Spring mapping annotations
                   - Fills placeholders in `readme-template.md` → writes `README.md`

                To **customise the layout**, edit `readme-template.md` using the `{{PLACEHOLDER}}` tokens listed below.

                ### Available Placeholders

                | Placeholder | Description |
                |-------------|-------------|
                | `{{PROJECT_NAME}}` | Project display name |
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
                | `{{ENDPOINTS_TABLE}}` | Auto-scanned REST endpoints |

                ---

                > 📝 **This README was auto-generated on `{{BUILD_TIME}}` — do not edit it manually. Edit `readme-template.md` instead.**
                """;
    }
}
