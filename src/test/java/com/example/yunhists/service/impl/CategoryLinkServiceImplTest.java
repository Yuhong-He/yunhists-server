package com.example.yunhists.service.impl;

import com.example.yunhists.service.CategoryLinkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CategoryLinkServiceImplTest {

    @Autowired
    CategoryLinkService categoryLinkService;

    @Test
    public void linkNotExist() {
        assertTrue(categoryLinkService.linkNotExist(999999, 999999, 1));
    }

    @Test
    public void getCategoryLinkByQuery() {
        assertNull(categoryLinkService.getCategoryLinkByQuery(999999, 999999, 1));
    }

    @Test
    public void getLinkByChildId() {
        assertTrue(categoryLinkService.getLinkByChildId(999999, 1).isEmpty());
    }

    @Test
    public void getLinkByParentIdWithType() {
        assertTrue(categoryLinkService.getLinkByParentId(999999, 1).isEmpty());
    }

    @Test
    public void getLinkByParentId() {
        assertTrue(categoryLinkService.getLinkByParentId(999999).isEmpty());
    }

    @Test
    public void getAll() {
        assertNotNull(categoryLinkService.getAll());
    }

}
