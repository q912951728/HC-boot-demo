package com.ztj.hcboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ztj.hcboot.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Insert("insert into t_user(id, nickname, password) values(#{id}, #{nickname}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOne(User user);

    @Select("select * from t_user where id = #{id}")
    User findByUserId(long id);

    @Select("select * from t_user")
    List<User> selectAllUsers();
}