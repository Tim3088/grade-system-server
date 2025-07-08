package cn.zzb.grade;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Base64;

@SpringBootTest
class GradeSystemServerApplicationTests {

    @Autowired
    private DataSource dataSource;

    @org.junit.jupiter.api.Test
    public void testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("成功连接到 OpenGauss 数据库！");
            }
        } catch (Exception e) {
            System.err.println("连接失败：" + e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    public void jwt() {
        SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println(Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded()));
    }

}
