import lombok.extern.slf4j.Slf4j;

@Slf4j
// ApplicationContext will be loaded from AppConfig and TestConfig
//@Transactional
public abstract class AbstractTest {
    static {
        log.info("spring  config  inited ");
    }
}
