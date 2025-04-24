package com.example.tastysphere_api.service;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemHealthService {

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> map = new HashMap<>();

        // CPU 使用率
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        double cpuLoad = osBean.getSystemCpuLoad(); // 返回值是 0~1 之间
        map.put("cpu", (int) (cpuLoad * 100));

        // 内存使用率
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;

        double memoryUsage = (double) used / total;
        map.put("memory", (int) (memoryUsage * 100));

        return map;
    }
}
