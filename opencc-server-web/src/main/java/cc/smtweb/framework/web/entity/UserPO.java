package cc.smtweb.framework.web.entity;

import com.openccos.framework.core.annotation.CcColumn;
import com.openccos.framework.core.annotation.CcTable;
import lombok.Data;

import java.io.Serializable;

@Data
@CcTable("iot_user.sys_user")
public class UserPO implements Serializable {
    @CcColumn(type={CcColumn.Type.ID})
    private Long userId;

    private String userName;

    private Long userSiteId;

//    @SwColumn(type={SwColumn.Type.CREATE_TIME})
//    private Long userCreateAt;

    @CcColumn(type={CcColumn.Type.LAST_TIME})
    private Long userDeptId;

    private String userPassword;

    private String userPhone;

    private Integer userStatus;

    private String userAvatar;
}
