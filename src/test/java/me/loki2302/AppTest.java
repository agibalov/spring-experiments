package me.loki2302;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = App.class)
public class AppTest {
    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void createNoteSuccess() throws Exception {
        App.CreateNoteRequestDto createNoteRequestDto = new App.CreateNoteRequestDto();
        createNoteRequestDto.title = "my note";
        createNoteRequestDto.description = "my description";

        ConstraintDescriptions requestConstraints =
                new ConstraintDescriptions(App.CreateNoteRequestDto.class, new MyConstraintDescriptionResolver());

        String titleConstraints = collectionToDelimitedString(requestConstraints.descriptionsForProperty("title"), ". ");

        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createNoteRequestDto)))
                .andExpect(status().isOk())
                .andDo(document("createNoteSuccess",
                        preprocessRequest(prettyPrint(), removeHeaders("Host", "Content-Length")),
                        preprocessResponse(prettyPrint(), removeHeaders("Content-Length")),
                        myDummySnippet(),
                        responseFields(
                                fieldWithPath("id").description("Note id"),
                                fieldWithPath("title").description("Note title"),
                                fieldWithPath("description").description("Note description")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .description("Note title")
                                        .attributes(key("constraints").value(titleConstraints)),
                                fieldWithPath("description").description("Note description")
                        )));
    }

    @Test
    public void createNoteValidationError() throws Exception {
        App.CreateNoteRequestDto createNoteRequestDto = new App.CreateNoteRequestDto();
        createNoteRequestDto.title = "";
        createNoteRequestDto.description = "";

        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createNoteRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(document("createNoteValidationError",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("error").description("Error details"),
                                fieldWithPath("errorFields").description("Map of field names and errors")
                        ),
                        requestFields(
                                fieldWithPath("title").description("Note title"),
                                fieldWithPath("description").description("Note description")
                        )));
    }

    private static class MyConstraintDescriptionResolver implements ConstraintDescriptionResolver {
        @Override
        public String resolveDescription(Constraint constraint) {
            String constraintName = constraint.getName();
            if(constraintName.equals(NotBlank.class.getCanonicalName())) {
                return "Should not be blank";
            }

            return constraint.getName();
        }
    }

    private static MyDummySnippet myDummySnippet() {
        return new MyDummySnippet();
    }

    private static class MyDummySnippet extends TemplatedSnippet {
        public MyDummySnippet() {
            super("dummy", null);
        }

        @Override
        protected Map<String, Object> createModel(Operation operation) {
            Map<String, Object> model = new HashMap<>();
            model.put("operationName", operation.getName());
            model.put("requestUri", operation.getRequest().getUri());
            model.put("responseStatus", operation.getResponse().getStatus());
            return model;
        }
    }
}
