import generator.FlowerGeneratorApplication;
import generator.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = FlowerGeneratorApplication.class)
public class CITest {

    @Autowired
    private Generator generator;
    @Test
    public void generate() {
        generator.generate("user");
    }
}
