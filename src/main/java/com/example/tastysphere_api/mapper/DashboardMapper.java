package com.example.tastysphere_api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    // 今日订单数（返回默认 0）
    @Select("""
        SELECT COUNT(*) FROM orders
        WHERE merchant_id = #{merchantId}
          AND DATE(created_time) = CURDATE()
    """)
    int countTodayOrders(@Param("merchantId") Long merchantId);

    // 总收入（返回默认 0）
    @Select("""
        SELECT IFNULL(SUM(total_amount), 0) FROM orders
        WHERE merchant_id = #{merchantId}
          AND status = 'COMPLETED'
    """)
    Double sumTotalRevenue(@Param("merchantId") Long merchantId);

    // 活跃客户数（近 30 天下过订单的唯一用户数）
    @Select("""
        SELECT COUNT(DISTINCT user_id) FROM orders
        WHERE merchant_id = #{merchantId}
          AND created_time >= NOW() - INTERVAL 30 DAY
    """)
    int countActiveCustomers(@Param("merchantId") Long merchantId);

    // 待处理订单数
    @Select("""
        SELECT COUNT(*) FROM orders
        WHERE merchant_id = #{merchantId}
          AND status = 'PENDING'
    """)
    int countPendingOrders(@Param("merchantId") Long merchantId);

    /**
     * 获取近 7 天订单数（含日期和数量），返回 Map：{ date: String, count: int }
     * 你可以在 Java 代码中补齐 0 数据，保证图表展示 7 天完整数据
     */
    @Select("""
        SELECT
            DATE(created_time) AS date,
            COUNT(*) AS count
        FROM orders
        WHERE merchant_id = #{merchantId}
          AND created_time >= CURDATE() - INTERVAL 6 DAY
        GROUP BY DATE(created_time)
        ORDER BY DATE(created_time)
    """)
    List<Map<String, Object>> getWeeklyOrders(@Param("merchantId") Long merchantId);
}
