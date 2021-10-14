package com.vienna.jaray.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Jaray
 * @date 2021年05月04日 19:40
 * @description:
 */
@Configuration
public class ActivitiDataSourceConfig extends DataSourceBaseConfig {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    @Autowired
    private Environment env;

    @Value("${spring.datasource.workflow.type}")
    private String dataSourceType;
    @Value("${spring.datasource.workflow.url}")
    private String url;
    @Value("${spring.datasource.workflow.username}")
    private String username;
    @Value("${spring.datasource.workflow.password}")
    private String password;

    /**
     * 工作流数据源
     * 返回workflow数据库的数据源
     * @return
     */
    @Bean(name="workflowDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.workflow")
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        //具体配置
        try {
            dataSource.setFilters("stat,wall,slf4j");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return dataSource;
        //return DataSourceBuilder.create().type(dataSourceType).build();

    }

    /**
     * 返回workflow数据库的会话工厂
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "workflowSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("workflowDataSource") DataSource dataSource) throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        String typeAliasesPackage = env.getProperty("mybatis.type-aliases-package");
        String configLocation = env.getProperty("mybatis.config-location");
        String mapperLocations = env.getProperty("mybatis.mapper-locations");
        bean.setDataSource(dataSource);
        //bean.setTypeAliasesPackage(typeAliasesPackage); // 单数据源
        bean.setTypeAliasesPackage(setTypeAliasesPackage(typeAliasesPackage));
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(configLocation));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return bean.getObject();
    }

    /**
     * 返回workflow数据库的会话模板
     * @param sessionFactory
     * @return
     * @throws Exception
     */
    @Bean(name = "activitiSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("workflowSqlSessionFactory") SqlSessionFactory sessionFactory) throws  Exception{
        return  new SqlSessionTemplate(sessionFactory);
    }

    /**
     * 返回master数据库的事务
     * @param dataSource
     * @return
     */
    @Bean(name = "workflowDataTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("workflowDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

//    private String setTypeAliasesPackage(String typeAliasesPackage) {
//        ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();
//        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
//        String[] typeAliasesPackages = typeAliasesPackage.split(Separator.COMMA_SEPARATOR_EN.getSeparator());
//        List<String> list = new ArrayList<>();
//        String result = "";
//        for(int i=0;i<typeAliasesPackages.length;i++){
//            typeAliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
//                    ClassUtils.convertClassNameToResourcePath(typeAliasesPackages[i]) + "/" + DEFAULT_RESOURCE_PATTERN;
//
//            //将加载多个绝对匹配的所有Resource
//            //将首先通过ClassLoader.getResource("META-INF")加载非模式路径部分
//            //然后进行遍历模式匹配
//            try {
//                Resource[] resources =  resolver.getResources(typeAliasesPackage);
//                if(resources != null && resources.length > 0){
//                    MetadataReader metadataReader = null;
//                    for(Resource resource : resources){
//                        if(resource.isReadable()){
//                            metadataReader =  metadataReaderFactory.getMetadataReader(resource);
//                            try {
//                                list.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
//                            } catch (ClassNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if(list.size() > 0) {
//            log.info("{}", StringUtils.join(list.toArray(), ","));
//            result = StringUtils.join(list.toArray(), Separator.COMMA_SEPARATOR_EN.getSeparator());
//        }else{
//            log.warn("参数setTypeAliasesPackage:"+typeAliasesPackage+"，未找到任何包");
//        }
//        //logger.info("d");
//        return result;
//    }
}
