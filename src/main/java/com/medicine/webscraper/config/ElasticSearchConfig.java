package com.medicine.webscraper.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

	/**
	 * Search Elastic Search clients.
	 *
	 * @return the client
	 */
	@Bean(name = "Client")
	public RestHighLevelClient searchClient() {

		String[] ipAddressArray = new String[] {"localhost"};
		String[] portNumberArray = new String[] {"9200"};

		return new RestHighLevelClient(
				RestClient.builder(
						new HttpHost(ipAddressArray[0], Integer.parseInt(portNumberArray[0]), "http")));
	}
}
