package com.example.yunhists.task;

import com.example.yunhists.entity.Category;
import com.example.yunhists.entity.CategoryLink;
import com.example.yunhists.enumeration.CategoryEnum;
import com.example.yunhists.service.CategoryLinkService;
import com.example.yunhists.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryTask {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryLinkService categoryLinkService;

    @Scheduled(cron ="0 0 0 * * ?")
    public void categoryStatisticDenormalizationSynchronization() {
        List<Category> allCategories = categoryService.getAll();
        for(Category category : allCategories) {

            // 1. check thesis count
            List<CategoryLink> thesisToCat = categoryLinkService.getLinkByParentId(category.getId(),
                    CategoryEnum.TYPE_LINK_THESIS.getCode());
            if(category.getCatTheses() != thesisToCat.size()) {
                category.setCatTheses(thesisToCat.size());
            }

            // 2. check subCat count
            List<CategoryLink> catToCat = categoryLinkService.getLinkByParentId(category.getId(),
                    CategoryEnum.TYPE_LINK_CATEGORY.getCode());
            if(category.getCatSubCats() != catToCat.size()) {
                category.setCatSubCats(catToCat.size());
            }

            categoryService.saveOrUpdate(category);
        }
    }

    @Scheduled(cron ="0 0 0 * * ?")
    public void categoryNameDenormalizationSynchronization() {
        List<Category> allCategories = categoryService.getAll();
        for(Category category : allCategories) {
            List<CategoryLink> categoryLinkList = categoryLinkService.getLinkByParentId(category.getId());
            for(CategoryLink link : categoryLinkList) {
                if(!link.getCatToZhName().equals(category.getZhName())) {
                    link.setCatToZhName(category.getZhName());
                }
                if(!link.getCatToEnName().equals(category.getEnName())) {
                    link.setCatToEnName(category.getEnName());
                }
                categoryLinkService.saveOrUpdate(link);
            }
        }
    }

}
