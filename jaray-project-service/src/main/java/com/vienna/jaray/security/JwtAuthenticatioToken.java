package com.vienna.jaray.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: token认证
 */
public class JwtAuthenticatioToken extends UsernamePasswordAuthenticationToken {

private static final long serialVersionUID = 1L;
	
	private String token;
	private String expireTime;

    public JwtAuthenticatioToken(Object principal, Object credentials){
        super(principal, credentials);
    }
    
    public JwtAuthenticatioToken(Object principal, Object credentials, String token){
    	super(principal, credentials);
    	this.token = token;
    }

    public JwtAuthenticatioToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String token) {
    	super(principal, credentials, authorities);
    	this.token = token;
    }
    
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
