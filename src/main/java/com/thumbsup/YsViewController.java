package com.thumbsup;

import static com.thumbsup.shared.SharedConstants.USER_FLAG_CONFIRMED;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thumbsup.model.YsUser;
import com.thumbsup.model.YsUserRepository;

//@RestController
@Controller
@RequestMapping(value = "/open/jsp")
public class YsViewController {
	private final YsUserRepository userRepository ;
	
	public YsViewController(YsUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@RequestMapping(value = "/login")
	String getView() {
		return "login";
	}
	
    @Autowired
    public JavaMailSender emailSender;	
	
	public void tryMail(String to, String subject, String text) {
		System.out.println("trying");
	          SimpleMailMessage message = new SimpleMailMessage(); 
	          message.setTo(to); 
	          message.setFrom("Your Stash<phelanphirewood@gmail.com>");
	          message.setSubject(subject); 
	          message.setText(text);
	          emailSender.send(message);		
	}

	@RequestMapping(value = "/home")
	String gohome() {
        System.out.println("we're home!");
//        tryMail(
//        		"GeneKnight4@GMail.com", 
//        		"my new subject", 
//        		"just a short note");
		return "home";
	}

	@RequestMapping(value="/confirm/{confirmCode}", method=RequestMethod.GET)
	String checkIp(@PathVariable String confirmCode, 
			HttpServletRequest request) {
		
		String decConfirm = "";
		try {
			decConfirm = URLDecoder.decode(confirmCode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String strConfirm = new String(
				Base64.getDecoder().decode(decConfirm.getBytes()));
//		System.out.println(confirmCode);
//		System.out.println(decConfirm);
//		System.out.println(strConfirm);
		String [] fields = strConfirm.split("/");
//		System.out.println(fields[1]);
		int nCode = Integer.parseInt(fields[1], 16);
//		System.out.println(fields[0] + ", " + nCode);
		Optional<YsUser> user = userRepository.findByUsername(fields[0]) ;
		if (user.isPresent()) {
			YsUser ysUser = user.get(); 
			if (ysUser.getConfirmCode() == nCode) {
				ysUser.setFlags(ysUser.getFlags() | USER_FLAG_CONFIRMED);
				userRepository.save(ysUser);
			}
//			System.out.println("Parent: " + user.get().getId());
		}
		
		return "confirmed";
	}

/*	@RequestMapping(value = "/confirmed")
	String gohello() {
//		YsHttpClient yshClient = new YsHttpClient ();
//		try {
//			yshClient.whenPostRequestUsingHttpClient_thenCorrect();
//		} catch (IOException e) {
//			System.out.println("Failed Http");
//		}
		return "confirmed";
	}*/
}
