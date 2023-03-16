package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.service.ThesisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ThesisServiceImplTest {

    @Autowired
    ThesisService thesisService;

    @Test
    public void validateNotExist() {
        Thesis thesis = new Thesis();
        thesis.setAuthor("abc");
        thesis.setTitle("abc");
        thesis.setPublisher("abc");
        assertTrue(thesisService.validateNotExist(thesis));
    }

    @Test
    public void validateNotExistWhenUpdate() {
        Thesis thesis = new Thesis();
        thesis.setAuthor("abc");
        thesis.setTitle("abc");
        thesis.setPublisher("abc");
        assertTrue(thesisService.validateNotExistWhenUpdate(thesis));
    }

    @Test
    public void getThesisBySearch() {
        Page<Thesis> page = new Page<>(1, 5);
        assertNotNull(thesisService.getThesisBySearch(page, "abc", "a b c", "abc", "9999", "id", "ASC"));
        assertNotNull(thesisService.getThesisBySearch(page, "abc", "abc", "abc", "9999", "", ""));
    }

    @Test
    public void getThesisByFile() {
        assertNull(thesisService.getThesisByFile("abc"));
    }

    @Test
    public void getAll() {
        assertTrue(thesisService.getAll().size() > 0);
    }
}
