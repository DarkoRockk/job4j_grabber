package ru.grabber;

import org.junit.Assert;
import org.junit.Test;

public class GrabberTest {

    @Test
    public void test() {
        Assert.assertEquals(1, new Grabber().trigger());
    }
}