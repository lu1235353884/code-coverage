package com.digiwin.code.coverage.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description
 * 添加了一个资源处理器，它映射/reports/**这个URL模式到文件系统中的D:\jacoco\report_all目录。
 * 当访问例如http://localhost:8080/reports/index.html时，它会提供位于D:\jacoco\report_all\index.html的文件。
 * @CreateDate 2024-03-02 22:31
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/reports/**")
                .addResourceLocations("file:/D:/jacoco/report_all/");
    }
    /**
     * <template>
     *   <div>
     *     <button @click="openReport">打开报告</button>
     *   </div>
     * </template>
     *
     * <script>
     * export default {
     *   // ... 其他组件选项 ...
     *   methods: {
     *     openReport() {
     *       // 注意：如果你的Spring Boot应用部署在不同的域名或端口，你需要相应地更新这个URL
     *       const url = '/reports/bm-opsc-diff/index.html';
     *       window.open(url, '_blank');
     *     }
     *   }
     * };
     * </script>
     */
}
