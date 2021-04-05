package cn.ablxyw.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
