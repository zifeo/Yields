package yields.client.nodes;

import org.junit.Test;

import yields.client.node.Group;

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
}
