package com.njust.log_recommend.utils;

import cn.hutool.json.JSONObject;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranswarpUtil {


    //获取 随机行键
    public static String getRowKeyByRandom(){
        String uuid = UUID.randomUUID().toString();
        return DigestUtils.md5DigestAsHex(uuid.getBytes()).substring(0,8);
    }

    //获取星环数据库  日志  指定时间戳范围内的所有日志
    public static List<String> getLogByTimestamp(String fileName, String startTime, String endTime) throws IOException {
        List<String> log_list = new ArrayList<String>();
        boolean already = false;
        RandomAccessFile rf = null;
        rf = new RandomAccessFile(fileName, "r");
        long len = rf.length();
        long start = rf.getFilePointer();
        long nextend = start + len - 1;
        String line;
        rf.seek(nextend);
        int c = -1;
        while (nextend > start) {
            c = rf.read();
            if (c == '\n') {
                line = rf.readLine();
                if (line != null){
                    /*首先判断这行是不是以时间戳开头*/
                    /*如果是  且时间戳在在startTime到endTime范围内   则开始加入到结构列表*/
                    /*如果时间戳在  startTime  之前    直接结束*/
                    if(line.length()>19){
                        String year_month_now = line.substring(0,10);
                        if (isStartWithTimestamp(year_month_now)){
                            String timestamp_now = line.substring(0,19);
                            if(timestamp_now.compareTo(startTime) < 0){
                                break;
                            }else if (timestamp_now.compareTo(endTime) <= 0){
                                already = true;
                            }
                        }
                    }
                    /*if (already && line.contains("stmt")){*/
                    if (already){
                        log_list.add(0,line);
                    }
                }

            }
            nextend--;
            rf.seek(nextend);
        }
        rf.close();
        return log_list;
    }

    //获取日志中的有效字符  即涉及到 stmt 的  的那个命令涉及到的几行需要加入  result
    //为了减少响应时间  结果最多50行
    public static JSONObject getLogUseful(String fileName, String startTime, String endTime,int how_many) throws IOException {

        List<String> log_list_ori = getLogByTimestamp(fileName,startTime,endTime);
        List<String> log_list_res = new ArrayList<String>();

        boolean flag = false;
        int log_list_ori_size = log_list_ori.size();
        //限制  how_many  的范围
        how_many = Math.max(0,how_many);
        how_many = Math.min(log_list_ori_size/50,how_many);

        // 限制  开始行号  和   结束行号
        int start_num = 50*how_many;
        int end_num = Math.min(log_list_ori_size-1,50*(1+how_many)-1);

        for(int i = start_num;i <= end_num;i++){
            String line = log_list_ori.get(i);

            if(!flag && line.length()>19){
                String year_month_now = line.substring(0,10);
                if (isStartWithTimestamp(year_month_now) && line.contains("stmt")){
                    //从这行开始  直到下一个   时间戳出现之前
                    flag = true;
                }
            }
            if(flag){
                log_list_res.add(line);
            }
            //如果下一行以 时间戳  开始   则这个有效命令 结束
            if(i<end_num){
                line = log_list_ori.get(i+1);
                if(flag && line.length()>19){
                    String year_month_now = line.substring(0,10);
                    if (isStartWithTimestamp(year_month_now)){
                        flag = false;
                    }
                }
            }
        }




        JSONObject resJson = new JSONObject(true);
        resJson.set("log_list_ori_size",log_list_ori_size);
        resJson.set("log_list_res",log_list_res);
        resJson.set("start_num",start_num);
        resJson.set("end_num",end_num);

        return resJson;
    }

    //判断字符串是不是以日期开头
    //re.search(r"(\d{4}-\d{1,2}-\d{1,2}\s\d{1,2}:\d{1,2})",test_datetime)
    public static boolean isStartWithTimestamp(String year_month_now) {
        String eL= "^([0-9]{4})-([0-9]{2})-([0-9]{2})([\\s\\S]*)";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(year_month_now);
        boolean b = m.matches();
        return b;
    }

    /*将字符串转换为list*/
    public static List<String> javaStrToArray(String str_ori){
        //去除字符串 包含的空格  和  [ ]
        String str_2 = str_ori.replace("[","").replace("]","").replace(" ","");
        List<String> res = Arrays.asList(str_2.split(","));
        return res;
    }
    /*将字符串A转换为list 然后加入列表B*/
    public static List<String> javaStrToArrayAddToList(List<String> list1, String str1){

        List<String> list2 = new ArrayList<>(list1);

        for(String str_a:javaStrToArray(str1)){
            if(!list1.contains(str_a)){
                list2.add(str_a);
            }
        }
        return list2;
    }

    public static void main(String[] args) throws IOException {

//        List<String> log_list_res = getLogUseful("D:\\files\\code\\vs_code\\op_exception\\argodb-server2.audit","2021-09-14 22:50:43","2021-09-14 23:00:43",0);
//        for(String line:log_list_res){
//            System.out.println(line);
//        }

//        for (int i=0;i<10;i++){
//            String res = getRowKeyByRandom();
//            System.out.println(res);
//        }
    }
}
