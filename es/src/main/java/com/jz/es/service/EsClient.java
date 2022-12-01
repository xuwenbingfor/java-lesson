package com.jz.es.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EsClient {
    @Value("${elasticsearch.rest.hosts}")//读取es主机+端口配置
    private String hosts;
    @Value("${elasticsearch.rest.username}")//读取es用户名
    private String esUser;
    @Value("${elasticsearch.rest.password}")//读取es密码
    private String esPassword;

    @Bean
    public ElasticsearchClient initClient1() {
        //tag::create-client
        // Create the low-level client
        String[] hostSplit = this.hosts.split(":");
        RestClient restClient = RestClient.builder(
                new HttpHost(hostSplit[0], Integer.parseInt(hostSplit[1]))).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        //end::create-client
        return client;
    }

/*    @Bean
    public RestHighLevelClient initClient1() {
        //根据配置文件配置HttpHost数组
        HttpHost[] httpHosts = Arrays.stream(hosts.split(",")).map(
                host -> {
                    //分隔es服务器IP和端口
                    String[] hostParts = host.split(":");
                    String hostName = hostParts[0];
                    int port = Integer.parseInt(hostParts[1]);
                    return new HttpHost(hostName, port, HttpHost.DEFAULT_SCHEME_NAME);
                }).filter(Objects::nonNull).toArray(HttpHost[]::new);
        //生成凭证
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(esUser, esPassword));//明文凭证
        //返回带验证的客户端
        return new RestHighLevelClient(
                RestClient.builder(
                        httpHosts)
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                httpClientBuilder.disableAuthCaching();
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }
                        }));
    }*/
}
