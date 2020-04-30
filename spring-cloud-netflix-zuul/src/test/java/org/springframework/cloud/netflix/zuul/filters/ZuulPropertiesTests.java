/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.netflix.zuul.filters;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 * @author Mathias Düsterhöft
 */
public class ZuulPropertiesTests {

	private ZuulProperties zuul;

	@Before
	public void setup() {
		this.zuul = new ZuulProperties();
	}

	@After
	public void teardown() {
		this.zuul = null;
	}

	@Test
	public void defaultIgnoredHeaders() {
		assertThat(this.zuul.isIgnoreSecurityHeaders()).isTrue();
		assertThat(this.zuul.getIgnoredHeaders())
				.containsAll(ZuulProperties.SECURITY_HEADERS);
	}

	@Test
	public void securityHeadersNotIgnored() {
		zuul.setIgnoreSecurityHeaders(false);

		assertThat(this.zuul.getIgnoredHeaders().isEmpty()).isTrue();
	}

	@Test
	public void addIgnoredHeaders() {
		this.zuul.setIgnoredHeaders(Collections.singleton("x-foo"));
		assertThat(this.zuul.getIgnoredHeaders().contains("x-foo")).isTrue();
	}

	@Test
	public void defaultSensitiveHeaders() {
		ZuulRoute route = new ZuulRoute("foo");
		this.zuul.getRoutes().put("foo", route);
		assertThat(this.zuul.getRoutes().get("foo").getSensitiveHeaders().isEmpty())
				.isTrue();
		assertThat(this.zuul.getSensitiveHeaders()
				.containsAll(Arrays.asList("Cookie", "Set-Cookie", "Authorization")))
						.isTrue();
		assertThat(route.isCustomSensitiveHeaders()).isFalse();
	}

	@Test
	public void addSensitiveHeaders() {
		this.zuul.setSensitiveHeaders(Collections.singleton("x-bar"));
		ZuulRoute route = new ZuulRoute("foo");
		route.setSensitiveHeaders(Collections.singleton("x-foo"));
		this.zuul.getRoutes().put("foo", route);
		ZuulRoute foo = this.zuul.getRoutes().get("foo");
		assertThat(foo.getSensitiveHeaders().contains("x-foo")).isTrue();
		assertThat(foo.getSensitiveHeaders().contains("Cookie")).isFalse();
		assertThat(foo.isCustomSensitiveHeaders()).isTrue();
		assertThat(this.zuul.getSensitiveHeaders().contains("x-bar")).isTrue();
		assertThat(this.zuul.getSensitiveHeaders().contains("Cookie")).isFalse();
	}

	@Test
	public void createWithSensitiveHeaders() {
		this.zuul.setSensitiveHeaders(Collections.singleton("x-bar"));
		ZuulRoute route = new ZuulRoute("foo", "/path", "foo", "/path", false, false,
				Collections.singleton("x-foo"));
		this.zuul.getRoutes().put("foo", route);
		ZuulRoute foo = this.zuul.getRoutes().get("foo");
		assertThat(foo.getSensitiveHeaders().contains("x-foo")).isTrue();
		assertThat(foo.getSensitiveHeaders().contains("Cookie")).isFalse();
		assertThat(foo.isCustomSensitiveHeaders()).isTrue();
		assertThat(this.zuul.getSensitiveHeaders().contains("x-bar")).isTrue();
		assertThat(this.zuul.getSensitiveHeaders().contains("Cookie")).isFalse();
	}

	@Test
	public void defaultHystrixThreadPool() {
		assertThat(this.zuul.getThreadPool().isUseSeparateThreadPools()).isFalse();
		assertThat(this.zuul.getThreadPool().getThreadPoolKeyPrefix()).isEqualTo("");
	}

}
