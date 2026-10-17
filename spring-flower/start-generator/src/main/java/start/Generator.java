package start;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class Generator {

    @Autowired
    private JDBCProperties jdbcProperties;

    public boolean generate(String tableName) {
         AtomicBoolean hasGenerateFile = new AtomicBoolean(false);
         String path = "spring-flower";
         String model = path + "/entity";
         String mapper = path + "/mapper";
         String service = path + "/service";
         String mapperXml = path + "/resource/mapper";
         String controller = path + "/controller";
         

        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.entity, model);      
        pathInfo.put(OutputFile.mapper, mapper);      
        pathInfo.put(OutputFile.service, service);     
        pathInfo.put(OutputFile.serviceImpl, service + "/impl");
        pathInfo.put(OutputFile.controller, controller);
        pathInfo.put(OutputFile.xml, mapperXml);              

        FastAutoGenerator.create(jdbcProperties.getUrl(),jdbcProperties.getUsername(),jdbcProperties.getPassword())
                .globalConfig(b -> b
                        .author("flower")
//                        .outputDir(model)
                        .disableOpenDir()
                        .commentDate("yyyy-MM-dd")
                )
                .injectionConfig(b -> b.beforeOutputFile((TableInfo tableInfo, Map<String, Object> objectMap) -> {
                    hasGenerateFile.set(true);
                    String entityName = tableInfo.getEntityName();
                    log.info("--- generate: 表{}，实体类名: {} ", tableName, entityName);
                    log.info("entity 路径: {}/{}.java", model, entityName);
                    log.info("mapper 接口: {}/{}Mapper.java", mapper, entityName);
                    log.info("mapper xml: {}/{}Mapper.xml", mapperXml, entityName);
                    log.info("service 接口: {}/{}Service.java", service, entityName);
                    log.info("service impl: {}/impl/{}ServiceImpl.java", service, entityName);
                    log.info("controller: {}/{}Controller.java", controller, entityName);
                }))
                .packageConfig(b -> b
                        .parent("com.branch")          // 项目统一父包
                        .entity("domain.entity")       // 实体包 com.branch.domain.entity
                        .mapper("mapper")              // Mapper 接口包 com.branch.mapper
                        .service("service")            // Service 接口包 com.branch.service
                        .serviceImpl("service.impl")   // Service 实现包 com.branch.service.impl
                        .controller("controller")      // Controller 包 com.branch.controller
                        .pathInfo(pathInfo)
                )

                .strategyConfig(b -> b
                        .addInclude(tableName)
                        .addTablePrefix("")
                        .entityBuilder()
//                                .enableFileOverride()
                                .naming(NamingStrategy.underline_to_camel)
                                .columnNaming(NamingStrategy.underline_to_camel)
                                .enableLombok()
                                .enableTableFieldAnnotation()
                                .idType(IdType.AUTO)
                        .mapperBuilder()
//                                .enableFileOverride()
                                .enableBaseResultMap()
                                .enableBaseColumnList()
                        .serviceBuilder()
//                                .enableFileOverride()
                                .formatServiceFileName("%sService")
                                .formatServiceImplFileName("%sServiceImpl")
                        .controllerBuilder()
//                                .enableFileOverride()
                                .enableRestStyle()
                                .enableHyphenStyle()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
        if(hasGenerateFile.get()){
            log.info(tableName + " 代码创建完成！");
            return true;
        }
        return false;
    }
}
