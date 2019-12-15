package com.leyou.item.pojo;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author Jiang-gege
 * 2019/11/422:45
 */
@Table(name = "tb_spec_group")
@Data

public class SpecGroup {
    @Id
    @KeySql(useGeneratedKeys =true)
    private Long id;

    private long cid;

    private String name;

    @Transient
    private List<SpecParam> prams;
}