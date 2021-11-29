package com.vienna.jaray.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vienna.jaray.common.ResponseResult;
import com.vienna.jaray.entity.system.SysLog;
import com.vienna.jaray.mapper.system.SysLogMapper;
import com.vienna.jaray.model.system.CommonParamsModel;
import com.vienna.jaray.service.system.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jaray
 * @date 2020年09月12日 13:58
 * @description: 系统日志服务实现类
 */
@Slf4j
@Service
public class SysLogServiceImpl implements SysLogService {
    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public ResponseResult findAll(CommonParamsModel commonParamsModel) {
        long start = System.currentTimeMillis();
        //设置分页信息(第几页，每页数量)
        PageHelper.offsetPage((commonParamsModel.getPageNum()-1) * commonParamsModel.getPageSize(), commonParamsModel.getPageSize());
        List<SysLog> logIdList = sysLogMapper.findIdsByPage(commonParamsModel);
        List<Integer> idList = logIdList.stream().map(SysLog::getId).collect(Collectors.toList());

        List<SysLog> sysLogList = sysLogMapper.findAllByPage(idList);
        // 取记录总条数
        PageInfo<SysLog> pageInfo = new PageInfo<>(logIdList);
        pageInfo.setList(sysLogList);
        long end = System.currentTimeMillis();
        log.info("耗时：{}", end - start);
        return ResponseResult.success().add("sysLogs", pageInfo);
    }

    @Override
    public ResponseResult findById(String id) {
        SysLog sysLog = sysLogMapper.findById(id);
        return ResponseResult.success().add("sysLog", sysLog);
    }

    @Override
    public ResponseResult findByName(String userName) {
        SysLog sysLog = sysLogMapper.findByName(userName);
        return ResponseResult.success().add("sysLog", sysLog);
    }

    @Override
    public ResponseResult add(SysLog sysLog) {
        ResponseResult responseResult = ResponseResult.fail();
        int result = sysLogMapper.add(sysLog);
        if(result > 0){
            responseResult = ResponseResult.success().add("sysLog", sysLog);
        }
        return responseResult;
    }

    @Override
    public ResponseResult deleteByIds(String[] ids) {
        ResponseResult responseResult = ResponseResult.fail();
        int result = sysLogMapper.deleteByIds(ids);
        if(result > 0){
            responseResult = ResponseResult.success().add("result", result);
        }
        return responseResult;
    }

    @Override
    public ResponseResult updateById(SysLog sysLog) {
        ResponseResult responseResult = ResponseResult.fail();
        int result = sysLogMapper.updateById(sysLog);
        if(result > 0){
            responseResult = ResponseResult.success().add("result", result);
        }
        return responseResult;
    }
}