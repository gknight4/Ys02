package com.thumbsup;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.tomcat.util.http.parser.Cookie;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thumbsup.YsRestController.RegisterResponseResource;
import com.thumbsup.model.CommonPasswords;
import com.thumbsup.model.CommonPasswordsRepository;
import com.thumbsup.model.YsUser;
import com.thumbsup.model.YsUserRepository;
import com.thumbsup.shared.SuccessResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static com.thumbsup.jwt.SecurityConstants.EXPIRATION_TIME;
import static com.thumbsup.jwt.SecurityConstants.HEADER_STRING;
import static com.thumbsup.jwt.SecurityConstants.SECRET;
import static com.thumbsup.jwt.SecurityConstants.TOKEN_PREFIX;
import static com.thumbsup.shared.SharedConstants.USER_FLAG_CONFIRMED;
import static com.thumbsup.shared.SharedConstants.USER_FLAG_PARENT;

@RestController
@CrossOrigin
@RequestMapping(value = "/open")
@RepositoryRestController
public class OpenChecks {
	private final YsUserRepository userRepository ;
	private final CommonPasswordsRepository commonPasswordsRepository ;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	

	
	public OpenChecks (YsUserRepository userRepository,
			CommonPasswordsRepository commonPasswordsRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder
			) {
		this.userRepository = userRepository;
		this.commonPasswordsRepository = commonPasswordsRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	private void sendConfirmationEmail(String email, String userName,
			Long confirmCode) {
		String encConfirm = "" ;
//		Random rand = new Random();
//		int confirmCode = rand.nextInt(1000000);
		String clearConfirm = String.format("%s/%06X", userName, confirmCode);
		String b64Confirm = Base64.getEncoder()
				.encodeToString(clearConfirm.getBytes());
		try {
			encConfirm = URLEncoder.encode(b64Confirm, "UTF-8");
			
		} catch (Exception e) {
			System.out.println("Exception");
		}
		String linkConfirm = String.format(
				"http://YourStash.tr8.us:6026/open/confirm/%s", 
				encConfirm);
		
		String msg =String.format("Greetings from YourStash! Before you can "
	+ "use your account, you have to confirm this email address. "
	+ "Please click on the link below, or enter %d in the Register "
	+ "dialog on the YourStash website.\n"
	+ "%s", confirmCode, linkConfirm);
		System.out.println(msg);;
		
	}
	
	String makeToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	SuccessResponse putLogin(
			@RequestBody LoginInfo loginInfo,
			HttpServletResponse response
//			@CookieValue(HEADER_STRING) String authCookie
			) {
//		System.out.println(loginInfo.getUsername());
//		System.out.println("cookie: " + authCookie);
		System.out.println("doing login");
		Optional<YsUser> user = userRepository.findByUsername(loginInfo.getUsername()) ;
		System.out.println("user present: " + user.isPresent());
		if(user.isPresent() && 
			(bCryptPasswordEncoder.matches(loginInfo.getPassword(), 
					user.get().getPassword()))
		) {
//			System.out.println("confirm");
			YsUser ysUser = user.get();
			ysUser.setFlags(ysUser.getFlags() | USER_FLAG_CONFIRMED);
			userRepository.save(ysUser);
			String token = makeToken(loginInfo.getUsername());
//			System.out.println(token);
			response.addHeader("Access-Control-Expose-Headers", "Authorization");
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
			System.out.println(HEADER_STRING + ", " + token);
			int nowTime = (int)((new Date().getTime()) / 1000) ;
//			System.out.println(nowTime);
			Cookie cookie = new Cookie(HEADER_STRING, token) ;
//			cookie.setHttpOnly(true);
			cookie.setSecure(false);
			cookie.setPath("/");
			cookie.setMaxAge(nowTime + 86400);
			response.addCookie(cookie);
//			response.setHeader("Expires", "100");
			return new SuccessResponse(true) ;
		} else {
			return new SuccessResponse(false) ;
		}
		
	}

	
	@RequestMapping(value="/confirm", method=RequestMethod.POST)
	SuccessResponse putConfirm(
			@RequestBody ConfirmInfo confirmInfo,
			HttpServletResponse response) {
		System.out.println("confirm: " + confirmInfo.getParentname());
		Optional<YsUser> user = userRepository.findByUsername(confirmInfo.getParentname()) ;
		System.out.println(user.isPresent());
		if(user.isPresent() && 
			(user.get().getConfirmCode()
			.equals(confirmInfo.getConfirm())) &&
			(bCryptPasswordEncoder.matches(confirmInfo.getPassword(), 
					user.get().getPassword()))
		) {
			System.out.println("confirm");
			YsUser ysUser = user.get();
			ysUser.setFlags(ysUser.getFlags() | USER_FLAG_CONFIRMED);
			userRepository.save(ysUser);
			String token = makeToken(confirmInfo.getParentname());
			System.out.println(token);
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
			response.addHeader("Access-Control-Expose-Headers", "Authorization");
//			response.setHeader("Expires", "100");
			return new SuccessResponse(true) ;
		} else {
			return new SuccessResponse(false) ;
		}
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	SuccessResponse putRegister(
		@RequestBody Registration newRegistration,
		HttpServletResponse response) {
		Random rand = new Random();
		Long confirmCode = (long)rand.nextInt(1000000);
		YsUser user = new YsUser(
			newRegistration.getParentName(), // username 
			newRegistration.getParentName(),
			newRegistration.getParentEmail(),
			bCryptPasswordEncoder.encode(
					newRegistration.getPassword()),
			confirmCode);
		user.setFlags(USER_FLAG_PARENT);
		sendConfirmationEmail(
				newRegistration.getParentEmail(), 
				newRegistration.getParentName(),
				confirmCode);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", 
				"Accept, Accept-Encoding, Accept-Language, Cache-Control, Connection, Content-Length, Content-Type, Host, Origin, Pragma, Referer, User-Agent");
		try {
			YsUser repUser = userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			System.out.println("execept");
			return new SuccessResponse(false);
		}
		return new SuccessResponse(true) ;
	}

/*	@RequestMapping(value="/confirm/{confirmCode}", method=RequestMethod.GET)
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
		System.out.println(fields[0] + ", " + nCode);
		Optional<YsUser> user = userRepository.findByUsername(fields[0]) ;
		if (user.isPresent()) {
			YsUser ysUser = user.get(); 
			if (ysUser.getConfirmCode() == nCode) {
				ysUser.setFlags(ysUser.getFlags() | USER_FLAG_CONFIRMED);
				userRepository.save(ysUser);
			}
			System.out.println("Parent: " + user.get().getId());
		}
		
		return "ok";
	}*/

		
	@RequestMapping(value="/ipcheck", method=RequestMethod.GET)
	SuccessResponse checkIp(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		ip = "173.244.48.20" ;
//		String ipAddress = request.getHeader("X-FORWARDED-FOR"); 
//		System.out.println(ipAddress);
		YsHttpClients ysHttpClients = new YsHttpClients();
		return new SuccessResponse(ysHttpClients.checkUserIp(ip)) ;
	}

	@RequestMapping(value="/passwordcheck/{password}", method=RequestMethod.GET)
	SuccessResponse checkPassword(@PathVariable String password) {
		Optional<CommonPasswords> pass = commonPasswordsRepository.findByPassword(password) ;
		return new SuccessResponse(pass.isPresent()) ;
	}

	@RequestMapping(value="/usercheck/{userName}", method=RequestMethod.GET)
	SuccessResponse checkUser(@PathVariable String userName) {
//		System.out.println("user: " + userName);
		Optional<YsUser> user = userRepository.findByUsername(userName) ;
		System.out.println(user.isPresent());
		return new SuccessResponse(user.isPresent()) ;
//		return new YsRestController.RegisterResponseResource(user.isPresent());
		
//		return "none";
	}

	@RequestMapping(value = "/hello")
	String gohello() {
//		YsHttpClient yshClient = new YsHttpClient ();
//		try {
//			yshClient.whenPostRequestUsingHttpClient_thenCorrect();
//		} catch (IOException e) {
//			System.out.println("Failed Http");
//		}
		return "hello";
	}

	

}
