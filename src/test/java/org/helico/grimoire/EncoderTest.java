package org.helico.grimoire;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.Assert;
import org.junit.jupiter.api.Test;

public class EncoderTest {

    @Test
    public void testEncoder() {
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bcryptPasswordEncoder.encode("admin"));
        String encoded = "$2a$10$mdwyAUbDxxLyvUQzlVlw0.flqghVLzEaiCvYSrwRy.EKr.brjNl/i";
        Assert.assertTrue(bcryptPasswordEncoder.matches("admin", encoded));
    }

}