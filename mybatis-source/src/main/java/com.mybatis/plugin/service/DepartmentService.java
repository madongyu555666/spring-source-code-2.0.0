package com.mybatis.plugin.service;


import com.mybatis.plugin.bean.Department;
import com.mybatis.plugin.dao.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    public List<Department> getDepts() {

        List<Department> list = departmentMapper.selectByMap(null);
        return list;
    }
}
