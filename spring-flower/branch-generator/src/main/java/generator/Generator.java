package generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import common.properties.JDBCProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Component
public class Generator {

    @Autowired
    private JDBCProperties jdbcProperties;

    public void generate(String tableName) {
         String model = "spring-flower/model/src/main/java/model/entity";
         String mapper = "spring-flower/mapper/src/main/java/mapper";
         String service = "spring-flower/service/src/main/java/service";
         String controller = "spring-flower/start/src/main/java/start/controller";
         String mapperXml = "spring-flower/mapper/src/main/resources/mapper";

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
                        .outputDir(model)
                        .disableOpenDir()
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(b -> b
                        .parent("")                    // 项目无统一父包，包名即模块名
                        .entity("model.entity")        // model 模块的 model.entity 包
                        .mapper("mapper")              // mapper 模块的 mapper 包
                        .service("service")            // service 模块的 service 包
                        .serviceImpl("service.impl")   // service 模块的 service.impl 包
                        .controller("start.controller") // start 模块的 start.controller 包
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
    }
}
