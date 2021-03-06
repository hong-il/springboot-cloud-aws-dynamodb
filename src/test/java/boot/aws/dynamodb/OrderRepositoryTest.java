package boot.aws.dynamodb;

import boot.aws.dynamodb.domain.Order;
import boot.aws.dynamodb.domain.OrderRepository;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "embedded-dynamodb.use=true")
public class OrderRepositoryTest {

    @Autowired
    private AmazonDynamoDB dynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        CreateTableRequest createTableRequest = dynamoDbMapper.generateCreateTableRequest(Order.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
    }

    @AfterEach
    void after() {
        TableUtils.deleteTableIfExists(dynamoDB, dynamoDbMapper.generateDeleteTableRequest(Order.class));
    }

    @Test
    @DisplayName("Test save")
    void test_save() {
        //given
        String orderNo = "orderNo";
        long orderAmount = 1000L;

        //when
        orderRepository.save(Order.builder()
                .orderNo(orderNo)
                .orderAmount(orderAmount)
                .build());

        //then
        Order savedOrder = orderRepository.findAll().iterator().next();
        assertThat(savedOrder.getOrderNo()).isEqualTo(orderNo);
        assertThat(savedOrder.getOrderAmount()).isEqualTo(orderAmount);
    }
}