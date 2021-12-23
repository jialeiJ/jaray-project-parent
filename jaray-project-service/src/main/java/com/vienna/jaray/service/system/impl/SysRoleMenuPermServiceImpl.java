package com.vienna.jaray.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.common.Separator;
import com.vienna.jaray.common.SysMenuConfig;
import com.vienna.jaray.entity.system.SysMenu;
import com.vienna.jaray.entity.system.SysRoleMenu;
import com.vienna.jaray.entity.system.SysRolePerm;
import com.vienna.jaray.mapper.system.SysMenuMapper;
import com.vienna.jaray.mapper.system.SysRoleMenuMapper;
import com.vienna.jaray.mapper.system.SysRolePermMapper;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysRoleMenuPermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jaray
 * @date 2020年09月12日 13:58
 * @description: 系统角色菜单权限服务实现类
 */
@Service
public class SysRoleMenuPermServiceImpl implements SysRoleMenuPermService {
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysRolePermMapper sysRolePermMapper;

    @Override
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        //设置分页信息(第几页，每页数量)
        PageHelper.startPage(commonParamsModel.getPageNum(), commonParamsModel.getPageSize());
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.findAll();
        //取记录总条数
        PageInfo<?> pageInfo = new PageInfo<>(sysRoleMenuList);
        return ResponseResult.success().add("sysRoleMenuList", pageInfo);
    }

    @Override
    public ResponseResult findByRid(String roleId) {
        // 1、查询角色权限
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.findByRid(roleId);
        List<Integer> sysPermPidList = new ArrayList<>();
        List<Integer> sysMenuIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysRoleMenuList)) {
            // 2、获取权限父ID
            List<Integer> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            List<SysMenu> sysMenuList = sysMenuMapper.findByIds(menuIdList);
            sysPermPidList = sysMenuList.stream().filter(sysMenu -> sysMenu.getType() == SysMenuConfig.BTN_FLAG).map(SysMenu::getId).distinct().collect(Collectors.toList());

            sysMenuIdList = sysMenuList.stream().filter(sysMenu -> sysMenu.getType() == SysMenuConfig.MENU_FLAG).map(SysMenu::getId).collect(Collectors.toList());
        }

        return ResponseResult.success().add("sysMenuIdList", sysMenuIdList).add("sysPermPidList", sysPermPidList);
    }

    @Override
    public ResponseResult add(SysRoleMenu sysRoleMenuEntity) {
        ResponseResult responseResult = ResponseResult.fail();
        int result = sysRoleMenuMapper.add(sysRoleMenuEntity);
        if(result > 0){
            responseResult = ResponseResult.success().add("sysRoleMenu", sysRoleMenuEntity);
        }
        return responseResult;
    }

    @Override
    public ResponseResult deleteByIds(String[] roleIds) {
        ResponseResult responseResult = ResponseResult.fail();
        int result = sysRoleMenuMapper.deleteByIds(roleIds);
        if(result > 0){
            responseResult = ResponseResult.success().add("result", result);
        }
        return responseResult;
    }

    @Override
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public ResponseResult updateByRid(Integer roleId, String menuPermId) {
        ResponseResult responseResult = ResponseResult.fail();

        int delResult = sysRoleMenuMapper.deleteByRid(roleId);

        int[] menuPermIds = Arrays.stream(menuPermId.split(Separator.COMMA_SEPARATOR_EN.getSeparator())).mapToInt(s -> Integer.parseInt(s)).toArray();
        List<Integer> menuPermIdList = Arrays.stream(menuPermIds).boxed().collect(Collectors.toList());

        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>(menuPermIdList.size());
        SysRoleMenu sysRoleMenu = null;
        for (Integer menuId: menuPermIdList) {
            sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);

            sysRoleMenuList.add(sysRoleMenu);
        }

        sysRoleMenuMapper.batchAdd(sysRoleMenuList);

        return ResponseResult.success();
    }
}