package com.example.yunhists.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yunhists.entity.Thesis;
import com.example.yunhists.mapper.ThesisMapper;
import com.example.yunhists.service.ThesisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("thesisServiceImpl")
public class ThesisServiceImpl extends ServiceImpl<ThesisMapper, Thesis> implements ThesisService {

    @Override
    public boolean validateNotExist(Thesis thesis) {
        QueryWrapper<Thesis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author", thesis.getAuthor());
        queryWrapper.eq("title", thesis.getTitle());
        queryWrapper.eq("publication", thesis.getPublication());
        Thesis t = baseMapper.selectOne(queryWrapper);
        return t == null;
    }

    @Override
    public boolean validateNotExistWhenUpdate(Thesis thesis) {
        QueryWrapper<Thesis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author", thesis.getAuthor());
        queryWrapper.eq("title", thesis.getTitle());
        queryWrapper.eq("publication", thesis.getPublication());
        Thesis t = baseMapper.selectOne(queryWrapper);
        if(t == null) {
            return true;
        } else return Objects.equals(t.getId(), thesis.getId());
    }

    @Override
    public IPage<Thesis> getThesisBySearch(Page<Thesis> page, String author, String title,
                                           String publication, String year, String sortCol, String sortOrder) {
        QueryWrapper<Thesis> queryWrapper = new QueryWrapper<>();
        if(!author.isEmpty()) {
            queryWrapper.like("author", author);
        }
        if(!title.isEmpty()) {
            if(title.contains(" ")) {
                while(title.contains(" ")) {
                    String temp = title.substring(0, title.indexOf(" "));
                    title = title.substring(title.indexOf(" ") + 1);
                    queryWrapper.like("title", temp);
                }
            }
            queryWrapper.like("title", title);
        }
        if(!publication.isEmpty()) {
            queryWrapper.like("publication", publication);
        }
        if(!year.isEmpty()) {
            queryWrapper.like("year", year);
        }
        if(!sortCol.isEmpty() && !sortOrder.isEmpty()) {
            queryWrapper.last(" ORDER BY IF(ISNULL(" + sortCol + "),1,0), CONVERT ( " + sortCol + " USING gbk )" + sortOrder);
        } else {
            queryWrapper.orderByDesc("approve_time");
        }
        page.setOptimizeCountSql(false); // https://github.com/baomidou/mybatis-plus/issues/3698
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Thesis getThesisByFile(String file) {
        QueryWrapper<Thesis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", file);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Thesis> getAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<Thesis> getThesisWithoutFile() {
        QueryWrapper<Thesis> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Thesis::getFileName, StringUtils.EMPTY);
        return baseMapper.selectList(queryWrapper);
    }

}
