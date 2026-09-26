package me.zhengjie.config.webConfig;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Convert Pageable for display in swagger
 */
@Configuration
public class SwaggerDataConfig {

    @Bean
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return CollUtil.newArrayList(newRule(resolver.resolve(Pageable.class), resolver.resolve(Page.class)));
            }
        };
    }

    @ApiModel
    @Data
    private static class Page {
        @ApiModelProperty("Page number (0..N)")
        private Integer page;

        @ApiModelProperty("Items shown per page")
        private Integer size;

        @ApiModelProperty("Sort criteria in the following format:property[,asc | desc]. The default sort order is ascending. Multiple sort conditions are supported, e.g.:id,asc")
        private List<String> sort;
    }
}
