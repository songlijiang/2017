
import com.slj.springconfig.DataConfig;
import com.slj.springconfig.SpringServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from AppConfig and TestConfig
@ContextConfiguration(classes = { SpringServiceConfig.class, DataConfig.class})
//@Transactional
public abstract class AbstractTest {
    static {
        log.info("spring  config  inited ");
    }
}
