package com.dhl.pizer;

import com.dhl.pizer.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 灯状态
 * 1. 红灯：管制区内有agv （我来控制变更灯颜色）
 * 2. 黄灯：管制区内有人员 （人员触发物理按键）
 * 3. 绿灯：无人无AGV
 */

/**
 * 取货辅助点：AP1000、AP1001（相机检测点）
 * 取货点：AP1002
 * 放货辅助点：AP1003\AP1004\AP1005
 * 放货点：LOC-AP1006\LOC-AP1007\LOC-AP1008
 */

/**
 * 操作步骤
 * socket通讯：当有货物时，触发条件，开始发起下面运单任务流程
 * 1. 控制车，PP点 -> 取货前置点，抬升插齿高度为1m
 * 2. 判断条件： 绿灯
 *         1. 满足条件： 取货前置点 -> 检测插齿点
 *         2. 不满足条件： 等待
 * 3. 判断条件： 相机信号
 *  *         1. 满足条件： 检测插齿点 -> 取货库位
 *  *         2. 不满足条件： 等待
 * 4. 控制车，取货库位 -> 放货前置点
 * 5. 判断条件：放货库位空闲（三个库位有一个空闲即可）
 *         1. 满足条件： 控制车，放货前置点 -> 放货库位，降低插齿高度为0m
 *         2. 不满足条件：继续轮询放货库位是否有空闲
 */

/**
 * web端页面工作：
 * 1.
 *
 * 手持端工作：
 * 1. 扫描起始库位，选择放货库位（三选一，只可选择未被调用的放货库位）
 * 2. 异常上报；  何时弹窗报警？
 */

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.dhl.pizer.*")
public class PizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PizerApplication.class, args);
    }

    @Bean
    public SpringContextUtil getSpringBeanUtil(ApplicationContext applicationContext) {
        SpringContextUtil springBeanUtil = new SpringContextUtil();
        springBeanUtil.setApplicationContext(applicationContext);
        return springBeanUtil;
    }

}
