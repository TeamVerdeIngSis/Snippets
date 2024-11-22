import org.springframework.aot.generate.Generated

@Generated
data class TestParseDTO(
    val snippetId: Long,
    val inputs: List<String>,
    val outputs: List<String>
)