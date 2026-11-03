package com.huansi.dataagent.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "请输入用户名")
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$", message = "用户名需为 3-30 位字母、数字或下划线")
        String username,

        @NotBlank(message = "请输入邮箱")
        @Email(message = "邮箱格式不正确")
        String email,

        @NotBlank(message = "请输入显示名称")
        @Size(max = 50, message = "显示名称不能超过 50 个字符")
        String displayName,

        @NotBlank(message = "请输入密码")
        @Size(min = 6, max = 64, message = "密码长度需为 6-64 位")
        String password) {
}
