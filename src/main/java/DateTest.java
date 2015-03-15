package main.java;

import junit.framework.TestCase;
import org.junit.Test;

public class DateTest extends TestCase {
    @Test
    public void testDateConstructor() {
        Date date = new Date("4 apr");
        assertEquals("2015-04-04", date.getLocalDateObj().toString());
    }

}
