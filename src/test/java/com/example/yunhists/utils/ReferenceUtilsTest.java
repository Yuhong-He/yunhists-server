package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReferenceUtilsTest {

    @Test
    public void vancouverStyle() {
        assertTrue(ReferenceUtils.vancouverStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" ,"doi", 0).length() > 0);
        assertTrue(ReferenceUtils.vancouverStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" ,"doi", 1).length() > 0);
        assertTrue(ReferenceUtils.vancouverStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" ,"doi", 3).length() > 0);
    }

    @Test
    public void harvardStyle() {
        assertTrue(ReferenceUtils.harvardStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" ,"doi", 0).length() > 0);
        assertTrue(ReferenceUtils.harvardStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" ,"doi", 1).length() > 0);
        assertTrue(ReferenceUtils.harvardStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" ,"doi", 3).length() > 0);
    }

    @Test
    public void gbt7714Style() {
        assertTrue(ReferenceUtils.gbt7714Style("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" , 0).length() > 0);
        assertTrue(ReferenceUtils.gbt7714Style("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" , 1).length() > 0);
        assertTrue(ReferenceUtils.gbt7714Style("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" , 2).length() > 0);
        assertTrue(ReferenceUtils.gbt7714Style("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" , 3).length() > 0);
    }

    @Test
    public void wikipediaStyle() {
        assertTrue(ReferenceUtils.wikipediaStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages" , "isbn","doi", 0).length() > 0);
        assertTrue(ReferenceUtils.wikipediaStyle("aut,hor", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages", "isbn" ,"doi", 1).length() > 0);
        assertTrue(ReferenceUtils.wikipediaStyle("author", "title",
                "publication", "location", "publisher", "year",
                "volume" ,"issue" ,"pages", "isbn" ,"doi", 3).length() > 0);
    }

}
