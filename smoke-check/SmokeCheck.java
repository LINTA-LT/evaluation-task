import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SmokeCheck {

    private static final Pattern LINK = Pattern.compile("<a\\s[^>]*href=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern SESSION_ID = Pattern.compile(";jsessionid=[0-9A-Za-z]+");
    private static final Pattern SCRIPT_STYLE_COMMENT = Pattern.compile("(?is)<script.*?</script>|<style.*?</style>|<!--.*?-->");
    private static final Pattern ISO_DATE_LINE = Pattern.compile("text: \\d{4}-\\d{2}-\\d{2}");
    private static final Pattern TAG = Pattern.compile("(?s)<[^>]*>");
    private static final Pattern ROW = Pattern.compile("(?s)<row[ >].*?</row>");
    private static final Pattern CELL = Pattern.compile("(?s)<c\\s([^>]*?)(?:/>|>(.*?)</c>)");
    private static final Pattern CELL_VALUE = Pattern.compile("(?s)<v>(.*?)</v>");
    private static final Pattern INLINE_TEXT = Pattern.compile("(?s)<t[^>]*>(.*?)</t>");
    private static final Pattern SHARED_ITEM = Pattern.compile("(?s)<si>(.*?)</si>");

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || !List.of("record", "check").contains(args[0])) {
            System.err.println("Usage: java SmokeCheck.java record|check [baseUrl] [name ...]");
            System.err.println("Default baseUrl: http://localhost:8080/pdb");
            System.exit(2);
        }
        boolean record = args[0].equals("record");
        String baseUrl = args.length > 1 ? args[1] : "http://localhost:8080/pdb";
        List<String> only = args.length > 2 ? List.of(args).subList(2, args.length) : List.of();

        Path requestsFile = findRequestsFile(Path.of(System.getProperty("user.dir")));
        Path expectedDir = requestsFile.getParent().resolve("expected");
        Files.createDirectories(expectedDir);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        int passed = 0;
        int failed = 0;
        for (String line : Files.readAllLines(requestsFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\t", 2);
            String name = parts[0];
            String path = parts[1];
            if (!only.isEmpty() && !only.contains(name)) {
                continue;
            }
            List<String> actual;
            try {
                actual = fetchAndNormalise(client, baseUrl + "/" + path);
            } catch (Exception e) {
                System.out.println("FAIL  " + name + "  request failed: " + e);
                failed++;
                continue;
            }
            Path expectedFile = expectedDir.resolve(name + ".txt");
            if (record) {
                Files.write(expectedFile, actual, StandardCharsets.UTF_8);
                System.out.println("REC   " + name + "  (" + actual.size() + " lines)");
                continue;
            }
            if (!Files.exists(expectedFile)) {
                System.out.println("FAIL  " + name + "  no recorded response " + expectedFile);
                failed++;
                continue;
            }
            List<String> expected = maskUpdatedDate(Files.readAllLines(expectedFile, StandardCharsets.UTF_8));
            actual = maskUpdatedDate(actual);
            if (name.endsWith("-unordered")) {
                expected = expected.stream().map(text -> text.replaceFirst("^row: [0-9.]+ \\| ", "row: ")).sorted().toList();
                actual = actual.stream().map(text -> text.replaceFirst("^row: [0-9.]+ \\| ", "row: ")).sorted().toList();
            }
            if (expected.equals(actual)) {
                System.out.println("OK    " + name);
                passed++;
            } else {
                System.out.println("FAIL  " + name);
                printDifferences(expected, actual);
                failed++;
            }
        }
        if (!record) {
            System.out.println();
            System.out.println(passed + " passed, " + failed + " failed");
            System.exit(failed == 0 ? 0 : 1);
        }
    }

    private static Path findRequestsFile(Path workingDir) {
        for (Path dir : List.of(workingDir, workingDir.resolve("smoke-check"))) {
            Path file = dir.resolve("requests.txt");
            if (Files.exists(file)) {
                return file;
            }
        }
        throw new IllegalStateException("requests.txt not found; run from the smoke-check directory or the project root");
    }

    private static List<String> fetchAndNormalise(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(120))
                .header("Accept-Language", "en")
                .GET()
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        String contentType = response.headers().firstValue("Content-Type").orElse("").split(";")[0].trim();
        List<String> lines = new ArrayList<>();
        lines.add("status: " + response.statusCode());
        lines.add("content-type: " + contentType);
        if (response.statusCode() != 200) {
            return lines;
        }
        if (contentType.contains("spreadsheetml")) {
            lines.addAll(normaliseWorkbook(response.body()));
        } else if (contentType.contains("html")) {
            String origin = url.substring(0, url.indexOf('/', url.indexOf("//") + 2));
            lines.addAll(normaliseHtml(new String(response.body(), StandardCharsets.UTF_8).replace(origin, "")));
        } else {
            lines.add("size: " + response.body().length);
        }
        return lines;
    }

    private static List<String> maskUpdatedDate(List<String> lines) {
        List<String> masked = new ArrayList<>(lines);
        for (int i = 2; i < masked.size(); i++) {
            if (masked.get(i - 2).equals("text: Updated")
                    && masked.get(i - 1).equals("text: :")
                    && ISO_DATE_LINE.matcher(masked.get(i)).matches()) {
                masked.set(i, "text: <updated date>");
            }
        }
        return masked;
    }

    private static List<String> normaliseHtml(String html) {
        List<String> lines = new ArrayList<>();
        String withoutNoise = SCRIPT_STYLE_COMMENT.matcher(html).replaceAll(" ");
        Matcher links = LINK.matcher(withoutNoise);
        while (links.find()) {
            lines.add("link: " + SESSION_ID.matcher(links.group(1)).replaceAll(""));
        }
        String text = TAG.matcher(withoutNoise).replaceAll("\n");
        for (String line : text.split("\n")) {
            String cleaned = line.replace("&nbsp;", " ").replaceAll("\\s+", " ").trim();
            if (!cleaned.isEmpty()) {
                lines.add("text: " + cleaned);
            }
        }
        return lines;
    }

    private static List<String> normaliseWorkbook(byte[] bytes) throws IOException {
        List<String> sharedStrings = new ArrayList<>();
        String sheet = "";
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals("xl/sharedStrings.xml")) {
                    Matcher items = SHARED_ITEM.matcher(new String(zip.readAllBytes(), StandardCharsets.UTF_8));
                    while (items.find()) {
                        StringBuilder value = new StringBuilder();
                        Matcher texts = INLINE_TEXT.matcher(items.group(1));
                        while (texts.find()) {
                            value.append(unescape(texts.group(1)));
                        }
                        sharedStrings.add(value.toString());
                    }
                } else if (entry.getName().equals("xl/worksheets/sheet1.xml")) {
                    sheet = new String(zip.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        List<String> lines = new ArrayList<>();
        Matcher rows = ROW.matcher(sheet);
        while (rows.find()) {
            List<String> cells = new ArrayList<>();
            Matcher cellMatcher = CELL.matcher(rows.group());
            while (cellMatcher.find()) {
                String attributes = cellMatcher.group(1);
                String body = cellMatcher.group(2) == null ? "" : cellMatcher.group(2);
                cells.add(cellText(attributes, body, sharedStrings));
            }
            lines.add("row: " + String.join(" | ", cells));
        }
        return lines;
    }

    private static String cellText(String attributes, String body, List<String> sharedStrings) {
        Matcher value = CELL_VALUE.matcher(body);
        if (attributes.contains("t=\"s\"") && value.find()) {
            int index = Integer.parseInt(value.group(1).trim());
            return index < sharedStrings.size() ? sharedStrings.get(index) : "";
        }
        if (attributes.contains("t=\"inlineStr\"")) {
            Matcher inline = INLINE_TEXT.matcher(body);
            return inline.find() ? unescape(inline.group(1)) : "";
        }
        return value.find() ? unescape(value.group(1)) : "";
    }

    private static String unescape(String text) {
        return text.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
    }

    private static void printDifferences(List<String> expected, List<String> actual) {
        int shown = 0;
        int limit = 5;
        int max = Math.max(expected.size(), actual.size());
        for (int i = 0; i < max && shown < limit; i++) {
            String e = i < expected.size() ? expected.get(i) : "<missing>";
            String a = i < actual.size() ? actual.get(i) : "<missing>";
            if (!e.equals(a)) {
                System.out.println("      line " + (i + 1) + " expected: " + shorten(e));
                System.out.println("      line " + (i + 1) + " actual:   " + shorten(a));
                shown++;
            }
        }
        System.out.println("      expected " + expected.size() + " lines, got " + actual.size());
    }

    private static String shorten(String text) {
        return text.length() <= 160 ? text : text.substring(0, 157) + "...";
    }
}
