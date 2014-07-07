package net.spring.example.user.serviceimpl;

import java.util.List;

import net.spring.example.mybatis.UserMapper;
import net.spring.example.user.service.UserService;
import net.spring.example.user.vo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
		
	public int insert(User user) {
		return userMapper.insert(user);
	}
	
	public User login(User user) {
		return userMapper.login(user);
	}
	
	public int modify(User user) {
		return userMapper.modify(user);
	}
	
	public void changePasswd(User user) {
		userMapper.changePasswd(user);
	}
	
	public void bye(User user) {
		userMapper.bye(user);
	}

	public List<User> getAllUsers() {
		return userMapper.getAllUsers();
	}
	
	public User getUserByEmail(String email) {
		return userMapper.getUserByEmail(email);
	}
	
	public List<User> getUsersByKeyword(String keyword) {
		return userMapper.getUsersByKeyword(keyword);
	}
	
}