package com.github.walkvoid.wvframework.crypto.autoconfigure;

import com.github.walkvoid.wvframework.crypto.api.CryptoOperations;
import com.github.walkvoid.wvframework.crypto.api.TemplateCryptoOperations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Map;

@AutoConfiguration
@ConditionalOnClass(CryptoOperations.class)
@EnableConfigurationProperties(CryptoProperties.class)
@ConditionalOnProperty(prefix = "crypto", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CryptoAutoConfiguration {

    @Bean
    public static CryptoInstancesBeanRegistrar cryptoInstancesBeanRegistrar() {
        return new CryptoInstancesBeanRegistrar();
    }

    @Bean
    @ConditionalOnProperty(prefix = "crypto", name = "default-instance")
    @Primary
    public CryptoOperations primaryCryptoOperations(CryptoProperties properties,
                                                    ConfigurableListableBeanFactory beanFactory) {
        String name = properties.getDefaultInstance();
        if (!StringUtils.hasText(name) || !beanFactory.containsBean(name)) {
            throw new IllegalStateException("crypto.default-instance=" + name + " 未找到对应 Bean");
        }
        return beanFactory.getBean(name, CryptoOperations.class);
    }

    static final class CryptoInstancesBeanRegistrar implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
            if (!Boolean.parseBoolean(environment.getProperty("crypto.enabled", "true"))) {
                return;
            }
            Bindable<Map<String, CryptoProperties.InstanceConfig>> bindable =
                    Bindable.mapOf(String.class, CryptoProperties.InstanceConfig.class);
            Binder.get(environment).bind("crypto.instances", bindable).ifBound(instances -> {
                for (Map.Entry<String, CryptoProperties.InstanceConfig> e : instances.entrySet()) {
                    String beanName = e.getKey();
                    CryptoProperties.InstanceConfig cfg = e.getValue();
                    if (cfg.getAlgo() == null || !StringUtils.hasText(cfg.getAlgo().getType())) {
                        continue;
                    }
                    try {
                        CryptoContext ctx = CryptoInstanceFactory.build(cfg);
                        RootBeanDefinition def = new RootBeanDefinition();
                        def.setBeanClass(TemplateCryptoOperations.class);
                        def.getConstructorArgumentValues().addIndexedArgumentValue(0, ctx);
                        registry.registerBeanDefinition(beanName, def);
                    } catch (Exception ex) {
                        throw new BeanDefinitionStoreException("注册 crypto 实例失败: " + beanName, ex);
                    }
                }
            });
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        }
    }
}
