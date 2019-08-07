package com.scurab.android.anuitor.reflect;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by JBruchanov on 12/03/2017.
 */
public class ReflectorTest {

    private HelpReflector mHelpClassReflector;

    @Before
    public void setUp() {
        mHelpClassReflector = new HelpReflector();
    }

    @Test
    public void testAccessPrivateField() {
        assertEquals("1", mHelpClassReflector.getFieldValue("mString"));
    }

    @Test
    public void testAccessPrivateMethod() {
        try {
            mHelpClassReflector.sampleMethodToCall();
            fail("Expected crash");
        } catch (RuntimeException e) {
            //this is impossible to test simply as we can't simulate @hide annotation easily...
            assertEquals("Unable to find method: sampleMethodToCall([])", e.getMessage());
        }
    }

    @Test
    public void testAccessPrivateMethod2() {
        try {
            mHelpClassReflector.sampleMethodToCall2();
            fail("Expected crash");
        } catch (RuntimeException e) {
            //this is impossible to test simply as we can't simulate @hide annotation easily...
            assertEquals("Unable to find method: sampleMethodToCall2([])", e.getMessage());
        }
    }

    @Test
    public void testAccessPrivateMethod3() {
        assertEquals("toString()", mHelpClassReflector.sampleMethodToCall3());
    }

    @Test
    public void testAccessPrivateStaticMethod() {
        assertEquals("3", mHelpClassReflector.sampleCallStaticMethod());
    }


    public static class HelpReflector extends ObjectReflector {

        public HelpReflector() {
            super(new HelpClass());
        }

        public String sampleMethodToCall() {
            return callByReflection();
        }

        public String sampleMethodToCall2() {
            return callMethodByReflection("sampleMethodToCall2");
        }

        public String sampleMethodToCall3() {
            return callMethodByReflection(Object.class, this, "toString");
        }

        public String sampleCallStaticMethod() {
            return callMethodByReflection(HelpClass.class, null, "staticMethod");
        }

        @Override
        public String toString() {
            return "toString()";
        }
    }


    @SuppressWarnings("unused")
    public static class HelpClass {
        private String mString = "1";

        private static String method() {
            return "2";
        }

        private static String staticMethod() {
            return "3";
        }
    }
}