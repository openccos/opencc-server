package com.openccos.framework.core.db;

import com.openccos.framework.core.spring.RuleProperties;
import com.openccos.framework.core.db.jdbc.IdGenerator;
import com.openccos.framework.core.db.jdbc.JdbcEngine;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供数据库基本操作和代理EntityDao操作，在DbEngineConfiguration中初始化
 * @author xkliu
 */
public class DbEngine extends JdbcEngine {
    private final Map<Class<?>, EntityDao<?>> daoMap = new ConcurrentHashMap<>();

    public DbEngine(JdbcTemplate jdbcTemplate, IdGenerator idGenerator) {
        super(jdbcTemplate, idGenerator);
    }

    public DbEngine(JdbcTemplate jdbcTemplate, IdGenerator idGenerator, RuleProperties ruleProperties) {
        this(jdbcTemplate, idGenerator);
        this.rule = ruleProperties;
    }

    public <T> EntityDao<T> findDao(Class<T> type) {
        EntityDao<T> handler = (EntityDao<T>)daoMap.get(type);

        if (handler == null) {
            synchronized (daoMap) {
                handler = new EntityDao<>(type, this);
                daoMap.put(type, handler);
            }
        }

        return handler;
    }

    /**
     * 根据PO对象的ID值更新其余所有字段
     * @param entity PO对象
     * @param <T> PO对象类型
     * @return 更新数量
     */
    public <T> int updateEntity(T entity) {
        return updateEntity(entity, null,null);
    }

    /**
     * 使用ID字段更新单行数据
     * @param entity PO值对象，对象属性是需要更新的值
     * @param fields 需要更新额字段列表，逗号分隔
     * @param <T> PO值对象类型
     * @return 更新数量
     */
    public <T> int updateEntity(T entity, String fields) {
        return updateEntity(entity, fields, null);
    }

    /**
     * 指定自定义条件更新对象
     * @param entity PO值对象，对象属性是需要更新的值和更新条件值
     * @param fields 需要更新额字段列表，逗号分隔
     * @param whereFields 更新条件字段列表，逗号分隔
     * @param <T> PO值对象类型
     * @return 更新数量
     */
    public <T> int updateEntity(T entity, String fields, String whereFields) {
        EntityDao<T> dao = findDao((Class<T>)entity.getClass());
        return dao.updateEntity(entity, fields, whereFields);
    }

    /**
     * 用PO对象所有字段入单行数据
     * @param entity PO对象
     * @param <T> PO对象类型
     * @return 更新数量
     */
    public <T> int insertEntity(T entity) {
        return insertEntity(entity, null);
    }
    /**
     * 插入单行数据
     * @param entity PO对象
     * @param fields 逗号分隔的字段列表
     * @param <T> PO对象类型
     * @return 更新数量
     */
    public <T> int insertEntity(T entity, String fields) {
        EntityDao<T> dao = findDao((Class<T>)entity.getClass());
        return dao.insertEntity(entity, fields);
    }

    /**
     * 根据ID值删除单行数据
     * @param entity PO对象
     * @param <T> PO对象类型
     * @return 删除数量
     */
    public <T> int deleteEntity(T entity) {
        EntityDao<T> dao = findDao((Class<T>)entity.getClass());
        return dao.deleteEntity(entity);
    }

    /**
     * 根据ID值删除单行数据
     * @param entityType PO对象类型
     * @param id 记录主建值
     * @param <T> PO对象
     * @return 删除数量
     */
    public <T> int deleteEntity(Class<T> entityType, Long id) {
        EntityDao<T> dao = findDao(entityType);
        return dao.deleteEntity(id);
    }

    /**
     * 根据ID值删除单行数据
     * @param entityType PO对象类型
     * @param whereSql Where条件SQL语句，以where开头
     * @param params 条件的值，可以多个
     * @param <T> PO对象
     * @return 删除数量
     */
    public <T> int deleteEntity(Class<T> entityType, String whereSql, Object...params) {
        EntityDao<T> dao = findDao(entityType);
        return dao.deleteEntity(whereSql, params);
    }

    /**
     * 读取实体对象ID值
     * @param entity
     * @param <T>
     * @return
     */
    public <T> Long readEntityId(T entity) {
        EntityDao<T> dao = findDao((Class<T>)entity.getClass());
        return dao.readId(entity);
    }

    /** 查询单行数据，返回bean */
    public <T> T queryEntity(Class<T> type, Long id) {
        return queryEntity(type, id, null);
    }

    public <T> T queryEntity(Class<T> type, Long id, String fields) {
        return findDao(type).queryEntity(id, fields);
    }

    /** 查询对象所有数据，返回列表 */
    public <T> List<T> query(Class<T> type) {
        return query(type, null);
    }

    /** 查询对象所有数据，返回列表 */
    public <T> List<T> query(Class<T> type, String fields) {
        return findDao(type).query(fields);
    }

    /**
     * 传入where条件查询实体类别
     * @param type 实体类型类
     * @param sqlWhere sql的where语句部分，不包含from
     * @param params 条件参数值
     * @param <T> 实体类型
     * @return
     */
    public <T> List<T> queryWhere(Class<T> type, String sqlWhere, Object...params) {
        return findDao(type).queryWhere(sqlWhere, params);
    }

    /**
     * 批量插入单行数据
     * @param entities PO对象列表
     * @param <T> 实体类型
     * @return 更新数量
     */
    public <T> int[] batchInsertEntity(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return findDao((Class<T>)entities.get(0).getClass()).batchInsertEntity(entities, null);
    }

    /**
     * 批量插入数据
     * @param entities PO对象列表
     * @param fields 逗号分隔的字段列表
     * @param <T> 实体类型
     * @return 更新数量
     */
    public <T> int[] batchInsertEntity(List<T> entities, String fields) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return findDao((Class<T>)entities.get(0).getClass()).batchInsertEntity(entities, fields);
    }

    /**
     * 批量更新数据
     * @param entities PO对象列表
     * @param <T> 实体类型
     * @return 更新数量
     */
    public <T> int[] batchUpdateEntity(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return findDao((Class<T>)entities.get(0).getClass()).batchUpdateEntity(entities, null);
    }

    /**
     * 批量更新数据
     * @param entities PO对象列表
     * @param fields 逗号分隔的字段列表
     * @param <T> 实体类型
     * @return 更新数量
     */
    public <T> int[] batchUpdateEntity(List<T> entities, String fields) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return findDao((Class<T>)entities.get(0).getClass()).batchUpdateEntity(entities, fields);
    }

}
