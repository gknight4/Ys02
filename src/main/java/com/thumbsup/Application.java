package com.thumbsup;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableWebSecurity
@Configuration

@ComponentScan(basePackages = {"com.thumbsup.jwt", "com.thumbsup"})

@ImportResource( {"classpath:security.xml"} )  
public class Application {

//	@Autowired
//    private AuthenticationManager authenticationManager;

	@Bean(name="passwordEncoder")
    public BCryptPasswordEncoder passwordEncoder() {
    	BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
    	return bcpe;
    }
    
  @Bean(name="dataSource")
  public DataSource dataSource() {
  	DriverManagerDataSource dmds = new DriverManagerDataSource();
  	dmds.setDriverClassName("com.mysql.jdbc.Driver");
  	dmds.setUsername("jdbc");
  	dmds.setPassword("jdbc");
  	dmds.setUrl("jdbc:mysql://localhost/TEST");
  	return dmds ;
  }
  
//  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//      return super.authenticationManagerBean();
//  }
  
//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//  return super.authenticationManagerBean();
//  }  
  
	@Endpoint(id="mypoint")
	@Component
	public class myPointEndPoint {
		@ReadOperation
		public String mypoint(){
			return "Hello" ;
		}
	}
  

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

/*
how deploy:
pom.xml needs:
	<packaging>war</packaging>
	<scope>compile</scope> in "mysql-connector-java", was "runtime"
	
add this class:
package com.thumbsup;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MyappApplication.class);
	}

}

then "mvn clean package" in the Ys01 directory

tomcat can be untarred to the /opt/tomcat8 directory. the ownership of the dirs
has to be set to "tomcat"
groupadd tomcat
useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
chgrp -R tomcat /opt/tomcat
chmod -R g+r conf
chmod g+x conf
chown -R tomcat webapps/ work/ temp/ logs/

create tomcat.conf in /etc/init:
description "Tomcat Server"

  start on runlevel [2345]
  stop on runlevel [!2345]
  respawn
  respawn limit 10 5

  setuid tomcat
  setgid tomcat

# these have to be set
  env JAVA_HOME=/usr/lib/jvm/java-8-oracle
  env CATALINA_HOME=/opt/tomcat8

  # Modify these options as needed
  env JAVA_OPTS="-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom"
  env CATALINA_OPTS="-Xms512M -Xmx1024M -server -XX:+UseParallelGC"

  exec $CATALINA_HOME/bin/catalina.sh run

  # cleanup temp directory after stop
  post-stop script
    rm -rf $CATALINA_HOME/temp/*
  end script
********************** end ********************
*then use
initctl restart tomcat
etc. to start, stop

rename the war file to Ys01 and put in webapps, and it will appear at 
localhost:8080/Ys01

For Apache:
enable the proxy and proxy_http modules
create a proxies.conf:
ProxyPass         /api  http://localhost:8080/Ys01
ProxyPassReverse  /api  http://localhost:8080/Ys01







*/


