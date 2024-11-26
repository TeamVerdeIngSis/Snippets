import org.springframework.aot.generate.Generated

@Generated
data class TestParseDTO(
    val version: String,
    val snippetId: String,
    val inputs: List<String>,
    val outputs: List<String>
)