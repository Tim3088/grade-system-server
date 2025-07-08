package cn.zzb.grade;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 10723
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.zzb.grade.mapper")
@EnableMPP
public class GradeSystemServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradeSystemServerApplication.class, args);
    }

}
