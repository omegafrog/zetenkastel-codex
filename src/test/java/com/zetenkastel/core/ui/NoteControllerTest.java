package com.zetenkastel.core.ui;

import com.zetenkastel.main.ZetenkastelApplication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ZetenkastelApplication.class)
@AutoConfigureMockMvc
class NoteControllerTest {

    private static Path storageRoot;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        try {
            storageRoot = Files.createTempDirectory("zettel-notes-");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        registry.add("note.storage.root", () -> storageRoot.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetStorage() throws IOException {
        if (storageRoot == null) {
            return;
        }

        try (var walk = Files.walk(storageRoot)) {
            walk.filter(path -> !path.equals(storageRoot))
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }

        for (String dir : new String[]{
                "inbox", "fleeting-notes", "literature-notes", "projects", "area",
                "archives", "maps-of-content", "references", "templates", "attachments"
        }) {
            Files.createDirectories(storageRoot.resolve(dir));
        }
    }

    @Test
    void shouldSupportCrudSearchBacklinksRecommendationsAndGraph() throws Exception {
        String seedA = """
                {
                  "type":"inbox",
                  "fileName":"a",
                  "title":"Spring Graph Basics",
                  "tags":["spring","graph"],
                  "content":"spring boot graph and links",
                  "links":[]
                }
                """;
        String seedB = """
                {
                  "type":"projects",
                  "fileName":"b",
                  "title":"Graph Project Plan",
                  "tags":["graph","plan"],
                  "content":"project planning with graph connections",
                  "links":["inbox/a"]
                }
                """;

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(seedA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("inbox/a"));

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(seedB))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("projects/b"));

        String updateA = """
                {
                  "type":"inbox",
                  "fileName":"a",
                  "title":"Spring Graph Advanced",
                  "tags":["spring","graph","advanced"],
                  "content":"advanced link analysis",
                  "links":[]
                }
                """;

        mockMvc.perform(put("/api/notes/inbox/a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Graph Advanced"));

        mockMvc.perform(get("/api/notes/inbox/a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("a"));

        mockMvc.perform(get("/api/notes/search").param("q", "advanced"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("inbox/a"));

        mockMvc.perform(get("/api/notes/inbox/a/backlinks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("projects/b"));

        mockMvc.perform(get("/api/notes/inbox/a/recommendations"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes.length()").value(2))
                .andExpect(jsonPath("$.edges.length()").value(1));

        mockMvc.perform(delete("/api/notes/projects/b"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
