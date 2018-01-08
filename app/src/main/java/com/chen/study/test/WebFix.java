package com.chen.study.test;

import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PengChen on 2017/11/22.
 */

/**
 <script src="/kukuku/js/mm.js" type="text/javascript"></script>
 <meta http-equiv="Content-Type" content="text/html; charset=gbk">
 <title>玄幻魔法_全书网</title>
 <meta name="keywords" content="玄幻魔法,玄幻魔法小说,全书网" />
 <meta name="description" content="全书网提供最新玄幻魔法小说，阅读玄幻魔法小说就在全书网。" />
 <meta name="author" content="http://www.quanshuwang.com" />
 <meta name="generator" content="http://www.quanshuwang.com" />
 <meta http-equiv="X-UA-Compatible" content="IE=edge" />
 <meta name="renderer" content="webkit">
 <meta name="applicable-device" content="pc">
 <meta name="robots" content="all">
 <meta http-equiv="Cache-Control" content="no-transform" />
 <meta http-equiv="Cache-Control" content="no-siteapp" />
 <!--[if lt IE 9]>
 <script src="/kukuku/js/html5.js"></script>
 <![endif]-->
 <script language="javascript" type="text/javascript"  src="/kukuku/js/jquery-1.js"></script>
 <script language="javascript" type="text/javascript"  src="/kukuku/js/index.js"></script>
 <script language="javascript" type="text/javascript"  src="/kukuku/ku.js"></script>
 <script language="javascript" type="text/javascript" src="/kukuku/js/common.js"></script>
 <link href="/kukuku/css/common.css" rel="stylesheet" type="text/css">
 <link href="/kukuku/css/board.css" rel="stylesheet" type="text/css">
 <link href="/kukuku/css/book.css" rel="stylesheet" type="text/css">
 </head>



 *
 */
public class WebFix {

    public static void main(String[] args) {
        Set<String> treeSet = new TreeSet<>();
        try {
            String urlStr = "http://www.quanshuwang.com/list/1_1.html";
            URLConnection connection = HttpRequestUtil.sendGetRequest(urlStr, null, null);
            if ("text/html".equalsIgnoreCase(connection.getHeaderField("Content-Type"))) {
                String result = HttpRequestUtil.read2String(connection.getInputStream(), "gbk");
                System.out.println("result = " + result);

                String rex = "href=\"([^\"]*?)\"";
                Pattern pa = Pattern.compile(rex);
                Matcher matcher = pa.matcher(result);
                while (matcher.find()) {
                    String nextName = matcher.group(1);
                    if (nextName.startsWith("http") ) {
                        System.out.println("nextName = " + nextName);
                        treeSet.add(nextName);
                    } else if (nextName.startsWith("/")) {
                        String host = HttpRequestUtil.getHostName(urlStr);
                        System.out.println("host and nextName = " + host + nextName);
                        treeSet.add(host + nextName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public class ThreadPoolManager {
//        private ExecutorService executorService = Executors.newScheduledThreadPool(1);
//
//    }

}
