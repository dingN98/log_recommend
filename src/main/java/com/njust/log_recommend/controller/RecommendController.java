package com.njust.log_recommend.controller;


import cn.hutool.json.JSONObject;
import com.njust.log_recommend.api.CommonResult;
import com.njust.log_recommend.utils.TranswarpUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/recommend")
public class RecommendController {


    @Value("${recommend.log_file_name}")
    private String log_file_name;

    private static final Logger LOG = LoggerFactory.getLogger(RecommendController.class);


    @ApiOperation(value = "获取星环数据库日志   指定时间戳范围内的   有效日志  默认一次返回50行日志 ")
    @RequestMapping(value = "/getLogUseful", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<JSONObject> getLogUseful(@RequestParam(value = "startTime") String startTime,
                                                 @RequestParam(value = "endTime") String endTime,
                                                 @RequestParam(value = "how_many") int how_many) throws IOException {
        /*"/var/log/inceptor1/argodb-server2.audit";*/
        /**
         * getLogUseful(String fileName, String startTime, String endTime,int how_many)
         * resJson.set("log_list_ori_size",log_list_ori_size);
         *         resJson.set("log_list_res",log_list_res);
         */
        LOG.info("触发  getLogUseful");
        JSONObject resJson = TranswarpUtil.getLogUseful(log_file_name,startTime,endTime,how_many);

//        String logs_lines = String.join("\n", log_list);
        return CommonResult.success(resJson);
    }

    @ApiOperation(value = "实时获取  服务器内星环数据库的  日志")
    @RequestMapping(value = "/getLogByTimestamp", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<JSONObject> getLogByTimestamp(String startTime,String endTime) throws IOException {

//        List<String> log_list = TranswarpUtil.getLogByTimestamp(log_file_name,"2021-10-24 10:30:52","2021-10-24 18:30:52");

        LOG.info("触发  getLogByTimestamp");
        List<String> log_list = TranswarpUtil.getLogByTimestamp(log_file_name,startTime,endTime);
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.set("log_list",log_list);

        return CommonResult.success(jsonObject);
    }

}
