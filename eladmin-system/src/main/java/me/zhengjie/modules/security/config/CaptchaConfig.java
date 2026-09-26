/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version loginCode.length.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-loginCode.length.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.zhengjie.modules.security.config;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.Data;
import lombok.Getter;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.config.enums.LoginCodeEnum;
import me.zhengjie.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

/**
 * Login captcha configuration
 * @author liaojinlong
 * @date 2025-01-13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "login.code")
public class CaptchaConfig {

    /**
     * Captcha config
     */
    @Getter
    private LoginCodeEnum codeType;

    /**
     * Captcha validity (minutes)
     */
    private Long expiration = 5L;

    /**
     * Captcha content length
     */
    private int length = 4;

    /**
     * Captcha width
     */
    private int width = 111;

    /**
     * Captcha height
     */
    private int height = 36;

    /**
     * Captcha font
     */
    private String fontName;

    /**
     * Font size
     */
    private int fontSize = 25;

    /**
     * Generate the captcha from the configuration
     * @return /
     */
    public Captcha getCaptcha() {
        Captcha captcha;
        switch (codeType) {
            case ARITHMETIC:
                // Arithmetic type https://gitee.com/whvse/EasyCaptcha
                captcha = new FixedArithmeticCaptcha(width, height);
                // Number of digits for the operation, two by default
                captcha.setLen(length);
                break;
            case CHINESE:
                captcha = new ChineseCaptcha(width, height);
                captcha.setLen(length);
                break;
            case CHINESE_GIF:
                captcha = new ChineseGifCaptcha(width, height);
                captcha.setLen(length);
                break;
            case GIF:
                captcha = new GifCaptcha(width, height);
                captcha.setLen(length);
                break;
            case SPEC:
                captcha = new SpecCaptcha(width, height);
                captcha.setLen(length);
                break;
            default:
                throw new BadRequestException("Captcha configuration error! For the correct configuration see LoginCodeEnum ");
        }
        if(StringUtils.isNotBlank(fontName)){
            captcha.setFont(new Font(fontName, Font.PLAIN, fontSize));
        }
        return captcha;
    }

    static class FixedArithmeticCaptcha extends ArithmeticCaptcha {
        public FixedArithmeticCaptcha(int width, int height) {
            super(width, height);
        }

        @Override
        protected char[] alphas() {
            // Generate random numbers and operators
            int n1 = num(1, 10), n2 = num(1, 10);
            int opt = num(3);

            // Calculation result
            int res = new int[]{n1 + n2, n1 - n2, n1 * n2}[opt];
            // Convert to the operator character
            char optChar = "+-x".charAt(opt);

            this.setArithmeticString(String.format("%s%c%s=?", n1, optChar, n2));
            this.chars = String.valueOf(res);

            return chars.toCharArray();
        }
    }
}
