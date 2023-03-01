/*
 * Copyright (c) 2023 Macula
 *   macula.dev, China
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.macula.boot.starter.feign.interceptor;

import dev.macula.boot.constants.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * {@code HeaderRelayInterceptor} 将请求头传递到下面的微服务
 *
 * @author rain
 * @since 2022/7/23 12:57
 */
public class HeaderRelayInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();

            // 微服务之间传递的唯一标识,区分大小写所以通过httpServletRequest获取
            String sid = request.getHeader(GlobalConstants.FEIGN_REQ_ID);
            if (!StringUtils.hasText(sid)) {
                sid = String.valueOf(UUID.randomUUID());
            }
            template.header(GlobalConstants.FEIGN_REQ_ID, sid);

            // 传递Gateway生成的Authorization头
            String token = request.getHeader(GlobalConstants.AUTHORIZATION_KEY);
            if (StringUtils.hasText(token)) {
                template.header(GlobalConstants.AUTHORIZATION_KEY, token);
            }
        }
    }
}