package npng.handdoc.telemed.util.naver;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;

@Configuration
public class NaverGrpcClient {

    @Value("${naver.clova.api-key-id}")
    private String apiKeyId;

    @Value("${naver.clova.api-key}")
    private String apiKey;

    @Value("${naver.clova.grpc.host}")
    private String host;

    @Value("${naver.clova.grpc.port}")
    private int port;

    @Bean
    public ManagedChannel clovaChannel() throws SSLException {
        return NettyChannelBuilder.forAddress(host, port)
                .sslContext(GrpcSslContexts.forClient().build())
                .build();
    }

    @Bean
    public Metadata clovaMetadata(){
        Metadata md = new Metadata();
        Metadata.Key<String> KEY_ID =
                Metadata.Key.of("X-NCP-APIGW-API-KEY-ID", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> KEY =
                Metadata.Key.of("X-NCP-APIGW-API-KEY", Metadata.ASCII_STRING_MARSHALLER);
        md.put(KEY_ID, apiKeyId);
        md.put(KEY, apiKey);
        return md;
    }

    @Bean
    public io.grpc.ClientInterceptor clovaAuthInterceptor(Metadata clovaMetadata) {
        return io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor(clovaMetadata);
    }
}
