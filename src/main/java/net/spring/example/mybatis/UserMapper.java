package net.spring.example.mybatis;

import java.util.List;

import net.spring.example.user.vo.User;

public interface UserMapper {
	
	public int insert(User user);
	
	public User login(User user);
	
	public int modify(User user);
	
	public void changePasswd(User user);

	public void bye(User user);

	public List<User> getAllUsers();
	
	public User getUserByEmail(String email);
	
	public List<User> getUsersByKeyword(String keyword);

	
}
