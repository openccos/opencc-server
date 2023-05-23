package cc.smtweb.framework.web.entity;

import com.openccos.framework.core.annotation.CcColumn;
import com.openccos.framework.core.annotation.CcTable;
import lombok.Data;

@Data
@CcTable("iot_user.sys_dict")
public class DictPO {
  @CcColumn(type = CcColumn.Type.ID)
  private Long dictId;

//  @SwColumn(type = SwColumn.Type.MASTER_ID)
//  @SwColumnForeign(table="iot_user.sys_dict_type", id="dt_id", code="dt_name")
  private Long dictDtId;

  @CcColumn(type = {CcColumn.Type.CODE, CcColumn.Type.ORDER})
  private String dictCode;

  private String dictLabel;
}
