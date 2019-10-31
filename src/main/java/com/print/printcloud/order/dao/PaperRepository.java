package com.print.printcloud.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.print.printcloud.order.dataobject.Paper;

/**
 * Created by 郑钦泓
 * 2019-10-31 20:22
 */
public interface PaperRepository extends JpaRepository<Paper, String> {
}
