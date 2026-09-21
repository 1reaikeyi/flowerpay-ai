import generator.GeneratorApplication;
import generator.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GeneratorApplication.class)
public class CITest {

    @Autowired
    private Generator generator;
    @Test
    public void generate() {
        generator.generate("user");
    }
}
