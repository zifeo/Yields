package yields.client.messages;

import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import yields.client.activities.MessageActivity;
import yields.client.activities.MockFactory;
import yields.client.exceptions.ContentException;
import yields.client.generalhelpers.MockModel;
import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Tests which test the methods of the classes in the messages package.
 */
public class MessageClassTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static TextContent MOCK_TEXT_CONTENT_1;
    private static final String JSON_MESSAGE = "[\"2015-11-17T00:30:16.276+01:00\", \"117\", \"null\", \"MESSAGE_TEXT\" ]";

    public MessageClassTests() {
        super(MessageActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        YieldsApplication.setResources(getInstrumentation().getContext().getResources());
        new MockModel();
        MOCK_TEXT_CONTENT_1 = new TextContent("Mock text.");
    }

    /**
     * Tests a Message's getSender() method.
     */
    @Test
    public void testMessageHasCorrectSender() {
        YieldsApplication.setUser(new ClientUser("test", new Id(-1), "test@epfl.ch",
                YieldsApplication.getDefaultUserImage()));
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Message message = new Message(new Id(-4), user.getId(),
                MockFactory.generateFakeTextContent(1), new Date());
        assertEquals(user.getId(), message.getSender());
    }

    /**
     * Tests a Message's getDate() method.
     */
    @Test
    public void testMessageHasCorrectDate() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Date date = new Date();
        Message message = new Message(new Id(-4), user.getId(),
                MockFactory.generateFakeTextContent(1), date);
        assertEquals(0, message.getDate().compareTo(date));
    }

    /**
     * Tests a Message's getContent() method.
     */
    @Test
    public void testMessageHasCorrectContent() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Content content = MockFactory.generateFakeTextContent(1);
        Message message = new Message( new Id(-4), user.getId(), content, new Date());
        assertEquals(content, message.getContent());
    }

    /**
     * Tests a Message's getStatus() method.
     */
    @Test
    public void testMessageHasCorrectStatus() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Content content = MockFactory.generateFakeTextContent(1);
        Message message = new Message(new Id(-4), user.getId(), content, new Date(),
                Message.MessageStatus.NOT_SENT);
        assertEquals(Message.MessageStatus.NOT_SENT, message.getStatus());

        message = new Message( new Id(-4), user.getId(), content, new Date(),
                Message.MessageStatus.SENT);
        assertEquals(Message.MessageStatus.SENT, message.getStatus());

        message = new Message(new Id(-4), user.getId(), content, new Date(),
                Message.MessageStatus.SEEN);
        assertEquals(Message.MessageStatus.SEEN, message.getStatus());
    }

    /**
     * Tests a TextContent's getType() method.
     */
    @Test
    public void testTextContentHasCorrectType() {
        assertEquals(Content.ContentType.TEXT, MOCK_TEXT_CONTENT_1.getType());
    }

    /**
     * Tests a ImageContent's getType() method.
     */
    @Test
    public void testImageContentHasCorrectType() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        assertEquals(Content.ContentType.IMAGE, imageContent.getType());
    }

    /**
     * Tests a TextContent's getText() method.
     */
    @Test
    public void testTextContentReturnsCorrectText() {
        assertEquals("Mock text.", MOCK_TEXT_CONTENT_1.getText());
    }

    /**
     * Test an ImageContent's getCaption() method.
     */
    @Test
    public void testImageContentReturnsCorrectCaption() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        assertEquals("Mock caption", imageContent.getCaption());
    }

    /**
     * Tests an ImageContent's getImage() method.
     */
    @Test
    public void testImageContentReturnsCorrectImage() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        assertTrue(imageContent.getImage().sameAs(bitmap));
    }

    /**
     * Tests a TextContent's getView() method.
     */
    @Test
    public void testTextContentReturnsCorrectLayout() {
        View view = MOCK_TEXT_CONTENT_1.getView();
        TextView text = (TextView) view;
        assertEquals("Mock text.", text.getText());
    }

    /**
     * Tests an ImageContent's getView() method.
     */
    @Test
    public void testImageContentReturnsCorrectLayout() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        LinearLayout view = null;
        try {
            view = (LinearLayout) imageContent.getView();
        } catch (ContentException e) {
            e.printStackTrace();
        }
        //Caption from TextView
        TextView caption = null;
        if (view != null) {
            caption = (TextView) view.getChildAt(0);
        } else {
            fail("View was incorrect !");
        }
        assertEquals("Mock caption", caption.getText());
    }

    @Test
    public void testMessageFromStringsParsedInResponseTextContent() {
        String dateTime = "2015-12-09T22:11:13.147+01:00";
        long contentId = 420;
        long senderID = 2;
        String text = "420blZit";
        String contentType = "text";
        String content = "";

        Message message = null;
        try {
            message = new Message(dateTime, contentId, senderID, text, contentType, content);
        } catch (ParseException e) {
            fail("Fail to parse text message");
        }

        assertEquals(Long.valueOf(2), message.getSender().getId());
        try {
            assertEquals(DateSerialization.dateSerializer.toDate(dateTime), message.getDate());
        } catch (ParseException e) {
            fail("Fail to serialize date");
        }
        assertEquals("GCC : 420blZit", message.getPreview());
        assertEquals(Message.MessageStatus.SENT, message.getStatus());
    }

    @Test
    public void testMessageFromStringsParsedInResponseUrlContent() {
        String dateTime = "2015-12-09T22:11:13.147+01:00";
        long contentId = 420;
        long senderID = 2;
        String text = "420blZit www.reddit.com";
        String contentType = "url";
        String content = "www.reddit.com";

        Message message = null;
        try {
            message = new Message(dateTime, contentId, senderID, text, contentType, content);
        } catch (ParseException e) {
            fail("Fail to parse text message");
        }

        assertEquals(Long.valueOf(2), message.getSender().getId());
        try {
            assertEquals(DateSerialization.dateSerializer.toDate(dateTime), message.getDate());
        } catch (ParseException e) {
            fail("Fail to serialize date");
        }
        assertEquals("GCC : 420blZit www.reddit.com", message.getPreview());
        assertEquals(Message.MessageStatus.SENT, message.getStatus());
    }

    @Test
    public void testMessageFromStringsParsedInResponseImageContent() {
        String dateTime = "2015-12-09T22:11:13.147+01:00";
        long contentId = 420;
        long senderID = 2;
        String text = "420blZit www.reddit.com";
        String contentType = "image";
        String content = "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsK" +
                "CwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQU" +
                "FBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wgARCABvAKsDAREA" +
                "AhEBAxEB/8QAHAAAAwACAwEAAAAAAAAAAAAAAAECAwcEBggF/8QAHAEBAQACAwEBAAAAAAAAAAAA" +
                "AAECAwQFBgcI/9oADAMBAAIQAxAAAAHtPcfPpTV3U+61r0/vFSGgNlCNKBOx8rod7+j+UkgCAACq" +
                "4wao6f6Dr3qfcpUxAtCZAoVn29/TegfTfHQAAABdc9Z7amHTuB6frXC9HFgjJpwkapHcfo7eD6F9" +
                "P8Vu4g7GgAjzR5H79MrLx2hNxVxVVEIyhMUx5jOstWS4bZ735d23n+ZAA80+R+/RMqMkK8QtzYsV" +
                "oSAULEzya+arEneu18PtnvflggB5s8j+hImVRVU4Yc3ByIx1AVwchc8c23r5app9Xf1W+PV/CKYg" +
                "J5u8j+iJlcWZrw4EZCBDEC4m+8OUlaOzfHrPgf1eR1YI84+O/RWPGurY/Rx4ufFFQZsXGyMyHDyv" +
                "EvIrHkAxG1+/+Td57TxAKvN/jv0TOOTsbHK4zGUBjFRCMN5GTXygdCdu7Dx+4vQfInjS5ebPJ/oO" +
                "ZkGaKvDEyHL12quT5+xhtDFORWHJFdBzNnB9Den+E3lrJPNHlv0FMtGXCrPjsQSkpRYpkMlTw3Jm" +
                "0ZNw3x6P4p9vldKZYea/J/oSJaMuGwWbFcUuTHOLi7MuGebHZx89EspZNi1m4bQ7j5l2nf5/Nt06" +
                "A899vjGxlr+po5/b+s9LRUyy47MuOYy52vkKBl87fxeh9r5HibOOUAm1+z+bfW2dbjNLdX9Wwyxs" +
                "0d86X2vYuD3hYS1MlcMOzRydXIplIMuJs43Qe58ZwNvDLEx3B2fzb6mfAnF//8QALBAAAQIFAgUE" +
                "AgMBAAAAAAAAAQACAwQQERIFEwYUMTNBICEiQhUyFiUwNf/aAAgBAQABBQJzjlkVxnEdbcctx6MR" +
                "63XW3HrccjEetx63HrccuHYzhquRWRWRWRWRWRWRWRTv2XGRvFr4oV5pobsdU/w/mUPIcYwVrupw" +
                "9UiePenvZXrdXWnnCdDxbILJX9MTuLxTovFCjUITEVq5yOCNQmVw5OGakfRE7i8UtdOhYrFWVlZW" +
                "Vq+VwpMbc36H9xfWrX2W4Ag4ZveC3IO9PlaZH5afabir+6vqjD+IaScSjDsMaYkrEoodV5ppkfmJ" +
                "KsTuL6HqPduSfE+PglAjJz7O3WhRjchXQ6q64WmdyUrE7ll9CflQDIkGGS9xC61b1XmnDUfb1Cyx" +
                "KxUTuon41Z1xs2HbDIARfZ9HfsF5pJxuXmmOyZdZKJ7RFb41uutL2V71NB6NJmhG0+96RO4vrQ9P" +
                "AribCG8gSsYqJDcw+nRDbT2RbLmSondRctPgc5Mfh5dfipZDTZZDT5cLloTUGNLsLVm5Rs02Zgul" +
                "4no0Zn9cWWVioncyRWjS+1LDovPSjm2TTkFakxLw5lk3JulHCukf88r2X//EADARAAECBAQFAQcF" +
                "AAAAAAAAAAEAAgMEERIQEyAxBQYhMEEUFSJCUVJhkSQyNENx/9oACAEDAQE/AS41VxXLXXMKoFRU" +
                "VFQKiIVFQKi4wP0T1cVcVcVcVcVcVcVcUT1w5ZHuRCh2uKNukon+diqPLMUno8I8sR/DwuEcPiSD" +
                "HNf57c4LpaIPsUWOCodNcBpqq6im7oy0F27B+EZSX+gfheglT/WPwuYJQSs37goDpG2LtlcVcVcV" +
                "cVeVcVeVcUHFDfRzTL3y7Yo8YVVVVDF23ZbvgMeKQPUycSH5oj0NNAxdt2RvgMXdehXE4Hp5t8P7" +
                "6BiduyEUD00c0y2XNZo86Bidu0U3bRzLLZspf9KtKtViGLh7vbZ+3ROwhHl3wz5Ce2xxCqqoYnqF" +
                "llZRWWUIRWUVlLLWWCsoYM20OXFJYwZt7R80ahdUMRgcBjQlNY/5LKiH4U9jmmhTdtBXGhWcdROh" +
                "XCi9KhgXUUlCMzEt8L2dCXs+Chw+EPC9FCHwr0sIfChBZXoEIIQhoQ1MSTYwUaE6A612grinWbcg" +
                "1WhDArhMuGQczyVRU6qiIVKrYpp0R4LIzaOCmJZ0u77IYEriP8p6Cqv/xAAoEQABAwIFBAIDAQAA" +
                "AAAAAAABAAIRAyAEEBIwURMUITEzQSIjMkD/2gAIAQIBAT8BhQsd9beG+UbeO9jbofKNnvm8Lvmc" +
                "LE1hVII26Xh4KBH+bqO5XUfyuq/lYSp1Kfnb97eAd+WnchR42aD9FQHcnaHhUHamA7MIZ+VChC7A" +
                "vlmm2bvd5swdTTUjlSFK1bEo3GymdLgUDIlQtNn2pU5znKnI2BUXg0whsFHOUXBa28prgbsOf1hB" +
                "8FdYZwqzhTbK7h67h67h6NZ/K6jj9rUeVqUqVTqlhTHB4kW0jDApU5j0sU+XabJzIsZUcz0qdQVL" +
                "GfwM/wD/xAAwEAAAAwUFBwQCAwAAAAAAAAAAAQIDESAxMhAhMHGREiIzQVFhgQRSkqFCYrHR4f/a" +
                "AAgBAQAGPwI7xMenIlHz5io9RUeorPUVK1FR6io9RUeoqPUVHqKj1DDeO/uJiYmJiYmJiYOxgXY8" +
                "P05y3sFT2C59RexWELZpMtknb2GwP9yExONWdh4lzRZeRxmnyHHafIb6tpaTcb4VZxTflhrZe4nw" +
                "qzin0H+hX9gwco2K+T74V5wPeWQkJCbz6CWEyX1KBedh2Ipd9ibvIN38hNMvImDvB32TiNnzQcCs" +
                "7DgdZPC2OSygVnEeQk7qCeLpC7AZNPaoEdqs8J+CxMzv2bVZ4UhQrQcNWgcpJkfeJDw8SCs7HAkn" +
                "TM3CStRQeo4f2OEkXMkaB2yReIHHcrkY2VlCyHQVBZ97dv8AJcb4HKIX3p5HAxyt/8QAJRAAAgIB" +
                "BAIDAQADAAAAAAAAAAERITEQQWFxUYGRobEg0fDx/9oACAEBAAE/IckZ8nI+SKBdV0GO/wC20CQo" +
                "tDenbYcyiftrb2Llmvgcj5OR8nKOUco5Zyjl/J9jRGUaq9oejUk+AqWmXA3syTpZWFSJ1pP8yOGQ" +
                "aZUg66K7QshtZdhzhnRbVj9gm9rRLwTc/Q86VIvNKMvY+lTgEPIpEkk6NPfJ0lo3AvsJrYwZciS+" +
                "NNxiH9Y5/lAItwr2F9NbMy4JJJJPs9Er6wZLA9Zgoh4Og+JHUW6NjHYy2yQsRu0SSSSZ/I2EWtUc" +
                "Dt8DaVqG64CjpSxIxO/Y0IOU31qsvrRMK3wTi4SPR0Q7RJJJ6khKjDjYxLnX5EAl4GjcRLiz3JeR" +
                "HAnHCwpgXgYjUpqHowTAWSYcou+0z3/HpS/RteRMGob0qK32IpKsPFpFKUrVQE5u2ODIXMaSmd35" +
                "JvCJm+izTcO12NSmooTIkdbDy0RVaGdK27s1w/4x4qRESgKGa1ehMiD5ymhqTki/A5to8EkiZCFU" +
                "BOdH8de0T0JlExu/RMUnW5m1rdW7ZIgpLBVvYeM1OIknGycMSKmJqVaySQpLW6EyvJgfPxA30IQY" +
                "akqS8FHy/dGl43MIkkwmyRg3/A3sy8mdGs8mQlo8oXtoT7Qthksx35/pkf7N9HE3HyMeBNo79E6T" +
                "PYwPzyHx7IPIqkT312Iltp+yCkbP9jLt5/pFDI+Q6lKpQocoaUVZx9mMeUZRo4Cgm7TSC2IXSG4q" +
                "S8SYaJ1yPZ7MTzwImzdDH0PPbNoxyBp5j9OKGLD5J4EsPCgVloJP4HNKVq0RvkO9i6JLippPdEPZ" +
                "OwGOm6EkvidD/9oADAMBAAIAAwAAABAKbkGwQmfNto4FvCrIJySSAM7P1dKljQk6JYTVfJ3YMkko" +
                "uw774Vbt/wCKt2wU2F6LBLA1ugS27OZJNG1sUkEmBPQYqYygGm8zsZcKBTunsL2C89CDInYqweZ+" +
                "Lilf+VCaSKmfGfXX/OcvM2ZGa4+w6wVd/F//xAAnEQADAAEEAQMEAwEAAAAAAAAAARExECFBYVEg" +
                "MJFxgbHRocHh8P/aAAgBAwEBPxChudhdO+EJ3AlXA1zCPAncCRcCLgjwdA08FXriflEOTuO47DsO" +
                "w7DsEUVDPqF+GYaN6LI36G0ORXb4aG77Co+MxVWz7/oc2m2T26Qk4RjTIyPR6PGnfv4ChVoRrcrK" +
                "ysorG2WqiQ4NChOnI9UIcMVT4D9FU/ifoeT8YZHBprx2UpSmIlJBozO0XkO8Xn0WcNJhx6z0fkzx" +
                "/R/6N8+gNUtcns4jDGqQ9GoFyL6rctjcFRUVegyok9meTBDyciJXgPBSafTOrMFrvYvsZIyFJDLu" +
                "rEK2VfK2eroiaVMDwZvZWdG5R505Ey5a/ZlN9NHBaPc3D0otbqsjMByJQYkK0HMeHBtwNzBaq2pa" +
                "bFExyb2Rzyd2LaUgNQ4B59BV21P53E2GoUYLXDRahYmtpvgk2TGlU/wLGYiGMWC7iaGZC3+n4Qqx" +
                "ChQwWjWkFd9k3YkpNpnQzBFbNoJCwJokiu0EWQQQcj4Y+J+5buItEFTa/wC2QmHcNsiwfelUX6Rb" +
                "BowoZsm1B5xLVFHgWzg3B/u7Jh7tgRk3IOD7T8/0eBHg/8QAIBEAAwADAQADAQEBAAAAAAAAAAER" +
                "ECExQSBRYTBxkf/aAAgBAgEBPxBaYPJQmysrKysrKysrKx2jaIiIiIiIiIhPRR/+Yjn8Xaj9l/hL" +
                "TcT/AEI5kLsl/i3+khpdGxMqKioqKhkw+m/BJlxCT4c4JPoX3BI9DKNWiERERD7huODEh6KmE0VG" +
                "i5ePMRd/V8Xh9NPLV2Nn0baEaEmsvHmLPwtVRs2bH0u4NUVKjXaJrwVFw/00vS4ajPMNVP8ALfg+" +
                "k3hD6xs3RHR6uhXxD0NkbMSYarykn0ot1CZUVDKsN+iy9H0YlHBua+PWfDUOB20SsH0g+l3Mvgmp" +
                "VEmLmbTo8wkmNSeMkIJH0gfcelmDYbHFhaIcYJ06PC4abHJfCGukHjjH3CtE+BqvRF7aGrwIqmPb" +
                "PCCwhKNkIn0mzY+1j9EfoNno3sephxtDf7PsyCi8whWVtjECyB9wlH45DdYnhXrHVCDJCFHKbC3X" +
                "S4XcRKY//8QAJxABAAICAgEDBQEBAQEAAAAAAQARITFBUWFxkaGBscHR8RDh8CD/2gAIAQEAAT8Q" +
                "8x3LuI/vQAmsUGeUeH337hZ/5PeOMCje37iygW9v3KYPvsz+q/cUB7LffHMF0MnF7z+2/czn3X7l" +
                "vPQ7o2/M/vp/fR4/fZ/fZ/fZ/dZ/fZ/cS/1X3iocy/1pdMf8GCw4gVKxeKbiyuC7OoaBb8zc136i" +
                "UEpSt9RpLy3c0gXFpsSvmAjMsgO5Z3Llncs7lIvP7gunfEovbhfi4Q6wAVbYpZcDgxBfBv4ikx4q" +
                "u46FBG4Kg9Kyk5MVRWVjyivDHN9ytG/aGwpzDIApdDCUAjXcKr+SCRwm5eWlpb3CoN1b7s1qFuTr" +
                "UAAxDioWTSxUYYXpEseYgFen0lQONr8zAauFca9IgKYRESOaJxvzKm2XXH8zziwvn3jiGOtG0vOH" +
                "4lO5TxKeJTxENNDlr1Yy7eNmt/5hxUuCgu3iceJdlqyyAYGeKiclsbIA39CxTDfeZTzEje6hHmxc" +
                "whdxrk6eIFSvVMubrHufD8QGP/gERFXf7s5PJBa4b4mvKMPtHUXoJTPZcSYWQCArTHGC4scICqPe" +
                "azKDRFBxrOKPSOGWFqz/AOsNRFHFTDi6SyrlVG28kWQ+jzBZCWHyPvCXFJZLdkt2S8uqNpj3Z7iX" +
                "6FJZT5iIFFLZF9QMbLNOXUerZfECu0IB2nOhmsnPUaqWx2QhyaDNRNI45MSxAOEpifPZAXeoClcO" +
                "iI5YggSkbGWMHFeAU/J/jqBiVGU1uto5CnpBeTJFr4bgbwAKlG0b/SC5RbT3+IVDaFY+ccS8BgA4" +
                "wKvuMlusE1g/LcCyuQq7y13lgVylODBD7y+HVAFvCXjiXYRTd+WXO7Xb9I3k6xFSFw5PBGss0wOq" +
                "X6S64YJzsPzD/BZkvbHW2clF7g0sZh01kRxAOpRBJLdtB5i1YDIWJGTK2kSlixySwrK5b5gXxmBv" +
                "y1AYgiaeMzM4zcuyyuSbuvEsvysFWya8bD4uPCY7gxdf54WqGH6omHiWgBJiIA0PMuiDZDQteqHU" +
                "7OC+SjUEWyEihyoH0ipsS0gLmq5c/EE4kCvGJdS7JabeOYkoWHJEwW7Llkpzw8RulmoFkER5A/Fx" +
                "B7GHsghVSiEZVV/lNERgWjP1iFBg4IU/5NL4jlpYdcwzNq89x5b8S7CUoSYktryhQs57l5riUH6f" +
                "eNFnuIFe9k3N4eOohWYMQOuGUBENfA/aUrvPE8nxLDlvw2lWqYk8fvHNOpg4lyoH1mezn1m9Y53F" +
                "aR2ru/E1uHhNZhwB2fqiIPXVfonPyFR4hR2UeZWpbrMKsmnuJZGrMlncO7asdXiVkDd4YMXBCRkH" +
                "5SjYxBGhQlpZWkBmuj1YTJh28/EpCx5WfmKFc8j8wJFDlu38wqACzM/MsAvbX0hJpZ4LipaG8IRs" +
                "ZK9YoGhh2eHsi5BaDD2MootuWpS+ackGrqIOAcm4hPKV0lQVH41FXXCqHB0+qZ3oK+ZcrXiHSFuD" +
                "YWg+7KHcvrLAjm2BJgNTmct6bi0VlkLhRqciLrcyW6YA3mVuXYGTwy5FW1l8JFvlvOZdKeIt+sTC" +
                "nID5ZYyldM//2Q==";
        Message message = null;
        try {
            message = new Message(dateTime, contentId, senderID, text, contentType, content);
        } catch (ParseException e) {
            fail("Fail to parse text message");
        }

        assertEquals(Long.valueOf(2), message.getSender().getId());
        try {
            assertEquals(DateSerialization.dateSerializer.toDate(dateTime), message.getDate());
        } catch (ParseException e) {
            fail("Fail to serialize date");
        }
        assertEquals("GCC : image", message.getPreview());
        assertEquals(Message.MessageStatus.SENT, message.getStatus());
    }
}
