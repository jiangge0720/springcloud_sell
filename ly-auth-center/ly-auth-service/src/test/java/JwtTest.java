import com.leyou.auth.LyAuthApplication;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Jiang-gege
 * 2019/12/815:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyAuthApplication.class)
public class JwtTest {

    private static final String pubKeyPath = "F:/upload/rsa/rsa.pub";

    private static final String priKeyPath = "F:/upload/rsa/rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        UserInfo userInfo = new UserInfo();
        userInfo.setId(20L);
        userInfo.setUsername("jack");
        String token = JwtUtils.generateToken(userInfo, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3NTc5MDU4MH0.Gtj_Vk-4zoS47kDfQ9Ew9RXJmbTfnVZkEx52wBK-W9ws--4pVJXe_ES_gYlsTpMuSgimPw35B4hX7qOGhvC4wkOj9rPwPOYpoKujW9i86_AY4nciM7QjqJLvfbr16KFHxViH_9bG_Zvo8fYntKfm7TbWZPg6gup9UL6xvfBzoi0";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
