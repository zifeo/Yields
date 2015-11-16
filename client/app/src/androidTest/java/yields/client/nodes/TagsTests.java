package yields.client.nodes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test the tag creation and if they can correctly be added to groups
 */
public class TagsTests {
    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectTagWithSpace(){
        Group.Tag tag = new Group.Tag("space space");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectTagUpperCase(){
        Group.Tag tag = new Group.Tag("Hello");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectTagTooShort(){
        Group.Tag tag = new Group.Tag("a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectTagTooLong(){
        Group.Tag tag = new Group.Tag("a_very_long_text_without_any_spaces_at_all");
    }

    @Test
    public void testCorrectTagInGroup(){
        Group.Tag tag = new Group.Tag("nature");
        Group.Tag copyTag = new Group.Tag("nature");
        Group g1 = new Group("Nature gurus", new Id(11), new ArrayList<User>());
        g1.addTag(tag);

        assertTrue(g1.matchToTag(tag));
        assertTrue(g1.matchToTag(copyTag));
    }

    @Test
    public void testCorrectTagsInGroup(){
        Group.Tag tag1 = new Group.Tag("nature");
        Group.Tag copyTag1 = new Group.Tag("nature");

        Group.Tag tag2 = new Group.Tag("wilderness");
        Group.Tag copyTag2 = new Group.Tag("wilderness");

        Group.Tag tag3 = new Group.Tag("animals");
        Group.Tag copyTag3 = new Group.Tag("animals");

        Group g1 = new Group("Nature gurus2", new Id(11), new ArrayList<User>());
        g1.addTag(tag1);
        g1.addTag(tag2);
        g1.addTag(tag3);

        assertTrue(g1.matchToTag(tag1));
        assertTrue(g1.matchToTag(copyTag1));
        assertTrue(g1.matchToTag(tag2));
        assertTrue(g1.matchToTag(copyTag2));
        assertTrue(g1.matchToTag(tag3));
        assertTrue(g1.matchToTag(copyTag3));

        List<Group.Tag> tags = g1.getTagList();
        assertTrue(tags.contains(tag1));
        assertTrue(tags.contains(copyTag1));
        assertTrue(tags.contains(tag2));
        assertTrue(tags.contains(copyTag2));
        assertTrue(tags.contains(tag3));
        assertTrue(tags.contains(copyTag3));

        assertEquals(tags.size(), 3);
    }
}
