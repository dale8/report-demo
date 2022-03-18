package com.epolsoft.reportdemo.config;

import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class JooqConfig {

    @Bean
    DataSourceConnectionProvider dataSourceConnectionProvider(@Autowired DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultConfiguration configuration(@Autowired DataSourceConnectionProvider connectionProvider) {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
        Settings settings = new Settings()
                .withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED)
                .withRenderNameCase(RenderNameCase.LOWER_IF_UNQUOTED);
        SQLDialect dialect = SQLDialect.POSTGRES;

        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider);
        jooqConfiguration.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));
        jooqConfiguration.setSettings(settings);
        jooqConfiguration.set(dialect);
        return jooqConfiguration;
    }

    @Bean
    public DefaultDSLContext dsl(@Autowired DefaultConfiguration configuration) {
        return new DefaultDSLContext(configuration);
    }
}
