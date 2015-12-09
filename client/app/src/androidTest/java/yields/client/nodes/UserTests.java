package yields.client.nodes;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.ImageSerialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTests {
    private static JSONObject jsonUserNoPic;
    private static JSONObject jsonUserPic;
    private static final String base64Image = "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsK" +
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

    @Before
    public void setUp() throws JSONException{
        jsonUserNoPic = new JSONObject();
        jsonUserNoPic.put("name", "Flash");
        jsonUserNoPic.put("uid", 420);
        jsonUserNoPic.put("email", "asd.asd@qwe.kr");

        jsonUserPic = new JSONObject();
        jsonUserPic.put("name", "Flash");
        jsonUserPic.put("uid", 420);
        jsonUserPic.put("email", "asd.asd@qwe.kr");
        jsonUserPic.put("pic", base64Image);
    }

    @Test
    public void testUserConstructorJSONWithoutPic() throws JSONException {
        User user = new User(jsonUserNoPic);
        assertEquals(Long.valueOf(420), user.getId().getId());
        assertEquals("Flash", user.getName());
        assertEquals("asd.asd@qwe.kr", user.getEmail());
    }

    @Test
    public void testUserConstructorJSONWithPic() throws JSONException {
        User user = new User(jsonUserNoPic);
        assertEquals(Long.valueOf(420), user.getId().getId());
        assertEquals("Flash", user.getName());
        assertEquals("asd.asd@qwe.kr", user.getEmail());
        Bitmap expectedImage = ImageSerialization.unSerializeImage(base64Image);
        String expectedUnserialized = ImageSerialization.serializeImage(expectedImage, expectedImage.getWidth());
        Bitmap userImage = user.getImg();
        String userUnserialized = ImageSerialization.serializeImage(userImage, expectedImage.getWidth());
        assertEquals(expectedUnserialized, userUnserialized);
    }

    @Test
    public void testUserEqualsSameUser() throws JSONException {
        User user1 = new User(jsonUserNoPic);
        User user2 = new User(jsonUserNoPic);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
    }

    @Test
    public void testUserEqualsOtherUser() throws JSONException {
        User user = new User(jsonUserNoPic);
        User user2 = new User(new Id(0xDEADBEEF));
        assertFalse(user.equals(user2));
        assertFalse(user2.equals(user));
    }

    @Test
    public void testUserEqualsObject() throws JSONException {
        User user = new User(jsonUserNoPic);
        assertFalse(user.equals("Doritos"));
    }
}
