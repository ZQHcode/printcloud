package com.print.printcloud.order.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Created by 郑钦泓
 * 2019-10-31 20:18
 */
@Entity
@Data
public class Paper {

    @Id
    private String pid;

    /** 纸的价格. */
    private BigDecimal pprice;
}
