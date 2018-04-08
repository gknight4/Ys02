package com.thumbsup;

import static com.thumbsup.jwt.SecurityConstants.HEADER_STRING;
import static com.thumbsup.jwt.SecurityConstants.SECRET;
import static com.thumbsup.jwt.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
//import org.hibernate.mapping.Collection;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.thumbsup.model.Transaction;
import com.thumbsup.model.TransactionRepository;
import com.thumbsup.model.YsUser;
import com.thumbsup.YsUserResource;
import com.thumbsup.model.YsUserRepository;
import com.thumbsup.postjson.AddChild;
import com.thumbsup.shared.SuccessResponse;
import static com.thumbsup.shared.SharedConstants.USER_FLAG_CONFIRMED;

import io.jsonwebtoken.Jwts;

//tag::code[]
@RestController
@CrossOrigin
@RequestMapping(value = "/api")
@RepositoryRestController
public class YsRestController {
	private final YsUserRepository userRepository ;
	private final TransactionRepository transactionRepository ;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public YsRestController(YsUserRepository userRepository,
			TransactionRepository transactionRepository,
//			AuthenticationManager authenticationManager,
			BCryptPasswordEncoder bCryptPasswordEncoder
			) {
		this.userRepository = userRepository ;
		this.transactionRepository = transactionRepository;
//		this.authenticationManager = authenticationManager;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	
	public class RecaptchaResponse{
		boolean success ;
		String challenge_ts ;
		String hostname ;
	}
	
	private boolean checkUserIp(String userIp) {
		String url = "http://check.getipintel.net/check.php?ip=" + 
				userIp + "&contact=GeneKnight4@GMail.com&flags=m" ;
//		String url = "http://anto.tr8.us/post/index.php" ;
		try {
		    CloseableHttpClient client = HttpClients.createDefault();
		    HttpGet httpGet = new HttpGet(url);
//		    HttpPost httpPost = new HttpPost(url);
//		    List<NameValuePair> params = new ArrayList<NameValuePair>();
//		    params.add(new BasicNameValuePair("secret", secret));
//		    params.add(new BasicNameValuePair("response", recResponse));
//		    httpPost.setEntity(new UrlEncodedFormEntity(params));
		    CloseableHttpResponse response = client.execute(httpGet);
		    String resp = EntityUtils.toString(response.getEntity(), "UTF-8") ;
		    System.out.println("response: " + resp);
		    return (resp == "1") ;
/*			ObjectMapper mapper = new ObjectMapper();
			RecaptchaResponse rcr = mapper.readValue(
					EntityUtils.toString(response.getEntity(), "UTF-8"),  
					RecaptchaResponse.class);
		    System.out.println("success: " + rcr.success);
		    client.close();
		    return rcr.success ;*/
		} catch (ClientProtocolException ioe) {
			System.out.println("CP Exception");
			return false ;
		} catch (IOException ioe) {
			System.out.println("IO Exception");
			return false ;
		}

	}
	
	private boolean verifyReCaptcha(String recResponse) {
/*
response: {
  "success": true,
  "challenge_ts": "2018-03-26T16:03:59Z",
  "hostname": "yourstash.tr8.us"
  
ObjectMapper mapper = new ObjectMapper();
String jsonInString = "{'name' : 'mkyong'}";

//JSON from file to Object
Staff obj = mapper.readValue(new File("c:\\file.json"), Staff.class);

//JSON from URL to Object
Staff obj = mapper.readValue(new URL("http://mkyong.com/api/staff.json"), Staff.class);

//JSON from String to Object
Staff obj = mapper.readValue(jsonInString, Staff.class);  
}
 */
		String secret = "6LfBrk4UAAAAABXVHvZIs7DegfDlJqmA-059vimb";
		String url = "https://www.google.com/recaptcha/api/siteverify" ;
//		String url = "http://anto.tr8.us/post/index.php" ;
		try {
		    CloseableHttpClient client = HttpClients.createDefault();
		    HttpPost httpPost = new HttpPost(url);
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
		    params.add(new BasicNameValuePair("secret", secret));
		    params.add(new BasicNameValuePair("response", recResponse));
		    httpPost.setEntity(new UrlEncodedFormEntity(params));
		    CloseableHttpResponse response = client.execute(httpPost);
			ObjectMapper mapper = new ObjectMapper();
			RecaptchaResponse rcr = mapper.readValue(
					EntityUtils.toString(response.getEntity(), "UTF-8"),  
					RecaptchaResponse.class);
		    System.out.println("success: " + rcr.success);
		    client.close();
		    return rcr.success ;
		} catch (ClientProtocolException ioe) {
			System.out.println("CP Exception");
			return false ;
		} catch (IOException ioe) {
			System.out.println("IO Exception");
			return false ;
		}
	}
	
	public class RegisterResponse{
		public RegisterResponse() {}
			
		public RegisterResponse(boolean succ) {
			this.success = succ ;
		}

		public boolean isSuccess() {
			return success;
		}
		
		public void setSuccess(boolean success) {
			this.success = success;
		}

		boolean success ;
		
	}
	
	public class RegisterResponseResource extends ResourceSupport {
		private final RegisterResponse rr ;
		public RegisterResponseResource(boolean success) {
			rr = new RegisterResponse(success);
		}
		
		public RegisterResponse getRegisterResponse() {
			return rr ;
		}
		
	}
	
/*	public void doLogin(HttpServletRequest request, String userName, String password)
	{

	    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);

	    HttpServletRequest authenticationManager;
		// Authenticate the user
	    Authentication authentication = authenticationManager.authenticate(authRequest);
	    SecurityContext securityContext = SecurityContextHolder.getContext();
	    securityContext.setAuthentication(authentication);

	    // Create a new session and add the security context.
	    HttpSession session = request.getSession(true);
	    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
	}*/	
	
	@RequestMapping(value="/**", method=RequestMethod.OPTIONS)
	public ResponseEntity<?>handle(){
		System.out.println("doing options");
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	String getLogin(
			@RequestBody Registration login,
			HttpServletResponse response) {
		System.out.println(login.getParentName());
		System.out.println(login.getUserName());
		System.out.println(login.getPassword());
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", 
				"Accept, Accept-Encoding, Accept-Language, Cache-Control, Connection, Content-Length, Content-Type, Host, Origin, Pragma, Referer, User-Agent");
		return "login" ;

	}
	
	@RequestMapping(value="/authcheck", method=RequestMethod.GET)
	SuccessResponse checkAuth(){
		return new SuccessResponse(true) ;
	}


	
	@RequestMapping(value="/usercheck/{userName}", method=RequestMethod.GET)
	RegisterResponseResource checkUser(@PathVariable String userName) {
		Optional<YsUser> user = userRepository.findByUsername(userName) ;
		return new RegisterResponseResource(user.isPresent());
		
//		return "none";
	}

/*	@RequestMapping(value="/register", method=RequestMethod.POST)
	RegisterResponseResource putRegister(
			@RequestBody Registration newRegistration,
			HttpServletResponse response) {
		
//		ResponseEntity<?>putUser(@RequestBody YsUser newUser){
		System.out.println(newRegistration.getParentName());
		System.out.println(newRegistration.getParentEmail());
		System.out.println(newRegistration.getPassword());
		System.out.println(newRegistration.getRecaptcha());
//		Optional<YsUser> curUser = userRepository.findByParentname(
//				"a");
//		System.out.println(curUser.get().getId());
//		this.verifyReCaptcha(newRegistration.getRecaptcha());
			YsUser user = new YsUser(
					"", // username 
					newRegistration.getParentName(),
					newRegistration.getParentEmail(),
					bCryptPasswordEncoder.encode(
							newRegistration.getPassword()));
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Headers", 
					"Accept, Accept-Encoding, Accept-Language, Cache-Control, Connection, Content-Length, Content-Type, Host, Origin, Pragma, Referer, User-Agent");
			try {
				YsUser repUser = userRepository.save(user);
				
			} catch (DataIntegrityViolationException e) {
				System.out.println("execept");
				return new RegisterResponseResource(false);
				
			}
//			System.out.println(repUser.getParentName());
//			Link forUser = new YsUserResource(repUser).getLink("self");
//			System.out.println(forUser.getHref());
//			return ResponseEntity.created(URI.create(forUser.getHref())).build();

//		Optional<User> user = this.userRepository.findById(userid);
//		if(user.isPresent()) {
//			Transaction transaction = new Transaction(
//					newTransaction.getDate(),
//					newTransaction.getName(),
//					newTransaction.getAmount(),
//					user.get()
//					);
//			Transaction repTransaction = transactionRepository.save(transaction);
//			Link forTransaction = new TransactionResource(repTransaction).getLink("self");
//			
//		}
//
//		return null;
		return new RegisterResponseResource(true) ;
	}*/

//	@RequestMapping(value="/transactions/{userid}", method=RequestMethod.POST)
//	ResponseEntity<?>putTransaction(@PathVariable Long userid, @RequestBody Transaction newTransaction){
//		Optional<User> user = this.userRepository.findById(userid);
//		if(user.isPresent()) {
//			Transaction transaction = new Transaction(
//					newTransaction.getDate(),
//					newTransaction.getName(),
//					newTransaction.getAmount(),
//					user.get()
//					);
//			Transaction repTransaction = transactionRepository.save(transaction);
//			Link forTransaction = new TransactionResource(repTransaction).getLink("self");
//			
//		}
//
//		return null;
//	}
	
	@RequestMapping(value="/secure", method=RequestMethod.GET)
	String getSecure(){
		return "yup, secure";
	}
	
//	@RequestMapping(value = "/users", method = RequestMethod.POST)
//	ResponseEntity<?>putUser(@RequestBody YsUser newUser){
//		YsUser user = new YsUser(
//				newUser.getUsername(), 
//				newUser.getParentname(), 
//				newUser.getPassword());
//		YsUser repUser = userRepository.save(user);
//		System.out.println(repUser.getParentname());
//		Link forUser = new YsUserResource(repUser).getLink("self");
//		System.out.println(forUser.getHref());
//		return ResponseEntity.created(URI.create(forUser.getHref())).build();
////		return null ;
//	}
	
	@RequestMapping(value = "/{userid}/transactions", 
			method = RequestMethod.POST)
	ResponseEntity<?>putTransaction(
			@PathVariable Long userid, 
			@RequestBody Transaction newTransaction){
		Optional<YsUser> user = userRepository.findById(userid);
		if(user.isPresent()) {
			Transaction transaction = new Transaction(
					newTransaction.getDate(), 
					newTransaction.getName(), 
					newTransaction.getAmount(), 
					user.get());
			Transaction repTransaction = transactionRepository.save(transaction);
			Link forTransaction = new TransactionResource(repTransaction).getLink("self");
			return ResponseEntity.created(URI.create(forTransaction.getHref())).build();
		}
		return null ;
	}
/*
	{"date": "20180309 00:00:00Z",
		"Name": "First Transaction",
		"Amount": 1234}
*/		
	
/*
{ "username": "randol",
"parentname": "gknight4",
"password": pass}
 */
	
	
	String getParentName(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
	}
	
/*	public class ChildBalance {
		public Long id ;
		public YsUser user ;
		public Long balance ;
		public ChildBalance(Long id, YsUser user, Long amt) {
			this.id = id; 
			this.user = user;
			this.balance = amt ;}

		@Override
		public boolean equals(Object obj) {
			return ((obj != null) && 
					(ChildBalance.class == obj.getClass()) &&
					(((ChildBalance)obj).id.equals(id))) ;
		}
		
		public void addBalance(Long amt) {
			this.balance += amt ;
		}
	}*/
	
/*	private class UserBalanceReturn{
		public String username;
		public Long amount;
		public Long id;
		public UserBalanceReturn(String username,
				Long amount,
				Long id) {
			this.username = username ;
			this.amount = amount ;
			this.id = id ;
		}
	}*/
	
/*	private List<ChildBalance> getBalances(Transaction [] trans){
		int i ;
		List<ChildBalance> listCb = new ArrayList<ChildBalance>();
		ChildBalance cb ;
		Long amt ;
		int pos ;
		for (i = 0 ; i < trans.length ; i++) {
			amt = new Long(trans[i].getAmount());
			cb = new ChildBalance(
					trans[i].getUser().getId(),
					trans[i].getUser(),
					amt);
			pos = listCb.indexOf(cb);
			if (pos >= 0) {
				listCb.get(pos).addBalance(amt);
			} else {
				listCb.add(cb);
			}
		}
		System.out.println(listCb.get(0).balance);
		return listCb ;
	}*/
	
//yourstash.tr8.us:3000/transactions/60	
	
//	@RequestMapping(value = "/transactions/{transactionid}", 
//			method = RequestMethod.OPTIONS)
//	ResponseEntity<?>approveTrans(){
//		System.out.println("options");
//		return null ;
//
//	}
	
	@RequestMapping(value="/transactions", method=RequestMethod.POST)
	SuccessResponse addTransaction(@RequestBody Transaction newTrans,
			HttpServletRequest request,
			HttpServletResponse response
			){
		String parentname = getParentName(request.getHeader(HEADER_STRING));
		String username = parentname + "/" + newTrans.getUser().getUsername();
		Optional<YsUser> user = userRepository.findByUsername(username);
		if(user.isPresent()) {
			System.out.println("parent: " + user.get().getParentname());
			Transaction saveTrans = new Transaction(
					newTrans.getDate(),
					newTrans.getName(),
					newTrans.getAmount(),
					user.get());
			transactionRepository.save(saveTrans);
			System.out.println("posting transaction");
			System.out.println("username: " + username);
			return new SuccessResponse(true) ;
		} else {
			return new SuccessResponse(false) ;
			
		}
	}

	@RequestMapping(value="/transactions/{transactionid}",
			method=RequestMethod.DELETE)
	SuccessResponse deleteTransaction(
			@PathVariable Long transactionid,
			HttpServletRequest request,
			HttpServletResponse response
			) {
		Optional<Transaction> trans = transactionRepository.findById(transactionid);
		if(trans.isPresent()) {
			transactionRepository.deleteById(transactionid);
			return new SuccessResponse(true) ;
		} else {
			return new SuccessResponse(false) ;
		}
	}
			
	@RequestMapping(value = "/transactions/{transactionid}", 
			method = RequestMethod.PUT)
	ResponseEntity<?>updateChildTransaction(
			@PathVariable Long transactionid, 
			@RequestBody Transaction newTransaction,
			HttpServletRequest request,
			HttpServletResponse response
			){
		Optional<Transaction> trans = transactionRepository.findById(transactionid);
		if(trans.isPresent()) {
			Transaction gotTrans = trans.get();
			gotTrans.setDate(newTransaction.getDate());
			gotTrans.setName(newTransaction.getName());
			gotTrans.setAmount(newTransaction.getAmount());
			transactionRepository.save(gotTrans);
		}
/*		System.out.println("posting: " + transactionid);
		System.out.println(newTransaction.getDate());
		
		Optional<Transaction> trans = transactionRepository.findById(transactionid);
//		System.out.println(transactionid);
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS");
//		response.setHeader("Access-Control-Allow-Headers", 
//				"Accept, Accept-Encoding, Accept-Language, Cache-Control, Connection, Content-Length, Content-Type, Host, Origin, Pragma, Referer, User-Agent");
		if(trans.isPresent()) {
			System.out.println("got it");
//			trans.
//			Transaction transaction = new Transaction(
//					newTransaction.getDate(), 
//					newTransaction.getName(), 
//					newTransaction.getAmount(), 
//					user.get());
//			Transaction repTransaction = transactionRepository.save(transaction);
//			Link forTransaction = new TransactionResource(repTransaction).getLink("self");
//			return ResponseEntity.created(URI.create(forTransaction.getHref())).build();
		}*/
		return null ;
	}

	@RequestMapping(value = "/transactions/{transactionid}", 
			method = RequestMethod.GET)
	TransactionResource getTransaction(
			@PathVariable Long transactionid,
			HttpServletRequest request,
			HttpServletResponse response
			) {
		System.out.println("getting: " + transactionid);
		Optional<Transaction> trans = this.transactionRepository
				.findById(transactionid);
		if(trans.isPresent()) {
			return new TransactionResource(trans.get());
		
		} else {
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException  iox) {
			}
			return null ;
		}
	}

		
	@RequestMapping(value = "/transactions", method = RequestMethod.GET)
	Transaction [] getUserTransactions(
			@RequestParam(required=false) String username,
			HttpServletRequest request
			) {
//		System.out.println("get transactions for" + username);
		if (username != null) {
			String parentname = getParentName(request.getHeader(HEADER_STRING));
			username = parentname + "/" + username ;
//			System.out.println("user: " + username);
			List<Transaction> listTrans = transactionRepository
					.findByUserUsername(username)
					.stream()
					.collect(Collectors.toList()) ; 
			Transaction [] arrayTrans = new Transaction[listTrans.size()];
			arrayTrans = listTrans.toArray(arrayTrans) ;
			
			return arrayTrans ;
		} else {
			String parentname = getParentName(request.getHeader(HEADER_STRING));
//			System.out.println("So parent: " + parentname);
//			parentname = "a";
			List<Transaction> listTrans = transactionRepository
					.findByUserParentname(parentname)
					.stream()
					.collect(Collectors.toList()) ; 
			Transaction [] arrayTrans = new Transaction[listTrans.size()];
//			System.out.println("responses: " + listTrans.size());
			arrayTrans = listTrans.toArray(arrayTrans) ;
			return arrayTrans ;

		}
//		return null ;
	}
/*	@RequestMapping(value = "/transactions", method = RequestMethod.GET)
	Transaction [] getTransactions(
			HttpServletRequest request
			) {
		String parentname = getParentName(request.getHeader(HEADER_STRING));
		List<Transaction> listTrans = transactionRepository
				.findByUserParentname(parentname)
				.stream()
				.collect(Collectors.toList()) ; 
		Transaction [] arrayTrans = new Transaction[listTrans.size()];
		arrayTrans = listTrans.toArray(arrayTrans) ;
		
//		System.out.println(userid);
//		Collection<Transaction> colTransaction = transactionRepository.findByUserId(userid);
//		Stream<TransactionResource> strTransaction = colTransaction.stream().map(TransactionResource::new);
//		List<TransactionResource> transactionList = strTransaction.collect(Collectors.toList()) ;
		return arrayTrans ;
	}*/
	
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	YsUser [] getUsers(
			HttpServletRequest request,
			HttpServletResponse response) {
		int i ;
		String parentname = getParentName(request.getHeader(HEADER_STRING));
		List<YsUser> listYsUser = userRepository
				.findByParentname(parentname)
				.stream()
				.collect(Collectors.toList()) ; 
		YsUser [] arrayYsUser = new YsUser[listYsUser.size()];
		arrayYsUser = listYsUser.toArray(arrayYsUser) ;
		
//		List<Transaction> listTrans = transactionRepository
//				.findByUserParentname(parentname)
//				.stream().collect(Collectors.toList());
//		List<YsUser> listYsUser = userRepository
//				.findByParentname(parentname)
//				.stream()
//				.collect(Collectors.toList()) ; 
//		for (i = 0 ; i < listYsUser.size() ; i++) {
//			listTrans.add(new Transaction(null, "", 0, 
//					listYsUser.get(i)));
//		}
//
//		Transaction [] arrayTrans = new Transaction[listTrans.size()] ;
//		arrayTrans = listTrans.toArray(arrayTrans);
//		List<ChildBalance> cbList = getBalances(arrayTrans);
//		UserBalanceReturn [] ubr = new UserBalanceReturn[cbList.size()];
//		for (i = 0 ; i < cbList.size() ; i++) {
//			ub
//			System.out.println(cbList.get(i).id);
//		}
		
		
		
//		Collection<YsUser> colUsers = userRepository
//		.findByParentname(parentname);
//		Stream<YsUser>x = colUsers.stream() ; // .map(u -> System.out.println(u.getUsername));
//		x.map(YsUserResource::new);
//		List<YsUser> listYsUser = colUsers.stream().collect(Collectors.toList()) ;
//		int i ;
//		for (i = 0 ; i < arrayTrans.length ; i++) {
//			System.out.println(arrayTrans[i].getName());
//		}
			return arrayYsUser ;
	}
	
/*
	Resources<TransactionResource> getTransactions(
			@RequestParam(value="userid") Long userid, 
			HttpServletResponse response) {
		System.out.println(userid);
		Collection<Transaction> colTransaction = 
		transactionRepository.findByUserId(userid);
		Stream<TransactionResource> strTransaction = 
		colTransaction.stream().map(TransactionResource::new);
		List<TransactionResource> transactionList = 
		strTransaction.collect(Collectors.toList()) ;
		return new Resources<TransactionResource>(transactionList) ;
*/	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	SuccessResponse addUser(
		@RequestBody AddChild addChild,
		HttpServletRequest request,
		HttpServletResponse response) {
		String parentname = getParentName(request.getHeader(HEADER_STRING));
//        String token = request.getHeader(HEADER_STRING);
//        String parentname = Jwts.parser()
//                .setSigningKey(SECRET.getBytes())
//                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
//                .getBody()
//                .getSubject();
		YsUser user = new YsUser(
				parentname + "/" + addChild.getUsername(), // username 
				parentname,//parentname
				"",// parentEmail
				bCryptPasswordEncoder.encode(
						addChild.getPassword()),
				0L);// confirmCode
		user.setFlags(USER_FLAG_CONFIRMED);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", 
				"Accept, Accept-Encoding, Accept-Language, Cache-Control, Connection, Content-Length, Content-Type, Host, Origin, Pragma, Referer, User-Agent");
		try {
			YsUser repUser = userRepository.save(user);
			
		} catch (DataIntegrityViolationException e) {
			System.out.println("execept");
			return new SuccessResponse(false) ;
		}
		System.out.println(addChild.getUsername());
		System.out.println("found parent: " + parentname);
		return new SuccessResponse(true) ;

	}
			
	
	
	@RequestMapping(value = "/users/{userid}", method = RequestMethod.GET)
	YsUserResource getUser(
			@PathVariable Long userid, 
			HttpServletResponse response) {
		System.out.println(userid);
		Optional<YsUser> user = this.userRepository.findById(userid);
		if(user.isPresent()) {
			return new YsUserResource(user.get());
		
		} else {
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException  iox) {
			}
			return null ;
		}
//	String testTwo() {
	}
	
	@RequestMapping(value="/{transactionid}/transactions", 
			method=RequestMethod.PUT)
	ResponseEntity<?>updateTransaction(@PathVariable Long transactionid,
			@RequestBody Transaction newTransaction){
		Optional<Transaction> transaction = transactionRepository.findById(transactionid);
		System.out.println("mod");
		if(transaction.isPresent()) {
			Transaction modTransaction = transaction.get() ; 
			modTransaction.setName("changed");
//			transactionRepository..updateTransaction(transactionid, modTransaction);
		}
			
//			Transaction transaction = new Transaction(
//		}
//				newTransaction.getDate(), 
//				newTransaction.getName(), 
//				newTransaction.getAmount(), 
//				user.get());
//				);
		return null ;
	}
	
	@RequestMapping(value="/{transactionid}/transactions", 
			method=RequestMethod.DELETE)
	ResponseEntity<?>deleteTransaction(@PathVariable Long transactionid){
		Optional<Transaction> transaction = transactionRepository.findById(transactionid);
		if(transaction.isPresent()) {
			transactionRepository.deleteById(transactionid);
			return new ResponseEntity<YsUser>(HttpStatus.NO_CONTENT);
		     			
		} else {
		    return new ResponseEntity<YsUser>(HttpStatus.NOT_FOUND);			
		}
	}
	
//	@RequestMapping(value = "/transactions", method = RequestMethod.GET)
//	Resources<TransactionResource> getTransactions(
//			@RequestParam(value="userid") Long userid, 
//			HttpServletResponse response) {
//		System.out.println(userid);
//		Collection<Transaction> colTransaction = transactionRepository.findByUserId(userid);
//		Stream<TransactionResource> strTransaction = colTransaction.stream().map(TransactionResource::new);
//		List<TransactionResource> transactionList = strTransaction.collect(Collectors.toList()) ;
//		return new Resources<TransactionResource>(transactionList) ;
//
//	}
	
//	@RequestMapping(value = "/{transactionid}/transactions", 
//			method = RequestMethod.GET)
//	TransactionResource getTransaction(
//			@PathVariable Long transactionid, 
//			HttpServletResponse response) {
////		System.out.println(transactionid);
//		Optional<Transaction> transaction = this.transactionRepository.findById(transactionid);
//		if(transaction.isPresent()) {
//			return new TransactionResource(transaction.get());
//		} else {
//			try {
//				response.sendError(HttpServletResponse.SC_NOT_FOUND);
//			} catch (IOException  iox) {}
//			return null ;
//		}
//	}
	
	
	@RequestMapping(method = RequestMethod.GET)
	String readTest() {
		System.out.println("getting");
		Optional<YsUser> user = userRepository.findById(1) ;
		if (user.isPresent()) {
			System.out.println("got user");
			System.out.println(user.get().getUsername());
		}
		return "api here";

	}
	
//	List<BookmarkResource> bookmarkResourceList = bookmarkRepository
//			.findByAccountUsername(userId).stream().map(BookmarkResource::new)
//			.collect(Collectors.toList());	
	

}
//end::code[]
