package com.pp.toptal.soccermanager.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.pp.toptal.soccermanager.controller.tl.ManagerTLController;
import com.pp.toptal.soccermanager.controller.tl.PageContentTLController;
import com.pp.toptal.soccermanager.controller.tl.TLControllerAbstract;
import com.pp.toptal.soccermanager.controller.tl.TestTLController;

@Configuration
public class TLConfig {
    
    public static final String PAGES_ROOT = "/pages";

    private TemplateEngine templateEngine;
    
    private Map<String, TLControllerAbstract> requestPathToControllerMap = new HashMap<>();
    
    /**
     * TODO: move to factory
     */
    @Autowired
    TestTLController testController;

    @Autowired
    ManagerTLController managerController;
    
    @Autowired
    PageContentTLController pageContentController;
    
    @PostConstruct
    private void init() {
        registerController("/tl_test", testController);
        
        registerController("/manager", managerController);
        
        registerController("/manager/users", pageContentController);
        registerController("/manager/teams", pageContentController); 

        initializeTemplateEngine();
    }
    
    public static Collection<String> getFilterUrlPatterns() {
        return Arrays.asList("/tl_test", "/manager/*");
    }
    
    private void registerController(String requestPath, TLControllerAbstract controller) {
        requestPathToControllerMap.put(requestPath, controller);
    }
    
    private void initializeTemplateEngine() {
        
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        
        templateResolver.setTemplateMode("HTML5");
        
        // This will convert f.e "home" to "/templates/home.html"
        templateResolver.setPrefix(PAGES_ROOT);
        templateResolver.setSuffix("/index.html");
        templateResolver.setCharacterEncoding("UTF-8");
        
        templateResolver.setCacheable(false);
        
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }
    
    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
    
    public TLControllerAbstract resolveController(String requestPath) {
        return requestPathToControllerMap.get(requestPath);
    }
    
}
