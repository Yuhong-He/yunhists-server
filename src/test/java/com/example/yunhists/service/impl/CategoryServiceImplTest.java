package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yunhists.entity.Category;
import com.example.yunhists.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CategoryServiceImplTest {

    @Autowired
    CategoryService categoryService;

    @Test
    public void validateChineseName() {
        assertTrue(categoryService.validateChineseName("abc"));
    }

    @Test
    public void validateChineseNameWithId() {
        assertTrue(categoryService.validateChineseName(1, "abc"));
    }

    @Test
    public void validateEnglishName() {
        assertTrue(categoryService.validateEnglishName("abc"));
    }

    @Test
    public void validateEnglishNameWithId() {
        assertTrue(categoryService.validateEnglishName(1, "abc"));
    }

    @Test
    public void getCategoryByIdWithoutPrivacy() {
        assertNull(categoryService.getCategoryByIdWithoutPrivacy(999999));
    }

    @Test
    public void getCategoriesByBatchId() {
        assertTrue(categoryService.getCategoriesByBatchId(List.of(999999)).isEmpty());
    }

    @Test
    public void getCategoryByNameLike() {
        Page<Category> page = new Page<>(1, 5);
        assertNotNull(categoryService.getCategoryByNameLike(page, "abc", "zh", "id", "ASC"));
        assertNotNull(categoryService.getCategoryByNameLike(page, "abc", "en", "", ""));
    }

    @Test
    public void getCategories() {
        assertTrue(categoryService.getCategories("abc", "zh").isEmpty());
        assertTrue(categoryService.getCategories("abc", "en").isEmpty());
    }

    @Test
    public void getCatIdByName() {
        assertNull(categoryService.getCatIdByName("abc", "zh"));
        assertNull(categoryService.getCatIdByName("abc", "en"));
    }

    @Test
    public void getAll() {
        assertTrue(categoryService.getAll().size() > 0);
    }

}
