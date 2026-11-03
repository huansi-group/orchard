package com.huansi.dataagent.user;

/**
 * 系统角色。角色会以 ROLE_ 前缀写入 JWT 与 Spring Security 权限。
 */
public enum Role {
    ADMIN,
    USER
}
