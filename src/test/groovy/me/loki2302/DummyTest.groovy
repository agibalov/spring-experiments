package me.loki2302
import me.loki2302.dto.*
import me.loki2302.entities.Comment
import me.loki2302.entities.CommentCreatedEvent
import me.loki2302.entities.Post
import me.loki2302.entities.PostCreatedEvent
import me.loki2302.entities.User
import me.loki2302.entities.UserCreatedEvent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.validation.Validator

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

@IntegrationTest
@SpringApplicationConfiguration(classes = Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DummyTest {
    @Autowired
    private Facade facade

    @Autowired
    private Validator validator

    private User loki2302
    private Post loki2302Post1
    private Post loki2302Post2
    private Post loki2302Post3
    private Comment loki2302Post1Comment1
    private Comment loki2302Post1Comment2
    private Comment loki2302Post1Comment3
    private Comment loki2302Post1Comment4
    private Comment loki2302Post1Comment5

    private User andrey
    private Post andreyPost1

    @Before
    void populate() {
        loki2302 = facade.makeUser("loki2302")
        loki2302Post1 = facade.makePost(loki2302, "loki2302-post1")
        loki2302Post1Comment1 = facade.makeComment(loki2302, loki2302Post1, "loki2302-post1-comment1")
        loki2302Post1Comment2 = facade.makeComment(loki2302, loki2302Post1, "loki2302-post1-comment2")
        loki2302Post2 = facade.makePost(loki2302, "loki2302-post2")
        loki2302Post1Comment3 = facade.makeComment(loki2302, loki2302Post1, "loki2302-post1-comment3")
        andrey = facade.makeUser("andrey")
        loki2302Post3 = facade.makePost(loki2302, "loki2302-post3")
        loki2302Post1Comment4 = facade.makeComment(loki2302, loki2302Post1, "loki2302-post1-comment4")
        loki2302Post1Comment5 = facade.makeComment(loki2302, loki2302Post1, "loki2302-post1-comment5")
        andreyPost1 = facade.makePost(andrey, "andrey-post1")
    }

    @Test
    void eventsAreOk() {
        def allEvents = facade.findEvents()
        assertTrue(allEvents[0] instanceof UserCreatedEvent)
        assertTrue(allEvents[1] instanceof PostCreatedEvent)
        assertTrue(allEvents[2] instanceof CommentCreatedEvent)
        assertTrue(allEvents[3] instanceof CommentCreatedEvent)
        assertTrue(allEvents[4] instanceof PostCreatedEvent)
        assertTrue(allEvents[5] instanceof CommentCreatedEvent)
        assertTrue(allEvents[6] instanceof UserCreatedEvent)
        assertTrue(allEvents[7] instanceof PostCreatedEvent)
        assertTrue(allEvents[8] instanceof CommentCreatedEvent)
        assertTrue(allEvents[9] instanceof CommentCreatedEvent)
        assertTrue(allEvents[10] instanceof PostCreatedEvent)

        def loki2302Events = facade.findEventsByUser(loki2302)
        assertTrue(loki2302Events[0] instanceof UserCreatedEvent)
        assertTrue(loki2302Events[1] instanceof PostCreatedEvent)
        assertTrue(loki2302Events[2] instanceof CommentCreatedEvent)
        assertTrue(loki2302Events[3] instanceof CommentCreatedEvent)
        assertTrue(loki2302Events[4] instanceof PostCreatedEvent)
        assertTrue(loki2302Events[5] instanceof CommentCreatedEvent)
        assertTrue(loki2302Events[6] instanceof PostCreatedEvent)
        assertTrue(loki2302Events[7] instanceof CommentCreatedEvent)
        assertTrue(loki2302Events[8] instanceof CommentCreatedEvent)

        def andreyEvents = facade.findEventsByUser(andrey)
        assertTrue(andreyEvents[0] instanceof UserCreatedEvent)
        assertTrue(andreyEvents[1] instanceof PostCreatedEvent)
    }

    @Test
    void loki2302IsOk() {
        def loki2302Actual = facade.findUser(loki2302.id)

        def loki2302BriefUserDTO = new BriefUserDTO(
                id: loki2302.id,
                name: 'loki2302',
                postCount: 3,
                commentCount: 5)

        def loki2302Expected = new UserDTO(
                id: loki2302.id,
                name: 'loki2302',
                postCount: 3,
                commentCount: 5,
                recentPosts: [
                        new BriefPostDTO(
                                id: loki2302Post3.id,
                                content: loki2302Post3.content,
                                commentCount: 0,
                                user: loki2302BriefUserDTO,
                                recentComments: []),
                        new BriefPostDTO(
                                id: loki2302Post2.id,
                                content: loki2302Post2.content,
                                commentCount: 0,
                                user: loki2302BriefUserDTO,
                                recentComments: []),
                        new BriefPostDTO(
                                id: loki2302Post1.id,
                                content: loki2302Post1.content,
                                commentCount: 5,
                                user: loki2302BriefUserDTO,
                                recentComments: [
                                        new BriefCommentDTO(
                                                id: loki2302Post1Comment5.id,
                                                content: loki2302Post1Comment5.content,
                                                user: loki2302BriefUserDTO),
                                        new BriefCommentDTO(
                                                id: loki2302Post1Comment4.id,
                                                content: loki2302Post1Comment4.content,
                                                user: loki2302BriefUserDTO),
                                        new BriefCommentDTO(
                                                id: loki2302Post1Comment3.id,
                                                content: loki2302Post1Comment3.content,
                                                user: loki2302BriefUserDTO)
                                ])
                ],
                recentComments: [
                        new BriefCommentDTO(
                                id: loki2302Post1Comment5.id,
                                content: loki2302Post1Comment5.content,
                                user: loki2302BriefUserDTO),
                        new BriefCommentDTO(
                                id: loki2302Post1Comment4.id,
                                content: loki2302Post1Comment4.content,
                                user: loki2302BriefUserDTO),
                        new BriefCommentDTO(
                                id: loki2302Post1Comment3.id,
                                content: loki2302Post1Comment3.content,
                                user: loki2302BriefUserDTO)
                ])

        assertUserDTOEquals(loki2302Expected, loki2302Actual)
    }

    @Test
    void andreyIsOk() {
        def andreyActual = facade.findUser(andrey.id)

        def andreyBriefUserDTO = new BriefUserDTO(
                id: andrey.id,
                name: 'andrey',
                postCount: 1,
                commentCount: 0)

        def andreyExpected = new UserDTO(
                id: andrey.id,
                name: 'andrey',
                postCount: 1,
                commentCount: 0,
                recentPosts: [
                        new BriefPostDTO(
                                id: andreyPost1.id,
                                content: andreyPost1.content,
                                commentCount: 0,
                                user: andreyBriefUserDTO,
                                recentComments: [])
                ],
                recentComments: [])

        assertUserDTOEquals(andreyExpected, andreyActual)
    }

    @Test
    void loki2302Post1IsOk() {
        def loki2302BriefUserDTO = new BriefUserDTO(
                id: loki2302.id,
                name: 'loki2302',
                postCount: 3,
                commentCount: 5)

        def actualPost = facade.findPost(loki2302Post1.id)
        def expectedPost = new PostDTO(
                id: loki2302Post1.id,
                content: loki2302Post1.content,
                user: loki2302BriefUserDTO,
                comments: [
                        new BriefCommentDTO(
                                id: loki2302Post1Comment1.id,
                                content: loki2302Post1Comment1.content,
                                user: loki2302BriefUserDTO),
                        new BriefCommentDTO(
                                id: loki2302Post1Comment2.id,
                                content: loki2302Post1Comment2.content,
                                user: loki2302BriefUserDTO),
                        new BriefCommentDTO(
                                id: loki2302Post1Comment3.id,
                                content: loki2302Post1Comment3.content,
                                user: loki2302BriefUserDTO),
                        new BriefCommentDTO(
                                id: loki2302Post1Comment4.id,
                                content: loki2302Post1Comment4.content,
                                user: loki2302BriefUserDTO),
                        new BriefCommentDTO(
                                id: loki2302Post1Comment5.id,
                                content: loki2302Post1Comment5.content,
                                user: loki2302BriefUserDTO)
                ]
        )

        assertPostDTOEquals(expectedPost, actualPost)
    }

    @Test
    void andreyPost1IsOk() {
        def andreyBriefUserDTO = new BriefUserDTO(
                id: andrey.id,
                name: 'andrey',
                postCount: 1,
                commentCount: 0)

        def actualPost = facade.findPost(andreyPost1.id)
        def expectedPost = new PostDTO(
                id: andreyPost1.id,
                content: andreyPost1.content,
                user: andreyBriefUserDTO,
                comments: []
        )

        assertPostDTOEquals(expectedPost, actualPost)
    }

    @Test
    void listOfPostsIsOk() {
        def loki2302BriefUserDTO = new BriefUserDTO(
                id: loki2302.id,
                name: 'loki2302',
                postCount: 3,
                commentCount: 5)

        def andreyBriefUserDTO = new BriefUserDTO(
                id: andrey.id,
                name: 'andrey',
                postCount: 1,
                commentCount: 0)

        def actualPosts = facade.findAllPosts()
        def expectedPosts = [
                new BriefPostDTO(
                        id: andreyPost1.id,
                        content: andreyPost1.content,
                        commentCount: 0,
                        user: andreyBriefUserDTO,
                        recentComments: []),
                new BriefPostDTO(
                        id: loki2302Post3.id,
                        content: loki2302Post3.content,
                        commentCount: 0,
                        user: loki2302BriefUserDTO,
                        recentComments: []),
                new BriefPostDTO(
                        id: loki2302Post2.id,
                        content: loki2302Post2.content,
                        commentCount: 0,
                        user: loki2302BriefUserDTO,
                        recentComments: []),
                new BriefPostDTO(
                        id: loki2302Post1.id,
                        content: loki2302Post1.content,
                        commentCount: 5,
                        user: loki2302BriefUserDTO,
                        recentComments: [
                                new BriefCommentDTO(
                                        id: loki2302Post1Comment5.id,
                                        content: loki2302Post1Comment5.content,
                                        user: loki2302BriefUserDTO),
                                new BriefCommentDTO(
                                        id: loki2302Post1Comment4.id,
                                        content: loki2302Post1Comment4.content,
                                        user: loki2302BriefUserDTO),
                                new BriefCommentDTO(
                                        id: loki2302Post1Comment3.id,
                                        content: loki2302Post1Comment3.content,
                                        user: loki2302BriefUserDTO)
                        ]),
        ]

        assertBriefPostDTOsEqual(expectedPosts, actualPosts)
    }

    private static void assertPostDTOEquals(PostDTO expected, PostDTO actual) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.content, actual.content)
        assertBriefUserDTOEquals(expected.user, actual.user)
        assertBriefCommentDTOsEqual(expected.comments, actual.comments)
    }

    private static void assertUserDTOEquals(UserDTO expected, UserDTO actual) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.name, actual.name)
        assertEquals(expected.postCount, actual.postCount)
        assertEquals(expected.commentCount, actual.commentCount)
        assertBriefPostDTOsEqual(expected.recentPosts, actual.recentPosts)
        assertBriefCommentDTOsEqual(expected.recentComments, actual.recentComments)
    }

    private static void assertBriefPostDTOsEqual(List<BriefPostDTO> expectedPosts, List<BriefPostDTO> actualPosts) {
        assertEquals(expectedPosts.size(), actualPosts.size())
        expectedPosts.eachWithIndex { BriefPostDTO expected, int i ->
            def actual = actualPosts[i]
            assertBriefPostDTOEquals(expected, actual)
        }
    }

    private static void assertBriefPostDTOEquals(BriefPostDTO expected, BriefPostDTO actual) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.content, actual.content)
        assertEquals(expected.commentCount, actual.commentCount)
        assertBriefUserDTOEquals(expected.user, actual.user)
        assertBriefCommentDTOsEqual(expected.recentComments, actual.recentComments)
    }

    private static void assertBriefCommentDTOsEqual(List<BriefCommentDTO> expectedComments, List<BriefCommentDTO> actualComments) {
        assertEquals(expectedComments.size(), actualComments.size())
        expectedComments.eachWithIndex { BriefCommentDTO expected, int i ->
            def actual = actualComments[i]
            assertBriefCommentDTOEquals(expected, actual)
        }
    }

    private static void assertBriefCommentDTOEquals(BriefCommentDTO expected, BriefCommentDTO actual) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.content, actual.content)
        assertBriefUserDTOEquals(expected.user, actual.user)
    }

    private static void assertBriefUserDTOEquals(BriefUserDTO expected, BriefUserDTO actual) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.name, actual.name)
        assertEquals(expected.postCount, actual.postCount)
        assertEquals(expected.commentCount, actual.commentCount)
    }

    private void assertValid(Object obj) {
        def violations = validator.validate(obj);
        if(violations.isEmpty()) {
            return
        }

        def sb = new StringBuilder();
        for(def violation : violations) {
            sb.append(String.format("\n%s[%s]::%s: %s (was %s)",
                    violation.getRootBeanClass().getSimpleName(),
                    violation.getLeafBean().getClass().getSimpleName(),
                    violation.getPropertyPath(),
                    violation.getMessage(),
                    violation.getInvalidValue()))
        }

        fail(sb.toString())
    }
}
