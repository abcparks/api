package cn.alex.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by WCY on 2022/9/9
 */
public class RequestUriBuilder {
    public static void main(String[] args) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("name", Arrays.asList("于谦", "王守仁"));
        multiValueMap.put("sex", Collections.singletonList("男"));
        multiValueMap.put("national", Collections.singletonList("中国"));
        multiValueMap.put("birthday", Collections.singletonList("1990-02-19"));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.baidu.com").queryParams(multiValueMap);
        System.out.println(builder.build().toUriString());
    }
}
