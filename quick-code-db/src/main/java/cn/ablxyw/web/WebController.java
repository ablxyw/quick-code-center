package cn.ablxyw.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static cn.ablxyw.constants.GlobalConstants.HTML_SUFFIX;
import static cn.ablxyw.constants.GlobalConstants.SLASH_CODE;

/**
 * webController
 *
 * @author weiqiang
 * @date 2021-04-05 下午3:46
 */
@Controller
public class WebController {
    /**
     * index.html
     */
    public static final String INDEX_HTML = "index.html";

    /**
     * index
     *
     * @return String
     */
    @GetMapping(value = {"", "/", "index", "/index", "index.htm", "index.html"})
    public String index() {
        return INDEX_HTML;
    }

    /**
     * 页面跳转
     *
     * @param module   模块名称
     * @param function 功能名称
     * @param url      url
     * @return String
     */
    @GetMapping("{module}/{function}/{url}.html")
    public String page(@PathVariable("module") String module, @PathVariable("function") String function, @PathVariable("url") String url) {
        return module + SLASH_CODE + function + SLASH_CODE + url + HTML_SUFFIX;
    }


    /**
     * 页面跳转
     *
     * @param module 模块名称
     * @param url    url
     * @return String
     */
    @GetMapping("{module}/{url}.html")
    public String page(@PathVariable("module") String module, @PathVariable("url") String url) {
        return module + SLASH_CODE + url + HTML_SUFFIX;
    }
}
