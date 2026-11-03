package com.huansi.dataagent.harness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HarnessChatRequest(
        @NotBlank(message = "请输入消息内容")
        @Size(max = 4000, message = "消息不能超过 4000 个字符")
        String message,

        @Pattern(
                regexp = "^$|^[A-Za-z0-9_-]{1,64}$",
                message = "sessionId 仅支持 1-64 位字母、数字、下划线或短横线")
        String sessionId) {
}
