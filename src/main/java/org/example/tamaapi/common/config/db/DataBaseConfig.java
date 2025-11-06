package org.example.tamaapi.common.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.example.tamaapi.common.config.db.RoundRobinDataSource.DATASOURCE_KEY_MASTER;


@Configuration
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = {"org.example.tamaapi"})
public class DataBaseConfig {

    @Autowired
    private DataBaseProperty dataBaseProperty;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public List<DataSource> slaveDataSources() {
        List<DataSource> sources = new ArrayList<>();

        for (DataBaseProperty.DatabaseDetail slave : dataBaseProperty.getSlaves()) {
            sources.add(
                    DataSourceBuilder.create()
                            .type(HikariDataSource.class)
                            .url(slave.getJdbcUrl())
                            .username(slave.getUsername())
                            .password(slave.getPassword())
                            .driverClassName(slave.getDriverClassName())
                            .build()
            );
        }

        //System.out.println("dataBaseProperty.getMaster() = " + dataBaseProperty.getMaster());
        //System.out.println("dataBaseProperty.getSlaves() = " + dataBaseProperty.getSlaves());
        return sources;
    }

    @Bean
    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource master,
                                        @Qualifier("slaveDataSources") List<DataSource> slaves) {
        RoundRobinDataSource roundRobinDataSource = new RoundRobinDataSource(dataBaseProperty.getSlaves().size());
        HashMap<Object, Object> sources = new HashMap<>();
        sources.put(DATASOURCE_KEY_MASTER, master);

        //HashMap에 slave DataSource put하기
        int index = 0;
        for (DataSource slave : slaves) {
            sources.put("slave"+index, slave);
            ++index;
        }

        roundRobinDataSource.setTargetDataSources(sources);
        roundRobinDataSource.setDefaultTargetDataSource(master);
        return roundRobinDataSource;
    }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

}
