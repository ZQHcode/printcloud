package com.print.printcloud.order.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class PrintParameter {

    @Id
    private String fileId;

    private String sided;

    /**文件打印的份数*/
    private int number;

    private String handle;

    /**文件的纸张数量*/
    private int pages;

    /**文件名字*/
    private String fileName;



}
