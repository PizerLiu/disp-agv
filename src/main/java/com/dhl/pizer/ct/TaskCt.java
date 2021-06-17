package com.dhl.pizer.ct;

import com.dhl.pizer.conf.Status;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.flowcontrol.flowchain.ControlArgs;
import com.dhl.pizer.flowcontrol.flowchain.DefaultFlowChainBuilder;
import com.dhl.pizer.flowcontrol.flowchain.ProcessorFlowChain;
import com.dhl.pizer.socket.NettyClient;
import com.dhl.pizer.vo.BugException;
import com.dhl.pizer.vo.ResponceBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TaskCt {

    @Autowired
    private DefaultFlowChainBuilder defaultFlowChainBuilder;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NettyClient nettyClient;

    @Scheduled(cron = "*/5 * * * * ?")
    public void scaleTask() {

        // 扫描进行中的任务
        Task taskExample = Task.builder().status(Status.RUNNING.getCode()).build();
        Example<Task> example = Example.of(taskExample);
        List<Task> tasks = taskRepository.findAll(example);

        for (Task task : tasks) {

            String taskId = task.getTaskId();
            // 执行后续流任务
            try {
                ProcessorFlowChain chain = defaultFlowChainBuilder.build(taskId);
                boolean chainRes = chain.run(ControlArgs.builder().taskId(taskId).build());

                // 若chain执行完成，则task任务状态改为执行结束
                if (chainRes) {
                    task.setStatus(Status.FINISHED.getCode());
                    taskRepository.save(task);
                }
            } catch (BugException e) {
                log.error("taskId: " + taskId + "; e: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void scaleDi() {
        nettyClient.sendMsg("5A0100010000000003F5000000000000");
    }

    @Scheduled(cron = "*/3 * * * * ?")
    public void scaleBattery() {
        nettyClient.sendMsg("5A010001000000000404000000000000");
    }

}
