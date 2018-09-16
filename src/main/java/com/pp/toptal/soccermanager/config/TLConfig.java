package com.pp.toptal.soccermanager.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.pp.toptal.soccermanager.controller.TLController;
import com.pp.toptal.soccermanager.controller.TestTLController;

@Configuration
public class TLConfig {
    
    public static final String PAGES_ROOT = "/manager/pages";
    
    private TemplateEngine templateEngine;
    
    private Map<String, TLController> controllersMap = new HashMap<>();
    
    private Map<String, String> pageToPageNameMap = new HashMap<>();
    
    /**
     * TODO: move to factory
     */
    @Autowired
    TestTLController testController;
    
    @PostConstruct
    private void init() {
        registerController("/tl_test", "TL test", testController);

        initializeTemplateEngine();
    }
    
    private void registerController(String page, TLController controller) {
        controllersMap.put(page, controller);
    }
    
    private void registerController(String page, String pageName, TLController controller) {
        registerController(page, controller);
        pageToPageNameMap.put(page, pageName);
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
    
    public TLController resolveController(String requestPath) {
        return controllersMap.get(requestPath);
    }
    
    public Map<String, String> getPageToPageNameMap() {
        return pageToPageNameMap;
    }
    
}
