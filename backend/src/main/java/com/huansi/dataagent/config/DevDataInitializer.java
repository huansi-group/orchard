package com.huansi.dataagent.config;

import com.huansi.dataagent.agent.AgentStatus;
import com.huansi.dataagent.agent.DataAgent;
import com.huansi.dataagent.agent.DataAgentRepository;
import com.huansi.dataagent.agent.DataSourceType;
import com.huansi.dataagent.user.Role;
import com.huansi.dataagent.user.User;
import com.huansi.dataagent.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * 内存数据库启动数据，方便首次运行即可登录体验。
 */
@Configuration
public class DevDataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               DataAgentRepository agentRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User(
                        "admin",
                        "admin@dataagent.local",
                        "系统管理员",
                        passwordEncoder.encode("admin123"),
                        Role.ADMIN));
                userRepository.save(new User(
                        "analyst",
                        "analyst@dataagent.local",
                        "数据分析师",
                        passwordEncoder.encode("analyst123"),
                        Role.USER));
            }
            if (agentRepository.count() == 0) {
                agentRepository.saveAll(List.of(
                        new DataAgent("销售数据分析助手",
                                "基于订单与回款数据，自动生成销售趋势与区域排名分析。",
                                DataSourceType.SQL, AgentStatus.RUNNING, "admin"),
                        new DataAgent("客户画像分析",
                                "聚合客户行为与标签数据，输出高价值客户分群洞察。",
                                DataSourceType.API, AgentStatus.RUNNING, "admin"),
                        new DataAgent("运营指标监控",
                                "定时巡检核心运营指标，异常波动自动生成预警摘要。",
                                DataSourceType.SQL, AgentStatus.RUNNING, "admin"),
                        new DataAgent("供应链预测助手",
                                "读取历史库存与采购数据，预测未来四周的补货需求。",
                                DataSourceType.FILE, AgentStatus.PAUSED, "admin"),
                        new DataAgent("财务报表核对",
                                "比对多来源财务明细，定位差异项并生成核对说明。",
                                DataSourceType.FILE, AgentStatus.DRAFT, "admin"),
                        new DataAgent("市场活动归因",
                                "接入投放平台 API，分析活动转化路径与渠道贡献。",
                                DataSourceType.API, AgentStatus.DRAFT, "admin")));
            }
        };
    }
}
