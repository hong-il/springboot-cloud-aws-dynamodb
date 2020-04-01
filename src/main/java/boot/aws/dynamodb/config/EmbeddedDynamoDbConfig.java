package boot.aws.dynamodb.config;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@RequiredArgsConstructor
@Configuration
@Profile("local") // (1)
@ConditionalOnProperty(name = "embedded-dynamodb.use", havingValue = "true") // (2)
public class EmbeddedDynamoDbConfig {

    private DynamoDBProxyServer server;

    @PostConstruct
    public void start() {
        if (server != null) {
            return;
        }

        try {
            AwsDynamoDbLocalTestUtils.initSqLite(); // (3)
            server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory"});
            server.start();
            log.info("Start Embedded DynamoDB");
        } catch (Exception e) {
            throw new IllegalStateException("Fail Start Embedded DynamoDB", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (server == null) {
            return;
        }

        try {
            log.info("Stop Embedded DynamoDB");
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("Fail Stop Embedded DynamoDB", e);
        }
    }
}