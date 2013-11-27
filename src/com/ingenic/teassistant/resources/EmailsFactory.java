package com.ingenic.teassistant.resources;

import java.util.Random;

public class EmailsFactory {
    public static String[] POSTFIX = {
        //
        "@gmail.com",   "@yahoo.com",	"@msn.com",         "@hotmail.com",		"@aol.com", 
        "@ask.com",		"@live.com",	"@qq.com",          "@0355.net",		"@163.com", 
        "@163.net",     "@263.net",     "@3721.net",        "@yeah.net",        "@googlemail.com", 
        "@mail.com",    "@hotmail.com", "@msn.com",         "@aim.com",         "@aol.com",
        "@mail.com",    "@walla.com",   "@inbox.com",       "@126.com",         "@sina.com",
        "@21cn.com",    "@sohu.com",    "@yahoo.com.cn",    "@tom.com",         "@etang.com",
        "@eyou.com",    "@56.com",		"@x.cn",            "@chinaren.com",    "@sogou.com",
        "@citiz.com",   "@ingenic.cn",
//        China HongKong:
        "@hongkong.com",		"@ctimail.com",		"@hknet.com",		"@netvigator.com",   
        "@mail.hk.com",		"@swe.com.hk",		"@itcclop.com.hk",		"@biznetvigator.com", 
//        China TaiWan:
        "@seed.net.tw",		"@topmarkeplg.com.tw",		"@pchome.com.tw",   
    };
    public static String getEmail() {
        Random random = new Random();
        int randomIndex = random.nextInt(POSTFIX.length);
        String postfix = POSTFIX[randomIndex];
        return "Test" + random.nextInt(500) + postfix;
    }
}
