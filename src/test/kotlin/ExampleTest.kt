import com.github.teamverdeingsis.snippets.SnippetsApplication
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [SnippetsApplication::class]) // Cambia esto al nombre de tu clase principal
class ExampleTest {

    @Test
    fun sampleTest() {
        assert(true)
    }
}
