package net.spring.example.user.controller;

import javax.servlet.http.HttpSession;

import net.spring.example.user.service.UserService;
import net.spring.example.user.vo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/signUp", method=RequestMethod.GET)
	public String signUp() {
		return "users/signUp";
	}
	
	@RequestMapping(value="/signUp", method=RequestMethod.POST)
	public String signUp(User user) throws Exception {
		userService.insert(user);
		return "users/welcome";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login() {
		return "users/login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(User user, HttpSession session) throws Exception {
		user = userService.login(user);
		if (user != null) {
			session.setAttribute("user", user);
			return "users/changePasswd";
		} else {
			return "users/login";
		}
	}
	
	@RequestMapping(value="/editAccount", method=RequestMethod.GET)
	public String editAccount() {
		return "users/editAccount";
	}
	
	@RequestMapping(value="/editAccount", method=RequestMethod.POST)
	public String editAccount(User user, HttpSession session) throws Exception {
		User loginUser = (User) session.getAttribute("user");
		String email = loginUser.getEmail(); //세션에 저장된 사용자 정보로부터 이메일을 알아낸다.
		
		if (user.getName() == null) {
			user.setName(loginUser.getName());
		}
		if (user.getMobile() == null) {
			user.setMobile(loginUser.getMobile());
		}
		
		user.setEmail(email);
		int check = userService.modify(user);
		if (check == 1) {
			session.setAttribute("user",user);
		}
		
		return "users/changePasswd";
		
	}
	
	@RequestMapping(value="/changePasswd", method=RequestMethod.GET)
	public String changePasswd() {
		return "users/changePasswd";
	}
	
	@RequestMapping(value="/changePasswd", method=RequestMethod.POST)
	public String changePasswd(String passwd, HttpSession session) throws Exception {
		String email = ((User)session.getAttribute("user")).getEmail();
		
		User user = new User();
		user.setEmail(email);
		user.setPasswd(passwd);
		
		userService.changePasswd(user);
		
		return "users/confirm";
	}
	
	@RequestMapping(value="/bye", method=RequestMethod.GET)
	public String bye(HttpSession session) {
		User user = (User)session.getAttribute("user");
		userService.bye(user);
		session.invalidate();
		
		return "users/login";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpSession session) {
		session.removeAttribute("user");

		return "users/login";

	}

	//TODO getAllUsers(),getUserByEmail() getUsersByKeyword() 메소드 추가(사용자 관리 페이지)
	
}

