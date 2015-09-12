package com.springmvc.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by xiaoxiao7 on 2015/9/5.
 */

@Controller
 public class BootstrapController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(){
        return "test";
    }
}
