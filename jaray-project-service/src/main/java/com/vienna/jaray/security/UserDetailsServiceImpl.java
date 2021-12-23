package com.vienna.jaray.security;

import com.vienna.jaray.common.Separator;
import com.vienna.jaray.common.SysMenuConfig;
import com.vienna.jaray.entity.system.*;
import com.vienna.jaray.mapper.system.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 用户详情服务实现类
 */
@Slf4j
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	private static final String USERNAME_ADMIN = "admin";
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private SysRoleMenuMapper sysRoleMenuMapper;
	@Autowired
	private SysMenuMapper sysMenuMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("用户登录："+username);
		
		if (StringUtils.isEmpty(username)) {
			return null;
		}
		SysUser sysUser = sysUserMapper.findByName(username);
		if(USERNAME_ADMIN.equalsIgnoreCase(username)){
			// 1、管理员查询所有权限菜单对象
			List<SysMenu> sysMenuList = sysMenuMapper.findBtnAll();

			// 2、获取所有权限
			Set<String> permissions = sysMenuList.stream().map(SysMenu::getPerm).collect(Collectors.toSet());

			//这里使用自定义权限列表的方式初始化权限
			List<GrantedAuthority> grantedAuthorities = new ArrayList<> ();
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			grantedAuthorities.add(new SimpleGrantedAuthority("delete"));
			// 3、生成权限对象集合
			grantedAuthorities = permissions.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());

			// 4、赋予用户权限,注意：大写P，Passw0rd
			return new User(sysUser.getName(), sysUser.getPassword(), grantedAuthorities);
		}else{
			// 1、获取角色
			List<SysUserRole> sysUserRoleList = sysUserRoleMapper.findByUserId(sysUser.getId());
			List<Integer> roleIdList = sysUserRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

			List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.findByRids(roleIdList);

			// 2、获取用户所有角色的权限id
			List<Integer> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

			// 3、获取用户所有角色的所有权限菜单对象
			List<SysMenu> sysMenuList = sysMenuMapper.findByIds(menuIdList);

			// 4、获取所有权限
			Set<String> permissions = sysMenuList.stream().filter(sysMenu -> sysMenu.getType() == SysMenuConfig.BTN_FLAG).map(SysMenu::getPerm).collect(Collectors.toSet());

			//这里使用自定义权限列表的方式初始化权限
			List<GrantedAuthority> grantedAuthorities = new ArrayList<> ();
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_NORMAL"));

			// 5、生成权限对象集合
			grantedAuthorities = permissions.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());

			// 6、赋予用户权限
			return new User(sysUser.getName(), sysUser.getPassword(), grantedAuthorities);
		}
	}
}
