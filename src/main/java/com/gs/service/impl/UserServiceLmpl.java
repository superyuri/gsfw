package com.gs.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.convert.UserConvert;
import com.gs.model.dto.UserDTO;
import com.gs.model.dto.UserLoginDTO;
import com.gs.model.entity.db1.User;
import com.gs.repository.db1.UserRepository;
import com.gs.service.intf.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import lombok.AllArgsConstructor;

import org.springframework.transaction.annotation.Propagation;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceLmpl extends ServiceImpl<UserRepository, User> implements UserService {

    private final UserRepository userRepository;

    private final UserConvert userConvert;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserName, userLoginDTO.getUserName());
        queryWrapper.lambda().eq(User::getPassword, DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes()));
        return userRepository.selectOne(queryWrapper);
    }

    /**
     * 登录成功后保存token,用来检验重复登录
     * @param User 用户新信息
     */
    @Override
    public void loginSuccess(User user) {
        userRepository.updateById(user);
    }

    @Override
    public UserDTO findByUseName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserName,userName);
        return userConvert.toDto(userRepository.selectOne(queryWrapper));
    }

    /**
     * 分页查询用户
     * @param Boolean deleted 检索条件1
     * @param Integer pageNo 当前页码
     * @param Integer pageSize 每页数据量
     */
    @Override
    public IPage<User> page(Boolean deleted, Integer pageNo, Integer pageSize) {
        Page<User> page = new Page<>(pageNo, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getDeleted, deleted);
        wrapper.lambda().orderByDesc(User::getId);
        return userRepository.selectPage(page, wrapper);
    }

    /**
     * 以sql的方式检索事例
     */
    @Override
    public List<User> sqlSelect() {
        return userRepository.test();
    }
}
