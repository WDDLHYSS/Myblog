package com.wddlhyss.myblog;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Types;

@SpringBootTest
public class MybatisPlusGenerator {

    @Test
    public void test() {
        FastAutoGenerator.create("", "root", "")
                .globalConfig(builder -> {
                    builder.author("haoyanlu") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir(System.getProperty("user.dir")+ "/src/main/java")
                            .disableOpenDir().enableSpringdoc();// 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("com.wddlhyss.myblog") // 设置父包名
                                .controller("controller").mapper("mapper").xml("mapper.xml")
                                //.pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/java")) // 设置mapperXml生成路径
                )
                .strategyConfig(builder ->
                                builder.addInclude("schedule_rule","wx_user","schedule_rule_shift_order","schedule_rule_rotation_day","user_schedule_plan")
                        //builder.addInclude("DianJiChe_Locomotives","DianJiChe_mining","DianJiChe_mining_schedule","DianJiChe_production_tasks","DianJiChe_production_tasks1","DianJiche_single_locomotive_tasks","DianJiChe_stations","DianJiChe_TimeTable","DianJiChe_TransportReport") // 设置需要生成的表名
                )
                .execute();
    }
}
