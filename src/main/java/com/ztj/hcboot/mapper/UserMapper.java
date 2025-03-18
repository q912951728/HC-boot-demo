package com.ztj.hcboot.mapper;

import com.ztj.hcboot.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    @Insert("insert into t_user(id, nickname, password) values(#{id}, #{nickname}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("select * from t_user where id = #{id}")
    User findByUserId(long id);
}