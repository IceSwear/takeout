package com.kk.util;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;

/**
 * @Description: MybatisPlusGenerator
 * @Author: Spike Wong
 * @Date: 2022/6/5
 */

@Repository
public class MybatisPlusGenerator {


    @Value("${spring.datasource.driverClass}")
    private  String driverClass;
    @Value("${spring.datasource.jdbcUrl}")
    private  String dbUrl;
    @Value("${spring.datasource.user}")
    private  String userName;
    @Value("${spring.datasource.password}")
    private  String passWord;


    /**
     * table name is table name in database,
     * parentPackage ,eg:"com.kiseki","com.kk"
     * @param tableNames
     * @param parentPackage
     */
    public  void generateByTable(String parentPackage, String... tableNames) {
//        MybatisPlusGenerator mybatisPlusGenerator = new MybatisPlusGenerator();
        AutoGenerator autoGenerator = new AutoGenerator();
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriverName(driverClass);
        dataSourceConfig.setUrl(dbUrl);
        dataSourceConfig.setUsername(userName);
        dataSourceConfig.setPassword(passWord);
        autoGenerator.setDataSource(dataSourceConfig);

        //global config
        GlobalConfig globalConfig = new GlobalConfig();
        // catalog
        globalConfig.setOutputDir(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java");
        //whether open directory or not after generating
        globalConfig.setOpen(false);
        //Author
        globalConfig.setAuthor("Spike Wong");
        //default is false,it will oveerwrite the content if you change to true
        globalConfig.setFileOverride(true);
        //Mapper name
        globalConfig.setMapperName("%sMapper");
        //Service name
        globalConfig.setServiceName("%sService");
        //API/Control name
        globalConfig.setControllerName("%sAPI");
        globalConfig.setIdType(IdType.AUTO);
        globalConfig.setBaseColumnList(true);
        globalConfig.setBaseResultMap(true);

        autoGenerator.setGlobalConfig(globalConfig);

        //package name
        PackageConfig packageConfig = new PackageConfig();
        //TODO should check before generating
        packageConfig.setParent(parentPackage);
        //entities path name
        packageConfig.setEntity("model.entities");
        //persistence  path name
        packageConfig.setMapper("model.persistence");
        //api path name
        packageConfig.setController("api");
        //persistence(mapper interface)  path name
        packageConfig.setXml("model.persistence");
        //service path name
        packageConfig.setService("service");
        //impl path name
        packageConfig.setServiceImpl("service.impl");
        autoGenerator.setPackageInfo(packageConfig);

        //strategy config
        StrategyConfig strategyConfig = new StrategyConfig();
        //TODO should check before generating
        strategyConfig.setInclude(tableNames);
        //table prefix
//        strategyConfig.setTablePrefix("tb_");
        //turn on restfull style
        strategyConfig.setRestControllerStyle(true);
        //set logic filed name,already set in yml file
        // strategyConfig.setLogicDeleteFieldName("isDeleted");
        //set optimistic lock field name
        strategyConfig.setVersionFieldName("version");
        //turn on Lombok—— @Data
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setEntitySerialVersionUID(true);
        // Entities will be camel---eg: AddressBook
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        // Field name will be camel,  eg:provinceCode,isDeleted
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        //default is false, turn true will be like—— <result column="create_time" property="createTime" /> in mapper.xml
        strategyConfig.setControllerMappingHyphenStyle(true);
        autoGenerator.setStrategy(strategyConfig);

        //execute!
        autoGenerator.execute();

    }

}
