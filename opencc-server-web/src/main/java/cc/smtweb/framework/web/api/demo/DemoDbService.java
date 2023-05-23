package cc.smtweb.framework.web.api.demo;

import cc.smtweb.framework.web.entity.UserPO;
import com.openccos.framework.core.R;
import com.openccos.framework.core.db.DbEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoDbService {
  @Autowired
  DbEngine dbEngine;

  @GetMapping("/db")
  public R db() {
    UserPO userPO = dbEngine.queryEntity("select * from iot_user.sys_user where user_id=1", UserPO.class);

    return R.success(userPO);
  }

  @GetMapping("/ping")
  public R ping() {
    return R.success();
  }
}
