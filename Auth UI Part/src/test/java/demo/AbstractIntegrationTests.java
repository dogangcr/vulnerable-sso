/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.test.BeforeOAuth2Context;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.client.token.grant.redirect.AbstractRedirectResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import demo.AbstractIntegrationTests.TestConfiguration;

@SpringApplicationConfiguration(classes = TestConfiguration.class, inheritLocations = true)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@IntegrationTest
public abstract class AbstractIntegrationTests {

	@Value("${local.server.port}")
	private int port;

	@Rule
	public MethodRule portSetter = new PortSetter();

	@Rule
	public ServerRunning serverRunning = ServerRunning.isRunning();

	@Rule
	public OAuth2ContextSetup context = OAuth2ContextSetup.standard(serverRunning);

	@Autowired
	private EmbeddedWebApplicationContext server;

	@Autowired(required = false)
	@Qualifier("consumerTokenServices")
	private ConsumerTokenServices tokenServices;

	@After
	public void cancelToken() {
		try {
			OAuth2AccessToken token = context.getOAuth2ClientContext().getAccessToken();
			if (token != null) {
				tokenServices.revokeToken(token.getValue());
			}
		} catch (Exception e) {
			// ignore
		}
	}

	@Before
	public void init() {
		serverRunning.setPort(port);
	}

	@BeforeOAuth2Context
	public void fixPaths() {
		init();
		BaseOAuth2ProtectedResourceDetails resource = (BaseOAuth2ProtectedResourceDetails) context
				.getResource();
		resource.setAccessTokenUri(serverRunning.getUrl("/oauth/token"));
		if (resource instanceof AbstractRedirectResourceDetails) {
			((AbstractRedirectResourceDetails) resource)
					.setUserAuthorizationUri(serverRunning.getUrl("/oauth/authorize"));
		}
		if (resource instanceof ImplicitResourceDetails) {
			resource.setAccessTokenUri(serverRunning.getUrl("/oauth/authorize"));
		}
	}

	@Configuration
	@PropertySource("classpath:test.properties")
	protected static class TestConfiguration {

	}

	public class PortSetter implements MethodRule {
		@Override
		public Statement apply(final Statement base, FrameworkMethod method,
				Object target) {
			serverRunning.setPort(server.getEmbeddedServletContainer().getPort());
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					base.evaluate();
				}
			};
		}
	}

}