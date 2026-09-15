import generator.FlowerGeneratorApplication;
import generator.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = FlowerGeneratorApplication.class)
public class Test {

    @Autowired
    private Generator generator;

    public void generate() {
        generator.generate("");
    }
}
